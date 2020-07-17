package odu.cs.ion.map;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.util.ByteArrayBuffer;

import odu.cs.ion.database.Location;
import odu.cs.ion.indoor.IndoorLocate;
import odu.cs.ion.R;
import odu.cs.ion.map.*;
import odu.cs.ion.path.*;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public class IndoorNav {

	IonView ionview;
	int gridsize = 20;
    NavGraph graph;
    public String floorplan = null;
 // the exact position
    ArrayList<GridSquare> squares;
    
    // the grid position
    ArrayList<GridSquare> positions;
    
    Resources resources;
    Context context;
    IndoorLocate indoorlocate;
    
    int floorplanid;
    String floorplangraph;
    String floorplan1;
    int size1, size2;
    public IonDrawable iond;
    
    
    public void setIndoorLocate(IndoorLocate a)
    {
    	this.indoorlocate = a;
    }
    
	public IndoorNav(Resources r, Context c, IndoorLocate indoorlocate)
	{
		resources = r;
		context = c;
		this.indoorlocate = indoorlocate;
	}
	
	public void setLocations(ArrayList<Location> a)
	{
		ionview = (IonView)indoorlocate.findViewById(R.id.ionview);
		ionview.setLocations(a);
	}
	
	public void setSize(int a, int b)
	{
		size1 = a;
		size2 = b;
	}
	
	public void setId(int id)
	{
		floorplanid = id;
	}
	
	public void setGraph(String graph)
	{
		floorplangraph = graph;
	}
	
	public void setFloorPlan(String floorplan)
	{
		this.floorplan1 = floorplan;
	}
	
	public void placePerson(int a, int b)
	{
		ionview.placePerson(a, b);
		//List<NavNode> path = AStarAlgorithm.search(graph, graph.getFirst(), graph.getLast());
	    //iond.setPath(path);   
	}
	
	public void placePersonByPixel(int a, int b)
	{
		ionview.placePersonByPixel(a, b);
		//List<NavNode> path = AStarAlgorithm.search(graph, graph.getFirst(), graph.getLast());
	    //iond.setPath(path);   
	}
	
	public void drawPixelBasedPath(int[] a, int[] b)
	{
		List<NavNode> path = AStarAlgorithm.search(graph, graph.getNodeAtPixel(a), graph.getNodeAtPixel(b));
        iond.setPath(path);
	}
	
	public void drawPath(int ax, int ay, int bx, int by)
	{
		List<NavNode> path = AStarAlgorithm.search(graph, graph.getNodeAtPixel(new int[] {ax,ay}), graph.getNodeAtPixel(new int[] {bx,by}));
        iond.setPath(path);
	}
	
	public void placeIcon(Bitmap b, int x, int y)
	{
		iond.placeIcon(b, x, y);
	}
	
	public void centerAtPosition(int[] a)
	{
		int resID = resources.getIdentifier("pngcircle", "drawable", "odu.cs.ion");
		Bitmap pngcircle = BitmapFactory.decodeResource(resources, resID);
        pngcircle = Bitmap.createScaledBitmap(pngcircle, 36, 36, true);

		iond.setCircleBitmap(pngcircle);
		iond.circlePixel(a);
		ionview.moveToPixel(a);
	}
	
	public void refresh()
	{
		iond.refresh();
	}
	
	public void createMap()
	{
		
        Bitmap bmp = BitmapFactory.decodeResource(resources, floorplanid);
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, size1, size2, true);
        iond = new IonDrawable(context, bmp2, ionview.getMatrix());
        
        
    
        
        setup(floorplan1);
        
        //Toast.makeText(context, ""+graph.getFirst()+" "+graph.getLast(), Toast.LENGTH_SHORT).show();
    	
        
        List<NavNode> path = AStarAlgorithm.search(graph, graph.getFirst(), graph.getLast());
        //printPath(path);
        //iond.setPath(path);
        
        Bitmap bmp3 = BitmapFactory.decodeResource(resources, R.drawable.starticon);
        bmp3 = Bitmap.createScaledBitmap(bmp3, 24, 24, true);
        
        //iond.placeStartIcon(bmp3, 29.0f,29.0f);
        
        Bitmap bmp5 = BitmapFactory.decodeResource(resources, R.drawable.personicon);
        bmp5 = Bitmap.createScaledBitmap(bmp5, 24, 24, true);
        
        iond.placePersonIcon(bmp5, 29.0f,29.0f);
        
        Bitmap bmp4 = BitmapFactory.decodeResource(resources, R.drawable.endicon);
        bmp4 = Bitmap.createScaledBitmap(bmp4, 24, 24, true);
        
        //iond.placeEndIcon(bmp4, 689.0f,189.0f);
        
        ionview.setImageDrawable(iond);
	}
	
	public void printPath(List<NavNode> path)
    {
    	
    	try { 
      
    	       FileOutputStream fOut = indoorlocate.openFileOutput("samplefile.txt",
    	                                                            indoorlocate.MODE_WORLD_READABLE);
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
    		//Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        	
    	}
    }
    
    public void setup(String a)
    {
    	floorplan = a;
        squares = new ArrayList<GridSquare>();
        positions = new ArrayList<GridSquare>();
		graph = new NavGraph();
		loadGraphFromFile(floorplangraph);
		    	
    }
    
    public void loadGraphFromFile(String name)
    {
    	String[] words = null;
    	try {
    	InputStream is = context.getAssets().open(name+".txt");
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
    		//Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        	
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
    		//Log.e("check",ii+" "+ii.equals(""));
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
