package com.example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CashCardJsonTest {

	@Autowired
	private JacksonTester<CashCard> json;

	@Test
	void cashCardSerializationTest() throws IOException {
		final CashCard cashCard = new CashCard(UUID.fromString("ff793947-c8a3-4a7b-9f34-1f131b1d9444"), 123.45);
		assertThat(json.write(cashCard)).isStrictlyEqualToJson("expected.json");
		assertThat(json.write(cashCard)).hasJsonPathValue("@.id");
		assertThat(json.write(cashCard)).extractingJsonPathValue("@.id")
			.isEqualTo("ff793947-c8a3-4a7b-9f34-1f131b1d9444");
		assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
		assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
			.isEqualTo(123.45);
	}

	@Test
	void cashCardDeserializationTest() throws IOException
	{
		final String expected = """
			{
			    "id":"ff793947-c8a3-4a7b-9f34-1f131b1d9444",
			    "amount":123.45
			}
			""";
		assertThat(json.parse(expected))
			.isEqualTo(new CashCard(UUID.fromString("ff793947-c8a3-4a7b-9f34-1f131b1d9444"), 123.45));

		assertThat(json.parseObject(expected).id()).isEqualTo(UUID.fromString("ff793947-c8a3-4a7b-9f34-1f131b1d9444"));
		assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
	}
}
