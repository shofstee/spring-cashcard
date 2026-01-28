package com.example.cashcard;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests
{
	private static final String REGEXP_UUIDv7 = "[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}";

	@Autowired
	RestTestClient restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved()
	{
		final String expected = """
			{
			    "id":"019c062e-677f-76d8-b2f4-61c06487a294",
			    "amount":123.45
			}
			""";
		restTemplate.get().uri("/cashcards/019c062e-677f-76d8-b2f4-61c06487a294", String.class)
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

	@DirtiesContext
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
				.matches("^/cashcards/" + REGEXP_UUIDv7 + "$"),
				"Location header with new CashCard URI");
	}



	@Test
	void shouldReturnAllCashCardsWhenListIsRequested()
	{
		final String expextedResult =
		  """
		  [
		  	{"id":"019c062e-677f-76d8-b2f4-61c06487a294","amount":123.45},
		  	{"id":"019c062f-463f-7287-be6b-d75290581aaf","amount":1.0},
		  	{"id":"019c062f-7af1-7098-9f0c-f6ad013d55ea","amount":150.0}
		  ]
		  """;
		restTemplate.get().uri("/cashcards", List.class)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.json(expextedResult);
	}

	@Test
	void shouldReturnAPageOfCashCards()
	{
		final String expextedResult =
			"""
			[
				{"id":"019c062f-463f-7287-be6b-d75290581aaf","amount":1.0}
			]
			""";
		restTemplate.get().uri("/cashcards?page=0&size=1", List.class)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.json(expextedResult);
	}

	@Test
	void shouldReturnASortedPageOfCashCards()
	{
		final String expextedResult =
			"""
			[
				{"id":"019c062f-7af1-7098-9f0c-f6ad013d55ea","amount":150.0}
			]
			""";
		restTemplate.get().uri("/cashcards?page=0&size=1&sort=amount,desc", List.class)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.json(expextedResult);
	}

	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues()
	{
		final String expextedResult =
			"""
			[
				{"id":"019c062f-463f-7287-be6b-d75290581aaf","amount":1.0},
				{"id":"019c062e-677f-76d8-b2f4-61c06487a294","amount":123.45},
				{"id":"019c062f-7af1-7098-9f0c-f6ad013d55ea","amount":150.0}
			]
			""";
		restTemplate.get().uri("/cashcards", List.class)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.json(expextedResult);
	}
}
