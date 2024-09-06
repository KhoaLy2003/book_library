import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { AuthService } from '../authentication/auth.service';
import { NotificationService } from '../shared/notification.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    console.log('Intercepting request:', request.url);

    // const authToken = this.authService.getToken() || '';
    // console.log(authToken);
    // request = request.clone({
    //   setHeaders: {
    //     Authorization: 'Bearer ' + authToken,
    //   },
    // });
    return next.handle(request).pipe(
      catchError((error) => {
        console.log(error.error);

        if (error instanceof HttpErrorResponse) {
          console.log(error.status);
        }

        this.notificationService.showMessage('error', error.error.message);
        return throwError(() => new Error(error.error));
      })
    );
  }
}
