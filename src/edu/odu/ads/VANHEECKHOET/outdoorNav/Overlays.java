package edu.odu.ads.VANHEECKHOET.outdoorNav;

import java.util.ArrayList;
import java.util.List;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;


public class Overlays extends ItemizedOverlay<OverlayItem>{
	
	private static final String TAG = "OutdoorNav/Overlays"; // Log Tag
	
	private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
	private List<LocationItem> LocationItems;
	private Drawable marker = null;
	
	public Overlays(Drawable marker, List<LocationItem> listItems) {
		super(boundCenterBottom(marker));
		this.marker = marker;
		
		if (listItems != null)
		{
			LocationItems = listItems;
		}		
		generateOverlay();
	    // TODO Auto-generated constructor stub
	    populate();
	}
	
	public void generateOverlay () {
		int i;		
		try 
		{
			for(i=0; i < LocationItems.size(); i++)
			{
				OverlayItem item = LocationItems.get(i).getOverlayItem();
				overlayItemList.add(item);
				Log.d(TAG, "add item " + item.getTitle() + " in overlay");
			}
		}catch (Exception e){
			Log.d(TAG, "Exception : " + e.getMessage());
		}
	}
	
	public void addItem(GeoPoint p, String title, String snippet){
		OverlayItem newItem = new OverlayItem(p, title, snippet);
		overlayItemList.add(newItem);
		populate();
	}
	
	@Override
	protected boolean onTap(int i) {	
		
		return (true);
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	   // TODO Auto-generated method stub
	   return overlayItemList.get(i);
	}
	
	@Override
	public int size() {
	   // TODO Auto-generated method stub
	   return overlayItemList.size();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	   // TODO Auto-generated method stub
	   super.draw(canvas, mapView, shadow);
	   boundCenterBottom(marker);
	}
		
}