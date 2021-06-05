import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { CertificateGrowerComponent } from './certificate-grower.component';
import { certificateGrowerRoute } from './certificate-grower.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild(certificateGrowerRoute)],
  declarations: [CertificateGrowerComponent],
})
export class CertificateGrowerModule {}
