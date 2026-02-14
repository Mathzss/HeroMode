package com.example.heromode.features.missions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

@Entity
@Getter // Do Lombok
@Setter // Do Lombok
@NoArgsConstructor
@Table(name = "missions")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category; // Estudo, Saúde, etc.
    private String difficulty; // Easy, Medium, Hard
    private Integer xpValue;

    @ManyToOne
    private User user; // Para vincular ao seu perfil
}