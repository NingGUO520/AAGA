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
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DefaultTeam {

	public class Arbre implements Comparable<Arbre> {
		Arbre pere;
		Point p;
		ArrayList<Arbre> fils ;

		public Arbre(Arbre pere, Point p) {
			this.pere = pere;
			this.p = p;
			this.fils = new ArrayList<Arbre>();
		}

		public boolean estFeuille() {
			return fils.isEmpty();
		}

		public ArrayList<Point> getPoints() {

			ArrayList<Point> noeuds = new ArrayList<Point>();
			noeuds.add(this.p);
			PriorityQueue<Arbre> queue = new PriorityQueue<Arbre> ();
			queue.add(this);
			while(!queue.isEmpty())
			{
				Arbre root = queue.poll();
				ArrayList<Arbre> sousArbres  = root.fils;
				for(Arbre a : sousArbres) {
					if(!noeuds.contains(a.p)) {
						noeuds.add(a.p);
						queue.add(a);
					}


				}
			}
			return noeuds;
		}
		@Override
		public int compareTo(Arbre o) {
			// TODO Auto-generated method stub
			return 0;
		}



	}

	/*
	 * Tous les points de degree 0 sont dominants
	 */
	public static ArrayList<Point> preTraiteDominant0(ArrayList<Point> points, int edgeThreshold){
		ArrayList<Point> dominants = new ArrayList<Point>();

		for(Point p:points) {
			ArrayList<Point> voisin = neighbor(p,points,edgeThreshold);
			if(voisin.size()==0) {
				dominants.add(p);
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
	//	public ArrayList<Point> glouton(ArrayList<Point> points, int edgeThreshold) {
	//
	//		ArrayList<Point> dominant = new ArrayList<Point>();
	//		ArrayList<Point> graphe = (ArrayList<Point>) points.clone();
	//		while(!isValid(points,dominant,edgeThreshold)) {
	//			Point pmax = getDegMax(graphe,edgeThreshold);
	//			dominant.add(pmax);
	//			ArrayList<Point> voisins = neighbor( pmax, graphe, edgeThreshold);
	//			graphe.remove(pmax);
	//			graphe.removeAll(voisins);
	//		}
	//		return dominant;
	//	}
	public ArrayList<Point> multithread(ArrayList<Point> points, int edgeThreshold, int n) throws InterruptedException, ExecutionException{
		ArrayList<Point> dominants = new ArrayList<Point>();
		FutureTask[] tasks = new FutureTask[n];

		for(int i = 0;i<n;i++) {
			tasks[i] = new FutureTask(new Tache(points,edgeThreshold,i));

			Thread t = new Thread(tasks[i]);
			t.start();
		}

		dominants = (ArrayList<Point>) tasks[0].get();
		int min = dominants.size();
		for(int i = 1;i<n;i++) {
			ArrayList<Point> result = (ArrayList<Point>) tasks[i].get();
			if(result.size()<min) {
				min = result.size();
				dominants = result;
			}

		}
		return dominants;

	}

	public static Point getDegMin(ArrayList<Point> points,int edgeThreshold) {
		int min = points.size();
		Point minP = points.get(0);
		for (Point p : points) {
			int size = neighbor(p,points,edgeThreshold).size();
			if (size<min) {
				min = size;
				minP = p;
			}
		}
		return minP;
	}

	public static ArrayList<Point> gloutonX(ArrayList<Point> points, int edgeThreshold){

		ArrayList<Point> graphe = (ArrayList<Point>) points.clone();
		ArrayList<Point> dom = new ArrayList<Point>();
		while(!isValid(points,dom,edgeThreshold)) {
			ArrayList<Point> dom0 = preTraiteDominant0(graphe,edgeThreshold);
			dom.addAll(dom0);
			for(Point p:graphe) {
				ArrayList<Point> voisin = neighbor(p,graphe,edgeThreshold);
				if(voisin.size()==1) {
					dom.add(voisin.get(0));
				}
			}

			for(Point p : dom) {
				graphe.removeAll(neighbor(p,points,edgeThreshold));
			}
			graphe.removeAll(dom);

			if(graphe.size()>0) {
//				Collections.shuffle(graphe);
				Point pDegmin = getDegMin(graphe,edgeThreshold);
				ArrayList<Point> voisins = neighbor(pDegmin,graphe,edgeThreshold);
				if(voisins.size() == 0) {
					dom.add(pDegmin);
					graphe.remove(pDegmin);
				}else {

//					Collections.shuffle(voisins);
					Point aAjouter = getDegMax(graphe,voisins,edgeThreshold);
					dom.add(aAjouter);
					graphe.removeAll(neighbor(aAjouter,graphe,edgeThreshold));
				}
			}


		}

		dom = cleanDominants(points,dom,edgeThreshold);
		return dom;
	}

	public Arbre construireArbre(ArrayList<Point> points, int edgeThreshold) {
		System.out.println("taille dee points " + 	points.size());

		PriorityQueue<Arbre> queue = new PriorityQueue<Arbre> ();
		HashSet<Point> marque = new HashSet<Point>();

		Point pRacine = getDegMax(points,points,edgeThreshold);

		marque.add(pRacine);
		Arbre arbre = new Arbre(null,pRacine);
		queue.add(arbre);
		while(!queue.isEmpty()) {

			Arbre root = queue.poll();
			ArrayList<Point> voisins = neighbor( root.p, points, edgeThreshold);

			for(Point p : voisins) {
				if(!marque.contains(p)) {
					Arbre a = new Arbre(root,p);
					root.fils.add(a);
					marque.add(p);
					queue.add(a);
				}
			}
		}


		System.out.println("taille dee marque " + marque.size());
		return arbre;
	}
	public ArrayList<Point> gloutonArbre(ArrayList<Point> points, int edgeThreshold) {
		Arbre a = construireArbre(points,edgeThreshold);
		ArrayList<Point> dominants = getDominantsBFS(points,a,edgeThreshold);

		System.out.println("taille de arbre " + a.getPoints().size()
				+ "taille de dominant dans ce  arbre " + dominants.size());

		points.removeAll(a.getPoints());
		while(!points.isEmpty()) {
			Arbre a2 = construireArbre(points,edgeThreshold);
			ArrayList<Point> dominants2 = getDominantsBFS(points,a2,edgeThreshold);

			System.out.println("taille de arbre2  " + a2.getPoints().size()
					+ "taille de dominant dans ce  arbre " + dominants2.size());

			dominants.addAll(dominants2);
			points.removeAll(a2.getPoints());

		}


		return dominants;
	}
	public ArrayList<Point> getDominantsBFS(ArrayList<Point> points,Arbre arbre, int edgeThreshold) {
		ArrayList<Point> dominants = new ArrayList<Point>();
		ArrayList<Point> couvert = new ArrayList<Point>();
		// etape 1 : obtenir l'ordre de chaque noeud
		ArrayList<Arbre> ordre = getOrdre(points,arbre,edgeThreshold);

		//etape2
		int i = ordre.size()-1;
		while(i>=0) {
			Arbre a = ordre.get(i);
			if(!couvert.contains(a.p)) {
				if(!dominants.contains(a.p) && !getVoisin(points,dominants,edgeThreshold).contains(a.p) ) {
					if(!dominants.contains(a.pere)) {
						dominants.add(a.pere.p);
					}
				}
				couvert.add(a.p);
				if(a.pere!=null) couvert.add(a.pere.p);
				if(a.pere.pere!=null) couvert.add(a.pere.pere.p);


			}
			i--;
		}
		return dominants;

	}

	public ArrayList<Point> getVoisin(ArrayList<Point> points,ArrayList<Point> dominants, int edgeThreshold){
		HashSet<Point> voisins = new HashSet<Point>();
		voisins.addAll(dominants);
		for(Point p : dominants) {
			voisins.addAll(neighbor( p, points, edgeThreshold));


		}
		ArrayList<Point> result = new ArrayList<Point>();
		result.addAll(voisins);
		return result;
	}

	public ArrayList<Arbre> getOrdre(ArrayList<Point> points,Arbre arbre, int edgeThreshold) {
		ArrayList<Arbre> ordre = new ArrayList<Arbre>();

		HashSet<Arbre> marque = new HashSet<Arbre>();
		PriorityQueue<Arbre> queue = new PriorityQueue<Arbre>();
		queue.add(arbre);
		marque.add(arbre);
		while(!queue.isEmpty()) {
			Arbre root = queue.poll();
			ordre.add(root);
			ArrayList<Arbre> fils = root.fils;

			for(Arbre a : fils) {
				if(!marque.contains(a)) {
					queue.add(a);
					marque.add(a);

				}
			}

		}
		return ordre;

	}
	public static Point getDegMax(ArrayList<Point> points,ArrayList<Point> voisins,int edgeThreshold) {
		int max = 0;
		Point maxP = points.get(0);
		for (Point p : voisins) {
			int size = neighbor(p,points,edgeThreshold).size();
			if (size>max) {
				max = size;
				maxP = p;
			}
		}
		return maxP;
	}

	public static ArrayList<Point> cleanDominants(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {
		ArrayList<Point> res = (ArrayList<Point>) domNaif.clone();

		for (int i=0; i<res.size() ; i++) {
			Point p = res.get(i);
			res.remove(p);
			if (!isValid(points, res, edgeThreshold))
				res.add(p);
		}

		return res;
	}

	public static ArrayList<Point> localSearch2_1(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {

		ArrayList<Point> solutionPrime = new ArrayList<Point>();
		ArrayList<Point> reste = (ArrayList<Point>) points.clone();
		reste.removeAll(domNaif);
//		System.out.println("dom size "+ domNaif.size());
		for (int i=0; i<domNaif.size() ; i++) {
			for (int j=i+1; j<domNaif.size();j++) {
				Point m = domNaif.get(i);
				Point n = domNaif.get(j);
				if(m.distance(n)>3*edgeThreshold) continue;

				for(Point p: reste) {
					if(p.distance(m)>2*edgeThreshold || p.distance(n)>2*edgeThreshold) {
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

	public static ArrayList<Point> localSearchNaif3_2(ArrayList<Point> points, ArrayList<Point> domNaif, int edgeThreshold) {
//		System.out.println("taille de dominants localS3_2 = "+ domNaif.size());
		ArrayList<Point> solutionPrime = new ArrayList<Point>(); 
		ArrayList<Point> reste = (ArrayList<Point>) points.clone();
		reste.removeAll(domNaif);
		for (int i=0; i<domNaif.size() ; i++) {
			Point m = domNaif.get(i);

			for (int j=i+1; j<domNaif.size();j++) {
				Point n = domNaif.get(j);
				if(m.distance(n)>2*edgeThreshold) continue;

				/*double x = (double)(m.x+n.x)/2;
				double y = (double)(m.y+n.y)/2;
				Point midPoint = new Point((int)x,(int)y);*/

				for (int k=j+1; k<domNaif.size();k++) {
					Point q = domNaif.get(k);
					if(q.distance(m)>2*edgeThreshold || q.distance(n)>2*edgeThreshold) continue;
					
				/*	double mx = (double)(m.x+n.x+q.x)/3;
					double my = (double)(m.y+n.y+q.y)/3;
					Point pointMilieu = new Point((int)mx,(int)my);*/
					
					for(Point p1: reste) {
						if(p1.distance(m)>2*edgeThreshold || p1.distance(n)>2*edgeThreshold || p1.distance(q)>2*edgeThreshold) {
							continue;
						}
						for(Point p2: reste) {
							if(p2.equals(p1) || p2.distance(m)>2*edgeThreshold || p2.distance(n)>2*edgeThreshold || p2.distance(q)>2*edgeThreshold) {
								continue;
							}
							solutionPrime = (ArrayList<Point>) domNaif.clone();
							solutionPrime.remove(m);
							solutionPrime.remove(n);
							solutionPrime.remove(q);
							solutionPrime.add(p1);
							solutionPrime.add(p2);
							if(isValid(points,solutionPrime,edgeThreshold)) return solutionPrime;
						}
					}
				}
			}
		}
		return domNaif;
	}
	public static ArrayList<Point> algo(ArrayList<Point> points, int edgeThreshold){
		ArrayList<Point> resultFinal = DefaultTeam.gloutonX(points,edgeThreshold);

		int s = resultFinal.size();
		for(int i = 0; i<20;i++) {
			Collections.shuffle(points);
			ArrayList<Point> result  = DefaultTeam.gloutonX(points,edgeThreshold);
			if(result.size()<s) {
				s = result.size();
				resultFinal = result;
			}


		}

//		System.out.println("size apres glouton "+resultFinal.size());

		int nb1 = resultFinal.size();
		resultFinal= localSearch2_1(points,resultFinal,edgeThreshold);
		int nb2 = resultFinal.size();
		while(nb2<nb1) {
			nb1 = resultFinal.size();
			resultFinal= localSearch2_1(points,resultFinal,edgeThreshold);
			nb2 = resultFinal.size();
		}

		nb1 = resultFinal.size();
		resultFinal= localSearchNaif3_2(points,resultFinal,edgeThreshold);
		nb2 = resultFinal.size();
		while(nb2<nb1) {
			nb1 = resultFinal.size();
			resultFinal= localSearchNaif3_2(points,resultFinal,edgeThreshold);
			nb2 = resultFinal.size();
		}

		System.out.println("taille de result final " + resultFinal.size());
		return resultFinal;
	}
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) throws InterruptedException, ExecutionException {

		return multithread(points,edgeThreshold,10);
		
	}

	public static boolean isValid(ArrayList<Point> points, ArrayList<Point> dominants,
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
	public static ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold){
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
