package odu.cs.ion.database;

public class OutdoorLocation {

	String name;
	float latitude;
	float longitude;
	String marker;
	int id;
	
	public OutdoorLocation(String name, float latitude, float longitude, String marker, int id)
	{
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.marker = marker;
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public float getLatitude()
	{
		return latitude;
	}
	
	public float getLongitude()
	{
		return longitude;
	}
	
	public String getMarker()
	{
		return marker;
	}
	
	public int getId()
	{
		return id;
	}
	

}