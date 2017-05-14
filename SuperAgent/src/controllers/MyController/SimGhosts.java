package controllers.MyController;

//package core;

import core.maze.Maze;
import core.utilities.Utilities;
import core.*;

import java.util.ArrayList;
import java.util.Random;

public class SimGhosts implements Constants {
    private Random rand = new Random();
	public int edibleTime;
    public Node current, previous;
    public int curDir;
    //int delay;
    //int delayCounter;
    Node returnNode = null;

    public SimGhosts() {
    }
    
    /*
    public SimGhosts(int delay) {
        this.delay = delay;
        delayCounter = delay;
        edibleTime = -1;
    }
    */

    public SimGhosts(int edibleTime, Node current) {
        this.edibleTime = edibleTime;
        this.current = current;
    }

    public SimGhosts(Ghosts gs) {
        this.edibleTime = gs.edibleTime;
        //this.delay = gs.delay;
        //this.delayCounter = gs.delayCounter;
        this.current = gs.current;
        this.previous = gs.previous;
        this.curDir = gs.curDir;
        //this.returnNode = gs.returnNode;
        // Return Node should be assign when in SimGameState
    }

    void updateState(Node next) {
        previous = current;
        current = next;
        if (edibleTime >= 0) {
            edibleTime--;
        }
        curDir = findDir();
    }

    int findDir() {
        int xd = current.x - previous.x;
        int yd = current.y - previous.y;
        for (int i = 0; i < dx.length; i++) {
            if (xd == dx[i] && yd == dy[i]) {
                return i;
            }
        }
        return 0;
    }

    public void reverse() {
        if (previous != null) {
            Node tmp = current;
            current = previous;
            previous = tmp;
        }
    }

    public boolean returning() {
        return returnNode != null;
    }

    void makeReturnMove(Maze maze) {

        if (returning()) {

            Node next = Utilities.getClosest(current.adj, returnNode, maze);
            updateState(next);
            if (next.equals(returnNode)) returnNode = null;
        }
    }

    public ArrayList<Node> getPossibles() {

        ArrayList<Node> possibles = new ArrayList<Node>();
        for (Node n : current.adj) {
            if (!n.equals(previous)) {
                possibles.add(n);
            }
        }
        return possibles;
    }

    public void next(int dir, SimGameState gs) {
        Maze maze = gs.maze;

        if (returning()) {
            makeReturnMove(maze);
            makeReturnMove(maze);
            return;
        }
       /*
       if (delayCounter > 0) {
            delayCounter--;
            return;
        }
        */

        if (gs.totalGameTicks % 2 == 0 && edible()) return;


        Node next = maze.getNode(current.x + dx[dir], current.y + dy[dir]);

        if (next == null || next.equals(previous) || next.equals(current)) {
            ArrayList<Node> possibles = getPossibles();
            if (possibles.size() == 1) {

                next = possibles.get(0);
            } else {
                next = possibles.get(rand.nextInt(possibles.size()));
            }
        }
        updateState(next);
    }

   public boolean edible() {
        return edibleTime >= 0 && !returning();
    }

    public void setPredatory() {
        edibleTime = -1;
    }
}