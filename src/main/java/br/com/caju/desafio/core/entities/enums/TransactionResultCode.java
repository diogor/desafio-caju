package br.com.caju.desafio.core.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum TransactionResultCode {
    APPROVED("00"),
    INSUFFICIENT_FUNDS("51"),
    ERROR("07");

    private final String code;
}
