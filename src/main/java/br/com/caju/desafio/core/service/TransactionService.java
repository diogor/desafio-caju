package br.com.caju.desafio.core.service;

import br.com.caju.desafio.core.definitions.MerchantCategories;
import br.com.caju.desafio.core.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.entities.models.Account;
import br.com.caju.desafio.core.entities.models.Merchant;
import br.com.caju.desafio.core.entities.models.Transaction;
import br.com.caju.desafio.core.repositories.AccountRepository;
import br.com.caju.desafio.core.repositories.MerchantRepository;
import br.com.caju.desafio.web.http.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final MerchantCategories merchantCategories;
    private final AccountService accountService;
    private final MerchantRepository merchantRepository;
    private final LockService lockService;

    public TransactionService(
            AccountRepository accountRepository,
            AccountService accountService,
            MerchantCategories merchantCategories,
            MerchantRepository merchantRepository,
            LockService lockService
    ) {
        this.accountRepository = accountRepository;
        this.merchantCategories = merchantCategories;
        this.accountService = accountService;
        this.merchantRepository = merchantRepository;
        this.lockService = lockService;
    }

    private MerchantCategory getMcc(CreateTransactionDTO createTransactionDTO) {
        Integer value = createTransactionDTO.getMcc();
        Merchant merchant = merchantRepository.findByName(createTransactionDTO.getMerchant()).orElse(null);

        if (merchant != null) {
            return merchant.getMcc();
        }

        try {
            return merchantCategories.mccs().stream().filter(mcc -> Objects.equals(mcc.value(), value)).findFirst().get().category();
        } catch (NoSuchElementException _) {
            return MerchantCategory.CASH;
        }
    }

    private Transaction convertToTransaction(CreateTransactionDTO createTransactionDTO) throws NotFoundException {
        Optional<Account> account = accountRepository.findByAccountId(createTransactionDTO.getAccount());

        if (account.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account.get());
        transaction.setAmount(createTransactionDTO.getTotalAmount().multiply(new BigDecimal(100)).longValue());
        transaction.setMcc(this.getMcc(createTransactionDTO));
        transaction.setMerchant(createTransactionDTO.getMerchant());
        return transaction;
    }


    @Transactional
    public TransactionResultCode processTransaction(CreateTransactionDTO createTransactionDTO) throws NotFoundException {
        Transaction transaction = convertToTransaction(createTransactionDTO);
        String key = transaction.getAccount().getAccountId();

        try {
            lockService.lock(key);
            Long balance = accountService.getAccountBalance(transaction.getAccount().getAccountId(), transaction.getMcc());

            if (transaction.getMcc() != MerchantCategory.CASH) {
                if (balance < transaction.getAmount())
                    return accountService.debitAccount(transaction, MerchantCategory.CASH);
            }
            return accountService.debitAccount(transaction, transaction.getMcc());
        } finally {
            lockService.unlock(key);
        }
    }
}
