package br.com.caju.desafio.entities.models;

import br.com.caju.desafio.entities.enums.MerchantCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "balance", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "mcc"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private MerchantCategory mcc;

    @Column
    private Long amount;
}
