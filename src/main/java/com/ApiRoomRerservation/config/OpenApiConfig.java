package com.ApiRoomRerservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI roomReservationOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Room Reservation API")
                .description("API REST para gerenciamento de usuários, salas, disponibilidade e reservas de salas.")
                .version("1.0"));
    }
}
