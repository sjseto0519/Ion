package odu.cs.ion.campus;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.*;
import org.apache.http.util.*;

import com.kyocera.dualscreen.DualScreen;

import odu.cs.ion.map.IonDrawable;
import odu.cs.ion.map.IonView;
import odu.cs.ion.path.*;
import odu.cs.ion.database.GridData;
import odu.cs.ion.database.GridDbAdapter;
import odu.cs.ion.database.Place;
import odu.cs.ion.database.PlacesDbAdapter;

import odu.cs.ion.R;
import java.util.*;

public class CampusNav extends Activity {
    
	IonView ionview;
	int gridsize = 20;
    NavGraph graph;
    public String floorplan = null;
 // the exact position
    ArrayList<GridSquare> squares;
    
    // the grid position
    ArrayList<GridSquare> positions;
    
    public String onEmulator;
    public String onDualScreen;
    public String campusPlaceName;
    public String campusimage;
    public float startgpslat;
    public float startgpslong;
    public DualScreen dualscreen;
    public int campuswidth;
    public int campusheight;
    public float[] gpsnw = new float[2];
    public float[] gpsne = new float[2];
    public float[] gpssw = new float[2];
    public float[] gpsse = new float[2];
    public ArrayList<Place> myplaces;
    public PlacesDbAdapter pda;
   
    
    @Override
    public void onPause() {
        super.onPause();
 
    }
    
    public int[] getPixelPosition(float[] a)
    {
    	float perclat = (a[0]-gpssw[0])/(gpsnw[0]-gpssw[0]);
    	float perclong = (a[1]-gpssw[1])/(gpsse[1]-gpssw[1]);
    	int perc1 = (int)(perclat*campusheight);
    	int perc2 = (int)(perclong*campuswidth);
    	Log.e("pixelposition",""+a[0]+" "+a[1]+" "+perclat+" "+perclong+" "+perc1+" "+perc2);
    	return new int[] {perc2, campusheight-perc1};
    }
    
    public void initialize()
    {
    	pda = new PlacesDbAdapter();
  	  pda.open();
  	fillPlaces();
	  pda.close();
	  
	  String[] placeslist = new String[myplaces.size()];
	  for (int i = 0; i < myplaces.size(); ++i)
	  {
		placeslist[i] = ((Place)myplaces.get(i)).getName();
	  }
	  
	  ListView lv1=(ListView)this.findViewById(R.id.campuslistview2);
	     // By using setAdpater method in listview we an add string array in list.
	        lv1.setOnItemClickListener(new OnItemClickListener() {
	        	
	        	@Override
	            public void onItemClick(AdapterView arg0, View v, int position, long arg3) {	
	        		
	        		String name = (String) arg0.getItemAtPosition(position);
	        		for (int i = 0; i < myplaces.size(); ++i)
	        		{
	        			Place p1 = myplaces.get(i);
	        			String n1 = p1.getName();
	        			if (name.equals(n1))
	        			{
	        				float[] position1 = p1.getPosition();
	        				int[] position2 = getPixelPosition(position1);
	        				Log.e("movetoposition",""+position2[0]+" "+position2[1]);
	        				ionview.moveToPixel(position2, n1);
	        			}
	        		}
	        	
	        	}
	        
	        });
	        lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , placeslist));
	  
    }
    
	public void fillPlaces()
	{
		myplaces = pda.getPlaces(gpsnw, gpsne, gpssw, gpsse);
		Log.e("places", ""+myplaces.size());
	}
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent ii = getIntent();
        onEmulator = ii.getStringExtra("onEmulator");
        onDualScreen = ii.getStringExtra("onDualScreen");
        campusPlaceName = ii.getStringExtra("placename");
        campusimage = ii.getStringExtra("image");
        campuswidth = ii.getIntExtra("width", 0);
        campusheight = ii.getIntExtra("height", 0);
        startgpslat = ii.getFloatExtra("gpslat", 0.0f);
        startgpslong = ii.getFloatExtra("gpslong", 0.0f);
        gpsnw[0] = ii.getFloatExtra("gpsnw1", 0.0f);
        gpsnw[1] = ii.getFloatExtra("gpsnw2", 0.0f);
        gpssw[0] = ii.getFloatExtra("gpssw1", 0.0f);
        gpssw[1] = ii.getFloatExtra("gpssw2", 0.0f);
        gpsne[0] = ii.getFloatExtra("gpsne1", 0.0f);
        gpsne[1] = ii.getFloatExtra("gpsne2", 0.0f);
        gpsse[0] = ii.getFloatExtra("gpsse1", 0.0f);
        gpsse[1] = ii.getFloatExtra("gpsse2", 0.0f);
        
        
        
        try {
            if (onDualScreen.equals("true")) dualscreen = new DualScreen(this);
          }
          catch (Exception e) {}
        
    	if ( dualscreen == null || dualscreen.getScreenMode() == DualScreen.FULL ) {
   	       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	       setContentView(R.layout.campusmain); 
      	}
      	else
      	{
      		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  	           setContentView(R.layout.campusmain);  
      	}
    	
    	initialize();
    
    	int resID = getResources().getIdentifier(campusimage, "drawable", "odu.cs.ion");
        ionview = (IonView)findViewById(R.id.ionview);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resID);
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, campuswidth, campusheight, true);
        IonDrawable iond = new IonDrawable(getBaseContext(), bmp2, ionview.getMatrix());
        iond.setResources(getResources());
       
        
        ionview.setImageDrawable(iond);
        
        
        
     
    }
    
    public void printPath(List<NavNode> path)
    {
    	
    	try { 
      
    	       FileOutputStream fOut = openFileOutput("samplefile.txt",
    	                                                            MODE_WORLD_READABLE);
    	       OutputStreamWriter osw = new OutputStreamWriter(fOut); 



	      Iterator itr = path.iterator();
	    while (itr.hasNext()){
	      osw.write(((NavNode)itr.next()).toString()+'\n');
	    }   
	    
	    
	       /* ensure that everything is
	        * really written out and close */
	       osw.flush();
	       osw.close();
    	}
    	catch (Exception e) {
    		Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        	
    	}
    }
    
    public void setup(String a)
    {
    	floorplan = a;
        squares = new ArrayList<GridSquare>();
        positions = new ArrayList<GridSquare>();
		graph = new NavGraph();
		loadGraphFromFile("floor1graph");
		    	
    }
    
    public void loadGraphFromFile(String name)
    {
    	String[] words = null;
    	try {
    	InputStream is = getBaseContext().getAssets().open(name+".txt");
        BufferedInputStream bis = new BufferedInputStream(is);

        ByteArrayBuffer baf = new ByteArrayBuffer(50);

        int current = 0;

        while ((current = bis.read()) != -1) {

            baf.append((byte) current);

        }

        byte[] myData = baf.toByteArray();
        String dataInString = new String(myData);
        words = dataInString.split("\n");

        fillGraph(words);
    	
    	}
    	catch (Exception e) {
    		Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        	
    		e.printStackTrace();
    		}
    }
    
    public void fillGraph(String[] words)
    {
    	int start = 0;
    	for (;start < words.length; ++start)
    	{
    		words[start] = words[start].replaceAll("[^0-9.|' ']","");
    		String ii = words[start];
    		if (ii.equals("")) {
    			start++;
    			break;
    		}
    		int ind = ii.indexOf(" ");
    		if (ind==-1) {
    			start++;
    			break;
    		}
    		//Log.e("graph",ii);
    		squares.add(new GridSquare(getInt(ii.substring(0, ind)), getInt(ii.substring(ind+1, ii.length()))));
    	}
    	for (;start < words.length; ++start)
    	{
    		words[start] = words[start].replaceAll("[^0-9.|' ']","");
    		String ii = words[start];
    		if (ii.equals("")) {
    			start++;
    			break;
    		}
    		int ind = ii.indexOf(" ");
    		if (ind==-1) {
    			start++;
    			break;
    		}
    		positions.add(new GridSquare(getInt(ii.substring(0, ind)), getInt(ii.substring(ind+1, ii.length()))));
    	}
    	for (;start < words.length; ++start)
    	{
    		words[start] = words[start].replaceAll("[^0-9.|' ']","");
    		String ii2 = words[start];
    		if (ii2.equals("")) {
    			start++;
    			break;
    		}
    		start++;
    		String ii = words[start];
    		int ind = ii.indexOf(" ");
    		if (ind==-1) {
    			start++;
    			break;
    		}
    		graph.addNode(new NavNode(ii2, getDouble(ii.substring(0, ind)), getDouble(ii.substring(ind+1, ii.length()))));
    	}
    	for (;start < words.length; ++start)
    	{
    		words[start] = words[start].replaceAll("[^0-9.|' ']","");
    		String ii = words[start];
    		Log.e("check",ii+" "+ii.equals(""));
    		if (ii.equals("")) {
    			start++;
    			break;
    		}
    		start++;
    		String ii2 = words[start];
            start++;
            String ii3 = words[start];

    		graph.addEdge(new NavEdge(ii, ii2, getDouble(ii3)));
    	}
    	for (;start < words.length; ++start)
    	{
    		words[start] = words[start].replaceAll("[^0-9.|' ']","");
    		String ii3 = words[start];
    		if (ii3.equals("")) {
    			start++;
    			break;
    		}
    		start++;
    		String ii4 = words[start];
    		start++;
    		String ii5 = words[start];
    		int ind3 = ii5.indexOf(" ");
    		start++;
    		
    		NavNode aa = new NavNode(ii5, getDouble(ii5.substring(0, ind3)), getDouble(ii5.substring(ind3+1, ii5.length())));
    		Set<NavNode> aa2 = new HashSet<NavNode>();
        	for (;start < words.length; ++start)
        	{
        		words[start] = words[start].replaceAll("[^0-9.|' ']","");
    		String ii = words[start];
    		if (ii.equals("")) {
    			start++;
    			break;
    		}
    		start++;
    		String ii2 = words[start];
    		int ind = ii2.indexOf(" ");
    		aa2.add(new NavNode(ii, getDouble(ii2.substring(0, ind)), getDouble(ii2.substring(ind+1, ii2.length()))));	
    		}
        	
        	graph.putAdjacency(ii3, new Adjacency<NavNode>(aa, aa2));
        start--;
        }
    }
    
    public int getInt(String a)
    {
    	//a = a.replaceAll("[^0-9.]","");
    	return (int)Integer.parseInt(a);
    }
    
    public double getDouble(String a)
    {
    	//Log.e("graph",a);
    	//a = a.replaceAll("[^0-9.]","");
    	return (double)Double.parseDouble(a);
    }

}