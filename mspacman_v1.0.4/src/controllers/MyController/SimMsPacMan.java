package controllers.MyController;

//package core;

import core.maze.*;
import core.*;
//import core.utilities.*;

public class SimMsPacMan implements Constants {

    public Node current;
    public int curDir;

    public SimMsPacMan(Node current) {
        this.current = current;
        curDir = LEFT;
    }

    public SimMsPacMan(MsPacMan p) {
        current = p.current;
        curDir = p.curDir;
    }

    public void next(int dir, Maze maze) {
        if (dir == NEUTRAL) dir = curDir;
        Node next = maze.getNode(current.x + dx[dir], current.y + dy[dir]);
        if (next != null) {
            current = next;
            curDir = dir;
        } else {
            next = maze.getNode(current.x + dx[curDir], current.y + dy[curDir]);
            if (next != null) current = next;
        }
    }
}