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
    private List<Team> teams;


    ///constructor
    public Room(String creatorId, String sessionId, String name, String password,int limit)
    {
        this.creatorId = creatorId;
        this.sessionId = sessionId;
        this.name = name;
        this.password = password;
        this.limit = limit;
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


    ///get teams 1 room
    public List<Team> getTeams()
    {
        return teams;
    }

    ///count player limit in 1 room if enough full not accepts
    public int getPlayerCount()
    {
        int count = 0;
        for (Team team : teams)
        {
            count += team.getPlayerIds().size();
        }
        return count;
    }
}

