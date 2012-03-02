package Lab1;
import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;

public class Shopping {
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

		IntVar cost = new IntVar(store, "Cost", 0, 500);
		IntVar inv_cost = new IntVar(store, "Inverted Cost", -500, 0);
		IntVar weight = new IntVar(store, "Weight", 0, 500);

		IntVar sunglasses = new IntVar(store, "Sunglasses", 0, 10);
		IntVar swimsuit = new IntVar(store, "Swimsuits", 0, 10);
		IntVar towel = new IntVar(store, "Towels", 0, 10);
		IntVar cooler = new IntVar(store, "Coolers", 0, 10);

		IntVar[] products = { sunglasses, swimsuit, towel, cooler };
		int[] values = { 50, 450, 520, 100 };
		int[] weights = { 50, 300, 400, 250 };

		store.impose(new SumWeight(products, values, cost));
		store.impose(new SumWeight(products, weights, weight));
		store.impose(new XmulCeqZ(cost, -1, inv_cost));

		System.out.println("Number of variables: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(products,
				new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());

		boolean result = label.labeling(store, select, inv_cost);

		if (result) {
			System.out.println("\n*** Yes");
			System.out.println("Solution : "
					+ java.util.Arrays.asList(sunglasses, swimsuit, towel,
							cooler, weight, cost));
		} else
			System.out.println("\n*** No");
	}
}
