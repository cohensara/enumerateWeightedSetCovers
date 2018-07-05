package enumerateWeightedSetCovers;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.BitSet;


/**
  * This class is used to implement a greedy algorithm that finds a single solution to the weighted set cover problem
  */
public class GreedyMinSetCover {
		
		/** 
		  * size of the universe
		  */
		private int universeSize;
		
		/**
		  * number of sets in the problem
		  */
		private int numOfSets;
		
		/** 
		  * This array contains, for each set, the number of items in the set that have not yet been covered
		  */
		private int[] currSetSize;
		
		/** 
		  * This array indicates which universe elements have already been covered
		  */
		private boolean[] elemCovered;
		
		/** 
		  * The number of elements that have already been covered
		  */
		private int numCovered;
		
		/** 
		  * A copy of the set cover problem, used to store the original problem, as it 
		  * will be changed over time (as elements are covered)
		  */
		private SetCoverProblem problemCopy;
		
		/**
		  * @param problem the problem to be solved
		  * @return a solution to the weighted set cover problem for the given input
		  */
		public Solution approxSetCover(SetCoverProblem problem) { 
			
			//asssume that no elements are covered
			BitSet covered = new BitSet();
			
			//assume that all sets are legal
			BitSet legalSets = new BitSet(problem.getNumberOfSets());
			legalSets.set(0, problem.getNumberOfSets());
			
			return approxSetCover(problem, covered, legalSets); 
		}
		
		/**
		  * This version of the algorithm is useful as a subprocedure in enumeration
		  * @param problem the problem to be solved
		  * @param covered a BitSet indicating which elements of the universe are assumed to already be covered
		  * @param legalSets a BitSet indicating which sets can be used in the solution 
		  * @return a solution to the weighted set cover problem for the given input
		  */
		public Solution approxSetCover(SetCoverProblem problem, BitSet covered, BitSet legalSets) {
		
			//log the fact that this function is run
			StatisticsLogger.incStat("ranGreedyMinSetCover", 1);
			
			//update universe size and number of sets according to the original problem
			universeSize = problem.getUniverseSize();
			numOfSets = problem.getNumberOfSets();
			
			//create an empty solution
			Solution solution = new Solution();
			
			//store a copy of the problem (so that we can make changes)
			problemCopy = (SetCoverProblem)problem.clone();
			
			//store a copy of the set of universe elements already covered
			BitSet coveredCopy = (BitSet)covered.clone();
			
			//find which elements can potentially be covered by first setting this to those already covered
			BitSet canStillBeCovered = new BitSet(universeSize);
			canStillBeCovered.or(coveredCopy);
			
			//add into the list of elements that can be covered all those appearing in legal sets
			for (int i = 0 ; i < numOfSets ; i++) {
				
				BitSet set = problemCopy.getSet(i);
				//clear all ilegal sets so that they cover no elements
				if (!legalSets.get(i)) 
					set.clear();
				else {
					//add all elements in the set that are not already in the covered copy to the list of those that can be covered
					set.andNot(coveredCopy);
					canStillBeCovered.or(set);
				}
			}
			
			//check if there is a solution to the problem, i.e., if there exists a set cover covering all elements
			if (canStillBeCovered.cardinality() != universeSize) return null;			
	
			//find a set cover using a greedy strategy
			while (coveredCopy.cardinality() < universeSize) {
				int bestSet = -1;
				double bestRatio = Double.MAX_VALUE;
				
				for (int i = 0 ; i < numOfSets ; i++) {
					BitSet set = problemCopy.getSet(i);
					//update the set so that it only contains elements not yet covered
					//this is important for cardinality to be computed correctly later only
					set.andNot(coveredCopy);
					
					if (set.cardinality() > 0) {
					
						double currRatio = ((double)problemCopy.getWeight(i))/set.cardinality();
						if (currRatio < bestRatio) {
							bestSet = i;
							bestRatio = currRatio;
						}
					}
					
				}
				// update solution with best set found
				solution.addSet(bestSet, problemCopy.getWeight(bestSet));
				coveredCopy.or(problemCopy.getSet(bestSet));
			}	
			
			return solution;
		}
		
		
		
		static public void main(String[] inp) throws FileNotFoundException {
			
			Solution s = (new GreedyMinSetCover()).approxSetCover(SetCoverProblemGenerator.generateSetCoverProblem(inp[0]));
			System.out.println(s);
		}
}

