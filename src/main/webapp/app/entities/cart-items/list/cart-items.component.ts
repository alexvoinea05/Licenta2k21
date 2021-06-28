import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICartItems } from '../cart-items.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { CartItemsService } from '../service/cart-items.service';
import { CartItemsDeleteDialogComponent } from '../delete/cart-items-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-cart-items',
  templateUrl: './cart-items.component.html',
})
export class CartItemsComponent implements OnInit {
  cartItems: ICartItems[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected cartItemsService: CartItemsService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.cartItems = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'idCartItems';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.cartItemsService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<ICartItems[]>) => {
          this.isLoading = false;
          this.paginateCartItems(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.cartItems = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackIdCartItems(index: number, item: ICartItems): number {
    return item.idCartItems!;
  }

  delete(cartItems: ICartItems): void {
    const modalRef = this.modalService.open(CartItemsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cartItems = cartItems;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'idCartItems') {
      result.push('idCartItems');
    }
    return result;
  }

  protected paginateCartItems(data: ICartItems[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.cartItems.push(d);
      }
    }
  }
}
