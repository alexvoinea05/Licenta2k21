import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAppUser, getAppUserIdentifier } from '../app-user.model';

export type EntityResponseType = HttpResponse<IAppUser>;
export type EntityArrayResponseType = HttpResponse<IAppUser[]>;

@Injectable({ providedIn: 'root' })
export class AppUserService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/app-users');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(appUser: IAppUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appUser);
    return this.http
      .post<IAppUser>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(appUser: IAppUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appUser);
    return this.http
      .put<IAppUser>(`${this.resourceUrl}/${getAppUserIdentifier(appUser) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(appUser: IAppUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appUser);
    return this.http
      .patch<IAppUser>(`${this.resourceUrl}/${getAppUserIdentifier(appUser) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAppUser>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAppUser[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAppUserToCollectionIfMissing(appUserCollection: IAppUser[], ...appUsersToCheck: (IAppUser | null | undefined)[]): IAppUser[] {
    const appUsers: IAppUser[] = appUsersToCheck.filter(isPresent);
    if (appUsers.length > 0) {
      const appUserCollectionIdentifiers = appUserCollection.map(appUserItem => getAppUserIdentifier(appUserItem)!);
      const appUsersToAdd = appUsers.filter(appUserItem => {
        const appUserIdentifier = getAppUserIdentifier(appUserItem);
        if (appUserIdentifier == null || appUserCollectionIdentifiers.includes(appUserIdentifier)) {
          return false;
        }
        appUserCollectionIdentifiers.push(appUserIdentifier);
        return true;
      });
      return [...appUsersToAdd, ...appUserCollection];
    }
    return appUserCollection;
  }

  protected convertDateFromClient(appUser: IAppUser): IAppUser {
    return Object.assign({}, appUser, {
      createdAt: appUser.createdAt?.isValid() ? appUser.createdAt.toJSON() : undefined,
      modifiedAt: appUser.modifiedAt?.isValid() ? appUser.modifiedAt.toJSON() : undefined,
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
      res.body.forEach((appUser: IAppUser) => {
        appUser.createdAt = appUser.createdAt ? dayjs(appUser.createdAt) : undefined;
        appUser.modifiedAt = appUser.modifiedAt ? dayjs(appUser.modifiedAt) : undefined;
      });
    }
    return res;
  }
}
