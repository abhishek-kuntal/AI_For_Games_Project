package controllers.testPacman;

import core.GameStateInterface;

public class DFSInitThread extends Thread {
	
	GameStateInterface gs;
	BetterController controller;
	
	public DFSInitThread(GameStateInterface gs, BetterController controller){
		this.gs = gs;
		this.controller = controller;
	}
	
	public void run(){
		controller.setMaze(gs);
		controller.setReady();
	}
	
}
