import dayjs from 'dayjs';
import { ICategorie } from 'app/shared/model/categorie.model';
import { IUser } from 'app/shared/model/user.model';
import { Priorite } from 'app/shared/model/enumerations/priorite.model';
import { StatutTache } from 'app/shared/model/enumerations/statut-tache.model';

export interface ITache {
  id?: number;
  titre?: string;
  description?: string | null;
  dateEcheance?: dayjs.Dayjs;
  priorite?: keyof typeof Priorite;
  statut?: keyof typeof StatutTache;
  categorie?: ICategorie | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<ITache> = {};
