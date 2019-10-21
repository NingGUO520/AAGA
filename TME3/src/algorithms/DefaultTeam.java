package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import algorithms.DefaultTeam.Arete;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultTeam {
	public class Arete  {
		Point p,q;
		
		public Arete(Point p, Point q) {
						
			this.p =p;
			this.q = q;
		}		
	}

	public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		  int[][] paths=new int[points.size()][points.size()];
		    for (int i=0;i<paths.length;i++) for (int j=0;j<paths.length;j++) paths[i][j]=j;
//		    matrice d'adjacence
		    double[][] m = new double[points.size()][points.size()];
		    for(int i =0;i<points.size();i++) {
		    	for (int j=0;j<points.size();j++) {
		    		double d = points.get(i).distance(points.get(j));
		    		if(d<edgeThreshold) {
		    			m[i][j] = d;
		    		}else {
		    			  			
		    			m[i][j]= Double.MAX_VALUE;
		    			
		    		}
		    	}
		    }
		    for(int k = 0 ; k< points.size();k++) {
		    	for(int i = 0 ; i< points.size();i++) {
		    		for(int j = 0 ; j< points.size();j++) {
		    			if(m[i][j]> m[i][k]+m[k][j]) {
		    				m[i][j]= m[i][k]+m[k][j];
		    				paths[i][j]=paths[i][k];
		    			}    	    	  	    	    	    	
		    	    }    	           	        	
		        }
		       
		    	
		    }
		   
//		  System.out.println(" fin de  matrice d'adjacence");
		  //enregistre points avec une clé est son index 
		  Map<Point,Integer> map = new HashMap<Point,Integer>();
		  for(int i = 0; i< points.size();i++) {		  		  
			  
			  map.put( points.get(i),i);
		  }
//	    Algorithme Kruskal
		    
	//  etape 1 :  Construire une liste de toutes les arêtes possibles de l’arbre
	    
		    ArrayList<Arete> aretes = new ArrayList<Arete>();
		    for(Point p :hitPoints) {
		    	for(Point q : hitPoints) {
		    		if(!q.equals(p)) {
		    		
			    		Arete a = new Arete(p,q);
			    	
			    		aretes.add(a);
			    		
		    		}
		    	}
		    	    	
		    }
		    
	//  etape 2:  Trier la liste des arêtes  par ordre croissant selon leur poids 
		    Collections.sort(aretes, new Comparator<Arete>() {

				@Override
				public int compare(Arete o1, Arete o2) {

					double d1 = m[map.get(o1.p)][map.get(o1.q)];
					double d2 = m[map.get(o2.p)][map.get(o2.q)];
					if(d1>d2) return 1;
					if(d1<d2) return -1;
					return 0;
				}
		    	
		    });
		    
		    
			  System.out.println(" fin de  trie");

		    
		//  etape 3:  Initialiser une liste “solution” à vide.
		    
		    ArrayList<Arete> solution = new ArrayList<Arete>();
		    
//		    système d’étiquettes

		    Map<Point,Integer> mapEtiquette = new HashMap<Point,Integer>();
		    for(int i = 0;i<points.size();i++) {
		    	
		    	mapEtiquette.put( points.get(i),i);
		    	
		    	
		    }
		    
			  System.out.println(" fin de  mapEtiquette");

//		    Parcourir la liste des arêtes candidates par ordre croissant 
		    
		    while(!hasAllPoints(solution,hitPoints)) {
			    
			    for(Arete a : aretes) {
			    	int x =mapEtiquette.get(a.p);
					int y = mapEtiquette.get(a.q);
			    	if(x != y) {
			    		
			    		//re-etiquetter tous les points ayant etiquette x en y 		
			    		reetiquette(mapEtiquette,x,y);
			    		solution.add(a);
			  		
			    	}
			    	
			    	
			    }
			    
		    }
//		    System.out.println("size of solution : "+solution.size());
//		    System.out.println("nombre de points total : "+hitPoints.size());
//		    
//			

//		    Traduire la liste “solution” en une structure d’arbre
		    
		    Point racine = solution.get(0).p;
		    Tree2D result = listeToArbre(solution,racine);
		  // fin de  Kruskal

//		    Remplacer toute arete uv par un plus court chemin entre u et v dans G
		    
		    ArrayList<Point> listePoint = hitPoints;
		    ArrayList<Arete> nouveauAretes = new ArrayList<Arete>();
		    for(Arete a : solution) {
		    	
		    	int u = map.get(a.p);
		    	int v = map.get(a.q);

		    	//ajouter tous les aretes entre u et v 
		    	ArrayList<Arete> res = new ArrayList<Arete>();
		    	
		    	while(!(paths[u][v]==v)) {
		    		
		    		int k = paths[u][v];
		    		
		    		Arete arete = new Arete(points.get(u),points.get(k));
		    		
		    		
		    		if(!listePoint.contains(points.get(k))) {
		    			
		    			listePoint.add(points.get(k));
		    			
		    		}
		    		res.add(arete);
		    		
		    		u = k;
		    	}
		    	Arete arete = new Arete(points.get(u),points.get(v));
	    		
	    		res.add(arete);
		    	nouveauAretes.addAll(res);
		    }

		    
		 
//		 On reapplique   Algorithme Kruskal une fois 
		    
		//  etape 1 :  Construire une liste de toutes les arêtes possibles de l’arbre
		    
			    aretes = new ArrayList<Arete>();
			    for(Point p :listePoint) {
			    	
			    	for(Point q : listePoint) {
			    		
			    		if(!q.equals(p)) {
			    		
				    		Arete a = new Arete(p,q);
				    	
				    		aretes.add(a);
				    		
			    		}
			    	}
			    	    	
			    }
			    
			    
		//  etape 2:  Trier la liste des arêtes  par ordre croissant selon leur poids 
			    Collections.sort(aretes, new Comparator<Arete>() {

					@Override
					public int compare(Arete o1, Arete o2) {

						
						double d1 = m[map.get(o1.p)][map.get(o1.q)];
						double d2 = m[map.get(o2.p)][map.get(o2.q)];
						if(d1>d2) return 1;
						if(d1<d2) return -1;
						return 0;
					}
			    	
			    	
			    	
			    });
			    
			    
				  System.out.println(" fin de  trie");

			    
			//  etape 3:  Initialiser une liste “solution” à vide.
			    
			 solution = new ArrayList<Arete>();
			    
//			    système d’étiquettes

			    Map<Point,Integer> Etiquette = new HashMap<Point,Integer>();
			    for(int i = 0;i<listePoint.size();i++) {
			    	
			    	Etiquette.put( listePoint.get(i),i);
			    	
			    	
			    }
			    
				  System.out.println(" fin de  mapEtiquette");

//			    Parcourir la liste des arêtes candidates par ordre croissant 
			    
			    while(!hasAllPoints(solution,listePoint)) {
				    
				    for(Arete a : aretes) {
				    	int x =Etiquette.get(a.p);
						int y = Etiquette.get(a.q);
				    	if(x != y) {
				    		
				    		//re-etiquetter tous les points ayant etiquette x en y 		
				    		reetiquette(Etiquette,x,y);
				    		solution.add(a);
				  		
				    	}
				    	
				    }
				    
			    }
				    
//			    System.out.println("size of solution : "+solution.size());
//			    System.out.println("nombre de points total : "+hitPoints.size());

//			    Traduire la liste “solution” en une structure d’arbre
			    
			    racine = solution.get(0).p;
			     result = listeToArbre(solution,racine);
			    
			  // fin de  Kruskal
	    return result;
	  }
	public Tree2D listeToArbre( ArrayList<Arete> solutions, Point racine) {
		
		  ArrayList<Arete> aretes =  (ArrayList<Arete>) solutions.clone();
		  ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
		   
		  for(int i=0;i<solutions.size();i++) {
			  	  
			  Arete a = solutions.get(i);
			  	  
			  if(racine.equals(a.p)) {
				  aretes.remove(a);
				  Tree2D sub = listeToArbre(aretes,a.q);
				  subTrees.add(sub);			  
				  
			  }else if(racine.equals(a.q)) {
				  aretes.remove(a);
				  Tree2D sub = listeToArbre(aretes,a.p);
				  subTrees.add(sub);
				  		  
			  }
		  }	  
		  
		    Tree2D result = new Tree2D(racine,subTrees);
		    	    	    	    
		    return result;	  
		  
	  }
	public void reetiquette(Map<Point,Integer> map, int x, int y) {
		  
		  for(Map.Entry<Point,Integer> entry : map.entrySet()) {
			  if(entry.getValue() == x) {
				  entry.setValue(y);
			  }
		  }
	  }
	public ArrayList<Point> calculConnectedDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		//REMOVE >>>>>
		ArrayList<Point> stable = getStable(points,edgeThreshold);
		Tree2D tree = calculSteiner( points, edgeThreshold,stable);
		
		ArrayList<Point> result = treeToList(tree);
		
		return stable;
	}
	
	
    public ArrayList<Point> algoSMIS(ArrayList<Point> points,ArrayList<Point> black, int edgeThreshold){
    	 ArrayList<Point> blue =  new ArrayList<Point>();
    	 
    	 ArrayList<Point> grey = (ArrayList<Point>) points.clone();
    	 grey.removeAll(black);
    	 
    	 int i = 5;
    	 while(i>1) {
    		
    		
    		 Point point = getNodes(grey,black,blue,i,edgeThreshold);
    			while(point!=null ) {
        			
    				
    				grey.remove(point);
    				blue.add(point);
    				
        		}
    		 i--;
    	 }
    	 return blue;
    }

    public Point getNodes(ArrayList<Point> grey,ArrayList<Point> black,
    		ArrayList<Point> blue,int i, int edgeThreshold){
    	
    	for(Point g : grey) {
    		
    	}
    	
    	return null;
    }
	
	
	 
    public ArrayList<Point> treeToList(Tree2D tree){
		
		
		ArrayList<Point> result = new ArrayList<Point>();
		
		result.add(tree.getRoot());
		for (int i=0;i<tree.getSubTrees().size();i++) {
			result.addAll(treeToList(tree.getSubTrees().get(i)));			
		}
		return result;
	}

	public ArrayList<Point> getStable(ArrayList<Point> points, int edgeThreshold){
		ArrayList<Point> result =  new ArrayList<Point>();

	
		
		for (Point p : points) {
			if (isIndependant(p,result, edgeThreshold)) {
				result.add(p);
			}
		}



		if(isValidStableDominants(points,result,edgeThreshold)) 
			System.out.println("valid");
		return result;
	}
	
	public boolean isIndependant(Point p,ArrayList<Point> dom, int edgeThreshold ) {
		
		for(Point d : dom) {
			if(d.distance(p)<= edgeThreshold) return false;
		}
		
		return true;
	}
	public boolean isValidStableDominants(ArrayList<Point> points, ArrayList<Point> stable,
			int edgeThreshold) {

		if(!isValidDominant(points,stable,edgeThreshold)) {
			System.out.println("pas dominant");
			return false;
		}

		for(int i = 0; i<stable.size();i++) {
			Point p = stable.get(i);
			for(int j=i+1;j<stable.size();j++) {
				Point p2 = stable.get(j);

				if(p.distance(p2)<=edgeThreshold) {
					return false;
				}
			}

		}

		return true;
	}

	public  boolean isValidDominant(ArrayList<Point> points, ArrayList<Point> dominants,
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
	public  ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold){
		ArrayList<Point> result = new ArrayList<Point>();

		for (Point point:vertices) {
			if (point.distance(p)<edgeThreshold && !point.equals(p))
				result.add((Point)point.clone());
		}
		return result;
	}
	public boolean hasAllPoints( ArrayList<Arete> aretes,ArrayList<Point>  points) {
		  Set<Point> lesPoints = new HashSet<Point>();
		  
		  for(Arete a : aretes) {
			  
			  lesPoints.add(a.p);
			  
			  lesPoints.add(a.q);

		  }
		  return lesPoints.size() >= points.size();
		  
		  
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
