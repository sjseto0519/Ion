package odu.cs.ion.path;

import java.util.*;

public class AStarAlgorithm {
 

   public static List<NavNode> search(NavGraph graph, NavNode source, NavNode target) {
        Map<String, AStarNode> openSet = new HashMap<String, AStarNode>();
        PriorityQueue<AStarNode> pQueue = new PriorityQueue(20, new AStarNodeComparator());
        Map<String, AStarNode> closeSet = new HashMap<String, AStarNode>();
        AStarNode start = new AStarNode(source, 0.0d, graph.calcManhattanDistance(source, target));
        openSet.put(source.getId(), start);
        pQueue.add(start);

        AStarNode goal = null;
        while(openSet.size() > 0){
            AStarNode x = pQueue.poll();
            openSet.remove(x.getId());
            if(x.getId().equals(target.getId())){
                //found
                //if(log.isDebugEnabled()){
                //    log.debug("Found target node " + x.getId());
                //}
                goal = x;
                break;
            }else{
                //if(log.isDebugEnabled()){
                //    log.debug("Search for node " + x.getId());
                //}
                closeSet.put(x.getId(), x);
                Set<NavNode> neighbors = graph.getAdjacentNodes(x.getId());
                for (NavNode neighbor : neighbors) {
                    AStarNode visited = closeSet.get(neighbor.getId());
                    if (visited == null) {
                        double g = x.getG() + graph.calcManhattanDistance(x.getNode(), neighbor);
                        AStarNode n = openSet.get(neighbor.getId());

                        if (n == null) {
                            //not in the open set
                            n = new AStarNode(neighbor, g, graph.calcManhattanDistance(neighbor, target));
                            n.setCameFrom(x);
                            openSet.put(neighbor.getId(), n);
                            pQueue.add(n);
                        } else if (g < n.getG()) {
                            //Have a better route to the current node, change its parent
                            n.setCameFrom(x);
                            n.setG(g);
                            n.setH(graph.calcManhattanDistance(neighbor, target));
                        }
                    }
                }
            }
        }

        //after found the target, start to construct the path 
        if(goal != null){
            Stack<NavNode> stack = new Stack<NavNode>();
            List<NavNode> list = new ArrayList<NavNode>();
            stack.push(goal.getNode());
            AStarNode parent = goal.getCameFrom();
            while(parent != null){
                stack.push(parent.getNode());
                parent = parent.getCameFrom();
            }
            //if (log.isDebugEnabled()) {
            //    log.debug("Constructing search path: ");
            //}
            while(stack.size() > 0){
                //if (log.isDebugEnabled()) {
                //    log.debug("\t" + stack.peek().getId());
                //}
                list.add(stack.pop());
            }
            return list;
        }
        
        return null;  
    }
}