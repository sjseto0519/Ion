package odu.cs.ion.outdoor;

import com.kyocera.dualscreen.DualScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import odu.cs.ion.R;


public class OutdoorLocate extends Activity implements LocationListener {
	
	private WebView webview;
	private static final String TAG = "Main";
	private ProgressDialog progressBar;
	private DualScreen dualscreen;
	public Location mostRecentLocation;
	public double latitude = 36.469;
	public double longitude = -76.045;
	public PowerManager.WakeLock wl;
	public String onEmulator;
	public String onDualScreen;
	public String startaddress;
	public String endaddress;
	public float startgpslat;
	public float startgpslong;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   
       
        Intent ii = getIntent();
        onEmulator = ii.getStringExtra("onEmulator");
        onDualScreen = ii.getStringExtra("onDualScreen");
        startgpslat = ii.getFloatExtra("gpslat", 0.0f);
        startgpslong = ii.getFloatExtra("gpslong", 0.0f);
        startaddress = ii.getStringExtra("startaddress");
        endaddress = ii.getStringExtra("endaddress");
        startaddress = startaddress.replaceAll(" ","%20");
        endaddress = endaddress.replaceAll(" ","%20");
       
        try {
            if (onDualScreen.equals("true")) dualscreen = new DualScreen(this);
          }
          catch (Exception e) {}
        
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	wl.release();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	 wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
    	 wl.acquire();
    	
    	
    	 if ( dualscreen == null || dualscreen.getScreenMode() == DualScreen.FULL ) {
    	       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	       setContentView(R.layout.main_full); 

    	       //getLocation();
    	       
    	       final String centerURL = "javascript:centerAt(" +
    	    		    latitude + "," +
    	    		    longitude+ ")";
    	       
    	       this.webview = (WebView)findViewById(R.id.webview);
    	       
    	       webview.getSettings().setJavaScriptEnabled(true);
    	       webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    	       
    	       final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	       
    	      
    	       webview.setWebViewClient(new WebViewClient() {
    	           public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	               Log.i(TAG, "Processing webview url click...");
    	               view.loadUrl(url);
    	               return true;
    	           }

    	           public void onPageFinished(WebView view, String url) {
    	               Log.i(TAG, "Finished loading URL: " +url);
  	    	       
    	               try {
    	               //Thread.sleep(30000);
    	               }
    	               catch (Exception e) {}
    	               //Log.i(TAG, "Moving");
      	    	       
    	               //view.loadUrl("javascript:moveTo("+latitude+","+longitude+")");
    	    	      
    	           }

    	           public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    	               Log.e(TAG, "Error: " + description);
    	              alertDialog.setTitle("Error");
    	               alertDialog.setMessage(description);
    	               alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	                   public void onClick(DialogInterface dialog, int which) {
    	                       return;
    	                   }
    	               });
    	               alertDialog.show();
    	           }
    	       });
    	       //webview.loadUrl("http://gmaps-samples-v3.googlecode.com/svn/trunk/draggable-directions/draggable-directions.html");
    	       webview.loadUrl("http://scott-seto.com/ion/draggable-directions5.html?lat="+startgpslat+"&lng="+startgpslong+"&start="+startaddress+"&end="+endaddress);
    	        //webview.addJavascriptInterface(new JavaScriptInterface(), "android");
    	       Log.e("h","h");
    		    
    	        } else {
    	           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	           setContentView(R.layout.main_normal);  
    	           
    	        }
    	 
    }
    
    private void getLocation() {
        LocationManager locationManager =
          (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria,true);
        //In order to make sure the device is getting the location, request updates.
        //locationManager.requestLocationUpdates(provider, 1, 0, this);
        //mostRecentLocation = locationManager.getLastKnownLocation(provider);
      }

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}