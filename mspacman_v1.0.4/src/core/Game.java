package core;

import competition.GhostsController;
import competition.MsPacManController;
import core.visuals.GameStateView;
import core.visuals.JEasyFrame;

public class Game implements Constants {
    public final GameState gs;
    final GameStateView gsv;
    final MsPacManController mspacman;
    final GhostsController ghosts;
    public JEasyFrame frame;

    public Game(GameState gs, GameStateView gsv, MsPacManController mspacman, GhostsController ghosts) {
        this.gs = gs;
        this.gsv = gsv;
        this.mspacman = mspacman;
        this.ghosts = ghosts;
    }

    public void run(int delay) throws Exception {
        while (!gs.terminal()) {
            cycle(delay);
        }
    }
    
    public void runLevel(int delay, int level) throws Exception {
        while (!gs.terminal() && gs.level == level) {
            cycle(delay);
        }
    }

    void cycle(int delay) throws Exception {
        gs.next(mspacman.getAction(gs), ghosts.getActions(gs));
        Thread.sleep(delay);
        if (gsv != null) {
            gsv.repaint();
            if (frame != null)
                frame.setTitle("Score: " + gs.score + ", time: " + gs.gameTicks);
        }
    }
}