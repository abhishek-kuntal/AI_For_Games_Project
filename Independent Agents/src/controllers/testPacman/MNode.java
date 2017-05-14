package controllers.testPacman;

public class MNode {
	/** x���W */public int x; 
	/** y���W */public int y; 
	/** �s���̎�� */public int pill; 
	/** ���ɍs����m�[�h��X���W */public int nextNodeX[]= new int[4];  
	/** ���ɍs����m�[�h��Y���W */public int nextNodeY[]= new int[4];  
	/** �p���[�s���R�X�g */public int powerpill_cost;
	/** �p�R�X�g */public int corner_cost; 
	/** �ʂ�邩�ǂ��� */public boolean canPoint; 
	/** �����_���ǂ����i�p���܂ށj */public boolean intersection;
	/** �p */public boolean corner2;
	/** index */public int ix;
	/** �s��������̐� */public int turnCounter; 
	/** �R�X�g */public int totalCost;
	/** �G���A�A�P:�ʂ��Ƃ� 2�F�p���[�s���G���A 3:���[�v�G���A */
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