import {
  Routes
} from '@angular/router';

import {
  LoginComponent
} from './features/auth/login/login.component';

import {
  DashboardComponent
} from './features/dashboard/dashboard/dashboard.component';

import {
  DashboardGerencialComponent
} from './features/dashboard-gerencial/dashboard-gerencial/dashboard-gerencial.component';

import {
  AdministracionComponent
} from './features/administracion/administracion/administracion.component';

import {
  ClientesComponent
} from './features/clientes/clientes/clientes.component';

import {
  SolicitudesComponent
} from './features/solicitudes/solicitudes/solicitudes.component';

import {
  CotizacionesComponent
} from './features/cotizaciones/cotizaciones/cotizaciones.component';

import {
  AprobacionesComponent
} from './features/aprobaciones/aprobaciones/aprobaciones.component';

import {
  PedidosComponent
} from './features/pedidos/pedidos/pedidos.component';

import {
  ProductosComponent
} from './features/productos/productos/productos.component';

import {
  InventarioComponent
} from './features/inventario/inventario/inventario.component';

import {
  TareasComponent
} from './features/tareas/tareas/tareas.component';

import {
  MainLayoutComponent
} from './core/layout/main-layout/main-layout.component';

import {
  authGuard
} from './core/guards/auth.guard';

import {
  dashboardGerencialGuard
} from './core/guards/dashboard-gerencial.guard';

import {
  administracionGuard
} from './core/guards/administracion.guard';

export const routes: Routes = [

  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [
      authGuard
    ],
    children: [

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },

      {
        path: 'dashboard',
        component: DashboardComponent
      },

      {
        path: 'clientes',
        component: ClientesComponent
      },

      {
        path: 'solicitudes',
        component: SolicitudesComponent
      },

      {
        path: 'cotizaciones',
        component: CotizacionesComponent
      },

      {
        path: 'aprobaciones',
        component: AprobacionesComponent
      },

      {
        path: 'pedidos',
        component: PedidosComponent
      },

      {
        path: 'productos',
        component: ProductosComponent
      },

      {
        path: 'inventario',
        component: InventarioComponent
      },

      {
        path: 'tareas',
        component: TareasComponent
      },

      {
        path: 'dashboard-gerencial',
        component: DashboardGerencialComponent,
        canActivate: [
          dashboardGerencialGuard
        ]
      },

      {
        path: 'administracion',
        component: AdministracionComponent,
        canActivate: [
          administracionGuard
        ]
      }

    ]
  },

  {
    path: '**',
    redirectTo: 'dashboard'
  }

];