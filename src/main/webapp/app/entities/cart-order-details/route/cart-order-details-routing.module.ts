import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CartOrderDetailsComponent } from '../list/cart-order-details.component';
import { CartOrderDetailsDetailComponent } from '../detail/cart-order-details-detail.component';
import { CartOrderDetailsUpdateComponent } from '../update/cart-order-details-update.component';
import { CartOrderDetailsRoutingResolveService } from './cart-order-details-routing-resolve.service';

const cartOrderDetailsRoute: Routes = [
  {
    path: '',
    component: CartOrderDetailsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCartOrderDetails/view',
    component: CartOrderDetailsDetailComponent,
    resolve: {
      cartOrderDetails: CartOrderDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CartOrderDetailsUpdateComponent,
    resolve: {
      cartOrderDetails: CartOrderDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCartOrderDetails/edit',
    component: CartOrderDetailsUpdateComponent,
    resolve: {
      cartOrderDetails: CartOrderDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cartOrderDetailsRoute)],
  exports: [RouterModule],
})
export class CartOrderDetailsRoutingModule {}
