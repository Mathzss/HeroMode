package com.example.heromode.features.progression;

import jakarta.persistence.*; // Importa Entity e Table

import java.time.LocalDate;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer level = 1;
    private Long xp = 0L;
    private Integer streak = 0;
    private LocalDate lastLogin;

    // Construtor vazio obrigatorio para o JPA
    public Player() {}


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Long getXp() { return xp; }
    public void setXp(Long xp) { this.xp = xp; }

    public Integer getStreak() { return streak; }
    public void setStreak(Integer streak) { this.streak = streak; }

    public LocalDate getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDate lastLogin) { this.lastLogin = lastLogin; }

    // Lista de regalias/inventory pode vir depois
}

//Tive que remover o lombok de todo o projeto para ele funcionar =(