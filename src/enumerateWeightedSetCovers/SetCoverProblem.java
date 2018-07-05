package enumerateWeightedSetCovers;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.BitSet;

/** 
  * The class SetCoverProblem is used to represent an instance of a weighted set cover problem. Internally, 
  * a weighted set cover problem is represented by an array of integer weights and an ArrayList of BitSets, each 
  * of which is the size of the universe. For a particular set, the members correspond to the bits with value 1
  */
public class SetCoverProblem implements Cloneable {
	
		/**
		  * the number of sets in the instance of the problem
		  */
		private int numOfSets; 
		
		/**
		  * the size of the universe
		  */
		private int universeSize; 
		
		/**
		  * the weights of the sets
		  */
		private static int[] weights; 
		
		/** 
		  * the sets themselves, where each member of the array list corresponds to a set
		  */
		private ArrayList<BitSet> setContents;
			
		/**
		  * @return the size of the universe
		  */
		public int getUniverseSize(){
			
			return universeSize;
		}
		
		/** 
		  * @return the number of sets
		  */
		public int getNumberOfSets() {
			
			return numOfSets;
		}

		/**
		  * @param setIndex the index of a set
		  * @return the weight of this set
		  */
		public int getWeight(int setIndex){
				
				return weights[setIndex];
		}
		
		/**
		  * @param setIndex the index of a set
		  * @return the set
		  */
		public BitSet getSet(int setIndex) {
			return setContents.get(setIndex);
		}
		
		
		public Object clone() {
			
			ArrayList<BitSet> setContentsCopy = new ArrayList<BitSet>();
			for (int i = 0 ; i < numOfSets ; i++) {
				setContentsCopy.add((BitSet)setContents.get(i).clone());
			}
			return new SetCoverProblem(numOfSets, universeSize, setContentsCopy);
		}
		
		/**
		  * @param prev a solution to the instance, when ignoring all universe elements indicated by covered
		  * @param covered a set of bits indicating which universe elements can be ignored by the problem
		  * @return a nonredundant solution contained in the input parameter. A solution is nonredundant when no set can be removed, while still retaining the set cover property
		  */ 
		public Solution makeNonRedundant(Solution prev, BitSet covered) {
		
			Solution newSol = (Solution)prev.clone();
		
			for (int i = prev.getSolution().nextSetBit(0); i != -1 ; i = prev.getSolution().nextSetBit(i+1)) {
			
				newSol.removeSet(i, getWeight(i));
				BitSet coveredBySolution = getCoveredBy(newSol.getSolution());
				coveredBySolution.or(covered);
				
				if (coveredBySolution.cardinality() != universeSize) {
					newSol.addSet(i, getWeight(i));
				}		
			}
			return newSol;
		}
	
		
		/** 
		  * @param sets a list of sets (indicated by the 1 bits of the BitSet)
		  * @return a BitSet of size of the universe which indicates which elements are covered by the sets of the input parameter
		  */
		public BitSet getCoveredBy(BitSet sets) {
		
			BitSet covered = new BitSet();
			for (int i = sets.nextSetBit(0) ; i != -1 ; i = sets.nextSetBit(i+1)) {
			
				BitSet elementsInSet = getSet(i);
				covered.or(elementsInSet);
			}
			return covered;
		}
		
		/**
		  * Creates an instance of a SetCoverProblem with the given parameters. Used in cloning an object.
		  * Note that the weights are not provided as we assume these are already available as a static data member
		  * 
		  * @param numOfSets the number of sets in the problem
		  * @param universeSize the size of the universe in the problem
		  * @param setContents the contents of the sets, as an array of sets of lit bits
		  */
		public SetCoverProblem(int numOfSets, int universeSize, ArrayList<BitSet> setContents) {
			
			this.numOfSets = numOfSets;
			this.universeSize = universeSize;
			this.setContents = setContents;
		}
		
		
		
		/**
		  * Creates an instance of a SetCoverProblem with the given parameters. 
  		  * @param numOfSets the number of sets in the problem
		  * @param universeSize the size of the universe in the problem
		  * @param setContents the contents of the sets, as an array of sets of lit bits
		  * @param weights the weights per set
		  */
		public SetCoverProblem(int numOfSets, int universeSize, ArrayList<BitSet> setContents, int[] weights) {
			
			this.numOfSets = numOfSets;
			this.universeSize = universeSize;
			this.setContents = setContents;
			this.weights = weights;
		}
		
}

