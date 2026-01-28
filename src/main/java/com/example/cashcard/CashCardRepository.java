package com.example.cashcard;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface CashCardRepository extends CrudRepository<CashCard, UUID>
{
}
