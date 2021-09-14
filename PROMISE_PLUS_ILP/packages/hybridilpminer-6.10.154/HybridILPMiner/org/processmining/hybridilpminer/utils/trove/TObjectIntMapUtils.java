package org.processmining.hybridilpminer.utils.trove;

import gnu.trove.map.TObjectIntMap;

import java.util.Collection;

public class TObjectIntMapUtils {

	public static <T> int sum(TObjectIntMap<T> map, Collection<T> keys) {
		int sum = 0;
		for (T key : keys) {
			sum += map.get(key);
		}
		return sum;
	}

}
