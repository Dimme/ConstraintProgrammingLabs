package Lab2;

import java.util.ArrayList;

import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;


public class Concert {
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
		final int CONCERT_DURATION = 33;
		final int PIECES = 9;
		final int PLAYERS = 5;
		final int[][] PLAY_OR_NOT = { { 1, 1, 0, 1, 0, 1, 1, 0, 1 },
								  { 1, 1, 0, 1, 1, 1, 0, 1, 0 },
								  { 1, 1, 0, 0, 0, 0, 1, 1, 0 },
								  { 1, 0, 0, 0, 1, 1, 0, 0, 1 },
								  { 0, 0, 1, 0, 1, 1, 1, 1, 0 } };
		final int[] DURATIONS = { 2, 4, 1, 3, 3, 2, 5, 7, 6 };

		// Start times for all pieces. A piece may start between time 0 and MAX_LENGTH - (last piece length).
		IntVar[] startTimes = new IntVar[PIECES];
		for (int i = 0; i < PIECES; i++) {
			startTimes[i] = new IntVar(store,"Piece"+(i+1),0,CONCERT_DURATION);
		}
		
		// Durations for each piece.
		IntVar[] durations = new IntVar[PIECES];
		for (int i = 0; i < PIECES; i++) {
			durations[i] = new IntVar(store,DURATIONS[i],DURATIONS[i]);
		}
		// 1 resource may be utilized at time. 2 or more pieces may not be performed in parallel.  
		IntVar[] resources = new IntVar[PIECES];
		for (int i = 0; i < PIECES; i++) {
			resources[i] = new IntVar(store,1,1);
		}
		// Impose the cumulative constraint
		store.impose(new Cumulative(startTimes, durations, resources, new IntVar(store,1,1)));
		
		// Finding the waiting time for each player
		ArrayList<IntVar> playerWaitingTimes = new ArrayList<IntVar>();
		for (int i = 0; i < PLAYERS; i++) {
			ArrayList<IntVar> myStarttimes = new ArrayList<IntVar>();
			for (int j = 0; j < PIECES; j++) {
				if (PLAY_OR_NOT[i][j] == 1) {
					myStarttimes.add(startTimes[j]);
				}
			}
			
			IntVar myMax = new IntVar(store,0,CONCERT_DURATION);
			IntVar myMin = new IntVar(store,0,CONCERT_DURATION);
			IntVar[] myWaitingTimes = new IntVar[PIECES];
			IntVar myTotalWaitingTime = new IntVar(store, 0, CONCERT_DURATION);
			
			store.impose(new Max(myStarttimes, myMax));
			store.impose(new Min(myStarttimes, myMin));
			
			for (int j = 0; j < PIECES; j++) {
				myWaitingTimes[j] = new IntVar(store,0,CONCERT_DURATION);
				if (PLAY_OR_NOT[i][j] == 0 ) {
					store.impose(new IfThenElse(
							new And(new XgtY(startTimes[j],myMin), new XltY(startTimes[j],myMax)), // If starttime is between max and min 
							new XeqC(myWaitingTimes[j], DURATIONS[j]), // Then
							new XeqC(myWaitingTimes[j], 0)) // Else
					);
				}
			}
			
			store.impose(new Sum(myWaitingTimes, myTotalWaitingTime));
			
			playerWaitingTimes.add(myTotalWaitingTime);
		}
		
		// The total waiting time is the sum of all players waiting times
		IntVar totalWaitingTime = new IntVar(store, "Total waiting time", 0, 73);
		store.impose(new Sum(playerWaitingTimes, totalWaitingTime));
		
		
		
		System.out.println("Number of variables: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(startTimes, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());

		boolean result = label.labeling(store, select, totalWaitingTime);

		if (result) {
			System.out.println("\n*** Yes");
			label.getSolutionListener().printAllSolutions();
			System.out.println("Min total waiting time: " + totalWaitingTime.min());
		} else
			System.out.println("\n*** No");
		
	}
}