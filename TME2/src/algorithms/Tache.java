package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

public class Tache implements Callable<ArrayList<Point>> {

	private ArrayList<Point> points;
	int edgeThreshold;
	int numeroTask;
	
	public Tache(ArrayList<Point> points,int edgeThreshold, int i) {
		this.points = (ArrayList<Point>)points.clone();
		this.edgeThreshold = edgeThreshold;
		this.numeroTask = i;
	
	}
	@Override
	public ArrayList<Point> call() throws Exception {


//		System.out.println("task " + numeroTask + " demarre");
		ArrayList<Point> resultMinimum  = DefaultTeam.algo(points,edgeThreshold);

//		int s = resultMinimum.size();
//		for(int i = 0; i<20;i++) {
//			Collections.shuffle(points);
//			ArrayList<Point> result  = DefaultTeam.algo(points,edgeThreshold);
//			result = DefaultTeam.cleanDominants(points,result,edgeThreshold);
//			if(result.size()<s) {
//				s = result.size();
//				resultMinimum = result;
//			}
//
//
//		}

		System.out.println("Task "+ numeroTask+" a un resultMinimum "+resultMinimum.size() );
		return resultMinimum;
	}

}
