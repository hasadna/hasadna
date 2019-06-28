import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { CanActivate } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private angularFireAuth: AngularFireAuth) { }

  canActivate(): Observable<boolean> {
    return this.angularFireAuth.authState.pipe(
      map(userData => {
        const isAuthorized: boolean = !!userData;
        return isAuthorized;
      }),
    );
  }
}
