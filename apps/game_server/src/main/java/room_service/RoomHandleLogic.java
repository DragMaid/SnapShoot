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


    ///create room function
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
        
        ///add creator to room
        room.addPlayer(creatorId);

        ///create team 1 default and add creator to the team 1 
        String teamId1 = UUID.randomUUID().toString();
        String color1 = getAvailableColor(room);
        Team team1 = new Team(teamId1, color1);
        team1.addPlayer(creatorId);
        room.addTeam(team1);

        ///create team 2 default and non-player until players join teams
        String teamId2 = UUID.randomUUID().toString();
        String color2 = getAvailableColor(room);
        Team team2 = new Team(teamId2, color2);
        room.addTeam(team2);

        return room;
    }

    ///random colors
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

    /// Allow player to join room
    public boolean joinRoom(String sessionId, String playerId)
    {
        Room room = roomCache.get(sessionId);

        if (room == null)
        {
            return false;
        }

        return room.addPlayer(playerId);
    }

     
    /// Allow player to join team
    public boolean joinTeam(String sessionId, String playerId, String teamId)
    {
        // Find room
        Room room = roomCache.get(sessionId);

        // Room does not exist
        if (room == null)
        {
            return false;
        }

        // Player must be in the room first
        if (!room.hasPlayer(playerId))
        {
            return false;
        }

        // Find target team
        Team targetTeam = null;

        for (Team team : room.getTeams())
        {
            if (team.getTeamId().equals(teamId))
            {
                targetTeam = team;
                break;
            }
        }

        // Team does not exist
        if (targetTeam == null)
        {
            return false;
        }

        // Check whether player is already in another team
        for (Team team : room.getTeams())
        {
            if (team.getPlayerIds().contains(playerId))
            {
                return false;
            }
        }

        // Add player to selected team
        return targetTeam.addPlayer(playerId);
    }

    /// Allow creators delete room only when VALID the RULE of rocksolid
    public boolean deleteTeam(String sessionId, String creatorId, String teamId)
    {
        Room room = roomCache.get(sessionId);

        // Room does not exist
        if (room == null)
        {
            return false;
        }

        // Only creator can delete a team
        if (!room.getCreatorId().equals(creatorId))
        {
            return false;
        }

        // Room must keep at least 2 teams
        if (room.getTeams().size() <= 2)
        {
            return false;
        }

        // Find the team
        Team targetTeam = null;

        for (Team team : room.getTeams())
        {
            if (team.getTeamId().equals(teamId))
            {
                targetTeam = team;
                break;
            }
        }

        // Team does not exist
        if (targetTeam == null)
        {
            return false;
        }

        // Team must be empty before deletion
        if (!targetTeam.getPlayerIds().isEmpty())
        {
            return false;
        }

        // Remove team
        return room.removeTeam(targetTeam);
    }

    /// Allow player to leave the room
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

        // Remove player from their team
        for (Team team : room.getTeams())
        {
            if (team.getPlayerIds().contains(playerId))
            {
                team.removePlayer(playerId);
                break;
            }
        }

        // Remove player from room
        room.removePlayer(playerId);

        // Player is not creator
        if (!room.getCreatorId().equals(playerId))
        {
            return true;
        }

        // Creator was the last player
        if (room.getPlayerCount() == 0)
        {
            roomCache.remove(sessionId);
            return true;
        }

        // Transfer ownership to another player
        String newCreatorId = room.getPlayerIds().get(0);
        room.setCreatorId(newCreatorId);

        return true;
    }
}