import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserSignal, NewUserSignal } from '../user-signal.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserSignal for edit and NewUserSignalFormGroupInput for create.
 */
type UserSignalFormGroupInput = IUserSignal | PartialWithRequiredKeyOf<NewUserSignal>;

type UserSignalFormDefaults = Pick<NewUserSignal, 'id'>;

type UserSignalFormGroupContent = {
  id: FormControl<IUserSignal['id'] | NewUserSignal['id']>;
  signalDate: FormControl<IUserSignal['signalDate']>;
  action: FormControl<IUserSignal['action']>;
  summaryMessage: FormControl<IUserSignal['summaryMessage']>;
  user: FormControl<IUserSignal['user']>;
  setting: FormControl<IUserSignal['setting']>;
};

export type UserSignalFormGroup = FormGroup<UserSignalFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserSignalFormService {
  createUserSignalFormGroup(userSignal: UserSignalFormGroupInput = { id: null }): UserSignalFormGroup {
    const userSignalRawValue = {
      ...this.getFormDefaults(),
      ...userSignal,
    };
    return new FormGroup<UserSignalFormGroupContent>({
      id: new FormControl(
        { value: userSignalRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      signalDate: new FormControl(userSignalRawValue.signalDate, {
        validators: [Validators.required],
      }),
      action: new FormControl(userSignalRawValue.action, {
        validators: [Validators.required],
      }),
      summaryMessage: new FormControl(userSignalRawValue.summaryMessage),
      user: new FormControl(userSignalRawValue.user),
      setting: new FormControl(userSignalRawValue.setting),
    });
  }

  getUserSignal(form: UserSignalFormGroup): IUserSignal | NewUserSignal {
    return form.getRawValue() as IUserSignal | NewUserSignal;
  }

  resetForm(form: UserSignalFormGroup, userSignal: UserSignalFormGroupInput): void {
    const userSignalRawValue = { ...this.getFormDefaults(), ...userSignal };
    form.reset(
      {
        ...userSignalRawValue,
        id: { value: userSignalRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserSignalFormDefaults {
    return {
      id: null,
    };
  }
}
