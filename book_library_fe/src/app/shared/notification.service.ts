import { inject, Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private messageService = inject(MessageService);

  showMessage(severity: string, summary: string, detail?: string) {
    this.messageService.add({
      severity: severity,
      summary: summary,
      detail: detail,
      life: 1500,
    });
  }

  showSuccessMessage(content: string) {
    return this.showMessage('success', 'Success', content);
  }

  showInvalidInputMessage() {
    return this.showMessage(
      'error',
      'Invalid search input',
      'Please check again your input data'
    );
  }
}
