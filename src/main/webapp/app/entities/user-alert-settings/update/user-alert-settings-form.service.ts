import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserAlertSettings, NewUserAlertSettings } from '../user-alert-settings.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserAlertSettings for edit and NewUserAlertSettingsFormGroupInput for create.
 */
type UserAlertSettingsFormGroupInput = IUserAlertSettings | PartialWithRequiredKeyOf<NewUserAlertSettings>;

type UserAlertSettingsFormDefaults = Pick<NewUserAlertSettings, 'id' | 'triggerIfGreater' | 'isActive'>;

type UserAlertSettingsFormGroupContent = {
  id: FormControl<IUserAlertSettings['id'] | NewUserAlertSettings['id']>;
  symbol: FormControl<IUserAlertSettings['symbol']>;
  threshold: FormControl<IUserAlertSettings['threshold']>;
  triggerIfGreater: FormControl<IUserAlertSettings['triggerIfGreater']>;
  action: FormControl<IUserAlertSettings['action']>;
  startTime: FormControl<IUserAlertSettings['startTime']>;
  endTime: FormControl<IUserAlertSettings['endTime']>;
  minDurationMinutes: FormControl<IUserAlertSettings['minDurationMinutes']>;
  isActive: FormControl<IUserAlertSettings['isActive']>;
  user: FormControl<IUserAlertSettings['user']>;
};

export type UserAlertSettingsFormGroup = FormGroup<UserAlertSettingsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserAlertSettingsFormService {
  createUserAlertSettingsFormGroup(userAlertSettings: UserAlertSettingsFormGroupInput = { id: null }): UserAlertSettingsFormGroup {
    const userAlertSettingsRawValue = {
      ...this.getFormDefaults(),
      ...userAlertSettings,
    };
    return new FormGroup<UserAlertSettingsFormGroupContent>({
      id: new FormControl(
        { value: userAlertSettingsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      symbol: new FormControl(userAlertSettingsRawValue.symbol, {
        validators: [Validators.required],
      }),
      threshold: new FormControl(userAlertSettingsRawValue.threshold, {
        validators: [Validators.required],
      }),
      triggerIfGreater: new FormControl(userAlertSettingsRawValue.triggerIfGreater, {
        validators: [Validators.required],
      }),
      action: new FormControl(userAlertSettingsRawValue.action, {
        validators: [Validators.required],
      }),
      startTime: new FormControl(userAlertSettingsRawValue.startTime),
      endTime: new FormControl(userAlertSettingsRawValue.endTime),
      minDurationMinutes: new FormControl(userAlertSettingsRawValue.minDurationMinutes),
      isActive: new FormControl(userAlertSettingsRawValue.isActive),
      user: new FormControl(userAlertSettingsRawValue.user),
    });
  }

  getUserAlertSettings(form: UserAlertSettingsFormGroup): IUserAlertSettings | NewUserAlertSettings {
    return form.getRawValue() as IUserAlertSettings | NewUserAlertSettings;
  }

  resetForm(form: UserAlertSettingsFormGroup, userAlertSettings: UserAlertSettingsFormGroupInput): void {
    const userAlertSettingsRawValue = { ...this.getFormDefaults(), ...userAlertSettings };
    form.reset(
      {
        ...userAlertSettingsRawValue,
        id: { value: userAlertSettingsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserAlertSettingsFormDefaults {
    return {
      id: null,
      triggerIfGreater: false,
      isActive: false,
    };
  }
}
