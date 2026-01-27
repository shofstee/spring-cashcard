package com.example.cashcard;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

interface CashCardRepository/// extends CrudRepository<CashCard, UUID>
{
}
