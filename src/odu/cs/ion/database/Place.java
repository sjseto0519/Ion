package odu.cs.ion.database;

public class Place {

	String name;
	float gpslat;
	float gpslong;
	
	public Place(String name, float gpslat, float gpslong)
	{
		this.name = name;
		this.gpslat = gpslat;
		this.gpslong = gpslong;
	}
	
	public String getName()
	{
		return name;
	}
	
	public float[] getPosition()
	{
		return new float[] {gpslat, gpslong};
	}
	
}
