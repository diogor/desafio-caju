package br.com.caju.desafio.core.repositories;

import br.com.caju.desafio.core.entities.models.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    @Query("SELECT m FROM Merchant m WHERE m.name ilike :name")
    Optional<Merchant> findByName(@Param("name") String name);
}