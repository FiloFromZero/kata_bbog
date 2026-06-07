import { Component, ChangeDetectionStrategy, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly username = signal('');
  readonly password = signal('');
  readonly isLoading = signal(false);
  readonly hasError = signal(false);
  readonly errorMessage = signal('');
  readonly env = environment;

  onSubmit(event: Event): void {
    event.preventDefault();
    if (!this.username() || this.password().length < 4) return;

    this.isLoading.set(true);
    this.hasError.set(false);

    this.authService.login(this.username(), this.password()).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.hasError.set(true);
        if (err.status === 401) {
          this.errorMessage.set('Usuario o contraseña incorrectos.');
        } else {
          this.errorMessage.set('Error de conexión. Intente más tarde.');
        }
      }
    });
  }
}
