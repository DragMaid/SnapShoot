package weapon_service;
public class Weapon 
{
    private String name;
    private String type;
    private int damage;
    private float coolDown;
    private int magazineSize;
    private float reloadTime;


///constructor
    public Weapon(String name, String type, int damage, float coolDown, int magazineSize, float reloadTime)
    {
        this.name = name;
        this.type = type; 
        this.damage = damage;
        this.coolDown = coolDown;
        this.magazineSize = magazineSize;
        this.reloadTime = reloadTime;
    }

    ///get information of the gun 
    ///get name of gun 
    public String getName()
    {
        return name;
    }

    ///get type of gun
    public String getType()
    {
        return type;
    }

    ///get damage 
    public int getDamage()
    {
        return damage;
    }

    ///get firerate (the time that the other shoot is accepctable)
    public float getCoolDown()
    {
        return coolDown;
    } 

    ///get magagzine size
    public int getMagazineSize()
    {
        return magazineSize;
    }

    ///get speed reload magazine of that weapon
    public float getReloadTime()
    {
        return reloadTime;
    }
}

