package controllers.examples;

//package controllers;

import java.util.ArrayList;
import controllers.*;
import core.*;
import core.maze.*;

public class JunctionE implements NodeScore {
	
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
		double d1=10000,d2=20000;
		Node junction, j1 = null, j2 = null;
		
		if (gs.getMaze() != maze) setMaze(gs.getMaze());
		for(int j = 0; j < junctions.size(); j++){
			junction = junctions.get(j);
			if(maze.dist(gs.getPacman().current,junction)<d1){
				d2=d1;
				d1=maze.dist(gs.getPacman().current,junction);
				if(j1!=null)j2=j1;
				j1=junction;
			}
			else if(maze.dist(gs.getPacman().current,junction)<d2){
				d2 = maze.dist(gs.getPacman().current,junction);
				j2 = junction;
			}
		}
		if(maze.dist(node,gs.getPacman().current)>=40)return sqr(node.x - j1.x) + sqr(node.y - j1.y);
		else{ return sqr(node.x - gs.getPacman().current.x) + sqr(node.y - gs.getPacman().current.y);}
	}
	
	private static double sqr(double x) {
        return x * x;
    }

}
