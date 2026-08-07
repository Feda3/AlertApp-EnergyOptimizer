import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDailyMarketData } from '../daily-market-data.model';
import { DailyMarketDataService } from '../service/daily-market-data.service';
import { DailyMarketDataFormGroup, DailyMarketDataFormService } from './daily-market-data-form.service';

@Component({
  selector: 'jhi-daily-market-data-update',
  templateUrl: './daily-market-data-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DailyMarketDataUpdateComponent implements OnInit {
  isSaving = false;
  dailyMarketData: IDailyMarketData | null = null;

  protected dailyMarketDataService = inject(DailyMarketDataService);
  protected dailyMarketDataFormService = inject(DailyMarketDataFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DailyMarketDataFormGroup = this.dailyMarketDataFormService.createDailyMarketDataFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dailyMarketData }) => {
      this.dailyMarketData = dailyMarketData;
      if (dailyMarketData) {
        this.updateForm(dailyMarketData);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dailyMarketData = this.dailyMarketDataFormService.getDailyMarketData(this.editForm);
    if (dailyMarketData.id !== null) {
      this.subscribeToSaveResponse(this.dailyMarketDataService.update(dailyMarketData));
    } else {
      this.subscribeToSaveResponse(this.dailyMarketDataService.create(dailyMarketData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDailyMarketData>>): void {
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

  protected updateForm(dailyMarketData: IDailyMarketData): void {
    this.dailyMarketData = dailyMarketData;
    this.dailyMarketDataFormService.resetForm(this.editForm, dailyMarketData);
  }
}
