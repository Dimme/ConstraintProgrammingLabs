package Lab2;

import java.util.ArrayList;

import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;

public class Rostering {
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

		// Allocating the variables
		IntVar[][] weekA = new IntVar[4][7];
		IntVar[][] weekB = new IntVar[3][7];
		IntVar[][] weekC = new IntVar[2][7];

		// Initiating the variables. Content of the variables is a boolean which
		// specifies if they can work or not work that specific day.
		ArrayList<IntVar> workers = new ArrayList<IntVar>();
		for (int i = 0; i < weekA.length; i++) {
			for (int j = 0; j < weekA[i].length; j++) {
				weekA[i][j] = new IntVar(store, "Worker: A" + (int) (i + 1)
						+ " @ Day: " + (int) (j + 1), 0, 1);
				workers.add(weekA[i][j]);
			}
		}
		for (int i = 0; i < weekB.length; i++) {
			for (int j = 0; j < weekB[i].length; j++) {
				weekB[i][j] = new IntVar(store, "Worker: B" + (int) (i + 1)
						+ " @ Day: " + (int) (j + 1), 0, 1);
				workers.add(weekB[i][j]);
			}
		}
		for (int i = 0; i < weekC.length; i++) {
			for (int j = 0; j < weekC[i].length; j++) {
				weekC[i][j] = new IntVar(store, "Worker: C" + (int) (i + 1)
						+ " @ Day: " + (int) (j + 1), 0, 1);
				workers.add(weekC[i][j]);
			}
		}

		// 1. In every day, at least 6 workers must be at work
		for (int i = 0; i < 7; i++) {
			ArrayList<IntVar> day = new ArrayList<IntVar>();
			for (int j = 0; j < 4; j++) {
				day.add(weekA[j][i]);
			}
			for (int k = 0; k < 3; k++) {
				day.add(weekB[k][i]);
			}
			for (int x = 0; x < 2; x++) {
				day.add(weekC[x][i]);
			}
			IntVar atLeast6 = new IntVar(store, 6, 10);
			store.impose(new Sum(day, atLeast6));

		}
		
		// 2. Workers of types A and C work 5 days/week, whereas those of type B
		// only work 4 days/week.
		for (int i = 0; i < 4; i++) {
			store.impose(new Sum(weekA[i], new IntVar(store, 5, 5)));
		}
		for (int i = 0; i < 3; i++) {
			store.impose(new Sum(weekB[i], new IntVar(store, 4, 4)));
		}
		for (int i = 0; i < 2; i++) {
			store.impose(new Sum(weekC[i], new IntVar(store, 5, 5)));
		}
		
		// 3. Workers of types A and C cannot work for 4 consecutive days, and
		// those of type B should not work more than 2 days in a row.
		int aDays = 3;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 7; j++) {
				IntVar[] consDays = new IntVar[aDays + 1];
				for (int k = 0; k < consDays.length; k++) {
					consDays[k] = weekA[i][(j + k) % 7];
				}
				store.impose(new Sum(consDays, new IntVar(store, 0, aDays)));
			}
		}
		int bDays = 2;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 7; j++) {
				IntVar[] consDays = new IntVar[bDays + 1];
				for (int k = 0; k < consDays.length; k++) {
					consDays[k] = weekB[i][(j + k) % 7];
				}
				store.impose(new Sum(consDays, new IntVar(store, 0, bDays)));
			}
		}

		int cDays = 3;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 7; j++) {
				IntVar[] consDays = new IntVar[cDays + 1];
				for (int k = 0; k < consDays.length; k++) {
					consDays[k] = weekC[i][(j + k) % 7];
				}
				store.impose(new Sum(consDays, new IntVar(store, 0, cDays)));
			}
		}
		
		// 4. If only 2 workers of type A are scheduled in a day, then three
		// workers of type B must work on that day.
		for (int i = 0; i < 7; i++) {
			IntVar[] dayA = new IntVar[] { weekA[0][i], weekA[1][i],
					weekA[2][i], weekA[3][i] };
			IntVar[] dayB = new IntVar[] { weekB[0][i], weekB[1][i],
					weekB[2][i] };
			IntVar sumA = new IntVar(store, 0, 1000);
			IntVar sumB = new IntVar(store, 0, 1000);
			store.impose(new Sum(dayA, sumA));
			store.impose(new Sum(dayB, sumB));
			store.impose(new IfThen(new XeqC(sumA, 2), new XeqC(sumB, 3)));
		}
		
		// 5. At no day should exactly 2 workers of type A and 2 workers of type
		// C work together.
		for (int i = 0; i < 7; i++) {
			IntVar[] dayA = new IntVar[] { weekA[0][i], weekA[1][i],
					weekA[2][i], weekA[3][i] };
			IntVar[] dayC = new IntVar[] { weekC[0][i], weekC[1][i] };
			IntVar sumA = new IntVar(store, 0, 1000);
			IntVar sumC = new IntVar(store, 0, 1000);
			store.impose(new Sum(dayA, sumA));
			store.impose(new Sum(dayC, sumC));
			store.impose(new IfThen(new XeqC(sumA, 2), new XneqC(sumC, 2)));
			store.impose(new IfThen(new XeqC(sumC, 2), new XneqC(sumA, 2)));
		}

		System.out.println("Number of variables: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(workers
				.toArray(new IntVar[0]), new SmallestDomain<IntVar>(),
				new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());
		label.getSolutionListener().searchAll(true);
		label.getSolutionListener().recordSolutions(true);

		boolean result = label.labeling(store, select);

		if (result) {
			System.out.println("\n*** Yes");
			// label.getSolutionListener().printAllSolutions();
		} else
			System.out.println("\n*** No");
	}
}