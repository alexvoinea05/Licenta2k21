import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AppUserDetailComponent } from './app-user-detail.component';

describe('Component Tests', () => {
  describe('AppUser Management Detail Component', () => {
    let comp: AppUserDetailComponent;
    let fixture: ComponentFixture<AppUserDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [AppUserDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ appUser: { idAppUser: 123 } }) },
          },
        ],
      })
        .overrideTemplate(AppUserDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AppUserDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load appUser on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.appUser).toEqual(jasmine.objectContaining({ idAppUser: 123 }));
      });
    });
  });
});
