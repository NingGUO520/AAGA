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
import java.util.HashSet;
import java.util.Set;

public class DefaultTeam {
	public class PairPoint{
		Point p1;
		Point p2;
		public PairPoint(Point p1, Point p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		public Point fst() {return p1;}
		public Point snd() {return p2;}

	}
	
	public ArrayList<Point> getSeparators(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> separateurs = new ArrayList<Point>();
		
		double maxX = 0;
		double minX = points.get(0).x;
		for(Point p:points) {
			if(p.getX()>maxX) {
				maxX =  p.getX();
			}
			
			
			if(p.getX()<minX) {
				minX = p.getX();
			}
		}
		double X =(double) (minX+maxX)/2;
		
		for(Point p:points) {
			if(p.x<=X+(double)edgeThreshold/2 && p.x>=X-(double)edgeThreshold/2) {
				separateurs.add(p);
			}
		}
		
		System.out.println("taille de separators :" + separateurs.size());
		return separateurs;
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

	public ArrayList<Point> glouton2(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> dominant = new ArrayList<Point>();
		ArrayList<Point> graphe = (ArrayList<Point>) points.clone();

		while(!isValid(points,dominant,edgeThreshold)) {
			PairPoint pmax = getDegMax2(graphe,edgeThreshold);

			dominant.add(pmax.p1);
			dominant.add(pmax.p2);

			ArrayList<Point> voisins = neighbor2( pmax.p1,pmax.p2, graphe, edgeThreshold);
			graphe.remove(pmax.p1);
			graphe.remove(pmax.p2);

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
	public PairPoint getDegMax2(ArrayList<Point> points,int edgeThreshold) {
		int max = 0;
		PairPoint maxP = new PairPoint(points.get(0),points.get(1));


		for ( int i = 0; i< points.size();i++) {
			for(int j = i+1; j<points.size();j++) {

				Point p1 = points.get(i);
				Point p2 = points.get(j);
				int size = neighbor2(p1,p2,points,edgeThreshold).size();
				if (size>max) {
					max = size;

					maxP = new PairPoint(p1,p2);
				}
			}
		}

		return maxP;
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
		ArrayList<Point> res = (ArrayList<Point>) domNaif.clone();

		for (int i=0; i<res.size() ; i++) {
			for (int j=i+1; j<res.size();j++) {
				Point m = res.get(i);
				Point n = res.get(j);

				if(m.distance(n)>3*edgeThreshold) continue;

				for(Point p: points) {
					if(p.equals(m) || p.equals(n)) {
						continue;
					}

					res.remove(m);
					res.remove(n);
					res.add(p);
					if(!isValid(points,res,edgeThreshold)) {
						res.add(m);
						res.add(n);
						res.remove(p);

					}
				}


			}

		}

		return res;
	}


	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> result = glouton(points, edgeThreshold);
		result= localSearch1(points,result,edgeThreshold);
		result= localSearch2(points,result,edgeThreshold);

		if (false) result = readFromFile("output0.points");
		else saveToFile("output",result);

		return result;
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

	public ArrayList<Point> neighbor2(Point p1,Point p2, ArrayList<Point> vertices, int edgeThreshold){
		ArrayList<Point> result = new ArrayList<Point>();

		for (Point point:vertices) {
			if (point.distance(p1)<edgeThreshold && !point.equals(p1)
					|| point.distance(p2)<edgeThreshold && !point.equals(p2))
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
