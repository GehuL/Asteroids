package util;

public enum EDirection
{
	NORTH(0, -1), 
	NORTHEAST(1, -1), 
	EAST(1, 0), 
	SOUTHEAST(1, 1), 
	SOUTH(0, 1), 
	SOUTHWEST(-1, 1), 
	WEST(-1, 0), 
	NORTHWEST(-1, -1);

	public final int dx, dy;

	EDirection(int dx, int dy)
	{
		this.dx = dx;
		this.dy = dy;
	}
	
	public EDirection opposite()
	{
		   switch(this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            case NORTHEAST: return SOUTHWEST;
            case SOUTHWEST: return NORTHEAST;
            case NORTHWEST: return SOUTHEAST;
            case SOUTHEAST: return NORTHWEST;
            default: throw new IllegalStateException("This direction " + this + " has no opposite");
		   }
	}
	
}
