package br.com.caju.desafio.entities.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTransactionDTO {
    private String account;
    private BigDecimal totalAmount;
    private Integer mcc;
    private String merchant;
}
