package controllers.examples;

//package controllers;

import java.util.ArrayList;
import controllers.*;
import core.*;
import core.maze.*;

public class JunctionM implements NodeScore {

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
		MsPacMan pacman = gs.getPacman();
		
		if (gs.getMaze() != maze) setMaze(gs.getMaze());
		for(int j = 0; j < junctions.size(); j++){
			junction = junctions.get(j);
			if(maze.dist(pacman.current,junction) < d1){
				d2=d1;
				d1=maze.dist(pacman.current,junction);
				if(j1!=null)j2=j1;
				j1=junction;
			}
			else if(maze.dist(pacman.current, junction)<d2){
				d2=maze.dist(pacman.current, junction);
				j2=junction;
			}
		}
    
		//if(j1!=null)System.out.println(j1+" "+j2);
		if(maze.dist(node,pacman.current)>=40)return Math.abs(node.x - j1.x) + Math.abs(node.y - j1.y);
		else{ return Math.abs(node.x - pacman.current.x) +  Math.abs(node.y - pacman.current.y);}
	}

}
