import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { MemberDetailComponent } from './components/member/member-detail/member-detail.component';
import { BookDetailComponent } from './components/book/book-detail/book-detail.component';
import { NewBorrowingComponent } from './components/borrowing/new-borrowing/new-borrowing.component';
import { BookPage } from './pages/book/book.page';
import { MemberPage } from './pages/member/member.page';
import { BorrowingPage } from './pages/borrowing/borrowing.page';
import { DashboardPage } from './pages/dashboard/dashboard.page';
import { AuthGuard } from './authentication/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    // canActivate: [AuthGuard],
    // data: { roles: ['admin'] },
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        component: DashboardPage,
      },
      {
        path: 'members',
        component: MemberPage,
        // canActivate: [AuthGuard],
        // data: { roles: ['member'] },
      },
      {
        path: 'members/:membershipNumber',
        component: MemberDetailComponent,
      },
      {
        path: 'books',
        component: BookPage,
      },
      {
        path: 'books/:isbn',
        component: BookDetailComponent,
      },
      {
        path: 'borrowing',
        component: BorrowingPage,
      },
      { path: 'borrowing/new-borrowing', component: NewBorrowingComponent },
      // {
      //   path: 'access-denied',
      //   component: AccessDeniedComponent,
      //   canActivate: [AuthGuard],
      // },
    ],
  },
];
