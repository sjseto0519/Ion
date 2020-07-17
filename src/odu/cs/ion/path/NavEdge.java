package odu.cs.ion.path;

public class NavEdge extends Edge {

    protected double cost;
    
    public NavEdge(String from, String to, double cost)
    {
    	super(from,to);
    	this.cost = cost;
    	
    }
    
    public NavEdge(String from, String to)
    {
    	super(from,to);
    }
}