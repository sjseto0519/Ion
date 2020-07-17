package odu.cs.ion.path;

import java.util.*;

public class AStarNodeComparator implements Comparator<AStarNode> {

    public int compare(AStarNode first, AStarNode second) {
        if(first.getF() < second.getF()){
            return -1;
        }else if(first.getF() > second.getF()){
            return 1;
        }else{
            return 0;
        }
    }
}