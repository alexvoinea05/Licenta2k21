import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICartItems, getCartItemsIdentifier } from '../cart-items.model';

export type EntityResponseType = HttpResponse<ICartItems>;
export type EntityArrayResponseType = HttpResponse<ICartItems[]>;

@Injectable({ providedIn: 'root' })
export class CartItemsService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/cart-items');

  public customResourceUrl = this.applicationConfigService.getEndpointFor('api/custom/cart-items');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(cartItems: ICartItems): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cartItems);
    return this.http
      .post<ICartItems>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(cartItems: ICartItems): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cartItems);
    return this.http
      .put<ICartItems>(`${this.customResourceUrl}/${getCartItemsIdentifier(cartItems) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(cartItems: ICartItems): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cartItems);
    return this.http
      .patch<ICartItems>(`${this.customResourceUrl}/${getCartItemsIdentifier(cartItems) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICartItems>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICartItems[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.customResourceUrl}/${id}`, { observe: 'response' });
  }

  deleteAndUpdatePrice(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.customResourceUrl}/${id}`, { observe: 'response' });
  }

  addCartItemsToCollectionIfMissing(
    cartItemsCollection: ICartItems[],
    ...cartItemsToCheck: (ICartItems | null | undefined)[]
  ): ICartItems[] {
    const cartItems: ICartItems[] = cartItemsToCheck.filter(isPresent);
    if (cartItems.length > 0) {
      const cartItemsCollectionIdentifiers = cartItemsCollection.map(cartItemsItem => getCartItemsIdentifier(cartItemsItem)!);
      const cartItemsToAdd = cartItems.filter(cartItemsItem => {
        const cartItemsIdentifier = getCartItemsIdentifier(cartItemsItem);
        if (cartItemsIdentifier == null || cartItemsCollectionIdentifiers.includes(cartItemsIdentifier)) {
          return false;
        }
        cartItemsCollectionIdentifiers.push(cartItemsIdentifier);
        return true;
      });
      return [...cartItemsToAdd, ...cartItemsCollection];
    }
    return cartItemsCollection;
  }

  protected convertDateFromClient(cartItems: ICartItems): ICartItems {
    return Object.assign({}, cartItems, {
      createdAt: cartItems.createdAt?.isValid() ? cartItems.createdAt.toJSON() : undefined,
      modifiedAt: cartItems.modifiedAt?.isValid() ? cartItems.modifiedAt.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdAt = res.body.createdAt ? dayjs(res.body.createdAt) : undefined;
      res.body.modifiedAt = res.body.modifiedAt ? dayjs(res.body.modifiedAt) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((cartItems: ICartItems) => {
        cartItems.createdAt = cartItems.createdAt ? dayjs(cartItems.createdAt) : undefined;
        cartItems.modifiedAt = cartItems.modifiedAt ? dayjs(cartItems.modifiedAt) : undefined;
      });
    }
    return res;
  }
}
