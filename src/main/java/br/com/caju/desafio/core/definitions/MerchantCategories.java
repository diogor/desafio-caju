package br.com.caju.desafio.core.definitions;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("business")
public record MerchantCategories(List<Mcc> mccs) {
}
