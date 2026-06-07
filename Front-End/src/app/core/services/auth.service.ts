import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginResponse } from '../models/customer.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  // Read initial token from sessionStorage
  readonly token = signal<string | null>(sessionStorage.getItem('token'));
  
  // Reactively derive if the user is authenticated
  readonly isAuthenticated = computed(() => !!this.token());

  login(username: string, password: string): Observable<LoginResponse> {
    // Basic Auth header for authentication endpoint
    const credentials = btoa(`${username}:${password}`);
    const headers = new HttpHeaders().set('Authorization', `Basic ${credentials}`);

    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, {}, { headers }).pipe(
      tap(response => {
        if (response && response.token) {
          sessionStorage.setItem('token', response.token);
          this.token.set(response.token);
        }
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem('token');
    this.token.set(null);
    this.router.navigate(['/login']);
  }
}
