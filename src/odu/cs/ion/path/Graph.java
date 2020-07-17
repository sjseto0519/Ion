package odu.cs.ion.path;

import java.util.*;

import odu.cs.ion.*;

import odu.cs.ion.R;
import odu.cs.ion.indoor.IndoorLocate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.*;


public class Graph<N extends Node, E extends Edge> {

    protected List<N> nodeList;
    
    protected List<E> edgeList;
    
    //Index for fast access
    private Map<String, Adjacency<N>> adjacency;

    //directed graph or not
    protected boolean diGraph;
    
    protected IndoorLocate indoornav;
    
    public String floorplan;
    
    public Graph(List<N> a)
    {
    	nodeList = a;
    }
    
    public void putAdjacency(String a, Adjacency<N> b)
    {
    	adjacency.put(a, b);
    	
    }
    
    public Graph()
    {
    	nodeList = new ArrayList<N>();
    	edgeList = new ArrayList<E>();
    	adjacency = new HashMap<String, Adjacency<N>>();
    }
    
    public Graph(String plan)
    {
    	nodeList = new ArrayList<N>();
    	edgeList = new ArrayList<E>();
    	adjacency = new HashMap<String, Adjacency<N>>();
    	floorplan = plan;
    }
    
    
    public N getFirst()
    {
    	return nodeList.get(0);
    }
    
    public N getLast()
    {
    	return nodeList.get(nodeList.size()-1);
    }
    
    public List<N> getNodeList()
    {
    	return nodeList;
    }
    
    public void createAdjacency(N a)
    {
    	String id = a.getId();
    	adjacency.put(id, new Adjacency<N>(a));
    }
    
    public void addAdjacency(String a, String b)
    {
        Adjacency<N> aa = adjacency.get(a);
        N bb = getNode(b);
        aa.addNeighbor(bb);
    }
    
    public void setNodeList(List<N> a)
    {
    	nodeList = a;
    }
    
    public N getNode(String a)
    {
    	for (int i = 0; i < nodeList.size(); ++i)
    	{
    		N aa = nodeList.get(i);
    		String id = aa.getId();
    		if (id.equals(a))
    		{
    			return aa;
    		}
    	}
    	return null;
    }
    
    public N getNodeAtPixel(int[] a)
    {
    	int mindist = 100000;
    	N aa1 = null;
    	for (int i = 0; i < nodeList.size(); ++i)
    	{
    		NavNode aa = (NavNode)nodeList.get(i);
    		int x = (int)aa.getPosition().getX();
    		int y = (int)aa.getPosition().getY();
    		int dist = Math.abs(a[0]-x)+Math.abs(a[1]-y);
    		if (dist < mindist) {
    			mindist = dist;
    			aa1 = nodeList.get(i);
    		}
    		
    	}
    	return aa1;    	
    }
    
    public void addEdge(NavEdge a)
    {
    	((List<NavEdge>)edgeList).add(a);
    }
    
    public void removeEdge(NavEdge a)
    {
    	((List<NavEdge>)edgeList).remove(a);
    }
    
    public void addNode(NavNode a)
    {
    	((List<NavNode>)nodeList).add(a);
    }
    
    public void removeNode(NavNode a)
    {
    	((List<NavNode>)nodeList).remove(a);
    }
    
    public Set<N> getAdjacentNodes(String a)
    {
    	Adjacency<N> aa = adjacency.get(a);
    	return aa.getNeighbors();
    }
}
