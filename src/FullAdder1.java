import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;

public class FullAdder1 {
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
		
		IntVar in1 = new IntVar(store, "In1", 0, 1);
		IntVar in2 = new IntVar(store, "In2", 0, 1);
		IntVar c = new IntVar(store, "C", 0, 1);
		IntVar t1 = new IntVar(store, "T1", 0, 1);
		IntVar t2 = new IntVar(store, "T2", 0, 1);
		IntVar t3 = new IntVar(store, "T3", 0, 1);
		IntVar s = new IntVar(store, "S", 0, 1);
		IntVar carry = new IntVar(store, "Carry", 0, 1);
		
		store.impose(new XorBool(in1, in2, t1));
		store.impose(new XorBool(t1, c, s));
		store.impose(new AndBool(new IntVar[] {in1,in2}, t2));
		store.impose(new AndBool(new IntVar[] {c,t1}, t3));
		store.impose(new OrBool(new IntVar[] {t3,t2}, carry));
		
		System.out.println("Number of variables: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());
		
		IntVar[] var = {in1,in2,c,s,carry};
		
		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(var,null,new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());
		label.getSolutionListener().searchAll(true); 
		label.getSolutionListener().recordSolutions(true);
		
		
		boolean result = label.labeling(store, select);

		if (result) {
			System.out.println("\n*** Yes");
			label.getSolutionListener().printAllSolutions();
		} else
			System.out.println("\n*** No");
		
	}
}
