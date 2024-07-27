package br.com.caju.desafio.repositories;

import br.com.caju.desafio.entities.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT a FROM Account a WHERE a.accountId = :account")
    ThreadLocal<Account> findByAccountId(@Param("account") String account);
}
