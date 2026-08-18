package room_service;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Room
{
    private String creatorId;
    private String sessionId;
    private String name;
    private String password;
    private int limit;
    private List<String> playerIds;
    private List<Team> teams;

    public Room(
        String creatorId,
        String sessionId,
        String name,
        String password,
        int limit
    )
    {
        this.creatorId = creatorId;
        this.sessionId = sessionId;
        this.name = name;
        this.password = password;
        this.limit = limit;
        this.playerIds = new ArrayList<>();
        this.teams = new ArrayList<>();
    }

    public boolean addPlayer(String playerId)
    {
        if (playerIds.contains(playerId))
        {
            return false;
        }

        if (playerIds.size() >= limit)
        {
            return false;
        }

        playerIds.add(playerId);
        return true;
    }

    public void addTeam(Team team)
    {
        teams.add(team);
    }

    public boolean hasPlayer(String playerId)
    {
        return playerIds.contains(playerId);
    }

    public int getPlayerCount()
    {
        return playerIds.size();
    }

    public boolean removeTeam(Team team)
    {
        return teams.remove(team);
    }

    public boolean removePlayer(String playerId)
    {
        return playerIds.remove(playerId);
    }

    public void setCreatorId(String newCreatorId)
    {
        this.creatorId = newCreatorId;
    }
}