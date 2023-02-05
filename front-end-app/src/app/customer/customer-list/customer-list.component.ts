import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../customer.service';




@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.scss']
})
export class CustomerListComponent implements OnInit {

  customers;
  error!: string;

  constructor(
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
  ) { }

  ngOnInit() {
    this.getCustomers();
  }

  public getCustomers(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers = data;
      },
      error: (error: HttpErrorResponse) => {
        console.log(error);
        this.onError(error);
      }
    });
  }

  public onError(error: HttpErrorResponse) {
    this.error = error.error.detail;
  }
}
