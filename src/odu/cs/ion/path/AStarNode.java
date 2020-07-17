package odu.cs.ion.path;

public class AStarNode {

    private NavNode node;

    //used to construct the path after the search is done
    private AStarNode cameFrom;

    // Distance from source along optimal path
    private double g;

    // Heuristic estimate of distance from the current node to the target node
    private double h;
    
    public double getF(){
      return g + h;
    }
    
    public AStarNode getCameFrom()
    {
    	return cameFrom;
    }
    
    public void setCameFrom(AStarNode a)
    {
    	cameFrom = a;
    }
    
    public AStarNode(NavNode node, double g, double h)
    {
    	this.node = node;
    	this.g = g;
    	this.h = h;
    }
    
    public void setG(double a)
    {
    	g = a;
    }
    
    public void setH(double a)
    {
    	h = a;
    }
    
    public NavNode getNode()
    {
    	return node;
    }
    
    public String getId()
    {
    	return node.getId();
    }
    
    public double getG()
    {
    	return g;
    }
    
    public double getH()
    {
    	return h;
    }
}