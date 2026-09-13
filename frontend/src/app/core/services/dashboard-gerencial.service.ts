import {
  Injectable
} from '@angular/core';

import {
  HttpClient,
  HttpParams
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';


export interface DashboardGerencialResumen {

  ventasTotales: number;

  totalPedidos: number;

  ticketPromedio: number;

  tasaConversion: number;

  totalCotizaciones: number;
}


export interface DashboardGerencialVentaMensual {

  periodo: string;

  mes: string;

  ventas: number;

  pedidos: number;
}


export interface DashboardGerencialPedidoMensual {

  periodo: string;

  mes: string;

  pedidos: number;
}


export interface DashboardGerencialProducto {

  productoId: number;

  producto: string;

  cantidadVendida: number;

  ventas: number;
}


export interface DashboardGerencialCotizacionEstado {

  estado: string;

  cantidad: number;
}


export interface DashboardGerencialAnalitica {

  resumen:
    DashboardGerencialResumen;

  ventasMensuales:
    DashboardGerencialVentaMensual[];

  pedidosMensuales:
    DashboardGerencialPedidoMensual[];

  topProductos:
    DashboardGerencialProducto[];

  cotizacionesPorEstado:
    DashboardGerencialCotizacionEstado[];
}


export interface DashboardGerencialFiltros {

  desde?: string | null;

  hasta?: string | null;

  clienteId?: number | null;

  productoId?: number | null;

  vendedorId?: number | null;
}


@Injectable({
  providedIn: 'root'
})
export class DashboardGerencialService {

  private readonly apiUrl =
    'http://localhost:8080/api/dashboard-gerencial';


  constructor(
    private http: HttpClient
  ) {
  }


  obtenerAnalitica(
    filtros?:
      DashboardGerencialFiltros
  ): Observable<
    DashboardGerencialAnalitica
  > {

    let params =
      new HttpParams();


    if (
      filtros?.desde
    ) {

      params =
        params.set(
          'desde',
          filtros.desde
        );
    }


    if (
      filtros?.hasta
    ) {

      params =
        params.set(
          'hasta',
          filtros.hasta
        );
    }


    if (
      filtros?.clienteId
    ) {

      params =
        params.set(
          'clienteId',
          filtros.clienteId
            .toString()
        );
    }


    if (
      filtros?.productoId
    ) {

      params =
        params.set(
          'productoId',
          filtros.productoId
            .toString()
        );
    }


    if (
      filtros?.vendedorId
    ) {

      params =
        params.set(
          'vendedorId',
          filtros.vendedorId
            .toString()
        );
    }


    return this.http
      .get<
        DashboardGerencialAnalitica
      >(
        `${this.apiUrl}/analitica`,
        {
          params
        }
      );
  }
}