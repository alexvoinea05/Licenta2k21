import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppUser } from '../app-user.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { AppUserService } from '../service/app-user.service';
import { AppUserDeleteDialogComponent } from '../delete/app-user-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-app-user',
  templateUrl: './app-user.component.html',
})
export class AppUserComponent implements OnInit {
  appUsers: IAppUser[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected appUserService: AppUserService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.appUsers = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'idAppUser';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.appUserService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IAppUser[]>) => {
          this.isLoading = false;
          this.paginateAppUsers(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.appUsers = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackIdAppUser(index: number, item: IAppUser): number {
    return item.idAppUser!;
  }

  delete(appUser: IAppUser): void {
    const modalRef = this.modalService.open(AppUserDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.appUser = appUser;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'idAppUser') {
      result.push('idAppUser');
    }
    return result;
  }

  protected paginateAppUsers(data: IAppUser[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.appUsers.push(d);
      }
    }
  }
}
