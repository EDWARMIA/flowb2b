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

export const administracionGuard:
  CanActivateFn = () => {

  const authService =
    inject(AuthService);

  const router =
    inject(Router);

  if (
    !authService.estaAutenticado()
  ) {

    return router.createUrlTree([
      '/login'
    ]);
  }

  const permisos =
    authService.obtenerPermisos();

  const puedeAdministrarUsuarios =
    permisos.includes(
      'ADMIN_USUARIO_GESTIONAR'
    );

  const puedeAdministrarRoles =
    permisos.includes(
      'ADMIN_ROL_GESTIONAR'
    );

  if (
    puedeAdministrarUsuarios &&
    puedeAdministrarRoles
  ) {

    return true;
  }

  return router.createUrlTree([
    '/dashboard'
  ]);
};