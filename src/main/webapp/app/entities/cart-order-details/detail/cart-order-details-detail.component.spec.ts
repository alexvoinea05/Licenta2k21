import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CartOrderDetailsDetailComponent } from './cart-order-details-detail.component';

describe('Component Tests', () => {
  describe('CartOrderDetails Management Detail Component', () => {
    let comp: CartOrderDetailsDetailComponent;
    let fixture: ComponentFixture<CartOrderDetailsDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CartOrderDetailsDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ cartOrderDetails: { idCartOrderDetails: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CartOrderDetailsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CartOrderDetailsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load cartOrderDetails on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.cartOrderDetails).toEqual(jasmine.objectContaining({ idCartOrderDetails: 123 }));
      });
    });
  });
});
