package Lab1;
import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;

public class Shopping2 {

	static Store store;
	
	public static void main(String[] args){
		long T1, T2, T;
		T1 = System.currentTimeMillis();
		post();
		T2 = System.currentTimeMillis();
		T = T2-T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");
	}
	
	static void post(){
		
		store = new Store();
		
		int[] weights = {50,300,400,250};
		IntVar[] amount = {new IntVar(store,0,1),new IntVar(store,0,1),new IntVar(store,0,1),new IntVar(store,0,1)};
		Constraint ctr = new SumWeight(amount,weights,new IntVar(store,0,500));
		store.impose(ctr);
		
		int[] costs = {50,450,520,100};
		IntVar cost = new IntVar(store,0,500);
		Constraint ctr2 = new SumWeight(amount,costs,cost);
		store.impose(ctr2);
	
		IntVar negatedCost = new IntVar(store,-500,0);
		Constraint ctr3 = new XmulCeqZ(cost,-1,negatedCost);
		store.impose(ctr3);
		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(amount,
		new SmallestDomain<IntVar>(),
		new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());
		boolean result = label.labeling(store, select,negatedCost);
		
		if (result) {
			System.out.println("\n*** Yes");
			label.printAllSolutions();
			}
			else System.out.println("\n*** No");
	}
}