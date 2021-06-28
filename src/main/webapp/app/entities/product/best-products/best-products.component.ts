import { Component, Inject, Injectable, Input, OnInit } from '@angular/core';
import { HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProduct } from '../../product/product.model';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ProductDeleteDialogComponent } from '../../product/delete/product-delete-dialog.component';
import { ParseLinks } from '../../../core/util/parse-links.service';
import { CategoryService } from '../../category/service/category.service';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { ProductService } from '../service/product.service';

@Component({
  selector: 'jhi-best-products',
  templateUrl: './best-products.component.html',
})
export class BestProductsComponent implements OnInit {
  //products: IProduct[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;
  queryParams: HttpParams;

  @Input() products: IProduct[] = [];
  question: IProduct[] = [];

  constructor(
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    protected modalService: NgbModal,
    protected parseLinks: ParseLinks
  ) {
    //this.products = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'idProduct';
    this.ascending = true;
    this.queryParams = new HttpParams();
  }

  ngOnInit(): void {
    this.question = this.products;
    // eslint-disable-next-line no-console
    console.log(this.question[0].idProduct!.valueOf());
  }

  //   loadBestProducts(queryParams: HttpParams): void {

  //   this.productService.getBestProducts(queryParams)
  //   .subscribe(
  //     (res: HttpResponse<IProduct[]>) => {
  //       this.isLoading = false;
  //       this.paginateProducts(res.body, res.headers);
  //     },
  //     () => {
  //       this.isLoading = false;
  //     }
  //   );

  //   }

  //   reset(): void {
  //     this.page = 0;
  //     this.products = [];
  //     this.loadBestProducts(this.queryParams);
  //   }

  //   loadPage(page: number): void {
  //     this.page = page;
  //     this.loadBestProducts(this.queryParams);
  //   }

  //   ngOnInit(): void {
  //     this.queryParams = this.activatedRoute.snapshot.queryParams.data;
  //     this.loadBestProducts(this.queryParams);
  //   }

  //   trackIdProduct(index: number, item: IProduct): number {
  //     return item.idProduct!;
  //   }

  //   delete(product: IProduct): void {
  //     const modalRef = this.modalService.open(ProductDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
  //     modalRef.componentInstance.product = product;
  //     // unsubscribe not needed because closed completes on modal close
  //     modalRef.closed.subscribe(reason => {
  //       if (reason === 'deleted') {
  //         this.reset();
  //       }
  //     });
  //   }

  //   protected sort(): string[] {
  //     const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
  //     if (this.predicate !== 'idProduct') {
  //       result.push('idProduct');
  //     }
  //     return result;
  //   }

  //   protected paginateProducts(data: IProduct[] | null, headers: HttpHeaders): void {
  //     this.links = this.parseLinks.parse(headers.get('link') ?? '');
  //     if (data) {
  //       for (const d of data) {
  //         this.products.push(d);
  //       }
  //     }
  //   }
}
