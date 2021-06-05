import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICartItems } from '../cart-items.model';
import { CartItemsService } from '../service/cart-items.service';

@Component({
  templateUrl: './cart-items-delete-dialog.component.html',
})
export class CartItemsDeleteDialogComponent {
  cartItems?: ICartItems;

  constructor(protected cartItemsService: CartItemsService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cartItemsService.deleteAndUpdatePrice(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
