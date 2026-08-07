import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDailyMarketData } from '../daily-market-data.model';
import { DailyMarketDataService } from '../service/daily-market-data.service';

@Component({
  templateUrl: './daily-market-data-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DailyMarketDataDeleteDialogComponent {
  dailyMarketData?: IDailyMarketData;

  protected dailyMarketDataService = inject(DailyMarketDataService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dailyMarketDataService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
