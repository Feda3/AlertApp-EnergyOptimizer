import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { UserAlertSettingsService } from '../service/user-alert-settings.service';
import { IUserAlertSettings } from '../user-alert-settings.model';
import { UserAlertSettingsFormService } from './user-alert-settings-form.service';

import { UserAlertSettingsUpdateComponent } from './user-alert-settings-update.component';

describe('UserAlertSettings Management Update Component', () => {
  let comp: UserAlertSettingsUpdateComponent;
  let fixture: ComponentFixture<UserAlertSettingsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userAlertSettingsFormService: UserAlertSettingsFormService;
  let userAlertSettingsService: UserAlertSettingsService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserAlertSettingsUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(UserAlertSettingsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserAlertSettingsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userAlertSettingsFormService = TestBed.inject(UserAlertSettingsFormService);
    userAlertSettingsService = TestBed.inject(UserAlertSettingsService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const userAlertSettings: IUserAlertSettings = { id: 28398 };
      const user: IUser = { id: 3944 };
      userAlertSettings.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userAlertSettings });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const userAlertSettings: IUserAlertSettings = { id: 28398 };
      const user: IUser = { id: 3944 };
      userAlertSettings.user = user;

      activatedRoute.data = of({ userAlertSettings });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.userAlertSettings).toEqual(userAlertSettings);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAlertSettings>>();
      const userAlertSettings = { id: 30435 };
      jest.spyOn(userAlertSettingsFormService, 'getUserAlertSettings').mockReturnValue(userAlertSettings);
      jest.spyOn(userAlertSettingsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAlertSettings });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userAlertSettings }));
      saveSubject.complete();

      // THEN
      expect(userAlertSettingsFormService.getUserAlertSettings).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userAlertSettingsService.update).toHaveBeenCalledWith(expect.objectContaining(userAlertSettings));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAlertSettings>>();
      const userAlertSettings = { id: 30435 };
      jest.spyOn(userAlertSettingsFormService, 'getUserAlertSettings').mockReturnValue({ id: null });
      jest.spyOn(userAlertSettingsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAlertSettings: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userAlertSettings }));
      saveSubject.complete();

      // THEN
      expect(userAlertSettingsFormService.getUserAlertSettings).toHaveBeenCalled();
      expect(userAlertSettingsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAlertSettings>>();
      const userAlertSettings = { id: 30435 };
      jest.spyOn(userAlertSettingsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAlertSettings });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userAlertSettingsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
