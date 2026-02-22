package com.example.heromode.features.progression;

import com.example.heromode.features.missions.MissionLog;
import java.util.List;

public class PlayerLoginResponse {

    private Player player;
    private List<MissionLog> todayMissions;
    private boolean penalized;

    public PlayerLoginResponse(Player players,
                               List<MissionLog> todayMissions,
                               boolean penalized) {
        this.player = players;
        this.todayMissions = todayMissions;
        this.penalized = penalized;
    }

    public Player getPlayer() { return player; }
    public List<MissionLog> getTodayMissions() { return todayMissions; }
    public boolean isPenalized() { return penalized; }

}
