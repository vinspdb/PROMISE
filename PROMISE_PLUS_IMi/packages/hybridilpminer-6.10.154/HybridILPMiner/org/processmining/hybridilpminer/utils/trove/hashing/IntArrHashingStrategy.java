package org.processmining.hybridilpminer.utils.trove.hashing;

import gnu.trove.strategy.HashingStrategy;

import java.util.Arrays;

public class IntArrHashingStrategy implements HashingStrategy<int[]> {

	private static final long serialVersionUID = -5385986594820866945L;

	public int computeHashCode(int[] arg0) {
		return Arrays.hashCode(arg0);
	}

	public boolean equals(int[] arg0, int[] arg1) {
		return Arrays.equals(arg0, arg1);
	}

}
