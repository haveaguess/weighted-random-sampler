package com.weighted.sample;

/**
 * Programming test
 * 
 * Notes:
 * 
 * In this implementation we don't check that probabilities add up to 1 
 * or that each probability is in [0,1] range.
 * 
 * It's easy to do but not clear this is desired in this implementation.
 * 
 * TODO: It's possible to feed in the same int as a sample outcome twice - ok?
 * 
 * Initial idea: 
 * 
 * Work out GCD of all probabilities to get common "buckets" and sample from a uniform distribution across that
 * to reduce problem to a random number problem. Could benefit from efficient Math libraries, potentially BigInteger.gcd() (which uses Euclid's Algorithm)
 * 
 * Since efficiency is not being chased currently that might be an early optimisation
 * 
 * Relevant online material :
 * 
 * <a href="http://en.wikipedia.org/wiki/Alias_method">Alias Method</a>
 * <a href="http://www.keithschwarz.com/darts-dice-coins/">Darts, Dice, and Coins: Sampling from a Discrete Distribution</a>
 * <a href="https://www.facebook.com/note.php?note_id=323786247654246">Smalltalk implementation of Vose' Alias Algorithm</a>
 * <a href="http://stackoverflow.com/questions/3679694/a-weighted-version-of-random-choice">Stackoverflow Weighted Random Choice</a>
 * <a href="http://stackoverflow.com/questions/17250568/randomly-choosing-from-a-list-with-weighted-probabilities">Stackoverflow Weighted Probabilities (inverse transform sampling)</a>
 * <a href="http://stackoverflow.com/questions/17912005/quick-way-of-selecting-a-random-item-from-a-list-with-varying-probabilities-ba?lq=1">Stackoverflow Jon Skeet's Neat/Slow Solution</a>
 * 
 *
 * TODO: Range checks / edge cases
 * 
 * 
 * @author Ben Ritchie
 */
public class RandomGen {
	// members are final because immutability is a good thing..
	
	// Values that may be returned by nextNum()
	private final int[] randomNums;
	
	// Probability of the occurence of randomNums
	private final float[] probabilities;
	
	// random number generator implementation
	private final RandomNumberGenerator generator;
	
	// the default precision used if none passed in 
	private static final float DEFAULT_PRECISION = 0.0001f; 
	
	/**
	 * This constructor uses a default precision of four decimal places
	 */
	public RandomGen(RandomNumberGenerator generator, int[] randomNums, float[] probabilities) {
		this(generator, randomNums, probabilities, DEFAULT_PRECISION);
	}
	
	/**
	 * 
	 * @param generator a random number generator instance
	 * @param randomNums the numbers to randomly sample from  
	 * @param probabilities array of sample outcome probabilities - adding up to 1
	 * @param precision the largest difference acceptable when checking for total probability of 1
	 */
	public RandomGen(RandomNumberGenerator generator, int[] randomNums, float[] probabilities, float precision) {
		this.generator = generator;
		this.randomNums = randomNums;
		this.probabilities = probabilities;
		
		// Sanity Checks
		if (randomNums.length == 0 || probabilities.length == 0) {
			throw new RuntimeException("Need at least one number/probability");
		} else if (randomNums.length != probabilities.length)  {
			throw new RuntimeException("Need matching length on number and probability arrays ");
		}
		
		// Compute the total weight of all items together
		float totalProbability = 0.0f;
		for (float probability : probabilities)
		{
			// TODO:Should use precision on these checks?
			if (probability < 0 || probability > 1) {
				throw new RuntimeException("Probability outside of range: "+ probability );
			}
			
			totalProbability += probability;
		}
		
		if (Math.abs(1 - totalProbability) > precision) {
			throw new RuntimeException("Probabilities add up to "+totalProbability+" needs to be 1 to within precision of " + precision);
		}
	}
	
	

	/**
	 * Returns one of the randomNums. When this method is called multiple times
	 * over a long period, it should return the numbers roughly with the
	 * initialized probabilities.
	 * 
	 * inspired by implementation at : 
	 * 
	 * http://stackoverflow.com/questions/6737283/weighted-randomness-in-java
	 */
	public int nextNum() {
		
		// Now choose a random item
		int randomIndex = -1;
		float random = generator.random();
		for (int i = 0; i < randomNums.length; ++i)
		{
		    random -= probabilities[i];
		    if (random <= 0.0f)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		int randomEntry = randomNums[randomIndex];
		return randomEntry;
	}
}

