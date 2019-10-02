package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultTeam {

	public ArrayList<Point> expand(ArrayList<Point> origins,ArrayList<Point> subset, int edgeThreshold){
		ArrayList<Point> setPlusUn = new ArrayList<Point>();
		HashSet<Point> set = new HashSet<Point>();
		set.addAll(subset);

		for(Point p: subset) {
			set.addAll(neighbor(p,origins,edgeThreshold));
		}
		setPlusUn.addAll(set);
		return setPlusUn;
	}

	public ArrayList<Point> algo(ArrayList<Point> points, int edgeThreshold, double a){
		HashSet<Point> result = new HashSet<Point>();
		ArrayList<Point> graphe = (ArrayList<Point>) points.clone();
		// get an arbitrary vertext v
		int arbitrary ;
		Point pointDepart ;
		ArrayList<Point> subset = new ArrayList<Point>();
		ArrayList<Point> dominant = new ArrayList<Point>();
		int taille1 ;
		int taille2 ;

		while(!graphe.isEmpty()) {

			// get an arbitrary vertex v
			if(graphe.size()>2) {
			arbitrary = graphe.size()/2;
			pointDepart = graphe.get(arbitrary);
			}else {
				pointDepart = graphe.get(0);
			}
			//Initialiser le subset
			subset = new ArrayList<Point>();
			subset.add(pointDepart);
			dominant.add(pointDepart);
			taille1 = dominant.size();

			subset = expand(graphe,subset,edgeThreshold);
			dominant = getDominant(subset,edgeThreshold);

			subset = expand(graphe,subset,edgeThreshold);
			dominant = getDominant(subset,edgeThreshold);
			taille2 = dominant.size();
			
			
			while(taille2>taille1*a) {
				taille1 = dominant.size();
				subset = expand(graphe,subset,edgeThreshold);
				dominant = getDominant(subset,edgeThreshold);

				subset = expand(graphe,subset,edgeThreshold);
				dominant = getDominant(subset,edgeThreshold);
				taille2 = dominant.size();
			}

			//union des dominant
			System.out.println("taille de dominant"+ dominant.size());
			result.addAll(dominant);
			graphe.removeAll(subset);
			

			
		}
		
		ArrayList<Point> dominants = new ArrayList<Point>();
		dominants.addAll(result);
		return dominants;

	}
	public ArrayList<Point> getDominant(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> result = glouton(points, edgeThreshold);
		result= localSearch1(points,result,edgeThreshold);

		ArrayList<Point> tmp = (ArrayList<Point>) result.clone();
		result= localSearch2(points,tmp,edgeThreshold);
		while(result.size()<tmp.size()) {
			tmp = (ArrayList<Point>) result.clone();
			result= localSearch2(points,tmp,edgeThreshold);
		}
		return result;
	}

	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> result = algo(points,edgeThreshold,1.3);
		return result;
	}
	public ArrayList<Point> localSearch1(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {
		ArrayList<Point> res = (ArrayList<Point>) domNaif.clone();

		for (int i=0; i<res.size() ; i++) {
			Point p = res.get(i);
			res.remove(p);
			if (!isValid(points, res, edgeThreshold))
				res.add(p);
		}

		return res;
	}
	public ArrayList<Point> localSearch2(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {
		ArrayList<Point> solutionPrime = new ArrayList<Point>(); 

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
					if(isValid(points,solutionPrime,edgeThreshold)) {
						return solutionPrime;

					}
				}


			}

		}

		return domNaif;
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
