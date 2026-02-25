package com.example.heromode.features.missions;

import com.example.heromode.features.progression.Player;
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

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

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

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
}