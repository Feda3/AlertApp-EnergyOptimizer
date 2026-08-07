import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUserAlertSettings } from 'app/entities/user-alert-settings/user-alert-settings.model';
import { UserAlertSettingsService } from 'app/entities/user-alert-settings/service/user-alert-settings.service';
import { AlertAction } from 'app/entities/enumerations/alert-action.model';
import { UserSignalService } from '../service/user-signal.service';
import { IUserSignal } from '../user-signal.model';
import { UserSignalFormGroup, UserSignalFormService } from './user-signal-form.service';

@Component({
  selector: 'jhi-user-signal-update',
  templateUrl: './user-signal-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UserSignalUpdateComponent implements OnInit {
  isSaving = false;
  userSignal: IUserSignal | null = null;
  alertActionValues = Object.keys(AlertAction);

  usersSharedCollection: IUser[] = [];
  userAlertSettingsSharedCollection: IUserAlertSettings[] = [];

  protected userSignalService = inject(UserSignalService);
  protected userSignalFormService = inject(UserSignalFormService);
  protected userService = inject(UserService);
  protected userAlertSettingsService = inject(UserAlertSettingsService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserSignalFormGroup = this.userSignalFormService.createUserSignalFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareUserAlertSettings = (o1: IUserAlertSettings | null, o2: IUserAlertSettings | null): boolean =>
    this.userAlertSettingsService.compareUserAlertSettings(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userSignal }) => {
      this.userSignal = userSignal;
      if (userSignal) {
        this.updateForm(userSignal);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userSignal = this.userSignalFormService.getUserSignal(this.editForm);
    if (userSignal.id !== null) {
      this.subscribeToSaveResponse(this.userSignalService.update(userSignal));
    } else {
      this.subscribeToSaveResponse(this.userSignalService.create(userSignal));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserSignal>>): void {
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

  protected updateForm(userSignal: IUserSignal): void {
    this.userSignal = userSignal;
    this.userSignalFormService.resetForm(this.editForm, userSignal);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userSignal.user);
    this.userAlertSettingsSharedCollection = this.userAlertSettingsService.addUserAlertSettingsToCollectionIfMissing<IUserAlertSettings>(
      this.userAlertSettingsSharedCollection,
      userSignal.setting,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userSignal?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.userAlertSettingsService
      .query()
      .pipe(map((res: HttpResponse<IUserAlertSettings[]>) => res.body ?? []))
      .pipe(
        map((userAlertSettings: IUserAlertSettings[]) =>
          this.userAlertSettingsService.addUserAlertSettingsToCollectionIfMissing<IUserAlertSettings>(
            userAlertSettings,
            this.userSignal?.setting,
          ),
        ),
      )
      .subscribe((userAlertSettings: IUserAlertSettings[]) => (this.userAlertSettingsSharedCollection = userAlertSettings));
  }
}
