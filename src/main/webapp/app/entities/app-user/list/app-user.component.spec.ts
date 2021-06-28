import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AppUserService } from '../service/app-user.service';

import { AppUserComponent } from './app-user.component';

describe('Component Tests', () => {
  describe('AppUser Management Component', () => {
    let comp: AppUserComponent;
    let fixture: ComponentFixture<AppUserComponent>;
    let service: AppUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AppUserComponent],
      })
        .overrideTemplate(AppUserComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppUserComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(AppUserService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ idAppUser: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.appUsers[0]).toEqual(jasmine.objectContaining({ idAppUser: 123 }));
    });

    it('should load a page', () => {
      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.appUsers[0]).toEqual(jasmine.objectContaining({ idAppUser: 123 }));
    });

    it('should calculate the sort attribute for an id', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalledWith(expect.objectContaining({ sort: ['idAppUser,asc'] }));
    });

    it('should calculate the sort attribute for a non-id attribute', () => {
      // INIT
      comp.ngOnInit();

      // GIVEN
      comp.predicate = 'name';

      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['name,asc', 'idAppUser'] }));
    });

    it('should re-initialize the page', () => {
      // WHEN
      comp.loadPage(1);
      comp.reset();

      // THEN
      expect(comp.page).toEqual(0);
      expect(service.query).toHaveBeenCalledTimes(2);
      expect(comp.appUsers[0]).toEqual(jasmine.objectContaining({ idAppUser: 123 }));
    });
  });
});
