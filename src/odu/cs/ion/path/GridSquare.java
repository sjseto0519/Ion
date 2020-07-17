package odu.cs.ion.path;

public class GridSquare {
	
	public int x;
	public int y;
	
	public GridSquare(int a, int b)
	{
		x = a;
		y = b;
	}
	
	public GridSquare()
	{
		
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public Integer[] toIntArray()
	{
		return new Integer[] {x,y};
	}
	
	public boolean equals(Object obj) {
       // check for reference equality
       if(this == obj) return true;

       // type check
       if( !(obj instanceof GridSquare) ) return false;

       // cast to correct type
       GridSquare p = (GridSquare)obj;

       // compare significant fields
       return (this.x == p.x && this.y == p.y);
   }
	
}
