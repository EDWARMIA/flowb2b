import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PedidoRequest {
  cotizacionId: number;
  codigo: string;
  fechaEstimadaEntrega: string | null;
  observaciones: string | null;
}

export interface PedidoEstadoRequest {
  estado: string;
  comentario: string | null;
}

export interface PedidoDetalleResponse {
  idDetalle: number;
  productoId: number;
  descripcion: string | null;
  cantidad: number;
  precioUnitario: number;
  cantidadReservada: number;
  cantidadPendiente: number;
  subtotal: number;
}

export interface PedidoResponse {
  idPedido: number;
  empresaId: number;
  cotizacionId: number;
  clienteId: number;
  codigo: string;
  estado: string;

  subtotal: number;
  descuento: number;
  impuesto: number;
  total: number;

  fechaPedido: string | null;
  fechaEstimadaEntrega: string | null;
  fechaEntrega: string | null;

  observaciones: string | null;

  fechaCreacion: string | null;
  fechaActualizacion: string | null;

  detalles: PedidoDetalleResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class PedidosService {

  private readonly apiUrl =
    'http://localhost:8080/api/pedidos';

  constructor(
    private http: HttpClient
  ) {
  }

  listar(): Observable<PedidoResponse[]> {

    return this.http.get<PedidoResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idPedido: number
  ): Observable<PedidoResponse> {

    return this.http.get<PedidoResponse>(
      `${this.apiUrl}/${idPedido}`
    );
  }

  crear(
    pedido: PedidoRequest
  ): Observable<PedidoResponse> {

    return this.http.post<PedidoResponse>(
      this.apiUrl,
      pedido
    );
  }

  reintentarReserva(
    idPedido: number
  ): Observable<PedidoResponse> {

    return this.http.put<PedidoResponse>(
      `${this.apiUrl}/${idPedido}/reintentar-reserva`,
      {}
    );
  }

  cambiarEstado(
    idPedido: number,
    datos: PedidoEstadoRequest
  ): Observable<PedidoResponse> {

    return this.http.put<PedidoResponse>(
      `${this.apiUrl}/${idPedido}/estado`,
      datos
    );
  }
}