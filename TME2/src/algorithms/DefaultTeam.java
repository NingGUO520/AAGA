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
	
	
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		
		//System.out.println(nbBout(points,edgeThreshold));
		ArrayList<Point> result = glouton(points, edgeThreshold);
		//result= localSearch1(points,result,edgeThreshold);
		result= localSearch2(points,result,edgeThreshold);
		
		//if (false) result = readFromFile("output0.points");
		//else saveToFile("output",result);
		
		return result;
	}
	
	
	// retourne le nombre de sommet qui n'a qu'un seul voisin = le bout du graphe
	private int nbBout(ArrayList<Point> a, int edgeThreshold) {
		int cpt = 0;
		for (Point p : a) {
			ArrayList<Point> voisins = neighbor(p, a, edgeThreshold);
			if(voisins.size() == 1) cpt++;
		}
		
		return cpt;
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

	/**
	 * 
	 * @param points
	 * @param edgeThreshold
	 * @return Ens Dominant qui est construit à partir de noeud de degres fort = 91
	 */
	public ArrayList<Point> glouton(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> dominant = new ArrayList<Point>();
		ArrayList<Point> graphe = (ArrayList<Point>) points.clone();
		ArrayList<Point> points_enAttente = new ArrayList<Point>();

		while(!isValid(points,dominant,edgeThreshold)) {
			Point pmax = getDegMax(graphe,edgeThreshold);
			ArrayList<Point> voisins_pmax = neighbor(pmax, graphe, edgeThreshold); //graphe ou points ?
			if (voisins_pmax.contains(pmax))
			dominant.add(pmax);
			ArrayList<Point> voisins = neighbor( pmax, graphe, edgeThreshold);
			graphe.remove(pmax);
			graphe.removeAll(voisins);
			System.out.println("Dans le glouton, dominant de taille "+dominant.size());
		}
		return dominant;
	}
	
	//retourne si il y a un voisin qui appartient au dom
	public boolean hasVoisinDom() {
		return true;
	}

	/**
	 * 
	 * @param points
	 * @param edgeThreshold
	 * @return Ens dominant construit à partir de pair de noeuds de degres fort = 92
	 */
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

			System.out.println("Dans le glouton2, dominant de taille "+dominant.size());
		}
		return dominant;
	}

	/**
	 * 
	 * @param points
	 * @param edgeThreshold
	 * @return Ens dominant construit en parcourant tous les points, s'il a un voisin qui n'est pas dans ED, alors ajout de ce point
	 * 
	 */
	
	public ArrayList<Point> glouton3(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> dominants = new ArrayList<Point>();
		ArrayList<Point> tmp_dom = new ArrayList<Point>();
		
		for (int i = 0; i < 150; i++) {
			Collections.shuffle(points);
			tmp_dom.clear();
			for (Point p : points) {
				ArrayList<Point> voisins = neighbor(p, points, edgeThreshold);
				// s'il existe un voisin de p qui n'est pas dans le DS, alors p a besoin d'être dedans
				for (Point v : voisins) {
					if (!tmp_dom.contains(v))
						tmp_dom.add(p);
					break;
				}
			} 
			System.out.println(i + " -> avant ls1 " + tmp_dom.size());
			tmp_dom = localSearch1(points, tmp_dom, edgeThreshold);
			if (i==0 || tmp_dom.size() < dominants.size())
				 dominants = (ArrayList<Point>) tmp_dom.clone();
			System.out.println(i + " -> apres ls1 " +tmp_dom.size());
		}
		return dominants;
		
	}
	
	public ArrayList<Point> glouton4(ArrayList<Point> points, int edgeThreshold) {

		ArrayList<Point> dominants = new ArrayList<Point>();
		ArrayList<Point> reste = (ArrayList<Point>) points.clone();
		
		//faire un premier parcours ou je prends que le strict necessaire : 
		// ceux qui n'ont aucun voisin dans le dominating set 
		//Ensuite je repasse pour ajouter les p de points qui restent jusqu'à obtenir un DS, en commencant par deg + fort
		
		Collections.shuffle(points);
		
		for (Point p : points) {
			ArrayList<Point> voisins = neighbor(p, points, edgeThreshold);
			int voisins_inDS = 0;
			for (Point v : voisins) {
				if (dominants.contains(v)) voisins_inDS++;
			}
			if (voisins_inDS == 0) dominants.add(p);
		}
		
		reste.removeAll(dominants);
		
		for (Point p : reste) {
			dominants.add(p);
			if (isValid(points, dominants, edgeThreshold))
				break;
		}
		
		return dominants;
		
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
		int nombre = domNaif.size();
		ArrayList<Point>  resultat = improve2for1(points,domNaif,edgeThreshold) ;
		
		while(nombre > resultat.size()) {
			System.out.println("Taille de l'ensemble dominant : " + nombre);
			nombre = resultat.size();
			resultat = improve2for1(points,resultat,edgeThreshold);
		}
		
		
		/*resultat = (ArrayList<Point>) domNaif.clone();
		int score;
		do {
			score = resultat.size();
			resultat = improve2for1(points,resultat,edgeThreshold);
			
		}while(score > resultat.size());*/
		
		return resultat;


	}
	
	
	public ArrayList<Point> improve2for1(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {
		ArrayList<Point> res = (ArrayList<Point>) domNaif.clone();
		ArrayList<Point> reste = (ArrayList<Point>) points.clone();
		reste.removeAll(domNaif);

		System.out.println("Dans le improve2en1");
		
		for (int i=0; i<res.size() ; i++) {
			System.out.println("indice i : " +i);
			for (int j=i+1; j<res.size();j++) {
				Point m = res.get(i);
				Point n = res.get(j);

				if(m.distance(n)>3*edgeThreshold) continue;

				for(Point p: reste) {
					
					res = (ArrayList<Point>) domNaif.clone();
					res.remove(m);
					res.remove(n);
					res.add(p);
					if(isValid(points,res,edgeThreshold)) {
						return res;
					}
				}
			}
		}

		return res;
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
					|| point.distance(p2)<edgeThreshold && !point.equals(p2)) // est ce qu'il faut voir le cas ou p1 et p2 sont voisins?
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
