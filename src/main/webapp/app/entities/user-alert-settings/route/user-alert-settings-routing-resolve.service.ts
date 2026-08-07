import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserAlertSettings } from '../user-alert-settings.model';
import { UserAlertSettingsService } from '../service/user-alert-settings.service';

const userAlertSettingsResolve = (route: ActivatedRouteSnapshot): Observable<null | IUserAlertSettings> => {
  const id = route.params.id;
  if (id) {
    return inject(UserAlertSettingsService)
      .find(id)
      .pipe(
        mergeMap((userAlertSettings: HttpResponse<IUserAlertSettings>) => {
          if (userAlertSettings.body) {
            return of(userAlertSettings.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default userAlertSettingsResolve;
