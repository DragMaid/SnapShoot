import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
public class Room
{
    private String creatorId;
    private String sessionId;
    private String name;
    private String password;
    private int limit;
    private LocalDateTime createdAt;
    private List<Team> teams;


    ///constructor
    public Room(String creatorId, String sessionId, String name, String password,int limit, LocalDateTime createdAt)
    {
        this.creatorId = creatorId;
        this.sessionId = sessionId;
        this.name = name;
        this.password = password;
        this.limit = limit;
        this.createdAt = createdAt;
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

    ///get local tome 
    public LocalDateTime getCreatedAt()
    {
        return createdAt;
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
        for (Team team : teams);
        {
            count += team.getPlayerIds().size();
        }
        return count;
    }
}

