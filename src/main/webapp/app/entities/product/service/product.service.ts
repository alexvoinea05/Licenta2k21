import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProduct, getProductIdentifier, IProductCart } from '../product.model';

export type EntityResponseType = HttpResponse<IProduct>;
export type EntityProductCartResponseType = HttpResponse<IProductCart>;
export type EntityArrayResponseType = HttpResponse<IProduct[]>;

@Injectable({ providedIn: 'root' })
export class ProductService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/products');
  public categoryProductsUrl = this.applicationConfigService.getEndpointFor('api/products/category');
  public addProductToCartUrl = this.applicationConfigService.getEndpointFor('api/add/product');
  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(product: IProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(product);
    return this.http
      .post<IProduct>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  addProductToCart(productCart: IProductCart): Observable<EntityProductCartResponseType> {
    const copy = this.convertDateProductCartFromClient(productCart);
    return this.http
      .post<IProductCart>(this.addProductToCartUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityProductCartResponseType) => this.convertDateProductCartFromServer(res)));
  }

  update(product: IProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(product);
    return this.http
      .put<IProduct>(`${this.resourceUrl}/${getProductIdentifier(product) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(product: IProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(product);
    return this.http
      .patch<IProduct>(`${this.resourceUrl}/${getProductIdentifier(product) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IProduct>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProduct[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProductToCollectionIfMissing(productCollection: IProduct[], ...productsToCheck: (IProduct | null | undefined)[]): IProduct[] {
    const products: IProduct[] = productsToCheck.filter(isPresent);
    if (products.length > 0) {
      const productCollectionIdentifiers = productCollection.map(productItem => getProductIdentifier(productItem)!);
      const productsToAdd = products.filter(productItem => {
        const productIdentifier = getProductIdentifier(productItem);
        if (productIdentifier == null || productCollectionIdentifiers.includes(productIdentifier)) {
          return false;
        }
        productCollectionIdentifiers.push(productIdentifier);
        return true;
      });
      return [...productsToAdd, ...productCollection];
    }
    return productCollection;
  }

  protected convertDateFromClient(product: IProduct): IProduct {
    return Object.assign({}, product, {
      createdAt: product.createdAt?.isValid() ? product.createdAt.toJSON() : undefined,
      modifiedAt: product.modifiedAt?.isValid() ? product.modifiedAt.toJSON() : undefined,
    });
  }

  protected convertDateProductCartFromClient(productCart: IProductCart): IProductCart {
    return Object.assign({}, productCart);
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdAt = res.body.createdAt ? dayjs(res.body.createdAt) : undefined;
      res.body.modifiedAt = res.body.modifiedAt ? dayjs(res.body.modifiedAt) : undefined;
    }
    return res;
  }

  protected convertDateProductCartFromServer(res: EntityProductCartResponseType): EntityProductCartResponseType {
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((product: IProduct) => {
        product.createdAt = product.createdAt ? dayjs(product.createdAt) : undefined;
        product.modifiedAt = product.modifiedAt ? dayjs(product.modifiedAt) : undefined;
      });
    }
    return res;
  }
}
