export interface Transaction {
  id: number;
  amount: number;
  currentAccountId: number;
}

export class NewTransaction {
  amount: number | undefined;
}
