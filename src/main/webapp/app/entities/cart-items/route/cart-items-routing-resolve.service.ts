import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICartItems, CartItems } from '../cart-items.model';
import { CartItemsService } from '../service/cart-items.service';

@Injectable({ providedIn: 'root' })
export class CartItemsRoutingResolveService implements Resolve<ICartItems> {
  constructor(protected service: CartItemsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICartItems> | Observable<never> {
    const id = route.params['idCartItems'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cartItems: HttpResponse<CartItems>) => {
          if (cartItems.body) {
            return of(cartItems.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CartItems());
  }
}
