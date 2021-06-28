import { Component, Inject, Injectable, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProduct } from '../../product/product.model';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ProductDeleteDialogComponent } from '../../product/delete/product-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { CategoryService } from '../../category/service/category.service';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../product/service/product.service';

@Component({
  selector: 'jhi-product-category',
  templateUrl: './product-category.component.html',
})
export class ProductCategoryComponent implements OnInit {
  products: IProduct[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;
  idGrower: number;

  constructor(
    protected categoryService: CategoryService,
    protected activatedRoute: ActivatedRoute,
    protected modalService: NgbModal,
    protected parseLinks: ParseLinks,
    protected productService: ProductService
  ) {
    this.products = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'idProduct';
    this.ascending = true;
    this.idGrower = 0;
    this.getIdGrower();
  }

  loadAllByCategory(): void {
    this.isLoading = true;
    this.activatedRoute.data.subscribe(({ category }) => {
      this.categoryService
        .queryByCategory(category, {
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
    });
  }

  getIdGrower(): any {
    this.productService.findIdGrower().subscribe((growerId: number) => {
      this.idGrower = growerId;
    });
  }

  checkIdGrower(idProductGrower: number): boolean {
    return this.idGrower === idProductGrower;
  }

  reset(): void {
    this.page = 0;
    this.products = [];
    this.loadAllByCategory();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAllByCategory();
  }

  ngOnInit(): void {
    this.getIdGrower();
    this.loadAllByCategory();
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
