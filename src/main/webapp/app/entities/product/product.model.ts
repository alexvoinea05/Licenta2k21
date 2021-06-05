import * as dayjs from 'dayjs';
import { ICartItems } from 'app/entities/cart-items/cart-items.model';
import { ICategory } from 'app/entities/category/category.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IProduct {
  idProduct?: number;
  name?: string;
  price?: number;
  stock?: number;
  imageUrl?: string | null;
  productUrl?: string | null;
  createdAt?: dayjs.Dayjs | null;
  modifiedAt?: dayjs.Dayjs | null;
  cartItems?: ICartItems[] | null;
  idCategory?: ICategory | null;
  idGrower?: IAppUser | null;
}

export interface IProductCart {
  idProduct?: number;
  name?: string;
  price?: number;
  stock?: number;
  quantity?: number;
}

export class ProductCart implements IProductCart {
  constructor(public idProduct?: number, public name?: string, public price?: number, public stock?: number, public number?: number) {}
}

export class Product implements IProduct {
  constructor(
    public idProduct?: number,
    public name?: string,
    public price?: number,
    public stock?: number,
    public imageUrl?: string | null,
    public productUrl?: string | null,
    public createdAt?: dayjs.Dayjs | null,
    public modifiedAt?: dayjs.Dayjs | null,
    public cartItems?: ICartItems[] | null,
    public idCategory?: ICategory | null,
    public idGrower?: IAppUser | null
  ) {}
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.idProduct;
}
