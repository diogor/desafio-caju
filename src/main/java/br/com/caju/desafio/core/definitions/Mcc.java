package br.com.caju.desafio.core.definitions;

import br.com.caju.desafio.core.entities.enums.MerchantCategory;

public record Mcc(Integer value, MerchantCategory category) {
}
