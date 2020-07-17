package odu.cs.ion.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class PlacesDbAdapter {

	private static final String DATABASE_CREATE_1 = "create table indoormaps (placename text not null, "
			+ "floor integer, width integer, height integer, gpslat float, gpslong float, image text not null, inchwidth integer, inchheight integer);";
	private static final String DATABASE_CREATE_2 = "create table outdoormaps (placename text not null, "
			+ "width integer, height integer, gpsnwlat float, gpsnwlong float, gpsswlat float, gpsswlong float, gpsnelat float, gpsnelong float, gpsselat float, gpsselong float, image text not null);";
	private static final String DATABASE_CREATE_3 = "create table places (placename text not null, "
			+ "gpslat float, gpslong float);";
	private static final String DATABASE_CREATE_4 = "create table locations (placename text not null, "
			+ "floor integer, posx integer, posy integer, type text not null);";
	private static final String DATABASE_CREATE_5 = "create table outdoorlocations (name text not null, "
			+ "latitude float, longitude float, marker text, id integer);";
	private static final String DATABASE_CREATE_6 = "create table destination (name text not null "
			+ ");";
	
	// Database fields
	public static final String KEY_PLACENAME = "placename";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_INCHWIDTH = "inchwidth";
	public static final String KEY_INCHHEIGHT = "inchheight";
	public static final String KEY_FLOOR = "floor";
	public static final String KEY_NAME = "name";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_MARKER = "marker";
	public static final String KEY_ID = "id";
	public static final String KEY_POSX = "posx";
	public static final String KEY_POSY = "posy";
	public static final String KEY_TYPE = "type";
	public static final String KEY_GPSLAT = "gpslat";
	public static final String KEY_GPSLONG = "gpslong";
	public static final String KEY_GPSNWLAT = "gpsnwlat";
	public static final String KEY_GPSNWLONG = "gpsnwlong";
	public static final String KEY_GPSNELAT = "gpsnelat";
	public static final String KEY_GPSNELONG = "gpsnelong";
	public static final String KEY_GPSSWLAT = "gpsswlat";
	public static final String KEY_GPSSWLONG = "gpsswlong";
	public static final String KEY_GPSSELAT = "gpsselat";
	public static final String KEY_GPSSELONG = "gpsselong";
	public static final String KEY_IMAGE = "image";
	private static final String DATABASE_TABLE_1 = "indoormaps";
	private static final String DATABASE_TABLE_2 = "outdoormaps";
	private static final String DATABASE_TABLE_3 = "places";
	private static final String DATABASE_TABLE_4 = "locations";
	private static final String DATABASE_TABLE_5 = "outdoorlocations";
	private static final String DATABASE_TABLE_6 = "destination";

	private Context context;
	private SQLiteDatabase database;

	public PlacesDbAdapter() {
	}

	public PlacesDbAdapter open() throws SQLException {
		File dbfile = new File("/data/data/odu.cs.ion/mydb.sqlite" ); 
		database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

		return this;
	}
	
	public void create()
	{
		try {
		database.execSQL(DATABASE_CREATE_1);
		}
		catch (Exception e) {}
		try {
		database.execSQL(DATABASE_CREATE_2);
		}
		catch (Exception e) {}
		try {
		database.execSQL(DATABASE_CREATE_3);
		}
		catch (Exception e) {}
		try {
		database.execSQL(DATABASE_CREATE_4);
		}
		catch (Exception e) {}
		try {
		database.execSQL(DATABASE_CREATE_5);
		}
		catch (Exception e) {}
		try {
		database.execSQL(DATABASE_CREATE_6);
		}
		catch (Exception e) {}
	}
	
	private int getNumRows() {
	    String sql = "SELECT COUNT(*) FROM " + DATABASE_TABLE_1;
	    SQLiteStatement statement = database.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return (int)count;
	}
	
	public void deleteAll(boolean drop)
	{
		if (drop)
		{
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_1);
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_2);
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_3);
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_4);
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_5);
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_6);
		
		}

	}
	
	public ArrayList<Map> getMaps()
	{
		ArrayList<Map> mm = new ArrayList<Map>();
		
		Cursor c = database.rawQuery("SELECT placename, floor, width,height,gpslat,gpslong, image, inchwidth, inchheight FROM "+DATABASE_TABLE_1, null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			Map m = new Map(c.getString(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getFloat(4), c.getFloat(5), c.getString(6), c.getInt(7), c.getInt(8));
		    mm.add(m);
		}
		while (c.moveToNext());
		}
		c.close();
		
		c = database.rawQuery("SELECT placename,width, height, gpsnwlat, gpsnwlong,gpsswlat, gpsswlong, gpsnelat, gpsnelong,gpsselat, gpsselong, image FROM "+DATABASE_TABLE_2, null);
		count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			Map m = new Map(c.getString(0), c.getInt(1), c.getInt(2), c.getFloat(3), c.getFloat(4), c.getFloat(5), c.getFloat(6), c.getFloat(7), c.getFloat(8), c.getFloat(9), c.getFloat(10), c.getString(11));
		    mm.add(m);
		}
		while (c.moveToNext());
		}
		c.close();		
		
		return mm;
	}
	
	public ArrayList<Place> getPlaces()
	{
		ArrayList<Place> mm = new ArrayList<Place>();
		
		Cursor c = database.rawQuery("SELECT placename,gpslat,gpslong FROM "+DATABASE_TABLE_3, null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			Place m = new Place(c.getString(0), c.getFloat(1), c.getFloat(2));
		    mm.add(m);
		}
		while (c.moveToNext());
		}
		c.close();
		
		return mm;
	}
	
	public ArrayList<Location> getLocations()
	{
		ArrayList<Location> mm = new ArrayList<Location>();
		
		Cursor c = database.rawQuery("SELECT placename,floor,posx,posy,type FROM "+DATABASE_TABLE_4, null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			Location m = new Location(c.getString(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getString(4));
		    mm.add(m);
		}
		while (c.moveToNext());
		}
		c.close();
		
		return mm;
	}
	
	public String getDestination()
	{
		String destination = "";
		
		Cursor c = database.rawQuery("SELECT name FROM "+DATABASE_TABLE_6, null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			destination = c.getString(0);
		}
		while (c.moveToNext());
		}
		c.close();
		
		return destination;
	}
	
	public void setDestination(String destination)
	{
		database.execSQL("INSERT INTO "+DATABASE_TABLE_6+" (name) VALUES ('"+destination+"');");
	}
	
	public ArrayList<OutdoorLocation> getOutdoorLocations()
	{
		ArrayList<OutdoorLocation> mm = new ArrayList<OutdoorLocation>();
		
		Cursor c = database.rawQuery("SELECT name,latitude,longitude,marker,id FROM "+DATABASE_TABLE_5, null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			OutdoorLocation m = new OutdoorLocation(c.getString(0), c.getFloat(1), c.getFloat(2), c.getString(3), c.getInt(4));
		    mm.add(m);
		}
		while (c.moveToNext());
		}
		c.close();
		
		return mm;
	}
	
	public ArrayList<Location> getLocations(String placeName)
	{
		ArrayList<Location> mm = new ArrayList<Location>();
		
		Cursor c = database.rawQuery("SELECT placename,floor,posx,posy,type FROM "+DATABASE_TABLE_4, null);
		int count = c.getCount();
		//Log.e("locationcount",""+count);
		if (count > 0)
		{
		c.moveToFirst();
		do {
			Location m = new Location(c.getString(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getString(4));
		    //Log.e("comp",c.getString(0));
		    //Log.e("comp",placeName);
		    //Log.e("comp",""+c.getInt(1));
			if (placeName.contains(c.getString(0))) {
			mm.add(m);
			//Log.e("location added",c.getString(4));
		    }
		}
		while (c.moveToNext());
		}
		c.close();
		
		return mm;
	}
	
	public ArrayList<Location> getLocations(String placeName, String floor)
	{
		ArrayList<Location> mm = new ArrayList<Location>();
		
		Cursor c = database.rawQuery("SELECT placename,floor,posx,posy,type FROM "+DATABASE_TABLE_4, null);
		int count = c.getCount();
		//Log.e("locationcount",""+count);
		if (count > 0)
		{
		c.moveToFirst();
		do {
			Location m = new Location(c.getString(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getString(4));
		    //Log.e("comp",c.getString(0));
		    //Log.e("comp",placeName);
		    //Log.e("comp",""+floor);
		    //Log.e("comp",""+c.getInt(1));
			if (placeName.contains(c.getString(0)) && floor.equals(""+c.getInt(1))) {
			mm.add(m);
			//Log.e("location added",c.getString(4));
		    }
		}
		while (c.moveToNext());
		}
		c.close();
		
		return mm;
	}
	
	public ArrayList<Place> getPlaces(float[] nw, float[] ne, float[] sw, float[] se)
	{
		ArrayList<Place> mm = new ArrayList<Place>();
		
		Cursor c = database.rawQuery("SELECT placename,gpslat,gpslong FROM "+DATABASE_TABLE_3, null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			
			float gpslat = c.getFloat(1);
			float gpslong = c.getFloat(2);
			if (gpslat < nw[0] && gpslat > sw[0] && gpslong < se[1] && gpslong > sw[1])
			{
			Place m = new Place(c.getString(0), gpslat, gpslong);
		    mm.add(m);
			}
		}
		while (c.moveToNext());
		}
		c.close();
		
		return mm;
	}
	
	public void close()
	{
		database.close();
	}
	
	public void execute(String a)
	{
		if (a != null && !a.equals("null"))
		{
		  a = a.substring(4, a.length());
		  String delims = "[;]";
		  String[] aa = a.split(delims);
		  for (int i = 0; i < aa.length; ++i)
		  {
		    database.execSQL(aa[i]+";");
		    //Log.e("execute",aa[i]);
		  }
		}
	}


	

}