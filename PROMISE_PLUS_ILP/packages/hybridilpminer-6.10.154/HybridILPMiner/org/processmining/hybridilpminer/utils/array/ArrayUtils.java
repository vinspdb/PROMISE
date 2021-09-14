package org.processmining.hybridilpminer.utils.array;

import java.util.Arrays;

public class ArrayUtils {

	public static <T> T[] append(T[] array, T elem) {
		T[] clone = Arrays.copyOf(array, array.length + 1);
		clone[array.length] = elem;
		return clone;
	}

	public static int[] append(int[] array, int elem) {
		int[] clone = Arrays.copyOf(array, array.length + 1);
		clone[array.length] = elem;
		return clone;
	}

	public static double[] round(double[] arg0) {
		double[] result = new double[arg0.length];
		for (int i = 0; i < arg0.length; i++) {
			result[i] = Math.round(arg0[i]);
		}
		return result;
	}

}
