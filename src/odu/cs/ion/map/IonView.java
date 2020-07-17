package odu.cs.ion.map;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import odu.cs.ion.database.Location;
import odu.cs.ion.indoor.IndoorLocate;
import odu.cs.ion.ClassInformation;
import odu.cs.ion.IonXml;
import odu.cs.ion.R;

import java.util.*;
import odu.cs.ion.path.*;

public class IonView extends ImageView implements OnTouchListener {

	private Context context;
	private boolean move = false;
	private int counter = 0;
	private int xpos = 0;
	private int ypos = 0;
	private long time;
	private boolean cancelpress = false;
	public int lastPressX;
	public int lastPressY;
	public int lastBlockX;
	public int lastBlockY;
	public ArrayList<Location> locations;
	public static boolean enablelogging = false;

	public IonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		
		setOnTouchListener(this);

	      // ...
	      // Work around a Cupcake bug
	      matrix.setTranslate(1f, 1f);
	      setImageMatrix(matrix);
		// TODO Auto-generated constructor stub
	      
	      
	}
	
	public void setLocations(ArrayList<Location> a)
	{
		locations = a;
	}
	
	public void showRoomInformation(int x, int y)
	{
		int[] bn = getBlockNumber(x,y);
		String indoorPlaceName = ((IndoorLocate)context).getIndoorPlaceName();
		String currentfloor = ((IndoorLocate)context).getCurrentFloor();
		
		for (int i = 0; i < locations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = locations.get(i);
			String type = ii.getType();
			String name = ii.getPlaceName();
			int posx = ii.getPosX();
			int posy = ii.getPosY();
			int[] bn2 = getBlockNumber(posx, posy);
			int floor = ii.getFloor();
            if (enablelogging) Log.e("room1",type+" "+name+" "+floor);
			if (!indoorPlaceName.equals(name)) continue;
			if (!type.contains("room ")) continue;
			int ind = type.indexOf(" ");
			String room = type.substring(ind+1, type.length());
			if (enablelogging) Log.e("positionroom",""+bn[0]+" "+bn[1]+" "+bn2[0]+" "+bn2[1]);
			if (bn[0]==bn2[0] && bn[1]==bn2[1])
			{
				if (enablelogging) Log.e("looking up room ",room);
				IonXml ix = new IonXml("http://scott-seto.com/appdoublescreen/"+room+".xml");
		         ix.parseUrl();
		         //Log.e("position", ""+position[0]+" "+position[1]);
		         ClassInformation ci = ix.getClassInformation();
		         if (enablelogging) Log.e("classinfo",ci.getClassName()+" "+ci.getStartTime()+" "+ci.getEndTime());
		         TextView tx11 = (TextView)((IndoorLocate)context).findViewById(R.id.textView9);
			 		tx11.setText(ci.getClassName()+"\n"+ci.getStartTime()+"\n"+ci.getEndTime());
			 		
		         
		         
		         break;
			}
			
		}

	}
	
	public int[] getLastPress()
	{
		return new int[] {lastPressX, lastPressY};
	}
	
	public int[] getLastBlock()
	{
		return new int[] {lastBlockX, lastBlockY};
	}
	
	public void placePerson(int a, int b)
	{
		((IonDrawable)getDrawable()).placePerson(a,b);
	}
	
	public void placePersonByPixel(int a, int b)
	{
		((IonDrawable)getDrawable()).placePersonByPixel(a,b);
	}
	
	public void moveToPixel(int[] a, String nn)
	{
		matrix.setTranslate(-1f*a[0]+200, -1f*a[1]+100);
	      setImageMatrix(matrix);
		((IonDrawable)getDrawable()).createMarker(a[0], a[1], nn);
	}
	
	public void moveToPixel(int[] a)
	{
		matrix.setTranslate(-1f*a[0]+200, -1f*a[1]+100);
	      setImageMatrix(matrix);
	}
	
	public Matrix getMatrix()
	{
		return matrix;
	}

	private static final String TAG = "Touch";
	   // These matrices will be used to move and zoom image
	   Matrix matrix = new Matrix();
	   Matrix savedMatrix = new Matrix();

	   // We can be in one of these 3 states
	   static final int NONE = 0;
	   static final int DRAG = 1;
	   static final int ZOOM = 2;
	   int mode = NONE;

	   // Remember some things for zooming
	   PointF start = new PointF();
	   PointF end = new PointF();
	   PointF mid = new PointF();
	   float oldDist = 1f;
	   
	   public int[] getBlockNumber(float a, float b)
	   {
		   int[] c = new int[2];
		   c[0] = (int)Math.floor(a/50.0d);
		   c[1] = (int)Math.floor(b/50.0d);
	       return c;
	   }
	    
	   @Override
	   public boolean onTouch(View v, MotionEvent rawEvent) {
		   
		   
	      WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
	      // ...
	      ImageView view = (ImageView) v;

	      // Dump touch event to log
	      dumpEvent(event);

	      // Handle touch events here...
	      switch (event.getAction() & MotionEvent.ACTION_MASK) {
	      case MotionEvent.ACTION_DOWN:

	         savedMatrix.set(matrix);
	         start.set(event.getX(), event.getY());
	         end.set(-1000,-1000);
	         Rect rr = getDrawable().getBounds();
	         if (enablelogging) Log.d(TAG, "mode=DRAG "+rr.left+" "+rr.right+" "+rr.top+" "+rr.bottom+" "+xpos+" "+ypos);
	         mode = DRAG;
	         time = System.currentTimeMillis();
	         cancelpress = false;
	         break;
	      case MotionEvent.ACTION_POINTER_DOWN:
	         oldDist = spacing(event);
	         if (enablelogging) Log.d(TAG, "oldDist=" + oldDist);
	         if (oldDist > 10f) {
	            savedMatrix.set(matrix);
	            midPoint(mid, event);
	            mode = ZOOM;
	            if (enablelogging) Log.d(TAG, "mode=ZOOM");
	         }
	         break;
	      case MotionEvent.ACTION_UP:
	    	  
	    	  if (end.x != -1000)
	    	  {
	    	  xpos+=(end.x-start.x);
	    	  ypos+=(end.y-start.y);
	    	  if (enablelogging) Log.e("position",""+xpos+" "+ypos);
	    	  if (enablelogging) Log.e("moveup", ""+end.x+" "+end.y+" "+start.x+" "+start.y);
 
	    	  }
	    	  case MotionEvent.ACTION_POINTER_UP:
	         mode = NONE;
	         if (enablelogging) Log.d(TAG, "mode=NONE");
	         
	         //if (System.currentTimeMillis()-time < 1000)
	         //{
	         if (!cancelpress) {
	         lastPressX = (int)(start.x-xpos);
	         lastPressY = (int)(start.y-ypos);
	         showRoomInformation(lastPressX, lastPressY);
	         int[] bn2 = getBlockNumber(lastPressX, lastPressY);
	         lastBlockX = bn2[0];
	         lastBlockY = bn2[1];
	         ((IndoorLocate)context).setPositionChange(new int[] {lastPressX, lastPressY, lastBlockX, lastBlockY});
	         }
	         //}
	         
	         if (enablelogging) Log.e("press",""+(start.x-xpos)+" "+(start.y-ypos));
	         
	         if (!cancelpress && System.currentTimeMillis()-time > 1000)
	         {
	        	 int[] bn = getBlockNumber((start.x-xpos),(start.y-ypos));
	         //Toast.makeText(context, "long "+(System.currentTimeMillis()-time)+" "+bn[0]+" "+bn[1], Toast.LENGTH_SHORT).show();
	         
	        	 if (enablelogging) Log.e("dist",""+((end.x-start.x)+(end.y-start.y)));
	         float dd = ((end.x-start.x)+(end.y-start.y));
	         if (dd < 8.0f && dd > -8.0f)
	         {
	           ((IonDrawable)getDrawable()).colorGrid(bn[0],bn[1]);
	           ((IndoorLocate)context).getFingerprint(bn);
	         }
	         
	         }
	         else
	         {
	        	 //Toast.makeText(context, "short "+(System.currentTimeMillis()-time)+" "+(start.x-xpos)+" "+(start.y-ypos), Toast.LENGTH_SHORT).show();
	  	       	 
	         }
	         
	         if (move == false)
	         {
	        	 //Toast.makeText(context, "touch", Toast.LENGTH_SHORT).show();
	        	 counter++;
	        	 //((IonDrawable)getDrawable()).movePoint(counter);
	        	 
	         }
	         
	         if (move == true) move = false;
	         
	         break;
	      case MotionEvent.ACTION_MOVE:
	         if (mode == DRAG) {
	        	 move = true;
	            // ...
	            matrix.set(savedMatrix);
	            matrix.postTranslate(event.getX() - start.x,
	                  event.getY() - start.y);
	            end.set(event.getX(), event.getY());
	            int dist = (int)Math.abs(event.getX() - start.x) + (int)Math.abs(event.getY() - start.y);
	            if (dist > 10) {
	            	if (enablelogging) Log.e("cancelpress dist=",""+dist);
	            	cancelpress = true;
	            }
	            
	         }
	         else if (mode == ZOOM) {
	        	 /*
	            float newDist = spacing(event);
	            Log.d(TAG, "newDist=" + newDist);
	            if (newDist > 10f) {
	               matrix.set(savedMatrix);
	               float scale = newDist / oldDist;
	               matrix.postScale(scale, scale, mid.x, mid.y);
	            }
	            */
	         }
	         break;
	      }

	      view.setImageMatrix(matrix);
	      
	      
	      return true; // indicate event was handled
	   }

	   /** Show an event in the LogCat view, for debugging */
	   private void dumpEvent(WrapMotionEvent event) {
	      // ...
	      String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
	            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
	      StringBuilder sb = new StringBuilder();
	      int action = event.getAction();
	      int actionCode = action & MotionEvent.ACTION_MASK;
	      sb.append("event ACTION_").append(names[actionCode]);
	      if (actionCode == MotionEvent.ACTION_POINTER_DOWN
	            || actionCode == MotionEvent.ACTION_POINTER_UP) {
	         sb.append("(pid ").append(
	               action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
	         sb.append(")");
	      }
	      sb.append("[");
	      for (int i = 0; i < event.getPointerCount(); i++) {
	         sb.append("#").append(i);
	         sb.append("(pid ").append(event.getPointerId(i));
	         sb.append(")=").append((int) event.getX(i));
	         sb.append(",").append((int) event.getY(i));
	         if (i + 1 < event.getPointerCount())
	            sb.append(";");
	      }
	      sb.append("]");
	      if (enablelogging) Log.d(TAG, sb.toString());
	   }

	   /** Determine the space between the first two fingers */
	   private float spacing(WrapMotionEvent event) {
	      // ...
	      float x = event.getX(0) - event.getX(1);
	      float y = event.getY(0) - event.getY(1);
	      return FloatMath.sqrt(x * x + y * y);
	   }

	   /** Calculate the mid point of the first two fingers */
	   private void midPoint(PointF point, WrapMotionEvent event) {
	      // ...
	      float x = event.getX(0) + event.getX(1);
	      float y = event.getY(0) + event.getY(1);
	      point.set(x / 2, y / 2);
	   }
	   
}
