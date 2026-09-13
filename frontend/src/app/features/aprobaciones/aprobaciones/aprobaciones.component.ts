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
  ReactiveFormsModule
} from '@angular/forms';

import {
  forkJoin
} from 'rxjs';

import {
  AuthService
} from '../../../core/services/auth.service';

import {
  AprobacionResponse,
  AprobacionesService,
  ResponderAprobacionRequest
} from '../../../core/services/aprobaciones.service';

import {
  CotizacionResponse,
  CotizacionesService
} from '../../../core/services/cotizaciones.service';

import {
  ClienteResponse,
  ClientesService
} from '../../../core/services/clientes.service';

@Component({
  selector: 'app-aprobaciones',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './aprobaciones.component.html',
  styleUrl: './aprobaciones.component.scss'
})
export class AprobacionesComponent
  implements OnInit {

  aprobaciones: AprobacionResponse[] = [];

  aprobacionesFiltradas:
    AprobacionResponse[] = [];

  cotizaciones: CotizacionResponse[] = [];

  clientes: ClienteResponse[] = [];

  cargando: boolean = true;

  procesando: boolean = false;

  errorCarga: boolean = false;

  mensajeError: string = '';

  busqueda: string = '';

  filtroEstado: string = 'TODAS';

  mostrarDetalle: boolean = false;

  mostrarRespuesta: boolean = false;

  tipoRespuesta:
    'APROBAR' | 'RECHAZAR' | null = null;

  aprobacionSeleccionada:
    AprobacionResponse | null = null;

  puedeAprobar: boolean = false;

  idUsuarioActual: number | null = null;

  formularioRespuesta: FormGroup;

  constructor(
    private aprobacionesService:
      AprobacionesService,

    private cotizacionesService:
      CotizacionesService,

    private clientesService:
      ClientesService,

    private authService:
      AuthService,

    private formBuilder:
      FormBuilder
  ) {

    this.formularioRespuesta =
      this.formBuilder.group({

        comentario: [
          ''
        ]

      });
  }

  ngOnInit(): void {

    this.cargarPermisos();

    this.obtenerUsuarioActual();

    this.cargarDatos();
  }

  cargarPermisos(): void {

    const permisos =
      this.authService
        .obtenerPermisos();

    this.puedeAprobar =
      permisos.includes(
        'COTIZACION_APROBAR'
      );
  }

  obtenerUsuarioActual(): void {

    const usuario =
      this.authService
        .obtenerUsuario();

    if (!usuario) {
      return;
    }

    const usuarioTemporal =
      usuario as any;

    if (
      usuarioTemporal.idUsuario !==
      undefined &&
      usuarioTemporal.idUsuario !==
      null
    ) {

      this.idUsuarioActual =
        Number(
          usuarioTemporal.idUsuario
        );
    }
  }

  cargarDatos(): void {

    this.cargando = true;

    this.errorCarga = false;

    this.mensajeError = '';

    forkJoin({

      aprobaciones:
        this.aprobacionesService
          .listar(),

      cotizaciones:
        this.cotizacionesService
          .listar(),

      clientes:
        this.clientesService
          .listar()

    }).subscribe({

      next: respuesta => {

        this.aprobaciones =
          respuesta.aprobaciones;

        this.cotizaciones =
          respuesta.cotizaciones;

        this.clientes =
          respuesta.clientes;

        this.aplicarFiltros();

        this.cargando =
          false;
      },

      error: error => {

        console.error(
          'Error al cargar aprobaciones:',
          error
        );

        this.cargando =
          false;

        this.errorCarga =
          true;

        if (error.status === 403) {

          this.mensajeError =
            'No tienes permiso para acceder a Aprobaciones.';

        } else {

          this.mensajeError =
            'No se pudo cargar el módulo de aprobaciones.';
        }
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
      [...this.aprobaciones];

    if (
      this.filtroEstado !== 'TODAS'
    ) {

      resultado =
        resultado.filter(
          aprobacion =>
            aprobacion.estado ===
            this.filtroEstado
        );
    }

    if (this.busqueda) {

      resultado =
        resultado.filter(
          aprobacion => {

            const cotizacion =
              this.obtenerCotizacion(
                aprobacion.cotizacionId
              );

            const codigoCotizacion =
              cotizacion?.codigo
                ?.toLowerCase() ?? '';

            const cliente =
              cotizacion
                ? this.obtenerNombreCliente(
                    cotizacion.clienteId
                  ).toLowerCase()
                : '';

            const motivo =
              aprobacion.motivo
                ?.toLowerCase() ?? '';

            const estado =
              aprobacion.estado
                ?.toLowerCase() ?? '';

            return (
              codigoCotizacion.includes(
                this.busqueda
              ) ||
              cliente.includes(
                this.busqueda
              ) ||
              motivo.includes(
                this.busqueda
              ) ||
              estado.includes(
                this.busqueda
              )
            );
          }
        );
    }

    this.aprobacionesFiltradas =
      resultado;
  }

  obtenerCotizacion(
    cotizacionId: number
  ): CotizacionResponse | undefined {

    return this.cotizaciones.find(
      cotizacion =>
        cotizacion.idCotizacion ===
        cotizacionId
    );
  }

  obtenerCodigoCotizacion(
    cotizacionId: number
  ): string {

    const cotizacion =
      this.obtenerCotizacion(
        cotizacionId
      );

    return (
      cotizacion?.codigo ||
      `Cotización #${cotizacionId}`
    );
  }

  obtenerTotalCotizacion(
    cotizacionId: number
  ): number {

    const cotizacion =
      this.obtenerCotizacion(
        cotizacionId
      );

    return cotizacion?.total ?? 0;
  }

  obtenerClienteCotizacion(
    cotizacionId: number
  ): string {

    const cotizacion =
      this.obtenerCotizacion(
        cotizacionId
      );

    if (!cotizacion) {

      return 'Cliente no disponible';
    }

    return this.obtenerNombreCliente(
      cotizacion.clienteId
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

  puedeResponder(
    aprobacion: AprobacionResponse
  ): boolean {

    if (
      !this.puedeAprobar
    ) {
      return false;
    }

    if (
      aprobacion.estado !==
      'PENDIENTE'
    ) {
      return false;
    }

    if (
      this.idUsuarioActual !== null &&
      aprobacion.solicitanteId ===
      this.idUsuarioActual
    ) {
      return false;
    }

    return true;
  }

  esPropia(
    aprobacion: AprobacionResponse
  ): boolean {

    return (
      this.idUsuarioActual !== null &&
      aprobacion.solicitanteId ===
      this.idUsuarioActual
    );
  }

  verDetalle(
    aprobacion: AprobacionResponse
  ): void {

    this.aprobacionSeleccionada =
      aprobacion;

    this.mostrarDetalle =
      true;
  }

  cerrarDetalle(): void {

    this.mostrarDetalle =
      false;

    this.aprobacionSeleccionada =
      null;
  }

  abrirRespuesta(
    aprobacion: AprobacionResponse,
    tipo: 'APROBAR' | 'RECHAZAR'
  ): void {

    if (
      !this.puedeResponder(
        aprobacion
      )
    ) {
      return;
    }

    this.aprobacionSeleccionada =
      aprobacion;

    this.tipoRespuesta =
      tipo;

    this.mensajeError =
      '';

    this.formularioRespuesta.reset({

      comentario: ''

    });

    this.mostrarDetalle =
      false;

    this.mostrarRespuesta =
      true;
  }

  cerrarRespuesta(): void {

    if (this.procesando) {
      return;
    }

    this.mostrarRespuesta =
      false;

    this.tipoRespuesta =
      null;

    this.aprobacionSeleccionada =
      null;

    this.mensajeError =
      '';
  }

  confirmarRespuesta(): void {

    if (
      !this.aprobacionSeleccionada ||
      !this.tipoRespuesta ||
      this.procesando
    ) {
      return;
    }

    const comentario =
      this.normalizarTexto(
        this.formularioRespuesta
          .get('comentario')
          ?.value
      );

    const datos:
      ResponderAprobacionRequest = {

      comentario:
        comentario

    };

    this.procesando =
      true;

    this.mensajeError =
      '';

    const peticion =
      this.tipoRespuesta ===
      'APROBAR'

        ? this.aprobacionesService
            .aprobar(
              this.aprobacionSeleccionada
                .idAprobacion,
              datos
            )

        : this.aprobacionesService
            .rechazar(
              this.aprobacionSeleccionada
                .idAprobacion,
              datos
            );

    peticion.subscribe({

      next: () => {

        this.procesando =
          false;

        this.mostrarRespuesta =
          false;

        this.tipoRespuesta =
          null;

        this.aprobacionSeleccionada =
          null;

        this.cargarDatos();
      },

      error: error => {

        console.error(
          'Error al responder aprobación:',
          error
        );

        this.procesando =
          false;

        this.mensajeError =
          this.obtenerMensajeError(
            error
          );
      }

    });
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

    const objetoFecha =
      new Date(fecha);

    if (
      Number.isNaN(
        objetoFecha.getTime()
      )
    ) {
      return fecha;
    }

    return objetoFecha
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

      return 'No tienes permiso para responder esta aprobación.';
    }

    return 'No se pudo procesar la aprobación.';
  }
}