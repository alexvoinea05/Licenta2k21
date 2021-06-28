import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IProduct } from '../entities/product/product.model';
import { ApplicationConfigService } from '../core/config/application-config.service';
import { map } from 'rxjs/operators';
import { ProductSearch } from './productSearch.model';
import * as dayjs from 'dayjs';

export type EntityArrayResponseType = HttpResponse<IProduct[]>;

@Injectable({ providedIn: 'root' })
export class HomeService {
  public resourceUrlToAlgorithm = this.applicationConfigService.getEndpointFor('api/product/alghorithm');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  getBestProducts(productSearch?: ProductSearch[]): any {
    //const options = createRequestOption(req);
    let params = new HttpParams();

    if (productSearch) {
      productSearch.forEach(prod => {
        params = params.append('numeProdus', prod.numeProdus!);
        params = params.append('cantitate', prod.cantitate!);
        // eslint-disable-next-line no-console
        console.log(JSON.stringify(params));
        // eslint-disable-next-line no-console
        console.log(params);
      });
    }

    // eslint-disable-next-line no-console
    console.log(JSON.stringify(params));
    return this.http
      .get<IProduct[]>(this.resourceUrlToAlgorithm, { params, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertProductDateArrayFromServer(res)));
  }

  protected convertProductDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    return res;
  }
}
