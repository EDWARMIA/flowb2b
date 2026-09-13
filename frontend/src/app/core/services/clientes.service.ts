import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ClienteRequest {
  tipoDocumento: string | null;
  numeroDocumento: string | null;
  razonSocial: string;
  nombreComercial: string | null;
  correo: string | null;
  telefono: string | null;
  direccion: string | null;
  contactoNombre: string | null;
  contactoTelefono: string | null;
  contactoCorreo: string | null;
  estado: boolean;
}

export interface ClienteResponse {
  idCliente: number;
  empresaId: number;
  tipoDocumento: string | null;
  numeroDocumento: string | null;
  razonSocial: string;
  nombreComercial: string | null;
  correo: string | null;
  telefono: string | null;
  direccion: string | null;
  contactoNombre: string | null;
  contactoTelefono: string | null;
  contactoCorreo: string | null;
  estado: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClientesService {

  private readonly apiUrl =
    'http://localhost:8080/api/clientes';

  constructor(
    private http: HttpClient
  ) {
  }

  listar(): Observable<ClienteResponse[]> {

    return this.http.get<ClienteResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idCliente: number
  ): Observable<ClienteResponse> {

    return this.http.get<ClienteResponse>(
      `${this.apiUrl}/${idCliente}`
    );
  }

  crear(
    cliente: ClienteRequest
  ): Observable<ClienteResponse> {

    return this.http.post<ClienteResponse>(
      this.apiUrl,
      cliente
    );
  }

  actualizar(
    idCliente: number,
    cliente: ClienteRequest
  ): Observable<ClienteResponse> {

    return this.http.put<ClienteResponse>(
      `${this.apiUrl}/${idCliente}`,
      cliente
    );
  }
}