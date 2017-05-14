package controllers.MyController;

//package core;

import core.Constants;
//import core.GameState;
import core.GameStateInterface;
import core.maze.*;
import core.utilities.*;

import java.util.BitSet;

public class SimGameState implements Constants {
	static final int edibleFactor = 4;

	void tryEatPower() {
		int ix = pacMan.current.powerIndex;
		if (ix >= 0) {
			if (powers.get(ix)) {
				powers.clear(ix);

				reverseGhosts();
				setEdible();
				score += powerScore*edibleFactor;
			}
		}
	}

	public boolean pillsCleared() {
		return pills.isEmpty() && powers.isEmpty();
	}

	void eatGhosts() {
		for (SimGhosts g : ghosts) {
			if (g.edible() && overlap(g, pacMan)) {
				score += edibleGhostScore*edibleFactor;
				edibleGhostScore *= 2;

				g.returnNode = maze.ghostStart();
				g.setPredatory();
			}
		}
	}

    public Maze maze;
    public BitSet powers;
    public BitSet pills;
    public SimMsPacMan pacMan;
    public SimGhosts[] ghosts;
    public int score = 0;
    int level;
    private int edibleGhostScore;
    public int totalGameTicks;
    public int nLivesRemaining;
    //private boolean extraLife = false;
    
    public SimGameState (GameStateInterface gs){
		// return a deep copy of the current game state
		// just copy a reference for the ones that don't change
		maze =Level.getMaze(gs.getLevel());
		powers = gs.getPowers();
		pills = gs.getPills();
		pacMan = new SimMsPacMan(gs.getPacman());
		ghosts = new SimGhosts[gs.getGhosts().length];
		for (int i = 0; i < ghosts.length; i++) {
			ghosts[i] = new SimGhosts(gs.getGhosts()[i]);
			if (gs.getGhosts()[i].returning()) ghosts[i].returnNode = maze.ghostStart();
		}
		score = gs.getScore();
		level = gs.getLevel();
		edibleGhostScore = gs.getEdibleGhostScore();
		totalGameTicks = gs.getTotalGameTicks();
		nLivesRemaining = gs.getLivesRemaining();
	}
   /*
   public void reset() {
        resetScores();
        maze = Level.getMaze(level);
        resetAgents();
        resetPills();
        totalGameTicks = 0;
        //gameTicks = 0;
    }
   
    void resetAgents() {
        pacMan = new SimMsPacMan(maze.getMap().get(0));
        pacMan.current = maze.pacStart();
        for (int i = 0; i < nGhosts; i++) {
            ghosts[i] = new SimGhosts(initGhostDelay[i]);
            ghosts[i].current = maze.ghostStart();
        }
    }
    

    //bug fix thanks to James_2
    void resetPills() {
        pills = new BitSet(maze.getPills().size());
        powers = new BitSet(maze.getPowers().size());
        pills.set(0, maze.getPills().size());
        powers.set(0, maze.getPowers().size());
        }

    void resetScores() {
        totalGameTicks = 0;
        score = 0;
        level = 0;
        nLivesRemaining = nLives;
    }

    public void nextLevel() {
        level++;
        maze = Level.getMaze(level);
        resetPills();
        resetAgents();
        //gameTicks = 0;
    }
	*/
    public void next(int pacDir, int[] ghostDirs) {
		pacMan.next(pacDir, maze);
		tryEatPill();
		tryEatPower();
		eatGhosts();
		//if (ghostReversal()) {
		//	reverseGhosts();
		///} else {
		moveGhosts(ghostDirs);
		//}
		totalGameTicks++;
	}

    boolean ghostReversal() {
        return rand.nextDouble() < 0.005;
    }

    void reverseGhosts() {
        for (SimGhosts g : ghosts) g.reverse();
    }

    void moveGhosts(int[] ghostDirs) {
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].next(ghostDirs[i], this);
        }
    }

    void tryEatPill() {
        int ix = pacMan.current.pillIndex;
        if (ix >= 0) {
            if (pills.get(ix)) {
                pills.clear(ix);
                score += pillScore;

            }
        }
    }

    void setEdible() {
        edibleGhostScore = edibleGhostStartingScore;
        for (SimGhosts g : ghosts) {
            g.edibleTime = Level.edibleTime(level);
        }
    }

    public boolean MsPacManDeath() {
        for (SimGhosts g : ghosts) {
            if (!g.edible() && !g.returning() && overlap(g, pacMan)) {

                return true;
            }
        }
        return false;
    }

    public boolean terminal() {
        return nLivesRemaining <= 0;
    }

    private static boolean overlap(SimGhosts g, SimMsPacMan agent) {
        return Utilities.manhattan(g.current, agent.current)
                <= agentOverlapDistance;
    }

    public int getLivesRemaining() {
        return nLivesRemaining;
    }

    public SimMsPacMan getPacman() {
        return pacMan;
    }

    public Maze getMaze() {
        return maze;
    }

    public BitSet getPills() {
        return pills;
    }

    public BitSet getPowers() {
        return powers;
    }

    public SimGhosts[] getGhosts() {
        return ghosts;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalGameTicks() {
        return totalGameTicks;
    }

    public int getEdibleGhostScore() {
        return edibleGhostScore;
    }
}
