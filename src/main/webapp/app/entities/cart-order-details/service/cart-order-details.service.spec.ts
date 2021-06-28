import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICartOrderDetails, CartOrderDetails } from '../cart-order-details.model';

import { CartOrderDetailsService } from './cart-order-details.service';

describe('Service Tests', () => {
  describe('CartOrderDetails Service', () => {
    let service: CartOrderDetailsService;
    let httpMock: HttpTestingController;
    let elemDefault: ICartOrderDetails;
    let expectedResult: ICartOrderDetails | ICartOrderDetails[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CartOrderDetailsService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        idCartOrderDetails: 0,
        totalPrice: 0,
        createdAt: currentDate,
        modifiedAt: currentDate,
        statusCommand: 'AAAAAAA',
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

      it('should create a CartOrderDetails', () => {
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

        service.create(new CartOrderDetails()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a CartOrderDetails', () => {
        const returnedFromService = Object.assign(
          {
            idCartOrderDetails: 1,
            totalPrice: 1,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
            statusCommand: 'BBBBBB',
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

      it('should partial update a CartOrderDetails', () => {
        const patchObject = Object.assign(
          {
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
          },
          new CartOrderDetails()
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

      it('should return a list of CartOrderDetails', () => {
        const returnedFromService = Object.assign(
          {
            idCartOrderDetails: 1,
            totalPrice: 1,
            createdAt: currentDate.format(DATE_TIME_FORMAT),
            modifiedAt: currentDate.format(DATE_TIME_FORMAT),
            statusCommand: 'BBBBBB',
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

      it('should delete a CartOrderDetails', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCartOrderDetailsToCollectionIfMissing', () => {
        it('should add a CartOrderDetails to an empty array', () => {
          const cartOrderDetails: ICartOrderDetails = { idCartOrderDetails: 123 };
          expectedResult = service.addCartOrderDetailsToCollectionIfMissing([], cartOrderDetails);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cartOrderDetails);
        });

        it('should not add a CartOrderDetails to an array that contains it', () => {
          const cartOrderDetails: ICartOrderDetails = { idCartOrderDetails: 123 };
          const cartOrderDetailsCollection: ICartOrderDetails[] = [
            {
              ...cartOrderDetails,
            },
            { idCartOrderDetails: 456 },
          ];
          expectedResult = service.addCartOrderDetailsToCollectionIfMissing(cartOrderDetailsCollection, cartOrderDetails);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a CartOrderDetails to an array that doesn't contain it", () => {
          const cartOrderDetails: ICartOrderDetails = { idCartOrderDetails: 123 };
          const cartOrderDetailsCollection: ICartOrderDetails[] = [{ idCartOrderDetails: 456 }];
          expectedResult = service.addCartOrderDetailsToCollectionIfMissing(cartOrderDetailsCollection, cartOrderDetails);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cartOrderDetails);
        });

        it('should add only unique CartOrderDetails to an array', () => {
          const cartOrderDetailsArray: ICartOrderDetails[] = [
            { idCartOrderDetails: 123 },
            { idCartOrderDetails: 456 },
            { idCartOrderDetails: 57724 },
          ];
          const cartOrderDetailsCollection: ICartOrderDetails[] = [{ idCartOrderDetails: 123 }];
          expectedResult = service.addCartOrderDetailsToCollectionIfMissing(cartOrderDetailsCollection, ...cartOrderDetailsArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const cartOrderDetails: ICartOrderDetails = { idCartOrderDetails: 123 };
          const cartOrderDetails2: ICartOrderDetails = { idCartOrderDetails: 456 };
          expectedResult = service.addCartOrderDetailsToCollectionIfMissing([], cartOrderDetails, cartOrderDetails2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cartOrderDetails);
          expect(expectedResult).toContain(cartOrderDetails2);
        });

        it('should accept null and undefined values', () => {
          const cartOrderDetails: ICartOrderDetails = { idCartOrderDetails: 123 };
          expectedResult = service.addCartOrderDetailsToCollectionIfMissing([], null, cartOrderDetails, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cartOrderDetails);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
