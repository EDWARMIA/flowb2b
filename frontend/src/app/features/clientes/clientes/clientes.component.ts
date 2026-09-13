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
  ClienteRequest,
  ClienteResponse,
  ClientesService
} from '../../../core/services/clientes.service';

import {
  AuthService
} from '../../../core/services/auth.service';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.scss'
})
export class ClientesComponent implements OnInit {

  clientes: ClienteResponse[] = [];

  clientesFiltrados: ClienteResponse[] = [];

  cargando: boolean = true;

  errorCarga: boolean = false;

  mensajeError: string = '';

  busqueda: string = '';

  mostrarFormulario: boolean = false;

  modoEdicion: boolean = false;

  idClienteEditando: number | null = null;

  guardando: boolean = false;

  puedeCrear: boolean = false;

  puedeEditar: boolean = false;

  formularioCliente: FormGroup;

  constructor(
    private clientesService: ClientesService,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {

    this.formularioCliente =
      this.formBuilder.group({

        tipoDocumento: [
          ''
        ],

        numeroDocumento: [
          ''
        ],

        razonSocial: [
          '',
          [
            Validators.required,
            Validators.maxLength(150)
          ]
        ],

        nombreComercial: [
          ''
        ],

        correo: [
          '',
          [
            Validators.email
          ]
        ],

        direccion: [
          ''
        ],

        contactoNombre: [
          ''
        ],

        contactoTelefono: [
          ''
        ],

        contactoCorreo: [
          '',
          [
            Validators.email
          ]
        ],

        estado: [
          true
        ]

      });
  }

  ngOnInit(): void {

    this.cargarPermisos();

    this.cargarClientes();
  }

  cargarPermisos(): void {

    const permisos =
      this.authService.obtenerPermisos();

    this.puedeCrear =
      permisos.includes(
        'CLIENTE_CREAR'
      );

    this.puedeEditar =
      permisos.includes(
        'CLIENTE_EDITAR'
      );
  }

  cargarClientes(): void {

    this.cargando = true;

    this.errorCarga = false;

    this.mensajeError = '';

    this.clientesService
      .listar()
      .subscribe({

        next: (
          respuesta: ClienteResponse[]
        ) => {

          this.clientes =
            respuesta;

          this.clientesFiltrados =
            [...respuesta];

          this.cargando =
            false;
        },

        error: error => {

          console.error(
            'Error al cargar clientes:',
            error
          );

          this.errorCarga =
            true;

          this.cargando =
            false;

          if (error.status === 403) {

            this.mensajeError =
              'No tienes permiso para ver clientes.';

          } else {

            this.mensajeError =
              'No se pudieron cargar los clientes.';
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

    if (!this.busqueda) {

      this.clientesFiltrados =
        [...this.clientes];

      return;
    }

    this.clientesFiltrados =
      this.clientes.filter(
        cliente => {

          const razonSocial =
            cliente.razonSocial
              ?.toLowerCase() ?? '';

          const nombreComercial =
            cliente.nombreComercial
              ?.toLowerCase() ?? '';

          const documento =
            cliente.numeroDocumento
              ?.toLowerCase() ?? '';

          const correo =
            cliente.correo
              ?.toLowerCase() ?? '';

          const contactoNombre =
            cliente.contactoNombre
              ?.toLowerCase() ?? '';

          const contactoCorreo =
            cliente.contactoCorreo
              ?.toLowerCase() ?? '';

          return (
            razonSocial.includes(
              this.busqueda
            ) ||
            nombreComercial.includes(
              this.busqueda
            ) ||
            documento.includes(
              this.busqueda
            ) ||
            correo.includes(
              this.busqueda
            ) ||
            contactoNombre.includes(
              this.busqueda
            ) ||
            contactoCorreo.includes(
              this.busqueda
            )
          );
        }
      );
  }

  abrirNuevoCliente(): void {

    this.modoEdicion =
      false;

    this.idClienteEditando =
      null;

    this.mensajeError =
      '';

    this.formularioCliente.reset({

      tipoDocumento: '',
      numeroDocumento: '',
      razonSocial: '',
      nombreComercial: '',
      correo: '',
      direccion: '',
      contactoNombre: '',
      contactoTelefono: '',
      contactoCorreo: '',
      estado: true

    });

    this.mostrarFormulario =
      true;
  }

  editarCliente(
    cliente: ClienteResponse
  ): void {

    this.modoEdicion =
      true;

    this.idClienteEditando =
      cliente.idCliente;

    this.mensajeError =
      '';

    this.formularioCliente.patchValue({

      tipoDocumento:
        cliente.tipoDocumento ?? '',

      numeroDocumento:
        cliente.numeroDocumento ?? '',

      razonSocial:
        cliente.razonSocial,

      nombreComercial:
        cliente.nombreComercial ?? '',

      correo:
        cliente.correo ?? '',

      direccion:
        cliente.direccion ?? '',

      contactoNombre:
        cliente.contactoNombre ?? '',

      contactoTelefono:
        cliente.contactoTelefono ?? '',

      contactoCorreo:
        cliente.contactoCorreo ?? '',

      estado:
        cliente.estado

    });

    this.mostrarFormulario =
      true;
  }

  cerrarFormulario(): void {

    if (this.guardando) {
      return;
    }

    this.mostrarFormulario =
      false;

    this.modoEdicion =
      false;

    this.idClienteEditando =
      null;

    this.mensajeError =
      '';
  }

  guardarCliente(): void {

    if (
      this.formularioCliente.invalid ||
      this.guardando
    ) {

      this.formularioCliente
        .markAllAsTouched();

      return;
    }

    const valores =
      this.formularioCliente.value;

    const cliente: ClienteRequest = {

      tipoDocumento:
        this.normalizarTexto(
          valores.tipoDocumento
        ),

      numeroDocumento:
        this.normalizarTexto(
          valores.numeroDocumento
        ),

      razonSocial:
        valores.razonSocial.trim(),

      nombreComercial:
        this.normalizarTexto(
          valores.nombreComercial
        ),

      correo:
        this.normalizarTexto(
          valores.correo
        ),

      telefono:
        null,

      direccion:
        this.normalizarTexto(
          valores.direccion
        ),

      contactoNombre:
        this.normalizarTexto(
          valores.contactoNombre
        ),

      contactoTelefono:
        this.normalizarTexto(
          valores.contactoTelefono
        ),

      contactoCorreo:
        this.normalizarTexto(
          valores.contactoCorreo
        ),

      estado:
        valores.estado === true

    };

    this.guardando =
      true;

    this.mensajeError =
      '';

    if (
      this.modoEdicion &&
      this.idClienteEditando !== null
    ) {

      this.clientesService
        .actualizar(
          this.idClienteEditando,
          cliente
        )
        .subscribe({

          next: () => {

            this.guardando =
              false;

            this.mostrarFormulario =
              false;

            this.idClienteEditando =
              null;

            this.cargarClientes();
          },

          error: error => {

            console.error(
              'Error al actualizar cliente:',
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

      return;
    }

    this.clientesService
      .crear(
        cliente
      )
      .subscribe({

        next: () => {

          this.guardando =
            false;

          this.mostrarFormulario =
            false;

          this.cargarClientes();
        },

        error: error => {

          console.error(
            'Error al crear cliente:',
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

    return 'Ocurrió un error al guardar el cliente.';
  }
}