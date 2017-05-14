package controllers.MyController;

import competition.GhostsController;
import controllers.EuclideanScore;
import controllers.ManhattanScore;
import controllers.NodeScore;
import controllers.PathScore;
import core.GameStateInterface;
import core.Ghosts;
import core.Node;
import core.utilities.Utilities;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SuperAgent extends MyGhostsTeamBot implements GhostsController{


    public SuperAgent(){
        roots = (List<TreeNode>[]) new List[nGhosts];
        trees = (List<TreeNode>[]) new List[nGhosts];
        nextTNode = new TreeNode[nGhosts];
        mctThread = new MCTThread[nGhosts];
        mainThread = Thread.currentThread();
        scorers = new NodeScore[]{
                new PathScore(), new EuclideanScore(),
                new ManhattanScore(), new PathScore()
        };
        //Setup for KNNAgent
        infoCollector = new PacInfo();
    }

    @Override
    public int[] getActions(GameStateInterface gs){
        int[] dirs = new int[nGhosts];

        if (!gs.getMaze().equals(maze)) {
            setMaze(gs.getMaze());
            resetAll();
        } else if (pretGS != null){
            if (checkRestarted(pretGS, gs)) {
                resetAll();
            }
        }
        pretGS = gs.copy();
        pacmanKnnCollectAndTest(gs);
        //System.out.println(pretGS.getPowers().toString().toString().length());
        //MCT or RBGhost selection

        //rbGhost.clear();
        ChoseRule(pretGS);
        for (int i = 0; i < nGhosts; i++) {
            //m = n;
            if (checkRBGhostIndex(i)){
                dirs[i]=getRBGhostMove(gs,i);
                continue;
            }
            int dir4Parent=0;
            if(i!=0)dir4Parent=((dirs[i-1] + 2*(i % 2)) % 4);
            dirs[i]=getMCTSGhostMove(gs,i,dir4Parent);
        }
        // Start new MCTS
        // Ket hop ca 2 estimator va random
        for (int i = 0; i < nGhosts; i++){
            if (checkRBGhostIndex(i)) continue;
            constructMCTS(gs,i);
        }
        return dirs;
    }

    public void ChoseRule(GameStateInterface pretGS)
    {
       // System.out.println(pretGS.getPowers().toString().toString().length());
        if(pretGS.getPowers().toString().toString().length() < 3)
        {
            rbGhost.clear();
            followPill = false;
        }
        else if(pretGS.getPowers().toString().toString().length() > 10)
        {
            followPill = false;
            if(rbGhost.size() == 0) {
                rbGhost.add(0);
            }

        }
        else if(pretGS.getPowers().toString().toString().length() < 6)
        {
            followPill = true;
            rbGhost.clear();
            if(rbGhost.size() == 0) {

               // Random rand = new Random();
               // int randomNum = rand.nextInt(4);
                rbGhost.add(0);
            }
        }
    }

    public int getRBGhostMove(GameStateInterface gs, int ghostIndex){
        int dir=0;
        boolean inverse = false;
        Ghosts curGhost = gs.getGhosts()[ghostIndex];
        Node target;
        Node pac=gs.getPacman().current;
        if(!followPill)
        {
            target = pac;
        }
        else{
            target = infoCollector.getPowerPillIndex(gs);
            if(target == null)
            {
                target = pac;
            }
        }

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
                    for (int j = 0; j < nGhosts; j++) if (j != ghostIndex && target == gs.getGhosts()[j].current && pre != gs.getGhosts()[j].previous && !followPill){
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
            dir = Utilities.getWrappedDirection(curGhost.current, sel, gs.getMaze());
        }
        else dir = getMaxDir(options, curGhost.current, scorers[ghostIndex], gs);
        return dir;
    }

    public int getMCTSGhostMove(GameStateInterface gs,int ghostIndex,int dir4Parent){
        int dir=0;
        Ghosts curGhost = gs.getGhosts()[ghostIndex];
        // for the first ghost, move to trap pacman

        if (roots[ghostIndex] == null) { // If the roots is not decided yet
            roots[ghostIndex] = new LinkedList<TreeNode>();
            TreeNode t = new TreeNode(ghostIndex,juncs[maze.ghostStart().nodeIndex],NEUTRAL,NEUTRAL, 4, maze, juncs);
            t.expand();
            if (ghostIndex == 0) trees[ghostIndex] = t.selectedNodesFinal();
            else {
                trees[ghostIndex] = new LinkedList<TreeNode>();
                trees[ghostIndex].add(t);
                for (TreeNode tn : t.children){
                    if (ghostIndex != 0 && tn.dir4Parent == dir4Parent ){
                        trees[ghostIndex].add(tn);
                        break;
                    }
                }
            }
            // Get direction for next move
            dir = trees[ghostIndex].get(1).dir4Parent;
        }
        // If the previous node isn't set, move in both direction
        if (ghostIndex != 0 && curGhost.previous == null) {
            dir = trees[ghostIndex].get(1).dir4Parent;
            return dir;
        }
        // Stop MCTS
        else if (curGhost.current.adj.size() > 2 && !curGhost.returning() && !(gs.getTotalGameTicks()%2 == 0 && curGhost.edible())){
            if (mctThread[ghostIndex] != null) mctThread[ghostIndex].die();
            for (TreeNode r : roots[ghostIndex]){
                if (r.node.node == curGhost.current && r.dir2Parent == Utilities.getDirection(curGhost.current, curGhost.previous)){
                    // If the node isn't visited enough, choose as default
                    if (r.nVisitsx < 50){
                        dir = getDefaultDir(gs, ghostIndex);
                        continue;
                    }
                    // Decide next move
                    trees[ghostIndex] = r.selectedNodesFinal();
                    // If some how the next move is not calculate, skip
                    if (trees[ghostIndex].size() < 2){
                        dir = getDefaultDir(gs, ghostIndex);
                        continue;
                    }
                    // Get direction for next move
                    dir = trees[ghostIndex].get(1).dir4Parent;
                }
            }
            nextTNode[ghostIndex] = null;
            roots[ghostIndex] = new LinkedList<TreeNode>();
        }
        return dir;
    }

    public void pacmanKnnCollectAndTest(GameStateInterface gs){
        Node pac = gs.getPacman().current;
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
        if (pac.x % 4 == 3 && pac.y % 4 == 1 && sampleData.size() >= maxData){
            if (expectPac != null && expectPac != gs.getPacman().current)
                falseTimes++;
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
        }
    }

    public void constructMCTS(GameStateInterface gs,int ghostIndex){

        //if (gs.getGhosts()[i].previous == null) continue;
        KNNAgent pseAgent = null;
        // Assume the next game state
        SimGameState nextGS = TreeNode.getStartState(trees, gs.copy(), ghostIndex, pseAgent);
        // Check if the next destination is ok else add new destination
        // If the dest is difference stop, start new Thread
        // Check if at a cross point, start new thread
        // Maybe rootId is not neccessary
        if(nextGS.getPills().isEmpty() && nextGS.getPowers().isEmpty() || nextGS.terminal()) {
            return;
        }
        SimGhosts curGhost = nextGS.getGhosts()[ghostIndex];
        int curDir = Utilities.getDirection(curGhost.current, curGhost.previous);
        //KNN
        if (sampleData != null && sampleData.size() >= maxData){
            PacInfo tmpInfo = new PacInfo(infoCollector);
            pseAgent = new
                    KNNAgent(tmpInfo, sampleData.toArray(new VectorND[0]), procData.toArray(new VectorND[0]), trustLvl*(1-1.0*falseTimes/allTimes));
        }
        //Stat game state should be put in the thread
        //Global reverse occur when roots[i].size >= 2 -> reset trees[i];
        if (nextTNode[ghostIndex] == null || nextTNode[ghostIndex].node.node != curGhost.current || nextTNode[ghostIndex].dir2Parent != curDir){
            boolean check = true; //Check wether the nextTNode is already in list or not
            if (mctThread[ghostIndex] != null) mctThread[ghostIndex].die();
            for (TreeNode r : roots[ghostIndex]){
                if (r.node.node == curGhost.current && r.dir2Parent == curDir){
                    nextTNode[ghostIndex] = r;
                    mctThread[ghostIndex] = new MCTThread(nextTNode[ghostIndex], ghostIndex, pseAgent, this, 4);
                    mctThread[ghostIndex].start();
                    check = false;
                    break;
                }
            }
            if (check && juncs[curGhost.current.nodeIndex] != null){
                TreeNode newNode = new TreeNode(ghostIndex, juncs[curGhost.current.nodeIndex], curDir, NEUTRAL, 4, maze, juncs);
                roots[ghostIndex].add(newNode);
                if (roots[ghostIndex].size() >= 2) trees[ghostIndex] = null; //in case of global reverse, reset trees[i]
                nextTNode[ghostIndex] = newNode;
                mctThread[ghostIndex] = new MCTThread(nextTNode[ghostIndex], ghostIndex, pseAgent, this, 4);
                mctThread[ghostIndex].start();
            }
        }
    }

}
