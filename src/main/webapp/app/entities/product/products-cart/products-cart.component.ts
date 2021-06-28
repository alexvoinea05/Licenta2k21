import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProduct, IProductCart, Product, ProductCart } from '../product.model';
import { ProductService } from '../service/product.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-products-cart',
  templateUrl: './products-cart.component.html',
})
export class ProductsCartComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    idProduct: [],
    name: [],
    price: [],
    stock: [],
    quantity: [null, [Validators.required, Validators.min(0.1)]],
  });

  constructor(protected productService: ProductService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      if (product.idProduct === undefined) {
        const today = dayjs().startOf('day');
        product.createdAt = today;
        product.modifiedAt = today;
      }

      this.updateForm(product);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productCart = this.createFromForm();

    this.subscribeToSaveResponse(this.productService.addProductToCart(productCart));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductCart>>): void {
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

  protected updateForm(product: IProductCart): void {
    this.editForm.patchValue({
      idProduct: product.idProduct,
      name: product.name,
      price: product.price,
      stock: product.stock,
      quantity: product.quantity,
    });
  }

  protected createFromForm(): IProductCart {
    return {
      ...new ProductCart(),
      idProduct: this.editForm.get(['idProduct'])!.value,
      name: this.editForm.get(['name'])!.value,
      price: this.editForm.get(['price'])!.value,
      stock: this.editForm.get(['stock'])!.value,
      quantity: this.editForm.get(['quantity'])!.value,
    };
  }
}
