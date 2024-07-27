package br.com.caju.desafio.entities.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String accountId;
}
