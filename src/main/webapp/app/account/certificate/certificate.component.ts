import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'jhi-certificate',
  templateUrl: './certificate.component.html',
})
export class CertificateComponent {
  image: any;
  success = false;
  imageForm = this.fb.group({
    imageUrl: [undefined, [Validators.required, Validators.minLength(1)]],
  });
  public showMsg = false;
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/custom/upload/certificate');

  constructor(private fb: FormBuilder, private applicationConfigService: ApplicationConfigService, private httpClient: HttpClient) {
    this.showMsg = false;
  }

  //   ngOnInit(): void {
  //     this.accountService.identity().subscribe(account => {
  //       if (account) {
  //         this.imageForm.patchValue({
  //           imageUrl: account.imageUrl,
  //         });

  //         this.account = account;
  //       }
  //     });
  //   }

  /*eslint consistent-return: ["error", { "treatUndefinedAsUnspecified": true }]*/
  onFileSelect($event: any): any {
    if ($event.target.files.length > 0) {
      const file = $event.target.files[0];
      this.imageForm.get('imageUrl')!.setValue(file);
    }
  }

  /*eslint consistent-return: ["error", { "treatUndefinedAsUnspecified": true }]*/
  onSubmit(): any {
    const formData = new FormData();
    formData.append('file', this.imageForm.get('imageUrl')!.value);

    this.httpClient.post<any>(this.resourceUrl, formData).subscribe(
      // eslint-disable-next-line no-console
      res => console.log(res),
      // eslint-disable-next-line no-console
      err => console.log(err)
    );

    this.showMsg = true;
  }
}
