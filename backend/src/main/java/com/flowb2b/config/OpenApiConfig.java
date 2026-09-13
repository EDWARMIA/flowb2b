package com.flowb2b.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME =
            "bearerAuth";

    @Bean
    public OpenAPI flowB2BOpenAPI() {

        return new OpenAPI()

            // =========================
            // INFORMACIÓN DE LA API
            // =========================
            .info(
                new Info()
                    .title("FlowB2B API")
                    .description(
                        """
                        API REST de FlowB2B.

                        Plataforma SaaS multiempresa para gestionar
                        el flujo comercial B2B desde la solicitud
                        del cliente hasta la preparación y entrega
                        del pedido.

                        Incluye autenticación JWT, roles y permisos,
                        clientes, productos, inventario, solicitudes,
                        cotizaciones, aprobaciones, pedidos, tareas,
                        notificaciones, administración y analítica.
                        """
                    )
                    .version("1.0.0")
                    .contact(
                        new Contact()
                            .name("FlowB2B")
                    )
                    .license(
                        new License()
                            .name("Proyecto de portafolio")
                    )
            )

            // =========================
            // JWT GLOBAL
            // =========================
            .addSecurityItem(
                new SecurityRequirement()
                    .addList(SECURITY_SCHEME_NAME)
            )

            // =========================
            // BOTÓN AUTHORIZE
            // =========================
            .components(
                new Components()
                    .addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                            .name(SECURITY_SCHEME_NAME)
                            .type(
                                SecurityScheme.Type.HTTP
                            )
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description(
                                "Ingresa el token JWT obtenido en /api/auth/login"
                            )
                    )
            );
    }
}