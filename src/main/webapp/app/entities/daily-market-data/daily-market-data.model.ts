import dayjs from 'dayjs/esm';

export interface IDailyMarketData {
  id: number;
  fetchDate?: dayjs.Dayjs | null;
  symbol?: string | null;
  metricValue?: number | null;
}

export type NewDailyMarketData = Omit<IDailyMarketData, 'id'> & { id: null };
