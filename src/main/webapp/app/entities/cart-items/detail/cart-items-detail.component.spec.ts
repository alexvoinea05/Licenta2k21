import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CartItemsDetailComponent } from './cart-items-detail.component';

describe('Component Tests', () => {
  describe('CartItems Management Detail Component', () => {
    let comp: CartItemsDetailComponent;
    let fixture: ComponentFixture<CartItemsDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CartItemsDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ cartItems: { idCartItems: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CartItemsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CartItemsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load cartItems on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.cartItems).toEqual(jasmine.objectContaining({ idCartItems: 123 }));
      });
    });
  });
});
