import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import UserSignalResolve from './route/user-signal-routing-resolve.service';

const userSignalRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-signal.component').then(m => m.UserSignalComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-signal-detail.component').then(m => m.UserSignalDetailComponent),
    resolve: {
      userSignal: UserSignalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-signal-update.component').then(m => m.UserSignalUpdateComponent),
    resolve: {
      userSignal: UserSignalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-signal-update.component').then(m => m.UserSignalUpdateComponent),
    resolve: {
      userSignal: UserSignalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userSignalRoute;
