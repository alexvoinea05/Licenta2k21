import * as dayjs from 'dayjs';
import { ICartItems } from 'app/entities/cart-items/cart-items.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface ICartOrderDetails {
  idCartOrderDetails?: number;
  totalPrice?: number | null;
  createdAt?: dayjs.Dayjs | null;
  modifiedAt?: dayjs.Dayjs | null;
  statusCommand?: string | null;
  cartItems?: ICartItems[] | null;
  idAppUser?: IAppUser | null;
}

export class CartOrderDetails implements ICartOrderDetails {
  constructor(
    public idCartOrderDetails?: number,
    public totalPrice?: number | null,
    public createdAt?: dayjs.Dayjs | null,
    public modifiedAt?: dayjs.Dayjs | null,
    public statusCommand?: string | null,
    public cartItems?: ICartItems[] | null,
    public idAppUser?: IAppUser | null
  ) {}
}

export function getCartOrderDetailsIdentifier(cartOrderDetails: ICartOrderDetails): number | undefined {
  return cartOrderDetails.idCartOrderDetails;
}
