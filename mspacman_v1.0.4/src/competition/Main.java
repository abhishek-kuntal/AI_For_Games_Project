package competition;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import utilities.StatSummary;
import controllers.KeyController;
import controllers.examples.*;
import controllers.testPacman.BetterController;
import controllers.*;
import core.*;
import core.maze.Level;
import core.maze.MazeOne;
import core.visuals.GameStateView;
import core.maze.Maze;
import core.visuals.JEasyFrame;
import controllers.MyController.*;

//Executes the code and shows how you can test your controllers.
public class Main implements Constants {

    private static final boolean visual = true;
    private static final boolean async = true;

    public static void main(String[] args) throws Exception {
    	int nTimes = 25;
    	
    	int kTicks[] = {40}, allTicks[] = {400};



    	
    	BufferedWriter writerI = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ResultDFSI.txt")));
    	//BufferedWriter writerM = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ResultDFSM.txt")));
    	//BufferedWriter writerS = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ResultDFSS.txt")));
    	
    	for (int kTick : kTicks){
    		for (int allTick : allTicks){
    			StatSummary ss = new StatSummary();
//    			StatSummary ss1 = new StatSummary();
//    			StatSummary ss2 = new StatSummary();
//    			StatSummary ss3 = new StatSummary();
    			System.out.println(kTick + "-" + allTick);
    			
    			//for (int level = 0; level < 4; level ++) {
    				
    				System.out.println("Base MCTS Ghost vs Smart Pill Controller");
    				//System.out.println("Level\t" + level);

    				/*StatSummary[] css = new StatSummary[MyGhostsTeamBot.contData];
    				StatSummary[] cssr = new StatSummary[MyGhostsTeamBot.contData];
    				for (int i = 0; i < MyGhostsTeamBot.contData; i++) css[i] = new StatSummary();
    				for (int i = 0; i < MyGhostsTeamBot.contData; i++) cssr[i] = new StatSummary();*/

    				//writerI.write(kTick + "-" + allTick + "\t");
    				//writerI.flush();
    				for (int i = 0; i < nTimes; i++){
    					//MsPacManController mspacman = null;//allows you to play the game yourself using the keyboard(arrow keys)
    					//MsPacManController mspacman = new RandomMsPacMan();
    					//MsPacManController mspacman = new RandomNonReverseMsPacMan();
    					//MsPacManController mspacman = new SimplePillEatingMsPacMan();
    					MsPacManController mspacman = new SmartPillEater();
    					//MsPacManController mspacman = new BetterController();
                        TreeNode.KNNTick = kTick;
                        TreeNode.maxTick = allTick;
                        //GhostsController ghosts = new ModifiedGhostTeamBot(0, 500);;
                        MyGhostsTeamBot ghosts = new MyGhostsTeamBot();
                        if (visual) {
                                    ss.add(runVisual(mspacman, ghosts, async));
                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
                                } else {
                                    ss.add(runDark(mspacman, ghosts, async));
                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
                                }
//    					switch(i%5) {
//                            case 0:
//                                GhostsController rGhosts = new RandomGhosts();
//                                if (visual) {
//                                    ss.add(runVisual(mspacman, rGhosts, async));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss.add(runDark(mspacman, rGhosts, async));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            case 1:
//                                GhostsController lGhosts = new LegacyTeam();
//                                if (visual) {
//                                    ss.add(runVisual(mspacman, lGhosts, async));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss.add(runDark(mspacman, lGhosts, async));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            //GhostsController ghosts = new LegacyTeam();
//                            //GhostsController ghosts = new LegacyTeam2();
//                            case 2:
//                                MyGhostsTeamBot myGhosts = new MyGhostsTeamBot();
//                                if (visual) {
//                                    ss.add(runVisual(mspacman, myGhosts, async));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss.add(runDark(mspacman, myGhosts, async));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            case 3:
//                                ModifiedGhostTeamBot modGhosts = new ModifiedGhostTeamBot(0, 500);
//                                if (visual) {
//                                    ss.add(runVisual(mspacman, modGhosts, async));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss.add(runDark(mspacman, modGhosts, async));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            case 4:
//                                ModifiedGhostTeamBot modghosts1 = new ModifiedGhostTeamBot(99999, 0);
//                                if (visual) {
//                                    ss.add(runVisual(mspacman, modghosts1, async));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss.add(runDark(mspacman, modghosts1, async));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            default:
//                                System.out.println("Not Supposed to hit");
//                                break;
////                            }
//                        System.out.println(i);
//                        switch(i%4) {
//                            case 0:
//                                MyGhostsTeamBot myGhosts = new MyGhostsTeamBot();
//                                if (visual) {
//                                    ss.add(runVisual(mspacman, myGhosts, async, i%4));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss.add(runDark(mspacman, myGhosts, async, i%4));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            case 1:
//                                ModifiedGhostTeamBot modGhosts = new ModifiedGhostTeamBot(0, 500);
//                                if (visual) {
//                                    ss1.add(runVisual(mspacman, modGhosts, async, i%4));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss1.add(runDark(mspacman, modGhosts, async, i%4));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            case 2:
//                                ModifiedGhostTeamBot modghosts1 = new ModifiedGhostTeamBot(9999, 500);
//                                if (visual) {
//                                    ss2.add(runVisual(mspacman, modghosts1, async, i%4));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss2.add(runDark(mspacman, modghosts1, async, i%4));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//                            case 3:
//                                ModifiedGhostTeamBot modghosts2 = new ModifiedGhostTeamBot(500, 500);
//                                if (visual) {
//                                    ss3.add(runVisual(mspacman, modghosts2, async, i%4));
//                                    //ss.add(runVisualLevel(mspacman, ghosts, 0));
//                                } else {
//                                    ss3.add(runDark(mspacman, modghosts2, async, i%4));
//                                    //ss.add(runDarkLevel(mspacman, ghosts, 0));
//                                }
//                                break;
//
//                            default:
//                                System.out.println("Not Supposed to hit");
//                                break;
//                        }

    					/*ghosts.css = css;
    					ghosts.cssr = cssr;*/

    				}
    				/*for (int k = 0; k < css.length; k++) {
    					if (css[k].sum()==0 && cssr[k].sum()==0) break;
    					System.out.println(k + "\t" + css[k].sum()+ "\t" + cssr[k].sum());
    				}*/
    			//}
    			System.out.println("Base MCTS Ghost vs Smart Pill Controller " + ss);
//                System.out.println("Mod Ghost 1" + ss1);
//                System.out.println("Mod Ghost 2" + ss2);
////                System.out.println("Mod Ghost 3" + ss3);

                //writerM.write(ss.mean() + "\t");
    			//writerM.flush();
    			//writerS.write(ss.sd() + "\t");
    			//writerS.flush();
    		}
    		//writerI.write("\n");
    		//writerI.flush();
    		//writerM.write("\n");
    		//writerM.flush();
    		//writerS.write("\n");
    		//writerS.flush();
    	}
    	//writerI.close();
    	//writerM.close();
    	//writerS.close();
    	
    	/*int Ks[] = {}, maxDatas[] = {};
    	TreeNode.KNNTick = 40;
		TreeNode.maxTick = 400;
    	for (int k : Ks){
    		StatSummary ss = new StatSummary();
    		System.out.println("K = " + k + "-" + TreeNode.KNNTick + "-" + TreeNode.maxTick);
    		for (int i = 0; i < nTimes; i++){
    			//MsPacManController mspacman = null;//allows you to play the game yourself using the keyboard(arrow keys)
    			//MsPacManController mspacman = new RandomMsPacMan();
    			//MsPacManController mspacman = new RandomNonReverseMsPacMan();
    			//MsPacManController mspacman = new SimplePillEatingMsPacMan();
    			//MsPacManController mspacman = new SmartPillEater();
    			MsPacManController mspacman = new BetterController();

        		//GhostsController ghosts = new RandomGhosts();
        		//GhostsController ghosts = new PincerTeam();
    			//GhostsController ghosts = new LegacyTeam();
    			//GhostsController ghosts = new LegacyTeam2();
    			GhostsController ghosts = new MyGhostsTeamBot();
    			KNNAgent.K = k;        
    			if (visual) {
    				ss.add(runVisual(mspacman, ghosts, async));
    			} else {
    				ss.add(runDark(mspacman, ghosts, async));
    			}
    		}
    		System.out.println(ss);
    	}
    	KNNAgent.K = 3;
    	for (int maxData : maxDatas){
    		StatSummary ss = new StatSummary();
    		System.out.println("maxData = " + maxData + "-" + TreeNode.KNNTick + "-" + TreeNode.maxTick);
    		for (int i = 0; i < nTimes; i++){
    			//MsPacManController mspacman = null;//allows you to play the game yourself using the keyboard(arrow keys)
    			//MsPacManController mspacman = new RandomMsPacMan();
    			//MsPacManController mspacman = new RandomNonReverseMsPacMan();
    			//MsPacManController mspacman = new SimplePillEatingMsPacMan();
    			//MsPacManController mspacman = new SmartPillEater();
    			MsPacManController mspacman = new BetterController();

        		//GhostsController ghosts = new RandomGhosts();
        		//GhostsController ghosts = new PincerTeam();
    			//GhostsController ghosts = new LegacyTeam();
    			//GhostsController ghosts = new LegacyTeam2();
    			GhostsController ghosts = new MyGhostsTeamBot();
    			MyGhostsTeamBot.maxData = maxData;        
    			if (visual) {
    				ss.add(runVisual(mspacman, ghosts, async));
    			} else {
    				ss.add(runDark(mspacman, ghosts, async));
    			}
    		}
    		System.out.println(ss);
    	}*/
    }

    public static int runDark(MsPacManController mspacman, GhostsController ghosts, boolean async) throws Exception {

        Maze maze = MazeOne.getMaze();
        GameState gs = new GameState(maze);
        gs.reset();
        Game game;

        if (!async)
            game = new Game(gs, null, mspacman, ghosts);
        else
            game = new GameThread(gs, null, mspacman, ghosts);

        game.gs.reset();
        //game.run(delay);
        game.run(0);
        System.out.println(" Final score: " + game.gs.score + ", ticks = " + game.gs.totalGameTicks + ", level = " + game.gs.getLevel());
        
        return game.gs.score;
    }
    
    public static int runDarkLevel(MsPacManController mspacman, GhostsController ghosts, int level) throws Exception {

        Maze maze = MazeOne.getMaze();
        GameState gs = new GameState(maze);
        gs.reset();
        gs.level = level;
		gs.maze = Level.getMaze(level);
        Game game;

        game = new Game(gs, null, mspacman, ghosts);
        

        game.gs.reset();
        game.runLevel(0,level);
        //game.run(1);
        //System.out.println("Final score: " + game.gs.score + ", ticks = " + game.gs.totalGameTicks + ", level = " + game.gs.getLevel());
        
        return game.gs.score;
    }

    public static int runVisual(MsPacManController mspacman, GhostsController ghosts, boolean async) throws Exception {
        Maze maze = MazeOne.getMaze();
        GameState gs = new GameState();
        
        gs.reset();
        GameStateView gsv = new GameStateView(gs);
        JEasyFrame fr = new JEasyFrame(gsv, "Pac-Man vs Ghosts", true);

        if (mspacman == null) {
            KeyController kc = new KeyController();
            fr.addKeyListener(kc);
            mspacman = kc;
        }

        Game game;

        if (!async)
            game = new Game(gs, gsv, mspacman, ghosts);
        else
            game = new GameThread(gs, gsv, mspacman, ghosts);

        game.frame = fr;

        game.run(delay);
        //game.run(4);
        System.out.println(" Final score: " + game.gs.score + ", ticks = " + game.gs.totalGameTicks + ", level = " + game.gs.getLevel());
        return game.gs.score;
    }
    
    public static int runVisualLevel(MsPacManController mspacman, GhostsController ghosts, int level) throws Exception {
        GameState gs = new GameState();
        
        gs.reset();
        gs.level = level;
		gs.maze = Level.getMaze(level);
        GameStateView gsv = new GameStateView(gs);
        JEasyFrame fr = new JEasyFrame(gsv, "Pac-Man vs Ghosts", true);

        if (mspacman == null) {
            KeyController kc = new KeyController();
            fr.addKeyListener(kc);
            mspacman = kc;
        }

        Game game;
        game = new Game(gs, gsv, mspacman, ghosts);
        
        game.frame = fr;
        game.runLevel(0, level);
        //game.run(4);
        
        return game.gs.score;
    }
}