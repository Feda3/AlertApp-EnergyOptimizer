import { IUserAlertSettings, NewUserAlertSettings } from './user-alert-settings.model';

export const sampleWithRequiredData: IUserAlertSettings = {
  id: 12158,
  symbol: 'bidet next',
  threshold: 779.21,
  triggerIfGreater: true,
  action: 'BUY',
};

export const sampleWithPartialData: IUserAlertSettings = {
  id: 16644,
  symbol: 'shrilly fall',
  threshold: 5469.23,
  triggerIfGreater: true,
  action: 'SELL',
  startTime: 'within',
  endTime: 'knowledgeably intently',
};

export const sampleWithFullData: IUserAlertSettings = {
  id: 19188,
  symbol: 'stupendous indolent into',
  threshold: 27573.7,
  triggerIfGreater: false,
  action: 'CONSUME',
  startTime: 'incidentally warlike',
  endTime: 'unkempt messy',
  minDurationMinutes: 12269,
  isActive: true,
};

export const sampleWithNewData: NewUserAlertSettings = {
  symbol: 'restfully ultimately lamp',
  threshold: 9601.69,
  triggerIfGreater: true,
  action: 'CONSUME',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
