import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { AccountService } from '../core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { ProductSearch } from './productSearch.model';
import { HomeService } from './home.service';
import { IProduct } from '../entities/product/product.model';
import { HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { ProductService } from '../entities/product/service/product.service';
import { threadId } from 'node:worker_threads';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;
  productSearch?: ProductSearch[];
  links: { [key: string]: number };
  isLoading = false;
  products1: IProduct[] = [];
  display = false;
  products: IProduct[] = [];

  formSearch: FormGroup;

  constructor(
    private accountService: AccountService,
    private router: Router,
    private fb: FormBuilder,
    private homeService: HomeService,
    protected parseLinks: ParseLinks,
    private productService: ProductService
  ) {
    //this.products1 = [];
    this.formSearch = this.fb.group({
      productCantitate: this.fb.array([]),
    });
    this.links = {
      last: 0,
    };
  }

  get productCantitate(): FormArray {
    return this.formSearch.get('productCantitate') as FormArray;
  }

  newProductCantitate(): FormGroup {
    return this.fb.group({
      numeProdus: '',
      cantitate: [
        '',
        [Validators.required, Validators.minLength(1), Validators.maxLength(10), Validators.pattern('^-?[0-9]\\d*(\\.\\d{1,2})?$')],
      ],
    });
  }

  addProductCantitate(): any {
    this.productCantitate.push(this.newProductCantitate());
  }

  stergeProdusCantitate(): any {
    this.productCantitate.removeAt(this.productCantitate.length - 1);
  }

  onSubmit(): any {
    this.productSearch = [];
    this.products1 = [];
    this.display = true;
    // eslint-disable-next-line no-console
    console.log(this.formSearch.value);
    // eslint-disable-next-line no-console
    console.log(this.productCantitate.value);
    //let myParams = new HttpParams();
    for (let i = 0; i < this.productCantitate.length; i++) {
      // myParams.set('numeProdus',this.productCantitate.at(i).get("numeProdus")?.value);
      // myParams.set('cantitate',this.productCantitate.at(i).get("cantitate")?.value);
      this.productSearch.push(
        new ProductSearch(this.productCantitate.at(i).get('numeProdus')?.value, this.productCantitate.at(i).get('cantitate')?.value)
      );
    }

    //this.router.navigate(['/product/best/products', myParams]);

    // eslint-disable-next-line no-console
    console.log(this.productSearch.toString());
    // eslint-disable-next-line no-console
    console.log(JSON.stringify(this.productSearch));
    // eslint-disable-next-line no-console
    console.log(this.homeService.getBestProducts(this.productSearch));

    this.homeService.getBestProducts(this.productSearch).subscribe(
      (res: HttpResponse<IProduct[]>) => {
        this.isLoading = false;
        this.paginateProducts(res.body, res.headers);
        if (this.products1.length.valueOf() > 0) {
          this.products = this.products1;
          localStorage.setItem('products', JSON.stringify(this.products));
          // this.router.navigate(['/best']);
        }
      },
      () => {
        this.isLoading = false;
      }
    );

    // this.homeService.getBestProducts(this.productSearch)
    // .subscribe(
    //   (res: HttpResponse<IProduct[]>) => {
    //     this.isLoading = false;
    //     this.paginateProducts(res.body, res.headers);
    //     this.products = this.paginateProducts(res.body, res.headers);
    //     // eslint-disable-next-line no-console
    // console.log(this.products[0].idProduct!.valueOf());
    // this.router.navigate(['/best']);
    //   },
    //   () => {
    //     this.isLoading = false;
    //   }
    // );

    // const response = this.homeService.getBestProducts(this.productSearch).toPromise();
    // response.then(() => {
    //   this.products = this.products1;
    //   // eslint-disable-next-line no-console
    //   console.log(this.products[0].idProduct!.valueOf());
    //   this.router.navigate(['/best']);});

    //this.currentProducts = response[0];
    //this.currentProducts = this.products;

    //this.router.navigate(['/best']);
  }

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));

    // eslint-disable-next-line no-console
    console.log(this.display);

    //if(this.display){
    if (this.products.length > 0) {
      this.products1 = this.products;
    }

    if (localStorage.getItem('products')) {
      const retrievedObj = localStorage.getItem('products')!;

      this.products1 = JSON.parse(retrievedObj);
      if (this.isAuth()) {
        this.display = true;
      }
      // eslint-disable-next-line no-console
      console.log('objjj', JSON.parse(localStorage.getItem('products')!));
      // eslint-disable-next-line no-console
      console.log('objjj parseee', this.products1);
    }

    // eslint-disable-next-line no-console
    console.log(this.products1.length);
    // eslint-disable-next-line no-console
    console.log(this.products.length);
    //}
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated() === true && this.display === false;
  }

  isAuth(): boolean {
    return this.accountService.isAuthenticated();
  }

  reset(): void {
    this.display = false;
    localStorage.clear();
  }

  login(): void {
    this.display = false;
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  protected paginateProducts(data: IProduct[] | null, headers: HttpHeaders): IProduct[] {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.products1.push(d);
      }
    }

    return this.products1;
  }
}
