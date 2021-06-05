import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICartOrderDetails } from '../cart-order-details.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { CartOrderDetailsService } from '../service/cart-order-details.service';
import { CartOrderDetailsDeleteDialogComponent } from '../delete/cart-order-details-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-cart-order-details',
  templateUrl: './cart-order-details.component.html',
})
export class CartOrderDetailsComponent implements OnInit {
  cartOrderDetails: ICartOrderDetails[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected cartOrderDetailsService: CartOrderDetailsService,
    protected modalService: NgbModal,
    protected parseLinks: ParseLinks
  ) {
    this.cartOrderDetails = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'idCartOrderDetails';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.cartOrderDetailsService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<ICartOrderDetails[]>) => {
          this.isLoading = false;
          this.paginateCartOrderDetails(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.cartOrderDetails = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackIdCartOrderDetails(index: number, item: ICartOrderDetails): number {
    return item.idCartOrderDetails!;
  }

  delete(cartOrderDetails: ICartOrderDetails): void {
    const modalRef = this.modalService.open(CartOrderDetailsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cartOrderDetails = cartOrderDetails;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'idCartOrderDetails') {
      result.push('idCartOrderDetails');
    }
    return result;
  }

  protected paginateCartOrderDetails(data: ICartOrderDetails[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.cartOrderDetails.push(d);
      }
    }
  }
}
