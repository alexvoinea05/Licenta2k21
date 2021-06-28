import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CategoryComponent } from '../list/category.component';
import { CategoryDetailComponent } from '../detail/category-detail.component';
import { CategoryUpdateComponent } from '../update/category-update.component';
import { CategoryRoutingResolveService } from './category-routing-resolve.service';
import { ProductCategoryComponent } from '../product-category/product-category.component';

const categoryRoute: Routes = [
  {
    path: '',
    component: CategoryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCategory/view',
    component: CategoryDetailComponent,
    resolve: {
      category: CategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'products/:idCategory',
    component: ProductCategoryComponent,
    resolve: {
      category: CategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CategoryUpdateComponent,
    resolve: {
      category: CategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCategory/edit',
    component: CategoryUpdateComponent,
    resolve: {
      category: CategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(categoryRoute)],
  exports: [RouterModule],
})
export class CategoryRoutingModule {}
