import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAppUser, AppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';

@Injectable({ providedIn: 'root' })
export class AppUserRoutingResolveService implements Resolve<IAppUser> {
  constructor(protected service: AppUserService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAppUser> | Observable<never> {
    const id = route.params['idAppUser'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((appUser: HttpResponse<AppUser>) => {
          if (appUser.body) {
            return of(appUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AppUser());
  }
}
