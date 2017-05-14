package controllers.MyController;

//package controllers;

//import competition.*;
import core.*;
import core.utilities.Utilities;
//import core.utilities.Utilities;
//import controllers.*;

import java.util.*;


public class MCTDummyTeam implements SimGhostsController, Constants {
	
	static Random rand = new Random();
	//public static boolean flag = false; 
	List<TreeNode>[] trees ;
	int[] treeCur;
	private final int[] dirs;
    //private final NodeScore[] scorers;
    //private final ArrayList<Node> options;
	Node pres[];
	//private int count = 0;
	
	private interface NodeScore {
		double score(SimGameState gs, Node node);
	}
	
	private final NodeScore[] scorers = new NodeScore[] {
			new NodeScore(){public double score(SimGameState gs, Node node){return PathScore(gs, node);}},
			new NodeScore(){public double score(SimGameState gs, Node node){return EuclideanScore(gs, node);}},
			new NodeScore(){public double score(SimGameState gs, Node node){return MahattanScore(gs, node);}},
			new NodeScore(){public double score(SimGameState gs, Node node){return PathScore(gs, node);}},
	};
	
	double PathScore(SimGameState gs, Node node) {
        return gs.getMaze().dist(gs.getPacman().current, node);
    }
	
	double EuclideanScore(SimGameState gs, Node node) {
        Node pac = gs.getPacman().current;
        return Math.sqrt(sqr(node.x - pac.x) + sqr(node.y - pac.y));
    }
	
	private static double sqr(double x) {
        return x * x;
    }
	
	double MahattanScore(SimGameState gs, Node node) {
		Node pac = gs.getPacman().current;
	    return Math.abs(node.x - pac.x) + Math.abs(node.y - pac.y);
	}
	
	public MCTDummyTeam(List<TreeNode>[] trees) {
		this.trees = trees;
		dirs = new int[nGhosts];
		treeCur = new int[nGhosts];
		for (int i = 0; i < nGhosts; i++) treeCur[i] = 0;
		pres = new Node[nGhosts];
		/*
		options = new ArrayList<Node>();
        scorers = new NodeScore[]{
                new PathScore(), new EuclideanScore(),
                new ManhattanScore(), new PathScore(),
        };
        */
	}
	
	@Override
	public int[] getActions(SimGameState gs) {
		
		for (int i=0; i<nGhosts; i++) {
			dirs[i] = NEUTRAL;
			
			/*if (i == MyGhostsTeamBot.rbGhost){
				//dirs[i] = getDefaultDir(gs, i);
				Node pac = gs.getPacman().current;
				SimGhosts curGhost = gs.getGhosts()[i];
				boolean inverse = false;
				Node target = pac;
				Node pre = null;
				for (Node n : pac.adj){
					if (Utilities.getWrappedDirection(n, pac, gs.getMaze()) == gs.getPacman().curDir){
						pre = n;
						break;
					}
				}
				if (pre != null) 
					while (target.adj.size() < 3) {
						for(Node n : target.adj) if (n != pre) { pre = target; target = n; break; }
						if (!inverse){
							// If there's a ghost with the same direction as MsPacman, chose other target node 
							for (int j = 0; j < nGhosts; j++) if (j != i && target == gs.getGhosts()[j].current && pre != gs.getGhosts()[j].previous){
								Node tmp = target;
								target = pre;
								pre = tmp;
								inverse = true;
							}
						}
					}
				ArrayList<Node> options = new ArrayList<Node>();
				for (Node n : curGhost.current.adj) if(n != curGhost.previous) options.add(n);
				if (pre != null && !curGhost.edible() || curGhost.edibleTime < gs.getMaze().dist(curGhost.current, gs.getPacman().current) / 3.0 ){
					double best = Double.MAX_VALUE;
					Node sel = null;
					for (Node n : options){
						if (gs.getMaze().dist(target, n) + gs.getMaze().dist(pre, n) < best) {
							best = gs.getMaze().dist(target, n) + gs.getMaze().dist(pre, n);
							sel = n;
						}
					}
					dirs[i] = Utilities.getWrappedDirection(curGhost.current, sel, gs.getMaze());
				}
				else dirs[i] = getMaxDir(options, curGhost.current, scorers[i], gs);
				continue;
			}*/
			
			if ( i == MyGhostsTeamBot.rbGhost ){
				continue;
			}
						
			if (trees[i] == null || treeCur[i] < 0 || trees[i].size() <= treeCur[i] + 1) {
				dirs[i] = NEUTRAL;
			} else {
				try {
					if (gs.getGhosts()[i].current == trees[i].get(treeCur[i] + 1).node.node) {
						treeCur[i]++;
						// if(flag) if (i == 3) System.out.println(i + "\t" +
						// treeCur[i] + "\t" + Thread.currentThread() + "\t"
						// +(count));
					}
					// If the ghost reverse direction, move randomly
					if (pres[i] == gs.getGhosts()[i].current || gs.getGhosts()[i].returning()) {
						dirs[i] = rand.nextInt(dx.length);
						//treeCur[i] = trees[i].size();
						treeCur[i] = -treeCur[i] - 1;
					}
					else if (trees[i].size() > treeCur[i] + 1) dirs[i] = trees[i].get(treeCur[i] + 1).dir4Parent;
					pres[i] = gs.getGhosts()[i].previous;
				} catch (Exception e){}
			}
		}
		//if (flag) System.out.println( "*\t" + dirs[3] + "\t" + Thread.currentThread() + "\t" +(count++));
		return dirs;
	}
	
	
	private int getMaxDir(ArrayList<Node> nodes, Node cur, NodeScore f, SimGameState gs){
		double best = Double.MIN_VALUE;
        // selected current
        Node sel = null;
        for (Node n : nodes) {
            if (f.score(gs, n) > best) {
                best = f.score(gs, n);
                sel = n;
            }
        }
        if (sel == null) return NEUTRAL;
        return Utilities.getWrappedDirection(cur,sel,gs.getMaze());
	}
	
}
