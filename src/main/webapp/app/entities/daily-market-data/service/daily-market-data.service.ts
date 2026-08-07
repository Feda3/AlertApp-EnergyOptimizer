import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDailyMarketData, NewDailyMarketData } from '../daily-market-data.model';

export type PartialUpdateDailyMarketData = Partial<IDailyMarketData> & Pick<IDailyMarketData, 'id'>;

type RestOf<T extends IDailyMarketData | NewDailyMarketData> = Omit<T, 'fetchDate'> & {
  fetchDate?: string | null;
};

export type RestDailyMarketData = RestOf<IDailyMarketData>;

export type NewRestDailyMarketData = RestOf<NewDailyMarketData>;

export type PartialUpdateRestDailyMarketData = RestOf<PartialUpdateDailyMarketData>;

export type EntityResponseType = HttpResponse<IDailyMarketData>;
export type EntityArrayResponseType = HttpResponse<IDailyMarketData[]>;

@Injectable({ providedIn: 'root' })
export class DailyMarketDataService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/daily-market-data');

  create(dailyMarketData: NewDailyMarketData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyMarketData);
    return this.http
      .post<RestDailyMarketData>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dailyMarketData: IDailyMarketData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyMarketData);
    return this.http
      .put<RestDailyMarketData>(`${this.resourceUrl}/${this.getDailyMarketDataIdentifier(dailyMarketData)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dailyMarketData: PartialUpdateDailyMarketData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyMarketData);
    return this.http
      .patch<RestDailyMarketData>(`${this.resourceUrl}/${this.getDailyMarketDataIdentifier(dailyMarketData)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDailyMarketData>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDailyMarketData[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDailyMarketDataIdentifier(dailyMarketData: Pick<IDailyMarketData, 'id'>): number {
    return dailyMarketData.id;
  }

  compareDailyMarketData(o1: Pick<IDailyMarketData, 'id'> | null, o2: Pick<IDailyMarketData, 'id'> | null): boolean {
    return o1 && o2 ? this.getDailyMarketDataIdentifier(o1) === this.getDailyMarketDataIdentifier(o2) : o1 === o2;
  }

  addDailyMarketDataToCollectionIfMissing<Type extends Pick<IDailyMarketData, 'id'>>(
    dailyMarketDataCollection: Type[],
    ...dailyMarketDataToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dailyMarketData: Type[] = dailyMarketDataToCheck.filter(isPresent);
    if (dailyMarketData.length > 0) {
      const dailyMarketDataCollectionIdentifiers = dailyMarketDataCollection.map(dailyMarketDataItem =>
        this.getDailyMarketDataIdentifier(dailyMarketDataItem),
      );
      const dailyMarketDataToAdd = dailyMarketData.filter(dailyMarketDataItem => {
        const dailyMarketDataIdentifier = this.getDailyMarketDataIdentifier(dailyMarketDataItem);
        if (dailyMarketDataCollectionIdentifiers.includes(dailyMarketDataIdentifier)) {
          return false;
        }
        dailyMarketDataCollectionIdentifiers.push(dailyMarketDataIdentifier);
        return true;
      });
      return [...dailyMarketDataToAdd, ...dailyMarketDataCollection];
    }
    return dailyMarketDataCollection;
  }

  protected convertDateFromClient<T extends IDailyMarketData | NewDailyMarketData | PartialUpdateDailyMarketData>(
    dailyMarketData: T,
  ): RestOf<T> {
    return {
      ...dailyMarketData,
      fetchDate: dailyMarketData.fetchDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDailyMarketData: RestDailyMarketData): IDailyMarketData {
    return {
      ...restDailyMarketData,
      fetchDate: restDailyMarketData.fetchDate ? dayjs(restDailyMarketData.fetchDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDailyMarketData>): HttpResponse<IDailyMarketData> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDailyMarketData[]>): HttpResponse<IDailyMarketData[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
