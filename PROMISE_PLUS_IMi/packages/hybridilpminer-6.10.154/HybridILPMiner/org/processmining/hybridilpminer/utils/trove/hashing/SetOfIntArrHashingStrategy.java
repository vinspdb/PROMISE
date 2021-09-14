package org.processmining.hybridilpminer.utils.trove.hashing;

import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;

import java.util.Set;

public class SetOfIntArrHashingStrategy implements HashingStrategy<Set<int[]>> {

	private static final long serialVersionUID = 8914205257284409400L;
	HashingStrategy<int[]> intArrStrat = new IntArrHashingStrategy();

	public int computeHashCode(Set<int[]> arg0) {
		int hash = 31 * arg0.size();
		for (int[] intArr : arg0) {
			hash += 31 * intArrStrat.computeHashCode(intArr);
		}
		return hash;
	}

	public boolean equals(Set<int[]> arg0, Set<int[]> arg1) {
		TCustomHashSet<int[]> troveArg0 = new TCustomHashSet<>(new IntArrHashingStrategy());
		troveArg0.addAll(arg0);
		TCustomHashSet<int[]> troveArg1 = new TCustomHashSet<>(new IntArrHashingStrategy());
		troveArg1.addAll(arg1);
		return troveArg0.equals(troveArg1);
	}

}
