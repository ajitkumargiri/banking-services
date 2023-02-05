import { CurrentAccount } from "./current-account.model";

export interface Customer {
  id: number;
  name: string;
  surname: string;
  currentAccounts: CurrentAccount[];
}

