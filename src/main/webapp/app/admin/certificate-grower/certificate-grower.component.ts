import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { CertificateGrowerService } from './certificate-grower.service';
import { CertificateGrower, ICertificateGrower } from './certificate-grower.model';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';

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

  constructor(
    private certificateGrowerService: CertificateGrowerService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    protected parseLinks: ParseLinks
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
        this.users.push(d);
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
