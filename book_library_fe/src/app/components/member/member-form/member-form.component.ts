import { CommonModule } from '@angular/common';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { Component, inject, OnInit, signal } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { InputMaskModule } from 'primeng/inputmask';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { Member, NewMember } from '../../../models/member.model';
import { MemberService } from '../../../services/member.service';

@Component({
  selector: 'app-member-form',
  standalone: true,
  providers: [],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DialogModule,
    InputTextModule,
    DropdownModule,
    InputNumberModule,
    CalendarModule,
    InputMaskModule,
  ],
  templateUrl: './member-form.component.html',
  styleUrl: './member-form.component.css',
})
export class MemberFormComponent implements OnInit {
  private memberService = inject(MemberService);
  buttonText = signal<string>('Save');
  visible: boolean = true;
  private dialogRef = inject(DynamicDialogRef);
  private dialogConfig = inject(DynamicDialogConfig);
  member: Member | undefined;
  statusList = [
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Inactive', value: 'INACTIVE' },
    { label: 'Pending', value: 'PENDING' },
  ];

  ngOnInit(): void {
    this.initMemberDataForUpdate();
  }

  initMemberDataForUpdate() {
    if (this.dialogConfig.data.member) {
      setTimeout(() => {
        const member = this.dialogConfig.data.member;

        if (member.dob) {
          member.dob = new Date(member.dob);
        }

        const statusItem = this.statusList.find(
          (status) => status.value === member.status
        );
        if (statusItem) {
          member.status = statusItem;
        } else {
          member.status = this.statusList[0];
        }

        console.log(member);
        this.form.patchValue(member);
      });
    }
  }

  form = new FormGroup({
    firstName: new FormControl('', {
      validators: [Validators.required],
    }),
    lastName: new FormControl('', {
      validators: [Validators.required],
    }),
    email: new FormControl('', {
      validators: [Validators.email, Validators.required],
    }),
    phoneNumber: new FormControl('', {
      validators: [Validators.required],
    }),
    address: new FormControl('', {
      validators: [Validators.required],
    }),
    status: new FormControl(this.statusList[0], {
      validators: [Validators.required],
    }),
  });

  onCloseForm() {
    this.dialogRef.close();
  }

  onSubmit() {
    console.log(this.form.value);
    if (this.form.invalid) {
      console.log('INVALID FORM');
      return;
    }

    const member: NewMember = {
      firstName: this.form.value.firstName!,
      lastName: this.form.value.lastName!,
      email: this.form.value.email!,
      phoneNumber: this.form.value.phoneNumber!.replace(/\D/g, ''),
      address: this.form.value.address!,
      status: this.form.value.status!.value,
    };

    console.log(member);
    if (!this.dialogConfig.data.member) {
      this.memberService.addMember(member).subscribe({
        next: () => {
          this.dialogRef.close(true);
        },
        error: (error: Error) => {
          console.log(error);
          // this.dialogRef.close();
        },
      });
    }

    // this.memberService
    //   .updateMember(this.dialogConfig.data.member.id, member)
    //   .subscribe({
    //     next: () => this.dialogRef.close(true),
    //     error: () => this.dialogRef.close(false),
    //   });
  }
}
