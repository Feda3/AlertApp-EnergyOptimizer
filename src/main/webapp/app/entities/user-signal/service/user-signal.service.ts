import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserSignal, NewUserSignal } from '../user-signal.model';

export type PartialUpdateUserSignal = Partial<IUserSignal> & Pick<IUserSignal, 'id'>;

type RestOf<T extends IUserSignal | NewUserSignal> = Omit<T, 'signalDate'> & {
  signalDate?: string | null;
};

export type RestUserSignal = RestOf<IUserSignal>;

export type NewRestUserSignal = RestOf<NewUserSignal>;

export type PartialUpdateRestUserSignal = RestOf<PartialUpdateUserSignal>;

export type EntityResponseType = HttpResponse<IUserSignal>;
export type EntityArrayResponseType = HttpResponse<IUserSignal[]>;

@Injectable({ providedIn: 'root' })
export class UserSignalService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-signals');

  create(userSignal: NewUserSignal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userSignal);
    return this.http
      .post<RestUserSignal>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(userSignal: IUserSignal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userSignal);
    return this.http
      .put<RestUserSignal>(`${this.resourceUrl}/${this.getUserSignalIdentifier(userSignal)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(userSignal: PartialUpdateUserSignal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userSignal);
    return this.http
      .patch<RestUserSignal>(`${this.resourceUrl}/${this.getUserSignalIdentifier(userSignal)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUserSignal>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUserSignal[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserSignalIdentifier(userSignal: Pick<IUserSignal, 'id'>): number {
    return userSignal.id;
  }

  compareUserSignal(o1: Pick<IUserSignal, 'id'> | null, o2: Pick<IUserSignal, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserSignalIdentifier(o1) === this.getUserSignalIdentifier(o2) : o1 === o2;
  }

  addUserSignalToCollectionIfMissing<Type extends Pick<IUserSignal, 'id'>>(
    userSignalCollection: Type[],
    ...userSignalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userSignals: Type[] = userSignalsToCheck.filter(isPresent);
    if (userSignals.length > 0) {
      const userSignalCollectionIdentifiers = userSignalCollection.map(userSignalItem => this.getUserSignalIdentifier(userSignalItem));
      const userSignalsToAdd = userSignals.filter(userSignalItem => {
        const userSignalIdentifier = this.getUserSignalIdentifier(userSignalItem);
        if (userSignalCollectionIdentifiers.includes(userSignalIdentifier)) {
          return false;
        }
        userSignalCollectionIdentifiers.push(userSignalIdentifier);
        return true;
      });
      return [...userSignalsToAdd, ...userSignalCollection];
    }
    return userSignalCollection;
  }

  protected convertDateFromClient<T extends IUserSignal | NewUserSignal | PartialUpdateUserSignal>(userSignal: T): RestOf<T> {
    return {
      ...userSignal,
      signalDate: userSignal.signalDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restUserSignal: RestUserSignal): IUserSignal {
    return {
      ...restUserSignal,
      signalDate: restUserSignal.signalDate ? dayjs(restUserSignal.signalDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUserSignal>): HttpResponse<IUserSignal> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUserSignal[]>): HttpResponse<IUserSignal[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
