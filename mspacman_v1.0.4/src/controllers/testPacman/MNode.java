package controllers.testPacman;

public class MNode {
	/** x座標 */public int x; 
	/** y座標 */public int y; 
	/** ピルの種類 */public int pill; 
	/** 次に行けるノードのX座標 */public int nextNodeX[]= new int[4];  
	/** 次に行けるノードのY座標 */public int nextNodeY[]= new int[4];  
	/** パワーピルコスト */public int powerpill_cost;
	/** 角コスト */public int corner_cost; 
	/** 通れるかどうか */public boolean canPoint; 
	/** 交差点かどうか（角も含む） */public boolean intersection;
	/** 角 */public boolean corner2;
	/** index */public int ix;
	/** 行ける方向の数 */public int turnCounter; 
	/** コスト */public int totalCost;
	/** エリア、１:通れるとこ 2：パワーピルエリア 3:ワープエリア */
	public int area;
	
	public MNode(){
		this.x=0;
		this.y=0;
		this.pill=0;
		this.powerpill_cost=0;
		this.corner_cost=0;
		this.canPoint=false;
		this.intersection=false;
		this.ix=0;
		this.turnCounter=0;
		this.corner2=false;
		this.totalCost=0;
		this.area=0;
		for(int i=0;i<4;i++){
			this.nextNodeX[i]=-1;
			this.nextNodeY[i]=-1;
		}
	}

}