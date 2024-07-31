package br.com.caju.desafio.config;

import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.models.Account;
import br.com.caju.desafio.core.entities.models.Balance;
import br.com.caju.desafio.core.entities.models.Merchant;
import br.com.caju.desafio.core.repositories.AccountRepository;
import br.com.caju.desafio.core.repositories.BalanceRepository;
import br.com.caju.desafio.core.repositories.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public void run(String... args) throws Exception {
        Account acc = new Account(1L, "1");
        this.accountRepository.save(acc);

        this.balanceRepository.save(new Balance(1L, acc, MerchantCategory.MEAL, 10000L));
        this.balanceRepository.save(new Balance(2L, acc, MerchantCategory.FOOD, 20000L));
        this.balanceRepository.save(new Balance(3L, acc, MerchantCategory.CASH, 30000L));

        this.merchantRepository.save(new Merchant(1L, "SUPER MARKET", MerchantCategory.FOOD));
        this.merchantRepository.save(new Merchant(2L, "PAGSEGURO", MerchantCategory.CASH));
        this.merchantRepository.save(new Merchant(3L, "UBER EATS", MerchantCategory.MEAL));
    }
}
