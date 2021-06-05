import { Injectable } from '@angular/core';
import { Resolve, Routes } from '@angular/router';
import { CertificateGrowerComponent } from './certificate-grower.component';
import { ICertificateGrower } from './certificate-grower.model';
import { CertificateGrowerService } from './certificate-grower.service';

export const certificateGrowerRoute: Routes = [
  {
    path: '',
    component: CertificateGrowerComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
];
