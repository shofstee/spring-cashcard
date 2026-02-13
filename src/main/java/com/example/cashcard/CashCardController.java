package com.example.cashcard;

import java.security.Principal;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

@RestController
@RequestMapping("/cashcards")
public class CashCardController
{
	private static final TimeBasedEpochGenerator timebasedEpochGenerator = Generators.timeBasedEpochGenerator();

	private final CashCardRepository cashCardRepository;

	public CashCardController(final CashCardRepository cashCardRepository)
	{
		this.cashCardRepository = cashCardRepository;
	}

	@GetMapping("/{id}")
	private ResponseEntity<CashCard> findById(@PathVariable final UUID id, final Principal principal)
	{
		return cashCardRepository.findByIdAndOwner(id, principal.getName())
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping
	private ResponseEntity<Iterable<CashCard>> findAll(final Pageable pageable, final Principal principal)
	{
		final var page = cashCardRepository.findByOwner(principal.getName(), PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));
		return ResponseEntity.ok(page.getContent());
	}

	@PostMapping
	private ResponseEntity<Void> createCashCard(@RequestBody final CashCard cashCard, final Principal principal)
	{
		final CashCard newCashCard = CashCard.builder()
			.id(timebasedEpochGenerator.generate())
			.amount(cashCard.amount())
			.owner(principal.getName())
			.build();
		cashCardRepository.save(newCashCard);
		return ResponseEntity.created(
				java.net.URI.create("/cashcards/" + newCashCard.id()))
			.build();
	}

	@PutMapping(("/{requestedId}"))
	private ResponseEntity<Void> upsertCashCard(
		@PathVariable @NonNull final UUID requestedId,
		@RequestBody @NonNull final CashCard cashCard,
		@NonNull final Principal principal)
	{
		final var existingCard = cashCardRepository.findById(requestedId);
		if (existingCard.isPresent() && !existingCard.get().owner().equals(principal.getName()))
		{
			return ResponseEntity.notFound().build();
		}

		final CashCard newCashCard = CashCard.builder()
			.id(requestedId)
			.amount(cashCard.amount())
			.owner(principal.getName())
			.build();
		cashCardRepository.save(newCashCard);
		return ResponseEntity.noContent().build();
	}
}

