import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICartItems, CartItems } from '../cart-items.model';

import { CartItemsService } from './cart-items.service';

describe('Service Tests', () => {
  describe('CartItems Service', () => {
    let service: CartItemsService;
    let httpMock: HttpTestingController;
    let elemDefault: ICartItems;
    let expectedResult: ICartItems | ICartItems[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CartItemsService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        idCartItems: 0,
        quantity: 0,
        createdAt: currentDate,
        modifiedAt: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a CartItems', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
            modifiedAt: currentDate,
          },
          returnedFromService
        );

        service.create(new CartItems()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a CartItems', () => {
        const returnedFromService = Object.assign(
          {
            idCartItems: 1,
            quantity: 1,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
            modifiedAt: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a CartItems', () => {
        const patchObject = Object.assign(
          {
            quantity: 1,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
          },
          new CartItems()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            createdAt: currentDate,
            modifiedAt: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of CartItems', () => {
        const returnedFromService = Object.assign(
          {
            idCartItems: 1,
            quantity: 1,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
            modifiedAt: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a CartItems', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCartItemsToCollectionIfMissing', () => {
        it('should add a CartItems to an empty array', () => {
          const cartItems: ICartItems = { idCartItems: 123 };
          expectedResult = service.addCartItemsToCollectionIfMissing([], cartItems);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cartItems);
        });

        it('should not add a CartItems to an array that contains it', () => {
          const cartItems: ICartItems = { idCartItems: 123 };
          const cartItemsCollection: ICartItems[] = [
            {
              ...cartItems,
            },
            { idCartItems: 456 },
          ];
          expectedResult = service.addCartItemsToCollectionIfMissing(cartItemsCollection, cartItems);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a CartItems to an array that doesn't contain it", () => {
          const cartItems: ICartItems = { idCartItems: 123 };
          const cartItemsCollection: ICartItems[] = [{ idCartItems: 456 }];
          expectedResult = service.addCartItemsToCollectionIfMissing(cartItemsCollection, cartItems);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cartItems);
        });

        it('should add only unique CartItems to an array', () => {
          const cartItemsArray: ICartItems[] = [{ idCartItems: 123 }, { idCartItems: 456 }, { idCartItems: 15672 }];
          const cartItemsCollection: ICartItems[] = [{ idCartItems: 123 }];
          expectedResult = service.addCartItemsToCollectionIfMissing(cartItemsCollection, ...cartItemsArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const cartItems: ICartItems = { idCartItems: 123 };
          const cartItems2: ICartItems = { idCartItems: 456 };
          expectedResult = service.addCartItemsToCollectionIfMissing([], cartItems, cartItems2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cartItems);
          expect(expectedResult).toContain(cartItems2);
        });

        it('should accept null and undefined values', () => {
          const cartItems: ICartItems = { idCartItems: 123 };
          expectedResult = service.addCartItemsToCollectionIfMissing([], null, cartItems, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cartItems);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
