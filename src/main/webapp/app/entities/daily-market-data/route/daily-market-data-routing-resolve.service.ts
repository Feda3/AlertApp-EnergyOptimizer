import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDailyMarketData } from '../daily-market-data.model';
import { DailyMarketDataService } from '../service/daily-market-data.service';

const dailyMarketDataResolve = (route: ActivatedRouteSnapshot): Observable<null | IDailyMarketData> => {
  const id = route.params.id;
  if (id) {
    return inject(DailyMarketDataService)
      .find(id)
      .pipe(
        mergeMap((dailyMarketData: HttpResponse<IDailyMarketData>) => {
          if (dailyMarketData.body) {
            return of(dailyMarketData.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default dailyMarketDataResolve;
