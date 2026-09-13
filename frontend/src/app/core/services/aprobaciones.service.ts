import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SolicitudAprobacionRequest {
  cotizacionId: number;
  motivo: string | null;
}

export interface ResponderAprobacionRequest {
  comentario: string | null;
}

export interface AprobacionResponse {
  idAprobacion: number;
  empresaId: number;
  cotizacionId: number;
  solicitanteId: number;
  aprobadorId: number | null;
  estado: string;
  motivo: string | null;
  comentario: string | null;
  fechaSolicitud: string | null;
  fechaRespuesta: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AprobacionesService {

  private readonly apiUrl =
    'http://localhost:8080/api/aprobaciones';

  constructor(
    private http: HttpClient
  ) {
  }

  listar(): Observable<AprobacionResponse[]> {

    return this.http.get<AprobacionResponse[]>(
      this.apiUrl
    );
  }

  listarPendientes():
    Observable<AprobacionResponse[]> {

    return this.http.get<AprobacionResponse[]>(
      `${this.apiUrl}/pendientes`
    );
  }

  obtenerPorId(
    idAprobacion: number
  ): Observable<AprobacionResponse> {

    return this.http.get<AprobacionResponse>(
      `${this.apiUrl}/${idAprobacion}`
    );
  }

  solicitar(
    datos: SolicitudAprobacionRequest
  ): Observable<AprobacionResponse> {

    return this.http.post<AprobacionResponse>(
      `${this.apiUrl}/solicitar`,
      datos
    );
  }

  aprobar(
    idAprobacion: number,
    datos: ResponderAprobacionRequest
  ): Observable<AprobacionResponse> {

    return this.http.put<AprobacionResponse>(
      `${this.apiUrl}/${idAprobacion}/aprobar`,
      datos
    );
  }

  rechazar(
    idAprobacion: number,
    datos: ResponderAprobacionRequest
  ): Observable<AprobacionResponse> {

    return this.http.put<AprobacionResponse>(
      `${this.apiUrl}/${idAprobacion}/rechazar`,
      datos
    );
  }
}