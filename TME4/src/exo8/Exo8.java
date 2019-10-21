package exo8;

import java.util.Random;

public class Exo8 {

	public static void main(String[] args) {

		long graine = 0;
		Random rand = new Random(graine);
		for(int i =0;i<10;i++) {
			double x = rand.nextDouble();
	        System.out.println(x);     
		}
		  
	}

}
