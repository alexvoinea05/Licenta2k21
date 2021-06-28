import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICartOrderDetails, CartOrderDetails } from '../cart-order-details.model';
import { CartOrderDetailsService } from '../service/cart-order-details.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-cart-order-details-update',
  templateUrl: './cart-order-details-update.component.html',
})
export class CartOrderDetailsUpdateComponent implements OnInit {
  isSaving = false;

  appUsersSharedCollection: IAppUser[] = [];

  editForm = this.fb.group({
    idCartOrderDetails: [],
    totalPrice: [null, [Validators.min(0.1)]],
    createdAt: [],
    modifiedAt: [],
    statusCommand: [],
    idAppUser: [],
  });

  constructor(
    protected cartOrderDetailsService: CartOrderDetailsService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cartOrderDetails }) => {
      if (cartOrderDetails.idCartOrderDetails === undefined) {
        const today = dayjs().startOf('day');
        cartOrderDetails.createdAt = today;
        cartOrderDetails.modifiedAt = today;
      }

      this.updateForm(cartOrderDetails);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cartOrderDetails = this.createFromForm();
    if (cartOrderDetails.idCartOrderDetails !== undefined) {
      this.subscribeToSaveResponse(this.cartOrderDetailsService.update(cartOrderDetails));
    } else {
      this.subscribeToSaveResponse(this.cartOrderDetailsService.create(cartOrderDetails));
    }
  }

  trackAppUserByIdAppUser(index: number, item: IAppUser): number {
    return item.idAppUser!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICartOrderDetails>>): void {
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

  protected updateForm(cartOrderDetails: ICartOrderDetails): void {
    this.editForm.patchValue({
      idCartOrderDetails: cartOrderDetails.idCartOrderDetails,
      totalPrice: cartOrderDetails.totalPrice,
      createdAt: cartOrderDetails.createdAt ? cartOrderDetails.createdAt.format(DATE_TIME_FORMAT) : null,
      modifiedAt: cartOrderDetails.modifiedAt ? cartOrderDetails.modifiedAt.format(DATE_TIME_FORMAT) : null,
      statusCommand: cartOrderDetails.statusCommand,
      idAppUser: cartOrderDetails.idAppUser,
    });

    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing(
      this.appUsersSharedCollection,
      cartOrderDetails.idAppUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing(appUsers, this.editForm.get('idAppUser')!.value))
      )
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }

  protected createFromForm(): ICartOrderDetails {
    return {
      ...new CartOrderDetails(),
      idCartOrderDetails: this.editForm.get(['idCartOrderDetails'])!.value,
      totalPrice: this.editForm.get(['totalPrice'])!.value,
      createdAt: this.editForm.get(['createdAt'])!.value ? dayjs(this.editForm.get(['createdAt'])!.value, DATE_TIME_FORMAT) : undefined,
      modifiedAt: this.editForm.get(['modifiedAt'])!.value ? dayjs(this.editForm.get(['modifiedAt'])!.value, DATE_TIME_FORMAT) : undefined,
      statusCommand: this.editForm.get(['statusCommand'])!.value,
      idAppUser: this.editForm.get(['idAppUser'])!.value,
    };
  }
}
