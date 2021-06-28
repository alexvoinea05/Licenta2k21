import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICartItems, CartItems } from '../cart-items.model';
import { CartItemsService } from '../service/cart-items.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ICartOrderDetails } from 'app/entities/cart-order-details/cart-order-details.model';
import { CartOrderDetailsService } from 'app/entities/cart-order-details/service/cart-order-details.service';

@Component({
  selector: 'jhi-cart-items-update',
  templateUrl: './cart-items-update.component.html',
})
export class CartItemsUpdateComponent implements OnInit {
  isSaving = false;

  productsSharedCollection: IProduct[] = [];
  cartOrderDetailsSharedCollection: ICartOrderDetails[] = [];

  editForm = this.fb.group({
    idCartItems: [],
    quantity: [null, [Validators.required, Validators.min(0.1)]],
    createdAt: [],
    modifiedAt: [],
    idProduct: [],
    idOrderDetails: [],
  });

  constructor(
    protected cartItemsService: CartItemsService,
    protected productService: ProductService,
    protected cartOrderDetailsService: CartOrderDetailsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cartItems }) => {
      if (cartItems.idCartItems === undefined) {
        const today = dayjs().startOf('day');
        cartItems.createdAt = today;
        cartItems.modifiedAt = today;
      }

      this.updateForm(cartItems);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cartItems = this.createFromForm();
    if (cartItems.idCartItems !== undefined) {
      this.subscribeToSaveResponse(this.cartItemsService.update(cartItems));
    } else {
      this.subscribeToSaveResponse(this.cartItemsService.create(cartItems));
    }
  }

  trackProductByIdProduct(index: number, item: IProduct): number {
    return item.idProduct!;
  }

  trackCartOrderDetailsByIdCartOrderDetails(index: number, item: ICartOrderDetails): number {
    return item.idCartOrderDetails!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICartItems>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(cartItems: ICartItems): void {
    this.editForm.patchValue({
      idCartItems: cartItems.idCartItems,
      quantity: cartItems.quantity,
      createdAt: cartItems.createdAt ? cartItems.createdAt.format(DATE_TIME_FORMAT) : null,
      modifiedAt: cartItems.modifiedAt ? cartItems.modifiedAt.format(DATE_TIME_FORMAT) : null,
      idProduct: cartItems.idProduct,
      idOrderDetails: cartItems.idOrderDetails,
    });

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(this.productsSharedCollection, cartItems.idProduct);
    this.cartOrderDetailsSharedCollection = this.cartOrderDetailsService.addCartOrderDetailsToCollectionIfMissing(
      this.cartOrderDetailsSharedCollection,
      cartItems.idOrderDetails
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('idProduct')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.cartOrderDetailsService
      .query()
      .pipe(map((res: HttpResponse<ICartOrderDetails[]>) => res.body ?? []))
      .pipe(
        map((cartOrderDetails: ICartOrderDetails[]) =>
          this.cartOrderDetailsService.addCartOrderDetailsToCollectionIfMissing(
            cartOrderDetails,
            this.editForm.get('idOrderDetails')!.value
          )
        )
      )
      .subscribe((cartOrderDetails: ICartOrderDetails[]) => (this.cartOrderDetailsSharedCollection = cartOrderDetails));
  }

  protected createFromForm(): ICartItems {
    return {
      ...new CartItems(),
      idCartItems: this.editForm.get(['idCartItems'])!.value,
      quantity: this.editForm.get(['quantity'])!.value,
      createdAt: this.editForm.get(['createdAt'])!.value ? dayjs(this.editForm.get(['createdAt'])!.value, DATE_TIME_FORMAT) : undefined,
      modifiedAt: this.editForm.get(['modifiedAt'])!.value ? dayjs(this.editForm.get(['modifiedAt'])!.value, DATE_TIME_FORMAT) : undefined,
      idProduct: this.editForm.get(['idProduct'])!.value,
      idOrderDetails: this.editForm.get(['idOrderDetails'])!.value,
    };
  }
}
