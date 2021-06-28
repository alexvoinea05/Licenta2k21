export interface IProductSearch {
  numeProdus?: string;
  cantitate?: string;
}

export class ProductSearch implements IProductSearch {
  constructor(public numeProdus?: string, public cantitate?: string) {}
}
