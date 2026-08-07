import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserSignalDetailComponent } from './user-signal-detail.component';

describe('UserSignal Management Detail Component', () => {
  let comp: UserSignalDetailComponent;
  let fixture: ComponentFixture<UserSignalDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserSignalDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./user-signal-detail.component').then(m => m.UserSignalDetailComponent),
              resolve: { userSignal: () => of({ id: 1082 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(UserSignalDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserSignalDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load userSignal on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', UserSignalDetailComponent);

      // THEN
      expect(instance.userSignal()).toEqual(expect.objectContaining({ id: 1082 }));
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
