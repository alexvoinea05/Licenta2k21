import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
})
export class FooterComponent {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/custom');
  public imageUrl: any = this.findImage(); // = this.http.get(this.applicationConfigService.getEndpointFor('image/get'));

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  // ngOnInit(): void{
  //   this.findImage();

  // }

  findImage(): any {
    return this.http.get(`${this.resourceUrl}/image/get`, { observe: 'response' });
    //return "https://i.ibb.co/0M8BC1L/Capture.png";
    // return this.http
    //   .get<IProduct>(`${this.resourceUrl}/${id}`, { observe: 'response' })
    //   .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
}
