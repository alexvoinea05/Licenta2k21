import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { CertificateGrowerService } from './certificate-grower.service';
import { CertificateGrower, ICertificateGrower } from './certificate-grower.model';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { AnyTxtRecord } from 'node:dns';

@Component({
  selector: 'jhi-certificate-grower',
  templateUrl: './certificate-grower.component.html',
})
export class CertificateGrowerComponent implements OnInit {
  currentAccount: Account | null = null;
  users: CertificateGrower[];
  isLoading = false;
  page: number;
  predicate: string;
  ascending: boolean;
  links: { [key: string]: number };
  itemsPerPage: number;
  totalItems = 0;
  image: any;

  constructor(
    private certificateGrowerService: CertificateGrowerService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    protected parseLinks: ParseLinks,
    private sanitizer: DomSanitizer
  ) {
    this.users = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  ngOnInit(): void {
    // eslint-disable-next-line no-console
    console.log('muie');
    this.loadAll();
  }

  trackIdentity(index: number, item: CertificateGrower): number {
    return item.id!;
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
      },
    });
  }

  loadAll(): void {
    this.isLoading = true;

    this.certificateGrowerService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<ICertificateGrower[]>) => {
          this.isLoading = false;
          res.body;
          this.paginateProducts(res.body, res.headers);
          // eslint-disable-next-line no-console
          console.log(res.body);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  protected paginateProducts(data: ICertificateGrower[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        // eslint-disable-next-line no-console
        console.log(d.certificate);

        //const objectURL = 'data:image/png;base64,' + d.certificate!;
        //this.image = this.sanitizer.bypassSecurityTrustResourceUrl(`data:image/jpeg;base64, ${String(d.certificate!)}`);
        // const TYPED_ARRAY = new Uint8Array(d.certificate);
        // const STRING_CHAR = TYPED_ARRAY.reduce((data1, byte) => {(
        //   return data1 + String.fromCharCode(byte);
        //   }, ''));
        // const base64String = btoa(STRING_CHAR);
        // this.image = this.sanitizer.bypassSecurityTrustUrl(`data:image/jpg;base64, ` + base64String);
        // d.certificate = this.image;
        //d.certificate = this.sanitizer.bypassSecurityTrustResourceUrl(`data:image/jpeg;base64, ${String(d.certificate)}`);
        //d.certificate = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpeg;base64,' + d.certificate);
        this.users.push(d);
        // eslint-disable-next-line no-console
        console.log(d.certificate);
      }
    }
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }
}
