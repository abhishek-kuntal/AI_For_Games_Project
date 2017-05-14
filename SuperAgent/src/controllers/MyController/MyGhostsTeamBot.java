package controllers.MyController;

//package controllers;

/*
 * Should only consider ghosts move until reach the leaf node.
 * No need to make the other ghosts move randomly => Stop simulation when all the ghosts has reached the leaf node?
 * Or after all ghosts have reached the leaf nodes a certain amount of time?
 * With the draw near mechanism, the only thing need to concern are parameters, the time to finish simulation.
 * this will be very effective in tight situations  
 */

import competition.GhostsController;
import core.*;
import core.maze.*;
import core.utilities.*;
import controllers.*;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.*;


/*
 * Change from using thread to non-thread
 * Using concurent: Run + stop, the others run + stop
 * Simulation stop condition put in this function
 * til 07/16
 */
public class MyGhostsTeamBot implements Constants {
	
	public static int maxData = 21;
    public static CopyOnWriteArrayList<Integer> rbGhost= new CopyOnWriteArrayList<Integer>();
	protected MazeInterface maze;
	protected JunctionNode[] juncs;
	protected List<TreeNode>[] roots; //They maybe become next start point in the next time slice of each ghost
	//int[] rootId; //The Idex of each root in list of available roots for each ghost
	protected List<TreeNode>[] trees; //The move of each ghost
	protected TreeNode nextTNode[]; //An array store the next cross-point tree node the ghost will visit
	protected MCTThread[] mctThread;
	protected Thread mainThread;
	protected NodeScore[] scorers;
	protected PacInfo infoCollector;
    protected LinkedList<VectorND> sampleData, procData;
    protected GameStateInterface preGS = null, pretGS = null;
    protected Node expectPac;
    protected double trustLvl = 1.0;
    protected int allTimes, falseTimes;
	protected boolean followPill = false;
    protected SuperAgent sa;
    // Make more thread to increase performance
    // At which condition the simulation's finished? Make my own simGame
	

	protected boolean checkRestarted(GameStateInterface pre, GameStateInterface cur){
		int pLives = pre.getLivesRemaining();
		int cLives = cur.getLivesRemaining();
		if (pre.getScore() >= 10000) pLives --;
		if (cur.getScore() >= 10000) cLives --;
		return (pLives!=cLives);
	}
	
	protected void setMaze(MazeInterface maze) {
		this.maze = maze;
		rbGhost.clear();
		followPill = false;
		//setup junctions 
		juncs = new JunctionNode[maze.getMap().size()];
		for (Node n : maze.getMap()){
			if (n.adj.size() > 2 || n.equals(maze.ghostStart())) juncs[n.nodeIndex] = new JunctionNode(n);
		}
		
	}
	
	protected int getMaxDir(ArrayList<Node> nodes, Node cur, NodeScore f, GameStateInterface gs){
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
	
	@SuppressWarnings("unchecked")
	public void resetAll() {
		roots = (List<TreeNode>[]) new List[nGhosts];
		trees = (List<TreeNode>[]) new List[nGhosts];
		//rootId = new int[nGhosts];
		nextTNode = new TreeNode[nGhosts];
		for(int i = 0; i < nGhosts; i++){
			if (mctThread[i] != null) mctThread[i].die();
		}
		mctThread = new MCTThread[nGhosts*4];
		sampleData = null;
        procData = null;
        expectPac = null;
        preGS = null;
    }
    protected static Boolean checkRBGhostIndex(int ghostIndex){
        if(rbGhost == null | rbGhost.size() < 1)
		{
			return false;
		}
		//#List<Integer> rbG= new ArrayList<>();
		//bG.addAll(rbGhost);
		//Iterator<Integer> iterator =
		for(Integer j: rbGhost){
            if(j==ghostIndex)
                return true;
        }
        return false;
    }

	protected int getDefaultDir (GameStateInterface gs, int ghostId){
		int dir;
		Ghosts curGhost = gs.getGhosts()[ghostId];
		ArrayList<Node> options = new ArrayList<Node>();
        for (Node n : curGhost.current.adj) {
            // turning back is not a valid option
            if (!n.equals(curGhost.previous)) options.add(n);
        }
        if (!curGhost.edible() || curGhost.edibleTime < maze.dist(curGhost.current, gs.getPacman().current) / 3.0 )
        	dir = Utilities.getMinDir(options, curGhost.current, scorers[ghostId], gs);
        else 
        	dir = getMaxDir(options, curGhost.current, scorers[ghostId], gs);
        return dir;
	}
}
