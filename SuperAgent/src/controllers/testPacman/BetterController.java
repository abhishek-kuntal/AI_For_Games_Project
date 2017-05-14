package controllers.testPacman;

import utilities.ElapsedTimer;
import competition.MsPacManController;
import core.*;
import core.maze.*;
import controllers.testPacman.parameter;
import controllers.testPacman.DFSInitThread;

public class BetterController implements MsPacManController, Constants {
	MazeInterface maze;
	GameStateInterface gf;
	NewDFS ndfs;

	
	
	private double[] DR ={19.093624849999998, 0.8497634084218519, 35.0, 13.0, 24.0, 0.654981644004646, 46.0, 25.0, 37.0, 0.9502374412707658, 41.0, 46.0, 5.0, 0.34022367467207604, 43.0, 22.0, 46.0, 0.5215748849390379, 29.0, 22.0, 3.0999999999999996, 0.20119836481062758, 15.0, 23.0, 18.0, 0.2530728202494148, 42.0, 39.0, 8.0, 0.4703458929837405, 28.4, 20.0, };
	public parameter p = new parameter( DR);
	
	//public parameter p;
	private int Current_Pacman_x;
	private int Current_Edible_Ghost_x;
	private int Current_Pacman_y;
	private int Current_Edible_Ghost_y;
	public int index;
	public MNode nextNode;
	public int minDistance=10000000;
	final public int NUMBER = 5;
	private int minDistGhostID=9;
	private int minDistGhostID2=9;
	private int minDistPowPillID=9;
	private int minDistGhostAndPowPillID=9;
	private int minDistGhost=9;
	private int minDistGhost2=9;
	private int minDistPowPill=9;
	private int minDistGhostAndPowPill=9;
	private int minDistGhostAndPowPill2=9;
	private int prevNumPills=0;
	private int direction;
	private boolean costFlag=false;
	private int powPillNum=0;
	private int prevPowPillNum=0;
	private int minDistEdible=9;
	private int minDistEdibleID=9;
	private int minDistEandG=1000000;
	private boolean goFlag=false;
	private boolean PPFlag=false;
	private boolean direction_1_flag=false;
	private boolean direction_3_flag=false;
	public int h=0;

	private DFSInitThread initThread;
	private boolean ready = false;

	public synchronized void setReady(){
		ready = true;
	}

	public synchronized void setMaze (GameStateInterface gf){
		this.maze = gf.getMaze();
	    this.gf=gf;
		nextNode=new MNode();

	    //深さ優先探索
	    ndfs=new NewDFS();
	    ndfs.SIntersection(gf);
	    ndfs.SetPill(gf);
	    prevNumPills=ndfs.numPills;
	    ndfs.SetGhostCost(gf, costFlag);

	    for(int i=0;i<ndfs.NUMNODEY;i++){
	    	for(int j=0;j<ndfs.NUMNODEX;j++){
	    		index=ndfs.newNode[i][j].ix;
	    		if(minDistance>maze.dist(gf.getPacman().current, maze.getNode(index))){
	    			minDistance=maze.dist(gf.getPacman().current, maze.getNode(index));
	    			nextNode.x=j;
	    			nextNode.y=i;
	    		}
	    	}
	    }
	    //System.out.println(" xxxxx "+nextNode.x+" yyyyyyyy "+nextNode.y);
	       ndfs.run(gf, NUMBER, nextNode,costFlag,maze);

	}

    /**
     *  最も近いゴーストとの距離を出力する
     * @param gf ゲームフレーム
     * @param maze オールドメイズ
     * @return 最短距離
     */
    int SetADistance(GameStateInterface gf,MazeInterface maze){
    	int minDistance=1000000;
    	minDistEdible=100000;
    	minDistEandG=1000000;
    	for(int i=0;i<4;i++){
    		if (gf.getGhosts()[i].returning()) continue;
    		if(!gf.getGhosts()[i].edible()){
	    		if(minDistance>maze.dist(gf.getPacman().current, gf.getGhosts()[i].current)/4){
	    			minDistance=maze.dist(gf.getPacman().current, gf.getGhosts()[i].current)/4;
	    			minDistGhostID=i;
	    		}
    		}
    		else{
    			if(minDistEdible>maze.dist(gf.getPacman().current, gf.getGhosts()[i].current)/4){
    				minDistEdible=maze.dist(gf.getPacman().current, gf.getGhosts()[i].current)/4;
	    			minDistEdibleID=i;
    			}
    		}

        	if(minDistance!=1000000&&minDistEdible!=100000){
        		minDistEandG=maze.dist(gf.getGhosts()[minDistGhostID].current, gf.getGhosts()[minDistEdibleID].current)/4;
            	//System.out.println("    dist = "+minDistEandG);
            }


    	}

    	return minDistance;
    }

    /**
     * 2番目に近いゴーストの距離を出力する
     * @param gf ゲームフレーム
     * @param maze オールドメイズ
     * @return 最短距離
     */
        int SetADistance2(GameStateInterface gf,MazeInterface maze){
        	int minDistance=1000000;
        	for(int i=0;i<4;i++){
        		if(!gf.getGhosts()[i].edible() && i!=minDistGhostID){
        			if(minDistance>maze.dist(gf.getGhosts()[i].current, maze.getNode(ndfs.newNode[ndfs.powerPillY[minDistPowPillID]][ndfs.powerPillX[minDistPowPillID]].ix))/4){
        				minDistance=maze.dist(gf.getGhosts()[i].current, maze.getNode(ndfs.newNode[ndfs.powerPillY[minDistPowPillID]][ndfs.powerPillX[minDistPowPillID]].ix))/4;
        				minDistGhostID2=i;
        			}
        		}
        	}
        	//System.out.println("    dist = "+minDistance+" id = "+minDistGhostAndPowPillID);
        	return minDistance;
        }

    /**
     *  最も近いパワーピルとの距離を出力する
     * @param gf ゲームフレーム
     * @param maze オールドメイズ
     * @return 最短距離
     */
    int SetBDistance(GameStateInterface gf,MazeInterface maze){
    	int minDistance=1000000;
    	prevPowPillNum=powPillNum;
    	powPillNum=0;
    	for(int i=0;i<4;i++){
    		if(ndfs.newNode[ndfs.powerPillY[i]][ndfs.powerPillX[i]].pill>0){
	    		if(minDistance>maze.dist(gf.getPacman().current, maze.getNode(ndfs.newNode[ndfs.powerPillY[i]][ndfs.powerPillX[i]].ix))/4){
	    			minDistance=maze.dist(gf.getPacman().current, maze.getNode(ndfs.newNode[ndfs.powerPillY[i]][ndfs.powerPillX[i]].ix))/4;
	    			minDistPowPillID=i;
	    		}
	    		powPillNum++;
    		}
    	}
    	//System.out.println("    dist = "+minDistance+" id = "+minDistPowPillID);
    	return minDistance;
    }
    /**
     *  最も近いパワーピルに最も近いゴーストの距離を出力する
     * @param gf ゲームフレーム
     * @param maze オールドメイズ
     * @return 最短距離
     */
    int SetCDistance(GameStateInterface gf,MazeInterface maze){
    	int minDistance=1000000;
    	for(int i=0;i<4;i++){
    		if(!gf.getGhosts()[i].edible()){
	    		if(minDistance>maze.dist(gf.getGhosts()[i].current, maze.getNode(ndfs.newNode[ndfs.powerPillY[minDistPowPillID]][ndfs.powerPillX[minDistPowPillID]].ix))/4){
	    			minDistance=maze.dist(gf.getGhosts()[i].current, maze.getNode(ndfs.newNode[ndfs.powerPillY[minDistPowPillID]][ndfs.powerPillX[minDistPowPillID]].ix))/4;
	    			minDistGhostAndPowPillID=i;
	    		}
    		}
    	}
    	//System.out.println("    dist = "+minDistance+" id = "+minDistGhostAndPowPillID);
    	return minDistance;
    }


	@Override
	public int getAction(GameStateInterface gf) {
		int Level;
		//ElapsedTimer t = new ElapsedTimer();
		if (maze != gf.getMaze()){
			setMaze(gf);
			setReady();
			/*if (initThread == null || !initThread.isAlive()){
				initThread = new DFSInitThread(gf, this);
				initThread.start();
				ready = false;
			}*/
		}
		if (!ready) return rand.nextInt(dx.length);
		if((Level = gf.getLevel()) >=8 ) Level-=8;
		costFlag=false;
		PPFlag = false;
    	minDistGhost=SetADistance(gf,maze);
    	minDistPowPill=SetBDistance(gf,maze);
    	minDistGhostAndPowPill=SetCDistance(gf,maze);
    	minDistGhost2=SetADistance2(gf,maze);
        // get all the successors of the current node
        Node cur = gf.getPacman().current;
        //System.out.println("pacX " + ndfs.CoordinateToNodeX(cur.x)+"  pacY  "+ndfs.CoordinateToNodeY(cur.y));

        //ndfs.pp(ndfs.CoordinateToNodeX(gf.getPacman().current.x),ndfs.CoordinateToNodeY(gf.getPacman().current.y));

        if(minDistGhost<(int) p.DR[Level*4]){
        	costFlag=true;
        	//System.out.println((minDistEdible+minDistGhost-minDistEandG)/(minDistEdible+minDistGhost+0.001));
        }
        prevNumPills=ndfs.numPills;
         ndfs.SetPill(gf);
         boolean checkEdible=true;
     	 for(int i=0;i<4;i++){
     		 if(gf.getGhosts()[i].edible()&&(gf.getGhosts()[i].edibleTime/2)>minDistEdible){
          			checkEdible=false;
     		 }
     	 }

     	 if(prevPowPillNum>powPillNum){
     		goFlag=false;
     	 }


     	 //パワーピル狙い

         if(costFlag&&checkEdible&&powPillNum>0){
    	 	nextNode.x=ndfs.powerPillX[minDistPowPillID];
			nextNode.y=ndfs.powerPillY[minDistPowPillID];
			nextNode.pill=ndfs.newNode[ndfs.powerPillY[minDistPowPillID]][ndfs.powerPillX[minDistPowPillID]].pill;
			//if(minDistGhostAndPowPill>minDistPowPill && minDistPowPill<10){

			if(minDistGhostAndPowPill>minDistPowPill && minDistPowPill<(int) p.DR[Level*4+2] && minDistGhost2<(int) p.DR[Level*4+3]){
				//if(minDistGhostAndPowPill>minDistPowPill && minDistPowPill<10){
				costFlag=false;
      			PPFlag=true;
			}


         }

         //エディブル狙い
         else if(!costFlag&&!checkEdible && (minDistEdible+minDistGhost-minDistEandG)/(minDistEdible+minDistGhost+0.001)<p.DR[Level*4+1]){
        	 nextNode.x=ndfs.CoordinateToNodeX(gf.getGhosts()[minDistEdibleID].current.x);
        	 nextNode.y=ndfs.CoordinateToNodeY(gf.getGhosts()[minDistEdibleID].current.y);
        	 Current_Pacman_x=gf.getPacman().current.x/4;
        	 Current_Edible_Ghost_x=gf.getGhosts()[minDistEdibleID].current.x/4;
        	 Current_Pacman_y=gf.getPacman().current.y/4;
        	 Current_Edible_Ghost_y=gf.getGhosts()[minDistEdibleID].current.y/4;

        	 if((Current_Pacman_x<=3) && (Current_Edible_Ghost_x>=25 || (Current_Edible_Ghost_x<3 && gf.getPacman().current.x-gf.getGhosts()[minDistEdibleID].current.x>=0)) && (Current_Pacman_y==Current_Edible_Ghost_y)) direction_3_flag=true;
        	 else if((Current_Pacman_x>=25) && (Current_Edible_Ghost_x<=3 || (Current_Edible_Ghost_x>25  && gf.getPacman().current.x-gf.getGhosts()[minDistEdibleID].current.x<=0)) && Current_Pacman_y==Current_Edible_Ghost_y) direction_1_flag=true;

         }

         else{
	         minDistance=1000000;
	         int minDist2=1000000;
	         for(int i=0;i<ndfs.NUMNODEY;i++){
		         	for(int j=0;j<ndfs.NUMNODEX;j++){
		         		index=ndfs.newNode[i][j].ix;
		         		//System.out.println(ndfs.newNode[i][j].pill);
		         		if(ndfs.newNode[i][j].pill>0){
		         			if(minDist2>=maze.dist(cur,gf.getMaze().getNode(index))/4){
		         				//System.out.println("minX  "+j+"  minY  "+i);
		         				minDist2=maze.dist(cur,gf.getMaze().getNode(index))/4;
		         			}
		         		}
		         	}
	         }
	         //minDistance=1000000;
	         for(int i=0;i<ndfs.NUMNODEY;i++){
	         	for(int j=0;j<ndfs.NUMNODEX;j++){
	         		index=ndfs.newNode[i][j].ix;
	         		//System.out.println(ndfs.newNode[i][j].pill);

	         		if(ndfs.newNode[i][j].pill>0){
	         			if(ndfs.newNode[i][j].area!=2 || (ndfs.FirstAreaPill==0) || (minDistPowPill>20 && minDist2<=10))
	         			if(minDistance>=maze.dist(cur,gf.getMaze().getNode(index))){
	         				//System.out.println("minX  "+j+"  minY  "+i);
	         				minDistance=maze.dist(cur,gf.getMaze().getNode(index));
	         				nextNode.x=j;
	         				nextNode.y=i;
	         				nextNode.pill=ndfs.newNode[i][j].pill;
	         			}

	         		}
	         	}
	        }
         }

         ndfs.SetGhostCost(gf,costFlag);
         //ndfs.run(gf, maze, 10, nextNode);
        // now simply find the closest one to this
    	//System.out.println(goFlag);
        //System.out.println(t);
         try {

        	if(minDistGhost>=4&&minDistPowPill<5&&minDistGhostAndPowPill>=6 && minDistGhost<=(int) p.DR[Level*4]&&PPFlag){
        		//goFlag=true;
        		return 4;
        	}

        	if(direction_1_flag){
        		direction=1;
        		direction_1_flag=false;
        		return direction;
        	}
        	else if(direction_3_flag){
        		direction=3;
        		direction_3_flag=false;
        		return direction;
        	}

        	else if(prevNumPills==ndfs.numPills &&(nextNode.x==ndfs.CoordinateToNodeX(cur.x)&&nextNode.y==ndfs.CoordinateToNodeY(cur.y))){
        		return direction;
        	}

        	else{
        		if(goFlag){
	        		return ndfs.run(gf, NUMBER, nextNode,false,maze);
	        		//return ndfs.RootSearchA(gf, maze, nextNode, false);
        		}
        		else{
	        		direction = ndfs.run(gf, NUMBER, nextNode,costFlag,maze);
	        		return ndfs.run(gf, NUMBER, nextNode,costFlag,maze);

	        		//direction = ndfs.RootSearchA(gf, maze, nextNode, costFlag);
	        		//return ndfs.RootSearchA(gf, maze, nextNode, costFlag);
        		}
        	}

        } catch (Exception e) {
            System.out.println(e);
            return NEUTRAL;
        }
	}

}
