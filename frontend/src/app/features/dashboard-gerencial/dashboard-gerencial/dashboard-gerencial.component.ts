import {
  CommonModule
} from '@angular/common';

import {
  Component,
  OnInit
} from '@angular/core';

import {
  FormsModule
} from '@angular/forms';

import {
  forkJoin
} from 'rxjs';

import {
  ClienteResponse,
  ClientesService
} from '../../../core/services/clientes.service';

import {
  ProductoResponse,
  ProductosService
} from '../../../core/services/productos.service';

import {
  CotizacionResponse,
  CotizacionesService
} from '../../../core/services/cotizaciones.service';

import {
  DashboardGerencialAnalitica,
  DashboardGerencialCotizacionEstado,
  DashboardGerencialFiltros,
  DashboardGerencialPedidoMensual,
  DashboardGerencialProducto,
  DashboardGerencialService,
  DashboardGerencialVentaMensual
} from '../../../core/services/dashboard-gerencial.service';


interface VendedorFiltro {

  id: number;

  nombre: string;
}


@Component({
  selector:
    'app-dashboard-gerencial',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl:
    './dashboard-gerencial.component.html',

  styleUrl:
    './dashboard-gerencial.component.scss'
})
export class DashboardGerencialComponent
  implements OnInit {

  analitica:
    DashboardGerencialAnalitica | null =
      null;


  clientes:
    ClienteResponse[] = [];

  productos:
    ProductoResponse[] = [];

  vendedores:
    VendedorFiltro[] = [];


  filtroDesde: string =
    '';

  filtroHasta: string =
    '';

  filtroClienteId:
    number | null =
      null;

  filtroProductoId:
    number | null =
      null;

  filtroVendedorId:
    number | null =
      null;


  cargando: boolean =
    false;

  cargandoOpciones: boolean =
    false;

  error: string =
    '';


  constructor(
    private dashboardGerencialService:
      DashboardGerencialService,

    private clientesService:
      ClientesService,

    private productosService:
      ProductosService,

    private cotizacionesService:
      CotizacionesService
  ) {
  }


  ngOnInit(): void {

    this.cargarOpciones();

    this.cargarAnalitica();
  }


  // =====================================================
  // OPCIONES DE FILTROS
  // =====================================================

  cargarOpciones(): void {

    this.cargandoOpciones =
      true;


    forkJoin({

      clientes:
        this.clientesService
          .listar(),

      productos:
        this.productosService
          .listar(),

      cotizaciones:
        this.cotizacionesService
          .listar()

    }).subscribe({

      next: (
        respuesta
      ) => {

        this.clientes =
          respuesta.clientes;

        this.productos =
          respuesta.productos;

        this.vendedores =
          this.construirVendedores(
            respuesta.cotizaciones
          );

        this.cargandoOpciones =
          false;
      },


      error: (
        error
      ) => {

        console.error(
          'Error al cargar opciones del Dashboard Gerencial:',
          error
        );

        this.cargandoOpciones =
          false;
      }

    });
  }


  construirVendedores(
    cotizaciones:
      CotizacionResponse[]
  ): VendedorFiltro[] {

    const ids =
      new Set<number>();


    for (
      const cotizacion
      of cotizaciones
    ) {

      if (
        cotizacion.vendedorId !==
          null &&
        cotizacion.vendedorId !==
          undefined
      ) {

        ids.add(
          Number(
            cotizacion.vendedorId
          )
        );
      }
    }


    return Array
      .from(
        ids
      )
      .sort(
        (
          a,
          b
        ) =>
          a - b
      )
      .map(
        id => {

          return {
            id,
            nombre:
              `Vendedor #${id}`
          };

        }
      );
  }


  // =====================================================
  // ANALÍTICA
  // =====================================================

  cargarAnalitica(): void {

    this.cargando =
      true;

    this.error =
      '';


    const filtros:
      DashboardGerencialFiltros = {

      desde:
        this.filtroDesde ||
        null,

      hasta:
        this.filtroHasta ||
        null,

      clienteId:
        this.filtroClienteId,

      productoId:
        this.filtroProductoId,

      vendedorId:
        this.filtroVendedorId

    };


    this.dashboardGerencialService
      .obtenerAnalitica(
        filtros
      )
      .subscribe({

        next: (
          analitica
        ) => {

          this.analitica =
            analitica;

          this.cargando =
            false;
        },


        error: (
          error
        ) => {

          console.error(
            'Error al cargar Dashboard Gerencial:',
            error
          );

          this.cargando =
            false;

          this.error =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }


  aplicarFiltros(): void {

    if (
      this.filtroDesde &&
      this.filtroHasta &&
      this.filtroDesde >
        this.filtroHasta
    ) {

      this.error =
        'La fecha desde no puede ser posterior a la fecha hasta.';

      return;
    }


    this.cargarAnalitica();
  }


  limpiarFiltros(): void {

    this.filtroDesde =
      '';

    this.filtroHasta =
      '';

    this.filtroClienteId =
      null;

    this.filtroProductoId =
      null;

    this.filtroVendedorId =
      null;

    this.cargarAnalitica();
  }


  // =====================================================
  // CLIENTES
  // =====================================================

  obtenerNombreCliente(
    cliente:
      ClienteResponse
  ): string {

    if (
      cliente.nombreComercial
    ) {

      return cliente
        .nombreComercial;
    }


    if (
      cliente.razonSocial
    ) {

      return cliente
        .razonSocial;
    }


    return `Cliente #${cliente.idCliente}`;
  }


  // =====================================================
  // GRÁFICO VENTAS
  // =====================================================

  obtenerVentaMaxima(): number {

    if (
      !this.analitica ||
      this.analitica
        .ventasMensuales
        .length === 0
    ) {

      return 0;
    }


    return Math.max(
      ...this.analitica
        .ventasMensuales
        .map(
          item =>
            Number(
              item.ventas ?? 0
            )
        )
    );
  }


  obtenerAlturaVenta(
    item:
      DashboardGerencialVentaMensual
  ): number {

    const maximo =
      this.obtenerVentaMaxima();


    if (
      maximo <= 0
    ) {

      return 2;
    }


    return Math.max(
      3,
      (
        Number(
          item.ventas ?? 0
        ) /
        maximo
      ) * 100
    );
  }


  // =====================================================
  // GRÁFICO PEDIDOS
  // =====================================================

  obtenerPedidosMaximos(): number {

    if (
      !this.analitica ||
      this.analitica
        .pedidosMensuales
        .length === 0
    ) {

      return 0;
    }


    return Math.max(
      ...this.analitica
        .pedidosMensuales
        .map(
          item =>
            Number(
              item.pedidos ?? 0
            )
        )
    );
  }


  obtenerAlturaPedido(
    item:
      DashboardGerencialPedidoMensual
  ): number {

    const maximo =
      this.obtenerPedidosMaximos();


    if (
      maximo <= 0
    ) {

      return 2;
    }


    return Math.max(
      3,
      (
        Number(
          item.pedidos ?? 0
        ) /
        maximo
      ) * 100
    );
  }


  // =====================================================
  // TOP PRODUCTOS
  // =====================================================

  obtenerCantidadProductoMaxima():
    number {

    if (
      !this.analitica ||
      this.analitica
        .topProductos
        .length === 0
    ) {

      return 0;
    }


    return Math.max(
      ...this.analitica
        .topProductos
        .map(
          producto =>
            Number(
              producto
                .cantidadVendida ??
              0
            )
        )
    );
  }


  obtenerAnchoProducto(
    producto:
      DashboardGerencialProducto
  ): number {

    const maximo =
      this
        .obtenerCantidadProductoMaxima();


    if (
      maximo <= 0
    ) {

      return 0;
    }


    return (
      Number(
        producto
          .cantidadVendida ??
        0
      ) /
      maximo
    ) * 100;
  }


  // =====================================================
  // COTIZACIONES
  // =====================================================

  obtenerCantidadCotizacionMaxima():
    number {

    if (
      !this.analitica ||
      this.analitica
        .cotizacionesPorEstado
        .length === 0
    ) {

      return 0;
    }


    return Math.max(
      ...this.analitica
        .cotizacionesPorEstado
        .map(
          estado =>
            Number(
              estado.cantidad ??
              0
            )
        )
    );
  }


  obtenerAnchoCotizacion(
    estado:
      DashboardGerencialCotizacionEstado
  ): number {

    const maximo =
      this
        .obtenerCantidadCotizacionMaxima();


    if (
      maximo <= 0
    ) {

      return 0;
    }


    return (
      Number(
        estado.cantidad ??
        0
      ) /
      maximo
    ) * 100;
  }


  obtenerEstadoLegible(
    estado: string
  ): string {

    if (
      !estado
    ) {

      return 'Sin estado';
    }


    return estado
      .replace(
        /_/g,
        ' '
      )
      .toLowerCase()
      .replace(
        /\b\w/g,
        letra =>
          letra.toUpperCase()
      );
  }


  // =====================================================
  // FORMATOS
  // =====================================================

  formatearMoneda(
    valor:
      number |
      null |
      undefined
  ): string {

    return new Intl
      .NumberFormat(
        'es-PE',
        {
          style:
            'currency',

          currency:
            'PEN',

          minimumFractionDigits:
            2,

          maximumFractionDigits:
            2
        }
      )
      .format(
        Number(
          valor ?? 0
        )
      );
  }


  formatearNumero(
    valor:
      number |
      null |
      undefined
  ): string {

    return new Intl
      .NumberFormat(
        'es-PE',
        {
          maximumFractionDigits:
            2
        }
      )
      .format(
        Number(
          valor ?? 0
        )
      );
  }


  formatearPorcentaje(
    valor:
      number |
      null |
      undefined
  ): string {

    return `${Number(
      valor ?? 0
    ).toFixed(2)} %`;
  }


  obtenerMensajeError(
    error: any
  ): string {

    if (
      error?.error?.mensaje
    ) {

      return error
        .error
        .mensaje;
    }


    if (
      error?.error?.message
    ) {

      return error
        .error
        .message;
    }


    if (
      error?.status === 401
    ) {

      return 'Tu sesión ha vencido.';
    }


    if (
      error?.status === 403
    ) {

      return 'No tienes permiso para visualizar el Dashboard Gerencial.';
    }


    return 'No se pudo cargar la analítica gerencial.';
  }
}