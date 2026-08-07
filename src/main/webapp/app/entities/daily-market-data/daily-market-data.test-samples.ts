import dayjs from 'dayjs/esm';

import { IDailyMarketData, NewDailyMarketData } from './daily-market-data.model';

export const sampleWithRequiredData: IDailyMarketData = {
  id: 31032,
  fetchDate: dayjs('2026-03-28'),
  symbol: 'minus',
  metricValue: 23713.19,
};

export const sampleWithPartialData: IDailyMarketData = {
  id: 28777,
  fetchDate: dayjs('2026-03-28'),
  symbol: 'yowza excluding',
  metricValue: 24799.56,
};

export const sampleWithFullData: IDailyMarketData = {
  id: 32614,
  fetchDate: dayjs('2026-03-28'),
  symbol: 'too freezing',
  metricValue: 13720.85,
};

export const sampleWithNewData: NewDailyMarketData = {
  fetchDate: dayjs('2026-03-28'),
  symbol: 'psst cruel',
  metricValue: 10688.44,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
