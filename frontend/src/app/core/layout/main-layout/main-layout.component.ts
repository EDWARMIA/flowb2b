import {
  CommonModule
} from '@angular/common';

import {
  Component,
  HostListener,
  OnInit
} from '@angular/core';

import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';

import {
  AuthService,
  LoginResponse
} from '../../services/auth.service';

import {
  NotificacionResponse,
  NotificacionesService
} from '../../services/notificaciones.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl:
    './main-layout.component.html',
  styleUrl:
    './main-layout.component.scss'
})
export class MainLayoutComponent
  implements OnInit {

  nombreUsuario: string =
    'Usuario';

  rolUsuario: string =
    'Usuario';

  menuColapsado: boolean =
    false;

  notificaciones:
    NotificacionResponse[] = [];

  contadorNoLeidas: number =
    0;

  mostrarNotificaciones: boolean =
    false;

  cargandoNotificaciones: boolean =
    false;

  errorNotificaciones: string =
    '';

  puedeVerNotificaciones: boolean =
    false;

  puedeVerDashboardGerencial: boolean =
    false;

  puedeVerAdministracion: boolean =
    false;


  constructor(
    private authService: AuthService,
    private router: Router,
    private notificacionesService:
      NotificacionesService
  ) {
  }


  ngOnInit(): void {

    this.cargarUsuario();

    this.cargarPermisos();

    if (
      this.puedeVerNotificaciones
    ) {

      this.cargarCantidadNoLeidas();
    }
  }


  cargarUsuario(): void {

    const usuario:
      LoginResponse | null =
      this.authService
        .obtenerUsuario();

    if (!usuario) {
      return;
    }

    const nombre =
      usuario['nombre'];

    const apellido =
      usuario['apellido'];

    if (
      typeof nombre === 'string' &&
      nombre.trim().length > 0
    ) {

      this.nombreUsuario =
        nombre.trim();

      if (
        typeof apellido === 'string' &&
        apellido.trim().length > 0
      ) {

        this.nombreUsuario =
          `${nombre.trim()} ${apellido.trim()}`;
      }
    }

    const roles =
      this.authService
        .obtenerRoles();

    if (
      roles.length > 0
    ) {

      this.rolUsuario =
        this.formatearRol(
          roles[0]
        );
    }
  }


  cargarPermisos(): void {

    const permisos =
      this.authService
        .obtenerPermisos();

    this.puedeVerNotificaciones =
      permisos.includes(
        'NOTIFICACION_VER'
      );

    this.puedeVerDashboardGerencial =
      permisos.includes(
        'DASHBOARD_GERENCIAL_VER'
      );

    this.puedeVerAdministracion =
      permisos.includes(
        'ADMIN_USUARIO_GESTIONAR'
      ) &&
      permisos.includes(
        'ADMIN_ROL_GESTIONAR'
      );
  }


  cargarCantidadNoLeidas(): void {

    if (
      !this.puedeVerNotificaciones
    ) {
      return;
    }

    this.notificacionesService
      .listarNoLeidas()
      .subscribe({

        next: (
          notificaciones
        ) => {

          this.contadorNoLeidas =
            notificaciones.length;
        },

        error: (error) => {

          console.error(
            'Error al cargar notificaciones no leídas:',
            error
          );
        }

      });
  }


  alternarNotificaciones(
    event: Event
  ): void {

    event.stopPropagation();

    if (
      !this.puedeVerNotificaciones
    ) {
      return;
    }

    this.mostrarNotificaciones =
      !this.mostrarNotificaciones;

    if (
      this.mostrarNotificaciones
    ) {

      this.cargarNotificaciones();
    }
  }


  cargarNotificaciones(): void {

    this.cargandoNotificaciones =
      true;

    this.errorNotificaciones =
      '';

    this.notificacionesService
      .listar()
      .subscribe({

        next: (
          notificaciones
        ) => {

          this.notificaciones =
            notificaciones;

          this.actualizarContador();

          this.cargandoNotificaciones =
            false;
        },

        error: (error) => {

          console.error(
            'Error al cargar notificaciones:',
            error
          );

          this.cargandoNotificaciones =
            false;

          this.errorNotificaciones =
            this.obtenerMensajeError(
              error
            );
        }

      });
  }


  seleccionarNotificacion(
    notificacion:
      NotificacionResponse,
    event?: Event
  ): void {

    if (event) {
      event.stopPropagation();
    }

    if (
      !notificacion.leida
    ) {

      this.notificacionesService
        .marcarComoLeida(
          notificacion.idNotificacion
        )
        .subscribe({

          next: (
            actualizada
          ) => {

            this.notificaciones =
              this.notificaciones.map(
                item =>
                  item.idNotificacion ===
                  actualizada.idNotificacion
                    ? actualizada
                    : item
              );

            this.actualizarContador();

            this.irAEntidad(
              actualizada
            );
          },

          error: (error) => {

            console.error(
              'Error al marcar notificación como leída:',
              error
            );

            this.errorNotificaciones =
              this.obtenerMensajeError(
                error
              );
          }

        });

      return;
    }

    this.irAEntidad(
      notificacion
    );
  }


  irAEntidad(
    notificacion:
      NotificacionResponse
  ): void {

    this.mostrarNotificaciones =
      false;

    const tipo =
      notificacion.entidadTipo
        ?.trim()
        .toUpperCase();

    const id =
      notificacion.entidadId;

    if (!tipo) {
      return;
    }

    switch (tipo) {

      case 'PEDIDO':

        this.router.navigate(
          ['/pedidos'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      case 'TAREA':

        this.router.navigate(
          ['/tareas'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      case 'COTIZACION':

        this.router.navigate(
          ['/cotizaciones'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      case 'APROBACION':

        this.router.navigate(
          ['/aprobaciones'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      case 'INVENTARIO':

        this.router.navigate(
          ['/inventario'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      case 'PRODUCTO':

        this.router.navigate(
          ['/productos'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      case 'SOLICITUD':

        this.router.navigate(
          ['/solicitudes'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      case 'CLIENTE':

        this.router.navigate(
          ['/clientes'],
          {
            queryParams: id
              ? {
                  id: id
                }
              : undefined
          }
        );

        break;


      default:

        console.warn(
          'Entidad de notificación no reconocida:',
          tipo
        );

        break;
    }
  }


  actualizarContador(): void {

    this.contadorNoLeidas =
      this.notificaciones.filter(
        notificacion =>
          !notificacion.leida
      ).length;
  }


  obtenerClaseTipo(
    tipo: string | null
  ): string {

    if (!tipo) {
      return 'general';
    }

    const valor =
      tipo
        .trim()
        .toLowerCase();

    if (
      valor.includes('pedido')
    ) {
      return 'pedido';
    }

    if (
      valor.includes('tarea')
    ) {
      return 'tarea';
    }

    if (
      valor.includes('stock') ||
      valor.includes('inventario')
    ) {
      return 'inventario';
    }

    if (
      valor.includes('cotizacion') ||
      valor.includes('cotización') ||
      valor.includes('aprobacion') ||
      valor.includes('aprobación')
    ) {
      return 'comercial';
    }

    return 'general';
  }


  obtenerInicialTipo(
    tipo: string | null
  ): string {

    const clase =
      this.obtenerClaseTipo(
        tipo
      );

    switch (clase) {

      case 'pedido':
        return 'P';

      case 'tarea':
        return 'T';

      case 'inventario':
        return 'I';

      case 'comercial':
        return 'C';

      default:
        return 'N';
    }
  }


  formatearFechaNotificacion(
    fecha: string | null
  ): string {

    if (!fecha) {
      return '';
    }

    const valor =
      new Date(fecha);

    if (
      Number.isNaN(
        valor.getTime()
      )
    ) {
      return '';
    }

    const ahora =
      new Date();

    const diferencia =
      ahora.getTime() -
      valor.getTime();

    const minutos =
      Math.floor(
        diferencia /
        60000
      );

    if (
      minutos >= 0 &&
      minutos < 1
    ) {

      return 'Ahora';
    }

    if (
      minutos >= 1 &&
      minutos < 60
    ) {

      return `Hace ${minutos} min`;
    }

    const horas =
      Math.floor(
        minutos / 60
      );

    if (
      horas >= 1 &&
      horas < 24
    ) {

      return horas === 1
        ? 'Hace 1 hora'
        : `Hace ${horas} horas`;
    }

    return valor.toLocaleDateString(
      'es-PE',
      {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
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
      error?.status === 401
    ) {

      return 'Tu sesión ha vencido.';
    }

    if (
      error?.status === 403
    ) {

      return 'No tienes permiso para ver las notificaciones.';
    }

    return 'No se pudieron cargar las notificaciones.';
  }


  formatearRol(
    rol: string
  ): string {

    return rol
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


  alternarMenu(): void {

    this.menuColapsado =
      !this.menuColapsado;
  }


  cerrarSesion(): void {

    this.authService.logout();

    this.router.navigate([
      '/login'
    ]);
  }


  @HostListener(
    'document:click'
  )
  cerrarNotificacionesAlHacerClickFuera():
    void {

    this.mostrarNotificaciones =
      false;
  }
}