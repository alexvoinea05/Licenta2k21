import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProduct } from '../product.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ProductService } from '../service/product.service';
import { ProductDeleteDialogComponent } from '../delete/product-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-product',
  templateUrl: './product.component.html',
})
export class ProductComponent implements OnInit {
  products: IProduct[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;
  idCategory: number;
  idGrower: number;

  constructor(protected productService: ProductService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.products = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'idProduct';
    this.ascending = true;
    this.idCategory = 1;
    this.idGrower = 0;
    this.getIdGrower();
  }

  loadAll(): void {
    this.isLoading = true;

    this.productService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IProduct[]>) => {
          this.isLoading = false;
          this.paginateProducts(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.products = [];
    this.loadAll();
  }

  getIdGrower(): any {
    this.productService.findIdGrower().subscribe((growerId: number) => {
      this.idGrower = growerId;
    });
  }

  checkIdGrower(idProductGrower: number): boolean {
    return this.idGrower === idProductGrower;
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.getIdGrower();
    this.loadAll();
  }

  trackIdProduct(index: number, item: IProduct): number {
    return item.idProduct!;
  }

  delete(product: IProduct): void {
    const modalRef = this.modalService.open(ProductDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.product = product;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'idProduct') {
      result.push('idProduct');
    }
    return result;
  }

  protected paginateProducts(data: IProduct[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.products.push(d);
      }
    }
  }
}
