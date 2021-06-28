import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'category',
        data: { pageTitle: 'Categorii' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'Products' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'cart-items',
        data: { pageTitle: 'CartItems' },
        loadChildren: () => import('./cart-items/cart-items.module').then(m => m.CartItemsModule),
      },
      {
        path: 'cart-order-details',
        data: { pageTitle: 'CartOrderDetails' },
        loadChildren: () => import('./cart-order-details/cart-order-details.module').then(m => m.CartOrderDetailsModule),
      },
      {
        path: 'app-user',
        data: { pageTitle: 'AppUsers' },
        loadChildren: () => import('./app-user/app-user.module').then(m => m.AppUserModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
