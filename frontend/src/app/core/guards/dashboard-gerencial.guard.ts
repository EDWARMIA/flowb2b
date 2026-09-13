import {
  inject
} from '@angular/core';

import {
  CanActivateFn,
  Router
} from '@angular/router';

import {
  AuthService
} from '../services/auth.service';

export const dashboardGerencialGuard:
  CanActivateFn = () => {

  const authService =
    inject(AuthService);

  const router =
    inject(Router);

  const permisos =
    authService.obtenerPermisos();

  const autorizado =
    permisos.includes(
      'DASHBOARD_GERENCIAL_VER'
    );

  if (autorizado) {
    return true;
  }

  return router.createUrlTree([
    '/dashboard'
  ]);
};