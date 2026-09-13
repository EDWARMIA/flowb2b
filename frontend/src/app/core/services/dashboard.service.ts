import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardResumen {
  solicitudes: number;
  cotizaciones: number;
  pedidos: number;
  aprobacionesPendientes: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private readonly apiUrl =
    'http://localhost:8080/api/dashboard';

  constructor(
    private http: HttpClient
  ) {
  }

  obtenerResumen(): Observable<DashboardResumen> {

    return this.http.get<DashboardResumen>(
      `${this.apiUrl}/resumen`
    );
  }
}