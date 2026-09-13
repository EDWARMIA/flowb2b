import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';

import { inject } from '@angular/core';

import { Router } from '@angular/router';

import {
  catchError,
  throwError
} from 'rxjs';

import { AuthService } from '../services/auth.service';

export const authInterceptor:
  HttpInterceptorFn = (
    req,
    next
  ) => {

  const authService =
    inject(AuthService);

  const router =
    inject(Router);

  // ==========================================
  // NO AGREGAR JWT AL LOGIN
  // ==========================================

  if (
    req.url.includes(
      '/api/auth/login'
    )
  ) {

    return next(req);
  }

  const token =
    authService.obtenerToken();

  // ==========================================
  // SI NO HAY TOKEN
  // ==========================================

  if (!token) {

    return next(req).pipe(

      catchError(
        (
          error:
            HttpErrorResponse
        ) => {

          if (
            error.status === 401
          ) {

            authService.logout();

            router.navigate([
              '/login'
            ]);
          }

          return throwError(
            () => error
          );
        }
      )

    );
  }

  // ==========================================
  // AGREGAR JWT
  // ==========================================

  const requestConToken =
    req.clone({

      setHeaders: {

        Authorization:
          `Bearer ${token}`

      }

    });

  // ==========================================
  // CONTROL GLOBAL DE ERRORES
  // ==========================================

  return next(
    requestConToken
  ).pipe(

    catchError(
      (
        error:
          HttpErrorResponse
      ) => {

        // ======================================
        // JWT EXPIRADO / SESIÓN INVÁLIDA
        // ======================================

        if (
          error.status === 401
        ) {

          authService.logout();

          router.navigate([
            '/login'
          ]);

        }

        return throwError(
          () => error
        );
      }
    )

  );
};