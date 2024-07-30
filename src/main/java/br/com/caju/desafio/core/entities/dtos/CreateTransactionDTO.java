package br.com.caju.desafio.core.entities.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTransactionDTO {
    @NotBlank(message = "Account cannot be blank")
    @NotNull(message = "Account cannot be null")
    private String account;

    @Min(value = 0, message = "Amount cannot be negative")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal totalAmount;

    @NotNull(message = "MCC cannot be null")
    private Integer mcc;

    @NotBlank(message = "Merchant cannot be blank")
    @NotNull(message = "Merchant cannot be null")
    private String merchant;
}
