import apps.game_sever.game_logic.weapon_service.Weapon;
public class Player

{   ///Fields
    private String playerId;
    private String name;
    private int health;
    private boolean isAlive;
    private int score;
    private Weapon currentWeapon;

    ///paramenter constructor
    public Player(String playerId, String name)
    {
        this.playerId = playerId; ///player Id
        this.name = name; ///player name
        this.health = 100; // Default health
        this.isAlive = true; // Player starts alive
        this.score = 0; // Initial score
        this.currentWeapon = null; /// weapon 
    }


    ///take damage (assuming damage is allowed only if the player is alive)
    public void takeDamage(int damage)
    {
        health -= damage;

        if (health <= 0 )
        {
            health = 0;
            isAlive = false;  
        }
    }
    ///heal player (assuming healing is allowed only if the player is alive)
    public void heal(int amount)
    {
        if (isAlive)
        {
            health += amount;
            if (health > 100)
            {
                health = 100;
            }
        }
    }

    ///equip weapon
    public void equipWeapon(Weapon weapon)
    {
        this.currentWeapon = weapon;
    }
    
    ///increase score
    public void increaseScore(int points)
    {
        score += points;
    }

    ///get information
    ///playerID
    public String getPlayerId()
    {
        return playerId;
    }

    ///player name
    public String getName()
    {
        return name;
    }

    ///player health
    public int getHealth()
    {
        return health;
    }
    

    ///player alive ?
    public boolean isAlive()
    {
        return isAlive;
    }

    ///getScore
    public int getScore()
    {
        return score;
    }

    ///get weapon
    public Weapon getCurrentWeapon()
    {
        return currentWeapon;
    }
}

