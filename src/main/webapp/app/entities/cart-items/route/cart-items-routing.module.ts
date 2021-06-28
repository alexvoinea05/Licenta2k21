import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CartItemsComponent } from '../list/cart-items.component';
import { CartItemsDetailComponent } from '../detail/cart-items-detail.component';
import { CartItemsUpdateComponent } from '../update/cart-items-update.component';
import { CartItemsRoutingResolveService } from './cart-items-routing-resolve.service';

const cartItemsRoute: Routes = [
  {
    path: '',
    component: CartItemsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCartItems/view',
    component: CartItemsDetailComponent,
    resolve: {
      cartItems: CartItemsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CartItemsUpdateComponent,
    resolve: {
      cartItems: CartItemsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCartItems/edit',
    component: CartItemsUpdateComponent,
    resolve: {
      cartItems: CartItemsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cartItemsRoute)],
  exports: [RouterModule],
})
export class CartItemsRoutingModule {}
