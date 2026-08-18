package room_service;
import java.util.ArrayList;
import java.util.List;
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
    ///ADD PLAYER TO TEAM
    public boolean addPlayer(String playerId)
    {
        if (playerIds.contains(playerId))
        {
            return false;
        }

        playerIds.add(playerId);
        return true;
    }
    ///REMOVE PLAYER
    public void removePlayer(String playerId)
    {
        playerIds.remove(playerId);
    }

    ///get team Id
    public String getTeamId()
    {
        return teamId;
    }

    ///get color 
    public String getColor()
    {
        return color;
    }

    ///get teamId
    public List<String>getPlayerIds()
    {
        return playerIds;
    }
}
