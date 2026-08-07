import { IUser } from 'app/entities/user/user.model';
import { AlertAction } from 'app/entities/enumerations/alert-action.model';

export interface IUserAlertSettings {
  id: number;
  symbol?: string | null;
  threshold?: number | null;
  triggerIfGreater?: boolean | null;
  action?: keyof typeof AlertAction | null;
  startTime?: string | null;
  endTime?: string | null;
  minDurationMinutes?: number | null;
  isActive?: boolean | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewUserAlertSettings = Omit<IUserAlertSettings, 'id'> & { id: null };
