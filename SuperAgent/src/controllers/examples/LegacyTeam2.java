package controllers.examples;

import java.util.ArrayList;

import competition.*;
import controllers.*;
import core.*;
import core.maze.*;
import core.utilities.*;

public class LegacyTeam2 implements GhostsController, Constants {

	int[] dirs;
    NodeScore[] scorers;
    ArrayList<Node> options;
    
    public LegacyTeam2() {
        this.dirs = new int[nGhosts];
        options = new ArrayList<Node>();
        scorers = new NodeScore[]{
                new RiskScore(), new JunctionE(),
                new JunctionM(), new JunctionP(),
        };
    }

    public int[] getActions(GameStateInterface gs) {
        // each score function dictates where each ghost should move
        for (int i=0; i<dirs.length; i++) {
            Ghosts gh = gs.getGhosts()[i];
            options.clear();
            for (Node n : gh.current.adj) {
                // turning back is not a valid option
                if (!n.equals(gh.previous)) options.add(n);
            }
            dirs[i] = Utilities.getMinDir(options, gh.current, scorers[i], gs);
        }
        return dirs;
    }

}
