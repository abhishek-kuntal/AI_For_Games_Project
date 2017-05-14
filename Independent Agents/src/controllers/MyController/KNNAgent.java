package controllers.MyController;

//package controllers;

import java.util.ArrayList;
import java.util.Random;

import core.*;
import core.utilities.Utilities;
import competition.*;

public class KNNAgent implements SimMsPacManController, Constants {
	
	public static int K = 3;
	public static final int maxRange = 4;
	static Random rand = new Random();
	static double epsilon = 1e-6;
	double trustLvl;
	VectorND[] sampleData, procData;
	VectorND maxVec = new VectorND(), minVec = new VectorND();
	PacInfo infoCollector;
	BetterRandomAgent randAgent;
	
	public KNNAgent(PacInfo infoCollector, VectorND sampleData[], VectorND procData[], double trustLvl){
		this.infoCollector = infoCollector;
		this.sampleData = sampleData;
		this.procData = procData;
		this.trustLvl = trustLvl;
		for (int i = 0; i < sampleData.length; i++) {
			maxVec.maxElVec(sampleData[i]);
			minVec.minElVec(sampleData[i]);
		}
		
		randAgent = new BetterRandomAgent();
	}
	
	public synchronized void reset(){
		randAgent = new BetterRandomAgent();
	}
	
	@Override
	public synchronized int getAction(SimGameState gs) {
		// TODO Auto-generated method stub
		
		//if((gs.getGameTick() + 2) % 4 != 0) {
		Node pac = gs.getPacman().current;
		Node tmp = pac;
		Node prev = null;
        Node tprev = null;
        for (Node n : pac.adj){
        	if (Utilities.getWrappedDirection(n, pac, gs.getMaze()) == gs.getPacman().curDir){
        		prev = tprev = n;
        		break;
        	}
        }
		if (prev != null){
			for (int i = 0; i < maxRange; i++){
				if (tmp.adj.size() > 2) break;
				for (Node n : tmp.adj) {
					if (n != tprev){
						tprev = tmp;
						tmp = n;
						break;
					}
				}
				for (int j = 0; j < nGhosts; j++){
					if (gs.getGhosts()[j].current == tmp) {
						//randAgent.setPreNode(gs.getPacman().current);
						//prev = pac;
						if (gs.getGhosts()[j].edible() || gs.getGhosts()[j].previous == tprev) {
							for (Node n : pac.adj) {
								if (n != prev){
									return Utilities.getWrappedDirection(pac, n, gs.getMaze());
								}
							}
						}
						// If thers's a ghost whose direction is different from MsPacman, reverse
						else return (gs.getPacman().curDir + 2) % 4;
					}
				}
				if (tmp.powerIndex >= 0 && gs.getPowers().get(tmp.powerIndex)) {
					for (Node n : pac.adj) {
						if (n != prev){
							return Utilities.getWrappedDirection(pac, n, gs.getMaze());
						}
					}
				}
			}
		}
		//prev = pac;
		if (pac.adj.size() < 2 ) return randAgent.getAction(gs);
		if (pac.adj.size() == 2 && (pac.adj.get(0).x == pac.adj.get(1).x || pac.adj.get(0).y == pac.adj.get(1).y)) 
			return randAgent.getAction(gs);
		if(infoCollector == null) return randAgent.getAction(gs);
		if(rand.nextDouble() > trustLvl) 
			return randAgent.getAction(gs);
		//randAgent.setPreNode(gs.getPacman().current);
		return KNNDecide(gs);
	}
	
	public synchronized int KNNDecide (SimGameState gs){
		Node pac = gs.getPacman().current;
		double dist[] = new double[sampleData.length];
		double distCand[] = new double[K];
		int[] candId = new int[K]; 
		VectorND curStat = infoCollector.getInfo(gs);
		VectorND nextStats[] = new VectorND[dx.length - 1];
		double d2NS[] = new double[dx.length - 1];
		VectorND expectStat = new VectorND();
		try{
			for (int j = 0; j < procData.length && j < sampleData.length; j++){
				VectorND maxV = maxVec.copy(), minV = minVec.copy();
				maxV.maxElVec(curStat);
				minV.minElVec(curStat);
				dist[j] = VectorND.sub(curStat, sampleData[j]).div(VectorND.sub(maxV, minV)).len();
			}
		}
		catch (Exception e){
			System.out.println(e);
			System.out.println("here1");
		}
		for (int i = 0; i < K; i++){
			double tmp = Double.MAX_VALUE;
			for (int j = 0; j < sampleData.length; j++){
				if (tmp > dist[j] && j < procData.length) {
					distCand[i] = tmp = dist[j];
					candId[i] = j;
				}
			}
			dist[candId[i]] = Double.MAX_VALUE;
		}
		double distCandSum = 0;
		for (int i = 0; i < K; i++){
			distCandSum += (1/(distCand[i] + epsilon));
		}
		try{
			for(int i = 0; i < K; i++){
				expectStat.add(VectorND.mul(procData[candId[i]], 1/((distCand[i] + epsilon)*distCandSum)));
			}
			for (int i = 0; i < dx.length-1; i++){
				if(gs.getMaze().getNode(pac.x + dx[i], pac.y + dy[i]) != null){
					nextStats[i] = VectorND.sub(infoCollector.getInfo(gs, i, 4), curStat);
					d2NS[i] = VectorND.sub(expectStat, nextStats[i]).len();
				}
				else d2NS[i] = Double.MAX_VALUE;
			}
		}
		catch (Exception e){
			System.out.println(e);
			System.out.println("here2");
		}
		double bestVal = Double.MAX_VALUE;
		int tmpDir = NEUTRAL;
		for (int i = 0; i < dx.length - 1; i++){
			double tmp = d2NS[i]+rand.nextDouble()*epsilon;
			if (bestVal > tmp){
				bestVal = tmp;
				tmpDir = i;
			}
		}
		return tmpDir;
	}
	
}
