import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;

public class Post {

	static Store store;

	public static void main(String[] args) {
		long T1, T2, dt;
		T1 = System.currentTimeMillis();

		post();

		T2 = System.currentTimeMillis();
		dt = T2 - T1;
		System.out.println("\n\t*** Execution time = " + dt + " ms");
	}

	static void post() {
		store = new Store();

		IntVar M = new IntVar(store, "M", 0, 30);
		IntVar T = new IntVar(store, "T", 0, 30);
		IntVar W = new IntVar(store, "W", 0, 30);
		IntVar H = new IntVar(store, "H", 0, 30);
		IntVar F = new IntVar(store, "F", 0, 30);
		IntVar S = new IntVar(store, "S", 0, 30);
		IntVar U = new IntVar(store, "U", 0, 30);

		IntVar[] week = { M, T, W, H, F, S, U };

		IntVar sum1 = new IntVar(store, 17, 100);
		store.impose(new Sum(new IntVar[] { M, U, S, F, H }, sum1));
		IntVar sum2 = new IntVar(store, 13, 100);
		store.impose(new Sum(new IntVar[] { T, M, U, S, F }, sum2));
		IntVar sum3 = new IntVar(store, 15, 100);
		store.impose(new Sum(new IntVar[] { W, T, M, U, S }, sum3));
		IntVar sum4 = new IntVar(store, 19, 100);
		store.impose(new Sum(new IntVar[] { H, W, T, M, U }, sum4));
		IntVar sum5 = new IntVar(store, 14, 100);
		store.impose(new Sum(new IntVar[] { F, H, W, T, M }, sum5));
		IntVar sum6 = new IntVar(store, 16, 100);
		store.impose(new Sum(new IntVar[] { S, F, H, W, T }, sum6));
		IntVar sum7 = new IntVar(store, 11, 100);
		store.impose(new Sum(new IntVar[] { U, S, F, H, W }, sum7));

		IntVar cost = new IntVar(store, "cost", 0, 100);
		store.impose(new Sum(week, cost));

		System.out.println("Number of variables: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(week,
				new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());
		boolean Result = label.labeling(store, select, cost);

		if (Result) {
			System.out.println("\n*** Yes");
			System.out.println("Solution : " + java.util.Arrays.asList(week));
		} else
			System.out.println("\n*** No");
	}
}
