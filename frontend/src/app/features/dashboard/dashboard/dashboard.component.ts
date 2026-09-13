import {
  Component,
  OnInit
} from '@angular/core';

import {
  Router
} from '@angular/router';

import {
  DashboardResumen,
  DashboardService
} from '../../../core/services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {

  resumen: DashboardResumen = {
    solicitudes: 0,
    cotizaciones: 0,
    pedidos: 0,
    aprobacionesPendientes: 0
  };

  cargando: boolean = true;

  errorCarga: boolean = false;

  fechaActual: Date =
    new Date();

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {
  }

  ngOnInit(): void {

    this.fechaActual =
      new Date();

    this.cargarResumen();
  }

  cargarResumen(): void {

    this.cargando = true;

    this.errorCarga = false;

    this.dashboardService
      .obtenerResumen()
      .subscribe({

        next: (
          respuesta: DashboardResumen
        ) => {

          this.resumen =
            respuesta;

          this.cargando =
            false;
        },

        error: error => {

          console.error(
            'Error al cargar el dashboard:',
            error
          );

          this.errorCarga =
            true;

          this.cargando =
            false;
        }

      });
  }

  nuevaSolicitud(): void {

    this.router.navigate([
      '/solicitudes'
    ]);
  }

  nuevaCotizacion(): void {

    this.router.navigate([
      '/cotizaciones'
    ]);
  }

  revisarInventario(): void {

    this.router.navigate([
      '/inventario'
    ]);
  }

  obtenerFechaFormateada(): string {

    return this.fechaActual
      .toLocaleDateString(
        'es-PE',
        {
          day: '2-digit',
          month: 'long',
          year: 'numeric'
        }
      );
  }

  obtenerMaximo(): number {

    const maximo =
      Math.max(
        Number(
          this.resumen.solicitudes ?? 0
        ),
        Number(
          this.resumen.cotizaciones ?? 0
        ),
        Number(
          this.resumen.pedidos ?? 0
        ),
        Number(
          this.resumen.aprobacionesPendientes ?? 0
        )
      );

    return maximo > 0
      ? maximo
      : 1;
  }

  obtenerLimiteGrafico(): number {

    const maximo =
      this.obtenerMaximo();

    if (maximo <= 4) {
      return 4;
    }

    if (maximo <= 8) {
      return 8;
    }

    if (maximo <= 12) {
      return 12;
    }

    if (maximo <= 16) {
      return 16;
    }

    return Math.ceil(
      maximo / 5
    ) * 5;
  }

  obtenerNivel(
    valor: number
  ): number {

    const valorNumerico =
      Number(
        valor ?? 0
      );

    const limite =
      this.obtenerLimiteGrafico();

    const ySuperior =
      24;

    const yInferior =
      205;

    const altura =
      yInferior -
      ySuperior;

    return (
      yInferior -
      (
        valorNumerico /
        limite
      ) *
      altura
    );
  }

  obtenerNivelEscala(
    porcentaje: number
  ): number {

    const ySuperior =
      24;

    const yInferior =
      205;

    const altura =
      yInferior -
      ySuperior;

    return (
      yInferior -
      (
        porcentaje /
        100
      ) *
      altura
    );
  }

  obtenerValorEscala(
    porcentaje: number
  ): number {

    return Math.round(
      this.obtenerLimiteGrafico() *
      porcentaje /
      100
    );
  }

  limitarY(
    valor: number
  ): number {

    return Math.max(
      24,
      Math.min(
        205,
        valor
      )
    );
  }

  obtenerPuntosSolicitudes(): string {

    const y =
      this.obtenerNivel(
        this.resumen.solicitudes
      );

    return `
      55,${this.limitarY(y + 18)}
      225,${this.limitarY(y - 20)}
      395,${this.limitarY(y + 10)}
      565,${this.limitarY(y - 24)}
      735,${this.limitarY(y)}
    `;
  }

  obtenerPuntosCotizaciones(): string {

    const y =
      this.obtenerNivel(
        this.resumen.cotizaciones
      );

    return `
      55,${this.limitarY(y + 22)}
      225,${this.limitarY(y - 16)}
      395,${this.limitarY(y + 14)}
      565,${this.limitarY(y - 21)}
      735,${this.limitarY(y)}
    `;
  }

  obtenerPuntosPedidos(): string {

    const y =
      this.obtenerNivel(
        this.resumen.pedidos
      );

    return `
      55,${this.limitarY(y + 17)}
      225,${this.limitarY(y - 19)}
      395,${this.limitarY(y + 9)}
      565,${this.limitarY(y - 18)}
      735,${this.limitarY(y)}
    `;
  }

  obtenerPuntosAprobaciones(): string {

    const y =
      this.obtenerNivel(
        this.resumen.aprobacionesPendientes
      );

    return `
      55,${this.limitarY(y + 11)}
      225,${this.limitarY(y - 14)}
      395,${this.limitarY(y + 7)}
      565,${this.limitarY(y - 12)}
      735,${this.limitarY(y)}
    `;
  }
}