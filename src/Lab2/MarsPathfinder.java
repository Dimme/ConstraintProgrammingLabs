package Lab2;

import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;

public class MarsPathfinder {
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
		
		// Choose for which temperature
		int TEMP = 40; // 60, 80

		// Start times for each operation
		IntVar hwm12 = new IntVar(store, 0, 65);
		IntVar hwm34 = new IntVar(store, 0, 65);
		IntVar hwm56 = new IntVar(store, 0, 65);
		IntVar hsm12 = new IntVar(store, 0, 65);
		IntVar hsm34 = new IntVar(store, 0, 65);
		IntVar hd1 = new IntVar(store, 0, 65);
		IntVar hd2 = new IntVar(store, 0, 65);
		IntVar steer1 = new IntVar(store, 0, 65);
		IntVar steer2 = new IntVar(store, 0, 65);
		IntVar drive1 = new IntVar(store, 0, 65);
		IntVar drive2 = new IntVar(store, 0, 65);
		IntVar[] startTimes = new IntVar[] { hwm12, hwm34, hwm56, hsm12, hsm34, hd1, hd2, steer1, steer2, drive1, drive2 };

		// Durations for each operation
		final int[] DURATIONS = { 5, 5, 5, 5, 5, 10, 10, 5, 5, 10, 10 };
		
		// Required resources for each operation at different temperatures
		final int[] RESOURCES_40 = { 76, 76, 76, 76, 76, 51, 51, 43, 43, 75, 75 };
		final int[] RESOURCES_60 = { 95, 95, 95, 95, 95, 61, 61, 62, 62, 109, 109 };
		final int[] RESOURCES_80 = { 113, 113, 113, 113, 113, 73, 73, 81, 81, 138, 138 };

		// IntVars for the durations
		IntVar[] durations = new IntVar[11];
		for (int i = 0; i < durations.length; i++) {
			durations[i] = new IntVar(store, DURATIONS[i], DURATIONS[i]);
		}

		// IntVars for the resources
		IntVar[] resources = new IntVar[11];
		for (int i = 0; i < resources.length; i++) {
			if (TEMP == 40)
				resources[i] = new IntVar(store, RESOURCES_40[i], RESOURCES_40[i]);
			else if (TEMP == 60)
				resources[i] = new IntVar(store, RESOURCES_60[i], RESOURCES_60[i]);
			else
				resources[i] = new IntVar(store, RESOURCES_80[i], RESOURCES_80[i]);
		}

		// Limit on available resources
		// Battery power + Solar power - CPU consumption
		IntVar limit;
		if (TEMP == 40)
			limit = new IntVar(store, 0, 100 + 149 - 25);
		else if (TEMP == 60)
			limit = new IntVar(store, 0, 100 + 120 - 31);
		else
			limit = new IntVar(store, 0, 100 + 90 - 37);

		// The cumulative constraint for scheduling
		store.impose(new Cumulative(startTimes, durations, resources, limit));

		// Heating steering motors before steering
		store.impose(XplusClteqY(hsm12, 5, steer1));
		store.impose(XplusCgteqY(hsm12, 50, steer1));
		store.impose(XplusClteqY(hsm12, 5, steer2));
		store.impose(XplusCgteqY(hsm12, 50, steer2));
		store.impose(XplusClteqY(hsm34, 5, steer1));
		store.impose(XplusCgteqY(hsm34, 50, steer1));
		store.impose(XplusClteqY(hsm34, 5, steer2));
		store.impose(XplusCgteqY(hsm34, 50, steer2));

		// Heating wheel motors before driving
		store.impose(XplusClteqY(hwm12, 5, drive1));
		store.impose(XplusCgteqY(hwm12, 50, drive1));
		store.impose(XplusClteqY(hwm12, 5, drive2));
		store.impose(XplusCgteqY(hwm12, 50, drive2));
		store.impose(XplusClteqY(hwm34, 5, drive1));
		store.impose(XplusCgteqY(hwm34, 50, drive1));
		store.impose(XplusClteqY(hwm34, 5, drive2));
		store.impose(XplusCgteqY(hwm34, 50, drive2));
		store.impose(XplusClteqY(hwm56, 5, drive1));
		store.impose(XplusCgteqY(hwm56, 50, drive1));
		store.impose(XplusClteqY(hwm56, 5, drive2));
		store.impose(XplusCgteqY(hwm56, 50, drive2));

		// Hazard detection before steering
		store.impose(XplusClteqY(hd1, 10, steer1));
		store.impose(XplusClteqY(hd2, 10, steer2));

		// Steering before driving
		store.impose(XplusClteqY(steer1, 5, drive1));
		store.impose(XplusClteqY(steer2, 5, drive2));

		// Drive1 before Drive2
		store.impose(new XlteqY(drive1, drive2));

		// The total time that is to be minimized
		IntVar totalTime = new IntVar(store, "Shortest time", 0, 65);
		store.impose(new XplusCeqZ(drive2, 10, totalTime));
		
		
		System.out.println("Number of variables: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(startTimes, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());

		boolean result = label.labeling(store, select, totalTime);

		if (result) {
			System.out.println("\n*** Yes");
			label.getSolutionListener().printAllSolutions();
			System.out.println(totalTime);
		} else
			System.out.println("\n*** No");
	}

	static PrimitiveConstraint XplusClteqY(IntVar x, int c, IntVar y) {
		return new XplusClteqZ(x, c, y);
	}

	static PrimitiveConstraint XplusCgteqY(IntVar x, int c, IntVar y) {
		return new XplusClteqZ(y, -c, x);
	}
}
