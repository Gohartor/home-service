package org.example.mapper;

import org.example.dto.wallet.WalletDto;
import org.example.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(target = "userId", source = "user.id")
    WalletDto toDto(Wallet wallet);

    List<WalletDto> toDtoList(List<Wallet> wallets);

    Wallet toEntity(WalletDto walletDto);
}
