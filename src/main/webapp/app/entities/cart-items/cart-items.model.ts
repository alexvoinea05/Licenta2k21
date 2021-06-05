import * as dayjs from 'dayjs';
import { IProduct } from 'app/entities/product/product.model';
import { ICartOrderDetails } from 'app/entities/cart-order-details/cart-order-details.model';

export interface ICartItems {
  idCartItems?: number;
  quantity?: number;
  createdAt?: dayjs.Dayjs | null;
  modifiedAt?: dayjs.Dayjs | null;
  idProduct?: IProduct | null;
  idOrderDetails?: ICartOrderDetails | null;
}

export class CartItems implements ICartItems {
  constructor(
    public idCartItems?: number,
    public quantity?: number,
    public createdAt?: dayjs.Dayjs | null,
    public modifiedAt?: dayjs.Dayjs | null,
    public idProduct?: IProduct | null,
    public idOrderDetails?: ICartOrderDetails | null
  ) {}
}

export function getCartItemsIdentifier(cartItems: ICartItems): number | undefined {
  return cartItems.idCartItems;
}
