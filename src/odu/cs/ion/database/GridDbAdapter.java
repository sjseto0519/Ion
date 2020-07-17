package odu.cs.ion.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

public class GridDbAdapter {

	private static final String DATABASE_CREATE_1 = "create table griddata (position text not null, "
			+ "apname text not null, rowid integer, f1 float, f2 float, f3 float, f4 float, f5 float, f6 float, f7 float, f8 float);";
	private static final String DATABASE_CREATE_2 = "create table probdata (buildingname text not null, position text not null, "
			+ "apname text not null, num integer, total integer);";
	private static final String DATABASE_CREATE_U1 = "create table unique1 (buildingname text not null, position text not null, "
			+ "apname text not null, type integer, value float, unique1 integer);";
	private static final String DATABASE_CREATE_U2 = "create table unique2 (buildingname text not null, position text not null, "
			+ "apname text not null, type integer, value float, type2 integer, value2 float, unique1 integer);";
	private static final String DATABASE_CREATE_U3 = "create table unique3 (buildingname text not null, position text not null, "
			+ "apname text not null, type integer, value float, type2 integer, value2 float, type3 integer, value3 float, unique1 integer);";
	
	
	// Database fields
	public static final String KEY_BUILDING = "buildingname";
	public static final String KEY_POSITION = "position";
	public static final String KEY_APNAME = "apname";
	public static final String KEY_NUM = "num";
	public static final String KEY_TOTAL = "total";
	public static final String KEY_TYPE = "type";
	public static final String KEY_VALUE = "value";
	public static final String KEY_TYPE2 = "type2";
	public static final String KEY_VALUE2 = "value2";
	public static final String KEY_TYPE3 = "type3";
	public static final String KEY_VALUE3 = "value3";
	public static final String KEY_ROWID = "rowid";
	public static final String KEY_UNIQUE = "unique1";
	public static final String KEY_F1 = "f1";
	public static final String KEY_F2 = "f2";
	public static final String KEY_F3 = "f3";
	public static final String KEY_F4 = "f4";
	public static final String KEY_F5 = "f5";
	public static final String KEY_F6 = "f6";
	public static final String KEY_F7 = "f7";
	public static final String KEY_F8 = "f8";
	private static final String DATABASE_TABLE_1 = "griddata";
	private static final String DATABASE_TABLE_2 = "probdata";
	private static final String DATABASE_UNIQUE_1 = "unique1";
	private static final String DATABASE_UNIQUE_2 = "unique2";
	private static final String DATABASE_UNIQUE_3 = "unique3";

	private Context context;
	private SQLiteDatabase database;
	private String location;

	public GridDbAdapter(String location) {
		this.location = location;
	}
	
	public GridDbAdapter() {
		
	}
	
	public boolean isOpen()
	{
		return database.isOpen();
	}

	public GridDbAdapter open() throws SQLException {
		File dbfile = new File("/data/data/odu.cs.ion/mydb2.sqlite" ); 
		database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

		return this;
	}
	
	public ArrayList<String> getPositions(String buildingname)
	{
		ArrayList<String> aa = new ArrayList<String>();
		Cursor c = database.rawQuery("SELECT DISTINCT position FROM "+DATABASE_TABLE_2+" WHERE buildingname = \""+buildingname+"\"", null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			aa.add(c.getString(0));
		}
		while (c.moveToNext());
		}
		c.close();	
		return aa;
	}
	
	public ArrayList<String> getPositions2(String buildingname)
	{
		ArrayList<String> aa = new ArrayList<String>();
		Cursor c = database.rawQuery("SELECT DISTINCT position, buildingname FROM "+DATABASE_TABLE_2+" WHERE buildingname LIKE \""+buildingname+" Floor%\"", null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
			String bname = c.getString(1);
			int ind = bname.lastIndexOf(" ");
			String floor = bname.substring(ind+1, bname.length());
			aa.add(c.getString(0)+" "+floor);
		}
		while (c.moveToNext());
		}
		c.close();	
		return aa;
	}
	
	public int getNumberOf(String buildingname, String apname)
	{
		Cursor c = database.rawQuery("SELECT DISTINCT buildingname, position, apname FROM "+DATABASE_UNIQUE_1+" WHERE buildingname = \""+buildingname+"\" AND apname = '"+apname+"'", null);
		int count = c.getCount();
		c.close();		
		return count;
	}
	
	public float getDistance1(String buildingname, String apname, String pos, Float[] values)
	{
		float dd = 0.0f;
		Cursor c = database.rawQuery("SELECT type, value FROM "+DATABASE_UNIQUE_1+" WHERE buildingname = \""+buildingname+"\" AND apname = '"+apname+"' AND position = '"+pos+"' AND unique1 = 1", null);
		int count = c.getCount();
		//Log.e("distance1count",""+count+" "+apname+" "+pos);
		if (count > 0)
		{
		c.moveToFirst();
		do {
           int type = c.getInt(0);
           float value = c.getFloat(1);
           dd += (float)Math.abs(values[type]-value);
		}
		while (c.moveToNext());
		}
		c.close();	
		return dd;
		
	}
	
	public float getDistance2(String buildingname, String apname, String pos, Float[] values)
	{
		float dd = 0.0f;
		Cursor c = database.rawQuery("SELECT type, value, type2, value2 FROM "+DATABASE_UNIQUE_2+" WHERE buildingname = \""+buildingname+"\" AND apname = '"+apname+"' AND position = '"+pos+"' AND unique1 = 1", null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
           int type = c.getInt(0);
           float value = c.getFloat(1);
           int type2 = c.getInt(2);
           float value2 = c.getFloat(3);
           float avg = (value+value2)/2.0f;
           float avg2 = (values[type]+values[type2])/2.0f;
           dd += (float)Math.abs(avg2-avg);
		}
		while (c.moveToNext());
		}
		c.close();	
		return dd;
		
	}
	
	public float getDistance3(String buildingname, String apname, String pos, Float[] values)
	{
		float dd = 0.0f;
		Cursor c = database.rawQuery("SELECT type, value, type2, value2, type3, value3 FROM "+DATABASE_UNIQUE_3+" WHERE buildingname = \""+buildingname+"\" AND apname = '"+apname+"' AND position = '"+pos+"' AND unique1 = 1", null);
		int count = c.getCount();
		if (count > 0)
		{
		c.moveToFirst();
		do {
           int type = c.getInt(0);
           float value = c.getFloat(1);
           int type2 = c.getInt(2);
           float value2 = c.getFloat(3);
           int type3 = c.getInt(4);
           float value3 = c.getFloat(5);
           float avg = (value+value2+value3)/3.0f;
           float avg2 = (values[type]+values[type2]+values[type3])/3.0f;
           dd += (float)Math.abs(avg2-avg);
		}
		while (c.moveToNext());
		}
		c.close();	
		return dd;
		
	}
	
	public void exportToServer()
	{
		String queries = "";
		Cursor c = database.rawQuery("SELECT * FROM "+DATABASE_TABLE_2, null);
		int count = c.getCount();
		//Log.e("probdatacount",""+count);
		if (count > 0)
		{
		c.moveToFirst();
		do {
			queries += "REPLACE INTO "+DATABASE_TABLE_2+" (buildingname, position, apname, num, total) VALUES (\""+c.getString(0)+"\",\""+c.getString(1)+"\",\""+c.getString(2)+"\","+c.getInt(3)+","+c.getInt(4)+"); ";
		}
		while (c.moveToNext());
		}
		c.close();
		
		Cursor c2 = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_1, null);
		int count2 = c2.getCount();
		if (count2 > 0)
		{
		c2.moveToFirst();
		do {
			queries += "REPLACE INTO "+DATABASE_UNIQUE_1+" (buildingname, position, apname, type, value, unique1) VALUES (\""+c2.getString(0)+"\",\""+c2.getString(1)+"\",\""+c2.getString(2)+"\","+c2.getInt(3)+","+c2.getFloat(4)+","+c2.getInt(5)+"); ";
		}
		while (c2.moveToNext());
		}
		c2.close();
		
		Cursor c3 = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_2, null);
		int count3 = c3.getCount();
		if (count3 > 0)
		{
		c3.moveToFirst();
		do {
			queries += "REPLACE INTO "+DATABASE_UNIQUE_2+" (buildingname, position, apname, type, value, type2, value2, unique1) VALUES (\""+c3.getString(0)+"\",\""+c3.getString(1)+"\",\""+c3.getString(2)+"\","+c3.getInt(3)+","+c3.getFloat(4)+","+c3.getInt(5)+","+c3.getFloat(6)+","+c3.getInt(7)+"); ";
		}
		while (c3.moveToNext());
		}
		c3.close();
		
		Cursor c4 = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_3, null);
		int count4 = c4.getCount();
		if (count4 > 0)
		{
		c4.moveToFirst();
		do {
			queries += "REPLACE INTO "+DATABASE_UNIQUE_3+" (buildingname, position, apname, type, value, type2, value2, type3, value3, unique1) VALUES (\""+c4.getString(0)+"\",\""+c4.getString(1)+"\",\""+c4.getString(2)+"\","+c4.getInt(3)+","+c4.getFloat(4)+","+c4.getInt(5)+","+c4.getFloat(6)+","+c4.getInt(7)+","+c4.getFloat(8)+","+c4.getInt(9)+"); ";
		}
		while (c4.moveToNext());
		}
		c4.close();
		//Log.e("exportqueries",queries.substring(0, queries.length()/2));
		//Log.e("exportqueries2",queries.substring(queries.length()/2, queries.length()));
		sendPostRequest(queries);
		
	}
	
	public void sendPostRequest(String queries)
	{
		try {
		    // Construct data
		    String data = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(queries, "UTF-8");

		    // Send data
		    URL url = new URL("http://scott-seto.com/appdoublescreen/Sql/importdata.php");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();
		    
		 // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        //Log.e("postresponse",line);
		    }
		    wr.close();
		    rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getLocation(GridData a)
	{
		HashMap<String, Float[]> gm = a.getGridMap();
		Iterator ii = gm.keySet().iterator();
		while (ii.hasNext())
		{
			String ss = (String)ii.next();
			Float[] ff = (Float[]) gm.get(ss);
	    	for (int i = 0; i < 6; ++i)
	    	{
	    		String loc = getLocation(ss, i, ff[i]);
	    		if (loc != null) return loc;
	    	}
	    	for (int i = 0; i < 5; ++i)
	    	{
	    		String loc = getLocation(ss, i, ff[i], i+1, ff[i+1]);
	    		if (loc != null) return loc;
	    	}
	    	for (int i = 0; i < 4; ++i)
	    	{
	    		String loc = getLocation(ss, i, ff[i], i+1, ff[i+1], i+2, ff[i+2]);
	    		if (loc != null) return loc;
	    	}
		}
		return "";
	}
	
	public String getLocation(String apname, int type, float value)
	{
		ContentValues cv = createContentValues(apname, type, value);
	    boolean bb = isUniqueA(cv);
	    if (bb)
	    {
	    	return getPositionA(cv);
	    }
	    return null;
	}
	
	public String getLocation(String apname, int type, float value, int type2, float value2)
	{
		ContentValues cv = createContentValues(apname, type, value, type2, value2);
	    boolean bb = isUnique1A(cv);
	    if (bb)
	    {
	    	return getPosition1A(cv);
	    }
	    return null;
	}
	
	public String getLocation(String apname, int type, float value, int type2, float value2, int type3, float value3)
	{
		ContentValues cv = createContentValues(apname, type, value, type2, value2, type3, value3);
	    boolean bb = isUnique2A(cv);
	    if (bb)
	    {
	    	return getPosition2A(cv);
	    }
	    return null;
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
		database.execSQL(DATABASE_CREATE_U1);
		}
		catch (Exception e) {}
		try {
		database.execSQL(DATABASE_CREATE_U2);
		}
		catch (Exception e) {}
		try {
		database.execSQL(DATABASE_CREATE_U3);
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
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_UNIQUE_1);
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_UNIQUE_2);
		database.execSQL("DROP TABLE IF EXISTS "+DATABASE_UNIQUE_3);
		}

	}
	
	public void close()
	{
		database.close();
	}

	
/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */

	public long createTodo(String category, String summary, String description) {
		ContentValues initialValues = createContentValues(category, summary,
				description, 0);

		return database.insert(DATABASE_TABLE_1, null, initialValues);
	}
	
	public void createGridData(GridData a)
	{
		
	}
	
	public void removeNotUnique(String position)
	{
		Cursor c = database.query(DATABASE_UNIQUE_1, new String[] {KEY_APNAME, KEY_TYPE, KEY_VALUE }, "position = '"+position+"'", null, null,
				null, "type, value");
	}
	
	public void addProb(String position, String apname, boolean found)
	{
		int num = 0;
		int total = 0;
		Cursor c = database.rawQuery("SELECT num,total FROM "+DATABASE_TABLE_2+" WHERE "+KEY_POSITION+"= '"+position+"' AND "+KEY_APNAME+"= '"+apname+"'", null);
		int count = c.getCount();
		c.moveToFirst();
		//Log.e("count",""+count);
		if (count > 0)
		{
			if (found)
			{
		      num = c.getInt(0)+1;
		      total = c.getInt(1)+1;
		      ContentValues c1 = createContentValues(location, position, apname, num, total);
		      database.replace(DATABASE_TABLE_2, null, c1);
				
			}
			else
			{
			  num = c.getInt(0);
			  total = c.getInt(1)+1;
			  ContentValues c1 = createContentValues(location, position, apname, num, total);
		      database.replace(DATABASE_TABLE_2, null, c1);
			}
			c.close();
		}
		else
		{
			c.close();
			if (found)
			{
				num = 1;
				total = 1;
				ContentValues c1 = createContentValues(location, position, apname, num, total);
				database.replace(DATABASE_TABLE_2, null, c1);
				
			}
			else
			{
				num = 0;
				total = 1;
				ContentValues c1 = createContentValues(location, position, apname, num, total);
				database.replace(DATABASE_TABLE_2, null, c1);
				
			}
			
		}
	}
	
    public void add(String position, String apname, Float[] values)
    {
    	for (int i = 0; i < 8; ++i)
    	{
    		addData(position, apname, i, values[i]);
    	}
    	for (int i = 0; i < 7; ++i)
    	{
    		addData(position, apname, i, values[i], i+1, values[i+1]);
    	}
    	for (int i = 0; i < 6; ++i)
    	{
    		addData(position, apname, i, values[i], i+1, values[i+1], i+2, values[i+2]);
    	}
    }
	
	public void addData(String position, String apname, int type, float value)
	{
		ContentValues initialValues = createContentValues(location, position, apname, type, value, 1);
		ContentValues initialValues2 = createContentValues(location, position, apname, type, value, 0);
		 
		boolean aa = isUnique(initialValues);
		if (aa) database.replace(DATABASE_UNIQUE_1, null, initialValues);
		else
		{
			database.replace(DATABASE_UNIQUE_1, null, initialValues2);
		    database.execSQL("UPDATE "+DATABASE_UNIQUE_1+" SET "+KEY_UNIQUE+" = 0 WHERE "+KEY_BUILDING+" = \""+location+"\" AND "+KEY_APNAME+" = '"+apname+"' AND "+KEY_TYPE+" = "+type+" AND "+KEY_VALUE+" = "+value+" ");
		}
	}
	
	public void addData(String position, String apname, int type, float value, int type2, float value2)
	{
		ContentValues initialValues = createContentValues(location, position, apname, type, value, type2, value2, 1);
		ContentValues initialValues2 = createContentValues(location, position, apname, type, value, type2, value2, 0);
		 
		boolean aa = isUnique1(initialValues);
		if (aa) database.replace(DATABASE_UNIQUE_2, null, initialValues);
		else
		{
			database.replace(DATABASE_UNIQUE_2, null, initialValues2);
			database.execSQL("UPDATE "+DATABASE_UNIQUE_2+" SET "+KEY_UNIQUE+" = 0 WHERE "+KEY_BUILDING+" = \""+location+"\" AND "+KEY_APNAME+" = '"+apname+"' AND "+KEY_TYPE+" = "+type+" AND "+KEY_VALUE+" = "+value+" AND "+KEY_TYPE2+" = "+type2+" AND "+KEY_VALUE2+" = "+value2+"");

		}
	}
	
	public void addData(String position, String apname, int type, float value, int type2, float value2, int type3, float value3)
	{
		ContentValues initialValues = createContentValues(location, position, apname, type, value, type2, value2, type3, value3, 1);
		ContentValues initialValues2 = createContentValues(location, position, apname, type, value, type2, value2, type3, value3, 0);
		 
		boolean aa = isUnique2(initialValues);
		if (aa) database.replace(DATABASE_UNIQUE_3, null, initialValues);
		else
		{
			database.replace(DATABASE_UNIQUE_3, null, initialValues2);
			database.execSQL("UPDATE "+DATABASE_UNIQUE_3+" SET "+KEY_UNIQUE+" = 0 WHERE "+KEY_BUILDING+" = \""+location+"\" AND "+KEY_APNAME+" = '"+apname+"' AND "+KEY_TYPE+" = "+type+" AND "+KEY_VALUE+" = "+value+" AND "+KEY_TYPE2+" = "+type2+" AND  "+KEY_VALUE2+" = "+value2+" AND "+KEY_TYPE3+" = "+type3+" AND "+KEY_VALUE3+" = "+value3+" ");

		}
	}
	
	public boolean isUnique(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_1+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE), null);
		int count = c.getCount();
		//Log.e("tag",""+count+" "+"SELECT COUNT(*) FROM "+DATABASE_UNIQUE_1+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE));
		c.close();
		if (count == 0) return true;
		return false;
	}
	
	public boolean isUniqueA(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_1+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_UNIQUE+" = 0 AND "+KEY_APNAME+" = '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE), null);
		int count = c.getCount();
		//Log.e("tag",""+count+" "+"SELECT COUNT(*) FROM "+DATABASE_UNIQUE_1+" WHERE "+KEY_BUILDING+"= '"+location+"' AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE));
		c.close();
		if (count == 0) return true;
		return false;
	}
	
	public String getPositionA(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT "+KEY_POSITION+" FROM "+DATABASE_UNIQUE_1+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_APNAME+" = '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE), null);
		//Log.e("result", "SELECT "+KEY_POSITION+" FROM "+DATABASE_UNIQUE_1+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_APNAME+" = '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE));
		//Log.e("result2", ""+c.getCount()+" "+c.getColumnCount());
		c.moveToFirst();
		String position = c.getString(0);
		c.close();
		return position;
	}
	
	public boolean isUnique1(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_2+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE)+"  AND "+KEY_TYPE2+"= "+init.getAsInteger(KEY_TYPE2)+" AND "+KEY_VALUE2+"= "+init.getAsFloat(KEY_VALUE2), null);
		int count = c.getCount();
		//Log.e("tag",""+count+" "+"SELECT COUNT(*) FROM "+DATABASE_UNIQUE_2+" WHERE "+KEY_BUILDING+"= '"+location+"' AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE));
		c.close();
		if (count == 0) return true;
		return false;
	}
	
	public String getPosition1A(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT "+KEY_POSITION+" FROM "+DATABASE_UNIQUE_2+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE)+"  AND "+KEY_TYPE2+"= "+init.getAsInteger(KEY_TYPE2)+" AND "+KEY_VALUE2+"= "+init.getAsFloat(KEY_VALUE2), null);
		c.moveToFirst();	
		String position = c.getString(0);
		c.close();
		return position;
	}
	
	public boolean isUnique1A(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_2+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_UNIQUE+" = 0 AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE)+"  AND "+KEY_TYPE2+"= "+init.getAsInteger(KEY_TYPE2)+" AND "+KEY_VALUE2+"= "+init.getAsFloat(KEY_VALUE2), null);
		int count = c.getCount();
		//Log.e("tag",""+count+" "+"SELECT COUNT(*) FROM "+DATABASE_UNIQUE_2+" WHERE "+KEY_BUILDING+"= '"+location+"' AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE));
		c.close();
		if (count == 0) return true;
		return false;
	}
	
	public boolean isUnique2(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_3+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE)+"  AND "+KEY_TYPE2+"= "+init.getAsInteger(KEY_TYPE2)+" AND "+KEY_VALUE2+"= "+init.getAsFloat(KEY_VALUE2)+"  AND "+KEY_TYPE3+"= "+init.getAsInteger(KEY_TYPE3)+" AND "+KEY_VALUE3+"= "+init.getAsFloat(KEY_VALUE3), null);
		int count = c.getCount();
		//Log.e("tag",""+count+" "+"SELECT COUNT(*) FROM "+DATABASE_UNIQUE_3+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE));
		c.close();
		if (count == 0) return true;
		return false;
	}
	
	public boolean isUnique2A(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT * FROM "+DATABASE_UNIQUE_3+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_UNIQUE+" = 0 AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE)+"  AND "+KEY_TYPE2+"= "+init.getAsInteger(KEY_TYPE2)+" AND "+KEY_VALUE2+"= "+init.getAsFloat(KEY_VALUE2)+"  AND "+KEY_TYPE3+"= "+init.getAsInteger(KEY_TYPE3)+" AND "+KEY_VALUE3+"= "+init.getAsFloat(KEY_VALUE3), null);
		int count = c.getCount();
		//Log.e("tag",""+count+" "+"SELECT COUNT(*) FROM "+DATABASE_UNIQUE_3+" WHERE "+KEY_BUILDING+"= '"+location+"' AND "+KEY_POSITION+"<> '"+init.getAsString(KEY_POSITION)+"' AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE));
		c.close();
		if (count == 0) return true;
		return false;
	}
	
	public String getPosition2A(ContentValues init)
	{
		Cursor c = database.rawQuery("SELECT "+KEY_POSITION+" FROM "+DATABASE_UNIQUE_3+" WHERE "+KEY_BUILDING+"= \""+location+"\" AND "+KEY_APNAME+"= '"+init.getAsString(KEY_APNAME)+"' AND "+KEY_TYPE+"= "+init.getAsInteger(KEY_TYPE)+" AND "+KEY_VALUE+"= "+init.getAsFloat(KEY_VALUE)+"  AND "+KEY_TYPE2+"= "+init.getAsInteger(KEY_TYPE2)+" AND "+KEY_VALUE2+"= "+init.getAsFloat(KEY_VALUE2)+"  AND "+KEY_TYPE3+"= "+init.getAsInteger(KEY_TYPE3)+" AND "+KEY_VALUE3+"= "+init.getAsFloat(KEY_VALUE3), null);
		c.moveToFirst();	
		String position = c.getString(0);
		c.close();
		return position;
	}
	
	public void addData(String loc, String position, String apname, int type, float value, int type2, float value2, int unique)
	{
		ContentValues initialValues = createContentValues(loc, position, apname, type, value, type2, value2, unique);

		database.replace(DATABASE_UNIQUE_2, null, initialValues);

	}
	
	public void addData(String loc, String position, String apname, int type, float value, int type2, float value2, int type3, float value3, int unique)
	{
		ContentValues initialValues = createContentValues(loc, position, apname, type, value, type2, value2, type3, value3, unique);

		database.replace(DATABASE_UNIQUE_3, null, initialValues);

	}

	
/**
	 * Update the todo
	 */

	public boolean updateTodo(long rowId, String category, String summary,
			String description) {
		ContentValues updateValues = createContentValues(category, summary,
				description, 0);

		return database.update(DATABASE_TABLE_1, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	
/**
	 * Deletes todo
	 */

	public boolean deleteTodo(long rowId) {
		return database.delete(DATABASE_TABLE_1, KEY_ROWID + "=" + rowId, null) > 0;
	}

	
/**
	 * Return a Cursor over the list of all todo in the database
	 * 
	 * @return Cursor over all notes
	 */

	public Cursor fetchAllTodos() {
		return database.query(DATABASE_TABLE_1, new String[] { KEY_ROWID }, null, null, null,
				null, null);
	}

	
/**
	 * Return a Cursor positioned at the defined todo
	 */

	public Cursor fetchTodo(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE_1, new String[] {
				KEY_ROWID },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String category, String summary,
			String description, int rowid) {
		ContentValues values = new ContentValues();
		values.put(KEY_ROWID, rowid);
		return values;
	}
	
	private ContentValues createContentValues(String position, String apname,
			int num, int total) {
		ContentValues values = new ContentValues();
		values.put(KEY_POSITION, position);
		values.put(KEY_APNAME, apname);
		values.put(KEY_NUM, num);
		values.put(KEY_TOTAL, total);
		return values;
	}
	
	private ContentValues createContentValues(String location, String position, String apname,
			int num, int total) {
		ContentValues values = new ContentValues();
		values.put(KEY_BUILDING, location);
		values.put(KEY_POSITION, position);
		values.put(KEY_APNAME, apname);
		values.put(KEY_NUM, num);
		values.put(KEY_TOTAL, total);
		return values;
	}
	
	private ContentValues createContentValues(String apname,
			int type, float value) {
		ContentValues values = new ContentValues();
		values.put(KEY_APNAME, apname);
		values.put(KEY_TYPE, type);
		values.put(KEY_VALUE, value);
		return values;
	}
	
	private ContentValues createContentValues(String apname,
			int type, float value, int type2, float value2) {
		ContentValues values = new ContentValues();
		values.put(KEY_APNAME, apname);
		values.put(KEY_TYPE, type);
		values.put(KEY_VALUE, value);
		values.put(KEY_TYPE2, type2);
		values.put(KEY_VALUE2, value2);
		return values;
	}
	
	private ContentValues createContentValues(String apname,
			int type, float value, int type2, float value2, int type3, float value3) {
		ContentValues values = new ContentValues();
		values.put(KEY_APNAME, apname);
		values.put(KEY_TYPE, type);
		values.put(KEY_VALUE, value);
		values.put(KEY_TYPE2, type2);
		values.put(KEY_VALUE2, value2);
		values.put(KEY_TYPE3, type3);
		values.put(KEY_VALUE3, value3);
		return values;
	}
	
	private ContentValues createContentValues(String loc, String a, String b,
			int c, float d, int e) {
		ContentValues values = new ContentValues();
		values.put(KEY_BUILDING, loc);
		values.put(KEY_POSITION, a);
		values.put(KEY_APNAME, b);
		values.put(KEY_TYPE, c);
		values.put(KEY_VALUE, d);
		values.put(KEY_UNIQUE, e);
		return values;
	}
	
	private ContentValues createContentValues(String loc, String a, String b,
			int c, float d, int e, float f, int g) {
		ContentValues values = new ContentValues();
		values.put(KEY_BUILDING, loc);
		values.put(KEY_POSITION, a);
		values.put(KEY_APNAME, b);
		values.put(KEY_TYPE, c);
		values.put(KEY_VALUE, d);
		values.put(KEY_TYPE2, e);
		values.put(KEY_VALUE2, f);
		values.put(KEY_UNIQUE, g);
		return values;
	}
	
	private ContentValues createContentValues(String loc, String a, String b,
			int c, float d, int e, float f, int g, float h, int i) {
		ContentValues values = new ContentValues();
		values.put(KEY_BUILDING, loc);
		values.put(KEY_POSITION, a);
		values.put(KEY_APNAME, b);
		values.put(KEY_TYPE, c);
		values.put(KEY_VALUE, d);
		values.put(KEY_TYPE2, e);
		values.put(KEY_VALUE2, f);
		values.put(KEY_TYPE3, g);
		values.put(KEY_VALUE3, h);
		values.put(KEY_UNIQUE, i);
		return values;
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