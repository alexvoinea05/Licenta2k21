import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from '../../../config/input.constants';

import { IAppUser, AppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';
import { IUser } from '../../user/user.model';
import { UserService } from '../../user/user.service';

@Component({
  selector: 'jhi-app-user-update',
  templateUrl: './app-user-update.component.html',
})
export class AppUserUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    idAppUser: [],
    certificateUrl: [],
    adress: [null, [Validators.required]],
    createdAt: [],
    modifiedAt: [],
    user: [],
  });

  constructor(
    protected appUserService: AppUserService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appUser }) => {
      if (appUser.idAppUser === undefined) {
        const today = dayjs().startOf('day');
        appUser.createdAt = today;
        appUser.modifiedAt = today;
      }

      this.updateForm(appUser);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appUser = this.createFromForm();
    if (appUser.idAppUser !== undefined) {
      this.subscribeToSaveResponse(this.appUserService.update(appUser));
    } else {
      this.subscribeToSaveResponse(this.appUserService.create(appUser));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppUser>>): void {
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

  protected updateForm(appUser: IAppUser): void {
    this.editForm.patchValue({
      idAppUser: appUser.idAppUser,
      certificateUrl: appUser.certificateUrl,
      adress: appUser.adress,
      createdAt: appUser.createdAt ? appUser.createdAt.format(DATE_TIME_FORMAT) : null,
      modifiedAt: appUser.modifiedAt ? appUser.modifiedAt.format(DATE_TIME_FORMAT) : null,
      user: appUser.user,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, appUser.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IAppUser {
    return {
      ...new AppUser(),
      idAppUser: this.editForm.get(['idAppUser'])!.value,
      certificateUrl: this.editForm.get(['certificateUrl'])!.value,
      adress: this.editForm.get(['adress'])!.value,
      createdAt: this.editForm.get(['createdAt'])!.value ? dayjs(this.editForm.get(['createdAt'])!.value, DATE_TIME_FORMAT) : undefined,
      modifiedAt: this.editForm.get(['modifiedAt'])!.value ? dayjs(this.editForm.get(['modifiedAt'])!.value, DATE_TIME_FORMAT) : undefined,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
