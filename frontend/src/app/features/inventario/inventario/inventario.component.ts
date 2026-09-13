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
  InventarioRequest,
  InventarioResponse,
  InventarioService
} from '../../../core/services/inventario.service';

import {
  ProductoResponse,
  ProductosService
} from '../../../core/services/productos.service';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './inventario.component.html',
  styleUrl: './inventario.component.scss'
})
export class InventarioComponent
  implements OnInit {

  inventarios: InventarioResponse[] = [];

  inventariosFiltrados:
    InventarioResponse[] = [];

  productos: ProductoResponse[] = [];

  cargando: boolean = true;

  guardando: boolean = false;

  errorCarga: boolean = false;

  mensajeError: string = '';

  busqueda: string = '';

  filtroStock: string = 'TODOS';

  puedeEditar: boolean = false;

  mostrarEdicion: boolean = false;

  inventarioSeleccionado:
    InventarioResponse | null = null;

  formularioInventario: FormGroup;

  constructor(
    private inventarioService:
      InventarioService,

    private productosService:
      ProductosService,

    private authService:
      AuthService,

    private formBuilder:
      FormBuilder
  ) {

    this.formularioInventario =
      this.formBuilder.group({

        stockActual: [
          0,
          [
            Validators.required,
            Validators.min(0)
          ]
        ],

        stockReservado: [
          {
            value: 0,
            disabled: true
          }
        ],

        stockMinimo: [
          0,
          [
            Validators.required,
            Validators.min(0)
          ]
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

    this.puedeEditar =
      permisos.includes(
        'INVENTARIO_EDITAR'
      );
  }

  cargarDatos(): void {

    this.cargando = true;

    this.errorCarga = false;

    this.mensajeError = '';

    forkJoin({

      inventarios:
        this.inventarioService
          .listar(),

      productos:
        this.productosService
          .listar()

    }).subscribe({

      next: respuesta => {

        this.inventarios =
          respuesta.inventarios;

        this.productos =
          respuesta.productos;

        this.aplicarFiltros();

        this.cargando =
          false;
      },

      error: error => {

        console.error(
          'Error al cargar inventario:',
          error
        );

        this.cargando =
          false;

        this.errorCarga =
          true;

        if (
          error.status === 403
        ) {

          this.mensajeError =
            'No tienes permiso para acceder al inventario.';

        } else {

          this.mensajeError =
            'No se pudo cargar el módulo de inventario.';
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

  cambiarFiltroStock(
    filtro: string
  ): void {

    this.filtroStock =
      filtro;

    this.aplicarFiltros();
  }

  aplicarFiltros(): void {

    let resultado =
      [...this.inventarios];

    if (
      this.filtroStock ===
      'BAJO'
    ) {

      resultado =
        resultado.filter(
          inventario =>
            Number(
              inventario.stockDisponible
            ) <=
            Number(
              inventario.stockMinimo
            )
        );
    }

    if (
      this.filtroStock ===
      'SIN_STOCK'
    ) {

      resultado =
        resultado.filter(
          inventario =>
            Number(
              inventario.stockDisponible
            ) <= 0
        );
    }

    if (
      this.filtroStock ===
      'RESERVADO'
    ) {

      resultado =
        resultado.filter(
          inventario =>
            Number(
              inventario.stockReservado
            ) > 0
        );
    }

    if (
      this.busqueda
    ) {

      resultado =
        resultado.filter(
          inventario => {

            const producto =
              this.obtenerProducto(
                inventario.productoId
              );

            const nombre =
              producto?.nombre
                ?.toLowerCase() ?? '';

            const codigo =
              producto?.codigo
                ?.toLowerCase() ?? '';

            const idProducto =
              String(
                inventario.productoId
              );

            return (
              nombre.includes(
                this.busqueda
              ) ||
              codigo.includes(
                this.busqueda
              ) ||
              idProducto.includes(
                this.busqueda
              )
            );
          }
        );
    }

    this.inventariosFiltrados =
      resultado;
  }

  obtenerProducto(
    productoId: number
  ): ProductoResponse | undefined {

    return this.productos.find(
      producto =>
        producto.idProducto ===
        productoId
    );
  }

  obtenerNombreProducto(
    productoId: number
  ): string {

    const producto =
      this.obtenerProducto(
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
      this.obtenerProducto(
        productoId
      );

    return (
      producto?.codigo ||
      `#${productoId}`
    );
  }

  obtenerUnidadMedida(
    productoId: number
  ): string {

    const producto =
      this.obtenerProducto(
        productoId
      );

    return (
      producto?.unidadMedida ||
      '-'
    );
  }

  obtenerEstadoStock(
    inventario: InventarioResponse
  ): string {

    const disponible =
      Number(
        inventario.stockDisponible
      );

    const minimo =
      Number(
        inventario.stockMinimo
      );

    if (
      disponible <= 0
    ) {
      return 'SIN STOCK';
    }

    if (
      disponible <= minimo
    ) {
      return 'STOCK BAJO';
    }

    return 'DISPONIBLE';
  }

  abrirEdicion(
    inventario: InventarioResponse
  ): void {

    if (
      !this.puedeEditar
    ) {
      return;
    }

    this.inventarioSeleccionado =
      inventario;

    this.mensajeError =
      '';

    this.formularioInventario.reset({

      stockActual:
        Number(
          inventario.stockActual
        ),

      stockReservado:
        Number(
          inventario.stockReservado
        ),

      stockMinimo:
        Number(
          inventario.stockMinimo
        )

    });

    this.mostrarEdicion =
      true;
  }

  cerrarEdicion(): void {

    if (this.guardando) {
      return;
    }

    this.mostrarEdicion =
      false;

    this.inventarioSeleccionado =
      null;

    this.mensajeError =
      '';
  }

  guardarInventario(): void {

    if (
      !this.inventarioSeleccionado ||
      this.formularioInventario.invalid ||
      this.guardando
    ) {

      this.formularioInventario
        .markAllAsTouched();

      return;
    }

    const stockActual =
      Number(
        this.formularioInventario
          .get('stockActual')
          ?.value ?? 0
      );

    const stockMinimo =
      Number(
        this.formularioInventario
          .get('stockMinimo')
          ?.value ?? 0
      );

    const stockReservado =
      Number(
        this.inventarioSeleccionado
          .stockReservado
      );

    if (
      stockActual <
      stockReservado
    ) {

      this.mensajeError =
        'El stock actual no puede ser menor que el stock reservado.';

      return;
    }

    const datos:
      InventarioRequest = {

      productoId:
        this.inventarioSeleccionado
          .productoId,

      stockActual:
        stockActual,

      stockReservado:
        stockReservado,

      stockMinimo:
        stockMinimo

    };

    this.guardando =
      true;

    this.mensajeError =
      '';

    this.inventarioService
      .actualizar(
        this.inventarioSeleccionado
          .idInventario,
        datos
      )
      .subscribe({

        next: () => {

          this.guardando =
            false;

          this.mostrarEdicion =
            false;

          this.inventarioSeleccionado =
            null;

          this.cargarDatos();
        },

        error: error => {

          console.error(
            'Error al actualizar inventario:',
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

      return 'No tienes permiso para modificar inventario.';
    }

    return 'No se pudo actualizar el inventario.';
  }
}