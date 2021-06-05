import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICartOrderDetails } from '../cart-order-details.model';
import { CartOrderDetailsService } from '../service/cart-order-details.service';

@Component({
  templateUrl: './cart-order-details-delete-dialog.component.html',
})
export class CartOrderDetailsDeleteDialogComponent {
  cartOrderDetails?: ICartOrderDetails;

  constructor(protected cartOrderDetailsService: CartOrderDetailsService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cartOrderDetailsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
