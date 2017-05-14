package controllers.MyController;

//package controllers;

import competition.*;
import controllers.*;
import core.*;
import core.maze.*;
import core.utilities.*;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;

public class BetterRandomAgent implements SimMsPacManController, Constants {
    static final int maxRange = 16;
    static Random rand = new Random();
	//Node prev;
    public int getAction(SimGameState gs) {
//        try {Thread.sleep(2000);}
//        catch(Exception e) {}
        Node cur = gs.getPacman().current;
        Node tmp = cur;
        ArrayList<Node> possibles = new ArrayList<Node>();
        Node prev = null;
        Node tprev = null;
        for (Node n : cur.adj){
        	if (Utilities.getWrappedDirection(n, cur, gs.getMaze()) == gs.getPacman().curDir){
        		prev = tprev = n;
        		break;
        	}
        }
        if (prev != null){
			for (int i = 0; i < maxRange; i++){
				if (tmp.adj.size() > 2) break;
				for (Node n : tmp.adj) {
					if (n != tprev){
						tprev = tmp;
						tmp = n;
						break;
					}
				}
				for (int j = 0; j < nGhosts; j++){
					if (gs.getGhosts()[j].current == tmp) {
						if (gs.getGhosts()[j].edible() && gs.getGhosts()[j].previous == tprev) {
							for (Node n : cur.adj) {
								if (n != prev){
									return Utilities.getWrappedDirection(cur, n, gs.getMaze());
								}
							}
						}
						else return (gs.getPacman().curDir + 2) % 4;
					}
				}
				if (tmp.powerIndex >= 0 && gs.getPowers().get(tmp.powerIndex)) {
					for (Node n : cur.adj) {
						if (n != prev){
							return Utilities.getWrappedDirection(cur, n, gs.getMaze());
						}
					}
				}
			}
		}
        if (cur.adj.size() > 2 && prev != null) possibles.add(prev);
        if (cur.adj.size() == 2 && prev != null) 
        	if (cur.adj.get(0).x != cur.adj.get(1).x && cur.adj.get(0).y != cur.adj.get(1).y) possibles.add(prev);
        for (Node n : cur.adj) {
            if (!n.equals(prev)) possibles.add(n);
        }
        Node next = possibles.get(rand.nextInt(possibles.size()));
        int action =  Utilities.getWrappedDirection(cur, next, gs.getMaze());
        //if(action == NEUTRAL) throw new RuntimeException("WTF");
        //prev = cur;
        return action;
    }
    /*
    public synchronized void setPreNode(Node prev){
    	this.prev = prev;
    }
    */
}

