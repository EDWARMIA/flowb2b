import {
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  forkJoin
} from 'rxjs';

import {
  AuthService
} from '../../../core/services/auth.service';

import {
  ClienteResponse,
  ClientesService
} from '../../../core/services/clientes.service';

import {
  CotizacionResponse,
  CotizacionesService
} from '../../../core/services/cotizaciones.service';

import {
  ProductoResponse,
  ProductosService
} from '../../../core/services/productos.service';

import {
  PedidoEstadoRequest,
  PedidoRequest,
  PedidoResponse,
  PedidosService
} from '../../../core/services/pedidos.service';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './pedidos.component.html',
  styleUrl: './pedidos.component.scss'
})
export class PedidosComponent
  implements OnInit {

  pedidos: PedidoResponse[] = [];

  pedidosFiltrados:
    PedidoResponse[] = [];

  cotizaciones: CotizacionResponse[] = [];

  clientes: ClienteResponse[] = [];

  productos: ProductoResponse[] = [];

  cotizacionesDisponibles:
    CotizacionResponse[] = [];

  cargando: boolean = true;

  guardando: boolean = false;

  procesandoEstado: boolean = false;

  reintentandoReserva: boolean = false;

  errorCarga: boolean = false;

  mensajeError: string = '';

  busqueda: string = '';

  filtroEstado: string = 'TODOS';

  mostrarFormulario: boolean = false;

  mostrarDetalle: boolean = false;

  mostrarCambioEstado: boolean = false;

  pedidoSeleccionado:
    PedidoResponse | null = null;

  cotizacionSeleccionada:
    CotizacionResponse | null = null;

  puedeGestionar: boolean = false;

  formularioPedido: FormGroup;

  formularioEstado: FormGroup;

  constructor(
    private pedidosService:
      PedidosService,

    private cotizacionesService:
      CotizacionesService,

    private clientesService:
      ClientesService,

    private productosService:
      ProductosService,

    private authService:
      AuthService,

    private formBuilder:
      FormBuilder
  ) {

    this.formularioPedido =
      this.formBuilder.group({

        cotizacionId: [
          '',
          Validators.required
        ],

        codigo: [
          '',
          Validators.required
        ],

        fechaEstimadaEntrega: [
          ''
        ],

        observaciones: [
          ''
        ]

      });

    this.formularioEstado =
      this.formBuilder.group({

        estado: [
          '',
          Validators.required
        ],

        comentario: [
          ''
        ]

      });
  }

  ngOnInit(): void {

    this.cargarPermisos();

    this.cargarDatos();
  }

  cargarPermisos(): void {

    const permisos =
      this.authService
        .obtenerPermisos();

    this.puedeGestionar =
      permisos.includes(
        'PEDIDO_GESTIONAR'
      );
  }

  cargarDatos(): void {

    this.cargando = true;

    this.errorCarga = false;

    this.mensajeError = '';

    forkJoin({

      pedidos:
        this.pedidosService
          .listar(),

      cotizaciones:
        this.cotizacionesService
          .listar(),

      clientes:
        this.clientesService
          .listar(),

      productos:
        this.productosService
          .listar()

    }).subscribe({

      next: respuesta => {

        this.pedidos =
          respuesta.pedidos;

        this.cotizaciones =
          respuesta.cotizaciones;

        this.clientes =
          respuesta.clientes;

        this.productos =
          respuesta.productos;

        this.calcularCotizacionesDisponibles();

        this.aplicarFiltros();

        this.cargando =
          false;
      },

      error: error => {

        console.error(
          'Error al cargar pedidos:',
          error
        );

        this.cargando =
          false;

        this.errorCarga =
          true;

        this.mensajeError =
          this.obtenerMensajeError(
            error
          );
      }

    });
  }

  calcularCotizacionesDisponibles(): void {

    const cotizacionesConPedido =
      new Set(
        this.pedidos.map(
          pedido =>
            pedido.cotizacionId
        )
      );

    this.cotizacionesDisponibles =
      this.cotizaciones.filter(
        cotizacion => {

          return (
            cotizacion.estado ===
              'ACEPTADA' &&
            !cotizacionesConPedido.has(
              cotizacion.idCotizacion
            )
          );
        }
      );
  }

  abrirNuevoPedido(): void {

    this.mensajeError = '';

    this.cotizacionSeleccionada =
      null;

    this.formularioPedido.reset({

      cotizacionId: '',
      codigo: '',
      fechaEstimadaEntrega: '',
      observaciones: ''

    });

    this.mostrarFormulario =
      true;
  }

  cambiarCotizacion(): void {

    const cotizacionId =
      Number(
        this.formularioPedido
          .get('cotizacionId')
          ?.value
      );

    if (
      !cotizacionId ||
      Number.isNaN(
        cotizacionId
      )
    ) {

      this.cotizacionSeleccionada =
        null;

      return;
    }

    this.cotizacionSeleccionada =
      this.cotizacionesDisponibles
        .find(
          cotizacion =>
            cotizacion.idCotizacion ===
            cotizacionId
        ) ?? null;
  }

  cerrarFormulario(): void {

    if (this.guardando) {
      return;
    }

    this.mostrarFormulario =
      false;

    this.cotizacionSeleccionada =
      null;

    this.mensajeError =
      '';
  }

  guardarPedido(): void {

    if (
      this.formularioPedido.invalid ||
      this.guardando
    ) {

      this.formularioPedido
        .markAllAsTouched();

      return;
    }

    const valores =
      this.formularioPedido.value;

    const pedido:
      PedidoRequest = {

      cotizacionId:
        Number(
          valores.cotizacionId
        ),

      codigo:
        valores.codigo
          .trim(),

      fechaEstimadaEntrega:
        this.normalizarFecha(
          valores.fechaEstimadaEntrega
        ),

      observaciones:
        this.normalizarTexto(
          valores.observaciones
        )

    };

    this.guardando =
      true;

    this.mensajeError =
      '';

    this.pedidosService
      .crear(
        pedido
      )
      .subscribe({

        next: () => {

          this.guardando =
            false;

          this.mostrarFormulario =
            false;

          this.cotizacionSeleccionada =
            null;

          this.cargarDatos();
        },

        error: error => {

          console.error(
            'Error al crear pedido:',
            error
          );

          this.guardando =
            false;

          this.mensajeError =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }

  verDetalle(
    pedido: PedidoResponse
  ): void {

    this.pedidoSeleccionado =
      pedido;

    this.mensajeError =
      '';

    this.mostrarDetalle =
      true;
  }

  cerrarDetalle(): void {

    if (
      this.reintentandoReserva
    ) {
      return;
    }

    this.mostrarDetalle =
      false;

    this.pedidoSeleccionado =
      null;

    this.mensajeError =
      '';
  }

  tienePendientes(
    pedido: PedidoResponse
  ): boolean {

    return pedido.detalles.some(
      detalle =>
        Number(
          detalle.cantidadPendiente
        ) > 0
    );
  }

  obtenerTotalPendiente(
    pedido: PedidoResponse
  ): number {

    return pedido.detalles.reduce(
      (
        total,
        detalle
      ) => {

        return total +
          Number(
            detalle.cantidadPendiente ?? 0
          );
      },
      0
    );
  }

  puedeReintentarReserva(
    pedido: PedidoResponse
  ): boolean {

    if (
      !this.puedeGestionar
    ) {
      return false;
    }

    if (
      pedido.estado !== 'CREADO' &&
      pedido.estado !==
        'EN_PREPARACION'
    ) {
      return false;
    }

    return this.tienePendientes(
      pedido
    );
  }

  reintentarReserva(
    pedido: PedidoResponse
  ): void {

    if (
      !this.puedeReintentarReserva(
        pedido
      ) ||
      this.reintentandoReserva
    ) {
      return;
    }

    this.reintentandoReserva =
      true;

    this.mensajeError =
      '';

    this.pedidosService
      .reintentarReserva(
        pedido.idPedido
      )
      .subscribe({

        next: pedidoActualizado => {

          this.reintentandoReserva =
            false;

          this.actualizarPedidoLocal(
            pedidoActualizado
          );

          if (
            this.mostrarDetalle
          ) {

            this.pedidoSeleccionado =
              pedidoActualizado;
          }
        },

        error: error => {

          console.error(
            'Error al reintentar reserva:',
            error
          );

          this.reintentandoReserva =
            false;

          this.mensajeError =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }

  actualizarPedidoLocal(
    pedidoActualizado:
      PedidoResponse
  ): void {

    this.pedidos =
      this.pedidos.map(
        pedido => {

          if (
            pedido.idPedido ===
            pedidoActualizado.idPedido
          ) {

            return pedidoActualizado;
          }

          return pedido;
        }
      );

    this.aplicarFiltros();
  }

  abrirCambioEstado(
    pedido: PedidoResponse,
    estado?: string
  ): void {

    if (
      !this.puedeGestionar
    ) {
      return;
    }

    this.pedidoSeleccionado =
      pedido;

    this.mensajeError =
      '';

    this.formularioEstado.reset({

      estado:
        estado ?? '',

      comentario:
        ''

    });

    this.mostrarDetalle =
      false;

    this.mostrarCambioEstado =
      true;
  }

  cerrarCambioEstado(): void {

    if (
      this.procesandoEstado
    ) {
      return;
    }

    this.mostrarCambioEstado =
      false;

    this.pedidoSeleccionado =
      null;

    this.mensajeError =
      '';
  }

  confirmarCambioEstado(): void {

    if (
      !this.pedidoSeleccionado ||
      this.formularioEstado.invalid ||
      this.procesandoEstado
    ) {
      return;
    }

    const valores =
      this.formularioEstado.value;

    const datos:
      PedidoEstadoRequest = {

      estado:
        valores.estado,

      comentario:
        this.normalizarTexto(
          valores.comentario
        )

    };

    this.procesandoEstado =
      true;

    this.mensajeError =
      '';

    this.pedidosService
      .cambiarEstado(
        this.pedidoSeleccionado
          .idPedido,
        datos
      )
      .subscribe({

        next: () => {

          this.procesandoEstado =
            false;

          this.mostrarCambioEstado =
            false;

          this.pedidoSeleccionado =
            null;

          this.cargarDatos();
        },

        error: error => {

          console.error(
            'Error al cambiar estado:',
            error
          );

          this.procesandoEstado =
            false;

          this.mensajeError =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }

  obtenerEstadosPermitidos(
    pedido: PedidoResponse
  ): string[] {

    switch (
      pedido.estado
    ) {

      case 'CREADO':

        return [
          'EN_PREPARACION',
          'CANCELADO'
        ];

      case 'EN_PREPARACION':

        return [
          'DESPACHADO',
          'CANCELADO'
        ];

      case 'DESPACHADO':

        return [
          'ENTREGADO'
        ];

      default:

        return [];
    }
  }

  puedeCambiarEstado(
    pedido: PedidoResponse
  ): boolean {

    return (
      this.puedeGestionar &&
      this.obtenerEstadosPermitidos(
        pedido
      ).length > 0
    );
  }

  obtenerSiguienteEstado(
    pedido: PedidoResponse
  ): string | null {

    switch (
      pedido.estado
    ) {

      case 'CREADO':

        return 'EN_PREPARACION';

      case 'EN_PREPARACION':

        return 'DESPACHADO';

      case 'DESPACHADO':

        return 'ENTREGADO';

      default:

        return null;
    }
  }

  obtenerTextoAccion(
    pedido: PedidoResponse
  ): string {

    switch (
      pedido.estado
    ) {

      case 'CREADO':

        return 'Iniciar preparación';

      case 'EN_PREPARACION':

        return 'Despachar';

      case 'DESPACHADO':

        return 'Marcar entregado';

      default:

        return 'Cambiar estado';
    }
  }

  buscar(
    evento: Event
  ): void {

    const input =
      evento.target as HTMLInputElement;

    this.busqueda =
      input.value
        .trim()
        .toLowerCase();

    this.aplicarFiltros();
  }

  cambiarFiltroEstado(
    estado: string
  ): void {

    this.filtroEstado =
      estado;

    this.aplicarFiltros();
  }

  aplicarFiltros(): void {

    let resultado =
      [...this.pedidos];

    if (
      this.filtroEstado !==
      'TODOS'
    ) {

      resultado =
        resultado.filter(
          pedido =>
            pedido.estado ===
            this.filtroEstado
        );
    }

    if (
      this.busqueda
    ) {

      resultado =
        resultado.filter(
          pedido => {

            const codigo =
              pedido.codigo
                ?.toLowerCase() ?? '';

            const estado =
              pedido.estado
                ?.toLowerCase() ?? '';

            const cliente =
              this.obtenerNombreCliente(
                pedido.clienteId
              )
                .toLowerCase();

            const cotizacion =
              this.obtenerCodigoCotizacion(
                pedido.cotizacionId
              )
                .toLowerCase();

            return (
              codigo.includes(
                this.busqueda
              ) ||
              estado.includes(
                this.busqueda
              ) ||
              cliente.includes(
                this.busqueda
              ) ||
              cotizacion.includes(
                this.busqueda
              )
            );
          }
        );
    }

    this.pedidosFiltrados =
      resultado;
  }

  obtenerNombreCliente(
    clienteId: number
  ): string {

    const cliente =
      this.clientes.find(
        item =>
          item.idCliente ===
          clienteId
      );

    if (!cliente) {

      return `Cliente #${clienteId}`;
    }

    return (
      cliente.nombreComercial ||
      cliente.razonSocial
    );
  }

  obtenerCodigoCotizacion(
    cotizacionId: number
  ): string {

    const cotizacion =
      this.cotizaciones.find(
        item =>
          item.idCotizacion ===
          cotizacionId
      );

    return (
      cotizacion?.codigo ||
      `Cotización #${cotizacionId}`
    );
  }

  obtenerNombreProducto(
    productoId: number
  ): string {

    const producto =
      this.productos.find(
        item =>
          item.idProducto ===
          productoId
      );

    return (
      producto?.nombre ||
      `Producto #${productoId}`
    );
  }

  obtenerCodigoProducto(
    productoId: number
  ): string {

    const producto =
      this.productos.find(
        item =>
          item.idProducto ===
          productoId
      );

    return producto?.codigo || '';
  }

  obtenerNombreClienteCotizacion(
    cotizacion:
      CotizacionResponse
  ): string {

    return this.obtenerNombreCliente(
      cotizacion.clienteId
    );
  }

  estadoLegible(
    estado: string
  ): string {

    return estado.replace(
      /_/g,
      ' '
    );
  }

  obtenerFechaPedido(
    pedido: PedidoResponse
  ): string | null {

    return (
      pedido.fechaPedido ||
      pedido.fechaCreacion ||
      null
    );
  }

  formatearFecha(
    fecha: string | null
  ): string {

    if (!fecha) {
      return '-';
    }

    const fechaObjeto =
      new Date(fecha);

    if (
      Number.isNaN(
        fechaObjeto.getTime()
      )
    ) {

      return fecha;
    }

    return fechaObjeto
      .toLocaleString(
        'es-PE',
        {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        }
      );
  }

  formatearMoneda(
    valor: number | null
  ): string {

    const numero =
      Number(
        valor ?? 0
      );

    return numero
      .toLocaleString(
        'es-PE',
        {
          style: 'currency',
          currency: 'PEN'
        }
      );
  }

  formatearCantidad(
    valor: number | null
  ): string {

    return Number(
      valor ?? 0
    ).toLocaleString(
      'es-PE',
      {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
      }
    );
  }

  normalizarFecha(
    valor: unknown
  ): string | null {

    if (
      typeof valor !== 'string'
    ) {
      return null;
    }

    const fecha =
      valor.trim();

    if (!fecha) {
      return null;
    }

    if (
      fecha.length === 16
    ) {

      return `${fecha}:00`;
    }

    return fecha;
  }

  normalizarTexto(
    valor: unknown
  ): string | null {

    if (
      typeof valor !== 'string'
    ) {
      return null;
    }

    const texto =
      valor.trim();

    return texto.length > 0
      ? texto
      : null;
  }

  obtenerMensajeError(
    error: any
  ): string {

    if (
      error?.error?.mensaje
    ) {

      return error.error.mensaje;
    }

    if (
      error?.error?.message
    ) {

      return error.error.message;
    }

    if (
      typeof error?.error ===
      'string'
    ) {

      return error.error;
    }

    if (
      error?.status === 403
    ) {

      return 'No tienes permiso para gestionar pedidos.';
    }

    if (
      error?.status === 401
    ) {

      return 'Tu sesión ha vencido. Inicia sesión nuevamente.';
    }

    return 'No se pudo procesar el pedido.';
  }
}