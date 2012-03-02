include "globals.mzn";

% The start times for each task
array[1..6, 1..6] of var 0..100: taskStartTimes;

% The durations for each task
array[1..6, 1..6] of int: taskDurations = 	[|1,3,6,7,3,6,
											 |8,5,10,10,10,4,
											 |5,4,8,9,1,7,
											 |5,5,5,3,8,9,
											 |9,3,5,4,3,1,
											 |3,3,9,10,4,1|];

% The tasks should be executed in the right order
constraint forall (i in 1..6, j in 1..5)
	(taskStartTimes[i,j] + taskDurations[i,j] <= taskStartTimes[i,j + 1]);

% Cumulatives for each machine. Tuples are [Job,Task]

% Machine0
constraint cumulative (
	[taskStartTimes[1,2],taskStartTimes[4,2],taskStartTimes[3,4],taskStartTimes[6,4],taskStartTimes[2,5],taskStartTimes[5,5]],
	[taskDurations[1,2],taskDurations[4,2],taskDurations[3,4],taskDurations[6,4],taskDurations[2,5],taskDurations[5,5]],
	[1,1,1,1,1,1],1 
);

% Machine1
constraint cumulative (
	[taskStartTimes[2,1],taskStartTimes[4,1],taskStartTimes[6,1],taskStartTimes[5,2],taskStartTimes[1,3],taskStartTimes[3,5]],
	[taskDurations[2,1],taskDurations[4,1],taskDurations[6,1],taskDurations[5,2],taskDurations[1,3],taskDurations[3,5]],
	[1,1,1,1,1,1],1
);

% Machine2
constraint cumulative (
	[taskStartTimes[1,1],taskStartTimes[3,1],taskStartTimes[5,1],taskStartTimes[2,2],taskStartTimes[4,3],taskStartTimes[6,6]],
	[taskDurations[1,1],taskDurations[3,1],taskDurations[5,1],taskDurations[2,2],taskDurations[4,3],taskDurations[6,6]],
	[1,1,1,1,1,1],1
);

% Machine3
constraint cumulative (
	[taskStartTimes[3,2],taskStartTimes[6,2],taskStartTimes[1,4],taskStartTimes[4,4],taskStartTimes[2,6],taskStartTimes[5,6]],
	[taskDurations[3,2],taskDurations[6,2],taskDurations[1,4],taskDurations[4,4],taskDurations[2,6],taskDurations[5,6]],
	[1,1,1,1,1,1],1
);

% Machine4
constraint cumulative (
	[taskStartTimes[2,3],taskStartTimes[5,3],taskStartTimes[4,5],taskStartTimes[6,5],taskStartTimes[1,6],taskStartTimes[3,6]],
	[taskDurations[2,3],taskDurations[5,3],taskDurations[4,5],taskDurations[6,5],taskDurations[1,6],taskDurations[3,6]],
	[1,1,1,1,1,1],1
);

% Machine5
constraint cumulative (
	[taskStartTimes[3,3],taskStartTimes[6,3],taskStartTimes[2,4],taskStartTimes[5,4],taskStartTimes[1,5],taskStartTimes[4,6]],
	[taskDurations[3,3],taskDurations[6,3],taskDurations[2,4],taskDurations[5,4],taskDurations[1,5],taskDurations[4,6]],
	[1,1,1,1,1,1],1
);

% Execution time for each machine
array[1..6] of var 0..100: machineTimes;
constraint forall (i in 1..6)
	(machineTimes[i] = taskStartTimes[i,6] + taskDurations[i,6]);
	
% The total execution time is the time of the machine that executes the most
var 0..100: totalTime = max(machineTimes);

solve minimize totalTime;

output[show(totalTime)];