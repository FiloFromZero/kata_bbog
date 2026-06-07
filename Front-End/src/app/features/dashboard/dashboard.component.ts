import { Component, ChangeDetectionStrategy, signal, computed, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../core/services/customer.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { RelativeTimePipe } from '../../shared/pipes/relative-time.pipe';
import { environment } from '../../../environments/environment';
import { Customer } from '../../core/models/customer.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [FormsModule, RelativeTimePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent {
  // Services Injection
  readonly customerService = inject(CustomerService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);

  // Environment and Resource References
  readonly env = environment;
  readonly customersResource = this.customerService.customers;

  // Reactively expose toast list to template
  readonly toasts = this.toastService.toasts;

  // Local Component State Signals
  readonly searchQuery = signal('');
  readonly isFormOpen = signal(false);
  readonly isConfirmOpen = signal(false);
  readonly isSaving = signal(false);
  readonly selectedCustomer = signal<Customer | null>(null);

  // Form Fields Signals
  readonly formName = signal('');
  readonly formEmail = signal('');

  // Pagination State Signals
  readonly pageSize = signal(10);
  readonly currentPage = signal(1);

  // Reactive computed property to filter the customers locally based on search query
  readonly filteredCustomers = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const rawList = this.customersResource.value() || [];

    if (!query) {
      return rawList;
    }

    return rawList.filter(customer => 
      customer.name.toLowerCase().includes(query) || 
      customer.email.toLowerCase().includes(query)
    );
  });

  // Pagination calculations based on filtered list
  readonly totalPages = computed(() => {
    const total = this.filteredCustomers().length;
    return Math.max(1, Math.ceil(total / this.pageSize()));
  });

  readonly paginatedCustomers = computed(() => {
    const list = this.filteredCustomers();
    const startIndex = (this.currentPage() - 1) * this.pageSize();
    const endIndex = startIndex + this.pageSize();
    return list.slice(startIndex, endIndex);
  });

  readonly showingStart = computed(() => {
    if (this.filteredCustomers().length === 0) return 0;
    return (this.currentPage() - 1) * this.pageSize() + 1;
  });

  readonly showingEnd = computed(() => {
    const end = this.currentPage() * this.pageSize();
    const total = this.filteredCustomers().length;
    return Math.min(end, total);
  });

  readonly totalCount = computed(() => this.filteredCustomers().length);

  readonly pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];
    
    // Always show up to 5 page numbers centered around the current page
    let start = Math.max(1, current - 2);
    let end = Math.min(total, start + 4);
    
    if (end - start < 4) {
      start = Math.max(1, end - 4);
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  });

  // Action Methods
  onLogout(): void {
    this.authService.logout();
    this.toastService.info('Sesión cerrada correctamente.');
  }

  onSearchQueryChange(query: string): void {
    this.searchQuery.set(query);
    this.currentPage.set(1); // Reset page on filter
  }

  openAddModal(): void {
    this.selectedCustomer.set(null);
    this.formName.set('');
    this.formEmail.set('');
    this.isFormOpen.set(true);
  }

  openEditModal(customer: Customer): void {
    this.selectedCustomer.set(customer);
    this.formName.set(customer.name);
    this.formEmail.set(customer.email);
    this.isFormOpen.set(true);
  }

  closeFormModal(): void {
    this.isFormOpen.set(false);
    this.selectedCustomer.set(null);
    this.formName.set('');
    this.formEmail.set('');
  }

  openDeleteConfirm(customer: Customer): void {
    this.selectedCustomer.set(customer);
    this.isConfirmOpen.set(true);
  }

  closeDeleteConfirm(): void {
    this.isConfirmOpen.set(false);
    this.selectedCustomer.set(null);
  }

  onSaveCustomer(event: Event): void {
    event.preventDefault();
    const name = this.formName().trim();
    const email = this.formEmail().trim();

    if (!name || !email) {
      this.toastService.error('Todos los campos son requeridos.');
      return;
    }

    this.isSaving.set(true);
    const requestData = { name, email };
    const currentCustomer = this.selectedCustomer();

    if (currentCustomer) {
      // Update Mode
      this.customerService.update(currentCustomer.id, requestData).subscribe({
        next: () => {
          this.toastService.success('Cliente actualizado exitosamente.');
          this.isSaving.set(false);
          this.closeFormModal();
        },
        error: (err) => {
          this.isSaving.set(false);
          if (err.status === 409) {
            this.toastService.error('El correo electrónico ya está registrado.');
          } else {
            this.toastService.error('Error al actualizar el cliente.');
          }
        }
      });
    } else {
      // Create Mode
      this.customerService.create(requestData).subscribe({
        next: () => {
          this.toastService.success('Cliente registrado exitosamente.');
          this.isSaving.set(false);
          this.closeFormModal();
        },
        error: (err) => {
          this.isSaving.set(false);
          if (err.status === 409) {
            this.toastService.error('El correo electrónico ya está registrado.');
          } else {
            this.toastService.error('Error al registrar el cliente.');
          }
        }
      });
    }
  }

  onConfirmDelete(): void {
    const customer = this.selectedCustomer();
    if (!customer) return;

    this.isSaving.set(true);
    this.customerService.delete(customer.id).subscribe({
      next: () => {
        this.toastService.success('Cliente eliminado exitosamente.');
        this.isSaving.set(false);
        this.closeDeleteConfirm();

        // Adjust current page if the list shrunk and we are now out of bounds
        setTimeout(() => {
          const total = this.totalPages();
          if (this.currentPage() > total) {
            this.currentPage.set(Math.max(1, total));
          }
        }, 100);
      },
      error: () => {
        this.toastService.error('Error al eliminar el cliente.');
        this.isSaving.set(false);
      }
    });
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages()) {
      this.currentPage.update(p => p + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage() > 1) {
      this.currentPage.update(p => p - 1);
    }
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
    }
  }

  dismissToast(id: number): void {
    this.toastService.dismiss(id);
  }
}
