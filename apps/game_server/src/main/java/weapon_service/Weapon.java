package weapon_service;
public class Weapon 
{
    private String name;
    private String type;
    private int damage;
    private float coolDown;
    private int magazineSize;
    private float reloadTime;


    public Weapon(String name, String type, int damage, float coolDown, int magazineSize, float reloadTime)
    {
        this.name = name;
        this.type = type; 
        this.damage = damage;
        this.coolDown = coolDown;
        this.magazineSize = magazineSize;
        this.reloadTime = reloadTime;
    }


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

    public float getCoolDown()
    {
        return coolDown;
    } 

    public int getMagazineSize()
    {
        return magazineSize;
    }

    public float getReloadTime()
    {
        return reloadTime;
    }
}

