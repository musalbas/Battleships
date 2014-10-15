public class Square
{
    private boolean hitStatus; //shows if this square has been fired on
    private boolean shipStatus; //shows if this square contains part of a ship
    private Ship ship; //the ship which occupies this square, can be null if square is sea
    
    //constructs a water square which hasn't been hit
    public Square()
    {
        hitStatus = false;
        shipStatus = false;
    }
    
    public boolean isHit()
    {
        return hitStatus;
    }
    
    public boolean isShip()
    {
        return shipStatus;
    }
    
    public void setHit(boolean b)
    {
        hitStatus = b;
    }
    
    public void setShip(Ship s)
    {
        shipStatus = true;
        ship = s;
    }
    
    //returns the ship on this tile, returns null if there's no ship
    public Ship getShip()
    {
        return ship;
    }
    
    @Override
    public String toString()
    {
        return "[hit=" + hitStatus + ", ship=" + shipStatus + "]";
    }
}