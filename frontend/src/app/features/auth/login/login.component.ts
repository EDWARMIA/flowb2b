import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  formulario: FormGroup;

  cargando = false;
  mensajeError = '';

  mostrarPassword = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {

    this.formulario = this.formBuilder.group({
      correo: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],
      password: [
        '',
        [
          Validators.required
        ]
      ]
    });
  }

  alternarVisibilidadPassword(): void {
    this.mostrarPassword =
      !this.mostrarPassword;
  }

  iniciarSesion(): void {

    this.mensajeError = '';

    if (this.formulario.invalid) {

      this.formulario.markAllAsTouched();

      return;
    }

    this.cargando = true;

    const datos = {
      correo: this.formulario.value.correo,
      password: this.formulario.value.password
    };

    this.authService.login(datos).subscribe({

      next: () => {

        this.cargando = false;

        this.router.navigate([
          '/dashboard'
        ]);
      },

      error: (error) => {

        this.cargando = false;

        if (error.status === 401) {

          this.mensajeError =
            'Correo o contraseña incorrectos.';

        } else {

          this.mensajeError =
            'No se pudo conectar con el servidor.';
        }

        console.error(
          'Error al iniciar sesión:',
          error
        );
      }
    });
  }
}