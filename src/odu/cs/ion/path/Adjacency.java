package odu.cs.ion.path;

import java.util.*;

public class Adjacency<N extends Node>{
    protected N node;
    protected Set<N> neighbors;
    
    public Set<N> getNeighbors()
    {
    	return neighbors;
    }
    
    public Adjacency(N a, Set<N> b)
    {
    	node = a;
    	neighbors = b;
    }
    
    public Adjacency(N a)
    {
    	node = a;
    	neighbors = new HashSet<N>();
    }
    
    public void addNeighbor(N a)
    {
    	neighbors.add(a);
    }
}