package foodapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class ApplicationSwaggerConfig {

    @Bean
    OpenAPI foodAppOpenAPI() {

        Contact contact = new Contact()
                .name("Kartik G")
                .email("gidaveerk@gmail.com");

        Info apiInfo = new Info()
                .title("FoodApp API")
                .description("API documentation for FoodApp backend services")
                .version("1.0")
                .contact(contact);

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local development server");

        return new OpenAPI()
                .info(apiInfo)
                .addServersItem(localServer);
    }
}
