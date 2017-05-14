package controllers.testPacman;

import core.*;
import core.maze.*;
import core.utilities.Utilities;
import controllers.testPacman.BetterController;

public class NewDFS implements Constants{
	//GameStateInterface gf;
	//public NewDFS(GameStateInterface gf){
		//this.gf=gf;
	//}

	public int minCost=10000000;
	public int minX=1000000,minY=1000000;
	public int direction;
	public int kaisuu=0;
	public boolean cut=false;
	final public int NUMNODEY=30;
	final public int NUMNODEX=28;
	final int GHOSTCOSTBASE=500000;//コストベース
	public int powerPillX[]=new int[4],powerPillY[]=new int[4];
	public MNode[][] newNode=new MNode[NUMNODEY][NUMNODEX];
	int prevX=0,prevY=0;
	public int numPills=0;
	public int FirstAreaPill=0;
	//Node[] intersection = new Node[67] ;

	public void SIntersection(GameStateInterface gf){
		/*
		for(int i = 0; i < 67; i++){
			intersection[i]=new Node(0,0);
		}
		*/
		for(int i = 0; i < NUMNODEY; i++){
			for(int j = 0; j < NUMNODEX; j++){
				newNode[i][j]=new MNode();
			}
		}
		for(int i=0;i<4;i++){
			powerPillX[i]=0;
			powerPillY[i]=0;
		}
		//交差点や角の座標
		//1段目：0～5 6個
		/*
		intersection[0].x=7;
		intersection[0].y=9;
		intersection[1].x=27;
		intersection[1].y=9;
		intersection[2].x=39;
		intersection[2].y=9;
		intersection[3].x=75;
		intersection[3].y=9;
		intersection[4].x=87;
		intersection[4].y=9;
		intersection[5].x=107;
		intersection[5].y=9;
		//2段目：6～15 10個
		intersection[6].x=7;
		intersection[6].y=21;
		intersection[7].x=15;
		intersection[7].y=21;
		intersection[8].x=27;
		intersection[8].y=21;
		intersection[9].x=39;
		intersection[9].y=21;
		intersection[10].x=51;
		intersection[10].y=21;
		intersection[11].x=63;
		intersection[11].y=21;
		intersection[12].x=75;
		intersection[12].y=21;
		intersection[13].x=87;
		intersection[13].y=21;
		intersection[14].x=99;
		intersection[14].y=21;
		intersection[15].x=107;
		intersection[15].y=21;
		//3段目：16～23 8個
		intersection[16].x=15;
		intersection[16].y=37;
		intersection[17].x=27;
		intersection[17].y=37;
		intersection[18].x=39;
		intersection[18].y=37;
		intersection[19].x=51;
		intersection[19].y=37;
		intersection[20].x=63;
		intersection[20].y=37;
		intersection[21].x=75;
		intersection[21].y=37;
		intersection[22].x=87;
		intersection[22].y=37;
		intersection[23].x=99;
		intersection[23].y=37;
		//4段目:24～27 4個
		intersection[24].x=15;
		intersection[24].y=49;
		intersection[25].x=39;
		intersection[25].y=49;
		intersection[26].x=57;
		intersection[26].y=49;
		intersection[27].x=99;
		intersection[27].y=49;
		//5段目:28～31 4個
		intersection[28].x=27;
		intersection[28].y=61;
		intersection[29].x=39;
		intersection[29].y=61;
		intersection[30].x=75;
		intersection[30].y=61;
		intersection[31].x=87;
		intersection[31].y=61;
		//6段目:32～39 8個
		intersection[32].x=15;
		intersection[32].y=73;
		intersection[33].x=27;
		intersection[33].y=73;
		intersection[34].x=39;
		intersection[34].y=73;
		intersection[35].x=51;
		intersection[35].y=73;
		intersection[36].x=63;
		intersection[36].y=73;
		intersection[37].x=75;
		intersection[37].y=73;
		intersection[38].x=87;
		intersection[38].y=73;
		intersection[39].x=99;
		intersection[39].y=73;
		//7段目:40～45 6個
		intersection[40].x=15;
		intersection[40].y=85;
		intersection[41].x=39;
		intersection[41].y=85;
		intersection[42].x=51;
		intersection[42].y=85;
		intersection[43].x=63;
		intersection[43].y=85;
		intersection[44].x=75;
		intersection[44].y=85;
		intersection[45].x=99;
		intersection[45].y=85;
		//8段目:46～55 10個
		intersection[46].x=7;
		intersection[46].y=97;
		intersection[47].x=15;
		intersection[47].y=97;
		intersection[48].x=27;
		intersection[48].y=97;
		intersection[49].x=39;
		intersection[49].y=97;
		intersection[50].x=51;
		intersection[50].y=97;
		intersection[51].x=63;
		intersection[51].y=97;
		intersection[52].x=75;
		intersection[52].y=97;
		intersection[53].x=87;
		intersection[53].y=97;
		intersection[54].x=99;
		intersection[54].y=97;
		intersection[55].x=107;
		intersection[55].y=97;
		//9段目:56～59 4個
		intersection[56].x=39;
		intersection[56].y=109;
		intersection[57].x=51;
		intersection[57].y=109;
		intersection[58].x=63;
		intersection[58].y=109;
		intersection[59].x=75;
		intersection[59].y=109;
		//10段目:60～65 6個
		intersection[60].x=7;
		intersection[60].y=121;
		intersection[61].x=27;
		intersection[61].y=121;
		intersection[62].x=39;
		intersection[62].y=121;
		intersection[63].x=75;
		intersection[63].y=121;
		intersection[64].x=87;
		intersection[64].y=121;
		intersection[65].x=107;
		intersection[65].y=121;
		//ノードから交差点（角）の情報(ix)を習得
		*/
		MazeInterface maze=gf.getMaze();
		int num=0;

		/*
		for(int i=0;i<66;i++){
			System.out.println("x==  "+intersection[i].x+"  y=  "+intersection[i].y+"  ix=  "+intersection[i].ix);
			System.out.println();
		}
		*/
		int num2=0,maxX=0,maxY=0;

		int powerPillcounter=0;
		//ノードを作成（２次元配列に変換）
		for (int i=0;i<maze.getMap().size();i++) {
			if(maze.getNode(i)!=null)
				if((maze.getNode(i).x-3)%4==0){
					if((maze.getNode(i).y-5)%4==0){
						Node n = maze.getNode(i);
						int y = (maze.getNode(i).y-5)/4, x = (maze.getNode(i).x-3)/4;
						int ix;
						newNode[y][x].canPoint=true;
						newNode[y][x].ix=i;
						if ((ix = maze.getNode(i).pillIndex) != -1 && gf.getPills().get(ix)){
							newNode[y][x].pill=10;
						}
						if ((ix = maze.getNode(i).powerIndex) != -1 && gf.getPowers().get(ix)){
							newNode[y][x].pill=50;
							//System.out.println(maze.getNode(i).nodeIndex);
						}
						newNode[y][x].x=x;
						newNode[y][x].y=y;
						newNode[y][x].area=1;
						if(newNode[y][x].pill==50){
							powerPillX[powerPillcounter]=x;
							powerPillY[powerPillcounter]=y;
							powerPillcounter++;
							newNode[y][x].area=2;
							//System.out.println(y+","+x);
						}
						/*
						for(int k=0;k<67;k++){
							if(maze.getNode(i).y==intersection[k].y){
								if(maze.getNode(i).x==intersection[k].x){
									newNode[y][x].intersection=true;
									//System.out.println(maze.getNode(i));
								  }
							}
						}
						*/
						if (n.adj.size() > 2 || (n.adj.size() == 2 && n.adj.get(0).x != n.adj.get(1).x && n.adj.get(0).y != n.adj.get(1).y)){
							newNode[y][x].intersection=true;
							//System.out.println(n.x + "\t" + n.y);
						}
						/*
						newNode[i].x=maze.na[i].x;
						newNode[i].y=maze.na[i].y;
						newNode[i].pill=maze.na[i].pill;
						*/
						num2++;

					}
				}
        }

		int x[]=new int[4],y[]=new int[4];
		int nowPositionX,nowPositionY;

		x[0]=0;x[1]=1;x[2]=0;x[3]=-1;
		y[0]=-1;y[1]=0;y[2]=1;y[3]=0;

		boolean e=false;
		//次の交差点調べる
		for(int i=0;i<NUMNODEY;i++){
			for(int j=0;j<NUMNODEX;j++){
				if(newNode[i][j].canPoint){
					Node curNode = maze.getNode(newNode[i][j].ix);
					//Warp point
					for (int k = 0; k < 4; k++){
						int tX = j, tY = i;
						tX += x[k];
						tY += y[k];
						if(tX<0) tX = NUMNODEX;
						if(tX >= NUMNODEX) tX=0;
						if(tY < 0) tY = NUMNODEY;
						if(tY >= NUMNODEY) tY=0;
						if (newNode[tY][tX].canPoint){
							Node n = maze.getNode(newNode[tY][tX].ix);
							if (Math.abs(curNode.x - n.x) + Math.abs(curNode.y - n.y) > 4 )
								newNode[i][j].area = 3;
						}
					}
					for(int k=0;k<4;k++){
						nowPositionX=j;
						nowPositionY=i;
						while(!e){
							nowPositionX+=x[k];
							nowPositionY+=y[k];

							if(nowPositionX<0){
								nowPositionX=27;
							}
							if(nowPositionX>=NUMNODEX){
								nowPositionX=0;
							}
							if(nowPositionY<0){
								nowPositionY=29;
							}
							if(nowPositionY>=NUMNODEY){
								nowPositionY=0;
							}
							if(newNode[nowPositionY][nowPositionX].canPoint){
								if(newNode[nowPositionY][nowPositionX].intersection){
									newNode[i][j].nextNodeX[k]=nowPositionX;
									newNode[i][j].nextNodeY[k]=nowPositionY;
									newNode[i][j].turnCounter++;
									e=true;
								}
							}
							else{
								e=true;
							}
						}
						e=false;
					}
					if(newNode[i][j].turnCounter==2){
						newNode[i][j].corner2=true;
					}
				}
			}
		}

		/*
		for(int i=0;i<4;i++){
			if(CanPoint(newNode[8][24],i)){
			System.out.println("Y "+newNode[8][24].nextNodeY[i]+" X "+newNode[8][24].nextNodeX[i]+" direction "+i);
			System.out.println("distance "+gf.getMaze().dist(maze.getNode(newNode[newNode[8][24].nextNodeY[i]][newNode[8][24].nextNodeX[i]].ix),maze.getNode(newNode[8][24].ix)));
			}
		}
		*/

		//パワーピルエリア改
		int powAreaX=0,powAreaY=0;/**1深さめのパワーピルエリア用*/
		int powAreaX2=0,powAreaY2=0;/**2深さめのパワーピルエリア用*/
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				powAreaX=newNode[powerPillY[i]][powerPillX[i]].nextNodeX[j];
				powAreaY=newNode[powerPillY[i]][powerPillX[i]].nextNodeY[j];
				if(powAreaX!=-1){
					SetPowerPillArea(powerPillX[i],powerPillY[i],powAreaX,powAreaY,9);
				}
			}
		}
		//ワープエリア
		/*
		newNode[8][0].area=3;
		newNode[8][1].area=3;
		newNode[8][2].area=3;
		newNode[8][25].area=3;
		newNode[8][26].area=3;
		newNode[8][27].area=3;
		newNode[17][0].area=3;
		newNode[17][1].area=3;
		newNode[17][2].area=3;
		newNode[17][25].area=3;
		newNode[17][26].area=3;
		newNode[17][27].area=3;
		*/
		for(int i=0;i<NUMNODEY;i++){
			for(int j=0;j<NUMNODEX;j++){
				if(newNode[i][j].canPoint && newNode[i][j].area == 3){
					Node node = maze.getNode(newNode[i][j].ix);
					for (Node n : node.adj){
						Node cur = n, pre = node;
						while (cur.adj.size() < 3){
							if ((cur.x - 3)%4 == 0 && (cur.y - 5)%4 == 0){
								newNode[(cur.y - 5)/4][(cur.x - 3)/4].area = 3;
							}
							for (Node tmp : cur.adj) if (tmp != pre){pre = cur; cur = tmp; break;}
						}
					}
				}
			}
		}
		//エリア確認用
		/*
		for(int i=0;i<NUMNODEY;i++){
			for(int j=0;j<NUMNODEX;j++){
				System.out.print(newNode[i][j].area+",");
			}
			System.out.println("");
		}
		System.out.println("****************************");
		for(int i=0;i<NUMNODEY;i++){
			for(int j=0;j<NUMNODEX;j++){
				if (newNode[i][j].corner2)
					System.out.print("c,");
				else if (newNode[i][j].intersection)
					System.out.print("i,");
				else System.out.print("0,");
			}
			System.out.println();
		}
		*/

	}

	public int run(GameStateInterface gf,int num,MNode next,boolean costFlag, MazeInterface maze){
		MNode node=newNode[CoordinateToNodeY(gf.getPacman().current.y)][CoordinateToNodeX(gf.getPacman().current.x)];
		int prevDirection;
		int temp;
		SetGhostCost(gf,costFlag);
		minCost=1000000;
		kaisuu=num;
		cut=false;
		//System.out.println("===============================================");
		//System.out.println(node.x+","+node.y);
		//System.out.println("         pacX  = "+CoordinateToNodeX(gf.game.pacman.current.x)+" pacY = "+CoordinateToNodeY(gf.game.pacman.current.y));
		//System.out.println("         nextX = "+next.x+" nextY = "+next.y);
		/*
		for(int i=0;i<4;i++){
			if(CanPoint(node,i)){
				System.out.println("nextX["+i+"] = "+node.nextNodeX[i]+" nextY["+i+"] = "+node.nextNodeY[i]+" cost = "+node.totalCost);
			}
		}
		*/

		prevX=node.x;
		prevY=node.y;
		for(int i=0;i<4;i++){
			if(CanPoint(node,i)){
				prevDirection = i;
				SetGhostCost(gf, costFlag);
				//RootSearchA(gf, maze, next, costFlag, prevDirection,i);
				ComebackFunction(next,gf,num,node.nextNodeX[i],node.nextNodeY[i],node.totalCost,prevDirection,i);
				//System.out.println(" "+newNode[8][3].x+","+newNode[8][3].y+" cost "+newNode[8][3].totalCost);
				//System.out.println(" "+newNode[8][24].x+","+newNode[8][24].y+" cost "+newNode[8][24].totalCost);
				//System.out.println(" "+newNode[17][3].x+","+newNode[17][3].y+" cost "+newNode[17][3].totalCost);
				//System.out.println(" "+newNode[17][24].x+","+newNode[17][24].y+" cost "+newNode[17][24].totalCost);

				//System.out.println("direction  "+direction);
				//System.out.println("-------------");

			}
			if(cut){
				break;
			}
		}

		//System.out.println("nextX = "+next.x+" nextY = "+next.y);
		//System.out.println("minX = "+minX+" minY = "+minY+" direction "+direction);
		//System.out.println("direction = "+direction+" pacX = "+CoordinateToNodeX(gf.game.pacman.current.x)+" pacY = "+CoordinateToNodeY(gf.game.pacman.current.y));
		//System.out.println("direction  "+direction);

		//pp(node.x,node.y);

		return direction;
	}


	/**
	 * Astarアルゴリズム近距離用
	 * */

	public  int RootSearchA(GameStateInterface gf, MazeInterface maze, MNode next, boolean costFlag, int prevDirection, int firstDirection) {
		int direction = 0;
		RootQueue<MNode> queue = new RootQueue<MNode>();
		MNode check, child, parent;
		int rootPoint[] = new int[4];
		MNode goal = next;
		int max;
		int maxDepth;
		boolean outFlag = false;
		int childNum;
		int depth;
		int rootDepth[] = new int[4];

		int distA=0,distB=0,distC=0,distCost=0;

		if(gf.getPacman().current.x % 4  != 3 || gf.getPacman().current.y % 4 != 1) return NEUTRAL;

		int pacX=CoordinateToNodeX(gf.getPacman().current.x);
		int pacY=CoordinateToNodeY(gf.getPacman().current.y);

		//前回のノード調べる
		prevX=newNode[pacY][pacX].x;
		prevY=newNode[pacY][pacX].y;
		prevNode(prevDirection,prevX,prevY);

			// パックマンから見て経路を探索する
		for (int i = 0; i < 4; i++) {
			if(newNode[pacY][pacX].nextNodeY[i]==-1) continue;
			if (newNode[newNode[pacY][pacX].nextNodeY[i]][newNode[pacY][pacX].nextNodeX[i]] == null) {
				rootPoint[i] = 100000000;
				continue;
			} else {
				rootPoint[i] = 0;
				// キューに最初の要素を入れる（パックマンの横の交差点）
				queue.enqueue(newNode[newNode[pacY][pacX].nextNodeY[i]][newNode[pacY][pacX].nextNodeX[i]], newNode[pacY][pacX], 0);

				// キューが空になるまで繰り返す
				while (!queue.isEmpty()) {

					// キューから最初の要素を取り出す
					check = queue.dequeue();
					parent = queue.getParent();
					depth = queue.getDepth();
					if (depth == 4) {
						outFlag = true;
					}
					if(outFlag && !newNode[pacY][pacX].intersection){
						if((pacX>next.x&&check.x<next.x) ||
							(pacX<next.x&&check.x>next.x)){
							if(pacY==next.y){
								depth=-1;
								//System.out.println("目標地点通過 pac");
							}
						}
						if((pacY>next.y&&check.y<next.y) ||
							(pacY<next.y&&check.y>next.y)){
							if(pacX==next.x){
								depth=-1;
								//System.out.println("目標地点通過 pac");
							}
						}
					}
					else{
						if((check.x>=next.x&&check.x<=next.x) ||
							(check.x<=next.x&&newNode[prevY][prevX].x>=next.x)){
							if(check.y==next.y){
								depth=-1;
								//System.out.println("目標地点通過");
							}
						}
						if((check.y>=next.y&&newNode[prevY][prevX].y<=next.y) ||
							(check.y<=next.y&&newNode[prevY][prevX].y>=next.y)){
							if(check.x==next.x){
								depth=-1;
								//System.out.println("目標地点通過");
							}
						}
					}
					// 要素に対し処理を行う
					rootPoint[i] += newNode[check.y][check.x].totalCost;

					// 要素の子供をキューにいれる
					if (outFlag) {
						//距離コスト＋コスト
						distA=maze.dist(maze.getNode(newNode[next.y][next.x].ix),maze.getNode(newNode[pacY][pacX].ix));
						distB=maze.dist(maze.getNode(check.ix),maze.getNode(newNode[next.y][next.x].ix));
						distC=maze.dist(maze.getNode(check.ix),maze.getNode(newNode[pacY][pacX].ix));
						distCost=(distB/4+distC/4)-distA/4;
						check.totalCost+=(parent.totalCost+distCost);

						//目標地点に着いてたら優先的に上書きする
						if(minCost>=check.totalCost && depth==-1){
							minCost=check.totalCost;
							minX=check.x;
							minY=check.y;
							//direction=firstDirection;
							//System.out.println("minCost  "+minCost);
							direction=firstDirection;

							if((next.x==1 && (next.y==2 || next.y==27)) || (next.x==26 && (next.y==2 || next.y==27))){
								minCost=newNode[prevY][prevX].totalCost;
								minX=prevX;
								minY=prevY;
							}
						}
						else{
							if(minCost>check.totalCost|| (minCost>=check.totalCost && (prevDirection==0 || prevDirection==3))){
								minCost=check.totalCost;
								minX=check.x;
								minY=check.y;
								//direction=firstDirection;
								//System.out.println("minCost  "+minCost);
								direction=firstDirection;

								if((next.x==1 && (next.y==2 || next.y==27)) || (next.x==26 && (next.y==2 || next.y==27))){
									minCost=newNode[prevY][prevX].totalCost;
									minX=prevX;
									minY=prevY;
								}

							}
						}
					} else {
						distA=maze.dist(maze.getNode(newNode[next.y][next.x].ix),maze.getNode(newNode[pacY][pacX].ix));
						distB=maze.dist(maze.getNode(check.ix),maze.getNode(newNode[next.y][next.x].ix));
						distC=maze.dist(maze.getNode(check.ix),maze.getNode(newNode[pacY][pacX].ix));
						distCost=(distB/4+distC/4)-distA/4;
						check.totalCost+=(parent.totalCost+distCost);
						childNum = 0;
						for (int j = 0; j < 4; j++) {
							if(newNode[check.y][check.x].nextNodeY[j]==-1) continue;
							if ((child = newNode[newNode[check.y][check.x].nextNodeY[j]][newNode[check.y][check.x].nextNodeX[j]]) == null) {
								continue;
							} else {
								if (child == parent) {
									continue;
								} else {
									childNum++;
									queue.enqueue(child, check, depth + 1);
								}
							}
						}
					}
				}
			}
		}
/*
		max = 100000000;
		maxDepth = 0;

		for (int i = 0; i < 4; i++) {

			if (max > rootDepth[i]) {
				if (rootPoint[direction] > rootPoint[i]) {
					direction = i;
				}
			}

		}
*/
		System.out.println(direction);
		return direction;

	}



	/**
	 * 2009/12/28  丸山
	 * ComebackFunction(目標ノード,GameFrame,深さ,今のX座標,今のY座標,前回のノードのコスト,前回選んだ方向,最初の方向)
	 * 深さ優先探索の再帰関数
	 * 深さ分再帰する
	 */
	public void ComebackFunction(MNode next,GameStateInterface gf,int num,int nowX,int nowY,int cost,int prevDirection,int firstDirection){
		int nextX,nextY;
		int pacX=CoordinateToNodeX(gf.getPacman().current.x),pacY=CoordinateToNodeY(gf.getPacman().current.y);
		int distA=0,distB=0,distC=0;
		int distCost=0;
		MazeInterface maze = gf.getMaze();

		//前回のノード調べる
		prevNode(prevDirection,nowX,nowY);

		//目標地点を通り過ぎたかどうか
		if(num==kaisuu && !newNode[pacY][pacX].intersection){
			if((pacX>next.x&&newNode[nowY][nowX].x<next.x) ||
				(pacX<next.x&&newNode[nowY][nowX].x>next.x)){
				if(pacY==next.y){
					num=-1;
					//System.out.println("目標地点通過 pac");
				}
			}
			if((pacY>next.y&&newNode[nowY][nowX].y<next.y) ||
				(pacY<next.y&&newNode[nowY][nowX].y>next.y)){
				if(pacX==next.x){
					num=-1;
					//System.out.println("目標地点通過 pac");
				}
			}
		}

		else{
			if((newNode[nowY][nowX].x>=next.x&&newNode[prevY][prevX].x<=next.x) ||
				(newNode[nowY][nowX].x<=next.x&&newNode[prevY][prevX].x>=next.x)){
				if(newNode[nowY][nowX].y==next.y){
					num=-1;
					//System.out.println("目標地点通過");
				}
			}
			if((newNode[nowY][nowX].y>=next.y&&newNode[prevY][prevX].y<=next.y) ||
				(newNode[nowY][nowX].y<=next.y&&newNode[prevY][prevX].y>=next.y)){
				if(newNode[nowY][nowX].x==next.x){
					num=-1;
					//System.out.println("目標地点通過");
				}
			}
		}

		//最深についたかor目標地点に着いたか。
		if(num<=1){
				//距離コスト＋コスト
				distA=maze.dist(maze.getNode(newNode[next.y][next.x].ix),maze.getNode(newNode[pacY][pacX].ix));
				distB=maze.dist(maze.getNode(newNode[nowY][nowX].ix),maze.getNode(newNode[next.y][next.x].ix));
				distC=maze.dist(maze.getNode(newNode[nowY][nowX].ix),maze.getNode(newNode[pacY][pacX].ix));
				distCost=(distB/4+distC/4)-distA/4;
				newNode[nowY][nowX].totalCost+=(cost+distCost);

				//目標地点に着いてたら優先的に上書きする
				if(minCost>=newNode[nowY][nowX].totalCost && num==-1){
					minCost=newNode[nowY][nowX].totalCost;
					minX=nowX;
					minY=nowY;
					direction=firstDirection;
					//System.out.println("minCost  "+minCost);


					if((next.x==1 && (next.y==2 || next.y==27)) || (next.x==26 && (next.y==2 || next.y==27))){
						minCost=newNode[prevY][prevX].totalCost;
						minX=prevX;
						minY=prevY;
					}
				}
				else{
					if(minCost>newNode[nowY][nowX].totalCost
							|| (minCost>=newNode[nowY][nowX].totalCost && (firstDirection==0 || firstDirection==3))){

						minCost=newNode[nowY][nowX].totalCost;
						minX=nowX;
						minY=nowY;
						direction=firstDirection;
						//System.out.println("minCost  "+minCost);


						if((next.x==1 && (next.y==2 || next.y==27)) || (next.x==26 && (next.y==2 || next.y==27))){
							minCost=newNode[prevY][prevX].totalCost;
							minX=prevX;
							minY=prevY;
						}

					}
				}
				//System.out.println("minX = "+minX+" minY = "+minY+" cost = "+minCost+" direction = "+direction);
				return;
		}
		//深さに達してなかったら～
		else{
			//距離コスト＋コスト
			distA=maze.dist(maze.getNode(newNode[next.y][next.x].ix),maze.getNode(newNode[pacY][pacX].ix));
			distB=maze.dist(maze.getNode(newNode[nowY][nowX].ix),maze.getNode(newNode[next.y][next.x].ix));
			distC=maze.dist(maze.getNode(newNode[nowY][nowX].ix),maze.getNode(newNode[pacY][pacX].ix));
			distCost=(distB/4+distC/4)-distA/4;
			//System.out.println(newNode[nowY][nowX].totalCost);
			newNode[nowY][nowX].totalCost+=(cost+distCost);

			//System.out.println(newNode[nowY][nowX].totalCost+"  "+distCost+"  "+cost);
			//4方向
			for(int i=0;i<4;i++){
				//通れるか、前回いた方向じゃないか
				if(CanPoint(newNode[nowY][nowX],i) && i!=OppositeDirection(prevDirection)){
					nextX=newNode[nowY][nowX].nextNodeX[i];
					nextY=newNode[nowY][nowX].nextNodeY[i];
					num--;
					ComebackFunction(next,gf,num,nextX,nextY,newNode[nowY][nowX].totalCost,i,firstDirection);
				}
			}
		}
	}
	/**
	 * 2009/12/28  丸山
	 * CanPoint(現在のノード,調べたい方向){
	 * 調べたい方向にノードがあるかどうかチェックする
	 * 0:上、1:右、2:下、3:左
	 */
	boolean CanPoint(MNode node,int direction){
		if(node.nextNodeY[direction]!=-1
				&& node.nextNodeX[direction]!=-1){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 2009/12/28  丸山
	 * OppositeDirection(今の方向)
	 * 逆の方向を調べる、右⇔左、上⇔下
	 */
	int OppositeDirection(int nowDirection){
		if(nowDirection>=2){
			return nowDirection-2;
		}
		else{
			return nowDirection+2;
		}
	}
	/**
	 * 2009/12/28  丸山
	 * prevNode(前の方向,今のX座標,今のY座標)
	 * 前のノードを得る。
	 */
	void prevNode(int prevDirection,int nowX,int nowY){
		if(newNode[nowY][nowX].nextNodeX[OppositeDirection(prevDirection)]!=-1
				&& newNode[nowY][nowX].nextNodeY[OppositeDirection(prevDirection)]!=-1){
			prevX=newNode[nowY][nowX].nextNodeX[OppositeDirection(prevDirection)];
			prevY=newNode[nowY][nowX].nextNodeY[OppositeDirection(prevDirection)];
		}
	}

	/**
	 * 2009/1/2  丸山
	 * SetGhostCost(ゲームフレーム,OldMaze maze)
	 * ゴーストコスト計算
	 */
	public void SetGhostCost(GameStateInterface gf,boolean costFlag){
		int ghostX,ghostY;//ゴーストの座標
		//パックマンの座標
		int pacX=CoordinateToNodeX(gf.getPacman().current.x);
		int pacY=CoordinateToNodeY(gf.getPacman().current.y);
		int pacNextX[]=new int[2],pacNextY[]=new int[2];
		int agreement=0;
		int count=0;
		boolean check[]=new boolean[4];


		//コスト初期化
		initCost();

		if(!costFlag){
			return;
		}
		//角コスト
		SetCornerCost(gf);

		//ゴーストのいる場所にコストをつける
		for(int i=0;i<4;i++){
			agreement=0;
			count=0;
			ghostX=CoordinateToNodeX(gf.getGhosts()[i].current.x);
			ghostY=CoordinateToNodeY(gf.getGhosts()[i].current.y);
			if(!gf.getGhosts()[i].edible() && !gf.getGhosts()[i].returning()){
				newNode[ghostY][ghostX].totalCost+=GHOSTCOSTBASE;
				//ゴーストとパックマンが同じ交差点の間にいる時
				//（パックマンから見てゴースト側のコストを最大にする）
				for(int j=0;j<4;j++){
					if(newNode[pacY][pacX].area!=3){
						if(CanPoint(newNode[pacY][pacX],j)){
							if((newNode[pacY][pacX].x>newNode[ghostY][ghostX].x && newNode[pacY][pacX].nextNodeX[j]<newNode[ghostY][ghostX].x)
									|| (newNode[pacY][pacX].x<newNode[ghostY][ghostX].x && newNode[pacY][pacX].nextNodeX[j]>newNode[ghostY][ghostX].x)){
								if(pacY==ghostY){
									newNode[newNode[pacY][pacX].nextNodeY[j]][newNode[pacY][pacX].nextNodeX[j]].totalCost+=10000000;
									//System.out.println("入った"+" x "+newNode[pacY][pacX].nextNodeX[j]+" y "+newNode[pacY][pacX].nextNodeY[j]+" cost "+newNode[newNode[pacY][pacX].nextNodeY[j]][newNode[pacY][pacX].nextNodeX[j]].totalCost);
								}
							}
							else if((newNode[pacY][pacX].y>newNode[ghostY][ghostX].y && newNode[pacY][pacX].nextNodeY[j]<newNode[ghostY][ghostX].y)
									|| (newNode[pacY][pacX].y<newNode[ghostY][ghostX].y && newNode[pacY][pacX].nextNodeY[j]>newNode[ghostY][ghostX].y)){
								if(pacX==ghostX){
									newNode[newNode[pacY][pacX].nextNodeY[j]][newNode[pacY][pacX].nextNodeX[j]].totalCost+=10000000;
									//System.out.println("入った"+" x "+newNode[pacY][pacX].nextNodeX[j]+" y "+newNode[pacY][pacX].nextNodeY[j]+" cost "+newNode[newNode[pacY][pacX].nextNodeY[j]][newNode[pacY][pacX].nextNodeX[j]].totalCost);
								}
							}
						}
					}
					else{
						if(newNode[ghostY][ghostX].area==3){
							/*
							if(ghostY==pacY){
								if(ghostX>pacX && ghostX<3){
									newNode[newNode[pacY][pacX].nextNodeY[1]][newNode[pacY][pacX].nextNodeX[1]].totalCost+=10000000;
								}
								if(ghostX>3 && ghostX<pacX){
									newNode[newNode[pacY][pacX].nextNodeY[3]][newNode[pacY][pacX].nextNodeX[3]].totalCost+=10000000;
								}
							}
							*/
							Ghosts g = gf.getGhosts()[i];
							Node pac = gf.getPacman().current;
							Node cur = g.current, pre = g.previous;
							while (cur.adj.size() < 3){
								for (Node n : cur.adj) if (n != pre) {pre = cur; cur = n; break;}
								if (cur == pac) {
									int dir = Utilities.getWrappedDirection(cur, pre, gf.getMaze());
									if (newNode[pacY][pacX].nextNodeY[dir] != -1)
										newNode[newNode[pacY][pacX].nextNodeY[dir]][newNode[pacY][pacX].nextNodeX[dir]].totalCost+=10000000;
									else {
										dir = (dir + 2) % 4;
										for (int k = 0; k < 4; k++){
											if (k != dir && newNode[pacY][pacX].nextNodeY[k] != -1){
												newNode[newNode[pacY][pacX].nextNodeY[k]][newNode[pacY][pacX].nextNodeX[k]].totalCost+=10000000;
											}
										}
									}
									break;
								}
							}
						}
					}
				}
			}
		}

		//ゴーストから2深さ目までのノードにコストつける
		MNode ghost;
		MNode ghost2;
		MNode ghost3;
		for(int i=0;i<4;i++){
			if(!gf.getGhosts()[i].edible()){
				ghost=newNode[CoordinateToNodeY(gf.getGhosts()[i].current.y)][CoordinateToNodeX(gf.getGhosts()[i].current.x)];
				SetGhostCostRoop(2,ghost.x,ghost.y,ghost.x,ghost.y,9,gf.getMaze());
			}
		}
/*
		//コスト付いてるかチェック
		for(int i=0;i<4;i++){
			System.out.println("  X  "+CoordinateToNodeX(gf.game.ghosts[i].current.x)+"  Y  "+CoordinateToNodeY(gf.game.ghosts[i].current.y));
		}
		for(int i=0;i<NUMNODEY;i++){
			for(int j=0;j<NUMNODEX;j++){
				if(newNode[i][j].intersection&&newNode[i][j].totalCost!=0)
					System.out.println("x = "+j+" y = "+i+" cost = "+newNode[i][j].totalCost);
			}
		}
*/

	}

	void SetGhostCostRoop(int num,int baseGhostX,int baseGhostY,int ghostX,int ghostY,int prevDirection,MazeInterface maze){
		int X,Y;
		if(num<=1){
			for(int i=0;i<4;i++){
				if(CanPoint(newNode[ghostY][ghostX],i) && prevDirection!=OppositeDirection(i)){
					Y=newNode[ghostY][ghostX].nextNodeY[i];
					X=newNode[ghostY][ghostX].nextNodeX[i];
					newNode[Y][X].totalCost+=GHOSTCOSTBASE/(maze.dist(maze.getNode(newNode[Y][X].ix),maze.getNode(newNode[baseGhostY][baseGhostX].ix))*maze.dist(maze.getNode(newNode[Y][X].ix),maze.getNode(newNode[baseGhostY][baseGhostX].ix))/16);
				}
			}
			return;
		}
		else if(num>1){
			for(int i=0;i<4;i++){
				if(CanPoint(newNode[ghostY][ghostX],i) && prevDirection!=OppositeDirection(i)){
					Y=newNode[ghostY][ghostX].nextNodeY[i];
					X=newNode[ghostY][ghostX].nextNodeX[i];
					newNode[Y][X].totalCost+=GHOSTCOSTBASE/(maze.dist(maze.getNode(newNode[Y][X].ix),maze.getNode(newNode[baseGhostY][baseGhostX].ix))*maze.dist(maze.getNode(newNode[Y][X].ix),maze.getNode(newNode[baseGhostY][baseGhostX].ix))/16);
					num-=1;
					SetGhostCostRoop(num,baseGhostX,baseGhostY,X,Y,i,maze);
				}
			}
		}
	}

	/**
	 * 2009/1/2  丸山
	 * CoordinateToNodeX(int x)
	 * CoordinateToNodeY(int y)
	 * 座標からノードに変換
	 */
	public int CoordinateToNodeX(int x){
		int coordinate;
		coordinate=(x-3)/4;
		return coordinate;
	}
	public int CoordinateToNodeY(int y){
		int coordinate;
		coordinate=(y-5)/4;
		return coordinate;
	}

	/**
	 * 2009/1/6  丸山
	 *
	 * ピル上書きとピルの残りを数える
	 */

	public void SetPill(GameStateInterface gf){
		numPills=0;
		FirstAreaPill=0;
		for (int i=0;i<1302;i++) {
			if(gf.getMaze().getNode(i)!=null)
				if((gf.getMaze().getNode(i).x-3)%4==0){
					if((gf.getMaze().getNode(i).y-5)%4==0){
						int ix;
						newNode[(gf.getMaze().getNode(i).y-5)/4][(gf.getMaze().getNode(i).x-3)/4].pill=0;
						if ((ix = gf.getMaze().getNode(i).pillIndex) != -1 && gf.getPills().get(ix))
							newNode[(gf.getMaze().getNode(i).y-5)/4][(gf.getMaze().getNode(i).x-3)/4].pill=10;
						if ((ix = gf.getMaze().getNode(i).powerIndex) != -1 && gf.getPowers().get(ix))
							newNode[(gf.getMaze().getNode(i).y-5)/4][(gf.getMaze().getNode(i).x-3)/4].pill=50;
						if(newNode[(gf.getMaze().getNode(i).y-5)/4][(gf.getMaze().getNode(i).x-3)/4].pill>0){
							numPills+=1;
							if(newNode[(gf.getMaze().getNode(i).y-5)/4][(gf.getMaze().getNode(i).x-3)/4].area!=2){
								FirstAreaPill+=1;
							}
						}
					}
				}
		}

	}



	/**
	 * 2009/1/17  丸山
	 * SetPowerPillArea(いまのX座標,いまのY座標,目標のX座標,目標のY座標)
	 * パワーピルエリアセットする
	 * いまから目標に向かって進む
	 */
	protected void SetPowerPillArea(int nowX,int nowY,int nextX,int nextY,int prevDirection){
		if(nowY==nextY){
			if(nowX>nextX){
				for(int k=0;k<nowX-nextX;k++){
					newNode[nowY][nowX-(k)].area=2;
				}
			}
			else{
				for(int k=0;k<nextX-nowX;k++){
					newNode[nowY][nowX+(k)].area=2;
				}
			}
		}
		else if(nowX==nextX){
			if(nowY>nextY){
				for(int k=0;k<nowY-nextY;k++){
					newNode[nowY-(k)][nowX].area=2;
				}
			}
			else{
				for(int k=0;k<nextY-nowY;k++){
					newNode[nowY+(k)][nowX].area=2;
				}
			}
		}
		if(newNode[nextY][nextX].turnCounter<3){
			for(int i=0;i<4;i++){
				if(newNode[nextY][nextX].nextNodeX[i]!=-1 && OppositeDirection(prevDirection)!=i){
					SetPowerPillArea(nextX,nextY,newNode[nextY][nextX].nextNodeX[i],newNode[nextY][nextX].nextNodeY[i],i);
				}
			}
		}
		else{
			return;
		}
	}

	/**
	 * コーナーコストつける
	 */
	protected void SetCornerCost(GameStateInterface gf){
		MazeInterface maze = gf.getMaze();
		for(int i=0;i<NUMNODEY;i++){
			for(int j=0;j<NUMNODEX;j++){
				if(newNode[i][j].corner2){
					if(newNode[i][j].intersection){
						newNode[i][j].totalCost+=(10*CornerDist(j, i, -1,-1, maze)*(10*CornerDist(j, i, -1,-1, maze)));
						//System.out.println("x " +j+" y "+i+ " cost" +newNode[i][j].totalCost);
						Boolean gFlag = false;
						Node pre = gf.getMaze().getNode(newNode[i][j].ix);
						for (Node n : pre.adj){
							Node cur = n;
							while (cur.adj.size() < 3){
								for (int k = 0; k < 4; k++) 
									if(gf.getGhosts()[k].edible() && cur == gf.getGhosts()[k].current) {
										gFlag = true;
										break;
									}
								if (gFlag) break;
								for (Node tn : cur.adj) if(tn != pre){pre = cur; cur = tn; break;}
							}
							if (gFlag) break;
						}
						if (gFlag) newNode[i][j].totalCost -= 500;
					}
				}
			}
		}
	}

	public int CornerDist (int cornerX,int cornerY, int prevX, int prevY,MazeInterface maze){
		int dist=0;
		int newX,newY;
		for(int i=0;i<4;i++){
			if(CanPoint(newNode[cornerY][cornerX],i)){
				if(newNode[cornerY][cornerX].nextNodeY[i]==-1) continue;
				if(prevY == -1 || newNode[newNode[cornerY][cornerX].nextNodeY[i]][newNode[cornerY][cornerX].nextNodeX[i]]!=newNode[prevY][prevX]){
					newX=newNode[cornerY][cornerX].nextNodeX[i];
					newY=newNode[cornerY][cornerX].nextNodeY[i];
					dist+=maze.dist(maze.getNode(newNode[cornerY][cornerX].ix),maze.getNode(newNode[newY][newX].ix))/4;
					if(newNode[newY][newX].corner2){
						dist+=CornerDist(newX,newY,cornerX,cornerY,maze);
					}
				}
			}
		}
		return dist;
	}

	public MNode pp(int pacX,int pacY){
		//パックマンの2深さめまでのコストチェック
		int minCost=1000000;
		int pacAX,pacAY,pacBX,pacBY;
		int minX=0,minY=0;
//		System.out.println("Y "+pacY+" X "+pacX+" cost "+newNode[pacY][pacX].totalCost);
		for(int i=0;i<4;i++){
			pacAX=newNode[pacY][pacX].nextNodeX[i];
			pacAY=newNode[pacY][pacX].nextNodeY[i];
			if(CanPoint(newNode[pacY][pacX],i)){
				//System.out.println("Y "+pacAY+" X "+pacAX+" cost "+newNode[pacAY][pacAX].totalCost);
				if(minCost>newNode[pacAY][pacAX].totalCost){
					minCost=newNode[pacAY][pacAX].totalCost;
					minX=pacAX;
					minY=pacAY;
				}
				for(int j=0;j<4;j++){
					pacBY=newNode[pacAY][pacAX].nextNodeY[j];
					pacBX=newNode[pacAY][pacAX].nextNodeX[j];
					if(CanPoint(newNode[pacAY][pacAX],j)){
						//System.out.println("Y "+pacBY+" X "+pacBX+" cost "+newNode[pacBY][pacBX].totalCost);
						if(minCost>newNode[pacBY][pacBX].totalCost){
							minCost=newNode[pacBY][pacBX].totalCost;
							minX=pacBX;
							minY=pacBY;
						}
					}
				}
			}
		}
	return newNode[minY][minX];
	}

	public void initCost(){
		for(int i=0;i<NUMNODEY;i++){
			for(int j=0;j<NUMNODEX;j++){
				newNode[i][j].totalCost=0;
			}
		}
	}

}

