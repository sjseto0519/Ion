package edu.odu.ads.VANHEECKHOET.outdoorNav;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class LocationItem {
	
	/** Declaration of the variables **/
	
	private static final String TAG = "OutdoorNav/LocationItem"; // Log Tag
	
	private String name;		// name of the item
	private String snippet;		// snippet of the item
	private int isPlan;			// If the item is a sub-map (1 yes, 0 no)
	private double lat;			// latitude of the item
	private double lng;			// longitude of the item
	private String map;			// URL of the map (if isPlan == 1)
	private String marker;		// marker of the item
	private String description;	// description of the item
	private String group;		// the group that the item is part
	private String image;		// URL of an image of the item	
	private String website;		// URL of the website related to the item
	private String plan;		// URL of the plan for the sub-map (if isPlan == 1)
	private int zoom;			// level of zoom (between 1 and 17)
	
	// constructor (basic)
	public LocationItem() {
		name = "";
		snippet = "";
		isPlan = 0;
		lat = 0;
		lng = 0;
		map = "";
		marker = "";
		description = "";
		group = "";
		image = "";
		website = "";
		plan = "";
		zoom = 1;
	}
	
	// constructor (advanced)
	public LocationItem(String newName, String newSnippet, int newIsPlan, double newLat, 
						double newLng, 	String newMap, String newMarker, String newDescription, 
						String newGroup, String newImage, String newWebsite, String newPlan, int newZoom) {
		
		setName(newName);
		setSnippet(newSnippet);
		setIsPlan(newIsPlan);
		setLat(newLat);
		setLng(newLng);
		setMap(newMap);
		setMarker(newMarker);
		setDescription(newDescription);
		setGroup(newGroup);
		setImage(newImage);
		setWebsite(newWebsite);
		setPlan(newPlan);
		setZoom(newZoom);
	}
	
	/** Advanced functions **/
	
	// return the full name (name + snippet + group)
	public String getFullName() {
		String str = new String(name + ", " + group + "\n" + snippet);
		//Log.d(TAG, "return fullName : " + str);
		return str;
	}
	
	// return an OverlayItem
	public OverlayItem getOverlayItem() {
		OverlayItem item = new OverlayItem(getPoint(lat,lng), name, snippet);
		//Log.d(TAG, "return OverlayItem (" + lat + "," + lng + "), " + name + ", " + snippet);
		return item;		
	}
	
	// return a point from lat and lng
	private GeoPoint getPoint(double lat, double lng) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lng * 1000000.0)));
	}
	
	// return the full data into a string
	// "|" is the separator for the data
	public String getFullDataToString() {
		StringBuilder buf = new StringBuilder();
			buf.append(getName());
			buf.append('|');
			buf.append(getSnippet());
			buf.append('|');
			buf.append(Integer.toString(getIsPlan()));
			buf.append('|');
			buf.append(Double.toString(getLat()));
			buf.append('|');
			buf.append(Double.toString(getLng()));
			buf.append('|');
			buf.append(getMap());
			buf.append('|');
			buf.append(getMarker());
			buf.append('|');
			buf.append(getDescription());
			buf.append('|');
			buf.append(getGroup());
			buf.append('|');
			buf.append(getImage());
			buf.append('|');
			buf.append(getWebsite());
			buf.append('|');
			buf.append(getPlan());
			buf.append('|');
			buf.append(Integer.toString(getZoom()));
			buf.append('|');
		//Log.d(TAG, "buf.toString() : " + buf.toString());
		return buf.toString();
	}
	
	// Fill an item with tha data included in a string
	public static void FillItem(LocationItem item, String str) {
	
		int l = str.length();
		int j = 0;
		int nbchar = 0;
		//Log.d(TAG, "l : " + l);
	
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < l; i++) {
			if (str.charAt(i) != '|') {
				buf.append(str.charAt(i));
				nbchar++;
			}
			if (str.charAt(i) == '|') {
				String str2 = new String(buf.toString());
				FillField(item, j,str2);
				//tabwords[j] = str2;
				//Log.d(TAG, "str2 : " + str2);
				// Log.d(TAG, "tabwords[" + j + "] = "+ tabwords[j]);
				j++;
				buf.delete(0, nbchar);
				nbchar = 0;
			}
		}
	}
	
	//Fill a field in an item with the required data
	public static int FillField(LocationItem item, int i, String str2) {
		try {
			if (i == 0)
				item.setName(str2);
			if (i == 1)
				item.setSnippet(str2);
			if (i == 2)
				item.setIsPlan(Integer.valueOf(str2));
			if (i == 3)
				item.setLat(Double.valueOf(str2));
			if (i == 4)
				item.setLng(Double.valueOf(str2));
			if (i == 5)
				item.setMap(str2);
			if (i == 6)
				item.setMarker(str2);
			if (i == 7)
				item.setDescription(str2);
			if (i == 8)
				item.setGroup(str2);
			if (i == 9)
				item.setImage(str2);
			if (i == 10)
				item.setWebsite(str2);
			if (i == 11)
				item.setPlan(str2);
			if (i == 12)
				item.setZoom(Integer.valueOf(str2));
		}catch (Exception e) {
			Log.d(TAG, "Exception : " + e.getMessage());
		}
		return 0;
	}
	
	// Extract data from a ressource and create the items for the LocationItems list
	public static boolean ExtractDataFromURL(String url, List<LocationItem> LocationItems) {
		LocationItem newItem;
		InputStream in = null;
		String line;
		
		Log.d(TAG, "Start Extract data from file " + url);
		
		try {
			URL urlImage = new URL(url);	
			HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();	
			in = connection.getInputStream();		
		}
		catch (Exception e) {
			e.printStackTrace();
		}	 

		try {
			InputStreamReader ipsr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(ipsr);
			while ((line = br.readLine()) != null) {
				newItem = new LocationItem();
				FillItem(newItem, line);	
				LocationItems.add(newItem);
			}
			br.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		return false;
	}	

	
	/** Basic functions **/
	
	// set the name
	public boolean setName(String str) {
		name = str;
		//Log.d(TAG, "name = " + str);
		return true;
	}
	
	// return the name
	public String getName() {
		String str = new String(name);
		return str;
	}
	
	// set the snippet
	public boolean setSnippet(String str) {
		snippet = str;
		//Log.d(TAG, "snippet = " + str);
		return true;
	}
	
	// return the snippet
	public String getSnippet() {
		String str = new String(snippet);
		return str;
	}
	
	// set isPlan
	public boolean setIsPlan(int i) {
		if ( (i==1) || (i==0) )
		{
			isPlan = i;
			//Log.d(TAG, "Isplan = " + i);
			return true;
		}
		else
		{
			isPlan = 0;
			Log.d(TAG, "error SetIsplan : " + i + " is not 0 or 1");
			return false;
		}
	}
		
	// return isPlan
	public int getIsPlan() {		
		return isPlan;
	}
	
	// set the latitude
	public boolean setLat(double i) {
		if ( (i>=0) && (i<=90) )
		{
			lat = i;
			//Log.d(TAG, "lat = " + i);
			return true;
		}
		else
		{
			lat = 0;
			Log.d(TAG, "error setLat : " + i + "is not between 0 and 90°");
			return false;
		}
	}
	
	// return the latitude
	public double getLat() {		
		return lat;
	}
	
	// set the longitude
	public boolean setLng(double i) {
		if ( (i>=-180) && (i<=180) )
		{
			lng = i;
			//Log.d(TAG, "lng = " + i);
			return true;
		}
		else
		{
			lng = 0;
			Log.d(TAG, "error setLng : " + i + "is not between -180 and 180°");
			return false;
		}
	}
	
	// return the longitude
	public double getLng() {		
		return lng;
	}	
	
	// set the URL of the Map
	public boolean setMap(String str) {
		map = str;
		//Log.d(TAG, "map = " + str);
		return true;			
	}
			
	// return the URL of the map
	public String getMap() {
		String str = new String(map);
		return str;
	}
	
	// set the name of the marker
	public boolean setMarker(String str) {
		marker = str;
		//Log.d(TAG, "marker = " + str);
		return true;			
	}
		
	// return the name of the marker
	public String getMarker() {
		String str = new String(marker);
		return str;
	}	
	
	// set the description
	public boolean setDescription(String str) {
		description = str;
		//Log.d(TAG, "description = " + str);
		return true;			
	}
			
	// return the description
	public String getDescription() {
		String str = new String(description);
		return str;
	}
	
	// set the group
	public boolean setGroup(String str) {
		group = str;
		//Log.d(TAG, "group = " + str);
		return true;			
	}
			
	// return the group
	public String getGroup() {
		String str = new String(group);
		return str;
	}
	
	// set the URL of the image
	public boolean setImage(String str) {
		image = str;
		//Log.d(TAG, "image = " + str);
		return true;			
	}
			
	// return the URL of the image
	public String getImage() {
		String str = new String(image);
		return str;
	}
	
	// set the URL of the website
	public boolean setWebsite(String str) {
		website = str;
		//Log.d(TAG, "website = " + str);
		return true;			
	}
			
	// return the URL of the website
	public String getWebsite() {
		String str = new String(website);
		return str;
	}
	
	// set the URL of the plan
	public boolean setPlan(String str) {
		plan = str;
		//Log.d(TAG, "plan = " + str);
		return true;			
	}
			
	// return the URL of the plan
	public String getPlan() {
		String str = new String(plan);
		return str;
	}
	
	// set the zoom
	public boolean setZoom(int i) {
		if ( (i>=1) && (i<=17) )
		{
			zoom = i;
			//Log.d(TAG, "zoom = " + i);
			return true;
		}
		else
		{
			zoom = 1;
			Log.d(TAG, "error setZoom : " + i + "is not between 1 and 17");
			return false;
		}
	}
	
	// return the longitude
	public int getZoom() {		
		return zoom;
	}	

}
