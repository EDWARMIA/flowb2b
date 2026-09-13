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
  AdministracionPermiso,
  AdministracionRol,
  AdministracionService,
  AdministracionUsuario,
  CrearUsuarioAdministracion,
  RolAdministracionRequest
} from '../../../core/services/administracion.service';

@Component({
  selector: 'app-administracion',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl:
    './administracion.component.html',
  styleUrl:
    './administracion.component.scss'
})
export class AdministracionComponent
  implements OnInit {

  pestanaActiva:
    'usuarios' | 'roles' =
      'usuarios';

  usuarios:
    AdministracionUsuario[] = [];

  roles:
    AdministracionRol[] = [];

  permisos:
    AdministracionPermiso[] = [];

  cargando = false;

  mensaje = '';
  error = '';

  // =====================================================
  // MODAL EDITAR USUARIO
  // =====================================================

  mostrarModalUsuario = false;

  usuarioSeleccionado:
    AdministracionUsuario | null =
      null;

  usuarioEstado = true;

  usuarioRolesIds:
    number[] = [];

  guardandoUsuario = false;

  // =====================================================
  // MODAL NUEVO USUARIO
  // =====================================================

  mostrarModalNuevoUsuario = false;

  nuevoNombre = '';
  nuevoApellido = '';
  nuevoCorreo = '';
  nuevaPassword = '';
  confirmarPassword = '';
  nuevoUsuarioEstado = true;

  nuevoUsuarioRolesIds:
    number[] = [];

  guardandoNuevoUsuario = false;

  mostrarPassword = false;

  // =====================================================
  // MODAL ROL
  // =====================================================

  mostrarModalRol = false;

  rolEditando:
    AdministracionRol | null =
      null;

  rolNombre = '';
  rolDescripcion = '';
  rolEstado = true;

  rolPermisosIds:
    number[] = [];

  guardandoRol = false;

  constructor(
    private administracionService:
      AdministracionService
  ) {
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  // =====================================================
  // CARGAR DATOS
  // =====================================================

  cargarDatos(): void {

    this.cargando = true;

    this.error = '';
    this.mensaje = '';

    forkJoin({

      usuarios:
        this.administracionService
          .listarUsuarios(),

      roles:
        this.administracionService
          .listarRoles(),

      permisos:
        this.administracionService
          .listarPermisos()

    })
      .subscribe({

        next: resultado => {

          this.usuarios =
            resultado.usuarios;

          this.roles =
            resultado.roles;

          this.permisos =
            resultado.permisos;

          this.cargando = false;
        },

        error: error => {

          console.error(error);

          this.error =
            error?.error?.mensaje ||
            'No se pudo cargar la administración.';

          this.cargando = false;
        }

      });
  }

  // =====================================================
  // PESTAÑAS
  // =====================================================

  cambiarPestana(
    pestana: 'usuarios' | 'roles'
  ): void {

    this.pestanaActiva =
      pestana;

    this.limpiarMensajes();
  }

  // =====================================================
  // NUEVO USUARIO
  // =====================================================

  abrirNuevoUsuario(): void {

    this.limpiarMensajes();

    this.nuevoNombre = '';
    this.nuevoApellido = '';
    this.nuevoCorreo = '';
    this.nuevaPassword = '';
    this.confirmarPassword = '';

    this.nuevoUsuarioEstado =
      true;

    this.nuevoUsuarioRolesIds =
      [];

    this.mostrarPassword =
      false;

    this.mostrarModalNuevoUsuario =
      true;
  }

  cerrarModalNuevoUsuario(): void {

    this.mostrarModalNuevoUsuario =
      false;

    this.nuevoNombre = '';
    this.nuevoApellido = '';
    this.nuevoCorreo = '';
    this.nuevaPassword = '';
    this.confirmarPassword = '';

    this.nuevoUsuarioRolesIds =
      [];

    this.nuevoUsuarioEstado =
      true;

    this.guardandoNuevoUsuario =
      false;

    this.mostrarPassword =
      false;
  }

  nuevoUsuarioTieneRol(
    idRol: number
  ): boolean {

    return this.nuevoUsuarioRolesIds
      .includes(idRol);
  }

  cambiarRolNuevoUsuario(
    idRol: number,
    seleccionado: boolean
  ): void {

    if (seleccionado) {

      if (
        !this.nuevoUsuarioRolesIds
          .includes(idRol)
      ) {

        this.nuevoUsuarioRolesIds
          .push(idRol);
      }

      return;
    }

    this.nuevoUsuarioRolesIds =
      this.nuevoUsuarioRolesIds
        .filter(
          id => id !== idRol
        );
  }

  alternarMostrarPassword(): void {

    this.mostrarPassword =
      !this.mostrarPassword;
  }

  guardarNuevoUsuario(): void {

    this.limpiarMensajes();

    // =========================
    // NOMBRE
    // =========================

    if (
      !this.nuevoNombre.trim()
    ) {

      this.error =
        'El nombre es obligatorio.';

      return;
    }

    // =========================
    // APELLIDO
    // =========================

    if (
      !this.nuevoApellido.trim()
    ) {

      this.error =
        'El apellido es obligatorio.';

      return;
    }

    // =========================
    // CORREO
    // =========================

    if (
      !this.nuevoCorreo.trim()
    ) {

      this.error =
        'El correo es obligatorio.';

      return;
    }

    if (
      !this.nuevoCorreo.includes('@')
    ) {

      this.error =
        'Ingresa un correo válido.';

      return;
    }

    // =========================
    // CONTRASEÑA
    // =========================

    if (
      !this.nuevaPassword
    ) {

      this.error =
        'La contraseña es obligatoria.';

      return;
    }

    if (
      this.nuevaPassword.length < 8
    ) {

      this.error =
        'La contraseña debe tener al menos 8 caracteres.';

      return;
    }

    if (
      this.nuevaPassword !==
      this.confirmarPassword
    ) {

      this.error =
        'Las contraseñas no coinciden.';

      return;
    }

    // =========================
    // ROLES
    // =========================

    if (
      this.nuevoUsuarioRolesIds
        .length === 0
    ) {

      this.error =
        'Selecciona al menos un rol.';

      return;
    }

    const datos:
      CrearUsuarioAdministracion = {

      nombre:
        this.nuevoNombre.trim(),

      apellido:
        this.nuevoApellido.trim(),

      correo:
        this.nuevoCorreo
          .trim()
          .toLowerCase(),

      password:
        this.nuevaPassword,

      estado:
        this.nuevoUsuarioEstado,

      rolesIds:
        this.nuevoUsuarioRolesIds
    };

    this.guardandoNuevoUsuario =
      true;

    this.administracionService
      .crearUsuario(datos)
      .subscribe({

        next: nuevoUsuario => {

          this.usuarios = [
            ...this.usuarios,
            nuevoUsuario
          ];

          this.usuarios.sort(
            (a, b) =>
              a.nombre.localeCompare(
                b.nombre
              )
          );

          this.guardandoNuevoUsuario =
            false;

          this.cerrarModalNuevoUsuario();

          this.mensaje =
            'Usuario creado correctamente.';
        },

        error: error => {

          console.error(
            'Error al crear usuario:',
            error
          );

          this.error =
            error?.error?.mensaje ||
            error?.error?.message ||
            'No se pudo crear el usuario.';

          this.guardandoNuevoUsuario =
            false;
        }

      });
  }

  // =====================================================
  // EDITAR USUARIO
  // =====================================================

  abrirUsuario(
    usuario: AdministracionUsuario
  ): void {

    this.limpiarMensajes();

    this.usuarioSeleccionado =
      usuario;

    this.usuarioEstado =
      usuario.estado;

    this.usuarioRolesIds = [
      ...usuario.rolesIds
    ];

    this.mostrarModalUsuario =
      true;
  }

  cerrarModalUsuario(): void {

    this.mostrarModalUsuario =
      false;

    this.usuarioSeleccionado =
      null;

    this.usuarioRolesIds =
      [];

    this.guardandoUsuario =
      false;
  }

  usuarioTieneRol(
    idRol: number
  ): boolean {

    return this.usuarioRolesIds
      .includes(idRol);
  }

  cambiarRolUsuario(
    idRol: number,
    seleccionado: boolean
  ): void {

    if (seleccionado) {

      if (
        !this.usuarioRolesIds
          .includes(idRol)
      ) {

        this.usuarioRolesIds
          .push(idRol);
      }

      return;
    }

    this.usuarioRolesIds =
      this.usuarioRolesIds
        .filter(
          id => id !== idRol
        );
  }

  guardarUsuario(): void {

    if (
      !this.usuarioSeleccionado
    ) {
      return;
    }

    this.limpiarMensajes();

    if (
      this.usuarioRolesIds
        .length === 0
    ) {

      this.error =
        'El usuario debe tener al menos un rol.';

      return;
    }

    this.guardandoUsuario =
      true;

    this.administracionService
      .actualizarUsuario(
        this.usuarioSeleccionado
          .idUsuario,
        {
          estado:
            this.usuarioEstado,

          rolesIds:
            this.usuarioRolesIds
        }
      )
      .subscribe({

        next: usuarioActualizado => {

          const indice =
            this.usuarios
              .findIndex(
                usuario =>
                  usuario.idUsuario ===
                  usuarioActualizado
                    .idUsuario
              );

          if (
            indice !== -1
          ) {

            this.usuarios[indice] =
              usuarioActualizado;

            this.usuarios = [
              ...this.usuarios
            ];
          }

          this.guardandoUsuario =
            false;

          this.cerrarModalUsuario();

          this.mensaje =
            'Usuario actualizado correctamente.';
        },

        error: error => {

          console.error(
            'Error al actualizar usuario:',
            error
          );

          this.error =
            error?.error?.mensaje ||
            error?.error?.message ||
            'No se pudo actualizar el usuario.';

          this.guardandoUsuario =
            false;
        }

      });
  }

  // =====================================================
  // ROLES
  // =====================================================

  abrirNuevoRol(): void {

    this.limpiarMensajes();

    this.rolEditando =
      null;

    this.rolNombre = '';

    this.rolDescripcion = '';

    this.rolEstado =
      true;

    this.rolPermisosIds =
      [];

    this.mostrarModalRol =
      true;
  }

  abrirEditarRol(
    rol: AdministracionRol
  ): void {

    this.limpiarMensajes();

    this.rolEditando =
      rol;

    this.rolNombre =
      rol.nombre;

    this.rolDescripcion =
      rol.descripcion || '';

    this.rolEstado =
      rol.estado;

    this.rolPermisosIds = [
      ...rol.permisosIds
    ];

    this.mostrarModalRol =
      true;
  }

  cerrarModalRol(): void {

    this.mostrarModalRol =
      false;

    this.rolEditando =
      null;

    this.rolNombre = '';

    this.rolDescripcion = '';

    this.rolEstado =
      true;

    this.rolPermisosIds =
      [];

    this.guardandoRol =
      false;
  }

  rolTienePermiso(
    idPermiso: number
  ): boolean {

    return this.rolPermisosIds
      .includes(idPermiso);
  }

  cambiarPermisoRol(
    idPermiso: number,
    seleccionado: boolean
  ): void {

    if (seleccionado) {

      if (
        !this.rolPermisosIds
          .includes(idPermiso)
      ) {

        this.rolPermisosIds
          .push(idPermiso);
      }

      return;
    }

    this.rolPermisosIds =
      this.rolPermisosIds
        .filter(
          id => id !== idPermiso
        );
  }

  seleccionarTodosPermisos():
    void {

    this.rolPermisosIds =
      this.permisos.map(
        permiso =>
          permiso.idPermiso
      );
  }

  quitarTodosPermisos():
    void {

    this.rolPermisosIds =
      [];
  }

  guardarRol(): void {

    this.limpiarMensajes();

    if (
      !this.rolNombre.trim()
    ) {

      this.error =
        'El nombre del rol es obligatorio.';

      return;
    }

    const datos:
      RolAdministracionRequest = {

      nombre:
        this.rolNombre.trim(),

      descripcion:
        this.rolDescripcion.trim(),

      estado:
        this.rolEstado,

      permisosIds:
        this.rolPermisosIds
    };

    this.guardandoRol =
      true;

    // ===================================================
    // ACTUALIZAR
    // ===================================================

    if (
      this.rolEditando
    ) {

      this.administracionService
        .actualizarRol(
          this.rolEditando.idRol,
          datos
        )
        .subscribe({

          next:
            rolActualizado => {

              const indice =
                this.roles
                  .findIndex(
                    rol =>
                      rol.idRol ===
                      rolActualizado
                        .idRol
                  );

              if (
                indice !== -1
              ) {

                this.roles[indice] =
                  rolActualizado;

                this.roles = [
                  ...this.roles
                ];
              }

              this.guardandoRol =
                false;

              this.cerrarModalRol();

              this.mensaje =
                'Rol actualizado correctamente.';
            },

          error: error => {

            console.error(
              'Error al actualizar rol:',
              error
            );

            this.error =
              error?.error?.mensaje ||
              error?.error?.message ||
              'No se pudo actualizar el rol.';

            this.guardandoRol =
              false;
          }

        });

      return;
    }

    // ===================================================
    // CREAR
    // ===================================================

    this.administracionService
      .crearRol(datos)
      .subscribe({

        next: nuevoRol => {

          this.roles = [
            ...this.roles,
            nuevoRol
          ];

          this.roles.sort(
            (a, b) =>
              a.nombre.localeCompare(
                b.nombre
              )
          );

          this.guardandoRol =
            false;

          this.cerrarModalRol();

          this.mensaje =
            'Rol creado correctamente.';
        },

        error: error => {

          console.error(
            'Error al crear rol:',
            error
          );

          this.error =
            error?.error?.mensaje ||
            error?.error?.message ||
            'No se pudo crear el rol.';

          this.guardandoRol =
            false;
        }

      });
  }

  // =====================================================
  // AUXILIARES
  // =====================================================

  nombreCompleto(
    usuario: AdministracionUsuario
  ): string {

    return (
      `${usuario.nombre} ${usuario.apellido}`
    );
  }

  obtenerIniciales(
    usuario: AdministracionUsuario
  ): string {

    const nombre =
      usuario.nombre
        ?.charAt(0)
        ?.toUpperCase() || '';

    const apellido =
      usuario.apellido
        ?.charAt(0)
        ?.toUpperCase() || '';

    return nombre + apellido;
  }

  contarUsuariosActivos():
    number {

    return this.usuarios
      .filter(
        usuario =>
          usuario.estado
      )
      .length;
  }

  contarRolesActivos():
    number {

    return this.roles
      .filter(
        rol =>
          rol.estado
      )
      .length;
  }

  trackByUsuario(
    index: number,
    usuario: AdministracionUsuario
  ): number {

    return usuario.idUsuario;
  }

  trackByRol(
    index: number,
    rol: AdministracionRol
  ): number {

    return rol.idRol;
  }

  trackByPermiso(
    index: number,
    permiso: AdministracionPermiso
  ): number {

    return permiso.idPermiso;
  }

  limpiarMensajes(): void {

    this.mensaje = '';
    this.error = '';
  }
}