package controllers.examples;

//package controllers;

import java.util.ArrayList;
import controllers.*;
import core.*;
import core.maze.*;

public class RiskScore implements NodeScore {
	
	ArrayList<Node> junctions;
	MazeInterface maze;
	
	private void setMaze(MazeInterface maze) {
        this.maze = maze;
        // set up the junctions
        junctions = new ArrayList<Node>();
        for (Node n : maze.getMap()) {
            if (n.adj.size() > 2) {
                junctions.add(n);
            }
        }
    }
	
	@Override
	public double score(GameStateInterface gs, Node node) {
		double m=0, d=0, d1=10000, d2=20000;
    	Node junction, j1 = null, j2 = null;
        if (gs.getMaze() != maze) setMaze(gs.getMaze());    	
    	for(int j = 0; j < junctions.size(); j++){
    		d = 0;
    		junction = junctions.get(j);
    		if(maze.dist(gs.getPacman().current,junction)<=60){
    			for(Ghosts g : gs.getGhosts()){
    				if(g != null){
    					d = d + maze.dist(junction,g.current);
    				}
    			}
    			if(m < d){j1 = junction; m = d;}
    		}
    	}
    	return maze.dist(node,j1); 
	}

}
