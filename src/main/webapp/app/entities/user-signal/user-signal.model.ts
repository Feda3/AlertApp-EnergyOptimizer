import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IUserAlertSettings } from 'app/entities/user-alert-settings/user-alert-settings.model';
import { AlertAction } from 'app/entities/enumerations/alert-action.model';

export interface IUserSignal {
  id: number;
  signalDate?: dayjs.Dayjs | null;
  action?: keyof typeof AlertAction | null;
  summaryMessage?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  setting?: IUserAlertSettings | null;
}

export type NewUserSignal = Omit<IUserSignal, 'id'> & { id: null };
