import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { forkJoin } from 'rxjs';

import {
  TareaRequest,
  TareaResponse,
  TareasService
} from '../../../core/services/tareas.service';

import {
  PedidoResponse,
  PedidosService
} from '../../../core/services/pedidos.service';

import {
  AuthService
} from '../../../core/services/auth.service';

@Component({
  selector: 'app-tareas',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './tareas.component.html',
  styleUrl: './tareas.component.scss'
})
export class TareasComponent implements OnInit {

  tareas: TareaResponse[] = [];
  tareasFiltradas: TareaResponse[] = [];

  pedidos: PedidoResponse[] = [];

  cargando = true;
  guardando = false;
  cambiandoEstado = false;

  errorCarga = false;

  mensajeError = '';

  busqueda = '';

  filtroEstado = 'TODAS';

  mostrarFormulario = false;

  tareaSeleccionada: TareaResponse | null = null;

  puedeGestionar = false;

  formularioTarea: FormGroup;

  constructor(
    private tareasService: TareasService,
    private pedidosService: PedidosService,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {

    this.formularioTarea =
      this.formBuilder.group({

        pedidoId: [
          '',
          Validators.required
        ],

        titulo: [
          '',
          [
            Validators.required
          ]
        ],

        descripcion: [
          ''
        ],

        tipo: [
          'PREPARACION',
          Validators.required
        ],

        prioridad: [
          'MEDIA',
          Validators.required
        ],

        fechaLimite: [
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
      this.authService.obtenerPermisos();

    this.puedeGestionar =
      permisos.includes(
        'TAREA_GESTIONAR'
      );
  }

  cargarDatos(): void {

    this.cargando = true;
    this.errorCarga = false;
    this.mensajeError = '';

    forkJoin({
      tareas:
        this.tareasService.listar(),

      pedidos:
        this.pedidosService.listar()
    }).subscribe({

      next: (respuesta) => {

        this.tareas =
          respuesta.tareas;

        this.pedidos =
          respuesta.pedidos;

        this.aplicarFiltros();

        this.cargando = false;
      },

      error: (error) => {

        console.error(
          'Error al cargar tareas:',
          error
        );

        this.cargando = false;
        this.errorCarga = true;

        this.mensajeError =
          this.obtenerMensajeError(
            error
          );
      }

    });
  }

  abrirNuevaTarea(): void {

    if (!this.puedeGestionar) {
      return;
    }

    this.mensajeError = '';

    this.formularioTarea.reset({
      pedidoId: '',
      titulo: '',
      descripcion: '',
      tipo: 'PREPARACION',
      prioridad: 'MEDIA',
      fechaLimite: ''
    });

    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {

    if (this.guardando) {
      return;
    }

    this.mostrarFormulario = false;
    this.mensajeError = '';
  }

  guardarTarea(): void {

    if (
      this.formularioTarea.invalid ||
      this.guardando
    ) {

      this.formularioTarea
        .markAllAsTouched();

      return;
    }

    const valores =
      this.formularioTarea.value;

    const tarea: TareaRequest = {

      pedidoId:
        Number(
          valores.pedidoId
        ),

      responsableId:
        null,

      titulo:
        String(
          valores.titulo
        ).trim(),

      descripcion:
        this.normalizarTexto(
          valores.descripcion
        ),

      tipo:
        String(
          valores.tipo
        )
          .trim()
          .toUpperCase(),

      prioridad:
        String(
          valores.prioridad
        )
          .trim()
          .toUpperCase(),

      fechaLimite:
        this.convertirFechaParaBackend(
          valores.fechaLimite
        )

    };

    this.guardando = true;
    this.mensajeError = '';

    this.tareasService
      .crear(tarea)
      .subscribe({

        next: () => {

          this.guardando = false;
          this.mostrarFormulario = false;

          this.cargarDatos();
        },

        error: (error) => {

          console.error(
            'Error al crear tarea:',
            error
          );

          this.guardando = false;

          this.mensajeError =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }

  cambiarEstado(
    tarea: TareaResponse,
    nuevoEstado: string
  ): void {

    if (
      !this.puedeGestionar ||
      this.cambiandoEstado
    ) {
      return;
    }

    this.cambiandoEstado = true;
    this.mensajeError = '';

    this.tareasService
      .cambiarEstado(
        tarea.idTarea,
        {
          estado: nuevoEstado
        }
      )
      .subscribe({

        next: (actualizada) => {

          this.actualizarTareaLocal(
            actualizada
          );

          this.cambiandoEstado = false;
        },

        error: (error) => {

          console.error(
            'Error al cambiar estado:',
            error
          );

          this.cambiandoEstado = false;

          this.mensajeError =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }

  actualizarTareaLocal(
    tareaActualizada: TareaResponse
  ): void {

    this.tareas =
      this.tareas.map(
        tarea =>
          tarea.idTarea ===
          tareaActualizada.idTarea
            ? tareaActualizada
            : tarea
      );

    this.aplicarFiltros();

    if (
      this.tareaSeleccionada &&
      this.tareaSeleccionada.idTarea ===
        tareaActualizada.idTarea
    ) {

      this.tareaSeleccionada =
        tareaActualizada;
    }
  }

  verDetalle(
    tarea: TareaResponse
  ): void {

    this.tareaSeleccionada = tarea;
  }

  cerrarDetalle(): void {

    this.tareaSeleccionada = null;
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

    this.filtroEstado = estado;

    this.aplicarFiltros();
  }

  aplicarFiltros(): void {

    let resultado = [
      ...this.tareas
    ];

    if (
      this.filtroEstado !== 'TODAS'
    ) {

      resultado =
        resultado.filter(
          tarea =>
            tarea.estado ===
            this.filtroEstado
        );
    }

    if (this.busqueda) {

      resultado =
        resultado.filter(
          tarea => {

            const titulo =
              tarea.titulo
                ?.toLowerCase() ?? '';

            const descripcion =
              tarea.descripcion
                ?.toLowerCase() ?? '';

            const tipo =
              tarea.tipo
                ?.toLowerCase() ?? '';

            const pedido =
              this.obtenerCodigoPedido(
                tarea.pedidoId
              ).toLowerCase();

            return (
              titulo.includes(
                this.busqueda
              ) ||
              descripcion.includes(
                this.busqueda
              ) ||
              tipo.includes(
                this.busqueda
              ) ||
              pedido.includes(
                this.busqueda
              )
            );
          }
        );
    }

    resultado.sort(
      (a, b) => {

        const fechaA =
          a.fechaCreacion
            ? new Date(
                a.fechaCreacion
              ).getTime()
            : 0;

        const fechaB =
          b.fechaCreacion
            ? new Date(
                b.fechaCreacion
              ).getTime()
            : 0;

        return fechaB - fechaA;
      }
    );

    this.tareasFiltradas =
      resultado;
  }

  obtenerCodigoPedido(
    pedidoId: number
  ): string {

    const pedido =
      this.pedidos.find(
        item =>
          item.idPedido === pedidoId
      );

    return pedido?.codigo ??
      `Pedido #${pedidoId}`;
  }

  obtenerCantidadPendientes():
    number {

    return this.tareas.filter(
      tarea =>
        tarea.estado ===
        'PENDIENTE'
    ).length;
  }

  obtenerCantidadEnProceso():
    number {

    return this.tareas.filter(
      tarea =>
        tarea.estado ===
        'EN_PROCESO'
    ).length;
  }

  obtenerCantidadCompletadas():
    number {

    return this.tareas.filter(
      tarea =>
        tarea.estado ===
        'COMPLETADA'
    ).length;
  }

  obtenerClasePrioridad(
    prioridad: string
  ): string {

    return (
      prioridad ?? 'MEDIA'
    ).toLowerCase();
  }

  obtenerClaseEstado(
    estado: string
  ): string {

    return (
      estado ?? ''
    )
      .toLowerCase()
      .replace(
        /_/g,
        '-'
      );
  }

  prioridadLegible(
    prioridad: string
  ): string {

    switch (prioridad) {

      case 'BAJA':
        return 'Baja';

      case 'ALTA':
        return 'Alta';

      case 'URGENTE':
        return 'Urgente';

      default:
        return 'Media';
    }
  }

  estadoLegible(
    estado: string
  ): string {

    switch (estado) {

      case 'PENDIENTE':
        return 'Pendiente';

      case 'EN_PROCESO':
        return 'En proceso';

      case 'COMPLETADA':
        return 'Completada';

      case 'CANCELADA':
        return 'Cancelada';

      default:
        return estado;
    }
  }

  tipoLegible(
    tipo: string
  ): string {

    if (!tipo) {
      return '-';
    }

    return tipo
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

  formatearFecha(
    fecha: string | null
  ): string {

    if (!fecha) {
      return '-';
    }

    const valor =
      new Date(fecha);

    if (
      Number.isNaN(
        valor.getTime()
      )
    ) {
      return '-';
    }

    return valor.toLocaleString(
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

  convertirFechaParaBackend(
    fecha: string | null
  ): string | null {

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
      error?.status === 401
    ) {

      return 'Tu sesión ha vencido. Inicia sesión nuevamente.';
    }

    if (
      error?.status === 403
    ) {

      return 'No tienes permisos para realizar esta operación.';
    }

    return 'No se pudo procesar la tarea.';
  }
}