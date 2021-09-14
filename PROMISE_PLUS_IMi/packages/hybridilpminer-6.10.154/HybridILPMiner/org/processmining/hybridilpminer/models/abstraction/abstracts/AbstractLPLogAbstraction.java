package org.processmining.hybridilpminer.models.abstraction.abstracts;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.utils.trove.TIntArrayIntHashMap;
import org.processmining.hybridilpminer.utils.trove.hashing.IntArrHashingStrategy;

import com.google.common.primitives.Ints;

public abstract class AbstractLPLogAbstraction<EC> implements LPLogAbstraction<EC> {

	protected final TObjectIntMap<EC> alphabet = new TObjectIntHashMap<>();
	protected final TObjectIntMap<int[]> log = new TIntArrayIntHashMap();
	protected final TObjectIntMap<int[]> prefixClosure = new TIntArrayIntHashMap();

	// maps an abstraction corresponding to a *full* trace in the log to
	// a set of abstractions representing its prefix.
	protected final Map<int[], Set<Set<int[]>>> traceMap = new TCustomHashMap<int[], Set<Set<int[]>>>(
			new IntArrHashingStrategy());

	@Override
	public int cardinality(int[] w) {
		return prefixClosure.get(w);
	}

	@Override
	public int cardinality(List<EC> w) {
		return cardinality(encode(w));
	}

	@Override
	public EC decode(int i) {
		EC result = null;
		for (EC ec : this.alphabet.keySet()) {
			if (this.alphabet.get(ec) == i) {
				result = ec;
				break;
			}
		}
		return result;
	}

	@Override
	public Set<EC> decode(Set<Integer> is) {
		Set<EC> result = new HashSet<>();
		for (int i : is) {
			result.add(decode(i));
		}
		return result;
	}

	public Set<EC> domain() {
		return this.alphabet.keySet();
	}

	@Override
	public int[] encode(List<EC> l) {
		int[] abstraction = new int[Ints.max(this.alphabet.values()) + 2];
		if (l.size() > 0) {
			for (int i = 0; i < l.size() - 1; i++) {
				abstraction[this.alphabet.get(l.get(i))]++;
			}
			abstraction[abstraction.length - 1] = this.alphabet.get(l.get(l.size() - 1));
		} else {
			abstraction[abstraction.length - 1] = -1;
		}
		return abstraction;
	}

	@Override
	public Set<Integer> encode(Set<EC> events) {
		Set<Integer> result = new HashSet<>();
		for (EC e : events) {
			result.add(encode(e));
		}
		return result;
	}

	@Override
	public int encode(EC eventClass) {
		int result = -1;
		if (this.alphabet.containsKey(eventClass)) {
			result = this.alphabet.get(eventClass);
		}
		return result;
	}

	@Override
	public TObjectIntMap<int[]> eventLogAbstraction() {
		return this.log;
	}

	@Override
	public Map<int[], Set<Set<int[]>>> tracePrefixMapping() {
		return this.traceMap;
	}

	@Override
	public TObjectIntMap<int[]> prefixClosureAbstraction() {
		return this.prefixClosure;
	}

	@Override
	public int[] range() {
		return this.alphabet.values();
	}

}
