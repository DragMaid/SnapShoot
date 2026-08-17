import java.util.List;
import java.util.ArrayList;
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
    public void addPlayer(String playerId)
    {
        playerIds.add(playerId);
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
