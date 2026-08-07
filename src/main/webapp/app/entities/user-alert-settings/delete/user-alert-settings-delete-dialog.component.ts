import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IUserAlertSettings } from '../user-alert-settings.model';
import { UserAlertSettingsService } from '../service/user-alert-settings.service';

@Component({
  templateUrl: './user-alert-settings-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UserAlertSettingsDeleteDialogComponent {
  userAlertSettings?: IUserAlertSettings;

  protected userAlertSettingsService = inject(UserAlertSettingsService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userAlertSettingsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
