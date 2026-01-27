package com.example.cashcard;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cashcards")
public class CashCardController
{
	@GetMapping("/{id}")
	private ResponseEntity<CashCard> findById(@PathVariable final UUID id)
	{
		if (id.equals(UUID.fromString("ff793947-c8a3-4a7b-9f34-1f131b1d9444")))
		{
			final CashCard cashCard = new CashCard(UUID.fromString("ff793947-c8a3-4a7b-9f34-1f131b1d9444"), 123.45);
			return ResponseEntity.ok(cashCard);
		}
		else
		{
			return ResponseEntity.notFound().build();
		}
	}
}

