package id.co.bca.abacas.transaction.controller


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureWebTestClient
class RootControllerTest {
	@Autowired
	lateinit var webTestClient: WebTestClient
	
	@Test
	fun base() {
		webTestClient.get()
			.uri("/swagger-ui")
			.exchange()
			.expectStatus().is2xxSuccessful
	}

}