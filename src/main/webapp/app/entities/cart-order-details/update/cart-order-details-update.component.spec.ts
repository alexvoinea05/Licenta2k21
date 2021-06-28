jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CartOrderDetailsService } from '../service/cart-order-details.service';
import { ICartOrderDetails, CartOrderDetails } from '../cart-order-details.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { CartOrderDetailsUpdateComponent } from './cart-order-details-update.component';

describe('Component Tests', () => {
  describe('CartOrderDetails Management Update Component', () => {
    let comp: CartOrderDetailsUpdateComponent;
    let fixture: ComponentFixture<CartOrderDetailsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let cartOrderDetailsService: CartOrderDetailsService;
    let appUserService: AppUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CartOrderDetailsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CartOrderDetailsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CartOrderDetailsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      cartOrderDetailsService = TestBed.inject(CartOrderDetailsService);
      appUserService = TestBed.inject(AppUserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call AppUser query and add missing value', () => {
        const cartOrderDetails: ICartOrderDetails = { idCartOrderDetails: 456 };
        const idAppUser: IAppUser = { idAppUser: 23708 };
        cartOrderDetails.idAppUser = idAppUser;

        const appUserCollection: IAppUser[] = [{ idAppUser: 95384 }];
        spyOn(appUserService, 'query').and.returnValue(of(new HttpResponse({ body: appUserCollection })));
        const additionalAppUsers = [idAppUser];
        const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
        spyOn(appUserService, 'addAppUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ cartOrderDetails });
        comp.ngOnInit();

        expect(appUserService.query).toHaveBeenCalled();
        expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(appUserCollection, ...additionalAppUsers);
        expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const cartOrderDetails: ICartOrderDetails = { idCartOrderDetails: 456 };
        const idAppUser: IAppUser = { idAppUser: 23281 };
        cartOrderDetails.idAppUser = idAppUser;

        activatedRoute.data = of({ cartOrderDetails });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(cartOrderDetails));
        expect(comp.appUsersSharedCollection).toContain(idAppUser);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const cartOrderDetails = { idCartOrderDetails: 123 };
        spyOn(cartOrderDetailsService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartOrderDetails });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cartOrderDetails }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(cartOrderDetailsService.update).toHaveBeenCalledWith(cartOrderDetails);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const cartOrderDetails = new CartOrderDetails();
        spyOn(cartOrderDetailsService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartOrderDetails });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cartOrderDetails }));
        saveSubject.complete();

        // THEN
        expect(cartOrderDetailsService.create).toHaveBeenCalledWith(cartOrderDetails);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const cartOrderDetails = { idCartOrderDetails: 123 };
        spyOn(cartOrderDetailsService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartOrderDetails });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(cartOrderDetailsService.update).toHaveBeenCalledWith(cartOrderDetails);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackAppUserByIdAppUser', () => {
        it('Should return tracked AppUser primary key', () => {
          const entity = { idAppUser: 123 };
          const trackResult = comp.trackAppUserByIdAppUser(0, entity);
          expect(trackResult).toEqual(entity.idAppUser);
        });
      });
    });
  });
});
