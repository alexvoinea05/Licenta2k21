import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Pagination } from 'app/core/request/request.model';
import { ICertificateGrower } from './certificate-grower.model';
import { map } from 'rxjs/operators';

export type EntityArrayResponseType = HttpResponse<ICertificateGrower[]>;

@Injectable({ providedIn: 'root' })
export class CertificateGrowerService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/custom/admin/certificate-image');

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  query(req?: Pagination): Observable<HttpResponse<ICertificateGrower[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<ICertificateGrower[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    return res;
  }
}
