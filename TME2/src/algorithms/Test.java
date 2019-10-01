package algorithms;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main (String[] args) {
		HashSet<HashSet<Integer>> a1 = new HashSet<>();
		HashSet<Integer> a2 = new HashSet<>();
		a2.add(1);
		a2.add(3);
		a2.add(2);
		a1.add(a2);
		ArrayList<Integer> a3 = new ArrayList<>();
		a3.add(1);
		a3.add(2);
		a3.add(3);
		a3.add(4);
		a3.add(5);
		a3.add(6);
		a3.add(7);
		a3.add(8);
		System.out.println(a3);
		Collections.shuffle(a3);
		System.out.println(a3);
	}
}
