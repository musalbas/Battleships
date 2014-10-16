public class Board
{
    private final int BOARD_DIMENSION = 10;
    private Square[][] squares;
    private Ship[] ships;
    
    //constructs a board with no ships
    public Board()
    {
        squares = new Square[BOARD_DIMENSION][BOARD_DIMENSION];
        
        //populates the squares array
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                squares[i][j] = new Square();
            }
        }
        
        ships = new Ship[5];
    }
    
    public Square getSquare(int x, int y)
    {
        return squares[x][y];
    }
    
    public void setHit(Square s)
    {
        s.setHit(true);
        
        if(s.getShip() != null) //if s has a ship on it
        {
            s.getShip().decreaseHealth();
        }
    }
    
    //places a ship on the board with the parameters x and y being
    //the co-ordinates of the head of the ship
    public void addShip(Ship ship, int x, int y)
    {
        //finds the first empty cell in ships array and stores the ship in it
        for(int i = 0; i < ships.length; i++)
        {
            if(ships[i] == null)
            {
                ships[i] = ship;
                break;
            }
        }
        
        //puts ship on squares
        for(int i = 0; i < ship.getLength(); i++)
        {
            if(ship.getOrientation() == Ship.HORIZONTAL)
            {
                squares[x][y + i].setShip(ship);
            }
            else if(ship.getOrientation() == Ship.VERTICAL)
            {
                squares[x + i][y].setShip(ship);
            }
        }
    }
    
    public boolean gameOver()
    {
        for(int i = 0; i < ships.length; i++)
        {
            if(!ships[i].isSunk())
            {
                return false;
            }
        }
        
        return true;
    }
    
    //for testing, prints "S" if square is ship, otherwise "0" for normal square
    public void printBoard()
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if(squares[i][j].isShip())
                {
                    System.out.print("S");
                }
                else
                {
                    System.out.print("0");
                }
            }
            System.out.println();
        }
    }
}