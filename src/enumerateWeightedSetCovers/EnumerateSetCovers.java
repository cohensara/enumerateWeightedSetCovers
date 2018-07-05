package enumerateWeightedSetCovers;

import java.util.ArrayList;
import java.util.Vector;
import java.util.PriorityQueue;
import java.util.BitSet;
import java.util.HashSet;
import java.io.FileNotFoundException;

public class EnumerateSetCovers {

	// set cover problem to be solved
	private SetCoverProblem problem;
	
	// number of sets in the problem
	private int numOfSets;
	
	// size of universe
	private int universeSize;
	
	// size of intervals for logging
	private int interval = StatisticsLogger.getStat("interval");
	
	// starting time 
	private long startingTime;

	// weight of the worst entry in the queue. (once the queue has the maximum amount, should not add worse solutions)
	private int worstWeightInQueue = 0;
	
	// maximum number of results to be generated
	private int maxResults;
		
	// priority queue used in the algorithm	
	// different from the pseudocode in that we use a single queue for elements of both Q1 and Q2 
	private PriorityQueue<QueueEntry> pq = new PriorityQueue<QueueEntry>();
			
	// adds an entry to the queue
	// if the queue contains the maximum number of elements, then only adds an entry if it is better than the current worst
	private void addToQueue(QueueEntry entry, PriorityQueue<QueueEntry> pq) {
		
			if (pq.size() < maxResults) {
				
				pq.add(entry);
				int weight = entry.sol.getWeight();
				if (weight > worstWeightInQueue) 
					worstWeightInQueue = weight;
			}
			else {
				if (entry.sol.getWeight() < worstWeightInQueue) pq.add(entry);
			}
	}
	
	
	// the part of the algorithm that adds elements to Q2 of the pseudo code while processing entries from Q1
	private void addToQ2WhileProcessingQ1(QueueEntry entry, PriorityQueue<QueueEntry> pq) {
		
		BitSet newInThird = (BitSet)entry.third.clone();
		newInThird.andNot(entry.sol.getSolution());
		if (!newInThird.isEmpty()){
					
			int bestSetToAdd = -1;
			int bestWeight = Integer.MAX_VALUE;
			for (int i = newInThird.nextSetBit(0) ; i != -1 ; i = newInThird.nextSetBit(i+1)) {
				int weight = problem.getWeight(i);
				if (weight < bestWeight) {
					bestWeight = weight;
					bestSetToAdd = i;
				}
			}
					
			Solution newSolution = (Solution)(entry.sol.clone());
			newSolution.addSet(bestSetToAdd, bestWeight);
			BitSet second = new BitSet();
			second.set(bestSetToAdd);
			newInThird.set(bestSetToAdd, false);
			QueueEntry newEntry = new QueueEntry(newSolution, second, newInThird,false);
			addToQueue(newEntry,pq);
		}
	}
	
	// process an entry that has been removed from Q1
	private void dealWithQ1Entry(QueueEntry entry, PriorityQueue<QueueEntry> pq, boolean nonRedundant) {
		
		// if we allow redundant entries (i.e., entries that are not minimal with respect to containment), add to Q2
		// otherwise never add to Q2 (as these will always be redundant)
		if (!nonRedundant) {
			addToQ2WhileProcessingQ1(entry, pq);
		}
				
		// add to q1
		BitSet canBeRemoved = (BitSet)entry.sol.getSolution().clone();
		canBeRemoved.andNot(entry.second);
				
		BitSet third = entry.third;
		BitSet second = entry.second;
		second = (BitSet)second.clone();
		int i=0;
		for (int si = canBeRemoved.nextSetBit(0) ; si != -1 ; si = canBeRemoved.nextSetBit(si+1)) {
			
			third = (BitSet)third.clone();		
			third.set(si, false);
			BitSet covered = problem.getCoveredBy(second);
			Solution newSolution = (new GreedyMinSetCover()).approxSetCover(problem, covered, third); //, nonRedundant);
			
			if (newSolution != null) {
				
				for (int j = second.nextSetBit(0) ; j != -1 ; j = second.nextSetBit(j+1)) {
					newSolution.addSet(j, problem.getWeight(j));
				}

				QueueEntry newEntry = new QueueEntry(newSolution, second, third, true);
				addToQueue(newEntry,pq);
			}
			
			second = (BitSet)second.clone();
			second.set(si);
		}	
	}

	
	/** 
	  * enumerate set covers
	  * @param problem is the weighted set cover problem
	  * @param maxResults is the maximum number of results to be returned
	  * @param nonRedundant determines whether only non-redundant results should be returned
	  */
	public void enumerate(SetCoverProblem problem, int maxResults, boolean nonRedundant) {
		
		startingTime = System.nanoTime();
		this.problem = problem;
		this.maxResults = maxResults;
		numOfSets = problem.getNumberOfSets();
		universeSize = problem.getUniverseSize();
		HashSet<BitSet> printedAlready = new HashSet<BitSet>();
		
		Solution s = (new GreedyMinSetCover()).approxSetCover(problem); 
					
		if (s != null) {
			BitSet none = new BitSet(numOfSets);
			BitSet all = new BitSet(numOfSets);
			all.set(0, numOfSets);
			if (nonRedundant) s = problem.makeNonRedundant(s, new BitSet());
			StatisticsLogger.changeStat("firstWeight", s.getWeight());
			addToQueue(new QueueEntry(s, none, all, true), pq);
		}
		
		int numResults = 0;
		int bestScoreInInterval = Integer.MAX_VALUE;
		
		while (!pq.isEmpty()) {
					
			QueueEntry entry = pq.poll();
			
			Solution newSolution = entry.sol;
			boolean foundNew = true;
			
			if (nonRedundant) {
				newSolution = problem.makeNonRedundant(newSolution, new BitSet());
				if (printedAlready.contains(newSolution)) foundNew = false;
				else printedAlready.add(newSolution.getSolution());
			}
			
			if (foundNew) {
				numResults++;
				if (newSolution.getWeight() < bestScoreInInterval) {
					bestScoreInInterval = newSolution.getWeight();
				}
				
				if (StatisticsLogger.minStat("lowestWeight", newSolution.getWeight()))
					StatisticsLogger.changeStat("whenBestFound", numResults);
			}
			
			if (numResults % interval == 0 && bestScoreInInterval != Integer.MAX_VALUE) {
				
					StatisticsLogger.addInterval((System.nanoTime() - startingTime)/1000000, bestScoreInInterval);
					bestScoreInInterval = Integer.MAX_VALUE;
			}
			
			if (numResults == maxResults) {
				break;
			}
			
			if (entry.fromQ1) {
				
				dealWithQ1Entry(entry, pq, nonRedundant);
			}
			else {
				dealWithQ2Entry(entry, pq);
			}
		}
		
		//if (pq.isEmpty()) System.out.println("Empty! Number of results:" + numResults);
	}
	
	
	// deal with an entry from Q2
	private void dealWithQ2Entry(QueueEntry entry, PriorityQueue<QueueEntry> pq) {
		
		BitSet second = entry.second;
		BitSet third = entry.third;
		if (!third.isEmpty()) {
			int bestSetToAdd = -1;
			int bestWeight = Integer.MAX_VALUE;
			for (int i = third.nextSetBit(0) ; i != -1 ; i = third.nextSetBit(i+1)) {
				int weight = problem.getWeight(i);
				if (weight < bestWeight) {
					bestWeight = weight;
					bestSetToAdd = i;
				}
			}
			Solution newSolution1 = (Solution)(entry.sol.clone());
			Solution newSolution2 = (Solution)(entry.sol.clone());
			newSolution1.addSet(bestSetToAdd, bestWeight);
			newSolution2.addSet(bestSetToAdd, bestWeight);
			newSolution1.removeSet(second.nextSetBit(0), problem.getWeight(bestSetToAdd));
					
			BitSet single = new BitSet();
			single.set(bestSetToAdd);
			BitSet newThird1 = (BitSet)third.clone();
			BitSet newThird2 = (BitSet)third.clone();
			newThird1.set(bestSetToAdd, false);
			newThird2.set(bestSetToAdd, false);
			addToQueue(new QueueEntry(newSolution1, single, newThird1, false), pq);
			addToQueue(new QueueEntry(newSolution2, single, newThird2, false), pq);
		}
	}
	
	public static void main(String[] inp) throws FileNotFoundException{
		
		if (inp.length != 3) {
			
			System.out.println("Usage: java EnumerateSetCovers <input file> <num results> <nonRedundant?>");
			System.exit(-1);
		}
		EnumerateSetCovers enumsc = new EnumerateSetCovers();
		enumsc.enumerate(SetCoverProblemGenerator.generateSetCoverProblem(inp[0]), Integer.parseInt(inp[1]), Boolean.parseBoolean(inp[2]));
		System.out.println("First Weight: " + StatisticsLogger.getStat("firstWeight"));
		System.out.println("Best Weight: " + StatisticsLogger.getStat("lowestWeight") + " at " + StatisticsLogger.getStat("whenBestFound"));
	}
}


/**
  * Class QueueEntry is used as entry values for the priority queue
  * Each entry contains 4 fields:
  *    - sol: the solution represented by the entry
  *    - second: a bit set indicating which sets are allowed in the "second" part of the queue entry as in the paper
  *    - third: a bit set indicating which sets are allowed in the "third" part of the queue entry as in the paper
  *    - fromQ1: a boolean value indicating if this is a Q1 entry or Q2 entry (from the paper)
  */
class QueueEntry implements Comparable<QueueEntry> {

	Solution sol;
	BitSet second;
	BitSet third;
	boolean fromQ1;
	
	public QueueEntry(Solution sol, BitSet second, BitSet third, boolean fromQ1) {
		
		this.sol = sol;
		this.second = second;
		this.third = third;
		this.fromQ1 = fromQ1;
	}
	
	public int compareTo(QueueEntry other) {
		return sol.getWeight() - other.sol.getWeight();
	}
	
	static String bitSetString(BitSet bs) {
		
		String s = "{";
		for (int i = bs.nextSetBit(0); i != -1 ; i = bs.nextSetBit(i+1)) {
			s += "" + i + " ";
		}
		return s + "}";
	}
	
	public String toString() {
		
		String queue = "Q1: ";
		if (!fromQ1) queue = "Q2: ";
		return queue + sol.toString() + " | " + bitSetString(second) + " | " + bitSetString(third);
	}

}