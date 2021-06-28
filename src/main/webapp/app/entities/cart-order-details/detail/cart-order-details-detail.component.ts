import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICartOrderDetails } from '../cart-order-details.model';

@Component({
  selector: 'jhi-cart-order-details-detail',
  templateUrl: './cart-order-details-detail.component.html',
})
export class CartOrderDetailsDetailComponent implements OnInit {
  cartOrderDetails: ICartOrderDetails | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cartOrderDetails }) => {
      this.cartOrderDetails = cartOrderDetails;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
