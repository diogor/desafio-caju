package br.com.caju.desafio.core.entities.models;

import br.com.caju.desafio.core.entities.enums.MerchantCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "merchants")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    private MerchantCategory mcc;
}
