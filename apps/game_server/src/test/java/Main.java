import room_service.Room;
import room_service.RoomHandleLogic;
import room_service.Team;

public class Main
{
    public static void main(String[] args)
    {
        RoomHandleLogic roomService = new RoomHandleLogic();

        // ========================================
        // 1. CREATE ROOM
        // ========================================

        String creatorId = "An";

        Room room = roomService.createRoom(
            creatorId,
            "Test Room",
            "1234",
            4
        );

        System.out.println("=== CREATE ROOM ===");
        System.out.println("Session ID: " + room.getSessionId());
        System.out.println("Creator: " + room.getCreatorId());
        System.out.println("Players: " + room.getPlayerCount());
        System.out.println("Teams: " + room.getTeams().size());


        // ========================================
        // 2. JOIN ROOM
        // ========================================

        String playerId = "Bob";

        boolean joined = roomService.joinRoom(
            room.getSessionId(),
            playerId
        );

        System.out.println("\n=== JOIN ROOM ===");
        System.out.println("Bob joined: " + joined);
        System.out.println("Players: " + room.getPlayerCount());
        System.out.println("Bob in room: " + room.hasPlayer(playerId));


        // ========================================
        // 3. SHOW TEAMS
        // ========================================

        System.out.println("\n=== TEAMS ===");

        for (Team team : room.getTeams())
        {
            System.out.println(
                "Team ID: " + team.getTeamId()
                + " | Color: " + team.getColor()
                + " | Players: " + team.getPlayerIds()
            );
        }


        // ========================================
        // 4. BOB JOIN TEAM
        // ========================================

        Team firstTeam = room.getTeams().get(0);

        boolean joinedTeam = roomService.joinTeam(
            room.getSessionId(),
            playerId,
            firstTeam.getTeamId()
        );

        System.out.println("\n=== JOIN TEAM ===");
        System.out.println("Bob joined team: " + joinedTeam);
        System.out.println("Team players: " + firstTeam.getPlayerIds());


        // ========================================
        // 5. CREATOR LEAVES ROOM
        // ========================================

        System.out.println("\n=== BEFORE CREATOR LEAVES ===");
        System.out.println("Creator: " + room.getCreatorId());
        System.out.println("Players: " + room.getPlayerIds());

        boolean creatorLeft = roomService.leaveRoom(
            room.getSessionId(),
            creatorId
        );

        System.out.println("\n=== CREATOR LEAVES ===");
        System.out.println("Creator left: " + creatorLeft);
        System.out.println("New Creator: " + room.getCreatorId());
        System.out.println("Players: " + room.getPlayerIds());
    }
}