import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'relativeTime',
  standalone: true
})
export class RelativeTimePipe implements PipeTransform {
  transform(value: string | Date | null | undefined): string {
    if (!value) return '';
    
    // Support ISO strings with and without Z/offsets
    const date = new Date(value);
    const now = new Date();
    const elapsed = now.getTime() - date.getTime();

    if (isNaN(date.getTime())) {
      return '';
    }

    const seconds = Math.floor(elapsed / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 10) {
      return 'hace unos segundos';
    } else if (seconds < 60) {
      return `hace ${seconds} segundos`;
    } else if (minutes < 60) {
      return `hace ${minutes} ${minutes === 1 ? 'minuto' : 'minutos'}`;
    } else if (hours < 24) {
      return `hace ${hours} ${hours === 1 ? 'hora' : 'horas'}`;
    } else if (days < 30) {
      return `hace ${days} ${days === 1 ? 'día' : 'días'}`;
    } else {
      return date.toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' });
    }
  }
}
