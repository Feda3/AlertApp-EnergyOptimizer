import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'alertApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'daily-market-data',
    data: { pageTitle: 'alertApp.dailyMarketData.home.title' },
    loadChildren: () => import('./daily-market-data/daily-market-data.routes'),
  },
  {
    path: 'user-alert-settings',
    data: { pageTitle: 'alertApp.userAlertSettings.home.title' },
    loadChildren: () => import('./user-alert-settings/user-alert-settings.routes'),
  },
  {
    path: 'user-signal',
    data: { pageTitle: 'alertApp.userSignal.home.title' },
    loadChildren: () => import('./user-signal/user-signal.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
