public class Ship
{
    //orientation constants
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    //instance variables
    private final int length;
    private int health;
    private int orientation;
    
    //constructs ship with specified length and a default orientation of VERTICAL
    public Ship(int length)
    {
        this.length = length;
        this.health = length;
        this.orientation = VERTICAL;
    }
    
    public void setOrientation(int orientation)
    {
        this.orientation = orientation;
    }
    
    public boolean isSunk()
    {
        return (health == 0);
    }
    
    //decreases when ship is hit
    public void decreaseHealth()
    {
        health--;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public int getOrientation()
    {
        return orientation;
    }
}