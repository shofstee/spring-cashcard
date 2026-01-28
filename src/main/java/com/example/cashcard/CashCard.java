package com.example.cashcard;

import java.util.UUID;

import lombok.Builder;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Builder
@Table
record CashCard(
	@Id
	UUID id,
	Double amount
){}