package odu.cs.ion.indoor;

import odu.cs.ion.ClassInformation;
import odu.cs.ion.IonMenu;
import odu.cs.ion.IonXml;
import odu.cs.ion.R;
import odu.cs.ion.database.*;
import odu.cs.ion.map.IndoorNav;
import odu.cs.ion.map.IonView;
import odu.cs.ion.pedometer.*;
import odu.cs.ion.wifi.Utilities;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import com.kyocera.dualscreen.DualScreen;



import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import odu.cs.ion.gps.*;

public class IndoorLocate extends Activity {
	
	private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    
    private TextView mStepValueView;
    private TextView mDistanceValueView;
    TextView mDesiredPaceView;
    private int mStepValue;
    private float mDistanceValue;
    private boolean mIsMetric;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
    private int fingerprintcount = 11;
    private float minavgwifi = 1000.0f;
    private float maxavgwifi = 0.0f;
    private float medavgwifi = 0.0f;
    private float avgavgwifi = 0.0f;
    private float minwifi = 1000.0f;
    private float maxwifi = 0.0f;
    private float medwifi = 0.0f;
    private float avgwifi = 0.0f;
    private Map<String, ArrayList<Float> > favgwifimap = new HashMap<String, ArrayList<Float> >();
    private Map<String, ArrayList<Float> > fwifimap = new HashMap<String, ArrayList<Float> >();
    private boolean nowifi = false;
    private DualScreen dualscreen;
    public ArrayList<String> accesspoints;
    public boolean donotsave = false;
    public int[] foundposition;
    public int fingerprintmax = 10;
    public float[] initialstep;
    public float[] currentstep;
    public float[] changestep;
    public int northposition;
    public int CompassAccuracy;
    public ArrayList<odu.cs.ion.database.Location> mylocations;
    public int[] destinationposition;
    public int destinationfloor;
    public ArrayList<String> duplicates = new ArrayList<String>();
    public String packagename = "odu.cs.ion";
    public static boolean enablelogging = false;
    
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;
	
	private WifiManager mainWifi;
	private SensorManager sensorManager;
	private WifiReceiver receiverWifi;
	
	private double[][] wifiLocation;
//	private double myLatitude, myLongitude;
	
	private float CompassValue = 0;
	String presentLoc;
	boolean toScan = false;
	
	List<ScanResult> wifiList;
	
	TextView mainText;
	//TextView mainText2;
	TextView mainText3;
	TextView txtCompass;
	
	StringBuilder sb = new StringBuilder();
	StringBuilder sb2 = new StringBuilder();
	
	HashMap<String, String> poiHash;
	
	private LocationManager locManager;
	private volatile boolean averaging = false;
	private boolean firstFix = false;
	private boolean returnToDifferentApp = false;
	
	private static final String ALT = "alt"; //$NON-NLS-1$
	private static final String GPS = "gps"; //$NON-NLS-1$
	private static final String LATLON = "latlon"; //$NON-NLS-1$
	private static final String LATLON_ACCURACY = "latlon_accuracy"; //$NON-NLS-1$

	private GpsAverageList measurements;
	private Exporter exporter;
	
	private int SamplingIntval = 2000;
	
	public Map<String, ArrayList<Integer> > wifimap;
	public Map<String, Float> wifiavg;
	public ArrayList<String> wificheck1;
	public ArrayList<String> wificheck2;
	public int wificheckcount = 0;
	public GridDbAdapter gdb;
	public int[] currentposition = new int[] {-1,-1};
	public String onEmulator;
	public String onDualScreen;
	public String indoorPlaceName;
	public String currentfloor;
	public String indoorimage;
	public String stepsize;
	public IndoorNav indoornav;
	public float stepsize1;
	public int indoorwidth;
	public int indoorheight;
	public int inchwidth;
	public int inchheight;
	public PowerManager.WakeLock wakelock;
	public PlacesDbAdapter pda;
	public String imagename;
	public ArrayList<odu.cs.ion.database.Map> mymaps;
	public Dialog floordialog;
	public Dialog roomdialog;
	public int[] startposition;
	public int[] elevatorposition;
	public static int[] positionchange = null;
	public float[] diststep = new float[2];
	public int[] finalpath = null;
	public int[] finalpathelev = null;
	public int finalpathfloor = -1;
	public boolean changefloor = false;
	public boolean stoploop = false;
	public String spaces = "                              ";
	
	public String getIndoorPlaceName()
	{
		return indoorPlaceName;
	}
	
	public String getCurrentFloor()
	{
		return currentfloor;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.indoormenu, menu);
        setMenuBackground();
        return true;
    }
    
   
         protected void setMenuBackground(){  
               
             if (enablelogging) Log.d(TAG, "Enterting setMenuBackGround");  
             getLayoutInflater().setFactory( new Factory() {  
                   
                 @Override  
                 public View onCreateView ( String name, Context context, AttributeSet attrs ) {  
                   
                     if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {  
                           
                         try { // Ask our inflater to create the view  
                             LayoutInflater f = getLayoutInflater();  
                             final View view = f.createView( name, null, attrs );  
                             /*  
                              * The background gets refreshed each time a new item is added the options menu.  
                              * So each time Android applies the default background we need to set our own  
                              * background. This is done using a thread giving the background change as runnable 
                              * object 
                              */  
                             new Handler().post( new Runnable() {  
                               public void run () {  
                                     view.setBackgroundColor(0x00FFFFFF); 
                                 }  
                             } );  
                             return view;  
                         }  
                         catch ( InflateException e ) {}  
                         catch ( ClassNotFoundException e ) {}  
                     }  
                     return null;  
                 }  
             });  
         }  
   
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icontext1:     
              changeFloor(null);
            break;
            case R.id.icontext2:     
                goToRoom(null);
                break;
            case R.id.icontext3:     
                goOutside(null);
                break;
            case R.id.icontext4:
            	scanQRCode();
            	break;
        }
        return true;
    }
    
    public void scanQRCode()
    {
    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        
        startActivityForResult(intent, 0);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
  	   if (requestCode == 0) {
  	
  	      if (resultCode == RESULT_OK) {
  	
  	         String contents = intent.getStringExtra("SCAN_RESULT");
  	
  	         String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
  	
  	       if (enablelogging) Log.e("contents",contents);
  	         //Toast.makeText(this, ""+contents, Toast.LENGTH_SHORT).show();
  	        	
  	         IonXml ix = new IonXml(contents);
  	         ix.parseUrl();
  	         int[] position = ix.getPosition();
  	         //Log.e("position", ""+position[0]+" "+position[1]);
  	         ClassInformation ci = ix.getClassInformation();
  	         String placename = ix.getPlaceName();
  	         String floor2 = ix.getFloor();
  	         if (floor2.equals(currentfloor))
  	         {
  	        	 indoornav.placePerson(position[0],position[1]);
  	         }
  	         else
  	         {
  	        	 currentfloor = floor2;
  	        	 reload();
  	        	indoornav.placePerson(position[0],position[1]);

  	        	if (enablelogging) Log.e("classinfo",ci.getClassName()+" "+ci.getStartTime()+" "+ci.getEndTime());
  	        TextView tx11 = (TextView)this.findViewById(R.id.textView9);
	 		tx11.setText(ci.getClassName()+"\n"+ci.getStartTime()+"\n"+ci.getEndTime());
	 		
  	         }

  	         
  	         // Handle successful scan
  	
  	      } else if (resultCode == RESULT_CANCELED) {
  	
  	         // Handle cancel
  	
  	      }
  	
  	   }
  	
  	}
	
	public int[] getPixelLocation(int[] position)
	{
		int[] pl = new int[2];
		pl[0] = (int)((position[0]*50.0f)+25.0f);
		pl[1] = (int)((position[1]*50.0f)+25.0f);
		return pl;
	}
	
	public void drawPixelBasedPath(int[] a, int[] b)
	{
		indoornav.drawPixelBasedPath(a, b);
	}
	
	public void initialize()
	{
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(true);
		initialstep = new float[] {0.0f, 0.0f};
		currentstep = new float[] {0.0f, 0.0f};
		changestep = new float[] {0.0f, 0.0f};
		stepsize1 = (float)Integer.parseInt(stepsize);
		if (enablelogging) Log.e("stepsize",""+stepsize1);
		northposition(244);
		pda = new PlacesDbAdapter();
		  pda.open();
		  fillLocations();
		  fillMaps();
		
		  indoornav.setIndoorLocate(this);
		  indoornav.setLocations(mylocations);
		  startposition = getStartPosition();
		  elevatorposition = getElevatorPosition();
		  
	}
	
	public int[] getElevatorPosition()
	{
		for (int i = 0; i < mylocations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = mylocations.get(i);
			String type = ii.getType();
			String name = ii.getPlaceName();
			int floor = ii.getFloor();
			if (floor != (int)Integer.parseInt(currentfloor)) continue;
			if (type.equals("elevator") && name.equals(indoorPlaceName))
			{
				return new int[] {ii.getPosX(), ii.getPosY()};
			}
		}
		for (int i = 0; i < mylocations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = mylocations.get(i);
			String type = ii.getType();
			String name = ii.getPlaceName();
			int floor = ii.getFloor();
			if (floor != (int)Integer.parseInt(currentfloor)) continue;
			if (type.equals("stairs") && name.equals(indoorPlaceName))
			{
				if (enablelogging) Log.e("elevposition",""+ii.getPosX()+" "+ii.getPosY());
				return new int[] {ii.getPosX(), ii.getPosY()};
			}
		}
		return new int[] {25,25};
	}

	public int[] getStartPosition()
	{
		for (int i = 0; i < mylocations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = mylocations.get(i);
			String type = ii.getType();
			String name = ii.getPlaceName();
			int floor = ii.getFloor();
			if (floor != (int)Integer.parseInt(currentfloor)) continue;
			if (type.equals("door") && name.equals(indoorPlaceName))
			{
				if (enablelogging) Log.e("startposition",""+ii.getPosX()+" "+ii.getPosY());
				return new int[] {ii.getPosX(), ii.getPosY()};
			}
		}
		return new int[] {25,25};
	}
	
	public void fillMaps()
	{
		mymaps = pda.getMaps();
		if (enablelogging) Log.e("maps", ""+mymaps.size());
	}
	
	public void fillLocations()
	{
		mylocations = pda.getLocations(imagename);
		if (enablelogging) Log.e("filllocation",imagename+" "+currentfloor);
		if (enablelogging) Log.e("locations", ""+mylocations.size());
	}
	
	public void northposition(int a)
	{
		northposition = a;
	}
	
	public void moveStep(float value)
	{
		changestep = new float[] {0.0f, 0.0f};
		value -= northposition;
		if (value < 0) value = 360+value;
		
		if (value == 0)
		{
			changestep[1] += -1.0f*stepsize1;
		}
		else if (value == 90)
		{
			changestep[0] += 1.0f*stepsize1;
		}
		else if (value == 180)
		{
			changestep[1] += 1.0f*stepsize1;
		}
		else if (value == 270)
		{
			changestep[0] += -1.0f*stepsize1;
		}
		else if (value > 0 && value < 90)
		{
			float diff = value;
			float x = (float)Math.sin(Math.toRadians(diff))*stepsize1;
			float y = (float)Math.cos(Math.toRadians(diff))*stepsize1;
			changestep[0] += x;
			changestep[1] -= y;
		}
		else if (value > 90 && value < 180)
		{
			float diff = 180-value;
			float x = (float)Math.sin(Math.toRadians(diff))*stepsize1;
			float y = (float)Math.cos(Math.toRadians(diff))*stepsize1;
			changestep[0] += x;
			changestep[1] += y;
		}
		else if (value > 180 && value < 270)
		{
			float diff = value-180;
			float x = (float)Math.sin(Math.toRadians(diff))*stepsize1;
			float y = (float)Math.cos(Math.toRadians(diff))*stepsize1;
			changestep[0] -= x;
			changestep[1] += y;
		}
		else if (value > 270 && value < 360)
		{
			float diff = 360-value;
			float x = (float)Math.sin(Math.toRadians(diff))*stepsize1;
			float y = (float)Math.cos(Math.toRadians(diff))*stepsize1;
			changestep[0] -= x;
			changestep[1] -= y;
		}
		
		if (enablelogging) Log.e("stepchange ",""+changestep[0]+" "+changestep[1]+" "+value+" "+stepsize1);
		
		//currentstep[0] += changestep[0];
		//currentstep[1] += changestep[1];
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        diststep[0] = 0.0f;
        diststep[1] = 0.0f;
        mIsRunning = false;
        Intent ii = getIntent();
            onEmulator = ii.getStringExtra("onEmulator");
            onDualScreen = ii.getStringExtra("onDualScreen");
            indoorPlaceName = ii.getStringExtra("placename");
            if (indoorPlaceName.contains("Engineering & Computional Science Department")) indoorPlaceName = "E&CS";
            if (indoorPlaceName.contains("Scott Seto")) indoorPlaceName = "Scott's Home";
            
            String qrfloor = ii.getStringExtra("floor");
            if (qrfloor != null)
            {
            	currentfloor = qrfloor;
            }
            currentfloor = ""+1;
           
            stepsize = ""+24;
            
            imagename = indoorPlaceName;
            
            pda = new PlacesDbAdapter();
      	  pda.open();
      	  mymaps = pda.getMaps();
      	  int floor = Integer.parseInt(currentfloor);
      	  String name = indoorPlaceName;
            for (int i = 0; i < mymaps.size(); ++i)
            {
            	odu.cs.ion.database.Map mm = mymaps.get(i);
            	boolean out = mm.outdoor();
            	String nn = mm.getName();
            	if (enablelogging) Log.e("nameandimage",name+" "+nn+" "+mm.getImage());
            	if (nn.contains(name) && out == false && floor == mm.getFloor())
            	{
            		indoorimage = mm.getImage();
            		indoorwidth = mm.getWidth();
            	    indoorheight = mm.getHeight();	
            	    inchwidth = mm.getInchWidth();
            	    inchheight = mm.getInchHeight();
            	}
            }
            
            pda.close();
            
            
            String pos1 = ii.getStringExtra("position");
            int xx1 = 0; int yy1 = 0;
            if (pos1 != null)
            {
              int ind = pos1.indexOf(" ");
              xx1 = Integer.parseInt(pos1.substring(0,ind));
              yy1 = Integer.parseInt(pos1.substring(ind+1, pos1.length()));
            }


            
            if (enablelogging) Log.e("indoorplacename",indoorPlaceName);
            if (enablelogging) Log.e("currentfloor",currentfloor);
            if (enablelogging) Log.e("indoorimage",indoorimage);
            if (enablelogging) Log.e("indoorwidth",""+indoorwidth);
            if (enablelogging) Log.e("indoorheight",""+indoorheight);
            
            
            
        try {
            if (onDualScreen.equals("true")) dualscreen = new DualScreen(this);
          }
          catch (Exception e) {}
        
        mStepValue = 0;
        
        wifimap = new HashMap<String, ArrayList<Integer> >();
        wifiavg = new HashMap<String, Float>();
        wificheck1 = new ArrayList<String>();
        wificheck2 = new ArrayList<String>();
        
    	if ( dualscreen == null || dualscreen.getScreenMode() == DualScreen.FULL ) {
  	       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  	       setContentView(R.layout.indoormain); 
     	}
     	else
     	{
     		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
 	           setContentView(R.layout.indoormain);  
     	}
    	
    	 TextView tx11 = (TextView)this.findViewById(R.id.textView9);
	 		tx11.setText(indoorPlaceName+"\n"+"Floor "+currentfloor);
	 		
    	
    	indoornav = new IndoorNav(getResources(), getBaseContext(), this);
        
        
        initialize();

        
        mUtils = Utils.getInstance();
        
        //txtCompass 	= (TextView)findViewById(R.id.txtCompass);
        //mainText 	= (TextView)findViewById(R.id.mainText);
        //mainText2 	= (TextView)findViewById(R.id.mainText2);
        //mainText3 	= (TextView)findViewById(R.id.mainText3);
        
        wifiLocation = new double[3][4]; 
        
        // Create WifiManager & Register the receiver
        mainWifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        
        
        // Create Compass
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensorList.size() > 0)
        {
        //sensorManager.registerListener(sensorListener, sensorList.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        
        sampleWifi();
        }
        
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        measurements = new odu.cs.ion.gps.GpsAverageList();
		exporter = new Exporter(getApplicationContext());
		
		
		gdb = new GridDbAdapter(indoorPlaceName+" Floor "+currentfloor);
		gdb.open();
		//gdb.deleteAll(true);
        gdb.create();
        //gdb.addProb("0 0", "AppDoubleScreen", true);
        
        
        int resID = getResources().getIdentifier(indoorimage, "drawable", packagename);
        indoornav.setId(resID);
        indoornav.setGraph(indoorimage+"graph");
        indoornav.setFloorPlan("floorplan2");
        indoornav.setSize(indoorwidth,indoorheight);
        indoornav.createMap();
        
        
        if (pos1 != null)
        {
        	indoornav.placePerson(xx1, yy1);
        }
        
        // gets the current location
        getLocation(true);
        
        drawIcons();
        
        //drawPath(116, 129, 377, 120);
        

    }
    
    public void reload()
    {
    	
    	TextView tx11 = (TextView)this.findViewById(R.id.textView9);
 		tx11.setText(indoorPlaceName+"\n"+"Floor "+currentfloor);
 		
 		
    	pda.close(); 
    	  pda = new PlacesDbAdapter();
      	  pda.open();
      	  mymaps = pda.getMaps();
      	  int floor = Integer.parseInt(currentfloor);
      	  String name = indoorPlaceName;
            for (int i = 0; i < mymaps.size(); ++i)
            {
            	odu.cs.ion.database.Map mm = mymaps.get(i);
            	boolean out = mm.outdoor();
            	String nn = mm.getName();
            	if (enablelogging) Log.e("nameandimage",name+" "+nn+" "+mm.getImage());
            	int ind2 = name.indexOf(" ");
            	String n2 = "";
            	if (ind2 != -1) n2 = name.substring(0,ind2);
            	else n2 = name;
            	if (nn.contains(n2) && out == false && floor == mm.getFloor())
            	{
            		indoorimage = mm.getImage();
            		indoorwidth = mm.getWidth();
            	    indoorheight = mm.getHeight();	
            	    inchwidth = mm.getInchWidth();
            	    inchheight = mm.getInchHeight();
            	}
            }
            
            pda.close();
            
            
            int xx1 = 0; int yy1 = 0;



            
            if (enablelogging) Log.e("indoorplacename",indoorPlaceName);
            if (enablelogging) Log.e("currentfloor",currentfloor);
            if (enablelogging) Log.e("indoorimage",indoorimage);
            if (enablelogging) Log.e("indoorwidth",""+indoorwidth);
            if (enablelogging) Log.e("indoorheight",""+indoorheight);
        
            initialize();
            
        try {
            if (onDualScreen.equals("true")) dualscreen = new DualScreen(this);
          }
          catch (Exception e) {}
        
        mStepValue = 0;
        
        wifimap = new HashMap<String, ArrayList<Integer> >();
        wifiavg = new HashMap<String, Float>();
        wificheck1 = new ArrayList<String>();
        wificheck2 = new ArrayList<String>();
        


        
        mUtils = Utils.getInstance();
        
        //txtCompass 	= (TextView)findViewById(R.id.txtCompass);
        //mainText 	= (TextView)findViewById(R.id.mainText);
        //mainText2 	= (TextView)findViewById(R.id.mainText2);
        //mainText3 	= (TextView)findViewById(R.id.mainText3);
        
        wifiLocation = new double[3][4]; 
        
        // Create WifiManager & Register the receiver
        mainWifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        
        
        // Create Compass
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensorList.size() > 0)
        {
        //sensorManager.registerListener(sensorListener, sensorList.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        
        sampleWifi();
        }
        
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        measurements = new odu.cs.ion.gps.GpsAverageList();
		exporter = new Exporter(getApplicationContext());
		
		gdb.close();
		gdb = new GridDbAdapter(indoorPlaceName+" Floor "+currentfloor);
		gdb.open();
		//gdb.deleteAll(true);
        gdb.create();
        //gdb.addProb("0 0", "AppDoubleScreen", true);
        
        
        //indoornav = new IndoorNav(getResources(), getBaseContext(), this);
        int resID = getResources().getIdentifier(indoorimage, "drawable", packagename);
        indoornav.setId(resID);
        indoornav.setGraph(indoorimage+"graph");
        indoornav.setFloorPlan("floorplan2");
        indoornav.setSize(indoorwidth,indoorheight);
        indoornav.createMap();
        
       
        
        // gets the current location
        getLocation(null);
        
        drawIcons();
        
        //drawPath(116, 129, 377, 120);
    	
    	
    	
    }
    
    public void placePerson(int x, int y)
    {
    	indoornav.placePerson(x, y);
    }
    
    public void placePersonByPixel(int x, int y)
    {
    	indoornav.placePersonByPixel(x, y);
    }
    
    public void drawPath(int ax, int ay, int bx, int by)
    {
    	indoornav.drawPath(ax, ay, bx, by);
    }
    
    public void drawIcons()
    {
    	for (int i = 0; i < mylocations.size(); ++i)
    	{
    		odu.cs.ion.database.Location ii = mylocations.get(i);
    		String type = ii.getType();
    		int floor = ii.getFloor();
    		if (floor != (int)Integer.parseInt(currentfloor)) continue;
    		Bitmap bmp33 = null;
    		if (type.equals("door"))
    		{
    			int resID = getResources().getIdentifier("door", "drawable", packagename);
    			bmp33 = BitmapFactory.decodeResource(getResources(), resID);
    	        bmp33 = Bitmap.createScaledBitmap(bmp33, 40, 40, true);
    		}
    		else if (type.equals("stairs"))
    		{
    			int resID = getResources().getIdentifier("stairs", "drawable", packagename);
    			bmp33 = BitmapFactory.decodeResource(getResources(), resID);
    	        bmp33 = Bitmap.createScaledBitmap(bmp33, 40, 40, true);
    		}
    		else if (type.equals("elevator"))
    		{
    			int resID = getResources().getIdentifier("elevator", "drawable", packagename);
    			bmp33 = BitmapFactory.decodeResource(getResources(), resID);
    	        bmp33 = Bitmap.createScaledBitmap(bmp33, 40, 40, true);
    		}
    		else
    		{
    			int resID = getResources().getIdentifier("mapmarker", "drawable", packagename);
    			bmp33 = BitmapFactory.decodeResource(getResources(), resID);
    	        bmp33 = Bitmap.createScaledBitmap(bmp33, 40, 40, true);
    		}
    		int posx = ii.getPosX();
    		int posy = ii.getPosY();
    		indoornav.placeIcon(bmp33, posx, posy);
    		
    	}
    	indoornav.refresh();
    }
    
    public void getLocation(View v)
    {
 	   favgwifimap = new HashMap<String, ArrayList<Float> >();
 	   maxavgwifi = 0.0f;
 	   minavgwifi = 1000.0f;
 	   medavgwifi = 0.0f;
 	   avgavgwifi = 0.0f;
 	   fwifimap = new HashMap<String, ArrayList<Float> >();
 	   maxwifi = 0.0f;
 	   minwifi = 1000.0f;
 	   medwifi = 0.0f;
 	   avgwifi = 0.0f;
 	   fingerprintmax = 7;
 	   fingerprintcount = 0;
 	   donotsave = true;
    }
    
    public void getLocation(boolean b)
    {
 	   favgwifimap = new HashMap<String, ArrayList<Float> >();
 	   maxavgwifi = 0.0f;
 	   minavgwifi = 1000.0f;
 	   medavgwifi = 0.0f;
 	   avgavgwifi = 0.0f;
 	   fwifimap = new HashMap<String, ArrayList<Float> >();
 	   maxwifi = 0.0f;
 	   minwifi = 1000.0f;
 	   medwifi = 0.0f;
 	   avgwifi = 0.0f;
 	   fingerprintmax = 7;
 	   fingerprintcount = 0;
 	   donotsave = true;
 	   changefloor = b;
    }
    
    
   public void getFingerprint(View v)
   {
	   
	   //Toast.makeText(this, "getting fingerprint", Toast.LENGTH_SHORT).show();
	    
	   favgwifimap = new HashMap<String, ArrayList<Float> >();
	   maxavgwifi = 0.0f;
	   minavgwifi = 1000.0f;
	   medavgwifi = 0.0f;
	   avgavgwifi = 0.0f;
	   fwifimap = new HashMap<String, ArrayList<Float> >();
	   maxwifi = 0.0f;
	   minwifi = 1000.0f;
	   medwifi = 0.0f;
	   avgwifi = 0.0f;
	   fingerprintcount = 0;
	   
   }
   
   public void getFingerprint(int[] bn)
   {
	   //Toast.makeText(this, "getting fingerprint", Toast.LENGTH_SHORT).show();
	   stoploop = true;
	   donotsave = false;
	   favgwifimap = new HashMap<String, ArrayList<Float> >();
	   maxavgwifi = -1000.0f;
	   minavgwifi = 1000.0f;
	   medavgwifi = 0.0f;
	   avgavgwifi = 0.0f;
	   fwifimap = new HashMap<String, ArrayList<Float> >();
	   maxwifi = -1000.0f;
	   minwifi = 1000.0f;
	   medwifi = 0.0f;
	   avgwifi = 0.0f;
	   fingerprintcount = 0;
	   currentposition = bn;
	   startposition = bn;
	   
   }
   
   public void getLocation2()
   {
	   setFingerprintText();
	   donotsave = false;
	   getLocation(null);
   }
   
   public void setFingerprintText()
   {
	   accesspoints = new ArrayList<String>();

	   
     ArrayList<Float[]> vv = new ArrayList<Float[]>();
     
     Iterator ii = favgwifimap.keySet().iterator();
     while (ii.hasNext())
     {
    	 String aa = (String)ii.next();
    	 accesspoints.add(aa);
     }
     String[] apoints = accesspoints.toArray(new String[] {});
	   
	   
	   String[] list = apoints;
	   String ff = "";
	   for (int j = 0; j < list.length; ++j)
	   {
		   if (enablelogging) Log.e("access point #",""+(j+1)+" "+list[j]);
		   vv.add(new Float[8]);
		   ArrayList<Float> ff1 = new ArrayList<Float>();
		   try {
	         if (favgwifimap.get(list[j]) != null) ff1 = favgwifimap.get(list[j]);
		   }
		   catch (Exception e) {
			   if (enablelogging) Log.e("exception","continue");
			   continue;}
	   float[] ff3 = new float[ff1.size()];
       for (int i = 0; i < ff1.size(); ++i)
       {
    	   float ff2 = ((Float)ff1.get(i)).floatValue();
    	   ff3[i] = ff2;
    	   if (ff2 > maxavgwifi) maxavgwifi = ff2;
    	   if (ff2 < minavgwifi) minavgwifi = ff2;
       }
       float sum = 0;
		for(int i=0; i < ff3.length ; i++)
			sum = sum + ff3[i];
		float average = sum / ff3.length;
       Arrays.sort(ff3);
       int len1 = ff1.size()/2;
       if (len1 >= ff3.length) {
    	   if (len1 >0) medavgwifi = ff3[len1-1];
    	   else
    	   {
    		   if (enablelogging) Log.e("nullmedavgwifi","");
    		   continue;
    	   }
       }
       else medavgwifi = ff3[len1];
       

    	   Float[] v1 = vv.get(j);
  	     v1[0] = minavgwifi; v1[1] = maxavgwifi; v1[2] = medavgwifi; v1[3] = average;
         vv.set(j, v1);
         if (enablelogging) Log.e("list1 "+list[j],v1[0]+" "+v1[1]+" "+v1[2]+" "+v1[3]);
     ff += list[j]+" avg "+minavgwifi+" "+maxavgwifi+" "+medavgwifi+" "+average+"\n";
       

	   ff1 = fwifimap.get(list[j]);
	   if (ff1 == null) {
		   if (enablelogging) Log.e("nullwifimap",list[j]);
		   continue;
	   }
	   ff3 = new float[ff1.size()];
       for (int i = 0; i < ff1.size(); ++i)
       {
    	   float ff2 = ((Float)ff1.get(i)).floatValue();
    	   ff3[i] = ff2;
    	   if (ff2 > maxwifi) maxwifi = ff2;
    	   if (ff2 < minwifi) minwifi = ff2;
       }
       sum = 0;
		for(int i=0; i < ff3.length ; i++)
			sum = sum + ff3[i];
		average = sum / ff3.length;
       Arrays.sort(ff3);
       len1 = ff1.size()/2;
       medwifi = ff3[len1];
       
       Float[] v2 = vv.get(j);
	     v2[4] = minwifi; v2[5] = maxwifi; v2[6] = medwifi; v2[7] = average;
     vv.set(j, v2);

       ff += list[j]+" nonavg "+minwifi+" "+maxwifi+" "+medwifi+" "+average+"\n";
       
       if (!donotsave) {
    	   String position = ""+currentposition[0]+" "+currentposition[1];
    	   gdb.add(position, apoints[j], v2);
       
    	   if (enablelogging) Log.e("addgrid",""+position+" "+apoints[j]+" "+v2[0]+" "+v2[1]);
       }
       
       if (donotsave)
       {
    	   if (changefloor)
    	   {
    		   findPosition2(indoorPlaceName, apoints[j], v2);
    		   changefloor = false;
    	   }
    	   else {
    		   if (!stoploop) findPosition(indoorPlaceName, apoints[j], v2);
    	   }
       }
	   
	   }
	   
	   if (!donotsave)
	   {
	   gdb.exportToServer();
	   stoploop = false;
	   donotsave = true;
	   getLocation(null);
	   }
	   
	   gdb.close();
       
       //mainText3.setText(ff);

   }
   
   public void findPosition(String buildingname, String apname, Float[] values)
   {
	   if (enablelogging) Log.e("findingposition",buildingname+" Floor "+currentfloor+" "+apname+" "+values[0]+" "+values[1]+" "+values[2]);
	   float distance = 10000000.0f;
	   String currposition = "";
	   ArrayList<String> positions = gdb.getPositions(buildingname+" Floor "+currentfloor);
	   if (enablelogging) Log.e(buildingname+" Floor "+currentfloor,"positions "+positions.size());
	   int apnum = gdb.getNumberOf(buildingname+" Floor "+currentfloor, apname);
	   if (apnum < 1) {
		   if (enablelogging) Log.e("apnamereturn",apname+" "+apnum);
		   return;
	   }
	   // saves the distance that is the shortest for each position
	   // in the database
	   // and the corresponding position
	   for (int i = 0; i < positions.size(); ++i)
	   {
		   String pos = positions.get(i);
		   float d1 = 0.0f;
		   // unique1
		   float dist1 = gdb.getDistance1(buildingname+" Floor "+currentfloor, apname, pos, values);
		   // unique2
		   float dist2 = gdb.getDistance2(buildingname+" Floor "+currentfloor, apname, pos, values);
		   // unique3
		   float dist3 = gdb.getDistance3(buildingname+" Floor "+currentfloor, apname, pos, values);
		   if (dist1 == 0.0f && dist2 == 0.0f && dist3 == 0.0f) continue;
		   if (enablelogging) Log.e("dist1",""+dist1);
		   if (enablelogging) Log.e("dist2",""+dist2);
		   if (enablelogging) Log.e("dist3",""+dist3);
		   d1 += dist1;
		   if (d1>distance) continue;
		   d1 += dist2;
		   if (d1>distance) continue;
		   d1 += dist3;
		   if (d1>distance) continue;
		   distance = d1;
		   currposition = pos;
	   }
	   if (enablelogging) Log.e("currentposition",currposition+" "+apname+" "+distance);
	   int ind = currposition.indexOf(" ");
	   if (ind != -1)
	   {
	   int xx = Integer.parseInt(currposition.substring(0, ind));
	   int yy = Integer.parseInt(currposition.substring(ind+1, currposition.length()));
	   indoornav.placePerson(xx, yy);
	   currentposition[0] = xx;
	   currentposition[1] = yy;
	   startposition = currentposition;
	   }
   }
   
   public void findPosition2(String buildingname, String apname, Float[] values)
   {
	   if (enablelogging) Log.e("findingposition",buildingname+" Floor "+currentfloor+" "+apname+" "+values[0]+" "+values[1]+" "+values[2]);
	   float distance = 10000000.0f;
	   String currposition = "";
	   String foundfloor = "";
	   ArrayList<String> positions = gdb.getPositions2(buildingname);
	   if (enablelogging) Log.e(buildingname,"positions "+positions.size());
	   int apnum = positions.size();
	   if (apnum < 1) {
		   if (enablelogging) Log.e("apnamereturn",apname+" "+apnum);
		   return;
	   }
	   // saves the distance that is the shortest for each position
	   // in the database
	   // and the corresponding position
	   for (int i = 0; i < positions.size(); ++i)
	   {
		   String pos = positions.get(i);
		   int ind = pos.lastIndexOf(" ");
		   String floor = pos.substring(ind+1, pos.length());
		   pos = pos.substring(0,ind);
		   float d1 = 0.0f;
		   // unique1
		   float dist1 = gdb.getDistance1(buildingname+" Floor "+floor, apname, pos, values);
		   // unique2
		   float dist2 = gdb.getDistance2(buildingname+" Floor "+floor, apname, pos, values);
		   // unique3
		   float dist3 = gdb.getDistance3(buildingname+" Floor "+floor, apname, pos, values);
		   if (dist1 == 0.0f && dist2 == 0.0f && dist3 == 0.0f) continue;
		   if (enablelogging) Log.e("dist1",""+dist1);
		   if (enablelogging) Log.e("dist2",""+dist2);
		   if (enablelogging) Log.e("dist3",""+dist3);
		   d1 += dist1;
		   if (d1>distance) continue;
		   d1 += dist2;
		   if (d1>distance) continue;
		   d1 += dist3;
		   if (d1>distance) continue;
		   distance = d1;
		   currposition = pos;
		   foundfloor = floor;
	   }
	   if (enablelogging) Log.e("currentposition",currposition+" "+apname+" "+distance);
	   int ind = currposition.indexOf(" ");
	   if (ind != -1)
	   {
	   int xx = Integer.parseInt(currposition.substring(0, ind));
	   int yy = Integer.parseInt(currposition.substring(ind+1, currposition.length()));
	   if (foundfloor != currentfloor)
	   {
	   currentfloor = foundfloor;
	   reload();
	   }
	   indoornav.placePerson(xx, yy);
	   currentposition[0] = xx;
	   currentposition[1] = yy;
	   startposition = currentposition;
	   }
   }
    
	@Override
	public void onStart() {
		super.onStart();
 	locManager.requestLocationUpdates(GPS, 0, 0, locListener);
		if (!locManager.isProviderEnabled(GPS)) {
			//Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_LONG).show();
			
	    }
		else {
			//start();
		}
		startStepService();
	}
	
	public void onStop() {
		super.onStop();
		locManager.removeUpdates(locListener);
		stopStepService();
		stop();
		
		
	}
	
	public void onResume() {
		super.onResume();
		
		if (wakelock == null || !wakelock.isHeld())
		{
		
		IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        
		registerReceiver(receiverWifi, filter );
		
		
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        
        mUtils.setSpeak(mSettings.getBoolean("speak", false));
        
        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();
        //Toast.makeText(this, ""+mIsRunning, Toast.LENGTH_LONG).show();
		
        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
        	//Toast.makeText(this, "startstepservice", Toast.LENGTH_SHORT).show();
        	  
        	
            bindStepService();
        }
        else if (mIsRunning) {
        	//Toast.makeText(this, "bindstepservice", Toast.LENGTH_SHORT).show();
          	
        	bindStepService();
        }
        
        
        mPedometerSettings.clearServiceRunning();

        //mStepValueView     = (TextView) findViewById(R.id.step_value);
        //mDistanceValueView = (TextView) findViewById(R.id.distance_value);

        mIsMetric = mPedometerSettings.isMetric();
        //((TextView) findViewById(R.id.distance_units)).setText(getString(
       //         mIsMetric
      //          ? R.string.kilometers
      //          : R.string.miles
      //  ));
        
        resetValues(true);
		}
		else
		{
			wakelock.release();
		}
        
        //gdb.open();
        //gdb.deleteAll(true);
        //gdb.create();
	}
	
	public void onPause() {
		super.onPause();
		
		PowerManager pm = (PowerManager)
				getSystemService(Context.POWER_SERVICE);
				boolean isScreenOn = pm.isScreenOn();
		if (isScreenOn)
		{
			try {
		unregisterReceiver(receiverWifi);
			}
			catch (Exception e) {}
		
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }
        
		}
		else
		{
			
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
			 wakelock.acquire();
			
			
		}
        
        //WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		//wifi.setWifiEnabled(false);

	}	
	
	
	   private StepService mService;
	    
	    private ServiceConnection mConnection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder service) {
	            mService = ((StepService.StepBinder)service).getService();
	            //Toast.makeText(getBaseContext(), "service connected", Toast.LENGTH_LONG).show();
	    		
	            mService.registerCallback(mCallback);
	            mService.reloadSettings();
	            
	        }

	        public void onServiceDisconnected(ComponentName className) {
	            mService = null;
	        }
	    };
	    
	    private void startStepService() {
	        if (! mIsRunning) {
	            mIsRunning = true;
	            startService(new Intent(IndoorLocate.this,
	                    StepService.class));
	            mIsRunning = true;
	        }
	    }
	   
	    
	    private void bindStepService() {
	    	if (enablelogging) Log.i(TAG, "[SERVICE] Bind");
	        boolean aa = bindService(new Intent(IndoorLocate.this, 
	                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	        //Toast.makeText(this, "bind "+aa, Toast.LENGTH_SHORT).show();
          	
	    }

	    private void unbindStepService() {
	    	if (enablelogging) Log.i(TAG, "[SERVICE] Unbind");
	        unbindService(mConnection);
	    }
	    
	    private void stopStepService() {
	    	if (enablelogging) Log.i(TAG, "[SERVICE] Stop");
	        if (mService != null) {
	        	if (enablelogging) Log.i(TAG, "[SERVICE] stopService");
	            stopService(new Intent(IndoorLocate.this,
	                  StepService.class));
	        }
	        mIsRunning = false;
	    }
	    
	    private void resetValues(boolean updateDisplay) {
	        if (mService != null && mIsRunning) {
	            mService.resetValues();                    
	        }
	        else {
	            //mStepValueView.setText("0");
	            //mDistanceValueView.setText("0");
	            SharedPreferences state = getSharedPreferences("state", 0);
	            SharedPreferences.Editor stateEditor = state.edit();
	            if (updateDisplay) {
	                stateEditor.putInt("steps", 0);
	                stateEditor.putFloat("distance", 0);
	                stateEditor.commit();
	            }
	        }
	    }

	    private static final int MENU_SETTINGS = 8;
	    private static final int MENU_QUIT     = 9;

	    private static final int MENU_PAUSE = 1;
	    private static final int MENU_RESUME = 2;
	    private static final int MENU_RESET = 3;
	    
	 
	    // TODO: unite all into 1 type of message
	    private StepService.ICallback mCallback = new StepService.ICallback() {
	        public void stepsChanged(int value) {
	            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
	        }
	        public void paceChanged(int value) {
	            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
	        }
	        public void distanceChanged(float value) {
	            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
	        }
	        public void speedChanged(float value) {
	            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
	        }
	        public void caloriesChanged(float value) {
	            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
	        }
	        public void compassChanged(float value)
	        {
	        	mHandler.sendMessage(mHandler.obtainMessage(COMPASS_MSG, (int)(value), 0));
	        }
	        public void accelChanged(float[] value)
	        {
	        	mHandler.sendMessage(mHandler.obtainMessage(ACCEL_MSG, 0, 0, value));
	        }
	        public void compassAccuracyChanged(int value)
	        {
	        	mHandler.sendMessage(mHandler.obtainMessage(COMPASS_ACCURACY_MSG, (value), 0));
	        }
	    };
	    
	    private static final int STEPS_MSG = 1;
	    private static final int PACE_MSG = 2;
	    private static final int DISTANCE_MSG = 3;
	    private static final int SPEED_MSG = 4;
	    private static final int CALORIES_MSG = 5;
	    private static final int COMPASS_MSG = 6;
	    private static final int COMPASS_ACCURACY_MSG = 7;
	    private static final int ACCEL_MSG = 8;
	    
	    private Handler mHandler = new Handler() {
	        @Override public void handleMessage(Message msg) {
	            switch (msg.what) {
                    case COMPASS_MSG:
                    	float c1 = CompassValue;
                    
                    	CompassValue = (int)msg.arg1;
                    //txtCompass.setText("Compass: " + CompassValue);
                    if (c1 != CompassValue) {
                    	if (enablelogging) Log.w("compass",""+CompassValue);
                    }
                    
                    break;
                    case ACCEL_MSG:
           
                    
                    	float[] accel = (float[])msg.obj;
                    //txtCompass.setText("Compass: " + CompassValue);
                    	if (enablelogging) Log.e("stepaccel",""+accel[0]+" "+accel[1]+" "+accel[2]);
                    diststep[0]+=accel[0];
                    diststep[1]+=accel[1];
                    float distance = (float)Math.sqrt(Math.pow(diststep[0],2)+Math.pow(diststep[1],2));
                    if (enablelogging) Log.e("combinedstep",""+diststep[0]+" "+diststep[1]+" "+distance);
                    if (distance > 1.2f)
                    {
                    	String direction = null;
                    	if (diststep[0] > 0.0f && diststep[1] > 0.0f)
                    	{
                    		direction = "SE";
                    		if (diststep[1] < 1.0f) direction = "E";
                    	}
                    	else if (diststep[0] < 0.0f && diststep[1] < 0.0f)
                    	{
                    		direction = "NW";
                    		if (diststep[1] > -1.0f) direction = "W";
                    	}
                    	else if (diststep[0] > 0.0f && diststep[1] < 0.0f)
                    	{
                    		direction = "NE";
                    		if (diststep[1] > -1.0f) direction = "N";
                    	}
                    	else if (diststep[0] < 0.0f && diststep[1] > 0.0f)
                    	{
                    		direction = "SW";
                    		if (diststep[0] > -1.0f) direction = "S";
                    	}
                    	
                    	if (direction != null)
                    		{
                    		if (enablelogging) Log.e("direction",direction+" "+distance);
		                    	if (direction.equals("N"))
		                    	{
		                    		currentposition[1] -= 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
		                    	else if (direction.equals("S"))
		                    	{
		                    		currentposition[1] += 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
		                    	else if (direction.equals("W"))
		                    	{
		                    		currentposition[0] -= 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
		                    	else if (direction.equals("E"))
		                    	{
		                    		currentposition[0] += 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
		                    	else if (direction.equals("NW"))
		                    	{
		                    		currentposition[0] -= 1;
		                    		currentposition[1] -= 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
		                    	else if (direction.equals("SW"))
		                    	{
		                    		currentposition[0] -= 1;
		                    		currentposition[1] += 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
		                    	else if (direction.equals("NE"))
		                    	{
		                    		currentposition[0] += 1;
		                    		currentposition[1] -= 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
		                    	else if (direction.equals("SE"))
		                    	{
		                    		currentposition[0] += 1;
		                    		currentposition[1] += 1;
		                    		indoornav.placePerson(currentposition[0],currentposition[1]);
		                    	}
                    		}
                    	diststep[0] = 0.0f;
                    	diststep[1] = 0.0f;

                    }
                    
                    
                    break;  
                    case COMPASS_ACCURACY_MSG:
                    CompassAccuracy = (int)msg.arg1;
                    
                    break;
	                case STEPS_MSG:
	                    mStepValue = (int)msg.arg1;
	                    //Log.e("step","detected");
	            		
	                    //moveStep(CompassValue);
	                    
	                    //mStepValueView.setText("" + mStepValue);
	                    break;
	                case PACE_MSG:

	                    break;
	                case DISTANCE_MSG:
	                    mDistanceValue = ((int)msg.arg1)/1000f;
	                    if (mDistanceValue <= 0) { 
	                        //mDistanceValueView.setText("0");
	                    }
	                    else {
	                       // mDistanceValueView.setText(
	                       //         ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
	                       // );
	                    }
	                    break;
	                case SPEED_MSG:

	                    break;
	                case CALORIES_MSG:

	                    break;
	                default:
	                    super.handleMessage(msg);
	            }
	        }
	        
	    };
	

	void start() {
		averaging = true;
		showAveragingUI();
		(new WorkerThread()).start();
	}
	
	private void showAveragingUI() {
	//	((TextView) findViewById(R.id.avglatlon_label))
	//			.setVisibility(View.VISIBLE);
	//	((TextView) findViewById(R.id.avglatlon)).setVisibility(View.VISIBLE);
		//((TextView) findViewById(R.id.avglatlonaccuracy))
		//		.setVisibility(View.VISIBLE);
		//((TextView) findViewById(R.id.avgalt)).setVisibility(View.VISIBLE);
	}

	void stop() {
		averaging = false;
		if (returnToDifferentApp) {
			Intent intent = new Intent();
			intent.putExtra("name", "Averaged Location");
			intent.putExtra("latitude", measurements.getLatitude());
			intent.putExtra("longitude", measurements.getLongitude());
			intent.putExtra("altitude", measurements.getAltitude());
			intent.putExtra("accuracy", (double)measurements.getAccuracy());
			setResult(RESULT_OK, intent);
			finish();
		}
		{

		}
	}

	private void showFinalButtons() {

	}
    
    public void sampleWifi()
    {
		Thread thread = new Thread(Scanner);
			//mainText.setText("Start Scanning...");
			//txtMyLocation.setText("My Lat & Long Scanning...");
	    	thread.start();	
    }
    
 	private Runnable Scanner = new Runnable() {
 		public void run() {
 			//for(int cnt=0; cnt<10; cnt++) {
 			while(true) {
				// sacnning WIFI and WPS Location
				mainWifi.startScan();
				
				try {
					Thread.sleep(SamplingIntval);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				sb = new StringBuilder();
				if (wifiList != null)
				for(int i = 0; i < wifiList.size(); i++){
					ScanResult scan = wifiList.get(i);
					//if(poiHash.get(scan.BSSID) != null)
					{
						
						try {
							
						
						if( i== 0 ) {
							sb.append(presentLoc + ", " + CompassValue + ", ");								 	
									 
						}
						
						sb.append(presentLoc + ", " + CompassValue + ", "			// loc, compass 
	            		   + wifiLocation[i][0] + ", " + wifiLocation[i][1]+ ", " 	// AP Lat, Long	               
	               		   + scan.SSID + ", " + scan.BSSID + ", " + scan.level 		// ssid, bssid, power
	               		   + ", " + scan.frequency + ", " + SamplingIntval + ", " 	// freq, interval
	               		   + wifiLocation[i][3] + ","  								// distance
	               		   
	               		   
	               		   + "\n"); 			// my Lat, Long
						}
						catch (Exception e) {}
					
					}	               	               
	             } 

			}
 		}
 	};
 	
 	public void setPositionChange(int[] a)
 	{

		  
		  currentposition[0] = a[2];
		  currentposition[1] = a[3];
		  indoornav.placePerson(a[2],a[3]);
		  startposition = new int[] {a[2],a[3]};
		
 	}
 	
 	class WifiReceiver extends BroadcastReceiver { 
        public void onReceive(Context c, Intent intent) { 


        	
        	if (!gdb.isOpen())
        	{
        		gdb.open();
        	}
        	//if (nowifi) return;
        	
           sb = new StringBuilder(); 
           sb2 = new StringBuilder(); 
           //Log.e("gettingscanresults","e");
           wifiList = mainWifi.getScanResults();
           String loc;
           String[] lat_lng;
           
           wificheckcount++;
           
           ArrayList<String> wificheck3 = new ArrayList<String>();
           for (int i = 0; i < wificheck2.size(); ++i)
           {
        	   wificheck3.add(wificheck2.get(i));
           }
           
           
           // check for duplicates
           ArrayList<String> names = new ArrayList<String>();
           for(int i = 0; i < wifiList.size(); i++)
           {    
        	   ScanResult scan = wifiList.get(i); 
             if (names.contains(scan.SSID))
             {
            	 if (enablelogging) Log.e("found duplicate ",scan.SSID);
            	 duplicates.add(scan.SSID);
             }
             else names.add(scan.SSID);
           }
           
           for(int i = 0; i < wifiList.size(); i++) { 
          	 
             ScanResult scan = wifiList.get(i);
             
             String identifier = scan.SSID;
             if (duplicates.contains(identifier))
             {
            	 identifier = scan.BSSID;
             }
             
             if (!wificheck2.contains(identifier)) wificheck2.add(identifier);
            
          	   int dist = (int) Utilities.calcDistance(scan.level);
          	
               if (fingerprintcount < fingerprintmax)
               {
            	   
            	   wificheck3.remove(identifier);
            	   if (!donotsave) {
            		   String position = ""+currentposition[0]+" "+currentposition[1];
                 	  
            		   gdb.addProb(position, identifier, true);
            	   }
               	
               if (!fwifimap.containsKey(identifier)) {
               	ArrayList<Float> bb = new ArrayList<Float>();
               	bb.add((float)scan.level);
               	fwifimap.put(identifier, bb);
               }
               else {
               	ArrayList<Float> bb = (ArrayList<Float>)fwifimap.get(identifier);
               	bb.add((float)scan.level);
               	fwifimap.put(identifier, bb);
               }
               
               }
          	   
               if (enablelogging) Log.d("Distance", "" + scan.level);
	   
          	   sb.append(scan.SSID+"\t"+scan.BSSID+"\t"+scan.level+"\t"+dist+"\n");
//          	   //sb.append(poiHash.get(scan.BSSID) +"\n\n");
//              }   
          	 if (!wifimap.containsKey(identifier))
          	 {
          		 ArrayList<Integer> aa = new ArrayList<Integer>();
          		 aa.add(new Integer(scan.level));
          		 wifimap.put(identifier, aa);
          	 }
          	 else
          	 {
          		 ArrayList<Integer> aa = wifimap.get(identifier);
          		 if (aa.size() < 5) aa.add(new Integer(scan.level));
          		 else {
          			 aa.remove(0);
          			 aa.add(new Integer(scan.level));
          		 }
          		 wifimap.put(identifier, aa);
          	 }
          	 
           
           }
           
           if (fingerprintcount < fingerprintmax)
           {
           for (int i = 0; i < wificheck3.size(); ++i)
           {
        	   String ssid = wificheck3.get(i);
        	   if (!donotsave) {
        		   String position = ""+currentposition[0]+" "+currentposition[1];
              	 
        		   gdb.addProb(position, ssid, false);
        	   
        	   }
           }
           }
           
           if (wificheckcount == 4)
           {
           wificheckcount = 0;
           wificheck2.clear();
           }
           
           calculateAvgWifi();
           
    		Iterator it = wifiavg.keySet().iterator();
    		sb2.append("Wifi Average:\n");
    		//Toast.makeText(getBaseContext(), ""+wifimap.size(), Toast.LENGTH_SHORT).show();
        	
    		ArrayList<String> removethis = new ArrayList<String>();
    		
     	    while (it.hasNext()) {
     	        String key = (String)it.next();
     	        
     	       Float aa = (Float)wifiavg.get(key);
     	        if (wificheckcount == 3)
     	        {
     	        	if (!wificheck2.contains(key))
     	        	{
     	        		removethis.add(key);
     	        	}
     	        	else
     	        	{
     	        		sb2.append(key+"\t"+aa.floatValue()+"\n");
     	        	}
     	        }
     	        else
     	        {
     	        	sb2.append(key+"\t"+aa.floatValue()+"\n");
     	        }
     	        
     	        
                
                
                if (fingerprintcount < fingerprintmax)
                {
                	
                if (!favgwifimap.containsKey(key)) {
                	ArrayList<Float> bb = new ArrayList<Float>();
                	bb.add(aa);
                	favgwifimap.put(key, bb);
                }
                else {
                	ArrayList<Float> bb = (ArrayList<Float>)favgwifimap.get(key);
                	bb.add(aa);
                	favgwifimap.put(key, bb);
                }
                
                }
                
     	    }
     	    
     	    if (wificheckcount == 3)
     	    {
     	    	for (int i = 0; i < removethis.size(); ++i)
     	    	{
     	    		wifiavg.remove((String)removethis.get(i));
     	    	}
     	    }
     	    
     	    if (fingerprintcount < 20) fingerprintcount++;
     	    
     	    if (fingerprintcount == fingerprintmax)
     	    {
     	    	if (!donotsave) setFingerprintText();
     	    	else
     	    	{
     	    		getLocation2();
     	    	}
     	    }
     	    
           //mainText2.setText(sb2);
                   //mainText.setText(sb);    
                 
        } 
   } 
 	
 	private void calculateAvgWifi()
 	{
 		Iterator it = wifimap.keySet().iterator();
 	    while (it.hasNext()) {
 	        String key = (String)it.next();
 	        ArrayList<Integer> aa = wifimap.get(key);
 	        if (aa.size() < 5) continue;
 	        Integer[] aa1 = aa.toArray(new Integer[] {});
            Arrays.sort(aa1);
            float result = 0.0f;
            int count = 1;
            result += aa1[0];
            int length = aa1.length;
            for (int i = length-1; i > 0; --i)
            {
            	if (aa1[i] < aa1[0].intValue()*1.5f)
            	{
            		result += aa1[i];
            		count++;
            	}
            }
            float avg = result/count;
            wifiavg.put(key, new Float(avg));
 	    }
 	}
    
    private final SensorEventListener sensorListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
			// TODO Auto-generated method stub
			//Toast.makeText(getBaseContext(), "accuracy "+accuracy, Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				CompassValue = event.values[0];
				txtCompass.setText("Compass: " + CompassValue);				
			}
		}    	
    };
    
    
	LocationListener locListener = new LocationListener() {

		public void onLocationChanged(Location loc) {
	}

		public void onStatusChanged(String arg0, int status, Bundle arg2) {
		}

		public void onProviderEnabled(String arg0) {
		}

		public void onProviderDisabled(String arg0) {
		stop();
		}
	};
	
	
	final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			final String latlon = msg.getData().getString(LATLON);
			final String latlonAccuracy = msg.getData().getString(
					LATLON_ACCURACY);
			final String alt = msg.getData().getString(ALT);

		//	((TextView) findViewById(R.id.avglatlon)).setText(latlon);
			//((TextView) findViewById(R.id.avglatlonaccuracy))
			//		.setText(latlonAccuracy);
			//((TextView) findViewById(R.id.avgalt)).setText(alt);
	}
	};
	
	/** Calculates distance between two gps lat/lon pairs */
	public static double distance(double lat1, double lng1, double lat2,
			double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;
		return (dist * meterConversion);
	}
	
	public ArrayList<String> getRoomList()
	{
		ArrayList<String> rooms = new ArrayList<String>();
		for (int i = 0; i < mylocations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = mylocations.get(i);
			String type = ii.getType();
			String name = ii.getPlaceName();
			int floor = ii.getFloor();
			if (enablelogging) Log.e("room1",type+" "+name+" "+floor);
			if (!indoorPlaceName.equals(name)) continue;
			if (!type.contains("room ")) continue;
			if (floor != (int)Integer.parseInt(currentfloor)) continue;
			int ind = type.indexOf(" ");
			String room = type.substring(ind+1, type.length());
			if (enablelogging) Log.e("room",room);
            rooms.add(room+spaces);
		}
		return rooms;
	}
	
	public ArrayList<String> getRoomList2()
	{
		ArrayList<String> rooms = new ArrayList<String>();
		for (int i = 0; i < mylocations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = mylocations.get(i);
			String type = ii.getType();
			String name = ii.getPlaceName();
			int floor = ii.getFloor();
			if (enablelogging) Log.e("room1",type+" "+name+" "+floor);
			if (!indoorPlaceName.equals(name)) continue;
			if (!type.contains("room ")) continue;
			int ind = type.indexOf(" ");
			String room = type.substring(ind+1, type.length());
			if (enablelogging) Log.e("room",room);
            rooms.add(room+spaces);
		}
		return rooms;
	}
	
	public int[] getRoomPosition(String name)
	{
		int posx = 0;
		int posy = 0;
		for (int i = 0; i < mylocations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = mylocations.get(i);
			String type = ii.getType();
			if (!type.contains("room ")) continue;
			int ind = type.indexOf(" ");
			String room = type.substring(ind+1, type.length());
			if (room.equals(name))
			{
				posx = ii.getPosX();
				posy = ii.getPosY();
			}
		}
		return new int[] {posx, posy};
	}
	
	public void drawPathOnFloor(String name)
	{
		finalpath = null;
		finalpathelev = null;
		finalpathfloor = -1;
		IonXml ix = new IonXml("http://scott-seto.com/appdoublescreen/"+name+".xml");
        ix.parseUrl();
        //Log.e("position", ""+position[0]+" "+position[1]);
        ClassInformation ci = ix.getClassInformation();

        Log.e("classinfo",ci.getClassName()+" "+ci.getStartTime()+" "+ci.getEndTime());
		TextView tx11 = (TextView)this.findViewById(R.id.textView9);
		tx11.setText(ci.getClassName()+"\n"+ci.getStartTime()+"\n"+ci.getEndTime());
		
		
		int[] position1 = getRoomPosition(name);
    	int floor = getRoomFloor(name);
    	if ((int)(Integer.parseInt(currentfloor)) != floor) changeFloorAndRoom(floor, position1);
    	else {
    		//Log.e("drawpath","curr "+currentposition[0]+" "+currentposition[1]);
    		//Log.e("drawpath","start "+startposition[0]+" "+startposition[1]);
    		
    		drawPixelBasedPath(getPixelNumber(startposition), position1);
    	}
	}
	
	   public int[] getPixelNumber(int[] a)
	   {
		   int[] c = new int[2];
		   c[0] = (int)(a[0]*50.0f+25.0f);
		   c[1] = (int)(a[1]*50.0f+25.0f);
	       return c;
	   }
	
	public void drawPathToRoom(final View target)
	{
		ArrayList<String> roomlist = new ArrayList<String>();
		roomlist = getRoomList();
		String[] roomlist1 = roomlist.toArray(new String[] {});
		roomdialog = new Dialog(IndoorLocate.this);
        roomdialog.setContentView(R.layout.roomdialog);
        roomdialog.setCancelable(true);
        
		ListView lv1=(ListView)roomdialog.findViewById(R.id.roomlistview1);
	     // By using setAdpater method in listview we an add string array in list.
	        lv1.setOnItemClickListener(new OnItemClickListener() {
	        	
	            @Override
	            public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
	            	
	            	String name = (String) arg0.getItemAtPosition(position);
	            	name = getName(name);
	            	int[] position1 = getRoomPosition(name);
	            	int floor = getRoomFloor(name);
	            	if ((int)(Integer.parseInt(currentfloor)) != floor) changeFloorAndRoom(floor, position1);
	            	else drawPixelBasedPath(startposition, position1);
	            	roomdialog.dismiss();
	            }
	            
	        });
	        lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , roomlist));
	    	
	        //set up button
	        Button button3 = (Button) roomdialog.findViewById(R.id.roomlayoutbutton);
	        button3.setOnClickListener(new OnClickListener() {
	        @Override
	            public void onClick(View v) {
	        	roomdialog.dismiss();
	            }
	        });
	        //now that the dialog is set up, it's time to show it    
	        roomdialog.show();
	}
	
	public String getName(String name)
	{
		int ind = name.indexOf(" ");
		return name.substring(0,ind);
	}
	
	public void drawPathToRoom2(final View target)
	{
		ArrayList<String> roomlist = new ArrayList<String>();
		roomlist = getRoomList();
		String[] roomlist1 = roomlist.toArray(new String[] {});
		roomdialog = new Dialog(IndoorLocate.this);
        roomdialog.setContentView(R.layout.roomdialog);
        roomdialog.setTitle("Select Room");
        roomdialog.setCancelable(true);
        
		ListView lv1=(ListView)roomdialog.findViewById(R.id.roomlistview1);
	     // By using setAdpater method in listview we an add string array in list.
	        lv1.setOnItemClickListener(new OnItemClickListener() {
	        	
	            @Override
	            public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
	            	
	            	String name = (String) arg0.getItemAtPosition(position);
	            	name = getName(name);
	            	int[] position1 = getRoomPosition(name);
	            	int floor = getRoomFloor(name);
	            	if ((int)(Integer.parseInt(currentfloor)) != floor) changeFloorAndRoom(floor, position1);
	            	else centerAtPosition(position1);
	            	roomdialog.dismiss();
	            }
	            
	        });
	        lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , roomlist));
	    	
	        //set up button
	        Button button3 = (Button) roomdialog.findViewById(R.id.roomlayoutbutton);
	        button3.setOnClickListener(new OnClickListener() {
	        @Override
	            public void onClick(View v) {
	        	roomdialog.dismiss();
	            }
	        });
	        //now that the dialog is set up, it's time to show it    
	        roomdialog.show();
	}
	
	public void goOutside(final View target)
	{
		finish();
	}
	
	public void goToRoom(final View target)
	{
		ArrayList<String> roomlist = new ArrayList<String>();
		roomlist = getRoomList2();
		String[] roomlist1 = roomlist.toArray(new String[] {});
		roomdialog = new Dialog(IndoorLocate.this);
        roomdialog.setContentView(R.layout.roomdialog);
        roomdialog.setTitle("Select Room");
        roomdialog.setCancelable(true);
        
		ListView lv1=(ListView)roomdialog.findViewById(R.id.roomlistview1);
	     // By using setAdpater method in listview we an add string array in list.
	        lv1.setOnItemClickListener(new OnItemClickListener() {
	        	
	            @Override
	            public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
	            	
	            	String name = (String) arg0.getItemAtPosition(position);
	            	name = getName(name);
	            	if (enablelogging) Log.e("scott", "drawing path to room "+name);
	            	IonXml ix = new IonXml("http://scott-seto.com/appdoublescreen/"+name+".xml");
			         ix.parseUrl();
			         //Log.e("position", ""+position[0]+" "+position[1]);
			         ClassInformation ci = ix.getClassInformation();

			         
	            	
	            	
	            	int[] position1 = getRoomPosition(name);
	            	int floor = getRoomFloor(name);
	            	
	            	if (enablelogging) Log.e("classinfo",ci.getClassName()+" "+ci.getStartTime()+" "+ci.getEndTime());
			         TextView tx11 = (TextView)((IndoorLocate)v.getContext()).findViewById(R.id.textView9);
			 		tx11.setText(indoorPlaceName+"\nFloor "+currentfloor+"\n\n"+ci.getClassName()+"\n"+ci.getStartTime()+"\n"+ci.getEndTime()+"\nFloor "+floor);
			 		
			 		
			 		if (enablelogging) Log.e("positionandfloor",""+position1[0]+" "+position1[1]+" "+floor+" "+name);
	            	if (floor == (int)Integer.parseInt(currentfloor)) {
	            		if (enablelogging) Log.e("drawpathonfloor","");
	            		drawPathOnFloor(name);
	            	}
	            	else {
	            		if (enablelogging) Log.e("drawtoelevator","");
	            	finalpath = position1;
	            	finalpathfloor = floor;
	            	drawPathToElevator();
	            	}
	            	roomdialog.dismiss();
	            }
	            
	        });
	        lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , roomlist1));
	    	
	        //set up button
	        Button button3 = (Button) roomdialog.findViewById(R.id.roomlayoutbutton);
	        button3.setOnClickListener(new OnClickListener() {
	        @Override
	            public void onClick(View v) {
	        	roomdialog.dismiss();
	            }
	        });
	        //now that the dialog is set up, it's time to show it    
	        roomdialog.show();
	}
	
	public void drawPathToElevator()
	{
		finalpathelev = elevatorposition;
		drawPixelBasedPath(getPixelNumber(startposition), elevatorposition);
	}
	
	public int getRoomFloor(String name)
	{
		int floor = 1;
		for (int i = 0; i < mylocations.size(); ++i)
		{
			odu.cs.ion.database.Location ii = mylocations.get(i);
			String type = ii.getType();
			
			if (type.contains(name) && type.contains("room "))
			{
				floor = ii.getFloor();
				break;
			}
			
		}
		return floor;
	}
	
	public String[] getFloorList()
	{
		ArrayList<String> floorlist = new ArrayList<String>();
		for (int i = 0; i < mymaps.size(); ++i)
		{
			odu.cs.ion.database.Map ii = mymaps.get(i);
			String name1 = ii.getName();
			if (name1.equals(indoorPlaceName))
			{
				if (enablelogging) Log.e("floorlist",""+ii.getFloor());
				floorlist.add(""+ii.getFloor()+spaces);
			}
			
		}		
		return floorlist.toArray(new String[] {});
	}
	
	public void changeFloor(final View target) {
		
		
		if (enablelogging) Log.e("gettingfloorlist","e");
		String[] floorlist = getFloorList();
		
		if (enablelogging) Log.e("changingfloor","e");
		floordialog = new Dialog(IndoorLocate.this);
        floordialog.setContentView(R.layout.floordialog);
        floordialog.setTitle("Choose Floor");
        floordialog.setCancelable(true);
        
		ListView lv1=(ListView)floordialog.findViewById(R.id.floorlistview1);
	     // By using setAdpater method in listview we an add string array in list.
	        lv1.setOnItemClickListener(new OnItemClickListener() {
	        	
	            @Override
	            public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
	            	
	            	String name = (String) arg0.getItemAtPosition(position);
	            	name = getName(name);
	            	currentfloor = name;
	            	
	            	if (finalpathfloor != (int)Integer.parseInt(currentfloor))
	            	{
	            	finalpath = null;
	        		finalpathelev = null;
	        		finalpathfloor = -1;
	            	}
	        		
	            	reload();
	            	//Log.e("finalpath",""+finalpath);
	            	//Log.e("finalpathelev",""+finalpathelev);
	            	if (finalpath != null && finalpathelev != null)
	            	{
	            		drawPixelBasedPath(elevatorposition, finalpath);
	            	}
	            	floordialog.dismiss();
	            
	            }
	        });
	        lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , floorlist));
	
	        //set up button
	        Button button3 = (Button) floordialog.findViewById(R.id.floorlayoutbutton);
	        button3.setOnClickListener(new OnClickListener() {
	        @Override
	            public void onClick(View v) {
	        	floordialog.dismiss();
	            }
	        });
	        //now that the dialog is set up, it's time to show it    
	        floordialog.show();
	        
	}
	
	public void changeFloorAndRoom(int floor, int[] position)
	{
		currentfloor = ""+floor;
    	reload();
    	centerAtPosition(position);
	}
	
	public void centerAtPosition(int[] position)
	{
		indoornav.centerAtPosition(position);
	}

	class WorkerThread extends Thread {

		@Override
		public void run() {
			measurements.clean();
			int start = 0;
			while (averaging) {
				start++;
				// the averaging algorithm - weightening by horizontal accuracy
				// increasing counters
				Location ll = locManager.getLastKnownLocation(GPS);
				if (ll != null) {
				measurements.add(ll);

				// sending results
				Message msg = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putString(LATLON,
						exporter.formatGPS(measurements.getLocation()));
				b.putString(LATLON_ACCURACY,
						exporter.formatAccuracy(measurements.getLocation()));
				b.putString(ALT,
						exporter.formatAltitude(measurements.getAltitude()));
				msg.setData(b);
				handler.sendMessage(msg);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {

				}
				
				if (start == 10)
				{
					measurements.removeOne();
					start = 0;
				}
				
				}
				else
				{
					try {
						Thread.sleep(2000);
					} catch (InterruptedException ex) {

					}
				}
			}
		}
	}
}