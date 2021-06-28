import { Route, RouterModule, Routes } from '@angular/router';
import { BestProductsComponent } from '../entities/product/best-products/best-products.component';

import { HomeComponent } from './home.component';
import { UserRouteAccessService } from '../core/auth/user-route-access.service';
import { NgModule } from '@angular/core';

export const HOME_ROUTE: Route = {
  path: '',
  component: HomeComponent,
  data: {
    pageTitle: 'Welcome, Java Hipster!',
  },
};

export const bestProducts: Routes = [
  {
    path: 'best',
    component: BestProductsComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bestProducts)],
  exports: [RouterModule],
})
export class BestProductsRoutingModule {}
