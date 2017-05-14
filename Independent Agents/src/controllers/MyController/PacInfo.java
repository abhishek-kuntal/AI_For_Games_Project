package controllers.MyController;

//package controllers;

import java.util.ArrayList;

import core.*;
import core.utilities.*;
import core.maze.*;

// Collect info about curGhost and Pacman

public class PacInfo implements Constants{
	/*
	public static final int d2Pac = 0, d2NGhost = 1; // distance to pac and distance to nearest ghost
	// distance from Pac to current ghost nearest ghost and Pac nearest ghost 
	public static final int dPac2NGhost = 2, dPac2PacNGhost = 3; 
	// distance to nearest Powerpill and from Pac to current ghost's nearest powerpill
	public static final int d2NPP = 4, dPac2NPP = 5;
	public static final int d2PacNPP = 6, dPac2PacNPP = 7;
	public static final int d2NPi = 8, dPac2NPi = 9;
	public static final int d2PacNPi = 10, dPac2PacNPi = 11;
	//Distance from curGhost and Pac to the curGhost's nearest intersection
	public static final int d2NCross = 12, dPac2Ncross = 13;
	//Distance from curGhost and Pac to the Pac's nearest intersection
	public static final int d2PacNCross = 14, dPac2PacNCross = 15;
	*/
	public static final int d2NG1 = 0, d2NG2 = 1; // distance to nearest ghost 1, 2
	public static final int d2NPP = 2; // distance to the nearest powerpill 
	public static final int d2NPi = 3; // distance to the nearest pill
	public static final int d2NPPNG = 4; // dist to the nearest PP's nearest ghost
	public static final int d2NCross = 5; //dist to the nearest crossroad
	//public static final int d2NCrNG = 6; // dist to the nearest CR's nearest ghost
	public static final int dNPP2NPNG = 6;
	//public static final int dNCr2NCrNG = 8;
	
	static double epsilon = 1e-6;
	
	MazeInterface maze;
	ArrayList<Node> junctions;
		
	public PacInfo (){
		
	}
	
	public PacInfo (ArrayList<Node> junctions){
		this.junctions = junctions;
	}
	
	public PacInfo (PacInfo pacInfo){
		this.maze = pacInfo.maze;
		this.junctions = pacInfo.junctions;
	}
	
	public synchronized VectorND getInfo(GameStateInterface gs, int pacDir){
		return getInfo(new SimGameState(gs));
	}
	
	public synchronized VectorND getInfo(GameStateInterface gs, int pacDir, int steps){
		return getInfo(new SimGameState(gs),pacDir,steps);
	}
	
	public synchronized VectorND getInfo(GameStateInterface gs){
		return getInfo(new SimGameState(gs));
	}
	
	public synchronized VectorND getInfo(GameStateInterface gs, Node pac){
		return getInfo(new SimGameState(gs), pac);
	}
	
	public synchronized VectorND getInfo(SimGameState gs, int pacDir){
		Node pac = gs.getPacman().current;
		if(maze != gs.getMaze()) {
			maze = gs.getMaze();
			junctions = getJunctions(maze);
		}
		if(maze.getNode(pac.x + dx[pacDir], pac.y + dy[pacDir]) != null)
			pac = maze.getNode(pac.x + dx[pacDir], pac.y + dy[pacDir]);
		return getInfo(gs, pac);
	}
	
	public synchronized VectorND getInfo(SimGameState gs, int pacDir, int steps){
		Node pac = gs.getPacman().current;
		if(maze != gs.getMaze()) {
			maze = gs.getMaze();
			junctions = getJunctions(maze);
		}
		for (int i = 0; i < steps; i++){
			if(maze.getNode(pac.x + dx[pacDir], pac.y + dy[pacDir]) != null)
				pac = maze.getNode(pac.x + dx[pacDir], pac.y + dy[pacDir]);
		}
		return getInfo(gs, pac);
	}
	
	public synchronized VectorND getInfo(SimGameState gs){
		return getInfo(gs, gs.getPacman().current);
	}
	
	public synchronized VectorND getInfo(SimGameState gs, Node pac){
		if(maze != gs.getMaze()) {
			maze = gs.getMaze();
			junctions = getJunctions(maze);
		}
		
		SimGhosts nrG1 = null, nrG2 = null, nrPPNG = null, nrCrNG = null;
		Node nrPP, nrPi;
		Node nrCr = null;	
				
		double ds[] = new double [10];
		
		int d = Integer.MAX_VALUE;
		for (int i=0; i < nGhosts; i++){
			if (maze.dist(pac, gs.getGhosts()[i].current) < d) {
				d = maze.dist(pac, gs.getGhosts()[i].current);
				nrG1 = gs.getGhosts()[i];
			}
		}
		
		d = Integer.MAX_VALUE;
		for (int i=0; i < nGhosts; i++){
			if (gs.getGhosts()[i] != nrG1){
				if (maze.dist(pac, gs.getGhosts()[i].current) < d ) {
					d = maze.dist(pac, gs.getGhosts()[i].current);
					nrG2 = gs.getGhosts()[i];
				}
			}
		}
		
		d = Integer.MAX_VALUE;
		nrPP = null;
		for (Node n : maze.getPowers()){
			if (gs.getPowers().get(n.powerIndex)){
				if (maze.dist(pac, n) < d){
					d = maze.dist(pac, n);
					nrPP = n;
				}
			}
		}
		
		d = Integer.MAX_VALUE;
		nrPi = null;
		for (Node n : maze.getPills()){
			if (gs.getPills().get(n.pillIndex)){
				if (maze.dist(pac, n) < d){
					d = maze.dist(pac, n);
					nrPi = n;
				}
			}
		}
		
		d = Integer.MAX_VALUE;
		nrPPNG = null;
		if (nrPP != null)
			for (int i=0; i < nGhosts; i++){
				if (maze.dist(gs.getGhosts()[i].current, nrPP) < d) {
					d = maze.dist(gs.getGhosts()[i].current, nrPP);
					nrPPNG = gs.getGhosts()[i];
				}
			}
		
		d = Integer.MAX_VALUE;
		for (Node n : junctions){
			if (maze.dist(pac, n) < d){
					d = maze.dist(pac, n);
					nrCr = n;
			}
		}
		/*
		d = Integer.MAX_VALUE;
		for (int i=0; i < nGhosts; i++){
			if (maze.dist(gs.getGhosts()[i].current, nrCr) < d) {
				d = maze.dist(gs.getGhosts()[i].current, nrCr);
				nrCrNG = gs.getGhosts()[i];
			}
		}
		*/
				
		ds[d2NG1] = maze.dist(nrG1.current, pac);
		ds[d2NG2] = maze.dist(nrG2.current, pac);
		if (nrPP != null){
			ds[d2NPP] = maze.dist(pac, nrPP);
		} else ds[d2NPP] = Integer.MAX_VALUE; 
		if (nrPi != null){
			ds[d2NPi] = maze.dist(pac, nrPi);
		} else ds[d2NPi] = Integer.MAX_VALUE;
		if (nrPPNG != null){
			ds[d2NPPNG] = maze.dist(pac, nrPPNG.current);
			ds[dNPP2NPNG] = maze.dist(nrPP, nrPPNG.current);
		} else ds[d2NPPNG] = ds[dNPP2NPNG] = Integer.MAX_VALUE;
		ds[d2NCross] = maze.dist(pac, nrCr);
		//ds[d2NCrNG] = maze.dist(pac, nrCrNG.current);
		//ds[dNCr2NCrNG] = maze.dist(nrCr, nrCrNG.current);
				
		if(nrG1.edible()) ds[7] = 50;
		else if(nrG1.returning()) ds[7] = 0;
		else ds[7] = -50;
		if(nrG2.edible()) ds[8] = 50;
		else if(nrG2.returning()) ds[8] = 0;
		else ds[8] = -50;
		if (nrPPNG != null){
			if(nrPPNG.edible()) ds[9] = 50;
			else if(nrPPNG.returning()) ds[9] = 0;
			else ds[9] = -50;
		}
		/*
		if(nrCrNG.edible()) ds[12] = 50;
		else if(nrCrNG.returning()) ds[12] = 0;
		else ds[12] = -50;
		*/
		return new VectorND(ds);
	}
	
	public synchronized ArrayList<Node> getJunctions(MazeInterface maze) {
		//setup junctions 
		ArrayList<Node >junctions = new ArrayList<Node>();
		for (Node n : maze.getMap()){
			if (n.adj.size() > 2) junctions.add(n);
		}
		return junctions;
	}
}
