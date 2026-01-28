package com.example.cashcard;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CashCardRepository extends
	CrudRepository<CashCard, UUID>,
	PagingAndSortingRepository<CashCard, UUID>
{

}
