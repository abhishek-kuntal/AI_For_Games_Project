package controllers.MyController;

//package controllers;

import core.*;
//import core.maze.*;
import core.maze.MazeInterface;
import core.utilities.*;

//import java.util.ArrayList;
//import java.util.Hashtable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//import java.util.Random;

//import competition.MsPacManController;


/*
 * Need to add more input in to NEAT, edibleTime
 * The arguments should be TreeNode[] roots;
 * New method StartState(TreeNode[] roots, GameStateInterface gs) simulate the game using
 * roots and randomly control Pacman
 * Expands is wrong, need to use dirParent
 */
 
/*
 * When the ghost arrive at a cross point , calculate where it's in the next time slice :D
 * Then after each cycle, estimate the position of the ghost again. If it's the same, contine the MCT thread, else start new.
 * We should save the current MCT move using array of move like tress[], then we can easily access it to simulate.
 * Only consider the current ghost whether it got into right position next time slice or not, not think about any else ghost 
 * Must use the dir2Parent for the starting Node (root)
 * How about start 8 thread for MCT, 2 heads of the tunnel
 * How about only a thread for MCT but with each turn the root may change => 2 MCT for each ghost
 */

/*
 * Root parallelization
 */

public class TreeNode implements Constants{
    static double epsilon = 1e-6;
    static double anpha = 0.6;
    //static double anpha1 = anpha*anpha;
    //static double anpha2 = (1 - anpha)*(1 - anpha);
    //static double anpha3 = anpha*(1 - anpha);
    static double beta = 0.2;
    public static int maxTick = 400;
    public static int KNNTick = 40;
    static Random rand = new Random();
    
    //GameStateInterface gs;
    JunctionNode node;
    //TreeNode parent;
    TreeNode[] children = null;
    int ghostId;
    int disNode; //The node number where the ghost suddenly reverse the direction
    int dir2Parent = NEUTRAL, dir4Parent = NEUTRAL;  // Direction to parent and direction from parent 
    //int[] dirs;
    
    double[] nVisits, totValue, min, pTotal, pTimeTotal, minTime;
    double[] minP, minPTime;
    
    double nVisitsx, totValuex, minx, pTotalx, pTimeTotalx, minTimex;
    double minPx, minPTimex;
    
    boolean expanded = false;
    private boolean rootPara = false;
    MazeInterface maze;
    JunctionNode[] juncs;

    // Node expanding should be put in constructor
    public TreeNode(int ghostId,JunctionNode node, int dir2Parent, int dir4Parent, int numThreads, MazeInterface maze, JunctionNode[] juncs) {
    	this.ghostId = ghostId;
    	this.node = node;
    	//this.parent = parent;
    	this.dir2Parent = dir2Parent;
    	this.dir4Parent = dir4Parent;
    	this.maze = maze;
    	this.juncs = juncs;
    	nVisits = new double[numThreads];
    	totValue = new double[numThreads];
    	min = new double[numThreads];
    	Arrays.fill(min, Double.MAX_VALUE);
    	minTime = new double[numThreads];
    	Arrays.fill(minTime, Double.MAX_VALUE);
    	pTotal = new double[numThreads];
    	pTimeTotal = new double[numThreads];
    	minP = new double[numThreads];
    	minPTime = new double[numThreads];
    }
        
    public TreeNode(JunctionNode node) {
    	this.node = node;
    	this.ghostId = 0;
    	//this.parent = parent;
    }
    
    public synchronized void expand() {
    	int ind = 0;
    	if (expanded) return;
    	if(dir2Parent == NEUTRAL) children = new TreeNode[node.node.adj.size()];
		else children = new TreeNode[node.node.adj.size() - 1];
    	node.expand(maze, juncs);  
    	for (int i=0; i<node.adjJunc.size(); i++){
        		if (node.dirs2Adj[i]!=dir2Parent){
        			children[ind] = new TreeNode(ghostId,node.adjJunc.get(i),node.dirs4Adj[i],node.dirs2Adj[i], nVisits.length, maze, juncs);
        			ind++;
        		}
    	}
    	expanded = true;
    }
    
    public boolean checkExpanded() {
    	return expanded;
    }
    
    static public SimGameState getStartState(List<TreeNode>[] trees, GameStateInterface gsi, int ghostId) {
    	SimGameState gs = new SimGameState(gsi);
    	//GameStateInterface gs = gsi.copy();
    	// Simulate until the selected node
    	// Trees is the linked list contain the tree node from start to the selected node
    	// gsi is the game state in the begin of a time slice :D
    	MCTDummyTeam ghostTeam = new MCTDummyTeam(trees);
    	SimMsPacManController agent = new BetterRandomAgent();
    	//agent = new RandomAgent();
    	//agent = new SimplePillEater();
    	//agent = new BetterController(gs);
    	// Just run the game simulator until the selected ghost reach a cross point
    	do {
    		gs.next(agent.getAction(gs), ghostTeam.getActions(gs));
    	} while (!gs.terminal() && gs.getGhosts()[ghostId].current.adj.size() < 3 && !gs.pillsCleared() && !gs.MsPacManDeath());
    	return gs;
    }
    
    static public SimGameState getStartState(List<TreeNode>[] trees, GameStateInterface gsi, int ghostId, SimMsPacManController pseudoAgent) {
    	SimGameState gs = new SimGameState(gsi);
    	MCTDummyTeam ghostTeam = new MCTDummyTeam(trees);
    	if (pseudoAgent == null)pseudoAgent =  new BetterRandomAgent();
    	// Just run the game simulator until the selected ghost reach a cross point
    	do {
    		gs.next(pseudoAgent.getAction(gs), ghostTeam.getActions(gs));
    	} while (!gs.terminal() && gs.getGhosts()[ghostId].current.adj.size() < 3 && !gs.pillsCleared() && !gs.MsPacManDeath());
    	return gs;
    }
    
    public LinkedList<TreeNode> selectedNodes(int id) {
    	if (id >= nVisits.length) return null;
    	LinkedList<TreeNode> ls = new LinkedList<TreeNode>();
    	TreeNode cur = this;
    	ls.add(cur);
    	while (!cur.isLeaf() && checkExpanded()) {
            cur = cur.select(id);
            // System.out.println("Adding: " + cur);
            if (cur == null) break; // Should be disable
            ls.add(cur);
        }
        return ls;
    }
    
    public TreeNode select(int id) {
        TreeNode selected = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        //double bestValue = Double.MIN_VALUE;
        //double bestValue = 0;
        if (children == null) {
        	System.out.println("NULL");
        	//expand();
        }
        for (TreeNode c : children) {
            if (c == null) {
            	System.out.println("C NULL WTF");
            	continue;
            }
            //double var = c.totValue2/(c.nVisits + epsilon) - sqr(c.totValue / (c.nVisits + epsilon)) +
            	//		Math.sqrt(2*Math.log(nVisits+1)/(c.nVisits + epsilon));
            //var = var < 1.0/4 ? var : 1.0/4;
        	double uctValue =
                    c.totValue[id] / (c.nVisits[id] + epsilon) +
                            Math.sqrt(Math.log(nVisits[id]+1) / (c.nVisits[id] + epsilon)) +
                            rand.nextDouble() * epsilon;
            // small random number to break ties randomly in unexpanded nodes
            // System.out.println("UCT value = " + uctValue);
            if (uctValue > bestValue) {
                selected = c;
                bestValue = uctValue;
            }
        }
        // System.out.println("Returning: " + selected);
        //if (selected == null) 
        	//System.out.println("OK!!!");
        return selected;
    }
    
    public List<TreeNode> selectedNodesFinal() {
    	List<TreeNode> ls = new LinkedList<TreeNode>();
    	TreeNode cur = this;
    	ls.add(cur);
    	while (!cur.isLeaf() && checkExpanded()) {
    		cur = cur.selectFinal();
            if (cur == null) break;
    		ls.add(cur);
        }
        return ls;
    }
    
    public TreeNode selectFinal() {
    	TreeNode selected = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        //double bestValue = Double.MIN_VALUE;
        //double bestValue = 0;
        if (children == null) {
        	System.out.println("NULL");
        	//expand();
        }
        for (TreeNode c : children) {
        	if (c == null) {
            	System.out.println("C FINAL NULL");
            	continue;
            }
        	      	
        	double tmp = c.totValuex / (c.nVisitsx + epsilon);
        	if (tmp > bestValue) {
                selected = c;
                bestValue = tmp;
            }
        	else if (tmp == bestValue && selected != null){
        		if (c.nVisitsx + rand.nextDouble() > selected.nVisitsx + rand.nextDouble())
        			selected = c;
        	}
        }
        return selected;
    }
    
    public boolean isLeaf() {
        return children == null;
    }

    public SimGameState simGame(List<TreeNode>[] trees, SimGameState gs) {
    	//SimGameState gs = new SimGameState(gsi);
    	// need to call getStartState then return the game's value;
        MCTDummyTeam ghostTeam = new MCTDummyTeam(trees);
        SimMsPacManController agent = new BetterRandomAgent(); //It should be sth we obtain from NEAT
        //agent = new RandomAgent();
        //agent = new SimplePillEater();
        //agent = new BetterController(gs);
        
        int tick = 0;
    	while (!gs.terminal() && !gs.MsPacManDeath() && !gs.pillsCleared() && (tick++)<maxTick) {
    		gs.next(agent.getAction(gs), ghostTeam.getActions(gs));
    	}
    	disNode = ghostTeam.treeCur[ghostId];
    	return gs;
    }
    
    public SimGameState simGame(List<TreeNode>[] trees, SimGameState gs, SimMsPacManController pseudoAgent) {
    	//SimGameState gs = new SimGameState(gsi);
    	// need to call getStartState then return the game's value;
        MCTDummyTeam ghostTeam = new MCTDummyTeam(trees);
        if (pseudoAgent == null)pseudoAgent =  new BetterRandomAgent();
        //AgentInterface agent = new BetterRandomAgent(); //It should be sth we obtain from NEAT
        int tick = 0;
    	while (!gs.terminal() && !gs.MsPacManDeath() && !gs.pillsCleared() && (tick++)<maxTick) {
    		if (tick > KNNTick && !(pseudoAgent instanceof BetterRandomAgent)) pseudoAgent = new BetterRandomAgent();
    		gs.next(pseudoAgent.getAction(gs), ghostTeam.getActions(gs));
    	}
    	disNode = ghostTeam.treeCur[ghostId];
    	//if (disNode < 0) throw new RuntimeException("abc");
    	return gs;
    }
    /*
    public synchronized void updateStats(double value, double time, double dist) {
        nVisits++;
        min = (min < value)? min : value;
        minTime = (minTime < time)? minTime : time;
        minD = (minD < dist)? minD : dist;
        pTotal += (1/value);
        pTimeTotal += (1/time);
        pDTotal += (1/dist);
        //avgVal = min*pTotal/(nVisits + epsilon);
        totValue = anpha*min*pTotal + (1-anpha)*minTime*pTimeTotal;
        if (isLeaf()) return;
        for (TreeNode c : children){
        	synchronized (c) {
        		if (c == null) continue;
        		c.min = min;
        		c.minTime = minTime;
        		c.minD = minD;
			}
        }
        //Nothing heere
        avg  += (value - avg)/nVisits;
        tAvg  += (time - tAvg)/nVisits;
        dif += (Math.pow(value - avg,2));
        tDif += (Math.pow(time - tAvg,2));
        if (dif != 0 && nVisits > 1) totValue += (avg - value) / Math.sqrt((dif)/(nVisits-1));
        if (tDif != 0 && nVisits > 1) totValue += (tAvg - time) / Math.sqrt((tDif)/(nVisits-1));
        if (isLeaf()) return;
        for (TreeNode c : children){
        	synchronized(c){
        		if (c == null) continue;
        		c.difP += (Math.pow(avg - c.avg,2));
        		c.tDifP += (Math.pow(tAvg - c.tAvg,2));
        		c.difPAvg += (avg - c.avg - c.difPAvg) / (c.nVisits+1);
        		c.tDifPAvg += (tAvg - c.tAvg - c.tDifPAvg) / (c.nVisits+1);
        		if (c.difPAvg != 0 && c.nVisits != 0) c.totValue += (avg - c.avg - c.difPAvg) / Math.sqrt((c.difP) / c.nVisits);
        		if (c.tDifPAvg != 0 && c.nVisits != 0) c.totValue += (tAvg - c.tAvg - c.tDifPAvg) / Math.sqrt((c.tDifP) / c.nVisits);
        	}
        }
        
    }
	*/
    
    public synchronized void updateStats(double value, double time, int id, TreeNode noUpdate) {
        nVisits[id]++;
        nVisitsx++;
        min[id] = (min[id] < value)? min[id] : value;
        minTime[id] = (minTime[id] < time)? minTime[id] : time;
        //minD = (minD < dist)? minD : dist;
        if (noUpdate != this){
        	pTotal[id] += (1/value);
        	pTotalx += (1/value);
        	pTimeTotal[id] += (1/time);
        	pTimeTotalx += (1/time);
        	//pTotal2 += sqr(1/value);
        	//pTimeTotal2 += sqr(1/time);
        	//pMix2 += 2/(value*time);
        	//pDTotal += (1/dist);
        	//avgVal = min*pTotal/(nVisits + epsilon);
        	//totValue = anpha*minP*pTotal + (1-anpha)/2*minPTime*pTimeTotal + (1-anpha)/2*minPD*pDTotal;
        	totValuex = anpha*minPx*pTotalx + (1-anpha)*minPTimex*pTimeTotalx;
        	totValue[id] = anpha*minP[id]*pTotal[id] + (1-anpha)*minPTime[id]*pTimeTotal[id];
        	//totValue2 = anpha1*sqr(minP)*pTotal2 + anpha2*sqr(minPTime)*pTimeTotal2 + anpha3*minP*minPTime*pMix2;
        }
        if (isLeaf() || !checkExpanded()) return;
        for (TreeNode c : children){
        	synchronized (c) {
        		c.minP = min;
        		c.minPx = min[id];
        		c.minPTime = minTime;
        		c.minPTimex = minTime[id];
        		//c.minPD = minD;
			}
        }
        /*
        avg  += (value - avg)/nVisits;
        tAvg  += (time - tAvg)/nVisits;
        dif += (Math.pow(value - avg,2));
        tDif += (Math.pow(time - tAvg,2));
        if (dif != 0 && nVisits > 1) totValue += (avg - value) / Math.sqrt((dif)/(nVisits-1));
        if (tDif != 0 && nVisits > 1) totValue += (tAvg - time) / Math.sqrt((tDif)/(nVisits-1));
        if (isLeaf()) return;
        for (TreeNode c : children){
        	synchronized(c){
        		if (c == null) continue;
        		c.difP += (Math.pow(avg - c.avg,2));
        		c.tDifP += (Math.pow(tAvg - c.tAvg,2));
        		c.difPAvg += (avg - c.avg - c.difPAvg) / (c.nVisits+1);
        		c.tDifPAvg += (tAvg - c.tAvg - c.tDifPAvg) / (c.nVisits+1);
        		if (c.difPAvg != 0 && c.nVisits != 0) c.totValue += (avg - c.avg - c.difPAvg) / Math.sqrt((c.difP) / c.nVisits);
        		if (c.tDifPAvg != 0 && c.nVisits != 0) c.totValue += (tAvg - c.tAvg - c.tDifPAvg) / Math.sqrt((c.tDifP) / c.nVisits);
        	}
        }
        */
    }

    
    public int arity() {
        return children == null ? 0 : children.length;
    }
    
    private static double sqr (double x){
    	return  x*x;
    }
    
    public String toString() {
    	String str = node.toString() ;
    	for (int i=0; i < arity(); i++){
    		str += " (";
    		str += children[i].toString();
    		str += ")";
    	}
    	return str;
    }
    
}