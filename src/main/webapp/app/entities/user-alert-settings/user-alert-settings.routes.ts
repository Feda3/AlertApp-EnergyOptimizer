import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import UserAlertSettingsResolve from './route/user-alert-settings-routing-resolve.service';

const userAlertSettingsRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-alert-settings.component').then(m => m.UserAlertSettingsComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-alert-settings-detail.component').then(m => m.UserAlertSettingsDetailComponent),
    resolve: {
      userAlertSettings: UserAlertSettingsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-alert-settings-update.component').then(m => m.UserAlertSettingsUpdateComponent),
    resolve: {
      userAlertSettings: UserAlertSettingsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-alert-settings-update.component').then(m => m.UserAlertSettingsUpdateComponent),
    resolve: {
      userAlertSettings: UserAlertSettingsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userAlertSettingsRoute;
