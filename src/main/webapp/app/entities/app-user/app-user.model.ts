import * as dayjs from 'dayjs';
import { IUser } from '../user/user.model';
import { ICartOrderDetails } from '../cart-order-details/cart-order-details.model';
import { IProduct } from '../product/product.model';

export interface IAppUser {
  idAppUser?: number;
  certificateUrl?: string | null;
  adress?: string;
  createdAt?: dayjs.Dayjs | null;
  modifiedAt?: dayjs.Dayjs | null;
  user?: IUser | null;
  cartOrderDetails?: ICartOrderDetails[] | null;
  products?: IProduct[] | null;
}

export class AppUser implements IAppUser {
  constructor(
    public idAppUser?: number,
    public certificateUrl?: string | null,
    public adress?: string,
    public createdAt?: dayjs.Dayjs | null,
    public modifiedAt?: dayjs.Dayjs | null,
    public user?: IUser | null,
    public cartOrderDetails?: ICartOrderDetails[] | null,
    public products?: IProduct[] | null
  ) {}
}

export function getAppUserIdentifier(appUser: IAppUser): number | undefined {
  return appUser.idAppUser;
}
