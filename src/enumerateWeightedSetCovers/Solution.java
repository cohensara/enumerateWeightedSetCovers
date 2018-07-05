package enumerateWeightedSetCovers;

import java.util.BitSet;


/**
  * This class represents a solution to a weighted set cover problem.
  * Internally, a solution is implemented using a BitSet, which indicates which sets of the problem are part of the solution.
  * In addition, we store the weight of the solution
  * 
  * Note that at different points in the runtime, this object may not be an actual set cover, but is is used to represent the solution being derived
  */
public class Solution implements Cloneable{
	
	/**
	  * The sets in the solution
	  */
	private BitSet sets = new BitSet(); 
	
	/** 
	  * The weight of the solution
	  */
	private int weight = 0;
	
	/** 
	  * Adds a set to the solution
	  * @param i the index of a set to be added to the solution
	  * @param weight the weight of the set to be added to the solution
	  */
	public void addSet(int i, int weight){
		sets.set(i);
		this.weight += weight;
	}
	
	/** 
	  * Removes a set from the solution
	  * @param i the index of a set to be removed from the solution
	  * @param weight the weight of the set to be removed from the solution
	  */
	public void removeSet(int i, int weight){
		sets.set(i, false);
		this.weight -= weight;
	}

	/** 
	  * @return the solution
	  */
	public BitSet getSolution() {
		return sets;
	}
	
	/**
	  * @return the solution size, i.e., the number of sets in the solution
	  */
	public int cardinality() {
		
		return sets.cardinality();
	}

	public Solution clone() {
		
		return new Solution ((BitSet)sets.clone(), weight);
	}
	
	/** 
	  * creates a solution with the given sets and weight
	  * @param sets lit bits indicates which sets appear in the solution
	  * @param weight total weight of the solution
	  */
	public Solution(BitSet sets, int weight) {
		this.sets = sets;
		this.weight = weight;
	}
	
	/**
	  * @return the weight of the solution
	  */
	public int getWeight() {
		return weight;
	}
	
	/** 
	  * Constructs an empty solution
	  */
	public Solution(){
	}
	
	public String toString() {
		
		String out = "({";
		for (int i = sets.nextSetBit(0) ; i != -1 ; i = sets.nextSetBit(i+1)) {
			out += i + " ";
		}
		out+="}=" + weight + ")";
		return out;
	}
	
	
	
}