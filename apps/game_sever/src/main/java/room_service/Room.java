package room_service;
import java.util.ArrayList;
import java.util.List;
public class Room
{
    private String creatorId;
    private String sessionId;
    private String name;
    private String password;
    private int limit;
    private List<String> playerIds;
    private List<Team> teams;


    ///constructor
    public Room(String creatorId, String sessionId, String name, String password,int limit)
    {
        this.creatorId = creatorId;
        this.sessionId = sessionId;
        this.name = name;
        this.password = password;
        this.limit = limit;
        this.playerIds = new ArrayList <>();
        this.teams = new ArrayList<>();
    }

    ///get information
    ///get creater ID
    public String getCreatorId()
    {
        return creatorId;
    }

    ///get sesion Id
    public String getSessionId()
    {
        return sessionId;
    }

    ///get name
    public String getName()
    {
        return name;
    }

    ///get password 
    public String getPassword()
    {
        return password;
    }

    ///get limit player in 1 room 
    public int getLimit()
    {
        return limit;
    }

    ///add player to room if valid 
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

    ///get teams 1 room
    public List<Team> getTeams()
    {
        return teams;
    }

    ///allow creator create more teams
    public void addTeam(Team team)
    {
        teams.add(team);
    }


    ///check whether player in the room
    public boolean hasPlayer(String playerId)
    {
        return playerIds.contains(playerId);
    }

    ///get number of players in rooom
    public int getPlayerCount()
    {
        return playerIds.size();
    }

    ///remove teams
    public boolean removeTeam(Team team)
    {
        return teams.remove(team);
    }
}

