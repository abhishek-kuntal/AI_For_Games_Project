package controllers.testPacman;
import java.util.Random;

/*
public class parameter {
	int minDistcost;
	double d;

	public parameter(int p1,double p4){
		this.minDistcost=p1;
		this.d=p4;
	}

}
*/

public class parameter {
	double[] DR = new double[32];
	double scoreCon=0;
	double scoreAvg=0;

	public parameter(){
		for(int i=0; i<32 ; i++){
			this.DR[i]=0;
		}
		this.scoreCon=0;
		this.scoreAvg=0;

	}
	
	public parameter(double[] para){
		for (int i = 0; i < para.length; i++) DR[i] = para[i];
	}

}
