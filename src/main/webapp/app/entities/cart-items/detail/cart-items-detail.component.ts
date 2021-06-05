import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICartItems } from '../cart-items.model';

@Component({
  selector: 'jhi-cart-items-detail',
  templateUrl: './cart-items-detail.component.html',
})
export class CartItemsDetailComponent implements OnInit {
  cartItems: ICartItems | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cartItems }) => {
      this.cartItems = cartItems;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
