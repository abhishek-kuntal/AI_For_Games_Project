
package controllers.MyController;

import competition.GhostsController;
import controllers.EuclideanScore;
import controllers.ManhattanScore;
import controllers.MyController.*;
import controllers.NodeScore;
import controllers.PathScore;
import core.Constants;
import core.GameStateInterface;
import core.Ghosts;
import core.Node;
import core.maze.MazeInterface;
import core.utilities.Utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by abhishekkuntal on 5/2/17.
 */


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

        import java.util.*;

        import utilities.StatSummary;


/*
 * Change from using thread to non-thread
 * Using concurent: Run + stop, the others run + stop
 * Simulation stop condition put in this function
 * til 07/16
 */
public class ModifiedGhostTeamBot implements GhostsController, Constants {

    public static int maxData = 21;
    public static int rbGhost = -1;
    private static int penaltyScore = 500;
    private static int penaltyPathScore = 0;
    //private static final int dThres = 80;
    //private static Random rand = new Random();

    MazeInterface maze;
    JunctionNode[] juncs;
    List<TreeNode>[] roots; //They maybe become next start point in the next time slice of each ghost
    //int[] rootId; //The Idex of each root in list of available roots for each ghost
    List<TreeNode>[] trees; //The move of each ghost
    TreeNode nextTNode[]; //An array store the next cross-point tree node the ghost will visit
    ModMCTThread[] mctThread;
    Thread mainThread;
    //int startDirs[];

    NodeScore[] scorers;
    //ArrayList<Node> options;

    //PacInfo[] infoCollector;
    PacInfo infoCollector;
    LinkedList<VectorND> sampleData, procData;
    GameStateInterface preGS = null, pretGS = null;
    Node expectPac;
    //KNNAgent[] pseuAgent;
    //double trustLvl[] = {1.0, 1.0, 1.0, 1.0};
    double trustLvl = 1.0;
    int allTimes, falseTimes;
    //int expectDir = NEUTRAL;
    //ElapsedTimer t = new ElapsedTimer();

    ///////////////////////////////For Experiment//////////////////////////////////////
    /*static int maxRange = 200;
    static int interval = 1;
    public static int contData = 200;
    StatSummary[] sss = new StatSummary[maxRange];
    StatSummary[] sssr = new StatSummary[maxRange];
    public StatSummary[] css = new StatSummary[contData];
    public StatSummary[] cssr = new StatSummary[contData];
    LinkedList<Node> expectPacx = new LinkedList<Node>();
    LinkedList<Node> expectPacr = new LinkedList<Node>();
    LinkedList<Integer> allTimesx = new LinkedList<Integer>();
    LinkedList<Integer> falseTimesx = new LinkedList<Integer>();
    LinkedList<LinkedList<VectorND>> sampleDatax = new LinkedList<LinkedList<VectorND>>();
    LinkedList<LinkedList<VectorND>> procDatax = new LinkedList<LinkedList<VectorND>>();
    LinkedList<Integer> continousT = new LinkedList<Integer>();
    LinkedList<int[]> conArrT = new LinkedList<int[]>(); // continuos guess right
    LinkedList<Integer> continousR = new LinkedList<Integer>();
    LinkedList<int[]> conArrR = new LinkedList<int[]>(); // continous random
    boolean crssPoint = false;
    int numOfDat = 0;
    {
    	//for (int i = 0; i < maxRange; i++) sss[i] = new StatSummary();
    	//for (int i = 0; i < maxRange; i++) sssr[i] = new StatSummary();
    	for (int i = 0; i < contData; i++) css[i] = new StatSummary();
    	for (int i = 0; i < contData; i++) cssr[i] = new StatSummary();
    }*/
    ///////////////////////////////////////////////////////////////////////////////////

    // Make more thread to increase performance
    // At which condition the simulation's finished? Make my own simGame

    @Override
    public int[] getActions(GameStateInterface gs) {
        // TODO Auto-generated method stub
        int[] dirs = new int[nGhosts];
        if (!gs.getMaze().equals(maze)) {
            setMaze(gs.getMaze());
            ///////////////////////////////For Experiment//////////////////////////////////////
            //printStat(gs);
            ///////////////////////////////////////////////////////////////////////////////////
            resetAll();
        } else if (pretGS != null){
            // If the position between preGS and current GS is changed, obviously the game was restarted.
            if (checkRestarted(pretGS, gs)) {
				/*if (allTimes != 0 ) {
					System.out.print("*\t");
					System.out.println(1.0-1.0*falseTimes/allTimes);
				}*/
                ///////////////////////////////For Experiment//////////////////////////////////////
                //printStat(null);
                ///////////////////////////////////////////////////////////////////////////////////
                resetAll();
            }


        }
        pretGS = gs.copy();

        // K-NN
        Node pac = gs.getPacman().current;
        // What is the below code doing
        if (pac.x % 4 == 3 && pac.y % 4 == 1){
            if (sampleData == null) sampleData = new LinkedList<VectorND>();
            if (procData == null) procData = new LinkedList<VectorND>();

            VectorND curStat = infoCollector.getInfo(gs);
            if (preGS != null && sampleData.size() > 0){
                try {
                    if (procData.size() >= maxData - 1) procData.pollFirst();
                    VectorND tmp = VectorND.sub(infoCollector.getInfo(preGS, gs.getPacman().current), sampleData.getLast());
                    tmp.normallize();
                    procData.add(tmp);
                } catch (Exception e){
                    System.out.println(e);
                }
            }
            if (sampleData.size() >= maxData) sampleData.pollFirst();
            sampleData.add(curStat);

            preGS = pretGS;
        }

        //MCT selection
        for (int i = 0; i < nGhosts; i++) {
            Ghosts curGhost = gs.getGhosts()[i];
            // for the first ghost, move to trap pacman
            if (i == rbGhost){
                //dirs[i] = getDefaultDir(gs, i);
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
                if (pre != null && !curGhost.edible() || curGhost.edibleTime < maze.dist(curGhost.current, gs.getPacman().current) / 3.0 ){
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
            }
            if (roots[i] == null) { // If the roots is not decided yet
                roots[i] = new LinkedList<TreeNode>();
                TreeNode t = new TreeNode(i,juncs[maze.ghostStart().nodeIndex],NEUTRAL,NEUTRAL, 4, maze, juncs);
                t.expand();
                if (i == 0) trees[i] = t.selectedNodesFinal();
                else {
                    trees[i] = new LinkedList<TreeNode>();
                    trees[i].add(t);
                    for (TreeNode tn : t.children){
                        if (i != 0 && tn.dir4Parent == (dirs[i-1] + 2*(i % 2)) % 4 ){
                            trees[i].add(tn);
                            break;
                        }
                    }
                }
                // Get direction for next move
                dirs[i] = trees[i].get(1).dir4Parent;
                //if (i == 0) dirs[i] = rand.nextInt(dx.length >> 2) << 2 + 1;
                //else dirs[i] = (dirs[i-1] + 2) % 4;

            }
            // If the previous node isn't set, move in both direction
            if (i != 0 && curGhost.previous == null) {
                //dirs[i] = (((i % 2) << 1) + 3 ) % 4;
                dirs[i] = trees[i].get(1).dir4Parent;
                continue;
            }
            // Stop MCTS
            else if (curGhost.current.adj.size() > 2 && !curGhost.returning() && !(gs.getTotalGameTicks()%2 == 0 && curGhost.edible())){
                if (mctThread[i] != null) mctThread[i].die();
                //if (mctThread[i + nGhosts] != null) mctThread[i + nGhosts].die();
                //if (mctThread[i + nGhosts*2] != null) mctThread[i + nGhosts*2].die();
                //if (mctThread[i + nGhosts*3] != null) mctThread[i + nGhosts*3].die();
                for (TreeNode r : roots[i]){
                    if (r.node.node == curGhost.current && r.dir2Parent == Utilities.getDirection(curGhost.current, curGhost.previous)){
                        // If the node isn't visited enough, choose as default
                        if (r.nVisitsx < 50){
                            //GhostState gh = gs.getGhosts()[i];
                            dirs[i] = getDefaultDir(gs, i);
                            continue;
                        }
                        // Decide next move
                        trees[i] = r.selectedNodesFinal();
                        // If some how the next move is not calculate, skip
                        if (trees[i].size() < 2){
                            //dirs[i] = rand.nextInt(dx.length);
                            dirs[i] = getDefaultDir(gs, i);
                            continue;
                        }
                        // Get direction for next move
                        dirs[i] = trees[i].get(1).dir4Parent;
                    }
                }
                nextTNode[i] = null;
                roots[i] = new LinkedList<TreeNode>();
            }
        }

        //KNN Testing
        if (pac.x % 4 == 3 && pac.y % 4 == 1 && sampleData.size() >= maxData){
            //System.out.println(t);
            if (expectPac != null && expectPac != gs.getPacman().current)
                falseTimes++;

            ///////////////////////////////For Experiment//////////////////////////////////////
			/*for (int i = 0; i < expectPacx.size(); i++){
				if (sampleDatax.get(i).size() <= 5) continue;
				Integer aT = allTimesx.get(i);
				if (expectPacx.get(i) == null || expectPacx.get(i) != gs.getPacman().current){
					falseTimesx.set(i, falseTimesx.get(i) + 1);
					sss[aT].add(0);
					//if (crssPoint) {
						continousT.set(i, 0);
					//}
				}
				else {
					//sss[aT].add(1);
					//if (crssPoint) {
						if (continousT.get(i) >= contData) conArrT.get(i)[continousT.get(i)-1]+=1;
						else {
							continousT.set(i,continousT.get(i)+1);
						}
					//}
				}
				//if (crssPoint) {
					conArrT.get(i)[continousT.get(i)]+=1;
				//}

				if (expectPacr.get(i) == null || expectPacr.get(i) != gs.getPacman().current){
					//sssr[aT].add(0);
					//if (crssPoint) {
						continousR.set(i, 0);
					//}
				}
				else {
					//sssr[aT].add(1);
					//if (crssPoint) {
						if (continousR.get(i) >= contData) conArrR.get(i)[continousR.get(i)-1]+=1;
						else {
							continousR.set(i,continousR.get(i)+1);
						}
					//}
				}
				//if (crssPoint) {
					conArrR.get(i)[(int)continousR.get(i)]+=1;
				//}


				//if (aT > 0) sss[aT - 1].add(trustLvl*(1 - 1.0*falseTimesx.get(i)/aT));
			}*/
            ///////////////////////////////////////////////////////////////////////////////////

            PacInfo tmpInfo = new PacInfo(infoCollector);
            KNNAgent pseAgent = new
                    KNNAgent(tmpInfo, sampleData.toArray(new VectorND[0]), procData.toArray(new VectorND[0]), trustLvl);
            int expectDir = pseAgent.getAction(new SimGameState(gs));
            expectPac = gs.getPacman().current;
            for (int j = 0; j < 4; j++){
                if(maze.getNode(expectPac.x + dx[expectDir], expectPac.y + dy[expectDir]) != null)
                    expectPac = maze.getNode(expectPac.x + dx[expectDir], expectPac.y + dy[expectDir]);
            }
            allTimes++;

            ///////////////////////////////For Experiment//////////////////////////////////////
			/*for (int i = 0; i < sampleDatax.size(); i++){
				if (sampleDatax.get(i).size() <= 5) continue;
				pseAgent = new
					KNNAgent(tmpInfo, sampleDatax.get(i).toArray(new VectorND[0]), procDatax.get(i).toArray(new VectorND[0]), trustLvl*(1-1.0*falseTimesx.get(i)/allTimesx.get(i)));
				expectDir = pseAgent.getAction(new SimGameState(gs));
				Node ePac = gs.getPacman().current;
				for (int j = 0; j < 4; j++){
					if(maze.getNode(ePac.x + dx[expectDir], ePac.y + dy[expectDir]) != null)
						ePac = maze.getNode(ePac.x + dx[expectDir], ePac.y + dy[expectDir]);
				}
				expectPacx.set(i, ePac);

				BetterRandomAgent rAgent = new BetterRandomAgent();
				expectDir = rAgent.getAction(new SimGameState(gs));
				ePac = gs.getPacman().current;
				for (int j = 0; j < 4; j++){
					if(maze.getNode(ePac.x + dx[expectDir], ePac.y + dy[expectDir]) != null)
						ePac = maze.getNode(ePac.x + dx[expectDir], ePac.y + dy[expectDir]);
				}
				expectPacr.set(i, ePac);

				allTimesx.set(i, allTimesx.get(i) + 1);
			}
			if (gs.getPacman().current.adj.size() > 2) crssPoint = true;
			else crssPoint = false;*/
            ///////////////////////////////////////////////////////////////////////////////////
        }

        // Start new MCTS
        // Ket hop ca 2 estimator va random
        for (int i = 0; i < nGhosts; i++){
            if (i == rbGhost) continue;
            //if (gs.getGhosts()[i].previous == null) continue;
            KNNAgent pseAgent = null;
            // Assume the next game state
            SimGameState nextGS = TreeNode.getStartState(trees, gs.copy(), i, pseAgent);
            // Check if the next destination is ok else add new destination
            // If the dest is difference stop, start new Thread
            // Check if at a cross point, start new thread
            // Maybe rootId is not neccessary
            if(nextGS.getPills().isEmpty() && nextGS.getPowers().isEmpty() || nextGS.terminal()) {
                continue;
            }
            SimGhosts curGhost = nextGS.getGhosts()[i];
            int curDir = Utilities.getDirection(curGhost.current, curGhost.previous);
            //SimMsPacMan curPac = nextGS.getPacman();
            //KNN
            //if (pac.x % 4 == 3 && pac.y % 4 == 1 && sampleData.size() > 5){
            //if (sampleData != null && sampleData.size() > 5 && maze.dist(curGhost.current, curPac.current) <= dThres){
            if (sampleData != null && sampleData.size() >= maxData){
                PacInfo tmpInfo = new PacInfo(infoCollector);
                pseAgent = new
                        KNNAgent(tmpInfo, sampleData.toArray(new VectorND[0]), procData.toArray(new VectorND[0]), trustLvl*(1-1.0*falseTimes/allTimes));
            }
            //pseAgent = null;
            //Stat game state should be put in the thread
            //int curDir = curGhost.curDir;
            //Global reverse occur when roots[i].size >= 2 -> reset trees[i];
            if (nextTNode[i] == null || nextTNode[i].node.node != curGhost.current || nextTNode[i].dir2Parent != curDir){
                boolean check = true; //Check wether the nextTNode is already in list or not
                if (mctThread[i] != null) mctThread[i].die();
                //if (mctThread[i + nGhosts] != null) mctThread[i + nGhosts].die();
                //if (mctThread[i + nGhosts*2] != null) mctThread[i + nGhosts*2].die();
                //if (mctThread[i + nGhosts*3] != null) mctThread[i + nGhosts*3].die();
                for (TreeNode r : roots[i]){
                    if (r.node.node == curGhost.current && r.dir2Parent == curDir){
                        nextTNode[i] = r;
                        mctThread[i] = new ModMCTThread(nextTNode[i], i, pseAgent, this, 4, penaltyPathScore, penaltyScore);
                        mctThread[i].start();
                        //mctThread[i + nGhosts] = new MCTThread(nextTNode[i], i, pseAgent, this, 1);
                        //mctThread[i + nGhosts].start();
                        //mctThread[i + nGhosts*2] = new MCTThread(nextTNode[i], i, pseAgent, this, 2);
                        //mctThread[i + nGhosts*2].start();
                        //mctThread[i + nGhosts*3] = new MCTThread(nextTNode[i], i, pseAgent, this, 3);
                        //mctThread[i + nGhosts*3].start();
                        check = false;
                        break;
                    }
                }
                if (check && juncs[curGhost.current.nodeIndex] != null){
                    TreeNode newNode = new TreeNode(i, juncs[curGhost.current.nodeIndex], curDir, NEUTRAL, 4, maze, juncs);
                    roots[i].add(newNode);
                    if (roots[i].size() >= 2) trees[i] = null; //in case of global reverse, reset trees[i]
                    nextTNode[i] = newNode;
                    mctThread[i] = new ModMCTThread(nextTNode[i], i, pseAgent, this, 4, penaltyPathScore, penaltyScore);
                    mctThread[i].start();
                    //mctThread[i + nGhosts] = new MCTThread(nextTNode[i], i, pseAgent, this, 1);
                    //mctThread[i + nGhosts].start();
                    //mctThread[i + nGhosts*2] = new MCTThread(nextTNode[i], i, pseAgent, this, 2);
                    //mctThread[i + nGhosts*2].start();
                    //mctThread[i + nGhosts*3] = new MCTThread(nextTNode[i], i, pseAgent, this, 3);
                    //mctThread[i + nGhosts*3].start();
                }
            }
        }
		/*
		if (gs.terminal() || gs.getPills().isEmpty() && gs.getPowers().isEmpty() || gs.agentDeath()) {
			for (int i=0; i<nGhosts; i++) mctThread[i].die();
		}
		*/

        return dirs;
    }

    @SuppressWarnings("unchecked")
    public ModifiedGhostTeamBot(int pPathScore, int pScore) {
        penaltyPathScore = pPathScore;
        penaltyScore = pScore;
        roots = (List<TreeNode>[]) new List[nGhosts];
        trees = (List<TreeNode>[]) new List[nGhosts];
        //rootId = new int[nGhosts];
        nextTNode = new TreeNode[nGhosts];
        mctThread = new ModMCTThread[nGhosts];
        mainThread = Thread.currentThread();

        //startDirs = new int[nGhosts];

        //options = new ArrayList<Node>();
        scorers = new NodeScore[]{
                new PathScore(), new EuclideanScore(),
                new ManhattanScore(), new PathScore()
        };

        //Setup for KNNAgent
        /*
        infoCollector = new PacInfo[nGhosts];
        for (int i = 0; i < nGhosts; i++){
        	infoCollector[i] = new PacInfo();
        }
        */
        infoCollector = new PacInfo();

        ///////////////////////////For Experiment////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////
    }

    ///////////////////////////For Experiment////////////////////////////////////////////
	/*public void computeStat(GameStateInterface gs) {
		if (gs != null) System.out.print("*\t" + gs.getLevel() + "\t");
		else System.out.print("*\t");
		System.out.println(numOfDat);
		for (int i = 0; i < sss.length; i++) {
			if (sss[i].n() <= 20) break;
			if (i%10 == 0)
				System.out.println((i*4) + "\t" + (sss[i].mean()+sss[i].sd()) + "\t" + (sss[i].mean()-sss[i].sd()) + "\t" + sss[i].mean());
			else System.out.println((i*4) + "\t" + "\t" + "\t" + sss[i].mean());
			System.out.println((i*4) + "\t" + sss[i].mean()+ "\t" + sssr[i].mean());
		}
		for (int i = 0; i < css.length; i++) {
			for (int j = 0; j < continousT.size(); j++) {
				css[i].add(conArrT.get(j)[i]);
			}
			for (int j = 0; j < continousR.size(); j++) {
				cssr[i].add(conArrR.get(j)[i]);
			}
			if (css[i].sum()==0 && cssr[i].sum()==0) break;
			//System.out.println(i + "\t" + css[i].sum()+ "\t" + cssr[i].sum());
		}
	}*/
    /////////////////////////////////////////////////////////////////////////////////

    private boolean checkRestarted(GameStateInterface pre, GameStateInterface cur){
        int pLives = pre.getLivesRemaining();
        int cLives = cur.getLivesRemaining();
        if (pre.getScore() >= 10000) pLives --;
        if (cur.getScore() >= 10000) cLives --;
        return (pLives!=cLives);
    }

    private void setMaze(MazeInterface maze) {
        this.maze = maze;
        //setup junctions
        juncs = new JunctionNode[maze.getMap().size()];
        for (Node n : maze.getMap()){
            if (n.adj.size() > 2 || n.equals(maze.ghostStart())) juncs[n.nodeIndex] = new JunctionNode(n);
        }

    }

    private int getMaxDir(ArrayList<Node> nodes, Node cur, NodeScore f, GameStateInterface gs){
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
        mctThread = new ModMCTThread[nGhosts*4];

        //startDirs = new int[nGhosts];
        //Setup for KNNAgent
        sampleData = null;
        procData = null;
        //sampleDatax = null;
        //procDatax = null;
        //if (sampleData != null) sampleData.pollLast();
        //allTimes = 0;
        //falseTimes = 0;
        //allTimesx = 0;
        //falseTimesx = 0;
        expectPac = null;
        //expectPacx = null;
        preGS = null;
        ///////////////////////////For Experiment////////////////////////////////////////////
        /*//for (int i = 0; i < maxRange; i++) sss[i] = new StatSummary();
        //for (int i = 0; i < maxRange; i++) sssr[i] = new StatSummary();
        //for (int i = 0; i < contData; i++) css[i] = new StatSummary();
        //for (int i = 0; i < contData; i++) cssr[i] = new StatSummary();
        sampleDatax = new LinkedList<LinkedList<VectorND>>();
        procDatax = new LinkedList<LinkedList<VectorND>>();
        allTimesx = new LinkedList<Integer>();
        falseTimesx = new LinkedList<Integer>();
        expectPacx = new LinkedList<Node>();
        expectPacr = new LinkedList<Node>();
        continousR = new LinkedList<Integer>();
        continousT = new LinkedList<Integer>();
        conArrT = new LinkedList<int[]>();
        conArrR = new LinkedList<int[]>();
        crssPoint = false;
        numOfDat = 0;*/
        ///////////////////////////////////////////////////////////////////////////////////

    }

    private int getDefaultDir (GameStateInterface gs, int ghostId){
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
	/*
	public static void main(String[] args) {
		GameState gs = new GameState();
		gs.reset();
		MCTTeam test = new MCTTeam();
		//ElapsedTimer et = new ElapsedTimer();
		test.setMaze(gs.getMaze());
		//System.out.println(et);
		for (JunctionNode jn : test.juncs){
			if (jn != null) System.out.println(jn.node.x + "\t" + jn.node.y + "\t" + jn.node.ix);
		}
		//GameStateView gsv = new GameStateView(gs);
		//JEasyFrame fr = new JEasyFrame(gsv, "Game State", true);
		//System.out.println(test.juncs.length);
		TreeNode tn = new TreeNode(test.juncs[gs.getMaze().getNode(27, 21).nodeIndex]);
		tn.expand(gs, test.juncs);
		tn.children[1].expand(gs, test.juncs);
		int i = 0;
		for (JunctionNode t : test.juncs[gs.getMaze().getNode(27, 21).nodeIndex].adjJunc){
			System.out.println("*" + test.juncs[gs.getMaze().getNode(27, 21).nodeIndex].dirs2Adj[i] + "\t"
					+ test.juncs[gs.getMaze().getNode(27, 21).nodeIndex].dirs4Adj[i++] + "\t" + t.node);
		}

		i = 0;
		for (JunctionNode t : test.juncs[gs.getMaze().getNode(39, 21).nodeIndex].adjJunc){
			System.out.println("**" + test.juncs[gs.getMaze().getNode(39, 21).nodeIndex].dirs2Adj[i] + "\t"
					+ test.juncs[gs.getMaze().getNode(39, 21).nodeIndex].dirs4Adj[i++] + "\t" + t.node);
		}
		for (i = 0; i < 10; i++){
			List<TreeNode> ls = tn.selectedNodes();
			for (TreeNode cur : ls) {
				System.out.println(cur.node.node);
			}
			System.out.println("*****");
		}
		//System.out.println(et);
	}
	*/

}
