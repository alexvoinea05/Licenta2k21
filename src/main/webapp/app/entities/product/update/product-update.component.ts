import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProduct, Product } from '../product.model';
import { ProductService } from '../service/product.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { AppUser, IAppUser } from '../../app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.component.html',
})
export class ProductUpdateComponent implements OnInit {
  isSaving = false;

  categoriesSharedCollection: ICategory[] = [];
  appUsersSharedCollection: IAppUser[] = [];
  currentGrower: number;
  appUser: IAppUser = {} as IAppUser;

  editForm = this.fb.group({
    idProduct: [],
    name: [null, [Validators.required, Validators.minLength(1)]],
    price: [null, [Validators.required, Validators.min(0.1)]],
    stock: [null, [Validators.required, Validators.min(0.1)]],
    imageUrl: [],
    productUrl: [],
    createdAt: [],
    modifiedAt: [],
    idCategory: [],
    idGrower: [],
  });

  constructor(
    protected productService: ProductService,
    protected categoryService: CategoryService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {
    this.currentGrower = 0;
    this.getIdGrower();
    //this.appUser = null;
    this.getCurrentAppUser();
  }

  ngOnInit(): void {
    this.getIdGrower();
    this.getCurrentAppUser();

    this.activatedRoute.data.subscribe(({ product }) => {
      if (product.idProduct === undefined) {
        const today = dayjs().startOf('day');
        product.createdAt = today;
        product.modifiedAt = today;
      }

      this.updateForm(product);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  getIdGrower(): any {
    this.productService.findIdGrower().subscribe((growerId: number) => {
      this.currentGrower = growerId;
    });
  }

  save(): void {
    this.isSaving = true;
    const product = this.createFromForm();
    if (product.idProduct !== undefined) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  trackCategoryByIdCategory(index: number, item: ICategory): number {
    return item.idCategory!;
  }

  trackAppUserByIdAppUser(index: number, item: IAppUser): number {
    return item.idAppUser!;
  }

  getCurrentAppUser(): any {
    this.productService.findAppUser().subscribe((appUser: IAppUser) => {
      this.appUser = appUser;
    });
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduct>>): void {
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

  protected updateForm(product: IProduct): void {
    this.editForm.patchValue({
      idProduct: product.idProduct,
      name: product.name,
      price: product.price,
      stock: product.stock,
      imageUrl: product.imageUrl,
      productUrl: product.productUrl,
      createdAt: product.createdAt ? product.createdAt.format(DATE_TIME_FORMAT) : null,
      modifiedAt: product.modifiedAt ? product.modifiedAt.format(DATE_TIME_FORMAT) : null,
      idCategory: product.idCategory,
      idGrower: this.appUser,
    });

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing(
      this.categoriesSharedCollection,
      product.idCategory
    );
    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing(this.appUsersSharedCollection, this.appUser);
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing(categories, this.editForm.get('idCategory')!.value)
        )
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));

    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing(appUsers, this.appUser)))
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }

  protected createFromForm(): IProduct {
    return {
      ...new Product(),
      idProduct: this.editForm.get(['idProduct'])!.value,
      name: this.editForm.get(['name'])!.value,
      price: this.editForm.get(['price'])!.value,
      stock: this.editForm.get(['stock'])!.value,
      imageUrl: this.editForm.get(['imageUrl'])!.value,
      productUrl: this.editForm.get(['productUrl'])!.value,
      createdAt: this.editForm.get(['createdAt'])!.value ? dayjs(this.editForm.get(['createdAt'])!.value, DATE_TIME_FORMAT) : undefined,
      modifiedAt: this.editForm.get(['modifiedAt'])!.value ? dayjs(this.editForm.get(['modifiedAt'])!.value, DATE_TIME_FORMAT) : undefined,
      idCategory: this.editForm.get(['idCategory'])!.value,
      idGrower: this.appUser,
    };
  }
}
