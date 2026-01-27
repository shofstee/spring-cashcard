package com.example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests
{
	@Autowired
	RestTestClient restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved()
	{
		final String expected = """
			{
			    "id":99,
			    "amount":123.45
			}
			""";
		restTemplate.get().uri("/cashcards/99", String.class)
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.json(expected);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId()
	{
		restTemplate.get().uri("/cashcards/100", String.class)
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty();
	}

}
