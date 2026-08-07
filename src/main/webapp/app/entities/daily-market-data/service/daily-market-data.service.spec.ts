import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDailyMarketData } from '../daily-market-data.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../daily-market-data.test-samples';

import { DailyMarketDataService, RestDailyMarketData } from './daily-market-data.service';

const requireRestSample: RestDailyMarketData = {
  ...sampleWithRequiredData,
  fetchDate: sampleWithRequiredData.fetchDate?.format(DATE_FORMAT),
};

describe('DailyMarketData Service', () => {
  let service: DailyMarketDataService;
  let httpMock: HttpTestingController;
  let expectedResult: IDailyMarketData | IDailyMarketData[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DailyMarketDataService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DailyMarketData', () => {
      const dailyMarketData = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dailyMarketData).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DailyMarketData', () => {
      const dailyMarketData = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dailyMarketData).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DailyMarketData', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DailyMarketData', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DailyMarketData', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDailyMarketDataToCollectionIfMissing', () => {
      it('should add a DailyMarketData to an empty array', () => {
        const dailyMarketData: IDailyMarketData = sampleWithRequiredData;
        expectedResult = service.addDailyMarketDataToCollectionIfMissing([], dailyMarketData);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dailyMarketData);
      });

      it('should not add a DailyMarketData to an array that contains it', () => {
        const dailyMarketData: IDailyMarketData = sampleWithRequiredData;
        const dailyMarketDataCollection: IDailyMarketData[] = [
          {
            ...dailyMarketData,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDailyMarketDataToCollectionIfMissing(dailyMarketDataCollection, dailyMarketData);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DailyMarketData to an array that doesn't contain it", () => {
        const dailyMarketData: IDailyMarketData = sampleWithRequiredData;
        const dailyMarketDataCollection: IDailyMarketData[] = [sampleWithPartialData];
        expectedResult = service.addDailyMarketDataToCollectionIfMissing(dailyMarketDataCollection, dailyMarketData);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dailyMarketData);
      });

      it('should add only unique DailyMarketData to an array', () => {
        const dailyMarketDataArray: IDailyMarketData[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dailyMarketDataCollection: IDailyMarketData[] = [sampleWithRequiredData];
        expectedResult = service.addDailyMarketDataToCollectionIfMissing(dailyMarketDataCollection, ...dailyMarketDataArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dailyMarketData: IDailyMarketData = sampleWithRequiredData;
        const dailyMarketData2: IDailyMarketData = sampleWithPartialData;
        expectedResult = service.addDailyMarketDataToCollectionIfMissing([], dailyMarketData, dailyMarketData2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dailyMarketData);
        expect(expectedResult).toContain(dailyMarketData2);
      });

      it('should accept null and undefined values', () => {
        const dailyMarketData: IDailyMarketData = sampleWithRequiredData;
        expectedResult = service.addDailyMarketDataToCollectionIfMissing([], null, dailyMarketData, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dailyMarketData);
      });

      it('should return initial array if no DailyMarketData is added', () => {
        const dailyMarketDataCollection: IDailyMarketData[] = [sampleWithRequiredData];
        expectedResult = service.addDailyMarketDataToCollectionIfMissing(dailyMarketDataCollection, undefined, null);
        expect(expectedResult).toEqual(dailyMarketDataCollection);
      });
    });

    describe('compareDailyMarketData', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDailyMarketData(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16517 };
        const entity2 = null;

        const compareResult1 = service.compareDailyMarketData(entity1, entity2);
        const compareResult2 = service.compareDailyMarketData(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16517 };
        const entity2 = { id: 15304 };

        const compareResult1 = service.compareDailyMarketData(entity1, entity2);
        const compareResult2 = service.compareDailyMarketData(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16517 };
        const entity2 = { id: 16517 };

        const compareResult1 = service.compareDailyMarketData(entity1, entity2);
        const compareResult2 = service.compareDailyMarketData(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
