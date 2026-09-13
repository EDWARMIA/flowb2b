import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleCotizacionRequest {
  productoId: number;
  descripcion: string | null;
  cantidad: number;
  precioUnitario: number;
  porcentajeDescuento: number;
}

export interface CotizacionRequest {
  solicitudId: number;
  codigo: string;
  porcentajeDescuento: number;
  fechaVencimiento: string | null;
  observaciones: string | null;
  detalles: DetalleCotizacionRequest[];
}

export interface DetalleCotizacionResponse {
  idDetalle: number;
  productoId: number;
  descripcion: string | null;
  cantidad: number;
  precioUnitario: number;
  porcentajeDescuento: number;
  subtotal: number;
}

export interface CotizacionResponse {
  idCotizacion: number;
  empresaId: number;
  solicitudId: number;
  clienteId: number;
  vendedorId: number;
  codigo: string;
  subtotal: number;
  descuento: number;
  impuesto: number;
  total: number;
  porcentajeDescuento: number;
  estado: string;
  fechaEmision: string | null;
  fechaVencimiento: string | null;
  fechaAceptacion: string | null;
  observaciones: string | null;
  fechaCreacion: string | null;
  fechaActualizacion: string | null;
  detalles: DetalleCotizacionResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class CotizacionesService {

  private readonly apiUrl =
    'http://localhost:8080/api/cotizaciones';

  constructor(
    private http: HttpClient
  ) {
  }

  listar(): Observable<CotizacionResponse[]> {

    return this.http.get<CotizacionResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idCotizacion: number
  ): Observable<CotizacionResponse> {

    return this.http.get<CotizacionResponse>(
      `${this.apiUrl}/${idCotizacion}`
    );
  }

  crear(
    cotizacion: CotizacionRequest
  ): Observable<CotizacionResponse> {

    return this.http.post<CotizacionResponse>(
      this.apiUrl,
      cotizacion
    );
  }

  aceptar(
    idCotizacion: number
  ): Observable<CotizacionResponse> {

    return this.http.put<CotizacionResponse>(
      `${this.apiUrl}/${idCotizacion}/aceptar`,
      null
    );
  }
}