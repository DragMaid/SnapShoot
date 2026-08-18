package room_service;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Team
{
    private String teamId;
    private String color;
    private List<String> playerIds;

    public Team(String teamId, String color)
    {
        this.teamId = teamId;
        this.color = color;
        this.playerIds = new ArrayList<>();
    }

    public boolean addPlayer(String playerId)
    {
        if (playerIds.contains(playerId))
        {
            return false;
        }

        playerIds.add(playerId);
        return true;
    }

    public boolean removePlayer(String playerId)
    {
        return playerIds.remove(playerId);
    }
}