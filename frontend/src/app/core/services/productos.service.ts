import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductoRequest {
  categoriaId: number;
  codigo: string;
  nombre: string;
  descripcion: string | null;
  unidadMedida: string;
  precioBase: number;
  estado: boolean;
}

export interface ProductoResponse {
  idProducto: number;
  empresaId: number;
  categoriaId: number;
  codigo: string;
  nombre: string;
  descripcion: string | null;
  unidadMedida: string;
  precioBase: number;
  estado: boolean;
  fechaCreacion: string | null;
  fechaActualizacion: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class ProductosService {

  private readonly apiUrl =
    'http://localhost:8080/api/productos';

  constructor(
    private http: HttpClient
  ) {
  }

  listar(): Observable<ProductoResponse[]> {

    return this.http.get<ProductoResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idProducto: number
  ): Observable<ProductoResponse> {

    return this.http.get<ProductoResponse>(
      `${this.apiUrl}/${idProducto}`
    );
  }

  crear(
    producto: ProductoRequest
  ): Observable<ProductoResponse> {

    return this.http.post<ProductoResponse>(
      this.apiUrl,
      producto
    );
  }

  actualizar(
    idProducto: number,
    producto: ProductoRequest
  ): Observable<ProductoResponse> {

    return this.http.put<ProductoResponse>(
      `${this.apiUrl}/${idProducto}`,
      producto
    );
  }
}