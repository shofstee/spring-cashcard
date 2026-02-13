package com.example.cashcard;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
	RestTestClient restTestClient;

	@Test
	void shouldReturnACashCardWhenDataIsSaved()
	{
		final String expected = """
			{
			    "id":"019c062e-677f-76d8-b2f4-61c06487a294",
			    "amount":123.45
			}
			""";
		restTestClient.get().uri("/cashcards/019c062e-677f-76d8-b2f4-61c06487a294", String.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.json(expected);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId()
	{
		restTestClient.get().uri("/cashcards/6db400ac-2b2a-4b56-8205-f42a90e8fb89", String.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
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
		final CashCard newCashCard = new CashCard(null, 250.00, null);
		final var headers = restTestClient.post().uri("/cashcards", Void.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
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
			.matches(uri -> uri
					.getPath()
					.matches("^/cashcards/" + REGEXP_UUIDv7 + "$"),
				"Location header with new CashCard URI");
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingCashCard()
	{
		final CashCard newCashCard = new CashCard(UUID.fromString("019c062e-677f-76d8-b2f4-61c06487a294"), 250.00, null);
		restTestClient.put().uri("/cashcards/019c062e-677f-76d8-b2f4-61c06487a294", Void.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
			.body(newCashCard)
			.exchange()
			.expectStatus()
			.isNoContent()
			.expectHeader()
			.doesNotExist(LOCATION)
			.returnResult()
			.getResponseHeaders();

		restTestClient.get().uri("/cashcards/019c062e-677f-76d8-b2f4-61c06487a294", String.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.json("""
				{"id":"019c062e-677f-76d8-b2f4-61c06487a294","amount":250.0}
				""");
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingCashCardFromAnotherPrincipal()
	{
		final CashCard newCashCard = new CashCard(UUID.fromString("019c062e-677f-76d8-b2f4-61c06487a294"), 250.00, null);
		restTestClient.put().uri("/cashcards/019c062e-677f-76d8-b2f4-61c06487a294", Void.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah2", "123abc"))
			.body(newCashCard)
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectHeader();
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
		restTestClient.get().uri("/cashcards", List.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
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
		restTestClient.get().uri("/cashcards?page=0&size=1", List.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
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
		restTestClient.get()
			.uri("/cashcards?page=0&size=1&sort=amount,desc", List.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
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
		restTestClient.get().uri("/cashcards", List.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.json(expextedResult);
	}

	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials()
	{
		restTestClient.get().uri("/cashcards/019c062e-677f-76d8-b2f4-61c06487a294", String.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("hacker", "hacked"))
			.exchange()
			.expectStatus()
			.isUnauthorized()
			.expectBody()
			.isEmpty();
	}

	@Test
	void shouldNotReturnACashCardWhenUsingUserWithoutRole()
	{
		restTestClient.get().uri("/cashcards/019c062e-677f-76d8-b2f4-61c06487a294", String.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("hank-owns-no-cards", "qrs456"))
			.exchange()
			.expectStatus()
			.isForbidden()
			.expectBody()
			.isEmpty();
	}

	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn()
	{
		restTestClient.get().uri("/cashcards/019c0f8b-2238-7110-9a46-a2be3aab8478", String.class)
			.header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("sarah1", "abc123"))
			.exchange()
			.expectStatus()
			.isNotFound()
			.expectBody()
			.isEmpty();
	}

	private String createBasicAuthHeader(final String username, final String password)
	{
		final String auth = username + ":" + password;
		final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
		return "Basic " + new String(encodedAuth);
	}

}
