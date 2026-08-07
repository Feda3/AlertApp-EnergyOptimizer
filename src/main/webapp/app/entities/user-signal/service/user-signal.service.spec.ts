import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IUserSignal } from '../user-signal.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../user-signal.test-samples';

import { RestUserSignal, UserSignalService } from './user-signal.service';

const requireRestSample: RestUserSignal = {
  ...sampleWithRequiredData,
  signalDate: sampleWithRequiredData.signalDate?.format(DATE_FORMAT),
};

describe('UserSignal Service', () => {
  let service: UserSignalService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserSignal | IUserSignal[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(UserSignalService);
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

    it('should create a UserSignal', () => {
      const userSignal = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userSignal).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserSignal', () => {
      const userSignal = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userSignal).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserSignal', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserSignal', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserSignal', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addUserSignalToCollectionIfMissing', () => {
      it('should add a UserSignal to an empty array', () => {
        const userSignal: IUserSignal = sampleWithRequiredData;
        expectedResult = service.addUserSignalToCollectionIfMissing([], userSignal);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userSignal);
      });

      it('should not add a UserSignal to an array that contains it', () => {
        const userSignal: IUserSignal = sampleWithRequiredData;
        const userSignalCollection: IUserSignal[] = [
          {
            ...userSignal,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserSignalToCollectionIfMissing(userSignalCollection, userSignal);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserSignal to an array that doesn't contain it", () => {
        const userSignal: IUserSignal = sampleWithRequiredData;
        const userSignalCollection: IUserSignal[] = [sampleWithPartialData];
        expectedResult = service.addUserSignalToCollectionIfMissing(userSignalCollection, userSignal);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userSignal);
      });

      it('should add only unique UserSignal to an array', () => {
        const userSignalArray: IUserSignal[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userSignalCollection: IUserSignal[] = [sampleWithRequiredData];
        expectedResult = service.addUserSignalToCollectionIfMissing(userSignalCollection, ...userSignalArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userSignal: IUserSignal = sampleWithRequiredData;
        const userSignal2: IUserSignal = sampleWithPartialData;
        expectedResult = service.addUserSignalToCollectionIfMissing([], userSignal, userSignal2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userSignal);
        expect(expectedResult).toContain(userSignal2);
      });

      it('should accept null and undefined values', () => {
        const userSignal: IUserSignal = sampleWithRequiredData;
        expectedResult = service.addUserSignalToCollectionIfMissing([], null, userSignal, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userSignal);
      });

      it('should return initial array if no UserSignal is added', () => {
        const userSignalCollection: IUserSignal[] = [sampleWithRequiredData];
        expectedResult = service.addUserSignalToCollectionIfMissing(userSignalCollection, undefined, null);
        expect(expectedResult).toEqual(userSignalCollection);
      });
    });

    describe('compareUserSignal', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserSignal(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 1082 };
        const entity2 = null;

        const compareResult1 = service.compareUserSignal(entity1, entity2);
        const compareResult2 = service.compareUserSignal(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 1082 };
        const entity2 = { id: 13014 };

        const compareResult1 = service.compareUserSignal(entity1, entity2);
        const compareResult2 = service.compareUserSignal(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 1082 };
        const entity2 = { id: 1082 };

        const compareResult1 = service.compareUserSignal(entity1, entity2);
        const compareResult2 = service.compareUserSignal(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
