import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { MenubarModule } from 'primeng/menubar';
import { BadgeModule } from 'primeng/badge';
import { AvatarModule } from 'primeng/avatar';
import { InputTextModule } from 'primeng/inputtext';
import { CommonModule } from '@angular/common';
import { RippleModule } from 'primeng/ripple';

import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from '../../authentication/auth.service';
import { NotificationService } from '../../shared/notification.service';
import { SharedModule } from '../../shared/shared.module';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    MenubarModule,
    BadgeModule,
    AvatarModule,
    InputTextModule,
    RippleModule,
    CommonModule,
    SharedModule,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  @Output() sidebarToggle = new EventEmitter<void>();
  socketClient: any = null;
  private notificationSubscription: any;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  toggleSidebar() {
    this.sidebarToggle.emit();
  }

  items: MenuItem[] | undefined;

  ngOnInit() {
    if (this.authService.getToken()) {
      let ws = new SockJS('http://localhost:8080/ws');
      this.socketClient = Stomp.over(ws);
      console.log(this.authService.getToken());
      this.socketClient.connect(
        { Authorization: 'Bearer ' + this.authService.getToken() },
        () => {
          console.log('Connecting to Websocket');
          this.notificationSubscription = this.socketClient.subscribe(
            `/topic/notifications`,
            (message: any) => {
              const notification = JSON.parse(message.body);
              console.log(notification);
              this.notificationService.showMessage('info', notification.message, notification.message);
            }
          );
        }
      );
    }

    this.items = [
      {
        icon: 'pi pi-bars',
        command: () => this.toggleSidebar(),
      },
      {
        label: 'Home',
        icon: 'pi pi-home',
      },
      {
        label: 'Features',
        icon: 'pi pi-star',
      },
      {
        label: 'Projects',
        icon: 'pi pi-search',
        items: [
          {
            label: 'Core',
            icon: 'pi pi-bolt',
            shortcut: '⌘+S',
          },
          {
            label: 'Blocks',
            icon: 'pi pi-server',
            shortcut: '⌘+B',
          },
          {
            label: 'UI Kit',
            icon: 'pi pi-pencil',
            shortcut: '⌘+U',
          },
          {
            separator: true,
          },
          {
            label: 'Templates',
            icon: 'pi pi-palette',
            items: [
              {
                label: 'Apollo',
                icon: 'pi pi-palette',
                badge: '2',
              },
              {
                label: 'Ultima',
                icon: 'pi pi-palette',
                badge: '3',
              },
            ],
          },
        ],
      },
      {
        label: 'Contact',
        icon: 'pi pi-envelope',
        badge: '3',
      },
    ];
  }
}
