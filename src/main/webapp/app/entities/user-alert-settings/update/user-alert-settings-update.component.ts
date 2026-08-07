import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { AlertAction } from 'app/entities/enumerations/alert-action.model';
import { UserAlertSettingsService } from '../service/user-alert-settings.service';
import { IUserAlertSettings } from '../user-alert-settings.model';
import { UserAlertSettingsFormGroup, UserAlertSettingsFormService } from './user-alert-settings-form.service';

@Component({
  selector: 'jhi-user-alert-settings-update',
  templateUrl: './user-alert-settings-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, HasAnyAuthorityDirective],
})
export class UserAlertSettingsUpdateComponent implements OnInit {
  isSaving = false;
  userAlertSettings: IUserAlertSettings | null = null;
  alertActionValues = Object.keys(AlertAction);

  usersSharedCollection: IUser[] = [];

  protected userAlertSettingsService = inject(UserAlertSettingsService);
  protected userAlertSettingsFormService = inject(UserAlertSettingsFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserAlertSettingsFormGroup = this.userAlertSettingsFormService.createUserAlertSettingsFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAlertSettings }) => {
      this.userAlertSettings = userAlertSettings;
      if (userAlertSettings) {
        this.updateForm(userAlertSettings);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userAlertSettings = this.userAlertSettingsFormService.getUserAlertSettings(this.editForm);
    if (userAlertSettings.id !== null) {
      this.subscribeToSaveResponse(this.userAlertSettingsService.update(userAlertSettings));
    } else {
      this.subscribeToSaveResponse(this.userAlertSettingsService.create(userAlertSettings));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserAlertSettings>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(userAlertSettings: IUserAlertSettings): void {
    this.userAlertSettings = userAlertSettings;
    this.userAlertSettingsFormService.resetForm(this.editForm, userAlertSettings);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userAlertSettings.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userAlertSettings?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
