jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { CartItemsService } from '../service/cart-items.service';

import { CartItemsDeleteDialogComponent } from './cart-items-delete-dialog.component';

describe('Component Tests', () => {
  describe('CartItems Management Delete Component', () => {
    let comp: CartItemsDeleteDialogComponent;
    let fixture: ComponentFixture<CartItemsDeleteDialogComponent>;
    let service: CartItemsService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CartItemsDeleteDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(CartItemsDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CartItemsDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CartItemsService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.close).not.toHaveBeenCalled();
        expect(mockActiveModal.dismiss).toHaveBeenCalled();
      });
    });
  });
});
