import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserAlertSettingsDetailComponent } from './user-alert-settings-detail.component';

describe('UserAlertSettings Management Detail Component', () => {
  let comp: UserAlertSettingsDetailComponent;
  let fixture: ComponentFixture<UserAlertSettingsDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserAlertSettingsDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./user-alert-settings-detail.component').then(m => m.UserAlertSettingsDetailComponent),
              resolve: { userAlertSettings: () => of({ id: 30435 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(UserAlertSettingsDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAlertSettingsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load userAlertSettings on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', UserAlertSettingsDetailComponent);

      // THEN
      expect(instance.userAlertSettings()).toEqual(expect.objectContaining({ id: 30435 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
