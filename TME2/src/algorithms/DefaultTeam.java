package algorithms;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DefaultTeam {


	public ArrayList<Point> preTraiteDominant(ArrayList<Point> points, int edgeThreshold){
		ArrayList<Point> dominants = new ArrayList<Point>();

		for(Point p:points) {
			ArrayList<Point> voisin = neighbor(p,points,edgeThreshold);
			if(voisin.size()==0) {
				dominants.add(p);
			}else if(voisin.size() == 1) {
				dominants.add(voisin.get(0));
			}
		}
		return dominants;
	}
	public ArrayList<Point> gloutonAlea(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> dominant = new ArrayList<Point>();
		ArrayList<Point> graphe = (ArrayList<Point>) points.clone();
		while(!isValid(points,dominant,edgeThreshold)) {
			Collections.shuffle(graphe);
			Point p = graphe.get(0);
			dominant.add(p);
			ArrayList<Point> voisins = neighbor( p, graphe, edgeThreshold);
			graphe.remove(p);
			graphe.removeAll(voisins);
		}
		return dominant;
	}
	public ArrayList<Point> glouton(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> dominant = new ArrayList<Point>();
		ArrayList<Point> graphe = (ArrayList<Point>) points.clone();
		while(!isValid(points,dominant,edgeThreshold)) {
			Point pmax = getDegMax(graphe,edgeThreshold);
			dominant.add(pmax);
			ArrayList<Point> voisins = neighbor( pmax, graphe, edgeThreshold);
			graphe.remove(pmax);
			graphe.removeAll(voisins);
		}
		return dominant;
	}

	public Point getDegMax(ArrayList<Point> points,int edgeThreshold) {
		int max = 0;
		Point maxP = points.get(0);
		for (Point p : points) {
			int size = neighbor(p,points,edgeThreshold).size();
			if (size>max) {
				max = size;
				maxP = p;
			}
		}
		return maxP;
	}

	public ArrayList<Point> cleanDominants(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {
		ArrayList<Point> res = (ArrayList<Point>) domNaif.clone();

		for (int i=0; i<res.size() ; i++) {
			Point p = res.get(i);
			res.remove(p);
			if (!isValid(points, res, edgeThreshold))
				res.add(p);
		}

		return res;
	}

	public ArrayList<Point> localSearch2_1(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {
		ArrayList<Point> solutionPrime = new ArrayList<Point>(); 
		System.out.println("dom size "+ domNaif.size());
		for (int i=0; i<domNaif.size() ; i++) {
			for (int j=i+1; j<domNaif.size();j++) {
				Point m = domNaif.get(i);
				Point n = domNaif.get(j);
				if(m.distance(n)>3*edgeThreshold) continue;
				double x = (double)(m.x+n.x)/2;
				double y = (double)(m.y+n.y)/2;
				Point point = new Point((int)x,(int)y);
				for(Point p: points) {
					if(p.equals(m) || p.equals(n) || p.distance(point)>5*edgeThreshold) {
						continue;
					}
					solutionPrime = (ArrayList<Point>) domNaif.clone();
					solutionPrime.remove(m);
					solutionPrime.remove(n);
					solutionPrime.add(p);
					if(isValid(points,solutionPrime,edgeThreshold)) return solutionPrime;

				}
			}
		}
		return domNaif;
	}

	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {

		// pretraitement: enlever tous les points de degree 0 et les voisins de degree 1
		ArrayList<Point> dominants0 =	 preTraiteDominant( points,  edgeThreshold);
		ArrayList<Point> resultFinal = new ArrayList<Point>();
		resultFinal.addAll(dominants0);

		for(Point p:dominants0) {
			points.removeAll(neighbor(p,points,edgeThreshold));
		}
		points.removeAll(dominants0);

		ArrayList<Point>  resultMinimum = gloutonAlea(points, edgeThreshold);
		int size = resultMinimum.size();
		for(int i = 0; i<100;i++) {
			ArrayList<Point> result = gloutonAlea(points, edgeThreshold);
			result= cleanDominants(points,result,edgeThreshold);

			System.out.println("nombre de points " + result.size());
			if(result.size()<size) {
				size = result.size();
				resultMinimum = result;
			}
		}
		resultFinal.addAll(resultMinimum);

		//		int nb1 = result.size();
		//		result= localSearch2_1(points,result,edgeThreshold);
		//		int nb2 = result.size();
		//		while(nb2<nb1) {
		//			nb1 = result.siresultFinalze();
		//			result= localSearch2_1(points,result,edgeThreshold);
		//			nb2 = result.size();
		//		}
		return resultFinal;
	}

	public boolean isValid(ArrayList<Point> points, ArrayList<Point> dominants,
			int edgeThreshold) {


		ArrayList<Point> vertices = (ArrayList<Point>) points.clone();
		Set<Point> voisins = new HashSet<Point>();
		voisins.addAll(dominants);
		for(Point p : dominants) {

			voisins.addAll(neighbor(p,points,edgeThreshold));
		}
		vertices.removeAll(voisins);
		return vertices.isEmpty();
	}
	public ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold){
		ArrayList<Point> result = new ArrayList<Point>();

		for (Point point:vertices) {
			if (point.distance(p)<edgeThreshold && !point.equals(p))
				result.add((Point)point.clone());
		}
		return result;
	}

	//FILE PRINTER
	private void saveToFile(String filename,ArrayList<Point> result){
		int index=0;
		try {
			while(true){
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			printToFile(filename+Integer.toString(index)+".points",result);
		}
	}
	private void printToFile(String filename,ArrayList<Point> points){
		try {
			PrintStream output = new PrintStream(new FileOutputStream(filename));
			int x,y;
			for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("I/O exception: unable to create "+filename);
		}
	}

	//FILE LOADER
	private ArrayList<Point> readFromFile(String filename) {
		String line;
		String[] coordinates;
		ArrayList<Point> points=new ArrayList<Point>();
		try {
			BufferedReader input = new BufferedReader(
					new InputStreamReader(new FileInputStream(filename))
					);
			try {
				while ((line=input.readLine())!=null) {
					coordinates=line.split("\\s+");
					points.add(new Point(Integer.parseInt(coordinates[0]),
							Integer.parseInt(coordinates[1])));
				}
			} catch (IOException e) {
				System.err.println("Exception: interrupted I/O.");
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close "+filename);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found.");
		}
		return points;
	}
}
