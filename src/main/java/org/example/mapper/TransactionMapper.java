package org.example.mapper;

import org.example.dto.transaction.TransactionDto;
import org.example.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {


    TransactionDto toDto(Transaction transaction);

    List<TransactionDto> toDtoList(List<Transaction> transactions);
}