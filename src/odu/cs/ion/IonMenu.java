package odu.cs.ion;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kyocera.dualscreen.DualScreen;

import odu.cs.ion.R;
import odu.cs.ion.campus.CampusNav;
import odu.cs.ion.database.GridData;
import odu.cs.ion.database.GridDbAdapter;
import odu.cs.ion.database.Location;
import odu.cs.ion.database.Map;
import odu.cs.ion.database.Place;
import odu.cs.ion.database.PlacesDbAdapter;
import odu.cs.ion.external.ExternalServer;
import odu.cs.ion.gps.GPSUtilities;
import odu.cs.ion.indoor.IndoorLocate;
import odu.cs.ion.outdoor.OutdoorLocate;
import odu.cs.ion.pedometer.StepService;

public class IonMenu extends Activity implements OnItemClickListener {
    /** Called when the activity is first created. */
	
	private DualScreen dualscreen;
	public float currentgpslat;
	public float currentgpslong;
	public ArrayList<Map> mymaps;
	public ArrayList<Place> myplaces;
	public ArrayList<Location> mylocations;
	public PlacesDbAdapter pda;
	public GridDbAdapter gda;
	public HashMap<String, GridData> grids;
	public Map closestindoor;
	public Map closestoutdoor;
	public int floors;
	public ArrayList<String> indoorlist = new ArrayList<String>();
	public Dialog indoordialog;
	public ArrayList<String> campuslist = new ArrayList<String>();
	public Dialog campusdialog;
	public boolean onEmulator = false;
	public boolean onDualScreen = true;
	public HashMap<Integer,String> indoorimage = new HashMap<Integer,String>();
	public int indoorwidth;
	public int indoorheight;
	public static String startaddress;
	public static String endaddress;
	public String indoorimage1;
	public String campusimage1;
	public int campuswidth;
	public int campusheight;
	public int indoorinchwidth;
	public int indoorinchheight;
	public boolean mIsRunning;
	public float CompassAccuracy;
	public boolean isfinished = false;
	
	public void initialize()
	{
	  currentgpslat = 36.88408f;
	  currentgpslong = -76.30517f;
	  onEmulator = false;
	  onDualScreen = true;
	  pda = new PlacesDbAdapter();
	  pda.open();
	  pda.deleteAll(true);
	  pda.create();
	  gda = new GridDbAdapter();
	  gda.open();
	  gda.deleteAll(true);
	  gda.create();
	  ExternalServer es = new ExternalServer();
	  String insert = es.getPlacesSql();
	  Log.e("insert", ""+insert);
	  pda.execute(insert);
	  insert = es.getSql();
	  
	  gda.execute(insert);
	  fillMaps();
	  fillPlaces();
	  fillLocations();
	  pda.close();
	  gda.close();
	  closestindoor = getClosestIndoorMap();
	  closestoutdoor = getClosestOutdoorMap();
	  floors = getFloors(closestindoor);
	  
	  mIsRunning = false;
	  CompassAccuracy = 0;
	  /*
	  startStepService();
	  bindStepService();
	  */
	  
	}
	
	public void onResume() {
		super.onResume();
		
	if (isfinished) finish();
	}
	
	public void onPause() {
		super.onPause();
		
		/*
		if (mIsRunning)
	unbindStepService();
		
		mIsRunning = false;
		*/
	}
	
    private void startStepService() {
        if (! mIsRunning) {
            mIsRunning = true;
            startService(new Intent(IonMenu.this,
                    StepService.class));
            mIsRunning = true;
        }
    }
    
    private void bindStepService() {
        boolean aa = bindService(new Intent(IonMenu.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
        //Toast.makeText(this, "bind "+aa, Toast.LENGTH_SHORT).show();
      	
    }
    
    private void unbindStepService() {
        unbindService(mConnection);
    }
    
    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();
            //Toast.makeText(getBaseContext(), "service connected", Toast.LENGTH_LONG).show();
    		

            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            //mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            //mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            //mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            //mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            //mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
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

                break;
                case COMPASS_ACCURACY_MSG:
                CompassAccuracy = (int)msg.arg1;
                Log.e("compassaccuracy",""+CompassAccuracy);
                if (CompassAccuracy > 1)
                {
                	
            	    unbindStepService();
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
	
	public int getFloors(Map m)
	{
		String name = m.getName();
		int maxfloor = 1;
		for (int i = 0; i < mymaps.size(); ++i)
		{
			Map m2 = mymaps.get(i);
			int width = m2.getWidth();
			int height = m2.getHeight();
            String name2 = m2.getName();
            String image = m2.getImage();
            int floor = m2.getFloor();
            if (floor > maxfloor && name2.equals(name))
            {
              maxfloor = floor;
              indoorwidth = width;
              indoorheight = height;
              indoorimage1 = image;
            }
		}
		Log.e("floors",""+maxfloor);
		return maxfloor;
	}
	
	public Map getClosestIndoorMap()
	{
		float min = 1000000.0f;
		Map minmap = null;
		for (int i = 0; i < mymaps.size(); ++i)
		{
			Map m = mymaps.get(i);
			String name = m.getName();
			if (m.outdoor()==false)
			{
				int floor = m.getFloor();
				String image = m.getImage();
				indoorlist.add(name+" "+"Floor "+floor);
				indoorimage.put(new Integer(floor), image);
			}
			float[] a = m.getGps();
			float dist = GPSUtilities.getDistance(a[0],a[1],currentgpslat, currentgpslong);
			if (dist < min && m.outdoor()==false)
			{
				
				minmap = m;
				min = dist;
			}
		}
		Log.e("indoormap",minmap.getName());
		return minmap;
	}
	
	public Map getClosestOutdoorMap()
	{
		float min = 1000000.0f;
		Map minmap = null;
		for (int i = 0; i < mymaps.size(); ++i)
		{
			Map m = mymaps.get(i);
			if (m.outdoor())
			{
			String name = m.getName();
			campuslist.add(name);
			}
			float[] a = m.getGps();
			float dist = GPSUtilities.getDistance(a[0],a[1],currentgpslat, currentgpslong);
			if (dist < min && m.outdoor())
			{
				
				minmap = m;
				min = dist;
			}
		}
		Log.e("outdoormap",minmap.getName());
		return minmap;
	}
	
	public void fillMaps()
	{
		mymaps = pda.getMaps();
		Log.e("maps", ""+mymaps.size());
	}
	
	public void fillPlaces()
	{
		myplaces = pda.getPlaces();
		Log.e("places", ""+myplaces.size());
	}
	
	public void fillLocations()
	{
		mylocations = pda.getLocations();
		Log.e("locations", ""+mylocations.size());
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = this.getIntent(); 
        String str = i.getStringExtra("placename");
        
        Log.e("scott","placename is null");
        if (str == null)
        {
        	finish();
        }
        
        initialize();       
        
        
        Intent intent = 
                new Intent( 
                    getApplicationContext(), 
                    IndoorLocate.class );
             intent.putExtra( "onEmulator", "false" );
             intent.putExtra( "onDualScreen", "true" );
             intent.putExtra("placename", str);
        
             isfinished = true;
        startActivity(intent);
        
        
        try {
          if (onDualScreen) dualscreen = new DualScreen(this);
        }
        catch (Exception e) {}
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
        
    	if ( dualscreen == null || dualscreen.getScreenMode() == DualScreen.FULL ) {
 	       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
 	       setContentView(R.layout.main); 
    	}
    	else
    	{
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	           setContentView(R.layout.main);  
    	}
    	
    	
    	
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
 	   if (requestCode == 0) {
 	
 	      if (resultCode == RESULT_OK) {
 	
 	         String contents = intent.getStringExtra("SCAN_RESULT");
 	
 	         String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
 	
 	         Log.e("contents",contents);
 	         //Toast.makeText(this, ""+contents, Toast.LENGTH_SHORT).show();
 	        	
 	         IonXml ix = new IonXml(contents);
 	         ix.parseUrl();
 	         int[] position = ix.getPosition();
 	         Log.e("position", ""+position[0]+" "+position[1]);
 	         ClassInformation ci = ix.getClassInformation();
 	         String placename = ix.getPlaceName();
 	         String floor2 = ix.getFloor();
 	         
 	         
 	         
 	         
 	         
 	    	String name = placename+" Floor "+floor2;
 	       int ind = name.lastIndexOf(" ");
 	       int floor = Integer.parseInt(name.substring(ind+1, name.length()));
 	       
 	       for (int i = 0; i < mymaps.size(); ++i)
 	       {
 	       	Map mm = mymaps.get(i);
 	       	String nn = mm.getName();
 	       	Log.e("nameandimage",name+" "+nn+" "+mm.getImage());
 	       	int ind2 = name.indexOf(" ");
 	       	String n2 = name.substring(0,ind2);
 	       	if (nn.contains(n2))
 	       	{
 	       		indoorimage1 = mm.getImage();
 	       		indoorwidth = mm.getWidth();
 	       		indoorheight = mm.getHeight();
 	       	}
 	       }
 	       
 	       Intent intent2 = 
 	               new Intent( 
 	                   this, 
 	                   IndoorLocate.class );
 	           intent2.putExtra( "onEmulator", ""+onEmulator );
 	           intent2.putExtra( "onDualScreen", ""+onDualScreen );
 	           intent2.putExtra("placename", name);
 	           intent2.putExtra("floor",""+floor);
 	           intent2.putExtra("stepsize","24");
 	           intent2.putExtra("image",indoorimage1);
 	           intent2.putExtra("indoorwidth",""+indoorwidth);
 	           intent2.putExtra("indoorheight",""+indoorheight);
 	           intent2.putExtra("position", ""+position[0]+" "+position[1]);
 	           startActivity( intent2 );
 	           
 	         
 	         
 	         // Handle successful scan
 	
 	      } else if (resultCode == RESULT_CANCELED) {
 	
 	         // Handle cancel
 	
 	      }
 	
 	   }
 	
 	}

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icontext1:     //Toast.makeText(this, "You pressed the icon!", Toast.LENGTH_LONG).show();
                                
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            
            startActivityForResult(intent, 0);
            
            
            break;
            case R.id.icontext2:     Toast.makeText(this, "You pressed the text!", Toast.LENGTH_LONG).show();
             
  
            //set up dialog
            final Dialog dialog = new Dialog(IonMenu.this);
            dialog.setContentView(R.layout.maindialog3);
            dialog.setCancelable(true);
            
            Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.steplength, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
            //there are a lot of settings, for dialog, check them all out!

            //set up image view
            ImageView img = (ImageView) dialog.findViewById(R.id.ImageView01);
            img.setImageResource(R.drawable.settingsicon1);

            //set up button
            Button button = (Button) dialog.findViewById(R.id.Button01);
            button.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            //now that the dialog is set up, it's time to show it    
            dialog.show();
            
            
            
            break;
            case R.id.icontext3: Toast.makeText(this, "You pressed the icon and text!", Toast.LENGTH_LONG).show();
            
            
            
          //set up dialog
            final Dialog dialog2 = new Dialog(IonMenu.this);
            dialog2.setContentView(R.layout.maindialog);
            dialog2.setCancelable(true);
            //there are a lot of settings, for dialog, check them all out!

            //set up image view
            ImageView img2 = (ImageView) dialog2.findViewById(R.id.ImageView01);
            img2.setImageResource(R.drawable.abouticon1);

            //set up button
            Button button2 = (Button) dialog2.findViewById(R.id.Button01);
            button2.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                }
            });
            //now that the dialog is set up, it's time to show it    
            dialog2.show();
            
            
                                break;
                                
                                
            case R.id.icontext4: Toast.makeText(this, "You pressed the icon and text!", Toast.LENGTH_LONG).show();
            
           

            //set up dialog
            final Dialog dialog3 = new Dialog(IonMenu.this);
            dialog3.setContentView(R.layout.testingdialog);
            dialog3.setCancelable(true);
            //there are a lot of settings, for dialog, check them all out!


            TextView view3 = (TextView) dialog3.findViewById(R.id.textViewTest);
            String text = "";
            text += "Current GPS: "+currentgpslat+" "+currentgpslong+"\n";
            view3.setText(text);
            //set up button
            Button button3 = (Button) dialog3.findViewById(R.id.Button01);
            button3.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
                    dialog3.dismiss();
                }
            });
            //now that the dialog is set up, it's time to show it    
            dialog3.show();
            
            
            break;
        }
        return true;
    }
    
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          Toast.makeText(parent.getContext(), "The planet is " +
              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    public void directionshandler(View target) {
    	//Toast.makeText(this, "showdirections", Toast.LENGTH_LONG).show();
    	
        //set up dialog
        final Dialog dialog2 = new Dialog(IonMenu.this);
        dialog2.setContentView(R.layout.maindialog2);
        dialog2.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up image view
        ImageView img2 = (ImageView) dialog2.findViewById(R.id.ImageView01);
        img2.setImageResource(R.drawable.directionsicon);

        EditText start = (EditText) dialog2.findViewById(R.id.startaddresstext);
        startaddress = start.getText().toString();
        EditText end = (EditText) dialog2.findViewById(R.id.endaddresstext);
        endaddress = end.getText().toString();
        
        endaddress = "4457 Clemsford Dr. Virginia Beach, VA";
        startaddress = "4320 Hampton Blvd, Norfolk, VA";
        
        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonclickview3: // doStuff
                        Intent intent = 
                        new Intent( 
                            getBaseContext(), 
                            OutdoorLocate.class );
                        intent.putExtra( "onEmulator", ""+onEmulator );
                        intent.putExtra( "onDualScreen", ""+onDualScreen );
                        intent.putExtra("gpslat", currentgpslat);
                        intent.putExtra("gpslong", currentgpslong);
                        intent.putExtra("startaddress", IonMenu.startaddress);
                        intent.putExtra("endaddress", IonMenu.endaddress);
                    startActivity( intent );
                        break;

                }
            }
        };
        
        Button bb = (Button) dialog2.findViewById(R.id.buttonclickview3);
        bb.setOnClickListener(handler);
        //set up button
        Button button2 = (Button) dialog2.findViewById(R.id.Button01);
        button2.setOnClickListener(new OnClickListener() {
        @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it    
        dialog2.show();
    	
    }
    
    public void campushandler(View target) {
    	//Toast.makeText(this, "showcampus", Toast.LENGTH_LONG).show();
    	
    	String[] campuslist2 = campuslist.toArray(new String[] {});
        //set up dialog
        campusdialog = new Dialog(IonMenu.this);
        campusdialog.setContentView(R.layout.campusdialog);
        campusdialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!
        
        ListView lv1=(ListView)campusdialog.findViewById(R.id.campuslistview1);
     // By using setAdpater method in listview we an add string array in list.
        lv1.setOnItemClickListener(this);
        lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , campuslist2));

        //set up button
        Button button3 = (Button) campusdialog.findViewById(R.id.campuslayoutbutton);
        button3.setOnClickListener(new OnClickListener() {
        @Override
            public void onClick(View v) {
        	campusdialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it    
        campusdialog.show();
        
    }
    
    
    public void indoorhandler(final View target) {
    	//Toast.makeText(this, "showindoor", Toast.LENGTH_LONG).show();
    	
    	String[] indoorlist2 = indoorlist.toArray(new String[] {});
        //set up dialog
        indoordialog = new Dialog(IonMenu.this);
        indoordialog.setContentView(R.layout.indoordialog);
        indoordialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!
        
        ListView lv1=(ListView)indoordialog.findViewById(R.id.indoorlistview1);
     // By using setAdpater method in listview we an add string array in list.
        lv1.setOnItemClickListener(new OnItemClickListener() {
        	
            @Override
            public void onItemClick(AdapterView arg0, View v, int position, long arg3) {	
            // TODO Auto-generated method stub
            	String name = (String) arg0.getItemAtPosition(position);
            //Toast.makeText(target.getContext(), "u clicked " +position+" "+name ,Toast.LENGTH_LONG).show();
            indoordialog.dismiss();
            int ind = name.lastIndexOf(" ");
            int floor = Integer.parseInt(name.substring(ind+1, name.length()));
            int ind2 = name.indexOf(" Floor");
            name = name.substring(0, ind2);
            
            
            Intent intent = 
                    new Intent( 
                        target.getContext(), 
                        IndoorLocate.class );
                intent.putExtra( "onEmulator", ""+onEmulator );
                intent.putExtra( "onDualScreen", ""+onDualScreen );
                intent.putExtra("placename", name);


                if (CompassAccuracy > 1) startActivity( intent );
                else
                {
                	startActivity( intent );
                	//Toast.makeText(getBaseContext(), "accuracy "+CompassAccuracy ,Toast.LENGTH_LONG).show();
                    	
                }
        	
        	
        }
        });
        lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , indoorlist2));

        //set up button
        Button button3 = (Button) indoordialog.findViewById(R.id.indoorlayoutbutton);
        button3.setOnClickListener(new OnClickListener() {
        @Override
            public void onClick(View v) {
        	indoordialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it    
        indoordialog.show();
        
    }
    
    @Override
    public void onItemClick(AdapterView arg0, View v, int position, long arg3) {

    		
        	String name = (String) arg0.getItemAtPosition(position);
            //Toast.makeText(this, "u clicked " +position+" "+name ,Toast.LENGTH_LONG).show();
            campusdialog.dismiss();
            Map mm1 = null;
            for (int i = 0; i < mymaps.size(); ++i)
            {
            	Map mm = mymaps.get(i);
            	String nn = mm.getName();

            	if (nn.equals(name))
            	{
            		campuswidth = mm.getWidth();
            		campusheight = mm.getHeight();
            		campusimage1 = mm.getImage();
            		mm1 = mm;
            	}
            }
            
            Intent intent = 
                    new Intent( 
                        this, 
                        CampusNav.class );
                intent.putExtra( "onEmulator", ""+onEmulator );
                intent.putExtra( "onDualScreen", ""+onDualScreen );
                intent.putExtra("placename", name);
                intent.putExtra("image",campusimage1);
                intent.putExtra("width",campuswidth);
                intent.putExtra("height",campusheight);
                intent.putExtra("gpslat", currentgpslat);
                intent.putExtra("gpslong", currentgpslong);
                intent.putExtra("gpsnw1", mm1.getGpsNw1());
                intent.putExtra("gpsnw2", mm1.getGpsNw2());
                intent.putExtra("gpssw1", mm1.getGpsSw1());
                intent.putExtra("gpssw2", mm1.getGpsSw2());
                intent.putExtra("gpsne1", mm1.getGpsNe1());
                intent.putExtra("gpsne2", mm1.getGpsNe2());
                intent.putExtra("gpsse1", mm1.getGpsSe1());
                intent.putExtra("gpsse2", mm1.getGpsSe2());
                startActivity( intent );
    		
    	    	
    }
    
}