package com.example.heromode.features.player;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer level = 1;
    private Long xp = 0L;
    private Integer streak = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Long getXp() { return xp; }
    public void setXp(Long xp) { this.xp = xp; }
    public Integer getStreak() { return streak; }
    public void setStreak(Integer streak) { this.streak = streak; }

    // Lista de regalias/inventory pode vir depois
}

//Tive que remover o lombok de todo o projeto para ele funcionar =(