import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CartOrderDetailsService } from '../service/cart-order-details.service';

import { CartOrderDetailsComponent } from './cart-order-details.component';

describe('Component Tests', () => {
  describe('CartOrderDetails Management Component', () => {
    let comp: CartOrderDetailsComponent;
    let fixture: ComponentFixture<CartOrderDetailsComponent>;
    let service: CartOrderDetailsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CartOrderDetailsComponent],
      })
        .overrideTemplate(CartOrderDetailsComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CartOrderDetailsComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CartOrderDetailsService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ idCartOrderDetails: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.cartOrderDetails[0]).toEqual(jasmine.objectContaining({ idCartOrderDetails: 123 }));
    });

    it('should load a page', () => {
      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.cartOrderDetails[0]).toEqual(jasmine.objectContaining({ idCartOrderDetails: 123 }));
    });

    it('should calculate the sort attribute for an id', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalledWith(expect.objectContaining({ sort: ['idCartOrderDetails,asc'] }));
    });

    it('should calculate the sort attribute for a non-id attribute', () => {
      // INIT
      comp.ngOnInit();

      // GIVEN
      comp.predicate = 'name';

      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['name,asc', 'idCartOrderDetails'] }));
    });

    it('should re-initialize the page', () => {
      // WHEN
      comp.loadPage(1);
      comp.reset();

      // THEN
      expect(comp.page).toEqual(0);
      expect(service.query).toHaveBeenCalledTimes(2);
      expect(comp.cartOrderDetails[0]).toEqual(jasmine.objectContaining({ idCartOrderDetails: 123 }));
    });
  });
});
