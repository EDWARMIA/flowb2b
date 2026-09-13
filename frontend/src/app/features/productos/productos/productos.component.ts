import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';

import {
  CategoriaProductoResponse,
  CategoriasProductoService
} from '../../../core/services/categorias-producto.service';

import {
  ProductoRequest,
  ProductoResponse,
  ProductosService
} from '../../../core/services/productos.service';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.scss'
})
export class ProductosComponent implements OnInit {

  productos: ProductoResponse[] = [];
  productosFiltrados: ProductoResponse[] = [];
  categorias: CategoriaProductoResponse[] = [];

  cargando = true;
  guardando = false;
  errorCarga = false;

  mensajeError = '';

  busqueda = '';
  filtroEstado = 'TODOS';

  mostrarFormulario = false;
  modoEdicion = false;

  productoSeleccionado: ProductoResponse | null = null;

  puedeCrear = false;
  puedeEditar = false;

  formularioProducto: FormGroup;

  constructor(
    private productosService: ProductosService,
    private categoriasService: CategoriasProductoService,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {

    this.formularioProducto = this.formBuilder.group({

      categoriaId: [
        '',
        Validators.required
      ],

      codigo: [
        '',
        [
          Validators.required,
          Validators.maxLength(50)
        ]
      ],

      nombre: [
        '',
        [
          Validators.required,
          Validators.maxLength(150)
        ]
      ],

      descripcion: [
        '',
        Validators.maxLength(255)
      ],

      unidadMedida: [
        'UND',
        [
          Validators.required,
          Validators.maxLength(30)
        ]
      ],

      precioBase: [
        0,
        [
          Validators.required,
          Validators.min(0)
        ]
      ],

      estado: [
        true
      ]

    });
  }

  ngOnInit(): void {

    this.cargarPermisos();
    this.cargarDatos();
  }

  cargarPermisos(): void {

    const permisos = this.authService.obtenerPermisos();

    this.puedeCrear = permisos.includes('PRODUCTO_CREAR');

    this.puedeEditar = permisos.includes('PRODUCTO_EDITAR');
  }

  cargarDatos(): void {

    this.cargando = true;
    this.errorCarga = false;
    this.mensajeError = '';

    forkJoin({
      productos: this.productosService.listar(),
      categorias: this.categoriasService.listar()
    }).subscribe({

      next: (respuesta) => {

        this.productos = respuesta.productos;
        this.categorias = respuesta.categorias;

        this.aplicarFiltros();

        this.cargando = false;
      },

      error: (error) => {

        console.error(
          'Error al cargar productos:',
          error
        );

        this.cargando = false;
        this.errorCarga = true;

        this.mensajeError =
          this.obtenerMensajeError(error);
      }

    });
  }

  abrirNuevoProducto(): void {

    if (!this.puedeCrear) {
      return;
    }

    this.modoEdicion = false;
    this.productoSeleccionado = null;
    this.mensajeError = '';

    this.formularioProducto.reset({
      categoriaId: '',
      codigo: '',
      nombre: '',
      descripcion: '',
      unidadMedida: 'UND',
      precioBase: 0,
      estado: true
    });

    this.mostrarFormulario = true;
  }

  abrirEditarProducto(
    producto: ProductoResponse
  ): void {

    if (!this.puedeEditar) {
      return;
    }

    this.modoEdicion = true;
    this.productoSeleccionado = producto;
    this.mensajeError = '';

    this.formularioProducto.reset({

      categoriaId:
        producto.categoriaId,

      codigo:
        producto.codigo,

      nombre:
        producto.nombre,

      descripcion:
        producto.descripcion ?? '',

      unidadMedida:
        producto.unidadMedida,

      precioBase:
        producto.precioBase,

      estado:
        producto.estado

    });

    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {

    if (this.guardando) {
      return;
    }

    this.mostrarFormulario = false;
    this.productoSeleccionado = null;
    this.mensajeError = '';
  }

  guardarProducto(): void {

    if (
      this.formularioProducto.invalid ||
      this.guardando
    ) {

      this.formularioProducto.markAllAsTouched();
      return;
    }

    const valores =
      this.formularioProducto.value;

    const producto: ProductoRequest = {

      categoriaId:
        Number(valores.categoriaId),

      codigo:
        String(valores.codigo)
          .trim(),

      nombre:
        String(valores.nombre)
          .trim(),

      descripcion:
        this.normalizarTexto(
          valores.descripcion
        ),

      unidadMedida:
        String(valores.unidadMedida)
          .trim()
          .toUpperCase(),

      precioBase:
        Number(valores.precioBase),

      estado:
        valores.estado === true

    };

    this.guardando = true;
    this.mensajeError = '';

    if (
      this.modoEdicion &&
      this.productoSeleccionado
    ) {

      this.productosService
        .actualizar(
          this.productoSeleccionado.idProducto,
          producto
        )
        .subscribe({

          next: () => {

            this.guardando = false;
            this.mostrarFormulario = false;
            this.productoSeleccionado = null;

            this.cargarDatos();
          },

          error: (error) => {

            console.error(
              'Error al actualizar producto:',
              error
            );

            this.guardando = false;

            this.mensajeError =
              this.obtenerMensajeError(error);
          }

        });

      return;
    }

    this.productosService
      .crear(producto)
      .subscribe({

        next: () => {

          this.guardando = false;
          this.mostrarFormulario = false;

          this.cargarDatos();
        },

        error: (error) => {

          console.error(
            'Error al crear producto:',
            error
          );

          this.guardando = false;

          this.mensajeError =
            this.obtenerMensajeError(error);
        }

      });
  }

  buscar(evento: Event): void {

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
      ...this.productos
    ];

    if (
      this.filtroEstado === 'ACTIVOS'
    ) {

      resultado = resultado.filter(
        producto =>
          producto.estado === true
      );

    } else if (
      this.filtroEstado === 'INACTIVOS'
    ) {

      resultado = resultado.filter(
        producto =>
          producto.estado === false
      );
    }

    if (this.busqueda) {

      resultado = resultado.filter(
        producto => {

          const codigo =
            producto.codigo
              ?.toLowerCase() ?? '';

          const nombre =
            producto.nombre
              ?.toLowerCase() ?? '';

          const descripcion =
            producto.descripcion
              ?.toLowerCase() ?? '';

          const categoria =
            this.obtenerNombreCategoria(
              producto.categoriaId
            ).toLowerCase();

          return (
            codigo.includes(this.busqueda) ||
            nombre.includes(this.busqueda) ||
            descripcion.includes(this.busqueda) ||
            categoria.includes(this.busqueda)
          );
        }
      );
    }

    resultado.sort(
      (a, b) =>
        a.nombre.localeCompare(
          b.nombre
        )
    );

    this.productosFiltrados = resultado;
  }

  obtenerCantidadActivos(): number {

    return this.productos.filter(
      producto =>
        producto.estado === true
    ).length;
  }

  obtenerNombreCategoria(
    categoriaId: number
  ): string {

    const categoria =
      this.categorias.find(
        item =>
          item.idCategoria === categoriaId
      );

    return (
      categoria?.nombre ||
      `Categoría #${categoriaId}`
    );
  }

  obtenerCategoriasOrdenadas():
    CategoriaProductoResponse[] {

    return [
      ...this.categorias
    ].sort(
      (a, b) =>
        a.nombre.localeCompare(
          b.nombre
        )
    );
  }

  formatearMoneda(
    valor: number | null
  ): string {

    return Number(
      valor ?? 0
    ).toLocaleString(
      'es-PE',
      {
        style: 'currency',
        currency: 'PEN'
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

    const texto = valor.trim();

    return texto.length > 0
      ? texto
      : null;
  }

  obtenerMensajeError(
    error: any
  ): string {

    if (error?.error?.mensaje) {
      return error.error.mensaje;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    if (
      typeof error?.error === 'string'
    ) {
      return error.error;
    }

    if (error?.status === 401) {
      return 'Tu sesión ha vencido. Inicia sesión nuevamente.';
    }

    if (error?.status === 403) {
      return 'No tienes permisos para realizar esta operación.';
    }

    return 'No se pudo procesar el producto.';
  }
}