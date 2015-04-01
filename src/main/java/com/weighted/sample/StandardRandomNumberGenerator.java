package com.weighted.sample;

import java.util.Random;

/**
 * 
 * @author Ben Ritchie
 *
 */
public class StandardRandomNumberGenerator implements RandomNumberGenerator {
	public float random() {
		Random r = new Random();
		return r.nextFloat();
	}
}