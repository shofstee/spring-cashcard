package com.example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests
{
	private static final String REGEXP_UUID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

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

	@Test
	void shouldCreateANewCashCard()
	{
		final CashCard newCashCard = new CashCard(null, 250.00);
		final var headers = restTemplate.post().uri("/cashcards", Void.class)
			.body(newCashCard)
			.exchange()
			.expectStatus()
			.isCreated()
			.expectHeader()
			.exists(LOCATION)
			.returnResult()
			.getResponseHeaders();
		final var location = headers.getLocation();
		assertThat(location)
			.matches(uri-> uri
				.getPath()
				.matches("^/cashcards/" + REGEXP_UUID + "$"),
				"Location header with new CashCard URI");

	}
}
