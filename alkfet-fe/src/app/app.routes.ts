import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'root-certificates', pathMatch: 'full' },
  {
    path: 'root-certificates',
    loadComponent: () =>
      import('./pages/root-certificates/root-certificates.component').then(m => m.RootCertificatesComponent)
  },
  {
    path: 'user-certificates',
    loadComponent: () =>
      import('./pages/user-certificates/user-certificates.component').then(m => m.UserCertificatesComponent)
  }
];
