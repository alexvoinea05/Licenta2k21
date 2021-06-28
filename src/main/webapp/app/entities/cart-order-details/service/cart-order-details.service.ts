import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICartOrderDetails, getCartOrderDetailsIdentifier } from '../cart-order-details.model';

export type EntityResponseType = HttpResponse<ICartOrderDetails>;
export type EntityArrayResponseType = HttpResponse<ICartOrderDetails[]>;

@Injectable({ providedIn: 'root' })
export class CartOrderDetailsService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/cart-order-details');

  public finalizareUrl = this.applicationConfigService.getEndpointFor('api/custom/cart-order-details');
  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(cartOrderDetails: ICartOrderDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cartOrderDetails);
    return this.http
      .post<ICartOrderDetails>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  updateToFinalizat(cartOrderDetails: any): Observable<EntityResponseType> {
    const copy = this.convertFinalizareDateFromClient(cartOrderDetails);
    return this.http
      .put<ICartOrderDetails>(`${this.finalizareUrl}/${cartOrderDetails as number}`, copy, {
        // .put<ICartOrderDetails>(`${this.finalizareUrl}  `, copy, {

        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(cartOrderDetails: ICartOrderDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cartOrderDetails);
    return this.http
      .put<ICartOrderDetails>(`${this.resourceUrl}/${getCartOrderDetailsIdentifier(cartOrderDetails) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(cartOrderDetails: ICartOrderDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cartOrderDetails);
    return this.http
      .patch<ICartOrderDetails>(`${this.resourceUrl}/${getCartOrderDetailsIdentifier(cartOrderDetails) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICartOrderDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICartOrderDetails[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCartOrderDetailsToCollectionIfMissing(
    cartOrderDetailsCollection: ICartOrderDetails[],
    ...cartOrderDetailsToCheck: (ICartOrderDetails | null | undefined)[]
  ): ICartOrderDetails[] {
    const cartOrderDetails: ICartOrderDetails[] = cartOrderDetailsToCheck.filter(isPresent);
    if (cartOrderDetails.length > 0) {
      const cartOrderDetailsCollectionIdentifiers = cartOrderDetailsCollection.map(
        cartOrderDetailsItem => getCartOrderDetailsIdentifier(cartOrderDetailsItem)!
      );
      const cartOrderDetailsToAdd = cartOrderDetails.filter(cartOrderDetailsItem => {
        const cartOrderDetailsIdentifier = getCartOrderDetailsIdentifier(cartOrderDetailsItem);
        if (cartOrderDetailsIdentifier == null || cartOrderDetailsCollectionIdentifiers.includes(cartOrderDetailsIdentifier)) {
          return false;
        }
        cartOrderDetailsCollectionIdentifiers.push(cartOrderDetailsIdentifier);
        return true;
      });
      return [...cartOrderDetailsToAdd, ...cartOrderDetailsCollection];
    }
    return cartOrderDetailsCollection;
  }

  protected convertDateFromClient(cartOrderDetails: ICartOrderDetails): ICartOrderDetails {
    return Object.assign({}, cartOrderDetails, {
      createdAt: cartOrderDetails.createdAt?.isValid() ? cartOrderDetails.createdAt.toJSON() : undefined,
      modifiedAt: cartOrderDetails.modifiedAt?.isValid() ? cartOrderDetails.modifiedAt.toJSON() : undefined,
    });
  }

  protected convertFinalizareDateFromClient(cartOrderDetails: ICartOrderDetails): ICartOrderDetails {
    return Object.assign({}, cartOrderDetails);
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
      res.body.forEach((cartOrderDetails: ICartOrderDetails) => {
        cartOrderDetails.createdAt = cartOrderDetails.createdAt ? dayjs(cartOrderDetails.createdAt) : undefined;
        cartOrderDetails.modifiedAt = cartOrderDetails.modifiedAt ? dayjs(cartOrderDetails.modifiedAt) : undefined;
      });
    }
    return res;
  }
}
