import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { bestProducts, BestProductsRoutingModule, HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { BestProductsComponent } from '../entities/product/best-products/best-products.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE]), RouterModule.forChild(bestProducts)],
  declarations: [HomeComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class HomeModule {}
