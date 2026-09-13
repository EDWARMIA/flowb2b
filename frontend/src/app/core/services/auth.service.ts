import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  correo: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  roles?: string[];
  permisos?: string[];
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {
  }

  login(datos: LoginRequest): Observable<LoginResponse> {

    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, datos)
      .pipe(
        tap(respuesta => {

          if (respuesta.token) {

            localStorage.setItem(
              'flowb2b_token',
              respuesta.token
            );

            localStorage.setItem(
              'flowb2b_usuario',
              JSON.stringify(respuesta)
            );

          }

        })
      );
  }

  obtenerToken(): string | null {
    return localStorage.getItem('flowb2b_token');
  }

  obtenerUsuario(): LoginResponse | null {

    const usuarioGuardado =
      localStorage.getItem('flowb2b_usuario');

    if (!usuarioGuardado) {
      return null;
    }

    try {

      const usuario: LoginResponse =
        JSON.parse(usuarioGuardado);

      return usuario;

    } catch (error) {

      console.error(
        'Error al leer el usuario guardado:',
        error
      );

      return null;
    }
  }

  obtenerRoles(): string[] {

    const usuario = this.obtenerUsuario();

    return usuario?.roles ?? [];
  }

  obtenerPermisos(): string[] {

    const usuario = this.obtenerUsuario();

    return usuario?.permisos ?? [];
  }

  estaAutenticado(): boolean {
    return !!this.obtenerToken();
  }

  logout(): void {

    localStorage.removeItem('flowb2b_token');

    localStorage.removeItem('flowb2b_usuario');
  }
}