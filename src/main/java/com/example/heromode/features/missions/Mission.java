package com.example.heromode.features.missions;

import jakarta.persistence.*;

@Entity
@Table(name = "missions")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String category;
    private String difficulty;
    private Integer xpValue;

    // Construtor Vazio (Obrigatório para o JPA)
    public Mission() {}

    // Getters e Setters (Gerar com Alt+Insert -> Getter and Setter)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getXpValue() { return xpValue; }
    public void setXpValue(Integer xpValue) { this.xpValue = xpValue; }
}