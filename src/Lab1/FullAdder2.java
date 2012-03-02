package Lab1;
import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;

public class FullAdder2 {
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
		
		IntVar a = new IntVar(store, "A", 0, 1);
		IntVar b = new IntVar(store, "B", 0, 1);
		IntVar c = new IntVar(store, "C", 0, 1);
		IntVar inv_cout = new IntVar(store, "Inverted Cout", 0, 1);
		IntVar inv_sum = new IntVar(store, "Inverted Sum", 0, 1);
		IntVar sum = new IntVar(store, "Sum", 0, 1);
		
		// Crosspoints
		IntVar t2 = new IntVar(store, 0, 1);
		IntVar t3 = new IntVar(store, 0, 1);
		IntVar t4 = new IntVar(store, 0, 1);
		IntVar t5 = new IntVar(store, 0, 1);
		IntVar t6 = new IntVar(store, 0, 1);
		IntVar t7 = new IntVar(store, 0, 1);
		IntVar t8 = new IntVar(store, 0, 1);
		IntVar t9 = new IntVar(store, 0, 1);
		IntVar t10 = new IntVar(store, 0, 1);
		IntVar t11 = new IntVar(store, 0, 1);
		
		// a) One stage inverse carry module
		store.impose(rtran(a,t2,1));
		store.impose(rtran(c,inv_cout,t2));
		store.impose(ntran(c,inv_cout,t3));
		store.impose(ntran(a,t3,0));
		store.impose(rtran(b,t2,1));
		store.impose(ntran(b,t3,0));
		store.impose(rtran(b,t10,1));
		store.impose(rtran(a,inv_cout,t10));
		store.impose(ntran(a,t11,inv_cout));
		store.impose(ntran(b,t11,0));
		
		// b) One stage inverse sum module
		store.impose(rtran(a,t4,1));
		store.impose(rtran(b,t4,1));
		store.impose(rtran(c,t4,1));
		store.impose(rtran(inv_cout,t4,inv_sum));
		store.impose(ntran(inv_cout,inv_sum,t5));
		store.impose(ntran(a,t5,0));
		store.impose(ntran(b,t5,0));
		store.impose(ntran(c,t5,0));
		store.impose(rtran(a,t6,1));
		store.impose(rtran(b,t6,t7));
		store.impose(rtran(c,t7,inv_sum));
		store.impose(ntran(c,inv_sum,t8));
		store.impose(ntran(b,t8,t9));
		store.impose(ntran(a,t9,0));
		
		// c) Inverter module
		store.impose(rtran(inv_sum,sum,1));
		store.impose(ntran(inv_sum,sum,0));
		
		System.out.println("Number of variables: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());
		
		IntVar[] var = {a,b,c,inv_cout,inv_sum,sum};
		
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
	
	static PrimitiveConstraint ntran(IntVar base, IntVar collector, int emitter) {
		return new IfThen(new XeqC(base, 1), new XeqC(collector, emitter));
	}
	
	static PrimitiveConstraint rtran(IntVar base, IntVar collector, int emitter) {
		return new IfThen(new XeqC(base, 0), new XeqC(collector, emitter));
	}
	
	static PrimitiveConstraint ntran(IntVar base, IntVar collector, IntVar emitter) {
		return new IfThen(new XeqC(base, 1), new XeqY(collector, emitter));
	}
	
	static PrimitiveConstraint rtran(IntVar base, IntVar collector, IntVar emitter) {
		return new IfThen(new XeqC(base, 0), new XeqY(collector, emitter));
	}
}
