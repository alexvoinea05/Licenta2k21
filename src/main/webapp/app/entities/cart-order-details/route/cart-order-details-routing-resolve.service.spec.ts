jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICartOrderDetails, CartOrderDetails } from '../cart-order-details.model';
import { CartOrderDetailsService } from '../service/cart-order-details.service';

import { CartOrderDetailsRoutingResolveService } from './cart-order-details-routing-resolve.service';

describe('Service Tests', () => {
  describe('CartOrderDetails routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CartOrderDetailsRoutingResolveService;
    let service: CartOrderDetailsService;
    let resultCartOrderDetails: ICartOrderDetails | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CartOrderDetailsRoutingResolveService);
      service = TestBed.inject(CartOrderDetailsService);
      resultCartOrderDetails = undefined;
    });

    describe('resolve', () => {
      it('should return ICartOrderDetails returned by find', () => {
        // GIVEN
        service.find = jest.fn(idCartOrderDetails => of(new HttpResponse({ body: { idCartOrderDetails } })));
        mockActivatedRouteSnapshot.params = { idCartOrderDetails: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCartOrderDetails = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCartOrderDetails).toEqual({ idCartOrderDetails: 123 });
      });

      it('should return new ICartOrderDetails if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCartOrderDetails = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCartOrderDetails).toEqual(new CartOrderDetails());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { idCartOrderDetails: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCartOrderDetails = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCartOrderDetails).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
