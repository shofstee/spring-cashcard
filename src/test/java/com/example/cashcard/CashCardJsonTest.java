package com.example.cashcard;

import java.io.IOException;
import java.util.UUID;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CashCardJsonTest
{

	@Autowired
	private JacksonTester<CashCard> json;

	@Autowired
	private JacksonTester<CashCard[]> jsonList;

	@Test
	void cashCardSerializationTest() throws IOException
	{
		final CashCard cashCard = new CashCard(UUID.fromString("019c062e-677f-76d8-b2f4-61c06487a294"), 123.45);
		assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");
		assertThat(json.write(cashCard)).hasJsonPathValue("@.id");
		assertThat(json.write(cashCard)).extractingJsonPathValue("@.id")
			.isEqualTo("019c062e-677f-76d8-b2f4-61c06487a294");
		assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
		assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
			.isEqualTo(123.45);
	}

	@Test
	void cashCardDeserializationTest() throws IOException
	{
		final String expected = """
			{
			    "id":"019c062e-677f-76d8-b2f4-61c06487a294",
			    "amount":123.45
			}
			""";
		assertThat(json.parse(expected))
			.isEqualTo(new CashCard(UUID.fromString("019c062e-677f-76d8-b2f4-61c06487a294"), 123.45));

		assertThat(json.parseObject(expected).id()).isEqualTo(UUID.fromString("019c062e-677f-76d8-b2f4-61c06487a294"));
		assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
	}

	@Test
	void cashCardListSerializationTest() throws IOException
	{
		final var cashCards = Arrays.array(
			new CashCard(UUID.fromString("019c062e-677f-76d8-b2f4-61c06487a294"), 123.45),
			new CashCard(UUID.fromString("019c062f-463f-7287-be6b-d75290581aaf"), 100.00),
			new CashCard(UUID.fromString("019c062f-7af1-7098-9f0c-f6ad013d55ea"), 150.00));
		assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
	}

	@Test
	void cashCardListDeserializationTest() throws IOException
	{
		final String expected = """
			[
			   { "id": "019c062e-677f-76d8-b2f4-61c06487a294", "amount": 123.45 },
			   { "id": "019c062f-463f-7287-be6b-d75290581aaf", "amount": 100.00 },
			   { "id": "019c062f-7af1-7098-9f0c-f6ad013d55ea", "amount": 150.00 }
			]
			""";

		final var cashCards = Arrays.array(
			new CashCard(UUID.fromString("019c062e-677f-76d8-b2f4-61c06487a294"), 123.45),
			new CashCard(UUID.fromString("019c062f-463f-7287-be6b-d75290581aaf"), 100.00),
			new CashCard(UUID.fromString("019c062f-7af1-7098-9f0c-f6ad013d55ea"), 150.00));

		assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
	}
}
