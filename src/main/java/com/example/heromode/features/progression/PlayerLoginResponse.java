package com.example.heromode.features.progression;

import com.example.heromode.features.godmode.Challenge;
import com.example.heromode.features.missions.MissionLog;
import java.util.List;

public class PlayerLoginResponse {

    private Player player;
    private List<MissionLog> todayMissions;
    private boolean penalized;
    private List<Challenge> todayChallenges;

    public PlayerLoginResponse(Player player,
                               List<MissionLog> todayMissions,
                               boolean penalized,
                               List<Challenge> todayChallenges) {
        this.player = player;
        this.todayMissions = todayMissions;
        this.penalized = penalized;
        this.todayChallenges = todayChallenges;
    }

    public Player getPlayer() { return player; }
    public List<MissionLog> getTodayMissions() { return todayMissions; }
    public boolean isPenalized() { return penalized; }
    public List<Challenge> getTodayChallenges() { return todayChallenges; }

}
