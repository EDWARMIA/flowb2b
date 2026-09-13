import {
  Injectable
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

export interface AdministracionUsuario {
  idUsuario: number;
  nombre: string;
  apellido: string;
  correo: string;
  estado: boolean;
  rolesIds: number[];
  roles: string[];
}

export interface AdministracionRol {
  idRol: number;
  nombre: string;
  descripcion: string;
  estado: boolean;
  permisosIds: number[];
  permisos: string[];
}

export interface AdministracionPermiso {
  idPermiso: number;
  codigo: string;
  nombre: string;
  descripcion: string;
}

export interface CrearUsuarioAdministracion {
  nombre: string;
  apellido: string;
  correo: string;
  password: string;
  estado: boolean;
  rolesIds: number[];
}

export interface ActualizarUsuarioAdministracion {
  estado: boolean;
  rolesIds: number[];
}

export interface RolAdministracionRequest {
  nombre: string;
  descripcion: string;
  estado: boolean;
  permisosIds: number[];
}

@Injectable({
  providedIn: 'root'
})
export class AdministracionService {

  private readonly apiUrl =
    'http://localhost:8080/api/administracion';

  constructor(
    private http: HttpClient
  ) {
  }

  // =====================================================
  // USUARIOS
  // =====================================================

  listarUsuarios():
    Observable<AdministracionUsuario[]> {

    return this.http.get<
      AdministracionUsuario[]
    >(
      `${this.apiUrl}/usuarios`
    );
  }

  crearUsuario(
    datos: CrearUsuarioAdministracion
  ):
    Observable<AdministracionUsuario> {

    return this.http.post<
      AdministracionUsuario
    >(
      `${this.apiUrl}/usuarios`,
      datos
    );
  }

  actualizarUsuario(
    idUsuario: number,
    datos: ActualizarUsuarioAdministracion
  ):
    Observable<AdministracionUsuario> {

    return this.http.put<
      AdministracionUsuario
    >(
      `${this.apiUrl}/usuarios/${idUsuario}`,
      datos
    );
  }

  // =====================================================
  // ROLES
  // =====================================================

  listarRoles():
    Observable<AdministracionRol[]> {

    return this.http.get<
      AdministracionRol[]
    >(
      `${this.apiUrl}/roles`
    );
  }

  crearRol(
    datos: RolAdministracionRequest
  ):
    Observable<AdministracionRol> {

    return this.http.post<
      AdministracionRol
    >(
      `${this.apiUrl}/roles`,
      datos
    );
  }

  actualizarRol(
    idRol: number,
    datos: RolAdministracionRequest
  ):
    Observable<AdministracionRol> {

    return this.http.put<
      AdministracionRol
    >(
      `${this.apiUrl}/roles/${idRol}`,
      datos
    );
  }

  // =====================================================
  // PERMISOS
  // =====================================================

  listarPermisos():
    Observable<AdministracionPermiso[]> {

    return this.http.get<
      AdministracionPermiso[]
    >(
      `${this.apiUrl}/permisos`
    );
  }
}