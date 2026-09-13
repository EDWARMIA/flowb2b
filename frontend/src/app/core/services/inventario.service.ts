import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventarioRequest {
  productoId: number;
  stockActual: number;
  stockReservado: number;
  stockMinimo: number;
}

export interface InventarioResponse {
  idInventario: number;
  empresaId: number;
  productoId: number;

  stockActual: number;
  stockReservado: number;
  stockMinimo: number;
  stockDisponible: number;

  fechaActualizacion: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class InventarioService {

  private readonly apiUrl =
    'http://localhost:8080/api/inventario';

  constructor(
    private http: HttpClient
  ) {
  }

  listar():
    Observable<InventarioResponse[]> {

    return this.http.get<InventarioResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idInventario: number
  ): Observable<InventarioResponse> {

    return this.http.get<InventarioResponse>(
      `${this.apiUrl}/${idInventario}`
    );
  }

  obtenerPorProducto(
    idProducto: number
  ): Observable<InventarioResponse> {

    return this.http.get<InventarioResponse>(
      `${this.apiUrl}/producto/${idProducto}`
    );
  }

  actualizar(
    idInventario: number,
    datos: InventarioRequest
  ): Observable<InventarioResponse> {

    return this.http.put<InventarioResponse>(
      `${this.apiUrl}/${idInventario}`,
      datos
    );
  }
}