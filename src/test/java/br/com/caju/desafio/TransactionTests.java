package br.com.caju.desafio;

import br.com.caju.desafio.core.definitions.MerchantCategories;
import br.com.caju.desafio.core.entities.dtos.CreateTransactionDTO;
import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.enums.TransactionResultCode;
import br.com.caju.desafio.core.entities.models.Account;
import br.com.caju.desafio.core.entities.models.Balance;
import br.com.caju.desafio.core.entities.models.Merchant;
import br.com.caju.desafio.core.repositories.AccountRepository;
import br.com.caju.desafio.core.repositories.BalanceRepository;
import br.com.caju.desafio.core.repositories.MerchantRepository;
import br.com.caju.desafio.core.repositories.TransactionRepository;
import br.com.caju.desafio.core.service.AccountService;
import br.com.caju.desafio.core.service.LockService;
import br.com.caju.desafio.core.service.TransactionService;
import br.com.caju.desafio.web.http.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
class TransactionTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MerchantCategories merchantCategories;

    @Autowired
    private MerchantRepository merchantRepository;

    private TransactionService transactionService;

    private Account account;

    @BeforeEach
    void setup() {
        account = new Account();
        Balance balanceCash = new Balance();
        Balance balanceMeal = new Balance();
        Balance balanceFood = new Balance();

        Merchant merchantFood = new Merchant();
        Merchant merchantCash = new Merchant();
        Merchant merchantMeal = new Merchant();

        account.setAccountId("1");
        balanceMeal.setAmount(10000L);
        balanceMeal.setAccount(account);
        balanceMeal.setMcc(MerchantCategory.MEAL);

        balanceCash.setAmount(20000L);
        balanceCash.setAccount(account);
        balanceCash.setMcc(MerchantCategory.CASH);

        balanceFood.setAmount(30000L);
        balanceFood.setAccount(account);
        balanceFood.setMcc(MerchantCategory.FOOD);

        merchantFood.setName("SUPER MARKET");
        merchantCash.setName("PAGSEGURO");
        merchantMeal.setName("UBER EATS");

        merchantFood.setMcc(MerchantCategory.FOOD);
        merchantCash.setMcc(MerchantCategory.CASH);
        merchantMeal.setMcc(MerchantCategory.MEAL);

        entityManager.persist(account);
        entityManager.persist(balanceMeal);
        entityManager.persist(balanceCash);
        entityManager.persist(balanceFood);
        entityManager.persist(merchantFood);
        entityManager.persist(merchantCash);
        entityManager.persist(merchantMeal);

        LockService lockService = new LockService();
        AccountService accountService = new AccountService(accountRepository, balanceRepository, transactionRepository);
        transactionService = new TransactionService(accountRepository, lockService, accountService, merchantCategories, merchantRepository);
    }

    @Test
    void insufficientFunds() throws NotFoundException {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setMcc("1");
        transactionDTO.setAccount("1");
        transactionDTO.setMerchant("test");
        transactionDTO.setTotalAmount(new BigDecimal("201.01"));

        TransactionResultCode result = transactionService.processTransaction(transactionDTO);

        assertEquals(TransactionResultCode.INSUFFICIENT_FUNDS, result);
    }

    @Test
    void fallbackToCash() throws NotFoundException {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setMcc("5812"); // MEAL mcc
        transactionDTO.setAccount("1");
        transactionDTO.setMerchant("test");
        transactionDTO.setTotalAmount(new BigDecimal("100.01"));

        TransactionResultCode result = transactionService.processTransaction(transactionDTO);

        assertEquals(TransactionResultCode.APPROVED, result);

        entityManager.clear();

        Balance updatedCashBalance = balanceRepository.findByAccountAndMcc(account, MerchantCategory.CASH).get();
        Balance updatedMealBalance = balanceRepository.findByAccountAndMcc(account, MerchantCategory.MEAL).get();

        assertEquals(9999L, updatedCashBalance.getAmount().longValue());
        assertEquals(10000L, updatedMealBalance.getAmount().longValue());
    }

    @Test
    void fallBackToCashAndInsufficientFunds() throws NotFoundException {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setMcc("5812"); // MEAL mcc
        transactionDTO.setAccount("1");
        transactionDTO.setMerchant("test");
        transactionDTO.setTotalAmount(new BigDecimal("200.01"));

        TransactionResultCode result = transactionService.processTransaction(transactionDTO);

        assertEquals(TransactionResultCode.INSUFFICIENT_FUNDS, result);
    }

    @Test
    void usingMerchant() throws NotFoundException {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setMcc("5812"); // MEAL mcc
        transactionDTO.setAccount("1");
        transactionDTO.setMerchant("SUPER MARKET"); // FOOD Merchant
        transactionDTO.setTotalAmount(new BigDecimal("250.50"));

        TransactionResultCode resultCode = transactionService.processTransaction(transactionDTO);

        assertEquals(TransactionResultCode.APPROVED, resultCode);

        entityManager.clear();

        Balance updatedMealBalance = balanceRepository.findByAccountAndMcc(account, MerchantCategory.MEAL).get();
        Balance updatedFoodBalance = balanceRepository.findByAccountAndMcc(account, MerchantCategory.FOOD).get();

        assertEquals(10000L, updatedMealBalance.getAmount().longValue());
        assertEquals(4950L, updatedFoodBalance.getAmount().longValue());
    }

    @Test
    void approveFood() throws NotFoundException {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setMcc("5411"); // FOOD mcc
        transactionDTO.setAccount("1");
        transactionDTO.setMerchant("test");
        transactionDTO.setTotalAmount(new BigDecimal("250.50"));

        TransactionResultCode resultCode = transactionService.processTransaction(transactionDTO);

        assertEquals(TransactionResultCode.APPROVED, resultCode);

        entityManager.clear();

        Balance updatedFoodBalance = balanceRepository.findByAccountAndMcc(account, MerchantCategory.FOOD).get();

        assertEquals(4950L, updatedFoodBalance.getAmount().longValue());
    }

    @Test
    void approveMeal() throws NotFoundException {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setMcc("5811"); // MEAL mcc
        transactionDTO.setAccount("1");
        transactionDTO.setMerchant("test");
        transactionDTO.setTotalAmount(new BigDecimal("50.01"));

        TransactionResultCode resultCode = transactionService.processTransaction(transactionDTO);

        assertEquals(TransactionResultCode.APPROVED, resultCode);

        entityManager.clear();

        Balance updatedBalance = balanceRepository.findByAccountAndMcc(account, MerchantCategory.MEAL).get();

        assertEquals(4999L, updatedBalance.getAmount().longValue());
    }

    @Test
    void approveCash() throws NotFoundException {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setMcc("1"); // CASH
        transactionDTO.setAccount("1");
        transactionDTO.setMerchant("test");
        transactionDTO.setTotalAmount(new BigDecimal("33.01"));

        TransactionResultCode resultCode = transactionService.processTransaction(transactionDTO);

        assertEquals(TransactionResultCode.APPROVED, resultCode);

        entityManager.clear();

        Balance updatedBalance = balanceRepository.findByAccountAndMcc(account, MerchantCategory.CASH).get();

        assertEquals(16699L, updatedBalance.getAmount().longValue());
    }
}