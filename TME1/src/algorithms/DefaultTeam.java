package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class DefaultTeam {

//	public ArrayList<Point> calculFVS_glouton(ArrayList<Point> points, int edgeThreshold) {
//
//		Evaluation eval = new Evaluation();
//		ArrayList<Point> fvs = new ArrayList<Point>();
//
//		ArrayList<Point> lesPoints = (ArrayList<Point>) points.clone();
//
//		while(!eval.isValid(points,fvs,edgeThreshold )) {
//			int max = 0;
//
//			Point maxP =lesPoints.get(0) ;
//
//			for(Point p :lesPoints) {
//
//
//
//				int size = neighbor(p,lesPoints,edgeThreshold).size() ;
//				if(size > max) {
//
//
//					maxP = p;
//					max = size;
//				}
//
//			}
//			fvs.add(maxP);
//			lesPoints.remove(maxP);
//
//		}
//
//
//		return fvs;
//	}


	
	public ArrayList<Point> calculFVS_glouton(ArrayList<Point> points, int edgeThreshold) {
		
		Evaluation eval = new Evaluation();
		ArrayList<Point> fvs;

		ArrayList<Point> lesPoints = (ArrayList<Point>) points.clone();
		ArrayList<Point> graphe = new ArrayList<>();

		//Si un sommet est de degree 1, on est sure qu'il est pas dans un cycle
		for(Point p: points) {
			if(neighbor(p,points,edgeThreshold).size()<2) {
				graphe.add(p);
				lesPoints.remove(p);
			}
			
		}
		while (!lesPoints.isEmpty()){
			Point p = getDegMin(lesPoints,edgeThreshold);

			ArrayList<Point> fvs_tmp = (ArrayList<Point>) points.clone();
			graphe.add(p);
			lesPoints.remove(p);
			fvs_tmp.removeAll(graphe);
			if (!eval.isValid(points, fvs_tmp, edgeThreshold)) {
				graphe.remove(p);
			}

		}
		
		fvs = (ArrayList<Point>) points.clone();
		fvs.removeAll(graphe);
		return fvs;
	}
	
	public Point getDegMin(ArrayList<Point> points,int edgeThreshold) {
		int min = points.size();
		Point minP = null;
		for (Point p : points) {
			int size = neighbor(p,points,edgeThreshold).size();
			if (size<=min) {
				min = size;
				minP = p;
			}
		}
		
		return minP;
	}
	
	
	/**
	 * @param points
	 * @param edgeThreshold
	 * @return
	 */
	public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {


		ArrayList<Point> glouton = calculFVS_glouton(points,edgeThreshold);
	//	ArrayList<Point>  resultat = glouton;
	//	int nombre = glouton.size();

		
		
		ArrayList<Point> reste = (ArrayList<Point>) points.clone();

		glouton = removeDuplicates(glouton);
		reste = removeDuplicates(reste);
		reste.removeAll(glouton);



		int nombre = glouton.size();
		ArrayList<Point>  resultat = improve1(points,glouton,  reste,edgeThreshold) ;

		while(nombre > resultat.size()) {


			nombre = resultat.size();

			ArrayList<Point> reste2 = (ArrayList<Point>) points.clone();
			reste2.removeAll(resultat);

			resultat = improve1(points,resultat,  reste2,edgeThreshold) ;



		}
		
		
		//improve 2 
	/*	ArrayList<Point> leReste = (ArrayList<Point>) points.clone();

		leReste.removeAll(resultat);

		  resultat = improve2(points,resultat,  leReste,edgeThreshold) ;

		while(nombre > resultat.size()) {


			nombre = resultat.size();

			ArrayList<Point> reste2 = (ArrayList<Point>) points.clone();
			reste2.removeAll(resultat);

			resultat = improve2(points,resultat,  reste2,edgeThreshold) ;



		}
		*/
		//System.out.print(nombre);
		



		return resultat;
	}

	public ArrayList<Point> improve2(ArrayList<Point> origin,
			ArrayList<Point> solution, ArrayList<Point> reste,
			int edgeThreshold) {


		Evaluation eval = new Evaluation();
		ArrayList<Point> solutionPrime = new ArrayList<Point>(); 

		for(int i = 0 ; i< solution.size();i++) {
			for(int j= i+1 ; j<solution.size();j++ ) {
				for(int k= j+1 ; k<solution.size();k++ ) {

					Point p = solution.get(i);
					Point q = solution.get(j);
					Point m = solution.get(k);
					int x = (int) ((p.getX()+q.getX())/2);
					int y = (int) ((p.getY()+q.getY())/2);
					Point mid = new Point(x,y);


					if(p.distance(q)>2*edgeThreshold) { continue;}
					if(m.distance(mid)>2*edgeThreshold) { continue;}




					for(int a = 0 ; a<reste.size();a++) {

						for(int b = a+1; b<reste.size();b++) {
							
							
							
						
							Point r = reste.get(a);
							Point r2 = reste.get(b);


							solutionPrime = (ArrayList<Point>) solution.clone();
							solutionPrime.remove(p);
							solutionPrime.remove(q);
							solutionPrime.remove(m);
							solutionPrime.add(r);
							solutionPrime.add(r2);
							if(eval.isValid(origin, solutionPrime, edgeThreshold)) {
								return solutionPrime;
							}
						}
					}
				} 
			}
		}
		return solution;

	}

	public ArrayList<Point> improve1(ArrayList<Point> origin,
			ArrayList<Point> solution, ArrayList<Point> reste,
			int edgeThreshold) {


		Evaluation eval = new Evaluation();
		ArrayList<Point> solutionPrime = new ArrayList<Point>(); 

		for(int i = 0 ; i< solution.size();i++) {
			for(int j= i+1 ; j<solution.size();j++ ) {


				Point p = solution.get(i);
				Point q = solution.get(j);

				if(p.distance(q)>3*edgeThreshold) { continue;}



				for(Point r: reste) {
					if(r.equals(p)  || r.equals(q)) {
						continue;
					}
					solutionPrime = (ArrayList<Point>) solution.clone();
					solutionPrime.remove(p);
					solutionPrime.remove(q);
					solutionPrime.add(r);
					if(eval.isValid(origin, solutionPrime, edgeThreshold)) {
						return solutionPrime;
					}

				}
			} 
		}
		return solution;

	}
	public ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold){
		ArrayList<Point> result = new ArrayList<Point>();

		for (Point point:vertices)
			if (point.distance(p)<edgeThreshold && !point.equals(p))
				result.add((Point)point.clone());

		return result;
	}
	
	public ArrayList<Point> removeDuplicates(ArrayList<Point> points) {
		ArrayList<Point> result = (ArrayList<Point>)points.clone();
		for (int i=0;i<result.size();i++) {
			for (int j=i+1;j<result.size();j++) if (result.get(i).equals(result.get(j))) {
				result.remove(j);
				j--;
			}
		}
		return result;
	}
}
