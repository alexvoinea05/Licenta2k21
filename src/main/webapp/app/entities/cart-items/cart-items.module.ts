import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { CartItemsComponent } from './list/cart-items.component';
import { CartItemsDetailComponent } from './detail/cart-items-detail.component';
import { CartItemsUpdateComponent } from './update/cart-items-update.component';
import { CartItemsDeleteDialogComponent } from './delete/cart-items-delete-dialog.component';
import { CartItemsRoutingModule } from './route/cart-items-routing.module';

@NgModule({
  imports: [SharedModule, CartItemsRoutingModule],
  declarations: [CartItemsComponent, CartItemsDetailComponent, CartItemsUpdateComponent, CartItemsDeleteDialogComponent],
  entryComponents: [CartItemsDeleteDialogComponent],
})
export class CartItemsModule {}
