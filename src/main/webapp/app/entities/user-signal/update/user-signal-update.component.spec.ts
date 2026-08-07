import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUserAlertSettings } from 'app/entities/user-alert-settings/user-alert-settings.model';
import { UserAlertSettingsService } from 'app/entities/user-alert-settings/service/user-alert-settings.service';
import { IUserSignal } from '../user-signal.model';
import { UserSignalService } from '../service/user-signal.service';
import { UserSignalFormService } from './user-signal-form.service';

import { UserSignalUpdateComponent } from './user-signal-update.component';

describe('UserSignal Management Update Component', () => {
  let comp: UserSignalUpdateComponent;
  let fixture: ComponentFixture<UserSignalUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userSignalFormService: UserSignalFormService;
  let userSignalService: UserSignalService;
  let userService: UserService;
  let userAlertSettingsService: UserAlertSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserSignalUpdateComponent],
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
      .overrideTemplate(UserSignalUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserSignalUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userSignalFormService = TestBed.inject(UserSignalFormService);
    userSignalService = TestBed.inject(UserSignalService);
    userService = TestBed.inject(UserService);
    userAlertSettingsService = TestBed.inject(UserAlertSettingsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const userSignal: IUserSignal = { id: 13014 };
      const user: IUser = { id: 3944 };
      userSignal.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userSignal });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call UserAlertSettings query and add missing value', () => {
      const userSignal: IUserSignal = { id: 13014 };
      const setting: IUserAlertSettings = { id: 30435 };
      userSignal.setting = setting;

      const userAlertSettingsCollection: IUserAlertSettings[] = [{ id: 30435 }];
      jest.spyOn(userAlertSettingsService, 'query').mockReturnValue(of(new HttpResponse({ body: userAlertSettingsCollection })));
      const additionalUserAlertSettings = [setting];
      const expectedCollection: IUserAlertSettings[] = [...additionalUserAlertSettings, ...userAlertSettingsCollection];
      jest.spyOn(userAlertSettingsService, 'addUserAlertSettingsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userSignal });
      comp.ngOnInit();

      expect(userAlertSettingsService.query).toHaveBeenCalled();
      expect(userAlertSettingsService.addUserAlertSettingsToCollectionIfMissing).toHaveBeenCalledWith(
        userAlertSettingsCollection,
        ...additionalUserAlertSettings.map(expect.objectContaining),
      );
      expect(comp.userAlertSettingsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const userSignal: IUserSignal = { id: 13014 };
      const user: IUser = { id: 3944 };
      userSignal.user = user;
      const setting: IUserAlertSettings = { id: 30435 };
      userSignal.setting = setting;

      activatedRoute.data = of({ userSignal });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.userAlertSettingsSharedCollection).toContainEqual(setting);
      expect(comp.userSignal).toEqual(userSignal);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserSignal>>();
      const userSignal = { id: 1082 };
      jest.spyOn(userSignalFormService, 'getUserSignal').mockReturnValue(userSignal);
      jest.spyOn(userSignalService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userSignal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userSignal }));
      saveSubject.complete();

      // THEN
      expect(userSignalFormService.getUserSignal).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userSignalService.update).toHaveBeenCalledWith(expect.objectContaining(userSignal));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserSignal>>();
      const userSignal = { id: 1082 };
      jest.spyOn(userSignalFormService, 'getUserSignal').mockReturnValue({ id: null });
      jest.spyOn(userSignalService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userSignal: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userSignal }));
      saveSubject.complete();

      // THEN
      expect(userSignalFormService.getUserSignal).toHaveBeenCalled();
      expect(userSignalService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserSignal>>();
      const userSignal = { id: 1082 };
      jest.spyOn(userSignalService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userSignal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userSignalService.update).toHaveBeenCalled();
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

    describe('compareUserAlertSettings', () => {
      it('should forward to userAlertSettingsService', () => {
        const entity = { id: 30435 };
        const entity2 = { id: 28398 };
        jest.spyOn(userAlertSettingsService, 'compareUserAlertSettings');
        comp.compareUserAlertSettings(entity, entity2);
        expect(userAlertSettingsService.compareUserAlertSettings).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
