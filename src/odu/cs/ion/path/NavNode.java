package odu.cs.ion.path;

import java.util.*;

public class NavNode extends Node{

    protected Position position;

    protected List<String> extraData;
    
    public Position getPosition()
    {
    	return position;
    }
    
    public NavNode(String a, int b, int c)
    {
    	super(a);
    	position = new Position((double)b,(double)c);
    	
    }
    
    public NavNode(String a, double b, double c)
    {
    	super(a);
    	position = new Position(b,c);
    	
    }
    
    public NavNode(String a, int b, int c, String d)
    {
    	super(a);
    	position = new Position((double)b,(double)c);
    	extraData = new ArrayList<String>();
    	extraData.add(d);
    	
    }
    
    public String toString()
    {
    	return ""+getPosition().getX()+" "+getPosition().getY();
    }

}