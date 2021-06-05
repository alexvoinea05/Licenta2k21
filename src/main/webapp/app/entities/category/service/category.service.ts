import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICategory, getCategoryIdentifier } from '../category.model';
import { IProduct } from '../../product/product.model';

export type EntityResponseType = HttpResponse<ICategory>;
export type EntityArrayResponseType = HttpResponse<ICategory[]>;
export type EntityArrayResponseProductType = HttpResponse<IProduct[]>;

@Injectable({ providedIn: 'root' })
export class CategoryService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/categories');

  public createCategoryUrl = this.applicationConfigService.getEndpointFor('api/adaugare/categorie');

  public categoryProductUrl = this.applicationConfigService.getEndpointFor('api/category/products');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(category: ICategory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(category);
    return this.http
      .post<ICategory>(this.createCategoryUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(category: ICategory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(category);
    return this.http
      .put<ICategory>(`${this.resourceUrl}/${getCategoryIdentifier(category) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(category: ICategory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(category);
    return this.http
      .patch<ICategory>(`${this.resourceUrl}/${getCategoryIdentifier(category) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICategory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICategory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryByCategory(category: ICategory, req?: any): Observable<EntityArrayResponseProductType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProduct[]>(`${this.categoryProductUrl}/${getCategoryIdentifier(category) as number}`, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseProductType) => this.convertProductDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCategoryToCollectionIfMissing(categoryCollection: ICategory[], ...categoriesToCheck: (ICategory | null | undefined)[]): ICategory[] {
    const categories: ICategory[] = categoriesToCheck.filter(isPresent);
    if (categories.length > 0) {
      const categoryCollectionIdentifiers = categoryCollection.map(categoryItem => getCategoryIdentifier(categoryItem)!);
      const categoriesToAdd = categories.filter(categoryItem => {
        const categoryIdentifier = getCategoryIdentifier(categoryItem);
        if (categoryIdentifier == null || categoryCollectionIdentifiers.includes(categoryIdentifier)) {
          return false;
        }
        categoryCollectionIdentifiers.push(categoryIdentifier);
        return true;
      });
      return [...categoriesToAdd, ...categoryCollection];
    }
    return categoryCollection;
  }

  protected convertDateFromClient(category: ICategory): ICategory {
    return Object.assign({}, category, {
      createdAt: category.createdAt?.isValid() ? category.createdAt.toJSON() : undefined,
      modifiedAt: category.modifiedAt?.isValid() ? category.modifiedAt.toJSON() : undefined,
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
      res.body.forEach((category: ICategory) => {
        category.createdAt = category.createdAt ? dayjs(category.createdAt) : undefined;
        category.modifiedAt = category.modifiedAt ? dayjs(category.modifiedAt) : undefined;
      });
    }
    return res;
  }

  protected convertProductDateArrayFromServer(res: EntityArrayResponseProductType): EntityArrayResponseProductType {
    if (res.body) {
      res.body.forEach((product: IProduct) => {
        product.createdAt = product.createdAt ? dayjs(product.createdAt) : undefined;
        product.modifiedAt = product.modifiedAt ? dayjs(product.modifiedAt) : undefined;
      });
    }
    return res;
  }
}
