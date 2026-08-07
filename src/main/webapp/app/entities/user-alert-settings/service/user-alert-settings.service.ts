import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserAlertSettings, NewUserAlertSettings } from '../user-alert-settings.model';

export type PartialUpdateUserAlertSettings = Partial<IUserAlertSettings> & Pick<IUserAlertSettings, 'id'>;

export type EntityResponseType = HttpResponse<IUserAlertSettings>;
export type EntityArrayResponseType = HttpResponse<IUserAlertSettings[]>;

@Injectable({ providedIn: 'root' })
export class UserAlertSettingsService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-alert-settings');

  create(userAlertSettings: NewUserAlertSettings): Observable<EntityResponseType> {
    return this.http.post<IUserAlertSettings>(this.resourceUrl, userAlertSettings, { observe: 'response' });
  }

  update(userAlertSettings: IUserAlertSettings): Observable<EntityResponseType> {
    return this.http.put<IUserAlertSettings>(
      `${this.resourceUrl}/${this.getUserAlertSettingsIdentifier(userAlertSettings)}`,
      userAlertSettings,
      { observe: 'response' },
    );
  }

  partialUpdate(userAlertSettings: PartialUpdateUserAlertSettings): Observable<EntityResponseType> {
    return this.http.patch<IUserAlertSettings>(
      `${this.resourceUrl}/${this.getUserAlertSettingsIdentifier(userAlertSettings)}`,
      userAlertSettings,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserAlertSettings>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserAlertSettings[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserAlertSettingsIdentifier(userAlertSettings: Pick<IUserAlertSettings, 'id'>): number {
    return userAlertSettings.id;
  }

  compareUserAlertSettings(o1: Pick<IUserAlertSettings, 'id'> | null, o2: Pick<IUserAlertSettings, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserAlertSettingsIdentifier(o1) === this.getUserAlertSettingsIdentifier(o2) : o1 === o2;
  }

  addUserAlertSettingsToCollectionIfMissing<Type extends Pick<IUserAlertSettings, 'id'>>(
    userAlertSettingsCollection: Type[],
    ...userAlertSettingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userAlertSettings: Type[] = userAlertSettingsToCheck.filter(isPresent);
    if (userAlertSettings.length > 0) {
      const userAlertSettingsCollectionIdentifiers = userAlertSettingsCollection.map(userAlertSettingsItem =>
        this.getUserAlertSettingsIdentifier(userAlertSettingsItem),
      );
      const userAlertSettingsToAdd = userAlertSettings.filter(userAlertSettingsItem => {
        const userAlertSettingsIdentifier = this.getUserAlertSettingsIdentifier(userAlertSettingsItem);
        if (userAlertSettingsCollectionIdentifiers.includes(userAlertSettingsIdentifier)) {
          return false;
        }
        userAlertSettingsCollectionIdentifiers.push(userAlertSettingsIdentifier);
        return true;
      });
      return [...userAlertSettingsToAdd, ...userAlertSettingsCollection];
    }
    return userAlertSettingsCollection;
  }
}
