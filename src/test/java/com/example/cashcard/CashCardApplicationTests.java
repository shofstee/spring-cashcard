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
			    "id":"ff793947-c8a3-4a7b-9f34-1f131b1d9444",
			    "amount":123.45
			}
			""";
		restTemplate.get().uri("/cashcards/ff793947-c8a3-4a7b-9f34-1f131b1d9444", String.class)
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.json(expected);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId()
	{
		restTemplate.get().uri("/cashcards/6db400ac-2b2a-4b56-8205-f42a90e8fb89", String.class)
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty();
	}

}
