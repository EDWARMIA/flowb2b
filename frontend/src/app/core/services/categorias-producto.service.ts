import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CategoriaProductoRequest {
  nombre: string;
  descripcion: string | null;
  estado: boolean;
}

export interface CategoriaProductoResponse {
  idCategoria: number;
  empresaId: number;
  nombre: string;
  descripcion: string | null;
  estado: boolean;
  fechaCreacion: string | null;
  fechaActualizacion: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class CategoriasProductoService {

  private readonly apiUrl =
    'http://localhost:8080/api/categorias-producto';

  constructor(
    private http: HttpClient
  ) {
  }

  listar():
    Observable<CategoriaProductoResponse[]> {

    return this.http.get<CategoriaProductoResponse[]>(
      this.apiUrl
    );
  }

  obtenerPorId(
    idCategoria: number
  ): Observable<CategoriaProductoResponse> {

    return this.http.get<CategoriaProductoResponse>(
      `${this.apiUrl}/${idCategoria}`
    );
  }

  crear(
    categoria: CategoriaProductoRequest
  ): Observable<CategoriaProductoResponse> {

    return this.http.post<CategoriaProductoResponse>(
      this.apiUrl,
      categoria
    );
  }

  actualizar(
    idCategoria: number,
    categoria: CategoriaProductoRequest
  ): Observable<CategoriaProductoResponse> {

    return this.http.put<CategoriaProductoResponse>(
      `${this.apiUrl}/${idCategoria}`,
      categoria
    );
  }
}