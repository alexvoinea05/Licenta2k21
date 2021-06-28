export interface ICertificateGrower {
  id?: number;
  login?: string;
  certificate?: any;
}

export class CertificateGrower implements ICertificateGrower {
  constructor(public id?: number, public login?: string, public certificate?: any) {}
}
