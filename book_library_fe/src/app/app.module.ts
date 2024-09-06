// import { APP_INITIALIZER, NgModule } from '@angular/core';
// import { AppComponent } from './app.component';
// import { KeycloakService } from './service/keycloak.service';
// import { BrowserModule } from '@angular/platform-browser';

// export function kcFactory(kcService: KeycloakService) {
//   return () => kcService.init();
// }

// @NgModule({
//   imports: [
//     BrowserModule,
//     AppComponent, // Import the standalone component here
//   ],
//   providers: [
//     {
//       provide: APP_INITIALIZER,
//       deps: [KeycloakService],
//       useFactory: kcFactory,
//       multi: true,
//     },
//   ],
//   bootstrap: [AppComponent],
// })
// export class AppModule {}
