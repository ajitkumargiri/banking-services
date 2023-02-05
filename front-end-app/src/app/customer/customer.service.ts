import { environment } from './../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer } from './customer.model';
import { CurrentAccount, CurrentAccountForm } from './current-account.model';
import { NewTransaction, Transaction } from './transaction.model';


@Injectable({
  providedIn: 'root'
})
export class CustomerService {


  protected apiUrl = `${environment.apiUrl}/customers`;

  constructor(protected http: HttpClient) { }


  findCustomerById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/${id}`);
  }

  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiUrl);
  }
  createCurrentAccount(currentAccount: CurrentAccountForm): Observable<CurrentAccount> {
    return this.http.post<CurrentAccount>(`${this.apiUrl}/account`, currentAccount);
  }

  createTransation(id: number, trasaction: NewTransaction): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/account/${id}/transactions`, trasaction);
  }

}
