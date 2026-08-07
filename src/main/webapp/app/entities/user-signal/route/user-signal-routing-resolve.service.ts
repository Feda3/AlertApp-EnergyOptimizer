import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserSignal } from '../user-signal.model';
import { UserSignalService } from '../service/user-signal.service';

const userSignalResolve = (route: ActivatedRouteSnapshot): Observable<null | IUserSignal> => {
  const id = route.params.id;
  if (id) {
    return inject(UserSignalService)
      .find(id)
      .pipe(
        mergeMap((userSignal: HttpResponse<IUserSignal>) => {
          if (userSignal.body) {
            return of(userSignal.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default userSignalResolve;
