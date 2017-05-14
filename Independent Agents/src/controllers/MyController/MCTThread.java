package controllers.MyController;

//package controllers;

import competition.*;
import core.*;
import core.maze.*;
import core.utilities.Utilities;

//import java.lang.reflect.Method;
import java.util.*;

public class MCTThread extends Thread implements Constants{
	private static final int maxVisitTimes = 1000;
	private static final int visitThres = 5;
	private static final double dThresNormal = 60;
	private static final int penaltyScore = 500, penaltyTime = 100;
	private static final int penaltyPathScore = 500;
//	private static Random rand = new Random();
	
	private interface NodeScore {
		double score(SimGameState gs, Node node);
	}
	
	private final NodeScore[] scorers = new NodeScore[] {
			new NodeScore(){public double score(SimGameState gs, Node node){return PathScore(gs, node);}},
			new NodeScore(){public double score(SimGameState gs, Node node){return EuclideanScore(gs, node);}},
			new NodeScore(){public double score(SimGameState gs, Node node){return MahattanScore(gs, node);}},
			new NodeScore(){public double score(SimGameState gs, Node node){return PathScore(gs, node);}},
	};
	//GameStateInterface gsi;
	TreeNode root;
	MyGhostsTeamBot gTeam;
	KNNAgent pseudoAgent = null;
	final int gId;
	final int numThreads;
	
	boolean alive;
	
	public MCTThread(TreeNode root, int gId, KNNAgent pseudoAgent, MyGhostsTeamBot gTeam, int numThreads) {
		//this.gsi = gTeam.pretGS;
		this.root = root;
		this.gTeam = gTeam;
		this.gId = gId;
		this.numThreads = numThreads;
		this.pseudoAgent = pseudoAgent;
		alive = true;
	}
	
	public synchronized void die() {
		alive = false;
	}
	
	public void run() {
		while(alive){
			boolean stop = true;
			for (int i = 0; i < numThreads; i++) {
				stop &= runThread(i);
				if (!alive) break;
			}
			if (stop) break;
	    }
		//System.out.println("MCTThread " + Thread.currentThread().getName());
	}
	
	boolean runThread(int threadId) {
		//ElapsedTimer t = new ElapsedTimer();
		if (root == null || root.nVisits[threadId] >= maxVisitTimes) 
			return true;
		if (!gTeam.mainThread.isAlive()) {
			return true;
		}
		//GameStateInterface gs = gsi.copy();
		SimGameState gs = TreeNode.getStartState(gTeam.trees, gTeam.pretGS.copy(), gId, pseudoAgent);
		if (root.node.node != gs.getGhosts()[gId].current || root.dir2Parent != (gs.getGhosts()[gId].curDir + 2) % 4) {
			root.nVisitsx++;
			root.nVisits[threadId]++;
			return false;
		}
		if (gs.MsPacManDeath() || gs.terminal() || gs.getPills().isEmpty() && gs.getPowers().isEmpty()){
			root.nVisitsx++;
			root.nVisits[threadId]++;
			return false;
		}
		LinkedList<TreeNode> visited = root.selectedNodes(threadId);
		TreeNode lastNode = visited.get(visited.size()-1); 
		if (lastNode == null) return false;
		if (lastNode.nVisits[threadId] > visitThres || lastNode == root){
			lastNode.expand();
			visited.add(lastNode.select(threadId));
		}
		//If this path is already having a ghost occured, raise penalty
		
		boolean penaltyPath = false;
		if (visited.size() > 1){
			Node next = null, pre = gs.getGhosts()[gId].current;
			SimGhosts curGhost = gs.getGhosts()[gId];
			int dir = visited.get(1).dir4Parent;
			for (Node n : gs.getGhosts()[gId].current.adj){
				if (Utilities.getWrappedDirection(pre, n, gs.getMaze()) == dir){
					next = n;
					break;
				}
			}
			while (next != null && next.adj.size() < 3){
				for (int k = 0; k < nGhosts; k++){
					if (k == MyGhostsTeamBot.rbGhost) continue;
					SimGhosts g = gs.getGhosts()[k];
					//if (g.current == next && g.previous == pre && !g.returning()) {
					//if (g.current == next && g.previous == pre && !g.returning() && !(curGhost.edible() ^ g.edible())) {
					if (g.current == next && g.previous == pre && !g.returning() && !g.edible()) {
						penaltyPath = true;
						break;
					}
				}
				if (penaltyPath) break;
				for (Node node : next.adj) if(node != pre){pre = next; next = node; break;}
			}
		}
		
		int i;
        List<TreeNode>[] trees = (List<TreeNode>[]) new List[nGhosts];
        for (i = 0; i < nGhosts; i++){
        	if(i == gId) trees[i] = visited;
        	else {
        		trees[i] = gTeam.trees[i];
        		//trees[i] = null;
        	}
        }
        if(pseudoAgent != null) pseudoAgent.reset();
        SimGameState result = root.simGame(trees, gs, pseudoAgent);
        double value = result.getScore();
        int time = result.getTotalGameTicks();
        //Apply penalty
        if (penaltyPath){
        	value += penaltyPathScore;
        	time += penaltyTime;
        }
        
        //double dist = 1;
        double sc = scorers[gId].score(result, result.getGhosts()[gId].current);
        if (result.getGhosts()[gId].edible()) {
        	double safeDist = result.getGhosts()[gId].edibleTime * 3;
        	if (sc < safeDist){
        		value += penaltyScore;
	       		time += penaltyTime;
        	}
        }
        else if (!result.getGhosts()[gId].returning() && sc > dThresNormal){
        	value += penaltyScore;
        	time += penaltyTime;
        }
        
        //dist = 1;
        i = root.disNode < 0? -root.disNode - 1 : visited.size();
        //int j = i > 3 ? 1 : 0;
        //j = i/2 - 1;
        //ListIterator<TreeNode> liVisited = visited.listIterator(j);
        for (TreeNode node : visited) {
            // would need extra logic for n-player game
            // System.out.println(node);
            node.updateStats(value, time, threadId, root);
            if (root.disNode < 0 && (i--) < 0) break;
        }
        //System.out.println (t);
        /*
        while (liVisited.hasPrevious()){
        	liVisited.previous().updateStats(root);
        }
        */
        return false;
	}
	
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
}
