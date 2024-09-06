import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ConfirmationService } from 'primeng/api';
import { DropdownModule } from 'primeng/dropdown';
import { PaginatorModule } from 'primeng/paginator';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InputGroupModule } from 'primeng/inputgroup';
import { CalendarModule } from 'primeng/calendar';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    TableModule,
    ButtonModule,
    ToastModule,
    ConfirmPopupModule,
    ToolbarModule,
    TagModule,
    ProgressSpinnerModule,
    DropdownModule,
    PaginatorModule,
    TooltipModule,
    DatePipe,
  ],
  declarations: [],
  exports: [
    CardModule,
    TableModule,
    ButtonModule,
    ToastModule,
    ConfirmPopupModule,
    ToolbarModule,
    TagModule,
    ProgressSpinnerModule,
    DropdownModule,
    PaginatorModule,
    TooltipModule,
    DatePipe,
  ],
  providers: [ConfirmationService],
})
export class SharedModule {}

@NgModule({
  imports: [ReactiveFormsModule, FormsModule, InputGroupModule, CalendarModule],
  exports: [ReactiveFormsModule, FormsModule, InputGroupModule, CalendarModule],
})
export class SharedFormModule {}
