import { Component, inject, ViewChild } from '@angular/core';
import { SidebarModule } from 'primeng/sidebar';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import { AvatarModule } from 'primeng/avatar';
import { StyleClassModule } from 'primeng/styleclass';
import { Sidebar } from 'primeng/sidebar';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../authentication/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    SidebarModule,
    ButtonModule,
    RippleModule,
    AvatarModule,
    StyleClassModule,
    RouterLinkActive,
    RouterLink,
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  @ViewChild('sidebarRef') sidebarRef!: Sidebar;
  private router = inject(Router);
  // private authService = inject(AuthService);

  closeCallback(e: any): void {
    this.sidebarRef.close(e);
  }

  sidebarVisible: boolean = false;

  openSidebar() {
    this.sidebarVisible = true;
  }

  closeSidebar() {
    this.sidebarVisible = false;
  }

  navigateTo(route: string): void {
    this.closeSidebar();
    this.router.navigate([route]);
  }

  onLogout() {
    // this.authService.logout();
  }
}
