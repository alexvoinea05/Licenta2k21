export interface ICertificateGrower {
  id?: number;
  login?: string;
  imageUrl?: string | null;
}

export class CertificateGrower implements ICertificateGrower {
  constructor(public id?: number, public login?: string, public imageUrl?: string | null) {}
}
