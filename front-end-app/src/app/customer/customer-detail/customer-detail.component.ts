import { NewTransaction, Transaction } from './../transaction.model';
import { CurrentAccountForm } from './../current-account.model';
import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrentAccount } from '../current-account.model';
import { Customer } from '../customer.model';
import { CustomerService } from '../customer.service';

@Component({
  selector: 'app-customer-detail',
  templateUrl: './customer-detail.component.html',
  styleUrls: ['./customer-detail.component.scss']
})
export class CustomerDetailComponent {

  selectedCurrentAccountId: number = 0;
  customer: Customer | null = null;
  currentAccount!: CurrentAccount;
  transactions!: Transaction[];
  currentAccountForm: CurrentAccountForm = new CurrentAccountForm();
  transactionForm: NewTransaction = new NewTransaction();
  error: any;

  constructor(protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    public router: Router) { }

  ngOnInit(): void {
    this.getCustomer(this.activatedRoute.snapshot.params["id"]);
  }

  getCustomer(id: number): void {
    console.log(id);
    this.customerService.findCustomerById(id)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.customer = data;
          this.currentAccountForm.customerId = id;
        },
        error: (error: HttpErrorResponse) => {

          this.onError(error);
          console.log(this.error);
        }
      });
  }


  save(): void {
    this.customerService.createCurrentAccount(this.currentAccountForm).subscribe({
      next: (data) => {
        console.log("success");
        this.getCustomer(this.activatedRoute.snapshot.params["id"]);
      },
      error: (error: HttpErrorResponse) => {
        console.log("error");
        this.onError(error);
      }
    });
  }

  saveTransaction(): void {
    if (this.currentAccount != null) {
      this.customerService.createTransation(this.currentAccount.id, this.transactionForm).subscribe({
        next: () => {
          this.customerService.findCustomerById(this.activatedRoute.snapshot.params["id"])
            .subscribe({
              next: (data) => {
                this.customer = data;
                this.getCurrentAccount();
              },
              error: (error: HttpErrorResponse) => {
                this.onError(error);
              }
            });
        },
        error: (error: HttpErrorResponse) => {
          console.log("error");
          this.onError(error);
        }

      });
    }

  }

  onOptionsSelected() {
    this.getCurrentAccount();
  }
  getCurrentAccount(): void {
    if (this.customer?.currentAccounts != null) {
      this.currentAccount = this.customer?.currentAccounts.filter((item) => {
        return item.id === Number(this.selectedCurrentAccountId);
      })[0];

    }
  }

  public onError(error: HttpErrorResponse) {
    console.log(error);
    this.error = error.error.detail;
  }
  public refreshError() {
    this.error = null;
  }
}
