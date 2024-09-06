import { Component } from '@angular/core';
import { MemberListComponent } from "../../components/member/member-list/member-list.component";

@Component({
  selector: 'app-member',
  standalone: true,
  imports: [MemberListComponent],
  templateUrl: './member.page.html',
  styleUrl: './member.page.css'
})
export class MemberPage {

}
