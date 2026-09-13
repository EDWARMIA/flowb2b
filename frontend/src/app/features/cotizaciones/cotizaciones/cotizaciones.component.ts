import {
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormArray,
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
  ProductoResponse,
  ProductosService
} from '../../../core/services/productos.service';

import {
  SolicitudResponse,
  SolicitudesService
} from '../../../core/services/solicitudes.service';

import {
  CotizacionRequest,
  CotizacionResponse,
  DetalleCotizacionRequest,
  CotizacionesService
} from '../../../core/services/cotizaciones.service';

import {
  AprobacionesService,
  SolicitudAprobacionRequest
} from '../../../core/services/aprobaciones.service';

@Component({
  selector: 'app-cotizaciones',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './cotizaciones.component.html',
  styleUrl: './cotizaciones.component.scss'
})
export class CotizacionesComponent
  implements OnInit {

  cotizaciones: CotizacionResponse[] = [];

  cotizacionesFiltradas:
    CotizacionResponse[] = [];

  solicitudes: SolicitudResponse[] = [];

  clientes: ClienteResponse[] = [];

  productos: ProductoResponse[] = [];

  cargando: boolean = true;

  guardando: boolean = false;

  aceptando: boolean = false;

  solicitandoAprobacion: boolean = false;

  errorCarga: boolean = false;

  mensajeError: string = '';

  busqueda: string = '';

  mostrarFormulario: boolean = false;

  mostrarDetalle: boolean = false;

  mostrarSolicitudAprobacion: boolean = false;

  cotizacionSeleccionada:
    CotizacionResponse | null = null;

  cotizacionParaAprobacion:
    CotizacionResponse | null = null;

  puedeCrear: boolean = false;

  puedeEditar: boolean = false;

  fechaEmisionActual: string = '';

  formularioCotizacion: FormGroup;

  formularioAprobacion: FormGroup;

  constructor(
    private cotizacionesService:
      CotizacionesService,

    private solicitudesService:
      SolicitudesService,

    private clientesService:
      ClientesService,

    private productosService:
      ProductosService,

    private aprobacionesService:
      AprobacionesService,

    private authService:
      AuthService,

    private formBuilder:
      FormBuilder
  ) {

    this.formularioCotizacion =
      this.formBuilder.group({

        solicitudId: [
          '',
          Validators.required
        ],

        codigo: [
          '',
          [
            Validators.required,
            Validators.maxLength(30)
          ]
        ],

        porcentajeDescuento: [
          0,
          [
            Validators.min(0),
            Validators.max(100)
          ]
        ],

        fechaVencimiento: [
          ''
        ],

        observaciones: [
          ''
        ],

        detalles:
          this.formBuilder.array([])

      });

    this.formularioAprobacion =
      this.formBuilder.group({

        motivo: [
          '',
          [
            Validators.maxLength(255)
          ]
        ]

      });
  }

  ngOnInit(): void {

    this.cargarPermisos();

    this.cargarDatos();
  }

  get detalles(): FormArray {

    return this.formularioCotizacion
      .get('detalles') as FormArray;
  }

  cargarPermisos(): void {

    const permisos =
      this.authService
        .obtenerPermisos();

    this.puedeCrear =
      permisos.includes(
        'COTIZACION_CREAR'
      );

    this.puedeEditar =
      permisos.includes(
        'COTIZACION_EDITAR'
      );
  }

  cargarDatos(): void {

    this.cargando = true;

    this.errorCarga = false;

    this.mensajeError = '';

    forkJoin({

      cotizaciones:
        this.cotizacionesService
          .listar(),

      solicitudes:
        this.solicitudesService
          .listar(),

      clientes:
        this.clientesService
          .listar(),

      productos:
        this.productosService
          .listar()

    }).subscribe({

      next: respuesta => {

        this.cotizaciones =
          respuesta.cotizaciones;

        this.cotizacionesFiltradas =
          [...respuesta.cotizaciones];

        this.solicitudes =
          respuesta.solicitudes;

        this.clientes =
          respuesta.clientes;

        this.productos =
          respuesta.productos;

        this.cargando =
          false;
      },

      error: error => {

        console.error(
          'Error al cargar cotizaciones:',
          error
        );

        this.errorCarga =
          true;

        this.cargando =
          false;

        if (error.status === 403) {

          this.mensajeError =
            'No tienes permisos suficientes para cargar Cotizaciones.';

        } else {

          this.mensajeError =
            'No se pudo cargar el módulo de cotizaciones.';
        }
      }

    });
  }

  abrirNuevaCotizacion(): void {

    this.mensajeError = '';

    this.fechaEmisionActual =
      this.obtenerFechaActual();

    this.formularioCotizacion.reset({

      solicitudId: '',
      codigo: '',
      porcentajeDescuento: 0,
      fechaVencimiento: '',
      observaciones: ''

    });

    this.detalles.clear();

    this.mostrarFormulario =
      true;
  }

  cambiarSolicitud(): void {

    const solicitudId =
      Number(
        this.formularioCotizacion
          .get('solicitudId')
          ?.value
      );

    this.detalles.clear();

    if (
      !solicitudId ||
      Number.isNaN(solicitudId)
    ) {
      return;
    }

    const solicitud =
      this.solicitudes.find(
        item =>
          item.idSolicitud ===
          solicitudId
      );

    if (!solicitud) {
      return;
    }

    for (
      const detalleSolicitud
      of solicitud.detalles
    ) {

      const producto =
        this.productos.find(
          item =>
            item.idProducto ===
            detalleSolicitud.productoId
        );

      this.detalles.push(
        this.crearDetalle(
          detalleSolicitud.productoId,
          detalleSolicitud.cantidad,
          producto?.precioBase ?? 0,
          producto?.nombre ?? null
        )
      );
    }
  }

  crearDetalle(
    productoId: number,
    cantidad: number,
    precioUnitario: number,
    descripcion: string | null
  ): FormGroup {

    return this.formBuilder.group({

      productoId: [
        productoId,
        Validators.required
      ],

      descripcion: [
        descripcion ?? ''
      ],

      cantidad: [
        cantidad,
        [
          Validators.required,
          Validators.min(0.01)
        ]
      ],

      precioUnitario: [
        precioUnitario,
        [
          Validators.required,
          Validators.min(0)
        ]
      ],

      porcentajeDescuento: [
        0,
        [
          Validators.min(0),
          Validators.max(100)
        ]
      ]

    });
  }

  cerrarFormulario(): void {

    if (this.guardando) {
      return;
    }

    this.mostrarFormulario =
      false;

    this.mensajeError =
      '';

    this.detalles.clear();
  }

  guardarCotizacion(): void {

    if (
      this.formularioCotizacion.invalid ||
      this.detalles.length === 0 ||
      this.guardando
    ) {

      this.formularioCotizacion
        .markAllAsTouched();

      return;
    }

    const valores =
      this.formularioCotizacion.value;

    const detallesRequest:
      DetalleCotizacionRequest[] =
      valores.detalles.map(
        (detalle: any) => {

          return {

            productoId:
              Number(
                detalle.productoId
              ),

            descripcion:
              this.normalizarTexto(
                detalle.descripcion
              ),

            cantidad:
              Number(
                detalle.cantidad
              ),

            precioUnitario:
              Number(
                detalle.precioUnitario
              ),

            porcentajeDescuento:
              Number(
                detalle.porcentajeDescuento ?? 0
              )

          };
        }
      );

    const cotizacion:
      CotizacionRequest = {

      solicitudId:
        Number(
          valores.solicitudId
        ),

      codigo:
        valores.codigo
          .trim(),

      porcentajeDescuento:
        Number(
          valores.porcentajeDescuento ?? 0
        ),

      fechaVencimiento:
        this.normalizarFecha(
          valores.fechaVencimiento
        ),

      observaciones:
        this.normalizarTexto(
          valores.observaciones
        ),

      detalles:
        detallesRequest

    };

    this.guardando =
      true;

    this.mensajeError =
      '';

    this.cotizacionesService
      .crear(
        cotizacion
      )
      .subscribe({

        next: () => {

          this.guardando =
            false;

          this.mostrarFormulario =
            false;

          this.detalles.clear();

          this.cargarDatos();
        },

        error: error => {

          console.error(
            'Error al crear cotización:',
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
    cotizacion: CotizacionResponse
  ): void {

    this.cotizacionSeleccionada =
      cotizacion;

    this.mostrarDetalle =
      true;
  }

  cerrarDetalle(): void {

    this.mostrarDetalle =
      false;

    this.cotizacionSeleccionada =
      null;
  }

  abrirSolicitudAprobacion(
    cotizacion: CotizacionResponse
  ): void {

    if (
      cotizacion.estado !== 'BORRADOR' ||
      !this.puedeEditar
    ) {
      return;
    }

    this.cotizacionParaAprobacion =
      cotizacion;

    this.mensajeError =
      '';

    this.formularioAprobacion.reset({

      motivo:
        'Requiere aprobación comercial'

    });

    this.mostrarDetalle =
      false;

    this.mostrarSolicitudAprobacion =
      true;
  }

  cerrarSolicitudAprobacion(): void {

    if (
      this.solicitandoAprobacion
    ) {
      return;
    }

    this.mostrarSolicitudAprobacion =
      false;

    this.cotizacionParaAprobacion =
      null;

    this.mensajeError =
      '';
  }

  enviarAprobacion(): void {

    if (
      !this.cotizacionParaAprobacion ||
      this.formularioAprobacion.invalid ||
      this.solicitandoAprobacion
    ) {
      return;
    }

    const motivo =
      this.normalizarTexto(
        this.formularioAprobacion
          .get('motivo')
          ?.value
      );

    const datos:
      SolicitudAprobacionRequest = {

      cotizacionId:
        this.cotizacionParaAprobacion
          .idCotizacion,

      motivo:
        motivo

    };

    this.solicitandoAprobacion =
      true;

    this.mensajeError =
      '';

    this.aprobacionesService
      .solicitar(
        datos
      )
      .subscribe({

        next: () => {

          this.solicitandoAprobacion =
            false;

          this.mostrarSolicitudAprobacion =
            false;

          this.cotizacionParaAprobacion =
            null;

          this.cargarDatos();
        },

        error: error => {

          console.error(
            'Error al solicitar aprobación:',
            error
          );

          this.solicitandoAprobacion =
            false;

          this.mensajeError =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }

  aceptarCotizacion(
    cotizacion: CotizacionResponse
  ): void {

    if (
      cotizacion.estado !== 'APROBADA' ||
      !this.puedeEditar ||
      this.aceptando
    ) {
      return;
    }

    const confirmar =
      window.confirm(
        `¿Confirmar que el cliente aceptó la cotización ${cotizacion.codigo}?`
      );

    if (!confirmar) {
      return;
    }

    this.aceptando =
      true;

    this.cotizacionesService
      .aceptar(
        cotizacion.idCotizacion
      )
      .subscribe({

        next: () => {

          this.aceptando =
            false;

          this.cerrarDetalle();

          this.cargarDatos();
        },

        error: error => {

          console.error(
            'Error al aceptar cotización:',
            error
          );

          this.aceptando =
            false;

          alert(
            this.obtenerMensajeError(
              error
            )
          );
        }

      });
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

    if (!this.busqueda) {

      this.cotizacionesFiltradas =
        [...this.cotizaciones];

      return;
    }

    this.cotizacionesFiltradas =
      this.cotizaciones.filter(
        cotizacion => {

          const codigo =
            cotizacion.codigo
              ?.toLowerCase() ?? '';

          const estado =
            cotizacion.estado
              ?.toLowerCase() ?? '';

          const cliente =
            this.obtenerNombreCliente(
              cotizacion.clienteId
            )
              .toLowerCase();

          const solicitud =
            this.obtenerCodigoSolicitud(
              cotizacion.solicitudId
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
            solicitud.includes(
              this.busqueda
            )
          );
        }
      );
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

  obtenerCodigoSolicitud(
    solicitudId: number
  ): string {

    const solicitud =
      this.solicitudes.find(
        item =>
          item.idSolicitud ===
          solicitudId
      );

    return (
      solicitud?.codigo ||
      `Solicitud #${solicitudId}`
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

  obtenerFechaCotizacion(
    cotizacion: CotizacionResponse
  ): string | null {

    return (
      cotizacion.fechaEmision ||
      cotizacion.fechaCreacion ||
      null
    );
  }

  obtenerFechaActual(): string {

    const ahora =
      new Date();

    return ahora.toLocaleString(
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

  estadoLegible(
    estado: string | null | undefined
  ): string {

    if (!estado) {
      return '-';
    }

    return estado.replace(
      /_/g,
      ' '
    );
  }

  calcularSubtotalLinea(
    indice: number
  ): number {

    const detalle =
      this.detalles.at(indice);

    const cantidad =
      Number(
        detalle.get('cantidad')
          ?.value ?? 0
      );

    const precio =
      Number(
        detalle.get('precioUnitario')
          ?.value ?? 0
      );

    const descuento =
      Number(
        detalle.get('porcentajeDescuento')
          ?.value ?? 0
      );

    const bruto =
      cantidad * precio;

    return bruto -
      (
        bruto *
        descuento /
        100
      );
  }

  calcularSubtotalPreview(): number {

    let subtotal = 0;

    for (
      let i = 0;
      i < this.detalles.length;
      i++
    ) {

      subtotal +=
        this.calcularSubtotalLinea(i);
    }

    return subtotal;
  }

  calcularDescuentoGeneralPreview(): number {

    const porcentaje =
      Number(
        this.formularioCotizacion
          .get('porcentajeDescuento')
          ?.value ?? 0
      );

    return (
      this.calcularSubtotalPreview() *
      porcentaje /
      100
    );
  }

  calcularTotalPreview(): number {

    return (
      this.calcularSubtotalPreview() -
      this.calcularDescuentoGeneralPreview()
    );
  }

  formatearMoneda(
    valor: number | null
  ): string {

    const numero =
      Number(valor ?? 0);

    return numero.toLocaleString(
      'es-PE',
      {
        style: 'currency',
        currency: 'PEN'
      }
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
      error?.error?.message
    ) {
      return error.error.message;
    }

    if (
      typeof error?.error === 'string'
    ) {
      return error.error;
    }

    if (
      error?.status === 403
    ) {

      return 'No tienes permiso para realizar esta acción.';
    }

    return 'Ocurrió un error al procesar la cotización.';
  }
}