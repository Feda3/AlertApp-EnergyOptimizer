import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../user-alert-settings.test-samples';

import { UserAlertSettingsFormService } from './user-alert-settings-form.service';

describe('UserAlertSettings Form Service', () => {
  let service: UserAlertSettingsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserAlertSettingsFormService);
  });

  describe('Service methods', () => {
    describe('createUserAlertSettingsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserAlertSettingsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            symbol: expect.any(Object),
            threshold: expect.any(Object),
            triggerIfGreater: expect.any(Object),
            action: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
            minDurationMinutes: expect.any(Object),
            isActive: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IUserAlertSettings should create a new form with FormGroup', () => {
        const formGroup = service.createUserAlertSettingsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            symbol: expect.any(Object),
            threshold: expect.any(Object),
            triggerIfGreater: expect.any(Object),
            action: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
            minDurationMinutes: expect.any(Object),
            isActive: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getUserAlertSettings', () => {
      it('should return NewUserAlertSettings for default UserAlertSettings initial value', () => {
        const formGroup = service.createUserAlertSettingsFormGroup(sampleWithNewData);

        const userAlertSettings = service.getUserAlertSettings(formGroup) as any;

        expect(userAlertSettings).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserAlertSettings for empty UserAlertSettings initial value', () => {
        const formGroup = service.createUserAlertSettingsFormGroup();

        const userAlertSettings = service.getUserAlertSettings(formGroup) as any;

        expect(userAlertSettings).toMatchObject({});
      });

      it('should return IUserAlertSettings', () => {
        const formGroup = service.createUserAlertSettingsFormGroup(sampleWithRequiredData);

        const userAlertSettings = service.getUserAlertSettings(formGroup) as any;

        expect(userAlertSettings).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserAlertSettings should not enable id FormControl', () => {
        const formGroup = service.createUserAlertSettingsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserAlertSettings should disable id FormControl', () => {
        const formGroup = service.createUserAlertSettingsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
