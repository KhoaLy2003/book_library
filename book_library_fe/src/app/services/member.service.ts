import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';
import { BaseResponse, PageResponse } from '../shared/base-response.model';
import { Member, MemberDetail, NewMember } from '../models/member.model';

@Injectable({
  providedIn: 'root',
})
export class MemberService {
  private httpClient = inject(HttpClient);
  private members = signal<Member[]>([]);

  loadedMembers = this.members.asReadonly();

  //http://localhost:8080/api/v1/members/detail?membershipNumber=MBKhoa2024081971909
  fetchMemberByMembershipNum(membershipNumber: string) {
    return this.httpClient.get<BaseResponse<MemberDetail>>(
      `http://localhost:8080/api/v1/members/detail?membershipNumber=${membershipNumber}`
    );
  }

  fetchMembers(pageNo: number) {
    return this.httpClient.get<BaseResponse<PageResponse<Member>>>(
      `http://localhost:8080/api/v1/members?pageNo=${pageNo}`
    );
  }

  addMember(newMember: NewMember) {
    return this.httpClient.post<BaseResponse<string>>(
      'http://localhost:8080/api/v1/members',
      newMember
    );
  }

  updateMember(id: string, member: Member) {
    return this.httpClient.put(
      `http://localhost:8080/api/v1/members/${id}`,
      member
    );
  }

  getMembers() {
    return this.httpClient
      .get<Member[]>('http://localhost:8080/api/v1/members')
      .pipe(
        tap({
          next: (members) => this.members.set(members),
        })
      );
  }

  deleteMember(id: string) {
    return this.httpClient.delete(`http://localhost:3000/member/${id}`);
  }
}
