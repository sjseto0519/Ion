package odu.cs.ion;

// Java APIs
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;

// Android APIs
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

// Google-APIs
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.google.android.maps.Overlay;

// DualScreen APIs
import com.kyocera.dualscreen.DualScreen;

// Customized Overlays
import edu.odu.ads.VANHEECKHOET.outdoorNav.LocationInfo;
import edu.odu.ads.VANHEECKHOET.outdoorNav.LocationInfoView;
import edu.odu.ads.VANHEECKHOET.outdoorNav.LocationItem;
import edu.odu.ads.VANHEECKHOET.outdoorNav.MyOverlay;
import edu.odu.ads.VANHEECKHOET.outdoorNav.Overlays;
import edu.odu.ads.VANHEECKHOET.outdoorNav.MyGpsService;
import edu.odu.ads.VANHEECKHOET.outdoorNav.SurroundingPlacesDb;

// Road 
import org.ci.geo.route.Road;
import org.ci.geo.route.RoadProvider;
import org.ci.geo.route.MapOverlay;

// Database
import odu.cs.ion.IonMenu;
import odu.cs.ion.database.PlacesDbAdapter;
import odu.cs.ion.database.OutdoorLocation;
import odu.cs.ion.external.ExternalServer;
import odu.cs.ion.indoor.IndoorLocate;


/** ACTIVTY **/

public class DualScreenApiSampleActivity extends MapActivity implements OnClickListener {  

    private static final String INTENT_ACTION_SLIDE = "com.kyocera.intent.action.SLIDE_OPEN";
    CustomReceiver mReceiver;

    // Log TAG
    String TAG = "DualScreenApiSampleActivity";
    
    // External Data
    private static final String CURRENLAT = "currentlat";
    private static final String CURRENLON = "currentlon";
    private static final String CURRENTFLOOR = "currentfloor";
    private static final String STARTLAT = "startlat";
    private static final String STARTLON = "startlon";
    private static final String STARTFLOOR = "startfloor";
    private static final String DESTINATIONLAT = "destinationlat";
    private static final String DESTINATIONLON = "destinationlon";
    private static final String DESTINATIONFLOOR = "destinationfloor";
    private static final String DESTINATIONNAME = "destinationname";
    private static final String IDSELECTEDINTHELIST = "idSelectedInTheList";
    private static final String IDSELECTEDFORDESTINATION = "idselectedfordestination";
    private static final String CURRENTZOOMLEVEL = "currentzoomlevel";

    public static boolean enablelogging = false;
    
    double currentlat = 0;			// current latitude
    double currentlon = 0;			// current longitude
    int currentfloor = 1;			// current floor (US way)
    double startlat = 0;			// start latitude
    double startlon = 0;			// start longitude
    int startfloor = 1;				// start floor (US way)
    double destinationlat = 0;		// destination latitude
    double destinationlon = 0;		// destination longitude
    int destinationfloor = 1;		// destination floor (US way)
    String destinationName = "";	// name of the destination building
    int currentzoomlevel = 15;			// currentzoomlevel
    
    public WifiManager wifiManager;
    
    // Framework 
    private static final String CURRENT_CONTENT = "currentContent";
    LinearLayout layoutContent1;
    LinearLayout layoutContent2;
    LinearLayout layoutContent3;
    LinearLayout layoutContent4;
    LinearLayout layoutContent5;
    LinearLayout layoutMenuBar;        
    Button menuButton1;
    Button menuButton2;
    Button menuButton3;
    Button menuButton4;
    Button menuButton5;    
    int CurrentContent = 1;
    
    MapView mapView;
    MapController mapController;
    Drawable marker;
    
    // GPS
    Intent  intentMyService;
    ComponentName service;
    BroadcastReceiver receiver;
    String GPS_FILTER= "cs495.action.GPS_LOCATION";
    
    // Map 
    Road mRoad;
    double StartLat = 36.885472;
    double StartLon = -76.305147;
    double DestinationLat = 36.850694;
    double DestinationLon = -76.285797;
    LocationManager lm;
    Location ll;
    Geocoder gc;
    
    
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
                //TextView textView = (TextView) findViewById(R.id.description);
                //textView.setText(mRoad.mName + " " + mRoad.mDescription);
                MapOverlay mapOverlay = new MapOverlay(mRoad, mapView);
                List<Overlay> listOfOverlays = mapView.getOverlays(); 
                listOfOverlays.clear();
                addClosetsLocationsInOverlay(closestlocations);
                addClosetsLocationsInOverlayByType(foodlocations, "food");
        	    addClosetsLocationsInOverlayByType(schoollocations, "school");
        	    addClosetsLocationsInOverlayByType(universitylocations, "university");
        	    addClosetsLocationsInOverlayByType(pharmacylocations, "pharmacy");
        	    addClosetsLocationsInOverlayByType(librarylocations, "library");
                listOfOverlays.add(myLocationOverlay);
                listOfOverlays.add(mapOverlay);
                mapView.invalidate(); 
        };
    };

    private InputStream getConnection(String url) {
        InputStream is = null;
        try {
                URLConnection conn = new URL(url).openConnection();
                is = conn.getInputStream();
        } catch (MalformedURLException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return is;
	}    
    
    // OutdoorLocate
    
    private WebView webview;
	private ProgressDialog progressBar;
	private DualScreen dualscreen;
	public Location mostRecentLocation;
	public double latitude = 36.469;
	public double longitude = -76.045;
	ArrayList<OutdoorLocation> closestlocations;
    
    /** OutdoorNav Variables **/
    
    private final String ItemInfo = "itemInfo";
    
    outdoorItemOverlay myoutdoorItemizedOverlay = null;
    
    public List<LocationItem> LocationItems = new ArrayList<LocationItem>();	// Array of location Items
    itemOverlay myItemizedOverlay = null;										// Overlay for the locations
    placesOverlay foodOverlay = null;												// Overlay for the food
    placesOverlay libraryOverlay = null;													// Overlay for the libraries
    placesOverlay pharmacyOverlay = null;											// Overlay for the pharmacies
    placesOverlay schoolOverlay = null;											// Overlay for the schools
    placesOverlay universityOverlay = null;										// Overlay for the university buildings
    MyLocationOverlay myLocationOverlay = null;									// current location overlay
	String Last_Sub_Overlay_File = "";											// File that contains the data for a new overlay
	LinearLayout listItems;														// Linear Layout that contains the textViews of the locations
	
	public Drawable databaseicon;
	public Drawable universityicon;
	public Drawable schoolicon;
	public Drawable foodicon;
	public Drawable libraryicon;
	public Drawable pharmacyicon;
	
	public ArrayList<OutdoorLocation> myoutdoorlocations;
	public ArrayList<OutdoorLocation> foodlocations;
	public ArrayList<OutdoorLocation> librarylocations;
	public ArrayList<OutdoorLocation> pharmacylocations;
	public ArrayList<OutdoorLocation> schoollocations;
	public ArrayList<OutdoorLocation> universitylocations;
	
	ScrollView outdoorlocationscroll;											// Linear Layout that contains the ScrollView for outdoorlocationslinear
	LinearLayout outdoorlocationslinear;										// Linear Layout that contains the TextViews of the outdoor locations
	
	
	private long lastTouchTimeDown = -1;
	private long lastTouchTimeUp = -1;
	private Intent intentLocationInfo;
	private AlertDialog.Builder dialogLocationInfoBuilder;
	private AlertDialog dialogLocationInfo;
	private OnTouchListener ontouchlistener;
	private Intent locationInfo;
	
	/** Database Variables **/	

	public PlacesDbAdapter pda;
	public boolean onEmulator = false;
	public boolean onDualScreen = true;	
	public int idSelectedInTheList = -1;
	public int idselectedfordestination = -1;
	public SurroundingPlacesDb spdb;
	
	/** Listeners **/
	
	OnClickListener outdoorClickListener = new OnClickListener () {
        public void onClick(View v) {
        	selectItemInList(v, myoutdoorlocations);	            	
         }
	};
	OnClickListener foodClickListener = new OnClickListener () {
        public void onClick(View v) {
        	selectItemInList(v, foodlocations);	            	
         }	
	};
	OnClickListener libraryClickListener = new OnClickListener () {
        public void onClick(View v) {
        	selectItemInList(v, librarylocations);	            	
         }	
	};
	OnClickListener pharmacyClickListener = new OnClickListener () {
        public void onClick(View v) {
        	selectItemInList(v, pharmacylocations);	            	
         }	
	};
	OnClickListener schoolClickListener = new OnClickListener () {
        public void onClick(View v) {
        	selectItemInList(v, schoollocations);	            	
         }	
	};
	OnClickListener universityClickListener = new OnClickListener () {
        public void onClick(View v) {
        	selectItemInList(v, universitylocations);	            	
         }	
	};
        
    
    /** FUNCTIONS **/
	
	public void initialize()
	{
	  onEmulator = true;
	  onDualScreen = true;
	  if (enablelogging) Log.e("scott","getplaces");
	  try {
		  pda = new PlacesDbAdapter();
		  pda.open();
		  pda.deleteAll(true);
		  pda.create();
	  } catch(Exception e)
	  {
		  if (enablelogging) Log.d(TAG, "error : oncreate, pda : " + e.toString());
		  pda.close();
	  }
	  if (enablelogging) Log.e("scott","getsurroundingplaces");
	  try {
		  spdb = new SurroundingPlacesDb();
		  spdb.open();
		  spdb.create();
		  spdb.close();
	  } catch(Exception e)
	  {
		  if (enablelogging) Log.d(TAG, "error : oncreate, spdb : " + e.toString());
		  spdb.close();
	  }
	  if (enablelogging) Log.e("scott","external server");
	 	

	  try {
		  ExternalServer es = new ExternalServer(wifiManager);
		  String insert = es.getPlacesSql();
		  if (enablelogging) Log.e("insert", ""+insert);
		  pda.execute(insert);
		  fillOutdoorLocations();
		  pda.close();	  
	  } catch(Exception e)
	  {
		  if (enablelogging) Log.d(TAG, "error : oncreate, pda (2) : " + e.toString());
		  pda.close();
	  }
	  if (enablelogging) Log.e("scott","myoutdoorlocations size "+myoutdoorlocations.size());
	  // DEBUG
	  int i;
	  for (i=0; i<myoutdoorlocations.size(); i++)
	  {
		  if (enablelogging) Log.d(TAG, "myoutdoorlocations[" + i + "] = " + myoutdoorlocations.get(i).getName() + ", " + myoutdoorlocations.get(i).getLatitude() + ", " + myoutdoorlocations.get(i).getLongitude() + ", " + myoutdoorlocations.get(i).getMarker() + ", " + myoutdoorlocations.get(i).getId());
	  }
	  // END DEBUG
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        /** External Data **/
        // 
        // We get the Data from the previous activity
        //
        
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        
        try {
	        Intent thisintent = getIntent();
	        currentlat = thisintent.getExtras().getDouble(CURRENLAT);
	        currentlon = thisintent.getExtras().getDouble(CURRENLON);
	        currentfloor = thisintent.getExtras().getInt(CURRENTFLOOR);
	        startlat = thisintent.getExtras().getDouble(STARTLAT);
	        startlon = thisintent.getExtras().getDouble(STARTLON);
	        startfloor = thisintent.getExtras().getInt(STARTFLOOR);
	        destinationlat = thisintent.getExtras().getDouble(DESTINATIONLAT);
	        destinationlon = thisintent.getExtras().getDouble(DESTINATIONLON);
	        destinationfloor = thisintent.getExtras().getInt(DESTINATIONFLOOR);
        }
        catch (Exception e)
        {
        	if (enablelogging) Log.e(TAG, "error : onCreate, first phase : " + e.getMessage());
        	currentlat = 36.885472;
            currentlon = -76.305147;
            currentfloor = 0;
	        startlat = currentlat;
	        startlon = currentlon;
	        startfloor = currentfloor;
	        destinationlat = currentlat;
	        destinationlon = destinationlon;
	        destinationfloor = currentfloor;
        }        
        
        /** Database
         * Initialization of the database
         */
        initialize();       
        
        // DEBUG
        currentlat = 36.885472;
        currentlon = -76.305147;
        // END DEBUG
        
        /** Load previous data **/
        //
        //
        //
        
        CurrentContent = getPreferences(MODE_PRIVATE).getInt(CURRENT_CONTENT, 1);
        currentlat = getPreferences(MODE_PRIVATE).getFloat(CURRENLAT, (float) 36.885472);
        currentlon = getPreferences(MODE_PRIVATE).getFloat(CURRENLON, (float) -76.305147);
        startlat = getPreferences(MODE_PRIVATE).getFloat(STARTLAT, (float) currentlat);
        startlon = getPreferences(MODE_PRIVATE).getFloat(STARTLON, (float) currentlon);
        destinationlat = getPreferences(MODE_PRIVATE).getFloat(DESTINATIONLAT, (float) currentlat);
        destinationlon = getPreferences(MODE_PRIVATE).getFloat(DESTINATIONLON, (float) currentlon);
        destinationName = getPreferences(MODE_PRIVATE).getString(DESTINATIONNAME, "");
        idSelectedInTheList = getPreferences(MODE_PRIVATE).getInt(IDSELECTEDINTHELIST, -1);
        idselectedfordestination = getPreferences(MODE_PRIVATE).getInt(IDSELECTEDFORDESTINATION, -1);
        currentzoomlevel = getPreferences(MODE_PRIVATE).getInt(CURRENTZOOMLEVEL, 15);
        
        try
        {
	        pda.open();
	        //destinationName = pda.getDestination();
	        pda.close();
        }
        catch (Exception e)
        {
        	if (enablelogging) Log.e(TAG, "No databse for the destination");
        	pda.close();
        }
        
        /** Framework **/
        // 
        // The Framework is initialized
        //
        
        DualScreen.restrictOrientationAtFullScreen( this, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setScreenLayout();
              
        /** DualScreen Receiver **/
        // 
        // The special receiver of the DualScreen for "slide" is initialized 
        //
        
        mReceiver = new CustomReceiver();
        IntentFilter slideFilter = new IntentFilter(INTENT_ACTION_SLIDE);
        registerReceiver(mReceiver, slideFilter);
        
        /** OutdoorNav **/
        //
        // Initialize the navigation components
        //
        
	    marker=getResources().getDrawable(android.R.drawable.star_big_on);
	    int markerWidth = marker.getIntrinsicWidth();
	    int markerHeight = marker.getIntrinsicHeight();
	    marker.setBounds(0, markerHeight, markerWidth, 0);
	    
	    databaseicon = getResources().getDrawable(android.R.drawable.btn_star_big_on);
		universityicon = getResources().getDrawable(R.drawable.icon_university);
		schoolicon = getResources().getDrawable(android.R.drawable.ic_dialog_info);
		foodicon = getResources().getDrawable(R.drawable.icon_food);
		libraryicon = getResources().getDrawable(R.drawable.icon_library);
		pharmacyicon = getResources().getDrawable(R.drawable.icon_pharmacy);

	    //
	    // Extract the data from a file in the raw resources folder and fill the LoacationItems Array of LocationItem
	    //
	    
	    //ExtractDataFromRes(R.raw.items);	
	    
	    //
	    // Initialize the overlay "myItemizedOverlay" with the LocationItems array content
	    // It creates the corresponding markers
	    //
	    
	    /*
	    try {	    	
	    	myItemizedOverlay = new itemOverlay(marker, LocationItems);
	    } catch (Exception e) {
	    	Log.e(TAG, "error : onCreate, myItemizedOverlay : " + e.toString());
	    }
	    */
	    
	    //
	    // Insert the "myItemizedOverlay" in the mapView
	    //
	    /*
	    //mapView.getOverlays().add(myItemizedOverlay);
	    mapView.getOverlays().add(myoutdoorItemizedOverlay);
	    mapView.postInvalidate();
	    */
	    //
	    // Insert the TextViews corresponding to the LocationItems array content in the linear layout listItems
	    //
	    
	    
	    listItems = (LinearLayout) findViewById(R.id.listItems);


	    /** SURROUNDING LOCATIONS **/
	    //
	    //	Download and display the locations around the device
	    //	    
	    
	    closestlocations = getClosestLocations(1000);
    	
	    spdb.open();
	    spdb.create();

	    try {
		    //if(spdb.isDbEmpty() == true)
		    //{
		    	
	    		//spdb.deleteAll(true);
			    spdb.saveSurroundingPlacesByType(getClosestLocationsByType(new String("food"), 1000), "food");
			    spdb.saveSurroundingPlacesByType(getClosestLocationsByType(new String("school"), 1000), "school");
			    spdb.saveSurroundingPlacesByType(getClosestLocationsByType(new String("university"), 1000), "university");
			    spdb.saveSurroundingPlacesByType(getClosestLocationsByType(new String("pharmacy"), 1000), "pharmacy");
			    spdb.saveSurroundingPlacesByType(getClosestLocationsByType(new String("library"), 1000), "library");
		    	
		    //}	    
	    }
    	catch (Exception e)
    	{
    		if (enablelogging) Log.d(TAG, "error : onCreate, isDbEmpty or saveSurroundingPlacesByType" + e.toString());
    		spdb.close();
    	}
	    
	    foodlocations = spdb.getSurroundingPlacesByType("food");   
	    schoollocations = spdb.getSurroundingPlacesByType("school"); 
	    universitylocations = spdb.getSurroundingPlacesByType("university"); 
	    pharmacylocations = spdb.getSurroundingPlacesByType("pharmacy"); 
	    librarylocations = spdb.getSurroundingPlacesByType("library"); 
	    
	    spdb.close();
	    
	    /*
        // DEBUG
  	  	int i;
  	  	for (i=0; i<closestlocations.size(); i++)
  	  	{
  	  		Log.d(TAG, "closestlocations[" + i + "] = " + closestlocations.get(i).getName() + ", " + closestlocations.get(i).getLatitude() + ", " + closestlocations.get(i).getLongitude() + ", " + closestlocations.get(i).getMarker() + ", " + closestlocations.get(i).getId());
  	  	}
  	  	// END DEBUG 
  	  	
  	  	// DEBUG
  	  	for (i=0; i<foodlocations.size(); i++)
  	  	{
  	  		Log.d(TAG, "foodlocations[" + i + "] = " + foodlocations.get(i).getName() + ", " + foodlocations.get(i).getLatitude() + ", " + foodlocations.get(i).getLongitude() + ", " + foodlocations.get(i).getMarker() + ", " + foodlocations.get(i).getId());
  	  	}
  	  	for (i=0; i<schoollocations.size(); i++)
	  	{
	  		Log.d(TAG, "schoollocations[" + i + "] = " + schoollocations.get(i).getName() + ", " + schoollocations.get(i).getLatitude() + ", " + schoollocations.get(i).getLongitude() + ", " + schoollocations.get(i).getMarker() + ", " + schoollocations.get(i).getId());
	  	}
  	  	for (i=0; i<universitylocations.size(); i++)
	  	{
	  		Log.d(TAG, "universitylocations[" + i + "] = " + universitylocations.get(i).getName() + ", " + universitylocations.get(i).getLatitude() + ", " + universitylocations.get(i).getLongitude() + ", " + universitylocations.get(i).getMarker() + ", " + universitylocations.get(i).getId());
	  	}
  	  	for (i=0; i<pharmacylocations.size(); i++)
	  	{
	  		Log.d(TAG, "pharmacylocations[" + i + "] = " + pharmacylocations.get(i).getName() + ", " + pharmacylocations.get(i).getLatitude() + ", " + pharmacylocations.get(i).getLongitude() + ", " + pharmacylocations.get(i).getMarker() + ", " + pharmacylocations.get(i).getId());
	  	}
  	  	for (i=0; i<librarylocations.size(); i++)
	  	{
	  		Log.d(TAG, "librarylocations[" + i + "] = " + librarylocations.get(i).getName() + ", " + librarylocations.get(i).getLatitude() + ", " + librarylocations.get(i).getLongitude() + ", " + librarylocations.get(i).getMarker() + ", " + librarylocations.get(i).getId());
	  	}
  	  	*/
  	  	// END DEBUG 
  	  	
	    
	    addClosetsLocationsInOverlayByType(foodlocations, "food");
	    addClosetsLocationsInOverlayByType(schoollocations, "school");
	    addClosetsLocationsInOverlayByType(universitylocations, "university");
	    addClosetsLocationsInOverlayByType(pharmacylocations, "pharmacy");
	    addClosetsLocationsInOverlayByType(librarylocations, "library");
	    addClosetsLocationsInOverlay(closestlocations);
	    
	    fillTheList();
	    
	    //
	    // Insert the "myLocationOverlay" in the mapView
	    //
	    
	    myLocationOverlay = new MyLocationOverlay(this, mapView);
	    mapView.getOverlays().add(myLocationOverlay);
	    mapView.postInvalidate();
	    
	    
	    /** GPS Navigation **/
	    //
	    // Initialize the GPS navigation
	    //
	    
	    //
	    // launch the GeoCoder
	    //
	    
	    gc = new Geocoder(this);		
	    
	    //
	    // Launch the GPS Service
	    //
	    
	    /*
	    Log.d(TAG, "Before startService");
	    intentMyService= new Intent(this, MyGpsService.class);  
	    service= startService(intentMyService);
	    Log.d(TAG, "After startService");
	    
	    //
	    // register & define filter for local listener
	    //
	    
	    IntentFilter mainFilter= new IntentFilter(GPS_FILTER);
	    receiver= new MyMainLocalReceiver();
	    registerReceiver(receiver, mainFilter); 
	    
	    //
	    // Get the places around the current position	    
	    //
	    */
	    //String urlStringDirections = new String("http://scott-seto.com/ion/getplaces.html?lat=" + currentlat + "&lng=" + currentlon + "&type=");
	    
	    
	    //
	    // GPS
	    //
	     
	    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    new Thread(new Runnable() {
	         public void run() {
		         try{
		        	 if (enablelogging) Log.d(TAG, "before the looper");
		        	 Looper.prepare();
		        	 Object object;
		        	 
		        	 object = getSystemService(Context.LOCATION_SERVICE);		        	 
		        	 
		        	 if (object != null)
		        	 {
		        		 String provider = LocationManager.GPS_PROVIDER;		        	
		        		 ll = lm.getLastKnownLocation(provider);
		        		 setCurrentLocation(ll.getLatitude(), ll.getLongitude(), 0);
		        		 if (enablelogging) Log.d(TAG, ">>GPS_Service<< : Lat:" + ll.getLatitude() + " lon:" + ll.getLongitude());
		        		 if (enablelogging) Log.d(TAG, "before the looper 2");
		        		 //updateDirections();
		        	 }
		        	 
		             Looper.loop();
		         } catch(Exception e) {
		        	 //Log.e(TAG, e.getMessage() );
		         }
		         if (enablelogging) Log.d(TAG, "after the try");
	         }// run
	       }).start();
	    
	    
	    //showPathandDirections(false);
	    mapController.setZoom(currentzoomlevel);	    
		setVisibleContent(CurrentContent);
		
	    //
	    // END OF onCreate
	    //		
    }
    
    @Override
    public void onDestroy() {       
        super.onDestroy();
       
        //
        // Stop the GPS Service and unregister the receiver
        //
        /*
        try{
            stopService(intentMyService);
            unregisterReceiver(receiver);
        } catch(Exception e) {
            Log.e("MAIN-DESTROY>>>", e.getMessage() );
        }
        */
        //
        // Unregister intent receiver.
        //
        
        unregisterReceiver(mReceiver);
    }
    
    //
    // Control the on click events
    //
    public void onClick(View v) {
        
    	switch (v.getId()) 
    	{
	    	case R.id.MenuButton1:	    		
	    		//this.onPause();
	    		//this.onStop();
	    		//this.onDestroy();
	    		//setVisibleContent(1);		// Display the Tab 1
	    		break;	    	
	    	case R.id.MenuButton2:	    		
	    		setVisibleContent(2);		// Display the Tab 2
	    		break;	    		
	    	case R.id.MenuButton3:	    		
	    		setVisibleContent(3);		// Display the Tab 3
	    		showPathandDirections(true);
	    		break;	    		
	    	case R.id.MenuButton4:	 
	    		gotoLocation(currentlat, currentlon, 16);
	    		//setVisibleContent(4);		// Display the Tab 4
	    		break;	    		
	    	case R.id.MenuButton5:	    		
	    		setVisibleContent(5);		// Display the Tab 5
	    		break;	    		
	    	default :    			
    	}	        
    	
    }
	
    //
    // Display the chosen Tab and hide the others
    //
    private void setVisibleContent(int content_number)
    {
    	
    	switch (content_number) 
    	{
	    	case 1:
	    		layoutContent1.setVisibility(View.VISIBLE);
	    		layoutContent2.setVisibility(View.GONE);
	    		layoutContent3.setVisibility(View.GONE);
	    		layoutContent4.setVisibility(View.GONE);
	    		setVisibilityMap(layoutContent5, View.GONE);
	    		CurrentContent = 1;
	    		break;
	    		
	    	case 2:
	    		layoutContent1.setVisibility(View.GONE);
	    		layoutContent2.setVisibility(View.VISIBLE);
	    		layoutContent3.setVisibility(View.GONE);
	    		layoutContent4.setVisibility(View.GONE);
	    		setVisibilityMap(layoutContent5, View.GONE);
	    		CurrentContent = 2;
	    		break;
	    		
	    	case 3:
	    		layoutContent1.setVisibility(View.GONE);
	    		layoutContent2.setVisibility(View.GONE);
	    		layoutContent3.setVisibility(View.VISIBLE);
	    		layoutContent4.setVisibility(View.GONE);
	    		setVisibilityMap(layoutContent5, View.GONE);
	    		CurrentContent = 3;
	    		break;
	    		
	    	case 4:
	    		layoutContent1.setVisibility(View.GONE);
	    		layoutContent2.setVisibility(View.GONE);
	    		layoutContent3.setVisibility(View.GONE);
	    		layoutContent4.setVisibility(View.VISIBLE);
	    		setVisibilityMap(layoutContent5, View.GONE);
	    		CurrentContent = 4;
	    		break;
	    		
	    	case 5:
	    		layoutContent1.setVisibility(View.GONE);
	    		layoutContent2.setVisibility(View.GONE);
	    		layoutContent3.setVisibility(View.GONE);
	    		layoutContent4.setVisibility(View.GONE);
	    		setVisibilityMap(layoutContent5, View.VISIBLE);
	    		CurrentContent = 5;
	    		break;  	
	    		
	    	default :
    	}
    }
    
    //
    // Special case for the map with the DualScreen mode
    // Display the Map if there is only one available screen
    //
    private void setVisibilityMap(LinearLayout linear, int visibility)
    {
    	DualScreen dualscreen = new DualScreen(getApplicationContext());
        int screen_mode = dualscreen.getScreenMode();
        
        if( screen_mode != DualScreen.FULL ) 
        {
        	linear.setVisibility(visibility);
        }
        else
        {      
        }
    }
    
    //
    // Adapt the Screen Layout in function of the phone mode :
    // - One screen or Double screen
    // - Portrait or Landscape
    //
    private void setScreenLayout() {
        
    	//
    	// Dual Screen Characteristics
    	//
        DualScreen dualscreen = new DualScreen(getApplicationContext());
        int screen_mode = dualscreen.getScreenMode();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        //
        // Choose the right ContentView
        //
		
        try {
        
        if( screen_mode == DualScreen.FULL ) {
            //
        	// for full screen
            //
        	if( width > height ) {
                setContentView(R.layout.fullscreen_landscape);
                             
            } else {
                setContentView(R.layout.fullscreen_portrait);
                
                //
                // Primary and secondary screen heights calculation.
                // (Full screen-portrait mode only)                 
                //
                
                View layout_primary = findViewById(R.id.LayoutPrimaryScreen);
                View layout_secondary = findViewById(R.id.LayoutSecondaryScreen);     
                
                //
                // Initialization of the Layout parameters for the two screens
                //
                
                LayoutParams layoutparams_primary = layout_primary.getLayoutParams();
                LayoutParams layoutparams_secondary = layout_secondary.getLayoutParams();

                //
                // get notification bar height
                //
                
                Drawable phone_call_icon = getResources().getDrawable(android.R.drawable.stat_sys_phone_call);
                int height_notification = phone_call_icon.getIntrinsicHeight();
                
                //
                // calculate heights
                //
                layoutparams_primary.height = height / 2 - height_notification;
                layoutparams_secondary.height = height / 2;
                
                layout_primary.setLayoutParams(layoutparams_primary);
                layout_secondary.setLayoutParams(layoutparams_secondary);                
            }

        } else {
        	//
            // for normal screen
            //
        	
        	if( width > height ) {
                setContentView(R.layout.normalscreen_landscape);
            } else {
                setContentView(R.layout.normalscreen_portrait);
            }
            
        	//
            // Initialize Special Framework for one Screen only mode
            //
        	layoutContent5 =  (LinearLayout)findViewById(R.id.LayoutContent5);            
            menuButton5 = (Button)findViewById(R.id.MenuButton5);
            findViewById(R.id.MenuButton5).setOnClickListener(this);
            
        }
      
        }
        catch (Exception e)
        {
        	if (enablelogging) Log.e(TAG, "error : onCreate, Screens : " + e.toString());
        }
        
        try 
        {
        //
        // Initialize Framework
        //
        
        layoutContent1 =  (LinearLayout)findViewById(R.id.LayoutContent1);
        layoutContent2 =  (LinearLayout)findViewById(R.id.LayoutContent2);
        layoutContent3 =  (LinearLayout)findViewById(R.id.LayoutContent3);
        layoutContent4 =  (LinearLayout)findViewById(R.id.LayoutContent4);
        layoutMenuBar = (LinearLayout)findViewById(R.id.LayoutMenuBar);
        menuButton1 = (Button)findViewById(R.id.MenuButton1);
        menuButton2 = (Button)findViewById(R.id.MenuButton2);
        menuButton3 = (Button)findViewById(R.id.MenuButton3);
        menuButton4 = (Button)findViewById(R.id.MenuButton4);
        findViewById(R.id.MenuButton1).setOnClickListener(this);
        findViewById(R.id.MenuButton2).setOnClickListener(this);
        findViewById(R.id.MenuButton3).setOnClickListener(this);
        findViewById(R.id.MenuButton4).setOnClickListener(this);
        
        }
        catch (Exception e)
        {
        	if (enablelogging) Log.e(TAG, "error : onCreate, Initialize Framework : " + e.toString());
        }
        
        try
        {
        
        //
        // TEMP
        //
        /*
        outdoorlocationslinear = new LinearLayout(this);
        outdoorlocationscroll = new ScrollView(this);
        TextView temptextview = new TextView(this);
        temptextview.setText("HELLO !");        	
        outdoorlocationscroll.addView(outdoorlocationslinear);
        layoutContent1.addView(outdoorlocationscroll);	
        outdoorlocationslinear.addView(temptextview);
        */
        }
        catch (Exception e)
        {
        	if (enablelogging) Log.e(TAG, "error : onCreate, outdoorlocationscroll : " + e.toString());
        }
        
        try
        {
        
        //
        // Initialize Framework State    
        //
        
        // This (These) button(s) is (are) not useful
        //menuButton1.setVisibility(View.GONE);
        //menuButton4.setVisibility(View.GONE);
        
        CurrentContent = getPreferences(MODE_PRIVATE).getInt(CURRENT_CONTENT, 1);
        setVisibleContent(CurrentContent);
        
        //
		// Initialize mapView 
        //
        
        mapView = (MapView)findViewById(R.id.mapview);		
        GeoPoint point = new GeoPoint (new Integer((int) Math.round(currentlat * 1000000)),new Integer((int) Math.round(currentlon * 1000000))); // Miami City 
        mapController = mapView.getController(); 
        mapController.animateTo(point); 
        mapController.setZoom(16);         
                
        }
        catch (Exception e)
        {
        	if (enablelogging) Log.e(TAG, "error : onCreate, mapView : " + e.toString());
        }
        
    }

    @Override
	protected void onResume() {	  
	  super.onResume();

	  /** OutdoorNav **/	  
	  //
	  // Initialize the mapView and its Overlays
	  //
	  
	  myLocationOverlay.enableMyLocation();
	  myLocationOverlay.enableCompass();	  
	  mapView.postInvalidate();
	}
	
    @Override
	protected void onStop() {
    	

    	super.onStop();
    	
    }
    
    
	@Override
	protected void onPause() {
	  
	  //
	  // DualScreen specificity
	  //
		
      currentzoomlevel = mapView.getZoomLevel();
		
	  getPreferences(MODE_PRIVATE).edit().putInt(CURRENT_CONTENT, CurrentContent).commit();
	  getPreferences(MODE_PRIVATE).edit().putFloat(CURRENLAT, (float) currentlat).commit();
	  getPreferences(MODE_PRIVATE).edit().putFloat(CURRENLON, (float) currentlon).commit();
	  getPreferences(MODE_PRIVATE).edit().putFloat(STARTLAT, (float) startlat).commit();
	  getPreferences(MODE_PRIVATE).edit().putFloat(STARTLON, (float) startlon).commit();
	  getPreferences(MODE_PRIVATE).edit().putFloat(DESTINATIONLAT, (float) destinationlat).commit();
	  getPreferences(MODE_PRIVATE).edit().putFloat(DESTINATIONLON, (float) destinationlon).commit();
	  getPreferences(MODE_PRIVATE).edit().putString(DESTINATIONNAME, destinationName).commit();
	  getPreferences(MODE_PRIVATE).edit().putInt(IDSELECTEDINTHELIST, idSelectedInTheList).commit();
	  getPreferences(MODE_PRIVATE).edit().putInt(IDSELECTEDFORDESTINATION, idselectedfordestination).commit();
	  getPreferences(MODE_PRIVATE).edit().putInt(CURRENTZOOMLEVEL, currentzoomlevel).commit();
	  
	  /** OutdoorNav **/
	  //
	  // Disable the mapView and its Overlays
	  //
	  
	  myLocationOverlay.disableMyLocation();
	  myLocationOverlay.disableCompass();
	  
	  super.onPause();
	}
    
    // -------------------------------------------------------------
    //  Broadcast intent custom receiver for DualScreen
    // -------------------------------------------------------------
    class CustomReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            
        	String action = intent.getAction();
            if ( action.equals(INTENT_ACTION_SLIDE)) {
                boolean slideOpen = intent.getBooleanExtra("OPEN",false);
                if(slideOpen) {
                    Toast.makeText(DualScreenApiSampleActivity.this, R.string.msg_slide_opened, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DualScreenApiSampleActivity.this, R.string.msg_slide_closed, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Define if the Route is displayed
	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}
	
	/** OutdoorNav Functions **/
	
	//
	//go to the location with the given coordinates and the given zoom
	//
	private void gotoLocation (double lat, double lng, int zoom)
	{
		mapController.animateTo(new GeoPoint((int) (lat * 1000000.0), (int) (lng * 1000000.0)));
		
		try 
		{
			//Log.d(TAG, "Zoom Level == " + mapView.getZoomLevel());
			while (mapView.getZoomLevel() < zoom)
			{	
				mapController.zoomIn();
				//Log.d(TAG, "Zoom Level after zoomIn() == " + mapView.getZoomLevel());
			}
			
			while (mapView.getZoomLevel() > zoom)
			{	
				mapController.zoomOut();
				//Log.d(TAG, "Zoom Level after zoomOut() == " + mapView.getZoomLevel());
			}
		} 
		catch (Exception e)
		{
			if (enablelogging) Log.e(TAG, "error : gotoLocation : " + e.toString());
		}
	}
	
	//
	// Add a textView in the list of locations currently available
	// Add also extra information in LayoutContent4
    //	
	public boolean addItemTextViews (LinearLayout linear){
		TextView textItem;
		//Button buttonItem;
		OnClickListener ClickListener;
		int i;
		
		for(i=0; i<LocationItems.size(); i++){
			textItem = new TextView(this);
		    textItem.setText(LocationItems.get(i).getFullName() + "\n" + "________________________________________");
		    textItem.setPadding(13, 2, 0, 0);
		    textItem.setTextSize(16);

		    ClickListener = new OnClickListener () {
	            public void onClick(View v) {
	            	LocationItem v2 = null;
	            	int k;
	            	int j = 0;
	            	
	            	//Log.d(TAG, "On click, view = " + v.getId());
	        		
	        		try
	        		{
	        			if (listItems.getChildCount() > 0)
	        			{
	        				while ( (j < listItems.getChildCount()) && (v != listItems.getChildAt(j)) )
	        				{
	        					
	        					j++;
	        				}
	        				
	        				for(k=0;k<listItems.getChildCount();k++)
	        				{
	        					listItems.getChildAt(k).setBackgroundColor(0xffffffff);
	        				}
	        				listItems.getChildAt(j).setBackgroundColor(0xff0000ff);
	        				
	        				v2 = LocationItems.get(j);
	        				gotoLocation(v2.getLat(),v2.getLng(),v2.getZoom());
	        				
	        				//
	        				// Update the LayoutContent 4 with information about the chosen location
	        				//
	        				addMoreContent(layoutContent4, LocationItems.get(j));
	        				
	        				//
	        				// Update destination
	        				//
	        				
	        				setDestination(LocationItems.get(j).getLat(),LocationItems.get(j).getLng(),0,LocationItems.get(j).getName());
	        				//DestinationLat = LocationItems.get(j).getLat();
	        				//DestinationLon = LocationItems.get(j).getLng();
	        				
	        			}	        			
	        		}
	        		catch (Exception e){
	        			if (enablelogging) Log.e(TAG, "error : addItemTextViews, onClickListener : " + e.getMessage());
	        		}	        		
	        		//setVisibleContent(5);
	             }
	        };
	        
	        // Add the new TextView in the LinearLayout listItems
	        //
	        textItem.setOnClickListener(ClickListener);
	        linear.addView(textItem);
		}
		return true;
	}
	
	// Set the currentLocation 
	//
	public boolean setCurrentLocation (double lat, double lon, int floor)
	{
		currentlat = lat;
		currentlon = lon;
		currentfloor = floor;
		return true;
	}
	
	// Set the startLocation 
	//
	public boolean setStartLocation (double lat, double lon, int floor)
	{
		startlat = lat;
		startlon = lon;
		startfloor = floor;
		return true;
	}
	
	// Set the destination 
	//
	public boolean setDestination (double lat, double lon, int floor, String name)
	{
		destinationlat = lat;
		destinationlon = lon;
		destinationfloor = floor;	
		destinationName = name;
		return true;
	}
	
	// Display the GPS position
	//
	public boolean displayGpsPosition(LinearLayout linear, String str){
	
		TextView textItem = new TextView(this);
	    textItem.setText(str);
	    textItem.setPadding(13, 2, 0, 0);
	    textItem.setTextSize(16);	    
	    linear.addView(textItem);
	    return true;
	}
	
	// Add more information about the chosen location in the layoutcontent4
	//
	public boolean addMoreContent (LinearLayout linear, LocationItem item){
			
			ScrollView scroll = new ScrollView(this);
			LinearLayout content = new LinearLayout(this);
			TextView title_text = new TextView(this);
			TextView snippet_text = new TextView(this);
			TextView group_text = new TextView(this);
			ImageView image = new ImageView(this);
			TextView description_text = new TextView(this);
			TextView website_text = new TextView(this);
			
			title_text.setText(item.getName());
			snippet_text.setText(item.getSnippet());
			group_text.setText("Region : " + item.getGroup());
			description_text.setText("Description : " + item.getDescription());
			website_text.setText("Website : " + item.getWebsite());
			setImage(image, item.getImage());
			
			content.setOrientation(LinearLayout.VERTICAL);
			
			content.addView(title_text);
			content.addView(snippet_text);
			content.addView(group_text);
			content.addView(image);
			content.addView(description_text);
			content.addView(website_text);
			
			scroll.addView(content);
			
			linear.removeAllViews();
			linear.addView(scroll);

			return true;
		}
		
		// Modify the ImageView with the given URL
		//
		public void setImage(ImageView view, String url) {
			Bitmap bitmap = null;
			
			try {
				URL urlImage = new URL(url);	
				HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();	
				InputStream inputStream = connection.getInputStream();	
				bitmap = BitmapFactory.decodeStream(inputStream);	
				view.setImageBitmap(bitmap);
			}
			catch (Exception e) {				
				if (enablelogging) Log.e(TAG, "error : setImage :  " + e.toString());
			}	 
			
			view.setImageBitmap(bitmap);
		}
	
	// Extract data from a file and create the items for the LocationItems list
	//
	public boolean ExtractDataFromRes(int res) {
		LocationItem newItem;
		
		if (enablelogging) Log.d(TAG, "Start Extract data from resource (file) " + res);
		
		Resources myResources = getResources();
		if (enablelogging) Log.d(TAG, "Resources acquired" );
		InputStream myFile = myResources.openRawResource(res); // get the required resource
		
		String line;
		try {
			InputStreamReader ipsr = new InputStreamReader(myFile);
			BufferedReader br = new BufferedReader(ipsr);
			while ((line = br.readLine()) != null) {
				newItem = new LocationItem();
				LocationItem.FillItem(newItem, line);	
				LocationItems.add(newItem);
			}
			br.close();
			} catch (Exception e) {
				if (enablelogging) Log.e(TAG, "error : ExtractDataFromRes : " + e.toString());
			}
		return false;
	}

	// Add linear in linearContent defined by i and destroy the previous views
	//
	public void changeContent(LinearLayout linear, int i)
	{
		switch (i) 
    	{
	    	case 1:	  
	    		break;	    	
	    	case 2:	    	    		
	    		break;	    		
	    	case 3:
	    		break;	    		
	    	case 4:
	    		layoutContent4.addView(linear);
	    		break;	    		
	    	case 5:
	    		break;	    		
	    	default :    			
    	}	      
	}
	
	// Add linear in linearContent defined by i and destroy the previous views
	//
	public void changeDirectionsContent(String str, int remove)
	{
		//TextView newTextItem = new TextView(this);
		WebView webview = new WebView(this);
		
		if (remove == 0)
		{
			layoutContent3.removeAllViews();			
		}
		else
		{
			//newTextItem.setText(str);
			//layoutContent3.addView(newTextItem);
			webview.getSettings().setJavaScriptEnabled(true);
 	       	webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
 	        webview.loadUrl(str);
 	        layoutContent3.addView(webview);
			
		}
	}
	
	/** Database Functions **/
	
	/**
	 * Fill the databse with the outdoor locations 
	 **/
	public void fillOutdoorLocations()
	{
		myoutdoorlocations = pda.getOutdoorLocations();
		//Log.e("locations", ""+myoutdoorlocations.size());
	}
	
	/**
	 * Return a ArrayList of the surronding locations
	 **/
	public ArrayList<OutdoorLocation> getClosestLocations(double radius)
	{	
		ArrayList<OutdoorLocation> closestlist = new ArrayList<OutdoorLocation>();
		int i;
		for (i=0; i<myoutdoorlocations.size(); i++)
		{
			//if (getdistance(currentlat, currentlon, myoutdoorlocations.get(i).getLatitude(),  myoutdoorlocations.get(i).getLongitude()) < radius)	
			//{
				OutdoorLocation newitem = new OutdoorLocation(myoutdoorlocations.get(i).getName(), myoutdoorlocations.get(i).getLatitude(), myoutdoorlocations.get(i).getLongitude(), myoutdoorlocations.get(i).getMarker(), myoutdoorlocations.get(i).getId());
				closestlist.add(newitem);
			//}			
		}		
		return closestlist;
	}
	
	/**
	 * Return a ArrayList of the surronding locations
	 **/
	public ArrayList<OutdoorLocation> getClosestLocationsByType(String type, double radius)
	{	
		ArrayList<OutdoorLocation> closestlist = new ArrayList<OutdoorLocation>();
		int i;
		
		try
		{
			//currentlat = 37.79199;
			//currentlon = -76.13101;		
			String firsturl = "http://scott-seto.com/ion/getplaces.html?lat=" + currentlat + "&lng= " + currentlon + "&type=" + type;
			URL urldestination = new URL(firsturl);
			
			
			changeDirectionsContent("", 0);
			changeDirectionsContent(firsturl, 1);
			
			if (enablelogging) Log.d(TAG, "fisrt URL : " + firsturl);
			
			HttpURLConnection connection = (HttpURLConnection) urldestination.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write("");
			wr.flush();
		
			changeDirectionsContent("", 0);
			//changeDirectionsContent(urlStringDirections, 1);
			//changeDirectionsContent(finalurl, 1);
		}
		catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), 1).show();
		}
		
		try
		{
			
		}
		catch (Exception e)
		{
			if (enablelogging) Log.d(TAG, "error : getClosestLocationsByType, wait : " + e.toString());
		}
		
		 try {
				Thread.sleep(1500);
			} catch (InterruptedException e1) {
			}
		
		try {
			String finalurl =new String("http://scott-seto.com/ion/places" + currentlat + ".htm");
			if (enablelogging) Log.d(TAG, "final URL : " + finalurl);
			
			URL urlDirections = new URL(finalurl);	
			HttpURLConnection connection = (HttpURLConnection) urlDirections.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(1000);
			
			connection.connect();
			
			InputStream inputStream = connection.getInputStream();	
			
			try {
				InputStreamReader ipsr = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(ipsr);
				String line;
				int nblines;
				OutdoorLocation item;
				String itemname = "";
				float itemlat = 0;
				float itemlon = 0;
				String itemmarker = type;
				int itemid = getRefIdFromType(type);	
				
				//changeDirectionsContent("", 0);
				nblines = 0;
				while ((line = br.readLine()) != null) {
					if (nblines == 3)
					{
						item = new OutdoorLocation(itemname, itemlat, itemlon, itemmarker, itemid);
						closestlist.add(item);
						itemid++;
						nblines = 0;
					}
					if (nblines == 0)
					{
						itemname = new String(line);
					}
					if (nblines == 1)
					{
						itemlat = Float.valueOf(line);
					}
					if (nblines == 2)
					{
						itemlon = Float.valueOf(line);
					}
					nblines++;					
				}
				br.close();
			} catch (Exception e1) {
				System.out.println(e1.toString());
			}
			
			//changeDirectionsContent(finalurl, 1);
		}
		catch (Exception e1) {				
			e1.printStackTrace();
		}	 
		
		
		
		/*
		for (i=0; i<myoutdoorlocations.size(); i++)
		{
			if (getdistance(currentlat, currentlon, myoutdoorlocations.get(i).getLatitude(),  myoutdoorlocations.get(i).getLongitude()) < radius)	
			{
				OutdoorLocation newitem = new OutdoorLocation(myoutdoorlocations.get(i).getName(), myoutdoorlocations.get(i).getLatitude(), myoutdoorlocations.get(i).getLongitude(), myoutdoorlocations.get(i).getMarker(), myoutdoorlocations.get(i).getId());
				closestlist.add(newitem);
			}			
		}
		*/
		
		return closestlist;
		
	}
	
	
	public int getRefIdFromType(String type)
	{
		if (type.equalsIgnoreCase("university"))
		{
			return 100000;
		}
		if (type.equalsIgnoreCase("food"))
		{
			return 200000;
		}
		if (type.equalsIgnoreCase("school"))
		{
			return 300000;
		}
		if (type.equalsIgnoreCase("pharmacy"))
		{
			return 400000;
		}
		if (type.equalsIgnoreCase("library"))
		{
			return 500000;
		}
		return 999999;
	}
	
	/**
	 * Return a ArrayList of the surronding locations
	 **/
	public void addClosetsLocationsInList (ArrayList<OutdoorLocation> closestlist, ArrayList<OutdoorLocation> list)
	{
		int i;
		for (i=0; i<closestlist.size(); i++)
		{
			ImageView itemicon = null;
			itemicon = createLocationListItemIcon(closestlist.get(i), list);
			
			TextView listitem = null;
			listitem = createLocationListItem(closestlist.get(i), list);
			listItems.addView(itemicon);
			listItems.addView(listitem);
			//Log.d(TAG, "new item '" + closestlist.get(i).getName() + "' added in listItems");
		}	
	}
	
	/**
	 * Return a ArrayList of the surronding locations
	 **/
	public void addClosetsLocationsInOverlay (ArrayList<OutdoorLocation> closestlist)
	{
		 try {	    	
		    myoutdoorItemizedOverlay = new outdoorItemOverlay(marker, closestlist);
		    
		    //
		    // Insert the "myItemizedOverlay" in the mapView
		    //	    
		    //mapView.getOverlays().clear();
		    mapView.getOverlays().add(myoutdoorItemizedOverlay);
		    mapView.postInvalidate();
		 } 
		 catch (Exception e) 
		 {
			 if (enablelogging) Log.e(TAG, "error : addClosetsLocationsInOverlay : " + e.toString());
		 }
	}
	
	/**
	 * Return a ArrayList of the surronding locations
	 **/
	public void addClosetsLocationsInOverlayByType (ArrayList<OutdoorLocation> closestlist, String type)
	{
		try 
		{
			Drawable markerforthetype;
			
			if (type.equalsIgnoreCase("university"))
			{
				markerforthetype = getResources().getDrawable(R.drawable.icon_university);
				universityOverlay = new placesOverlay(markerforthetype, closestlist);
				mapView.getOverlays().add(universityOverlay);
			}
			if (type.equalsIgnoreCase("food"))
			{
				markerforthetype = getResources().getDrawable(R.drawable.icon_food);
				foodOverlay = new placesOverlay(markerforthetype, closestlist);
				mapView.getOverlays().add(foodOverlay);
			}
			if (type.equalsIgnoreCase("school"))
			{
				markerforthetype = getResources().getDrawable(android.R.drawable.ic_dialog_info);
				schoolOverlay = new placesOverlay(markerforthetype, closestlist);
				mapView.getOverlays().add(schoolOverlay);
			}
			if (type.equalsIgnoreCase("pharmacy"))
			{
				markerforthetype = getResources().getDrawable(R.drawable.icon_pharmacy);
				pharmacyOverlay = new placesOverlay(markerforthetype, closestlist);
				mapView.getOverlays().add(pharmacyOverlay);
			}
			if (type.equalsIgnoreCase("library"))
			{
				markerforthetype = getResources().getDrawable(R.drawable.icon_library);
				libraryOverlay = new placesOverlay(markerforthetype, closestlist);
				mapView.getOverlays().add(libraryOverlay);
			}
		
		    mapView.postInvalidate();
		} 
		catch (Exception e) 
		{
			if (enablelogging) Log.e(TAG, "error : addClosetsLocationsInOverlay : " + e.toString());
		}
	}
	
	/**
	 * Create new TextView for the new location
	 */
	
	public ImageView createLocationListItemIcon(OutdoorLocation item, ArrayList<OutdoorLocation> list)
	{
		
		ImageView IconItem = new ImageView(this);		
		IconItem.setTag(new Integer(item.getId()));
 
	    if (list == myoutdoorlocations) { IconItem.setOnClickListener(outdoorClickListener); IconItem.setBackgroundDrawable(databaseicon);}
	    else {
	    	if (list == universitylocations) { IconItem.setOnClickListener(universityClickListener); IconItem.setBackgroundDrawable(universityicon);}
		    else {
		    	if (list == pharmacylocations) { IconItem.setOnClickListener(pharmacyClickListener); IconItem.setBackgroundDrawable(pharmacyicon);}
			    else {
			    	if (list == schoollocations) { IconItem.setOnClickListener(schoolClickListener); IconItem.setBackgroundDrawable(schoolicon); }
				    else {
				    	if (list == foodlocations) { IconItem.setOnClickListener(foodClickListener); IconItem.setBackgroundDrawable(foodicon);}
					    else {
					    	if (list == librarylocations) { IconItem.setOnClickListener(universityClickListener); IconItem.setBackgroundDrawable(libraryicon);}
						    else {
						    	IconItem.setOnClickListener(outdoorClickListener);  // default
						    	IconItem.setBackgroundDrawable(databaseicon);		//default
						    	if (enablelogging) Log.e(TAG, "error : createLocationListItemIcon : no clicklistener found");
						    }	
					    }	
				    }	
			    }	
		    }	
	    }	   	   
	    
	    IconItem.setAdjustViewBounds(true);
	    IconItem.setMaxHeight(20);
	    IconItem.setMaxWidth(20);
		LayoutParams l = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		IconItem.setLayoutParams(l);
	    return IconItem;		
	}
	
	
	/**
	 * Create new TextView for the new location
	 */
	
	public TextView createLocationListItem(OutdoorLocation item, ArrayList<OutdoorLocation> list)
	{
		LinearLayout linear = new LinearLayout(this);
		TextView textItem = new TextView(this);
		textItem.setText(item.getName() + ", " + "\n" + Math.round(getdistance(currentlat, currentlon, item.getLatitude(),  item.getLongitude())) + "m");// + "\n" + "________________________________________");
	    textItem.setPadding(13, 2, 0, 0);
	    textItem.setTextSize(16);
	    textItem.setTag(new Integer(item.getId()));
	    
    
	    if (idSelectedInTheList == item.getId())
	    {
	    	textItem.setBackgroundColor(0xff0000ff);
	    	textItem.setTextColor(0xffffffff);
	    }
	    
	    if (idselectedfordestination != -1)
	    {
	    	if (idselectedfordestination == item.getId())
		    {
	    		textItem.setText("Your Destination\n --> " + item.getName() + ", " + "\n" + Math.round(getdistance(currentlat, currentlon, item.getLatitude(),  item.getLongitude())) + "m");
		    	textItem.setBackgroundColor(0xffff0000);
		    	textItem.setTextColor(0xffffffff);
		    }
	    }    
	    
 
	    if (list == myoutdoorlocations) { textItem.setOnClickListener(outdoorClickListener); }
	    else {
	    	if (list == universitylocations) { textItem.setOnClickListener(universityClickListener); }
		    else {
		    	if (list == pharmacylocations) { textItem.setOnClickListener(pharmacyClickListener); }
			    else {
			    	if (list == schoollocations) { textItem.setOnClickListener(schoolClickListener); }
				    else {
				    	if (list == foodlocations) { textItem.setOnClickListener(foodClickListener); }
					    else {
					    	if (list == librarylocations) { textItem.setOnClickListener(libraryClickListener); }
						    else {
						    	textItem.setOnClickListener(outdoorClickListener); // default
						    	if (enablelogging) Log.e(TAG, "error : createLocationListItem : no clicklistener found");
						    }	
					    }	
				    }	
			    }	
		    }	
	    }	
	    return textItem;		
	}
	
	/**
	 * Change the color of the background of the TextView of the selected item in the list
	 */
	
	public void selectItemInList (View v, ArrayList<OutdoorLocation> list)
	{
		//Log.d(TAG, "On click, view = " + v.getId());
		try
    	{
    		int i;
    		boolean found;
    		
    		i = 0;
    		found = false;
    		while ( (found == false) && (i < list.size()) )
    		{
    			//Log.d(TAG, "On click, view = " + (Integer) v.getTag());
    			if (list.get(i).getId() == (Integer) v.getTag())
    			{
    				found = true;
    				idSelectedInTheList = list.get(i).getId();
    				gotoLocation(list.get(i).getLatitude(),list.get(i).getLongitude(),18); // Ajouter Zoom
    				setDestination(list.get(i).getLatitude(),list.get(i).getLongitude(),0,list.get(i).getName());

    				setSelectedItemInList(v);			
    			}
    			i++;
    		}   	
    	}
    	catch (Exception e)
    	{
    		if (enablelogging) Log.e(TAG, "createLocationListItem, onClickListener " + e.toString());
    	}
	}
	
	public void clearTheList()
	{
		listItems.removeAllViews();
	}
	
	public void fillTheList()
	{
		addClosetsLocationsInList(closestlocations, closestlocations);
	    addClosetsLocationsInList(foodlocations, foodlocations);
	    addClosetsLocationsInList(schoollocations, schoollocations);
	    addClosetsLocationsInList(universitylocations, universitylocations);
	    addClosetsLocationsInList(pharmacylocations, pharmacylocations);
	    addClosetsLocationsInList(librarylocations, librarylocations);
	}
	
	public void setSelectedItemInList(View v)
	{
		/*
		int j;
		for(j=0;j<listItems.getChildCount();j++)
		{
			if (v.getClass() == TextView.class)
			{
			if (v == listItems.getChildAt(j))
			{
				listItems.getChildAt(j).setBackgroundColor(0xff0000ff);
			}
			else
			{
				listItems.getChildAt(j).setBackgroundColor(0xffffffff);
			}
			}
		}   
		*/
		clearTheList();
		fillTheList();
	}
	
	public void setSelectedItemInListByID(int id)
	{
		/*
		int j;
		for(j=0;j<listItems.getChildCount();j++)
		{
			if (id == (Integer) listItems.getChildAt(j).getTag())
			{
				listItems.getChildAt(j).setBackgroundColor(0xff0000ff);
			}
			else
			{
				listItems.getChildAt(j).setBackgroundColor(0xffffffff);
			}
		}   
		*/
		clearTheList();
		fillTheList();
	}
	
	public double getdistance(double x1, double y1, double x2, double y2)
	{
		double distance;
		double lat1 = x1 * Math.PI / 180;
		double lon1 = y1 * Math.PI / 180;
		double lat2 = x2 * Math.PI / 180;
		double lon2 = y2 * Math.PI / 180;
		//Log.d(TAG, lat1 + " " + " " + lon1 + " " + lat2 + " " + lon2);
		double dlon = (lon2 - lon1) / 2;
		double dlat = (lat2 - lat1) / 2;
		double dist = Math.sin(dlat) * Math.sin(dlat) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon) * Math.sin(dlon);
		//Log.d(TAG,"dist = " + dist);
		distance = (6378137 * 2 * Math.atan2(Math.sqrt(dist), Math.sqrt(1 - dist)));
		//Log.d(TAG,"distance = " + distance);
		return distance;
	}
	
	public void showPathandDirections(boolean changecontent)
	{
		if(idSelectedInTheList != -1) 
		{
		
		idselectedfordestination = idSelectedInTheList;
		
		try
		{
			pda.open();
			//pda.setDestination(destinationName);
			pda.close();
		}
		catch (Exception e)
		{
			if (enablelogging) Log.e(TAG, "error : showPathandDirections, database" + e.toString());
			try
			{
				pda.close();
			}
			catch (Exception e1)
			{
				if (enablelogging) Log.e(TAG, "error : showPathandDirections, database" + e1.toString());				
			}
		}
		
		clearTheList();
		fillTheList();
		
		try 
		{			
			String url = RoadProvider.getUrl(currentlat, currentlon, destinationlat, destinationlon);
	        InputStream is = getConnection(url);
	        mRoad = RoadProvider.getRoute(is);
	        mHandler.sendEmptyMessage(0);
		}
		catch (Exception e1)
		{
			if (enablelogging) Log.e(TAG, "error : showPathandDirections, RoadProvider" + e1.toString());
		}
		
        if (enablelogging) Log.d(TAG, "Start getting directions");
        
        String urlStringDirections = new String("http://scott-seto.com/ion/getdirections.html?lat=" + currentlat + "&lng=" + currentlon + "&start=");
        String adr;   		        
        String address = new String();
        
        if (enablelogging) Log.d(TAG, urlStringDirections);
        
		try {  // get up to 5 locations
			List<Address> lstFoundAddresses = gc.getFromLocation(currentlat, currentlon, 1); 
			if (lstFoundAddresses.size() == 0) 
				if (enablelogging) Log.d(TAG, "no adress");
			else {
				
				adr = lstFoundAddresses.get(0).getAddressLine(0) + " " + lstFoundAddresses.get(0).getAddressLine(1);							
				urlStringDirections = urlStringDirections + adr + "&end=";
				//showListOfFoundAddresses(lstFoundAddresses);
				//for now map the first address from the list
				//navigateToLocation(lstFoundAddresses.get(0), myMap); 
			}
			
			if (enablelogging) Log.d(TAG, urlStringDirections);
			
			lstFoundAddresses = gc.getFromLocation(destinationlat, destinationlon, 1);
			if (lstFoundAddresses.size() == 0) 
				if (enablelogging) Log.d(TAG, "no adress");
			else {
				
				adr = lstFoundAddresses.get(0).getAddressLine(0) + " " + lstFoundAddresses.get(0).getAddressLine(1);							
				urlStringDirections = urlStringDirections + adr;
			}
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), 1).show();
		}
        
		if (enablelogging) Log.d(TAG, urlStringDirections);
		
		try
		{
			changeDirectionsContent("", 0);
			changeDirectionsContent(urlStringDirections, 1);
		}
		catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), 1).show();
		}
			if (changecontent == true)
			{setVisibleContent(3);}
		}
		else
		{
			if (changecontent == true)
			{setVisibleContent(2);}
		}
		
        
	}
	
	public void updateDirections()
	{
		if (enablelogging) Log.d(TAG, "Start getting directions");
        
        String urlStringDirections = new String("http://scott-seto.com/ion/getdirections.html?lat=" + currentlat + "&lng=" + currentlon + "&start=");
        String adr;   		        
        String address = new String();
        
        if (enablelogging) Log.d(TAG, urlStringDirections);
        
		try {  // get up to 5 locations
			List<Address> lstFoundAddresses = gc.getFromLocation(currentlat, currentlon, 1); 
			if (lstFoundAddresses.size() == 0) 
				if (enablelogging) Log.d(TAG, "no adress");
			else {
				
				adr = lstFoundAddresses.get(0).getAddressLine(0) + " " + lstFoundAddresses.get(0).getAddressLine(1);							
				urlStringDirections = urlStringDirections + adr + "&end=";
				//showListOfFoundAddresses(lstFoundAddresses);
				//for now map the first address from the list
				//navigateToLocation(lstFoundAddresses.get(0), myMap); 
			}
			
			if (enablelogging) Log.d(TAG, urlStringDirections);
			
			lstFoundAddresses = gc.getFromLocation(destinationlat, destinationlon, 1);
			if (lstFoundAddresses.size() == 0) 
				if (enablelogging) Log.d(TAG, "no adress");
			else {
				
				adr = lstFoundAddresses.get(0).getAddressLine(0) + " " + lstFoundAddresses.get(0).getAddressLine(1);							
				urlStringDirections = urlStringDirections + adr;
			}
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), 1).show();
		}
        
		if (enablelogging) Log.d(TAG, urlStringDirections);
		
		try
		{
			changeDirectionsContent("", 0);
			changeDirectionsContent(urlStringDirections, 1);
		}
		catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), 1).show();
		}
	}
	
	public ArrayList<OutdoorLocation> getLocationsListFromMarker(Drawable marker)
	{
		if(marker.equals(getResources().getDrawable(android.R.drawable.star_big_on)) )
		{
			return myoutdoorlocations;
		}
		else
		{
			if(marker == getResources().getDrawable(android.R.drawable.btn_dialog) )
			{
				return universitylocations;
			}
			else
			{
				if(marker == getResources().getDrawable(android.R.drawable.btn_plus) )
				{
					return pharmacylocations;
				}
				else
				{
					if(marker == getResources().getDrawable(android.R.drawable.ic_dialog_info) )
					{
						return schoollocations;
					}
					else
					{
						if(marker.equals(getResources().getDrawable(android.R.drawable.ic_lock_idle_low_battery)) )
						{
							return foodlocations;
						}
						else
						{
							if(marker == getResources().getDrawable(android.R.drawable.ic_menu_agenda) )
							{
								return librarylocations;
							}
							else
							{
								if (enablelogging) Log.e(TAG, "error : getLocationsListFromMarker : no ArrayList<OutdoorLocation> found for marker");
								return myoutdoorlocations;
							}
						}
					}
				}
			}
		}
	}
	
	/**---------------------------------------------------
	 * ITEMOVERLAY CLASS 
	 * ---------------------------------------------------**/
	
	public class outdoorItemOverlay extends ItemizedOverlay<OverlayItem> {
		
		/** Declaration of the variables **/
	
		String TAG = "DualScreenApiSampleActivity";
		
		DualScreenApiSampleActivity dualScreenApiSample;
		Context context;
		
		private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
		private ArrayList<OutdoorLocation> LocationItems;
		private Drawable marker = null;
		
		/** Functions **/
	
		public outdoorItemOverlay(Drawable marker, ArrayList<OutdoorLocation> listItems) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			
			context = getApplicationContext();					
			
			if (listItems != null)
			{
				LocationItems = listItems;
			}		
			generateOverlay();
		    populate();
		}
		
		// generate the overlays with the list of items
		public void generateOverlay () {
			int i;		
			try 
			{
				for(i=0; i < LocationItems.size(); i++)
				{
					OverlayItem item = getOverlayItem(LocationItems.get(i));
					overlayItemList.add(item);
					if (enablelogging) Log.d(TAG, "add item " + item.getTitle() + " in overlay");
				}
			}catch (Exception e){
				if (enablelogging) Log.e(TAG, "error : generateOverlay : " + e.getMessage());
			}
		}
		
		// add an item in the overlay
		public void addItem(GeoPoint p, String title, String snippet){
			OverlayItem newItem = new OverlayItem(p, title, snippet);
			overlayItemList.add(newItem);
			populate();
		}
		
		@Override
		protected boolean onTap(int i) {				
				String buildingname;
			if (enablelogging) Log.e("scott","on tap1");
				//Log.d(TAG, "On Tap");
			
				//Log.d(TAG, "On Tap, item Id from snippet = " + overlayItemList.get(i).getSnippet());
				idSelectedInTheList = Integer.valueOf(overlayItemList.get(i).getSnippet());
				setSelectedItemInListByID(Integer.valueOf(overlayItemList.get(i).getSnippet()));
			
				//Log.d(TAG, "Get point : " + ((double) overlayItemList.get(i).getPoint().getLatitudeE6())/1000000 + ", " + ((double) overlayItemList.get(i).getPoint().getLongitudeE6())/1000000); 
				gotoLocation(((double) overlayItemList.get(i).getPoint().getLatitudeE6())/1000000,((double) overlayItemList.get(i).getPoint().getLongitudeE6())/1000000,18); // Ajouter Zoom
				setDestination(((double) overlayItemList.get(i).getPoint().getLatitudeE6())/1000000,((double) overlayItemList.get(i).getPoint().getLongitudeE6())/1000000,0,overlayItemList.get(i).getTitle());
			
				buildingname = new String(overlayItemList.get(i).getTitle());
				

				if (enablelogging) Log.e("scott","sending intent to the indoor activity");
				Intent intent = 
		                   new Intent( 
		                       getApplicationContext(), 
		                       IonMenu.class );
		                intent.putExtra( "onEmulator", "false" );
		                intent.putExtra( "onDualScreen", "true" );
		                intent.putExtra("placename", buildingname);
		                
		                startActivity( intent );
				
				return (true);
			
		}
		
		// return an OverlayItem
		public OverlayItem getOverlayItem(OutdoorLocation outdooritem) {
			OverlayItem item = new OverlayItem(getPoint(outdooritem.getLatitude(),outdooritem.getLongitude()), outdooritem.getName(), Integer.toString(outdooritem.getId()));
			//Log.d(TAG, "return OverlayItem (" + outdooritem.getLatitude() + "," + outdooritem.getLongitude() + "), " + outdooritem.getName() + ", " + outdooritem.getId());
			return item;		
		}
		
		// return a point from lat and lng
		private GeoPoint getPoint(double lat, double lng) {
			return (new GeoPoint((int) (lat * 1000000.0), (int) (lng * 1000000.0)));
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
    
	/**---------------------------------------------------
	 * PLACESOVERLAY CLASS 
	 * ---------------------------------------------------**/
	
	public class placesOverlay extends ItemizedOverlay<OverlayItem> {
		
		/** Declaration of the variables **/
	
		String TAG = "DualScreenApiSampleActivity";
		
		DualScreenApiSampleActivity dualScreenApiSample;
		Context context;
		
		private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
		private ArrayList<OutdoorLocation> LocationItems;
		private Drawable marker = null;
		
		/** Functions **/
	
		public placesOverlay(Drawable marker, ArrayList<OutdoorLocation> listItems) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			
			context = getApplicationContext();					
			
			if (listItems != null)
			{
				LocationItems = listItems;
			}		
			generateOverlay();
		    populate();
		}
		
		// generate the overlays with the list of items
		public void generateOverlay () {
			int i;		
			try 
			{
				for(i=0; i < LocationItems.size(); i++)
				{
					OverlayItem item = getOverlayItem(LocationItems.get(i));
					overlayItemList.add(item);
					//Log.d(TAG, "add item " + item.getTitle() + " in overlay");
				}
			}catch (Exception e){
				if (enablelogging) Log.e(TAG, "error : generateOverlay : " + e.getMessage());
			}
		}
		
		// add an item in the overlay
		public void addItem(GeoPoint p, String title, String snippet){
			OverlayItem newItem = new OverlayItem(p, title, snippet);
			overlayItemList.add(newItem);
			populate();
		}
		
		@Override
		protected boolean onTap(int i) {				
			if (enablelogging) Log.e("scott","on tap2");
				//Log.d(TAG, "On Tap");
				
				//Log.d(TAG, "On Tap, item Id from snippet = " + overlayItemList.get(i).getSnippet());
				idSelectedInTheList = Integer.valueOf(overlayItemList.get(i).getSnippet());
				setSelectedItemInListByID(Integer.valueOf(overlayItemList.get(i).getSnippet()));
				
				//Log.d(TAG, "Get point : " + ((double) overlayItemList.get(i).getPoint().getLatitudeE6())/1000000 + ", " + ((double) overlayItemList.get(i).getPoint().getLongitudeE6())/1000000); 
				gotoLocation(((double) overlayItemList.get(i).getPoint().getLatitudeE6())/1000000,((double) overlayItemList.get(i).getPoint().getLongitudeE6())/1000000,18); // Ajouter Zoom
				setDestination(((double) overlayItemList.get(i).getPoint().getLatitudeE6())/1000000,((double) overlayItemList.get(i).getPoint().getLongitudeE6())/1000000,0, overlayItemList.get(i).getTitle());
				
				
				setDestination(((double) overlayItemList.get(i).getPoint().getLatitudeE6())/1000000,((double) overlayItemList.get(i).getPoint().getLongitudeE6())/1000000,0,overlayItemList.get(i).getTitle());
				
				String buildingname = new String(overlayItemList.get(i).getTitle());
				

				if (enablelogging) Log.e("scott","sending intent to the indoor activity "+buildingname);
				Intent intent = 
		                   new Intent( 
		                       getApplicationContext(), 
		                       IonMenu.class );
		                intent.putExtra( "onEmulator", "false" );
		                intent.putExtra( "onDualScreen", "true" );
		                intent.putExtra("placename", buildingname);
		                
		                startActivity( intent );
				
				
				return (true);			
		}
		
		// return an OverlayItem
		public OverlayItem getOverlayItem(OutdoorLocation outdooritem) {
			OverlayItem item = new OverlayItem(getPoint(outdooritem.getLatitude(),outdooritem.getLongitude()), outdooritem.getName(), Integer.toString(outdooritem.getId()));
			//Log.d(TAG, "return OverlayItem (" + outdooritem.getLatitude() + "," + outdooritem.getLongitude() + "), " + outdooritem.getName() + ", " + outdooritem.getId());
			return item;		
		}
		
		// return a point from lat and lng
		private GeoPoint getPoint(double lat, double lng) {
			return (new GeoPoint((int) (lat * 1000000.0), (int) (lng * 1000000.0)));
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
	
	/**---------------------------------------------------
	 * ITEMOVERLAY CLASS 
	 * ---------------------------------------------------**/
	
	public class itemOverlay extends ItemizedOverlay<OverlayItem> {
		
		/** Declaration of the variables **/
	
		String TAG = "DualScreenApiSampleActivity";
		
		DualScreenApiSampleActivity dualScreenApiSample;
		Context context;
		
		private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
		private List<LocationItem> LocationItems;
		private Drawable marker = null;
		
		/** Functions **/
	
		public itemOverlay(Drawable marker, List<LocationItem> listItems) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			
			context = getApplicationContext();					
			
			if (listItems != null)
			{
				LocationItems = listItems;
			}		
			generateOverlay();
		    populate();
		}
		
		// generate the overlays with the list of items
		public void generateOverlay () {
			int i;		
			try 
			{
				for(i=0; i < LocationItems.size(); i++)
				{
					OverlayItem item = LocationItems.get(i).getOverlayItem();
					overlayItemList.add(item);
					//Log.d(TAG, "add item " + item.getTitle() + " in overlay");
				}
			}catch (Exception e){
				if (enablelogging) Log.e(TAG, "error : generateOverlay : " + e.getMessage());
			}
		}
		
		// add an item in the overlay
		public void addItem(GeoPoint p, String title, String snippet){
			OverlayItem newItem = new OverlayItem(p, title, snippet);
			overlayItemList.add(newItem);
			populate();
		}
		
		@Override
		protected boolean onTap(int i) {
			if (enablelogging) Log.e("scott","on tap3");
			if (LocationItems.get(i).getIsPlan() == 1)
			{
				//Log.d(TAG, "On Tap 0");
				
				if (Last_Sub_Overlay_File.equals(LocationItems.get(i).getPlan())) 
				{
					//Log.d(TAG, "On Tap 1");
					
					//Log.d(TAG, "Plan " + LocationItems.get(i).getPlan() + " already added in overlays");					
					//LocationItem item = LocationItems.get(i);					
					//addMoreContent(layoutContent4, item);
					
					// Update destination
					LocationItem item = LocationItems.get(i);
    				DestinationLat = item.getLat();
    				DestinationLon = item.getLng();
    				
    				//Log.d(TAG, "After upadating the location");
    				
    				String url = RoadProvider.getUrl(currentlat, currentlon, DestinationLat, DestinationLon);
    		        InputStream is = getConnection(url);
    		        mRoad = RoadProvider.getRoute(is);
    		        mHandler.sendEmptyMessage(0);
    		        
    		        
    		        
					return (true);
				}
				else
				{
					//Log.d(TAG, "On Tap 2");
					
					Last_Sub_Overlay_File = new String(LocationItems.get(i).getPlan());
					LocationItem.ExtractDataFromURL(LocationItems.get(i).getPlan(), LocationItems);
					listItems.removeAllViews();
					addItemTextViews(listItems);
					
					mapView.getOverlays().remove(0);
					myItemizedOverlay = new itemOverlay(marker, LocationItems);
				    mapView.getOverlays().add(myItemizedOverlay);
				    mapView.postInvalidate();				

					return (true);
				}				
			}
			else
			{			
				//Log.d(TAG, "On Tap 3");
				
				// Update destination
				LocationItem item = LocationItems.get(i);
				DestinationLat = item.getLat();
				DestinationLon = item.getLng();
				
				/*	
				List<Overlay> listOfOverlays = mapView.getOverlays();
				Overlay newOverlay = mapView.getOverlays().get(0);
				listOfOverlays.clear();
				*/
				String url = RoadProvider.getUrl(currentlat, currentlon, DestinationLat, DestinationLon);
		        InputStream is = getConnection(url);
		        mRoad = RoadProvider.getRoute(is);
		        mHandler.sendEmptyMessage(0);
		        
		        //mapView.getOverlays().add(newOverlay);
				
		        if (enablelogging) Log.d(TAG, "Start getting directions");
		        
		        String urlStringDirections = new String("http://scott-seto.com/ion/getdirections.html?lat=" + currentlat + "&lng=" + currentlon + "&start=");
		        String adr;   		        
		        String address = new String();
		        
		        if (enablelogging) Log.d(TAG, urlStringDirections);
		        
				try {  // get up to 5 locations
					List<Address> lstFoundAddresses = gc.getFromLocation(StartLat, StartLon, 1); 
					if (lstFoundAddresses.size() == 0) 
						if (enablelogging) Log.d(TAG, "no adress");
					else {
						
						adr = lstFoundAddresses.get(0).getAddressLine(0) + " " + lstFoundAddresses.get(0).getAddressLine(1);							
						urlStringDirections = urlStringDirections + adr + "&end=";
						//showListOfFoundAddresses(lstFoundAddresses);
						//for now map the first address from the list
						//navigateToLocation(lstFoundAddresses.get(0), myMap); 
					}
					
					if (enablelogging) Log.d(TAG, urlStringDirections);
					
					lstFoundAddresses = gc.getFromLocation(DestinationLat, DestinationLon, 1);
					if (lstFoundAddresses.size() == 0) 
						if (enablelogging) Log.d(TAG, "no adress");
					else {
						
						adr = lstFoundAddresses.get(0).getAddressLine(0) + " " + lstFoundAddresses.get(0).getAddressLine(1);							
						urlStringDirections = urlStringDirections + adr;
					}
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(), 1).show();
				}
		        
				if (enablelogging) Log.d(TAG, urlStringDirections);
				
				try
				{
					/*
					String finalurl =new String("http://scott-seto.com/ion/places" + StartLat + ".htm");
					
					URL urldestination = new URL("http://scott-seto.com/ion/getplaces.html?lat=" + StartLat + "&lng= " + StartLon + "&type=" + "university");
					
					String firsturl = "http://scott-seto.com/ion/getplaces.html?lat=" + StartLat + "&lng= " + StartLon + "&type=" + "university";
					changeDirectionsContent("", 0);
					changeDirectionsContent(firsturl, 1);
					
					Log.d(TAG, "Final URL : " + finalurl);
					
					HttpURLConnection connection = (HttpURLConnection) urldestination.openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("GET");
					OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
					wr.write("");
					wr.flush();
					*/
					
					changeDirectionsContent("", 0);
					changeDirectionsContent(urlStringDirections, 1);
					//changeDirectionsContent(finalurl, 1);
				}
				catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(), 1).show();
				}
				
				
				/*
		        try {
		        	Log.d(TAG, urlStringDirections);
					URL urlDirections = new URL(urlStringDirections);	
					HttpURLConnection connection = (HttpURLConnection) urlDirections.openConnection();
					connection.setRequestMethod("GET");
					connection.setDoOutput(true);
					connection.setReadTimeout(1000);
					
					connection.connect();
					
					InputStream inputStream = connection.getInputStream();	
					
					try {
						InputStreamReader ipsr = new InputStreamReader(inputStream);
						BufferedReader br = new BufferedReader(ipsr);
						String line;
						
						changeDirectionsContent("", 0);
						while ((line = br.readLine()) != null) {
							changeDirectionsContent(line, 1);
						}
						br.close();
					} catch (Exception e) {
						System.out.println(e.toString());
					}
					
				}
				catch (Exception e) {				
					e.printStackTrace();
				}	 
				*/
				
		        setVisibleContent(3);
		        
				//addMoreContent(layoutContent4, item);	
				/*
				LayoutInflater inflater = (LayoutInflater)context.getSystemService
					      (Context.LAYOUT_INFLATER_SERVICE);
				inflater.inflate(context.getResources().getXml(R.layout.locationinfo), layoutContent4);				
				*/
				/*
				locationInfo.putExtra(ItemInfo, LocationItems.get(i).getFullDataToString());
				startActivity(locationInfo);
				*/
				return (true);
			}
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

	
	/**---------------------------------------------------
	 * LOCAL RECEIVER CLASS 
	 * ---------------------------------------------------**/

	  private class MyMainLocalReceiver extends BroadcastReceiver{
	    @Override
		public void onReceive(Context localContext, Intent callerIntent) {
		double latitude = callerIntent.getDoubleExtra("latitude",-1);
		double longitude = callerIntent.getDoubleExtra("longitude",-1);
		currentlat = latitude;
		currentlon = longitude;
		
		displayGpsPosition(layoutContent1, new String(Double.toString(latitude) + " , " + Double.toString(longitude)));
		
		String url = RoadProvider.getUrl(currentlat, currentlon, DestinationLat, DestinationLon);
        InputStream is = getConnection(url);
        mRoad = RoadProvider.getRoute(is);
        mHandler.sendEmptyMessage(0);
		
		if (enablelogging) Log.e("MAIN>>>", Double.toString(latitude));
		if (enablelogging) Log.e("MAIN>>>", Double.toString(longitude));
	    }
	  }//MyMainLocalReceiver
	
} // END OF DualScreenApiSample

