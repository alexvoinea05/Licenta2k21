import { Route } from '@angular/router';
import { CertificateComponent } from './certificate.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

export const certificateRoute: Route = {
  path: 'certificate',
  component: CertificateComponent,
  data: {
    pageTitle: 'Certificate',
  },
  canActivate: [UserRouteAccessService],
};
