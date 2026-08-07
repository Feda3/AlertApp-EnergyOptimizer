import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../daily-market-data.test-samples';

import { DailyMarketDataFormService } from './daily-market-data-form.service';

describe('DailyMarketData Form Service', () => {
  let service: DailyMarketDataFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyMarketDataFormService);
  });

  describe('Service methods', () => {
    describe('createDailyMarketDataFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDailyMarketDataFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fetchDate: expect.any(Object),
            symbol: expect.any(Object),
            metricValue: expect.any(Object),
          }),
        );
      });

      it('passing IDailyMarketData should create a new form with FormGroup', () => {
        const formGroup = service.createDailyMarketDataFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fetchDate: expect.any(Object),
            symbol: expect.any(Object),
            metricValue: expect.any(Object),
          }),
        );
      });
    });

    describe('getDailyMarketData', () => {
      it('should return NewDailyMarketData for default DailyMarketData initial value', () => {
        const formGroup = service.createDailyMarketDataFormGroup(sampleWithNewData);

        const dailyMarketData = service.getDailyMarketData(formGroup) as any;

        expect(dailyMarketData).toMatchObject(sampleWithNewData);
      });

      it('should return NewDailyMarketData for empty DailyMarketData initial value', () => {
        const formGroup = service.createDailyMarketDataFormGroup();

        const dailyMarketData = service.getDailyMarketData(formGroup) as any;

        expect(dailyMarketData).toMatchObject({});
      });

      it('should return IDailyMarketData', () => {
        const formGroup = service.createDailyMarketDataFormGroup(sampleWithRequiredData);

        const dailyMarketData = service.getDailyMarketData(formGroup) as any;

        expect(dailyMarketData).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDailyMarketData should not enable id FormControl', () => {
        const formGroup = service.createDailyMarketDataFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDailyMarketData should disable id FormControl', () => {
        const formGroup = service.createDailyMarketDataFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
