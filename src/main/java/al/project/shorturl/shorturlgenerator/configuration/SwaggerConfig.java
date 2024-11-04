package al.project.shorturl.shorturlgenerator.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("URL Shortener API")
                        .version("1.0")
                        .description("API for shortening URLs, tracking clicks, and managing expiration times.")
                        .contact(new Contact().name("Anxhelo Lame").email("anxhelolame@gmail.com")));
    }
}
