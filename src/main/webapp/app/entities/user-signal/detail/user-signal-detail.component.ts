import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IUserSignal } from '../user-signal.model';

import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  selector: 'jhi-user-signal-detail',
  templateUrl: './user-signal-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe, HasAnyAuthorityDirective],
})
export class UserSignalDetailComponent {
  userSignal = input<IUserSignal | null>(null);

  previousState(): void {
    window.history.back();
  }
}
