import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IUserSignal } from '../user-signal.model';
import { UserSignalService } from '../service/user-signal.service';

@Component({
  templateUrl: './user-signal-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UserSignalDeleteDialogComponent {
  userSignal?: IUserSignal;

  protected userSignalService = inject(UserSignalService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userSignalService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
