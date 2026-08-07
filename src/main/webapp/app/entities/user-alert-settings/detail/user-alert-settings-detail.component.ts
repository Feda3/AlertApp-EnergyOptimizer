import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IUserAlertSettings } from '../user-alert-settings.model';

@Component({
  selector: 'jhi-user-alert-settings-detail',
  templateUrl: './user-alert-settings-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class UserAlertSettingsDetailComponent {
  userAlertSettings = input<IUserAlertSettings | null>(null);

  previousState(): void {
    window.history.back();
  }
}
