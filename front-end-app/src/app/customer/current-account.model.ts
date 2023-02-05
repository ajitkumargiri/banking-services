import { Customer } from "./customer.model";
import { Transaction } from "./transaction.model";


export interface CurrentAccount {
  id: number;
  balance: number;
  customer: Customer;
  transactions: Transaction[];
}

export class CurrentAccountForm {
  customerId!: number;
  initialCredit!: number;
}
