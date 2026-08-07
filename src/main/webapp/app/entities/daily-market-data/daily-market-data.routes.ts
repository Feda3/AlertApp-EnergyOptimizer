import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DailyMarketDataResolve from './route/daily-market-data-routing-resolve.service';

const dailyMarketDataRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/daily-market-data.component').then(m => m.DailyMarketDataComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/daily-market-data-detail.component').then(m => m.DailyMarketDataDetailComponent),
    resolve: {
      dailyMarketData: DailyMarketDataResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/daily-market-data-update.component').then(m => m.DailyMarketDataUpdateComponent),
    resolve: {
      dailyMarketData: DailyMarketDataResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/daily-market-data-update.component').then(m => m.DailyMarketDataUpdateComponent),
    resolve: {
      dailyMarketData: DailyMarketDataResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default dailyMarketDataRoute;
