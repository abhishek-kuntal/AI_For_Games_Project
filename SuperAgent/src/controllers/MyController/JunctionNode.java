package controllers.MyController;

//package controllers;

import core.*;
import core.maze.MazeInterface;
import core.utilities.Utilities;

import java.util.*;

public class JunctionNode {
	
	public ArrayList<JunctionNode> adjJunc;
	int[] dirs2Adj;
	int[] dirs4Adj;
	
	boolean initialized = false;  
	
	public Node node;
		
	public JunctionNode(Node node){
		this.node = node;
		adjJunc = new ArrayList<JunctionNode>();
		dirs2Adj = new int[node.adj.size()];
		dirs4Adj = new int[node.adj.size()];
	}
	
	public synchronized void expand(MazeInterface maze, JunctionNode[] juncs) {
		if (initialized) return;
		initialized = true;
		if (this.adjJunc.size() == 0) {
        	// if the node's adj aren't created yet
        	// create and add into arraylist
    			for (int i=0; i < this.node.adj.size(); i++){
        			Node pre = this.node;
        			Node cur = this.node.adj.get(i);
        			int dir2Adj = Utilities.getWrappedDirection(pre, cur, maze);
        			while (cur.adj.size() == 2) {
        				for (Node next : cur.adj){
    						if (!pre.equals(next)){
    							pre = cur;
    							cur = next;
    							break;
    						}
        				}
        			}
        			if (cur.adj.size() > 2) {
        				this.adjJunc.add(juncs[cur.nodeIndex]);
        				this.dirs2Adj[i] =  dir2Adj;
        				this.dirs4Adj[i] = Utilities.getWrappedDirection(cur, pre, maze);
        			}
    			}
		}
	}
	
	public boolean initialized(){
		return initialized;
	}
	
	public String toString(){
		return node.toString();
	}
	
}
