package org.processmining.hybridilpminer.utils.trove.hashing;

import gnu.trove.strategy.HashingStrategy;

import java.util.Arrays;

public class DoubleArrHashingStrategy implements HashingStrategy<double[]> {

	private static final long serialVersionUID = -3879405543122686698L;

	public int computeHashCode(double[] arg0) {
		return Arrays.hashCode(arg0);
	}

	public boolean equals(double[] arg0, double[] arg1) {
		return Arrays.equals(arg0, arg1);
	}

}
