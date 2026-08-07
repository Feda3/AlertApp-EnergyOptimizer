import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DailyMarketDataDetailComponent } from './daily-market-data-detail.component';

describe('DailyMarketData Management Detail Component', () => {
  let comp: DailyMarketDataDetailComponent;
  let fixture: ComponentFixture<DailyMarketDataDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DailyMarketDataDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./daily-market-data-detail.component').then(m => m.DailyMarketDataDetailComponent),
              resolve: { dailyMarketData: () => of({ id: 16517 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DailyMarketDataDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DailyMarketDataDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load dailyMarketData on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DailyMarketDataDetailComponent);

      // THEN
      expect(instance.dailyMarketData()).toEqual(expect.objectContaining({ id: 16517 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
