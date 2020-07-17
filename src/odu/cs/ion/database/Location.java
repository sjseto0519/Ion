package odu.cs.ion.database;

public class Location {

	String placename;
	int floor;
	int posx;
	int posy;
	String type;
	
	public Location(String placename, int floor, int posx, int posy, String type)
	{
		this.placename = placename;
		this.floor = floor;
		this.posx = posx;
		this.posy = posy;
		this.type = type;
	}
	
	public String getPlaceName()
	{
		return placename;
	}
	
	public int getFloor()
	{
		return floor;
	}
	
	public int getPosX()
	{
		return posx;
	}
	
	public int getPosY()
	{
		return posy;
	}
	
	public String getType()
	{
		return type;
	}
	

}
