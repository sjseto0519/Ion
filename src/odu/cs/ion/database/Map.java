package odu.cs.ion.database;

public class Map {

	String name;
	boolean outdoor;
	int width;
	int height;
	int inchwidth;
	int inchheight;
	int floor;
	float gpslat;
	float gpslong;
	float gpsnwlat;
	float gpsnwlong;
	float gpsswlat;
	float gpsswlong;
	float gpsnelat;
	float gpsnelong;
	float gpsselat;
	float gpsselong;
	int widthpergridline;
	int heightpergridline;
	float stepspergridline;
	String image;
	
	public int getInchWidth()
	{
		return inchwidth;
	}
	
	public int getInchHeight()
	{
		return inchheight;
	}
	
	public float getGpsNw1()
	{
		return gpsnwlat;
	}
	
	public float getGpsNw2()
	{
		return gpsnwlong;
	}
	
	public float getGpsSw1()
	{
		return gpsswlat;
	}
	
	public float getGpsSw2()
	{
		return gpsswlong;
	}
	
	public float getGpsNe1()
	{
		return gpsnelat;
	}
	
	public float getGpsNe2()
	{
		return gpsnelong;
	}
	
	public float getGpsSe1()
	{
		return gpsselat;
	}
	
	public float getGpsSe2()
	{
		return gpsselong;
	}
	
	public String getImage()
	{
		return image;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getFloor()
	{
		return floor;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean outdoor()
	{
		return outdoor;
	}
	
	public float[] getGps()
	{
		if (outdoor)
		{
			return new float[] {gpsnwlat, gpsnwlong};
		}
        return new float[] {gpslat, gpslong};
	}
	
	public Map(String name, int floor, int width, int height, float gpslat, float gpslong, String image, int inchwidth, int inchheight)
	{
		outdoor = false;
		this.name = name;
		this.floor = floor;
		this.width = width;
		this.height = height;
		this.gpslat = gpslat;
		this.gpslong = gpslong;
		this.image = image;
		this.inchwidth = inchwidth;
		this.inchheight = inchheight;
	}
	
	public Map(String name, int width, int height, float gpsnwlat, float gpsnwlong, float gpsswlat, float gpsswlong, float gpsnelat, float gpsnelong, float gpsselat, float gpsselong, String image)
	{
		outdoor = true;
		this.name = name;
		this.width = width;
		this.height = height;
		this.gpsnwlat = gpsnwlat;
		this.gpsnwlong = gpsnwlong;
		this.gpsswlat = gpsswlat;
		this.gpsswlong = gpsswlong;
		this.gpsnelat = gpsnelat;
		this.gpsnelong = gpsnelong;
		this.gpsselat = gpsselat;
		this.gpsselong = gpsselong;
		this.image = image;
	}
	
}
