package enumerateWeightedSetCovers;

import java.io.*;
import java.util.*;

public class SetCoverProblemGenerator {
	
	private static SetCoverProblem generateDblpProblem(String fileName) throws FileNotFoundException{

		Scanner scanner = new Scanner(new File(fileName));
		int universeSize = scanner.nextInt();
		int numOfSets = scanner.nextInt();	
		int[] weights = new int[numOfSets];
		ArrayList<BitSet> setContents = new ArrayList<BitSet>(numOfSets);
				
		for (int i=0 ; i < numOfSets ; i++) {
			String line = scanner.nextLine();
			weights[i] = scanner.nextInt();
					
			BitSet newSet = new BitSet(universeSize);
					
			Scanner lineScanner = new Scanner(line);
			int currSetSize = 0;
			while (lineScanner.hasNext()) {
				int element = lineScanner.nextInt() - 1;
				newSet.set(element);
					
			}
			
			setContents.add(i, newSet);
		}
		return new SetCoverProblem(numOfSets, universeSize, setContents, weights);
	}
		
		
	private static SetCoverProblem generateFisProblem(String fileName) throws FileNotFoundException{
		
		Scanner scanner = new Scanner(new File(fileName));	
		int numOfSets = scanner.nextInt();
		int universeSize = scanner.nextInt();
		int[] weights = new int[numOfSets];
		ArrayList<BitSet> setContents = new ArrayList<BitSet>(numOfSets);
			
		Random weightsGenerator = new Random();
					
		for (int i=0 ; i < numOfSets ; i++) {
			String line = scanner.nextLine();
			weights[i] = 1;
			BitSet newSet = new BitSet(universeSize);
					
			Scanner lineScanner = new Scanner(line);
			int currSetSize = 0;
			while (lineScanner.hasNext()) {
				int element = lineScanner.nextInt() - 1;
				newSet.set(element);
					
			}
			setContents.add(i, newSet);
		}
		return new SetCoverProblem(numOfSets, universeSize, setContents, weights);	
	}
		
	private static SetCoverProblem generateDefaultTypeProblem(String fileName) throws 	FileNotFoundException {
			
		Scanner scanner = new Scanner(new File(fileName));
		int universeSize = scanner.nextInt();
		int numOfSets = scanner.nextInt();
		int[] weights = new int[numOfSets];
		ArrayList<BitSet> setContents = new ArrayList<BitSet>(numOfSets);
			
		for (int i=0 ; i < numOfSets ; i++) {
			weights[i] = scanner.nextInt();
			BitSet newSet = new BitSet(universeSize);
			setContents.add(i, newSet);
		}
			
		for (int i=0 ; i < universeSize ; i++) {
			int numOfSetsForElem = scanner.nextInt();
			for (int j = 0 ; j < numOfSetsForElem ; j++) {
					
				int set = scanner.nextInt() - 1; //reduce set number by 1 to fit indices
				setContents.get(set).set(i);
					
			}
		}
			
		return new SetCoverProblem(numOfSets, universeSize, setContents, weights);	
	}
			
	private static SetCoverProblem generateRailProblem(String fileName) throws FileNotFoundException{
	
		Scanner scanner = new Scanner(new File(fileName));
		int universeSize = scanner.nextInt();
		int numOfSets = scanner.nextInt();	
		int[] weights = new int[numOfSets];
		ArrayList<BitSet> setContents = new ArrayList<BitSet>(numOfSets);
			
		for (int i=0 ; i < numOfSets ; i++) {
					
			weights[i] = scanner.nextInt();
			int setSize = scanner.nextInt();
			BitSet newSet = new BitSet(universeSize);
									
			for (int j=0 ; j < setSize ; j++) {
				int element = scanner.nextInt() - 1;
				newSet.set(element);
			}
			setContents.add(i, newSet);
		}
		return new SetCoverProblem(numOfSets, universeSize, setContents, weights);	
	}
	
	public static SetCoverProblem generateSetCoverProblem(String fileName) throws FileNotFoundException {
		
		if (fileName.contains("rail")) return generateRailProblem(fileName);
		else if (fileName.contains("dblp")) return generateDblpProblem(fileName);
		else if (fileName.contains("accidents")) return generateFisProblem(fileName);
		else return generateDefaultTypeProblem(fileName);
	}
		
	
}