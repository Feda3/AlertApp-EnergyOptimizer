import dayjs from 'dayjs/esm';

import { IUserSignal, NewUserSignal } from './user-signal.model';

export const sampleWithRequiredData: IUserSignal = {
  id: 7872,
  signalDate: dayjs('2026-03-28'),
  action: 'SELL',
};

export const sampleWithPartialData: IUserSignal = {
  id: 12706,
  signalDate: dayjs('2026-03-28'),
  action: 'INFO',
};

export const sampleWithFullData: IUserSignal = {
  id: 5659,
  signalDate: dayjs('2026-03-28'),
  action: 'CONSUME',
  summaryMessage: 'woefully crazy typewriter',
};

export const sampleWithNewData: NewUserSignal = {
  signalDate: dayjs('2026-03-28'),
  action: 'SELL',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
