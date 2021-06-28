jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProductService } from '../service/product.service';
import { IProduct, Product } from '../product.model';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { ProductUpdateComponent } from './product-update.component';

describe('Component Tests', () => {
  describe('Product Management Update Component', () => {
    let comp: ProductUpdateComponent;
    let fixture: ComponentFixture<ProductUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let productService: ProductService;
    let categoryService: CategoryService;
    let appUserService: AppUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ProductUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ProductUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProductUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      productService = TestBed.inject(ProductService);
      categoryService = TestBed.inject(CategoryService);
      appUserService = TestBed.inject(AppUserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Category query and add missing value', () => {
        const product: IProduct = { idProduct: 456 };
        const idCategory: ICategory = { idCategory: 31264 };
        product.idCategory = idCategory;

        const categoryCollection: ICategory[] = [{ idCategory: 80199 }];
        spyOn(categoryService, 'query').and.returnValue(of(new HttpResponse({ body: categoryCollection })));
        const additionalCategories = [idCategory];
        const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
        spyOn(categoryService, 'addCategoryToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ product });
        comp.ngOnInit();

        expect(categoryService.query).toHaveBeenCalled();
        expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(categoryCollection, ...additionalCategories);
        expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call AppUser query and add missing value', () => {
        const product: IProduct = { idProduct: 456 };
        const idGrower: IAppUser = { idAppUser: 23708 };
        product.idGrower = idGrower;

        const appUserCollection: IAppUser[] = [{ idAppUser: 95384 }];
        spyOn(appUserService, 'query').and.returnValue(of(new HttpResponse({ body: appUserCollection })));
        const additionalAppUsers = [idGrower];
        const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
        spyOn(appUserService, 'addAppUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ product });
        comp.ngOnInit();

        expect(appUserService.query).toHaveBeenCalled();
        expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(appUserCollection, ...additionalAppUsers);
        expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const product: IProduct = { idProduct: 456 };
        const idCategory: ICategory = { idCategory: 24103 };
        product.idCategory = idCategory;
        const idGrower: IAppUser = { idAppUser: 23281 };
        product.idGrower = idGrower;

        activatedRoute.data = of({ product });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(product));
        expect(comp.categoriesSharedCollection).toContain(idCategory);
        expect(comp.appUsersSharedCollection).toContain(idGrower);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const product = { idProduct: 123 };
        spyOn(productService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ product });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: product }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(productService.update).toHaveBeenCalledWith(product);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const product = new Product();
        spyOn(productService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ product });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: product }));
        saveSubject.complete();

        // THEN
        expect(productService.create).toHaveBeenCalledWith(product);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const product = { idProduct: 123 };
        spyOn(productService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ product });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(productService.update).toHaveBeenCalledWith(product);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCategoryByIdCategory', () => {
        it('Should return tracked Category primary key', () => {
          const entity = { idCategory: 123 };
          const trackResult = comp.trackCategoryByIdCategory(0, entity);
          expect(trackResult).toEqual(entity.idCategory);
        });
      });

      describe('trackAppUserByIdAppUser', () => {
        it('Should return tracked AppUser primary key', () => {
          const entity = { idAppUser: 123 };
          const trackResult = comp.trackAppUserByIdAppUser(0, entity);
          expect(trackResult).toEqual(entity.idAppUser);
        });
      });
    });
  });
});
