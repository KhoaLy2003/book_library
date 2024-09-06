// import { Injectable } from '@angular/core';
// import Keycloak from 'keycloak-js';

// export interface UserProfile {
//   username: string | undefined;
//   email: string | undefined;
//   firstName: string | undefined;
//   lastName: string | undefined;
//   token: string | undefined;
// }

// @Injectable({
//   providedIn: 'root',
// })
// export class KeycloakService {
//   private _keycloak: Keycloak | undefined;
//   private _profile: UserProfile | undefined;

//   get profile(): UserProfile | undefined {
//     return this._profile;
//   }

//   get keycloak() {
//     if (!this._keycloak) {
//       this._keycloak = new Keycloak({
//         url: 'http://localhost:8081',
//         realm: 'khoa-ly',
//         clientId: 'khoa-ly',
//       });
//     }
//     return this._keycloak;
//   }

//   constructor() {}

//   async init() {
//     console.log('KEYCLOAK');
//     const authenticated = await this.keycloak.init({
//       onLoad: 'login-required',
//     });

//     if (authenticated) {
//       console.log('HELLO THERE');
//       this._profile = (await this.keycloak.loadUserProfile()) as UserProfile;
//       this._profile.token = this.keycloak.token || '';
//     }

//     console.log(this._profile);
//   }
// }
