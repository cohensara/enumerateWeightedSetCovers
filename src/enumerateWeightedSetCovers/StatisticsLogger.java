package enumerateWeightedSetCovers;

import java.util.Hashtable;
import java.util.Vector;

public class StatisticsLogger {
	
	static Hashtable<String,Integer> statistics = new Hashtable<String, Integer>();
	static Vector<Long> intervalTimes = new Vector<Long>();
	static Vector<Integer> intervalWeights = new Vector<Integer>();
	
	
	/**
	  * Add the amount of time that an interval ran, as well as the weight of the best solution in the interval
	  * @param time the amount of time from the begining of the execution until the end of the interval
	  * @param weight the lowest weight solution found within the interval
	  */
	public static void addInterval(long time, int weight) {
		intervalTimes.add(time);
		intervalWeights.add(weight);
	}
	
	public static long getIntervalTime(int i){
		return intervalTimes.get(i);
	}
		
	public static int getIntervalWeight(int i){
		return intervalWeights.get(i);
	}
	
	public static int numberOfIntervals() {
		return intervalTimes.size();
	}
		
	
	/**
	  * increase given statistic with given value
	  * @param key statistic key
	  * @param value statistic value
	  */
	public static void incStat(String key, int value) {
		
		Integer pastVal = statistics.get(key);
		if (pastVal == null) 
			statistics.put(key, value);
		else 
			statistics.put(key, pastVal + value);
	}
	
	/**
	  * change given statistic to have given value
  	  * @param key statistic key
	  * @param value statistic value
	  */
	public static void changeStat(String key, int value) {
		 
		statistics.put(key, value);
	}


	/** 
	  * update statistic to new value, if it is lower
  	  * @param key statistic key
	  * @param value statistic value	  
	  * @return true if an update was made
	  */
	public static boolean minStat(String key, int value) {
		
		Integer pastVal = statistics.get(key);
		if (pastVal == null) { 
			statistics.put(key, value);
			return true;
		}
		else {
			if (pastVal > value) {
				statistics.put(key, value);
				return true;
			}
			return false;
		}
	}
	
	
	public static int getStat(String key) {
	
		Integer val = statistics.get(key);
		
		if (val == null) System.out.println("Missing value for " + key);
		return val;
	}
	
	public static void removeStat(String key) {
		
		statistics.remove(key);
	}
	
	public static void removeAll() {
		
		statistics.clear();
		intervalTimes.clear();
		intervalWeights.clear();
	}
}