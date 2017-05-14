package controllers.testPacman;

import java.util.Random;
import java.util.Date;

import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;

import controllers.examples.LegacyTeam;
import controllers.testPacman.parameter;
import utilities.StatSummary;
import competition.GhostsController;
import competition.Main;
import competition.MsPacManController;
import core.maze.*;
import core.*;

public class DE {
	public int kotaisuu=20;
	int sedaisuu=100;
	Date date = new Date();
	parameter[] p = new parameter[kotaisuu];
	Random rnd= new Random();
	parameter[] newP= new parameter[kotaisuu];
	parameter BestP;
	double MaxScore=0;

	public static void main(String[] args){
		DE de = new DE();

		for(int i=0; i<de.kotaisuu ; i++){
			de.p[i]= new parameter();
		}
				
		de.Making();
		for(int i=0;i<de.kotaisuu;i++) de.Evaluation(de.p[i]);
		for(int i=0;i<de.sedaisuu;i++){
			System.out.println("***************************    ¢‘ã”:" + i);
			de.Mutation();
			de.Compare();
		}
		for(int i=0;i<de.kotaisuu;i++){
			if(de.p[i].scoreAvg>de.MaxScore){
				de.MaxScore=de.p[i].scoreAvg;
				de.BestP=de.p[i];
			}
		}

		System.out.println("Score "+de.BestP.scoreAvg);
		System.out.print("{");
		for(int j=0; j<32; j++){
			System.out.print(de.BestP.DR[j]+", "+ de.BestP.DR[j+1]+ ", ");
			j++;
		}
		System.out.print("}");
		System.out.println("End");
	}


	public void Making(){
		for(int i=0; i<kotaisuu ; i++){
			for(int j=0; j<32 ; j+=4){
				double ranInt = rnd.nextInt(50);
				double ranDbl = rnd.nextDouble();
				p[i].DR[j]= ranInt;
				p[i].DR[j+1] = ranDbl ;
				ranInt = rnd.nextInt(50);
				p[i].DR[j+2] = ranInt ;
				ranInt = rnd.nextInt(50);
				p[i].DR[j+3] = ranInt ;
			}
		}
		/*
		double tmp[] ={29, 0.09788635365879828, 11,  0.44733715997188184, 44, 0.44733715997188184, 39, 0.9400730485544085,
				42, 0.2485155015392504, 20,  0.23089813261823078, 3,  0.20620080689653542, 37, 0.24586451305440238};
		p[0].DR=tmp;
		*/
		double tmp[] = {41.3485, 0.8497634084218519, 35.0, 13.0, 24.0, 0.654981644004646, 46.0, 25.0, 37.0, 0.9502374412707658, 41.0, 46.0, 5.0, 0.34022367467207604, 43.0, 22.0, 46.0, 0.5215748849390379, 29.0, 22.0, 48.0, 0.20119836481062758, 15.0, 23.0, 18.0, 0.2530728202494148, 42.0, 39.0, 8.0, 0.4703458929837405, 28.4, 20.0, };
		p[rnd.nextInt(kotaisuu)] = new parameter(tmp);
	}

	public void Evaluation(parameter p){
		int nTimes = 10;
        StatSummary ss = new StatSummary();
        for (int i = 0; i < nTimes; i++){
        	//MsPacManController mspacman = null;//allows you to play the game yourself using the keyboard(arrow keys)
        	//MsPacManController mspacman = new RandomMsPacMan();
        	//MsPacManController mspacman = new RandomNonReverseMsPacMan();
        	//MsPacManController mspacman = new SimplePillEatingMsPacMan();
    	//	MsPacManController mspacman = new SmartPillEater();
        	BetterController mspacman = new BetterController();

//        	GhostsController ghosts = new RandomGhosts();
//        	GhostsController ghosts = new PincerTeam();
        	GhostsController ghosts = new LegacyTeam();
        	//GhostsController ghosts = new LegacyTeam2();
        	//GhostsController ghosts = new MyGhostsTeamBot();

        	mspacman.p=p;
        	int sum = 0;
        	for (int j = 0; j < 8; j++){
        		GameState gs = new GameState(Level.getMaze(j));
        		gs.level = j;
        		gs.maze = Level.getMaze(j);
        		gs.nLivesRemaining = 1;
        		//gs.reset();
                while(gs.getLevel() == j && !gs.terminal()){
                	gs.next(mspacman.getAction(gs), ghosts.getActions(gs));
                }
                sum += gs.getScore();
            }
        	ss.add(sum);

        }
        System.out.println(p.scoreAvg=ss.mean());
	}

	public void Mutation(){
		parameter x1=null,x2=null,x3=null;
		int ranInt1, ranInt2, ranInt3;
		for(int i=0; i<kotaisuu; i++){
			do{
				ranInt1 =rnd.nextInt(kotaisuu);
				x1=p[ranInt1];
				ranInt2 =rnd.nextInt(kotaisuu);
				x2=p[ranInt2];
				ranInt3 =rnd.nextInt(kotaisuu);
				x3=p[ranInt3];
			}while(ranInt1==ranInt2 || ranInt2==ranInt3 || ranInt3==ranInt1);
		newP[i]=Crossover(p[i],x1,x2,x3);
		}
	}

	public parameter Crossover(parameter p,parameter x1,parameter x2,parameter x3){
		double F=0.7;
		double ranDbl = rnd.nextDouble();
		double CR=0.95;
		int k=0;
		int j=rnd.nextInt(32);
		parameter newP = new parameter(p.DR);
		do{
			newP.DR[j]=x1.DR[j]+F*(x2.DR[j]-x3.DR[j]);
			if(newP.DR[j]<0) newP.DR[j]=(-1)*newP.DR[j];
			if(newP.DR[j]>1 && j%2 == 1)newP.DR[j]=newP.DR[j]-1;
			j=(j+1)/32;
			k++;

				/*
				p.Dist[j]=(int) (x1.Dist[j]+F*(x2.Dist[j]-x3.Dist[j]));
				if(p.Dist[j]<0) p.Dist[j]=(-1)*p.Dist[j];
				p.ratio[j]=x1.ratio[j]+F*(x2.ratio[j]-x3.ratio[j]);
				if(p.ratio[j]<0)p.ratio[j]=(-1)*p.ratio[j];
				if(p.ratio[j]>1)p.ratio[j]=p.ratio[j]-1;
				j=(j+1)/32;
				k++;
				*/
		}while(k<32 && ranDbl<CR);


		return newP;
	}

	public void Compare(){
		double Better=0;
		potential(p,newP);
		for(int i=0; i<kotaisuu; i++){
		Better = (-newP[i].scoreCon+p[i].scoreAvg)/p[i].scoreAvg;
			if (Better<=0.01 || newP[i].scoreCon > p[i].scoreAvg){
			Evaluation(newP[i]);
				if(newP[i].scoreAvg>p[i].scoreAvg) {
					p[i]=newP[i];
					/*
					System.out.println("score "+p[i].scoreAvg);
					for(int j=0; j<32; j++){
						System.out.println("Level"+(j+1)+"  Dist "+p[i].Dist+"  ratio  "+ p[i].ratio);
					}
					*/
				}
			}
		}
	}

	public void potential(parameter p[], parameter newP[]){
		double Uo = 0;
		double Uc = 0;
		double Evlcon=0;
		parameter pmin = new parameter();
		parameter pmax = new parameter();
		double tmp[] ={100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100};
		pmin.DR=tmp;

		for (int i=0;i<kotaisuu;i++){
			for(int j=0;j<32;j++){
				if(p[i].DR[j]<pmin.DR[j]) pmin.DR[j]=p[i].DR[j];
				if(p[i].DR[j]>pmax.DR[j]) pmax.DR[j]=p[i].DR[j];
				if(newP[i].DR[j]<pmin.DR[j]) pmin.DR[j]=newP[i].DR[j];
				if(newP[i].DR[j]>pmax.DR[j]) pmax.DR[j]=newP[i].DR[j];
			}
		}

		for (int i=0;i<kotaisuu;i++){
			for(int j=0;j<kotaisuu;j++){
				if(i==j)continue;
				Uo+=p[j].scoreAvg/Distance(p[j],newP[i],pmin,pmax);
				Uc+=1/Distance(p[j],newP[i],pmin,pmax);
			}
		newP[i].scoreCon=Uo/Uc;
		}


	}

	public double Distance(parameter p1, parameter p2, parameter pmin, parameter pmax){
		double dist=0;
		/*
		double min=1000; double max=0;
		for(int j=0;j<32;j++){
			if(p1.DR[j]<min) min=p1.DR[j];
			if(p1.DR[j]>max) max=p1.DR[j];
			if(p2.DR[j]<min) min=p2.DR[j];
			if(p2.DR[j]>max) max=p2.DR[j];
		}
		*/
		for(int i=0;i<32;i++){
			dist+=(p1.DR[i]-p2.DR[i])*(p1.DR[i]-p2.DR[i])/((pmax.DR[i]-pmin.DR[i])*(pmax.DR[i]-pmin.DR[i]));
		}
		return dist;
	}
}







