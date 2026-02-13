package com.example.cashcard;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CashCardRepository extends
	CrudRepository<CashCard, UUID>,
	PagingAndSortingRepository<CashCard, UUID>
{
	Optional<CashCard> findByIdAndOwner(UUID id, String owner);

	Page<CashCard> findByOwner(String owner, PageRequest pageRequest);

}
