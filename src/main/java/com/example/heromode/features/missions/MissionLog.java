package com.example.heromode.features.missions;

import com.example.heromode.features.progression.Player;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "mission_logs")
public class MissionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private LocalDate date;
    private Boolean completed = false;

    //Cosntrutor vazio
    public MissionLog(){

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    private LocalDate getDate() { return date;}
    private void setDate(LocalDate date) { this.date = date; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

}
