import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleSolicitudRequest {
  productoId: number;
  cantidad: number;
  observacion: string | null;
}

export interface SolicitudRequest {
  clienteId: number;
  codigo: string;
  origen: string;
  descripcion: string | null;
  detalles: DetalleSolicitudRequest[];
}

export interface DetalleSolicitudResponse {
  idDetalle: number;
  productoId: number;
  cantidad: number;
  observacion: string | null;
}

export interface SolicitudResponse {
  idSolicitud: number;
  empresaId: number;
  clienteId: number;
  vendedorId: number;
  codigo: string;
  origen: string;
  descripcion: string | null;
  estado: string;
  fechaSolicitud: string;
  fechaCreacion: string;
  fechaActualizacion: string;
  detalles: DetalleSolicitudResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class SolicitudesService {

  private readonly apiUrl =
    'http://localhost:8080/api/solicitudes';

  constructor(
    private http: HttpClient
  ) {
  }

  listar(): Observable<SolicitudResponse[]> {

    return this.http.get<SolicitudResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idSolicitud: number
  ): Observable<SolicitudResponse> {

    return this.http.get<SolicitudResponse>(
      `${this.apiUrl}/${idSolicitud}`
    );
  }

  crear(
    solicitud: SolicitudRequest
  ): Observable<SolicitudResponse> {

    return this.http.post<SolicitudResponse>(
      this.apiUrl,
      solicitud
    );
  }
}