import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { CartOrderDetailsComponent } from './list/cart-order-details.component';
import { CartOrderDetailsDetailComponent } from './detail/cart-order-details-detail.component';
import { CartOrderDetailsUpdateComponent } from './update/cart-order-details-update.component';
import { CartOrderDetailsDeleteDialogComponent } from './delete/cart-order-details-delete-dialog.component';
import { CartOrderDetailsRoutingModule } from './route/cart-order-details-routing.module';

@NgModule({
  imports: [SharedModule, CartOrderDetailsRoutingModule],
  declarations: [
    CartOrderDetailsComponent,
    CartOrderDetailsDetailComponent,
    CartOrderDetailsUpdateComponent,
    CartOrderDetailsDeleteDialogComponent,
  ],
  entryComponents: [CartOrderDetailsDeleteDialogComponent],
})
export class CartOrderDetailsModule {}
