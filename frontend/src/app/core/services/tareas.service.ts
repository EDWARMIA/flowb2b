import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TareaRequest {
  pedidoId: number;
  responsableId: number | null;
  titulo: string;
  descripcion: string | null;
  tipo: string;
  prioridad: string;
  fechaLimite: string | null;
}

export interface TareaEstadoRequest {
  estado: string;
}

export interface TareaResponse {
  idTarea: number;
  empresaId: number;
  pedidoId: number;
  responsableId: number | null;
  titulo: string;
  descripcion: string | null;
  tipo: string;
  prioridad: string;
  estado: string;
  fechaLimite: string | null;
  fechaCompletada: string | null;
  fechaCreacion: string | null;
  fechaActualizacion: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TareasService {

  private readonly apiUrl =
    'http://localhost:8080/api/tareas';

  constructor(
    private http: HttpClient
  ) {
  }

  listar(): Observable<TareaResponse[]> {

    return this.http.get<TareaResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idTarea: number
  ): Observable<TareaResponse> {

    return this.http.get<TareaResponse>(
      `${this.apiUrl}/${idTarea}`
    );
  }

  listarPorPedido(
    pedidoId: number
  ): Observable<TareaResponse[]> {

    return this.http.get<TareaResponse[]>(
      `${this.apiUrl}/pedido/${pedidoId}`
    );
  }

  crear(
    tarea: TareaRequest
  ): Observable<TareaResponse> {

    return this.http.post<TareaResponse>(
      this.apiUrl,
      tarea
    );
  }

  cambiarEstado(
    idTarea: number,
    estado: TareaEstadoRequest
  ): Observable<TareaResponse> {

    return this.http.put<TareaResponse>(
      `${this.apiUrl}/${idTarea}/estado`,
      estado
    );
  }
}