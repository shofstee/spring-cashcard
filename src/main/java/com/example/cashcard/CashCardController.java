package com.example.cashcard;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cashcards")
public class CashCardController
{
	private final CashCardRepository cashCardRepository;

	public CashCardController(final CashCardRepository cashCardRepository)
	{
		this.cashCardRepository = cashCardRepository;
	}

	@GetMapping("/{id}")
	private ResponseEntity<CashCard> findById(@PathVariable final UUID id)
	{
		return cashCardRepository.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	private ResponseEntity<Void> createCashCard(@RequestBody final CashCard cashCard)
	{
		final CashCard newCashCard = CashCard.builder()
				.id(UUID.randomUUID())
				.amount(cashCard.amount())
				.build();
		cashCardRepository.save(newCashCard);
		return ResponseEntity.created(
				java.net.URI.create("/cashcards/" + newCashCard.id()))
			.build();
	}
}

