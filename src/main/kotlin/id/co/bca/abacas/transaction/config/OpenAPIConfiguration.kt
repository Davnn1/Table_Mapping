package id.co.bca.abacas.transaction.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenAPIConfiguration {
    @Bean
    fun springShopOpenAPI(): OpenAPI? {
        return OpenAPI()
            .info(Info().title("API Transaction")
                .description("Service Transaksi")
                .version("1.0.0")
                .contact(Contact().name("").url("").email(""))
            )
    }
}