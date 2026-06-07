import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { httpResource } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Customer, CustomerRequest } from '../models/customer.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly http = inject(HttpClient);

  // Declarative resource to load the list of customers.
  // It automatically handles loading, value, and error signals, and can be reloaded reactively.
  readonly customers = httpResource<Customer[]>(() => `${environment.apiUrl}/api/customers`);

  create(data: CustomerRequest): Observable<Customer> {
    return this.http.post<Customer>(`${environment.apiUrl}/api/customers`, data).pipe(
      tap(() => this.customers.reload())
    );
  }

  update(id: string, data: CustomerRequest): Observable<Customer> {
    return this.http.put<Customer>(`${environment.apiUrl}/api/customers/${id}`, data).pipe(
      tap(() => this.customers.reload())
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/api/customers/${id}`).pipe(
      tap(() => this.customers.reload())
    );
  }
}
