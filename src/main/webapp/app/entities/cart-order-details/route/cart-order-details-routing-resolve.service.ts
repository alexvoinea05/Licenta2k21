import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICartOrderDetails, CartOrderDetails } from '../cart-order-details.model';
import { CartOrderDetailsService } from '../service/cart-order-details.service';

@Injectable({ providedIn: 'root' })
export class CartOrderDetailsRoutingResolveService implements Resolve<ICartOrderDetails> {
  constructor(protected service: CartOrderDetailsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICartOrderDetails> | Observable<never> {
    const id = route.params['idCartOrderDetails'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cartOrderDetails: HttpResponse<CartOrderDetails>) => {
          if (cartOrderDetails.body) {
            return of(cartOrderDetails.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CartOrderDetails());
  }
}
