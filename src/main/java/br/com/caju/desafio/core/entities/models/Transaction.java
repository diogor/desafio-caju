package br.com.caju.desafio.core.entities.models;

import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "transactions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    @Builder.Default
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column
    private Long amount;

    @Enumerated(EnumType.STRING)
    private MerchantCategory mcc;

    @Column
    private String merchant;
}
