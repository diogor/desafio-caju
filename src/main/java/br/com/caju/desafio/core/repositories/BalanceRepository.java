package br.com.caju.desafio.core.repositories;

import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import br.com.caju.desafio.core.entities.models.Account;
import br.com.caju.desafio.core.entities.models.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    @Query("SELECT b FROM Balance b WHERE b.account = :account AND b.mcc = :mcc")
    Optional<Balance> findByAccountAndMcc(@Param("account") Account account, @Param("mcc") MerchantCategory mcc);

    @Modifying()
    @Query("UPDATE Balance b SET b.amount = b.amount - :amount WHERE b.account = :account AND b.mcc = :mcc")
    void debitAccount(@Param("account") Account account, @Param("mcc") MerchantCategory mcc, @Param("amount") Long amount);
}
