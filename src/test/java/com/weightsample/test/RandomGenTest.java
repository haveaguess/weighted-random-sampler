package com.weightsample.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.weighted.sample.RandomGen;
import com.weighted.sample.RandomNumberGenerator;
import com.weighted.sample.StandardRandomNumberGenerator;

/**
 * Unit test for weighted probability number choice
 * 
 * @author Ben Ritchie
 */
public class RandomGenTest 
{
	/*
	 * useful test objects
	 */
	private final int[] noNums = {};
	private final float[] noProbabilities = {};
	
	// generator implementation that flips between head/tails (0.2/0.7) starting with heads
	private final RandomNumberGenerator alternatingFlipGenerator = new RandomNumberGenerator() {
		private boolean heads = false;
		public float random() {
			heads = !heads;
			if (heads) {
				return 0.2f;
			} else {
				return 0.7f;
			}
		}
	};

	private final RandomNumberGenerator standardGenerator = new StandardRandomNumberGenerator();

	/**
     * Check the basic sanity conditions
     */
	@Test
    public void testSanityChecksBothEmpty()
    {
		try {
			new RandomGen(standardGenerator, noNums, noProbabilities);
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}

        fail("Should throw exception when empty array passed in");
    }

	@Test
    public void testSanityChecksEmptyNumber()
    {
		try {
			new RandomGen(standardGenerator, noNums, new float[] { 3 });
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}

        fail("Should throw exception when empty array passed in");
    }

	@Test
    public void testSanityChecksEmptyProb()
    {
		try {
			new RandomGen(standardGenerator, new int[] { 3 }, noProbabilities);
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}

        fail("Should throw exception when empty array passed in");
    }

	@Test
    public void testSanityChecksArrayMismatch()
    {
		try {
			new RandomGen(standardGenerator, new int[] { 3 }, new float[] { 3, 4 });
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}

        fail("Should throw exception when array lengths don't match");
    }

	@Test
    public void testOversizedProbability()
    {
		try {
			new RandomGen(standardGenerator, new int[] { 3 }, new float[] { 4 });
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}
		
        fail("Should throw exception when any one probability is outside range [0,1]");
    }
	
	@Test
    public void testNegativeProbability()
    {
		try {
			new RandomGen(standardGenerator, new int[] { 3 }, new float[] { -1 });
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}
		
        fail("Should throw exception when any one probability is outside range [0,1]");
    }
	
	@Test
    public void testTotalProbability()
    {
		// even though probabilities don't add up to 1 it's ok because of reduced precision
		new RandomGen(standardGenerator, new int[] { 3, 5 }, new float[] { 0.5f, 0.45f }, 0.3f);
		
		try {
			// using default precision this should blow up
			new RandomGen(standardGenerator, new int[] { 3, 5 }, new float[] { 0.5f, 0.45f });
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}
		
        fail("Should throw exception when probabilities dont add up to 1");
    }
	

	/**
	 * Check basics
	 */
	@Test
    public void testBasicNumber()
    {
		RandomGen r = new RandomGen(standardGenerator, new int[] { 5 }, new float[] { 1 });
		// give it a few runs to make sure 
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
    }
	
	
	@Test
    public void testZeroProbability()
    {
		RandomGen r = new RandomGen(standardGenerator, new int[] { 5,6,7 }, new float[] { 1,0,0 });
		// give it a few runs to make sure 
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		 r = new RandomGen(standardGenerator, new int[] { 5,6,7 }, new float[] { 0,1,0 });
		// give it a few runs to make sure 
		assertEquals(6, r.nextNum());
		assertEquals(6, r.nextNum());
		assertEquals(6, r.nextNum());
		assertEquals(6, r.nextNum());
		 r = new RandomGen(standardGenerator, new int[] { 5,6,7 }, new float[] { 0,0,1 });
		// give it a few runs to make sure 
		assertEquals(7, r.nextNum());
		assertEquals(7, r.nextNum());
		assertEquals(7, r.nextNum());
		assertEquals(7, r.nextNum());
    }

	@Test
    public void testCoinToss()
    {
		RandomGen r = new RandomGen(alternatingFlipGenerator, new int[] { 5,6 }, new float[] { 0.5f, 0.5f });
		// give it a few runs to make sure 
		assertEquals(5, r.nextNum());
		assertEquals(6, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(6, r.nextNum());
		assertEquals(5, r.nextNum());
		assertEquals(6, r.nextNum());
    }
	

	@Test
    public void testBiasCoinToss()
    {
		RandomGen r = new RandomGen(alternatingFlipGenerator, new int[] { 5,6 }, new float[] { 0.8f, 0.2f });
		// give it a few runs to make sure 
		assertEquals(5, r.nextNum());
		assertEquals(5, r.nextNum());
		
		 r = new RandomGen(alternatingFlipGenerator, new int[] { 5,6 }, new float[] { 0.1f, 0.9f });
		// give it a few runs to make sure 
		assertEquals(6, r.nextNum());
		assertEquals(6, r.nextNum());
		assertEquals(6, r.nextNum());
    }
	
	/*
	 * Generally to support rapid refactoring and continuous deployment these types of exhaustive/probablistic 
	 * tests are best kept out of the unit test suite and best kept in a seperate integration testing suite 
	 */
	@Test
    public void testExhaustiveTest()
    {
		int numberOfSamples = 10000000;
		float precision = 0.001f;
		int[] samplePossibilities = new int[] { -1, 0, 1, 2, 3 };
		float[] sampleProbabilities = new float[] { 0.01f, 0.3f, 0.58f, 0.1f, 0.01f };
		
		Map<Integer, Float> sampleProbabilityMap = new HashMap<Integer,Float>();
		
		for (int i=0; i<samplePossibilities.length; i++) {
			sampleProbabilityMap.put(samplePossibilities[i],sampleProbabilities[i]);
		}
		
		RandomGen r = new RandomGen(standardGenerator, samplePossibilities, sampleProbabilities);
		
		// keep count of how many of each int we sample
		Map<Integer, Integer> sampleHistory = new HashMap<Integer,Integer>();
		
		// sample
		for (int i=0; i<numberOfSamples; i++) {
			int sample = r.nextNum();
			Integer currentCount = sampleHistory.get(sample);
			if (currentCount == null) {
				currentCount = 0;
			} else {
				currentCount++;
			}
			
			sampleHistory.put(sample, currentCount); 
		}
		
		for (int possibility : samplePossibilities) {
			int count = sampleHistory.get(possibility);
			float sampledProbability = (float)count / numberOfSamples;
			float expectedProbability = sampleProbabilityMap.get(possibility);
			if (sampledProbability==0) {
				System.out.println("");
			}
			assertEquals(expectedProbability, sampledProbability, precision);
		}
    }
	
}

