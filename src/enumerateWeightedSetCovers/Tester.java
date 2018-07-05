package enumerateWeightedSetCovers;

import java.io.*;
import java.util.Vector;

public class Tester {
	
	
	public static void main(String args[]) throws FileNotFoundException, IOException {
		
		if (args.length != 4) {
				System.out.println("Usage: java Tester <tests folder> <number of results> <nonredundant?> <output>");
				System.exit(-1);
		}
		
		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();

		PrintWriter br = new PrintWriter(new FileWriter(args[3] + ".csv"));
		
		br.println("Test Name, Max Num of Results, Only Nonredundant, Universe Size, Number of Sets," + 
					" Time, First Weight, Best Weight, When Found, Interval Times, Weights, ...., Number of Times Running Greedy");
		int intervalLen = 500;
		
		for (int i = 0; i < listOfFiles.length; i++) {
			
			if (listOfFiles[i].isFile()) {
				
				String testFile = listOfFiles[i].getName();
				StatisticsLogger.removeAll();
				StatisticsLogger.changeStat("interval", intervalLen);

				String fileName = args[0] + File.separator + testFile;
				SetCoverProblem problem = SetCoverProblemGenerator.generateSetCoverProblem(fileName);
				
				EnumerateSetCovers enumsc = new EnumerateSetCovers();
				
				br.print(testFile + ", " + Integer.parseInt(args[1]) + ", " + 
					Boolean.parseBoolean(args[2]) + ", " + 
					problem.getUniverseSize() + ", " + problem.getNumberOfSets() + ", ");
				System.out.println(testFile);
				
				long startTime = System.nanoTime();
				enumsc.enumerate(problem, Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]));
				long endTime = System.nanoTime();
				long duration = (endTime - startTime)/1000000;

				br.print(duration + ", " + StatisticsLogger.getStat("firstWeight") + ", " + StatisticsLogger.getStat("lowestWeight") + ", " + 
							StatisticsLogger.getStat("whenBestFound"));
				
				for (int j = 0 ; j < StatisticsLogger.numberOfIntervals() ; j++) {
					
					br.print(", " + ((j+1) * intervalLen) + ", " + StatisticsLogger.getIntervalTime(j) + ", " + 
						StatisticsLogger.getIntervalWeight(j));
				}
				br.print(", " + StatisticsLogger.getStat("ranGreedyMinSetCover"));
				br.println();
				br.flush();
				
			} 
		}
		
	}
	
}