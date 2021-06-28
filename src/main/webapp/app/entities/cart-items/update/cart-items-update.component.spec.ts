jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CartItemsService } from '../service/cart-items.service';
import { ICartItems, CartItems } from '../cart-items.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ICartOrderDetails } from 'app/entities/cart-order-details/cart-order-details.model';
import { CartOrderDetailsService } from 'app/entities/cart-order-details/service/cart-order-details.service';

import { CartItemsUpdateComponent } from './cart-items-update.component';

describe('Component Tests', () => {
  describe('CartItems Management Update Component', () => {
    let comp: CartItemsUpdateComponent;
    let fixture: ComponentFixture<CartItemsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let cartItemsService: CartItemsService;
    let productService: ProductService;
    let cartOrderDetailsService: CartOrderDetailsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CartItemsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CartItemsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CartItemsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      cartItemsService = TestBed.inject(CartItemsService);
      productService = TestBed.inject(ProductService);
      cartOrderDetailsService = TestBed.inject(CartOrderDetailsService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Product query and add missing value', () => {
        const cartItems: ICartItems = { idCartItems: 456 };
        const idProduct: IProduct = { idProduct: 82284 };
        cartItems.idProduct = idProduct;

        const productCollection: IProduct[] = [{ idProduct: 74211 }];
        spyOn(productService, 'query').and.returnValue(of(new HttpResponse({ body: productCollection })));
        const additionalProducts = [idProduct];
        const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
        spyOn(productService, 'addProductToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ cartItems });
        comp.ngOnInit();

        expect(productService.query).toHaveBeenCalled();
        expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
        expect(comp.productsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call CartOrderDetails query and add missing value', () => {
        const cartItems: ICartItems = { idCartItems: 456 };
        const idOrderDetails: ICartOrderDetails = { idCartOrderDetails: 33889 };
        cartItems.idOrderDetails = idOrderDetails;

        const cartOrderDetailsCollection: ICartOrderDetails[] = [{ idCartOrderDetails: 62372 }];
        spyOn(cartOrderDetailsService, 'query').and.returnValue(of(new HttpResponse({ body: cartOrderDetailsCollection })));
        const additionalCartOrderDetails = [idOrderDetails];
        const expectedCollection: ICartOrderDetails[] = [...additionalCartOrderDetails, ...cartOrderDetailsCollection];
        spyOn(cartOrderDetailsService, 'addCartOrderDetailsToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ cartItems });
        comp.ngOnInit();

        expect(cartOrderDetailsService.query).toHaveBeenCalled();
        expect(cartOrderDetailsService.addCartOrderDetailsToCollectionIfMissing).toHaveBeenCalledWith(
          cartOrderDetailsCollection,
          ...additionalCartOrderDetails
        );
        expect(comp.cartOrderDetailsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const cartItems: ICartItems = { idCartItems: 456 };
        const idProduct: IProduct = { idProduct: 57374 };
        cartItems.idProduct = idProduct;
        const idOrderDetails: ICartOrderDetails = { idCartOrderDetails: 40298 };
        cartItems.idOrderDetails = idOrderDetails;

        activatedRoute.data = of({ cartItems });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(cartItems));
        expect(comp.productsSharedCollection).toContain(idProduct);
        expect(comp.cartOrderDetailsSharedCollection).toContain(idOrderDetails);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const cartItems = { idCartItems: 123 };
        spyOn(cartItemsService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartItems });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cartItems }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(cartItemsService.update).toHaveBeenCalledWith(cartItems);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const cartItems = new CartItems();
        spyOn(cartItemsService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartItems });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cartItems }));
        saveSubject.complete();

        // THEN
        expect(cartItemsService.create).toHaveBeenCalledWith(cartItems);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const cartItems = { idCartItems: 123 };
        spyOn(cartItemsService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartItems });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(cartItemsService.update).toHaveBeenCalledWith(cartItems);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackProductByIdProduct', () => {
        it('Should return tracked Product primary key', () => {
          const entity = { idProduct: 123 };
          const trackResult = comp.trackProductByIdProduct(0, entity);
          expect(trackResult).toEqual(entity.idProduct);
        });
      });

      describe('trackCartOrderDetailsByIdCartOrderDetails', () => {
        it('Should return tracked CartOrderDetails primary key', () => {
          const entity = { idCartOrderDetails: 123 };
          const trackResult = comp.trackCartOrderDetailsByIdCartOrderDetails(0, entity);
          expect(trackResult).toEqual(entity.idCartOrderDetails);
        });
      });
    });
  });
});
