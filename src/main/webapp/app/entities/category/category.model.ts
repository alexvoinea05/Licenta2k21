import * as dayjs from 'dayjs';
import { IProduct } from 'app/entities/product/product.model';

export interface ICategory {
  idCategory?: number;
  categoryName?: string;
  createdAt?: dayjs.Dayjs | null;
  modifiedAt?: dayjs.Dayjs | null;
  products?: IProduct[] | null;
}

export class Category implements ICategory {
  constructor(
    public idCategory?: number,
    public categoryName?: string,
    public createdAt?: dayjs.Dayjs | null,
    public modifiedAt?: dayjs.Dayjs | null,
    public products?: IProduct[] | null
  ) {}
}

export function getCategoryIdentifier(category: ICategory): number | undefined {
  return category.idCategory;
}
