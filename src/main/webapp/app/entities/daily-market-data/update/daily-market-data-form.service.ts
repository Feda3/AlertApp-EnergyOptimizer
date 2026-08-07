import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDailyMarketData, NewDailyMarketData } from '../daily-market-data.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDailyMarketData for edit and NewDailyMarketDataFormGroupInput for create.
 */
type DailyMarketDataFormGroupInput = IDailyMarketData | PartialWithRequiredKeyOf<NewDailyMarketData>;

type DailyMarketDataFormDefaults = Pick<NewDailyMarketData, 'id'>;

type DailyMarketDataFormGroupContent = {
  id: FormControl<IDailyMarketData['id'] | NewDailyMarketData['id']>;
  fetchDate: FormControl<IDailyMarketData['fetchDate']>;
  symbol: FormControl<IDailyMarketData['symbol']>;
  metricValue: FormControl<IDailyMarketData['metricValue']>;
};

export type DailyMarketDataFormGroup = FormGroup<DailyMarketDataFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DailyMarketDataFormService {
  createDailyMarketDataFormGroup(dailyMarketData: DailyMarketDataFormGroupInput = { id: null }): DailyMarketDataFormGroup {
    const dailyMarketDataRawValue = {
      ...this.getFormDefaults(),
      ...dailyMarketData,
    };
    return new FormGroup<DailyMarketDataFormGroupContent>({
      id: new FormControl(
        { value: dailyMarketDataRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fetchDate: new FormControl(dailyMarketDataRawValue.fetchDate, {
        validators: [Validators.required],
      }),
      symbol: new FormControl(dailyMarketDataRawValue.symbol, {
        validators: [Validators.required],
      }),
      metricValue: new FormControl(dailyMarketDataRawValue.metricValue, {
        validators: [Validators.required],
      }),
    });
  }

  getDailyMarketData(form: DailyMarketDataFormGroup): IDailyMarketData | NewDailyMarketData {
    return form.getRawValue() as IDailyMarketData | NewDailyMarketData;
  }

  resetForm(form: DailyMarketDataFormGroup, dailyMarketData: DailyMarketDataFormGroupInput): void {
    const dailyMarketDataRawValue = { ...this.getFormDefaults(), ...dailyMarketData };
    form.reset(
      {
        ...dailyMarketDataRawValue,
        id: { value: dailyMarketDataRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DailyMarketDataFormDefaults {
    return {
      id: null,
    };
  }
}
