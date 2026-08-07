import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { DailyMarketDataService } from '../service/daily-market-data.service';
import { IDailyMarketData } from '../daily-market-data.model';
import { DailyMarketDataFormService } from './daily-market-data-form.service';

import { DailyMarketDataUpdateComponent } from './daily-market-data-update.component';

describe('DailyMarketData Management Update Component', () => {
  let comp: DailyMarketDataUpdateComponent;
  let fixture: ComponentFixture<DailyMarketDataUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dailyMarketDataFormService: DailyMarketDataFormService;
  let dailyMarketDataService: DailyMarketDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DailyMarketDataUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DailyMarketDataUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DailyMarketDataUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dailyMarketDataFormService = TestBed.inject(DailyMarketDataFormService);
    dailyMarketDataService = TestBed.inject(DailyMarketDataService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const dailyMarketData: IDailyMarketData = { id: 15304 };

      activatedRoute.data = of({ dailyMarketData });
      comp.ngOnInit();

      expect(comp.dailyMarketData).toEqual(dailyMarketData);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyMarketData>>();
      const dailyMarketData = { id: 16517 };
      jest.spyOn(dailyMarketDataFormService, 'getDailyMarketData').mockReturnValue(dailyMarketData);
      jest.spyOn(dailyMarketDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyMarketData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyMarketData }));
      saveSubject.complete();

      // THEN
      expect(dailyMarketDataFormService.getDailyMarketData).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dailyMarketDataService.update).toHaveBeenCalledWith(expect.objectContaining(dailyMarketData));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyMarketData>>();
      const dailyMarketData = { id: 16517 };
      jest.spyOn(dailyMarketDataFormService, 'getDailyMarketData').mockReturnValue({ id: null });
      jest.spyOn(dailyMarketDataService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyMarketData: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyMarketData }));
      saveSubject.complete();

      // THEN
      expect(dailyMarketDataFormService.getDailyMarketData).toHaveBeenCalled();
      expect(dailyMarketDataService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyMarketData>>();
      const dailyMarketData = { id: 16517 };
      jest.spyOn(dailyMarketDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyMarketData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dailyMarketDataService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
