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
  DetalleSolicitudRequest,
  SolicitudRequest,
  SolicitudResponse,
  SolicitudesService
} from '../../../core/services/solicitudes.service';

@Component({
  selector: 'app-solicitudes',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './solicitudes.component.html',
  styleUrl: './solicitudes.component.scss'
})
export class SolicitudesComponent
  implements OnInit {

  solicitudes: SolicitudResponse[] = [];

  solicitudesFiltradas:
    SolicitudResponse[] = [];

  clientes: ClienteResponse[] = [];

  productos: ProductoResponse[] = [];

  cargando: boolean = true;

  guardando: boolean = false;

  errorCarga: boolean = false;

  mensajeError: string = '';

  busqueda: string = '';

  mostrarFormulario: boolean = false;

  mostrarDetalle: boolean = false;

  solicitudSeleccionada:
    SolicitudResponse | null = null;

  puedeCrear: boolean = false;

  formularioSolicitud: FormGroup;

  constructor(
    private solicitudesService:
      SolicitudesService,

    private clientesService:
      ClientesService,

    private productosService:
      ProductosService,

    private authService:
      AuthService,

    private formBuilder:
      FormBuilder
  ) {

    this.formularioSolicitud =
      this.formBuilder.group({

        clienteId: [
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

        origen: [
          'WHATSAPP',
          [
            Validators.required,
            Validators.maxLength(30)
          ]
        ],

        descripcion: [
          ''
        ],

        detalles:
          this.formBuilder.array([])

      });
  }

  ngOnInit(): void {

    this.cargarPermisos();

    this.cargarDatos();
  }

  get detalles(): FormArray {

    return this.formularioSolicitud
      .get('detalles') as FormArray;
  }

  cargarPermisos(): void {

    const permisos =
      this.authService
        .obtenerPermisos();

    this.puedeCrear =
      permisos.includes(
        'SOLICITUD_CREAR'
      );
  }

  cargarDatos(): void {

    this.cargando = true;

    this.errorCarga = false;

    this.mensajeError = '';

    forkJoin({

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

        this.solicitudes =
          respuesta.solicitudes;

        this.solicitudesFiltradas =
          [...respuesta.solicitudes];

        this.clientes =
          respuesta.clientes.filter(
            cliente =>
              cliente.estado === true
          );

        this.productos =
          respuesta.productos.filter(
            producto =>
              producto.estado === true
          );

        this.cargando =
          false;
      },

      error: error => {

        console.error(
          'Error al cargar solicitudes:',
          error
        );

        this.errorCarga =
          true;

        this.cargando =
          false;

        if (error.status === 403) {

          this.mensajeError =
            'No tienes permisos suficientes para cargar esta información.';

        } else {

          this.mensajeError =
            'No se pudo cargar el módulo de solicitudes.';
        }
      }

    });
  }

  crearDetalle(): FormGroup {

    return this.formBuilder.group({

      productoId: [
        '',
        Validators.required
      ],

      cantidad: [
        1,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      observacion: [
        ''
      ]

    });
  }

  abrirNuevaSolicitud(): void {

    this.mensajeError = '';

    this.formularioSolicitud.reset({

      clienteId: '',
      codigo: '',
      origen: 'WHATSAPP',
      descripcion: ''

    });

    this.detalles.clear();

    this.agregarProducto();

    this.mostrarFormulario =
      true;
  }

  agregarProducto(): void {

    this.detalles.push(
      this.crearDetalle()
    );
  }

  eliminarProducto(
    indice: number
  ): void {

    if (
      this.detalles.length <= 1
    ) {
      return;
    }

    this.detalles.removeAt(
      indice
    );
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

  guardarSolicitud(): void {

    if (
      this.formularioSolicitud.invalid ||
      this.detalles.length === 0 ||
      this.guardando
    ) {

      this.formularioSolicitud
        .markAllAsTouched();

      return;
    }

    const valores =
      this.formularioSolicitud.value;

    const detalles:
      DetalleSolicitudRequest[] =
      valores.detalles.map(
        (detalle: any) => {

          return {

            productoId:
              Number(
                detalle.productoId
              ),

            cantidad:
              Number(
                detalle.cantidad
              ),

            observacion:
              this.normalizarTexto(
                detalle.observacion
              )

          };
        }
      );

    const solicitud:
      SolicitudRequest = {

      clienteId:
        Number(
          valores.clienteId
        ),

      codigo:
        valores.codigo
          .trim(),

      origen:
        valores.origen
          .trim(),

      descripcion:
        this.normalizarTexto(
          valores.descripcion
        ),

      detalles:
        detalles

    };

    this.guardando =
      true;

    this.mensajeError =
      '';

    this.solicitudesService
      .crear(
        solicitud
      )
      .subscribe({

        next: () => {

          this.guardando =
            false;

          this.mostrarFormulario =
            false;

          this.detalles.clear();

          /*
           * IMPORTANTE:
           *
           * No agregamos directamente la respuesta
           * del POST a la tabla.
           *
           * Volvemos a consultar el backend para
           * recuperar los valores generados por MySQL,
           * como fechaSolicitud y fechas de creación.
           */
          this.cargarDatos();
        },

        error: error => {

          console.error(
            'Error al crear solicitud:',
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

      this.solicitudesFiltradas =
        [...this.solicitudes];

      return;
    }

    this.solicitudesFiltradas =
      this.solicitudes.filter(
        solicitud => {

          const codigo =
            solicitud.codigo
              ?.toLowerCase() ?? '';

          const origen =
            solicitud.origen
              ?.toLowerCase() ?? '';

          const estado =
            solicitud.estado
              ?.toLowerCase() ?? '';

          const cliente =
            this.obtenerNombreCliente(
              solicitud.clienteId
            )
              .toLowerCase();

          return (
            codigo.includes(
              this.busqueda
            ) ||
            origen.includes(
              this.busqueda
            ) ||
            estado.includes(
              this.busqueda
            ) ||
            cliente.includes(
              this.busqueda
            )
          );
        }
      );
  }

  verDetalle(
    solicitud: SolicitudResponse
  ): void {

    this.solicitudSeleccionada =
      solicitud;

    this.mostrarDetalle =
      true;
  }

  cerrarDetalle(): void {

    this.mostrarDetalle =
      false;

    this.solicitudSeleccionada =
      null;
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

  obtenerNombreProducto(
    productoId: number
  ): string {

    const producto =
      this.productos.find(
        item =>
          item.idProducto ===
          productoId
      );

    if (!producto) {

      return `Producto #${productoId}`;
    }

    return producto.nombre;
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

      return 'No tienes permiso para crear solicitudes.';
    }

    return 'No se pudo registrar la solicitud.';
  }
}