import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface NotificacionResponse {
  idNotificacion: number;
  empresaId: number;
  usuarioId: number;

  titulo: string;
  mensaje: string;

  tipo: string | null;

  entidadTipo: string | null;
  entidadId: number | null;

  leida: boolean;

  fechaCreacion: string | null;
  fechaLectura: string | null;
}

export interface NotificacionRequest {
  usuarioId: number;

  titulo: string;
  mensaje: string;

  tipo: string | null;

  entidadTipo: string | null;
  entidadId: number | null;
}

@Injectable({
  providedIn: 'root'
})
export class NotificacionesService {

  private readonly apiUrl =
    'http://localhost:8080/api/notificaciones';

  constructor(
    private http: HttpClient
  ) {
  }

  listar():
    Observable<NotificacionResponse[]> {

    return this.http.get<
      NotificacionResponse[]
    >(
      this.apiUrl
    );
  }

  listarNoLeidas():
    Observable<NotificacionResponse[]> {

    return this.http.get<
      NotificacionResponse[]
    >(
      `${this.apiUrl}/no-leidas`
    );
  }

  obtenerPorId(
    idNotificacion: number
  ): Observable<NotificacionResponse> {

    return this.http.get<
      NotificacionResponse
    >(
      `${this.apiUrl}/${idNotificacion}`
    );
  }

  crear(
    notificacion: NotificacionRequest
  ): Observable<NotificacionResponse> {

    return this.http.post<
      NotificacionResponse
    >(
      this.apiUrl,
      notificacion
    );
  }

  marcarComoLeida(
    idNotificacion: number
  ): Observable<NotificacionResponse> {

    return this.http.put<
      NotificacionResponse
    >(
      `${this.apiUrl}/${idNotificacion}/leer`,
      {}
    );
  }
}