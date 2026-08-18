package room_service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RoomService
{
    private Map<String, Room> roomCache;

    public RoomService()
    {
    this.roomCache = new HashMap<>();
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
        String teamId = UUID.randomUUID().toString();
        String color = getAvailableColor(room);
        Team team = new Team(teamId, color);
        team.addPlayer(creatorId);
        room.getTeams().add(team);
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
}