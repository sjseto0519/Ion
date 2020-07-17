package edu.odu.ads.VANHEECKHOET.outdoorNav;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;


public class MyOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
	public MyOverlay(Drawable marker) {
	    super(boundCenterBottom(marker));
	    // TODO Auto-generated constructor stub
	    populate();
	}
	
	public void addItem(GeoPoint p, String title, String snippet){
		   OverlayItem newItem = new OverlayItem(p, title, snippet);
		   overlayItemList.add(newItem);
		   populate();
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
		   //boundCenterBottom(marker);
		}
	
}