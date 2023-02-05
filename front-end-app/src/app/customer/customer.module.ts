import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CustomerRoutingModule } from './customer-routing.module';
import { CustomerDetailComponent } from './customer-detail/customer-detail.component';
import { CustomerListComponent } from './customer-list/customer-list.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    CustomerDetailComponent,
    CustomerListComponent
  ],
  imports: [
    FormsModule,
    CommonModule,
    CustomerRoutingModule
  ]
})
export class CustomerModule { }
