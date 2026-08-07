import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IDailyMarketData } from '../daily-market-data.model';

import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  selector: 'jhi-daily-market-data-detail',
  templateUrl: './daily-market-data-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe, HasAnyAuthorityDirective],
})
export class DailyMarketDataDetailComponent {
  dailyMarketData = input<IDailyMarketData | null>(null);

  previousState(): void {
    window.history.back();
  }
}
