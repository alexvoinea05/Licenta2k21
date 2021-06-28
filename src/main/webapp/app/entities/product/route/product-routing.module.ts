import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductComponent } from '../list/product.component';
import { ProductDetailComponent } from '../detail/product-detail.component';
import { ProductUpdateComponent } from '../update/product-update.component';
import { ProductRoutingResolveService } from './product-routing-resolve.service';
import { ProductCategoryComponent } from '../../category/product-category/product-category.component';
import { ProductsCartComponent } from '../products-cart/products-cart.component';

const productRoute: Routes = [
  {
    path: '',
    component: ProductComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idProduct/view',
    component: ProductDetailComponent,
    resolve: {
      product: ProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductUpdateComponent,
    resolve: {
      product: ProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idProduct/edit',
    component: ProductUpdateComponent,
    resolve: {
      product: ProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idProduct/add-product-to-cart',
    component: ProductsCartComponent,
    resolve: {
      product: ProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  // {
  //   path: 'best/products/:queryParams',
  //   component: BestProductsComponent,
  //   resolve: {
  //     product: ProductRoutingResolveService,
  //   },
  //   canActivate: [UserRouteAccessService],
  // },
];

@NgModule({
  imports: [RouterModule.forChild(productRoute)],
  exports: [RouterModule],
})
export class ProductRoutingModule {}
