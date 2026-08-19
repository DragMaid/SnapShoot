package room_service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RoomHandleLogic
{
    private Map<String, Room> roomCache;

    public RoomHandleLogic()
    {
    this.roomCache = new ConcurrentHashMap<>();
    }


    public Room createRoom(String creatorId, String name, String password,int limit)
    {
        String sessionId = UUID.randomUUID().toString();
        
        Room room = new Room(
            creatorId,
            sessionId,
            name,
            password,
            limit
        );
        roomCache.put(sessionId, room);
        
        room.addPlayer(creatorId);

        String teamId1 = UUID.randomUUID().toString();
        String color1 = getAvailableColor(room);
        Team team1 = new Team(teamId1, color1);
        team1.addPlayer(creatorId);
        room.addTeam(team1);

        String teamId2 = UUID.randomUUID().toString();
        String color2 = getAvailableColor(room);
        Team team2 = new Team(teamId2, color2);
        room.addTeam(team2);

        return room;
    }


    private String getAvailableColor(Room room) 
    {
    List<String> colors = List.of(
        "RED", "BLUE", "GREEN", "YELLOW",
        "BLACK", "WHITE", "PINK", "GRAY", "ORANGE"
    );

    List<String> usedColors = new ArrayList<>();

    for (Team team : room.getTeams())
    {
        usedColors.add(team.getColor());
    }

    List<String> availableColors = new ArrayList<>(colors);
    availableColors.removeAll(usedColors);

    Random random = new Random();

    return availableColors.get(
        random.nextInt(availableColors.size())
    );
    }


    public boolean joinRoom(String sessionId, String playerId)
    {
        Room room = roomCache.get(sessionId);

        if (room == null)
        {
            return false;
        }

        return room.addPlayer(playerId);
    }


     
    public boolean joinTeam(String sessionId, String playerId, String teamId)
    {
        Room room = roomCache.get(sessionId);

        if (room == null)
        {
            return false;
        }

        if (!room.hasPlayer(playerId))
        {
            return false;
        }

        Team targetTeam = null;

        for (Team team : room.getTeams())
        {
            if (team.getTeamId().equals(teamId))
            {
                targetTeam = team;
                break;
            }
        }

        if (targetTeam == null)
        {
            return false;
        }

        for (Team team : room.getTeams())
        {
            if (team.getPlayerIds().contains(playerId))
            {
                return false;
            }
        }

        return targetTeam.addPlayer(playerId);
    }


    public boolean deleteTeam(String sessionId, String creatorId, String teamId)
    {
        Room room = roomCache.get(sessionId);

        if (room == null)
        {
            return false;
        }

        if (!room.getCreatorId().equals(creatorId))
        {
            return false;
        }

        if (room.getTeams().size() <= 2)
        {
            return false;
        }

        Team targetTeam = null;

        for (Team team : room.getTeams())
        {
            if (team.getTeamId().equals(teamId))
            {
                targetTeam = team;
                break;
            }
        }

        if (targetTeam == null)
        {
            return false;
        }

        if (!targetTeam.getPlayerIds().isEmpty())
        {
            return false;
        }

        return room.removeTeam(targetTeam);
    }
    

    public boolean leaveRoom(String sessionId, String playerId)
    {
        Room room = roomCache.get(sessionId);

        if (room == null)
        {
            return false;
        }

        if (!room.hasPlayer(playerId))
        {
            return false;
        }

        for (Team team : room.getTeams())
        {
            if (team.getPlayerIds().contains(playerId))
            {
                team.removePlayer(playerId);
                break;
            }
        }

        room.removePlayer(playerId);

        if (!room.getCreatorId().equals(playerId))
        {
            return true;
        }

        if (room.getPlayerCount() == 0)
        {
            roomCache.remove(sessionId);
            return true;
        }

        String newCreatorId = room.getPlayerIds().get(0);
        room.setCreatorId(newCreatorId);

        return true;
    }
}