export interface ICategorie {
  id?: number;
  nom?: string;
  description?: string | null;
}

export const defaultValue: Readonly<ICategorie> = {};
