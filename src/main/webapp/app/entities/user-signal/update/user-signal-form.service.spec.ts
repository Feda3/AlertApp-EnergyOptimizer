import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../user-signal.test-samples';

import { UserSignalFormService } from './user-signal-form.service';

describe('UserSignal Form Service', () => {
  let service: UserSignalFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserSignalFormService);
  });

  describe('Service methods', () => {
    describe('createUserSignalFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserSignalFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            signalDate: expect.any(Object),
            action: expect.any(Object),
            summaryMessage: expect.any(Object),
            user: expect.any(Object),
            setting: expect.any(Object),
          }),
        );
      });

      it('passing IUserSignal should create a new form with FormGroup', () => {
        const formGroup = service.createUserSignalFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            signalDate: expect.any(Object),
            action: expect.any(Object),
            summaryMessage: expect.any(Object),
            user: expect.any(Object),
            setting: expect.any(Object),
          }),
        );
      });
    });

    describe('getUserSignal', () => {
      it('should return NewUserSignal for default UserSignal initial value', () => {
        const formGroup = service.createUserSignalFormGroup(sampleWithNewData);

        const userSignal = service.getUserSignal(formGroup) as any;

        expect(userSignal).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserSignal for empty UserSignal initial value', () => {
        const formGroup = service.createUserSignalFormGroup();

        const userSignal = service.getUserSignal(formGroup) as any;

        expect(userSignal).toMatchObject({});
      });

      it('should return IUserSignal', () => {
        const formGroup = service.createUserSignalFormGroup(sampleWithRequiredData);

        const userSignal = service.getUserSignal(formGroup) as any;

        expect(userSignal).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserSignal should not enable id FormControl', () => {
        const formGroup = service.createUserSignalFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserSignal should disable id FormControl', () => {
        const formGroup = service.createUserSignalFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
