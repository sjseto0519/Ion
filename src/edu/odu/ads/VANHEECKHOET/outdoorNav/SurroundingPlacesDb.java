package edu.odu.ads.VANHEECKHOET.outdoorNav;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import odu.cs.ion.database.OutdoorLocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class SurroundingPlacesDb {

	public static final String TAG = "SurroundingPlacesDb";
	
	private static final String DATABASE_CREATE = "create table surroundingplaces (id integer, placename text not null, "
			+ "latitude float, longitude float, marker text not null, type text not null);";
	
	
	// Database fields
	public static final String KEY_PLACENAME = "placename";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_MARKER = "marker";
	public static final String KEY_ID = "id";
	public static final String KEY_TYPE = "type";
	private static final String DATABASE_TABLE = "surroundingplaces";

	private Context context;
	private SQLiteDatabase database;

	public SurroundingPlacesDb() {
	}

	public SurroundingPlacesDb open() throws SQLException {
		File dbfile = new File("/data/data/odu.cs.ion/mydb.sqlite" ); 
		database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
		return this;
	}
	
	public void create()
	{
		try {
		database.execSQL(DATABASE_CREATE);
		//Log.d(TAG, DATABASE_CREATE + " created");
		}
		catch (Exception e) {
			//Log.e(TAG, "error : create, " + DATABASE_CREATE + e.toString());
		}
	}
	
	private int getNumRows() {
	    String sql = "SELECT COUNT(*) FROM " + DATABASE_TABLE;
	    SQLiteStatement statement = database.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return (int)count;
	}
	
	public void deleteAll(boolean drop)
	{
		if (drop)
		{
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
		}

	}
	
	public ArrayList<OutdoorLocation> getSurroundingPlacesByType(String type)
	{
		ArrayList<OutdoorLocation> list = new ArrayList<OutdoorLocation>();
		
		//Log.d(TAG, "getSurroundingPlacesByType : Query : " + "SELECT placename, latitude, longitude, marker, id FROM "+ DATABASE_TABLE + " WHERE (type = " + type + ")");
		Cursor c = database.rawQuery("SELECT placename, latitude, longitude, marker, id FROM "+ DATABASE_TABLE + " WHERE type = '" + type +"'", null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			OutdoorLocation item = new OutdoorLocation(c.getString(0), c.getFloat(1), c.getFloat(2), c.getString(3), c.getInt(4));
			list.add(item);
		}
		while (c.moveToNext());
		}
		c.close();
		
		return list;
	}
	
	public boolean saveSurroundingPlacesByType(ArrayList<OutdoorLocation> list, String type)
	{
		OutdoorLocation item;
		String name;
		String marker;
		String thetype;
		int i;
		for(i=0;i<list.size();i++)
		{
			item = list.get(i);
			name = checkString(item.getName());
			marker = checkString(item.getMarker());
			thetype = checkString(type);
			//Log.d(TAG, "saveSurroundingPlacesByType : Query : " + "REPLACE INTO " + DATABASE_TABLE + " ( id, placename, latitude, longitude, marker, type ) values ( " + item.getId() +","+ item.getName() +","+ item.getLatitude() +","+ item.getLongitude() +","+ item.getMarker() +","+ type + " )");
			database.execSQL("REPLACE INTO " + DATABASE_TABLE + " ( id, placename, latitude, longitude, marker, type ) values ( " + item.getId() +",\""+ name +"\","+ item.getLatitude() +","+ item.getLongitude() +",'"+ marker +"','"+ type + "' )");
			/*
			ContentValues cv = new ContentValues();
			cv.put("id" , item.getId());
			cv.put("placename" , item.getName());
			cv.put("latitude" , item.getLatitude());
			cv.put("longitude" , item.getLongitude());
			cv.put("marker" , item.getMarker());
			cv.put("type", type);
			database.update(DATABASE_TABLE,cv,"id","=?", Integer.toString(item.getId()));
			*/
		}		
		return true;
	}
	
	public String checkString(String str)
	{
		int i;
		String newstr = new String(str);
		//newstr.replace('\'', '\\\'');
		
		//Log.d(TAG, "str = " + str);
		//Log.d(TAG, "newstr = " + newstr);
		
		return newstr;
	}
	
	public boolean isDbEmpty()
	{
		//Log.d(TAG, "isDbEmpty : Query : " + "SELECT id FROM "+ DATABASE_TABLE);
		Cursor c = database.rawQuery("SELECT id FROM "+ DATABASE_TABLE, null);
		
		if (c.getCount() == 0)
		{
			//Log.d(TAG, "isDbEmpty : c.getCount() == 0");
			return true;
		}
		else
		{
			//Log.d(TAG, "isDbEmpty : c.getCount() != 0, c.getCount() == " +  c.getCount());
			return false;
		}
		
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
		  }
		}
	}


	

}