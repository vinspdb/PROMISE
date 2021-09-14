package org.processmining.hybridilpminer.models.thr;

import org.processmining.framework.util.collection.MultiSet;

/**
 * Simple data structure that stores a sequence as a
 * "theory of regions constraint". The prefix is a multiset of activities. We
 * keep track of the "final" activity as well. Given some List over T, we encode
 * the data structure as an int array. Assume we have the List: [A,B,C] and the
 * constraint ([a^2,b],c) this will be encoded into the array [2,1,0,2], i.e. 2
 * occurrences of A (index 0), 1 occurrence of B, zero occurrences of C and C
 * (index 2) is the last activity
 * 
 * @author svzelst
 *
 * @param <T>
 */
public class TheoryOfRegionsConstraintImpl<T> {

	private final MultiSet<T> prefix;
	private final T last;

	public TheoryOfRegionsConstraintImpl(MultiSet<T> prefix, T last) {
		this.prefix = prefix;
		this.last = last;
	}

	public MultiSet<T> getPrefix() {
		return prefix;
	}

	public T getLast() {
		return last;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (o instanceof TheoryOfRegionsConstraintImpl) {
			TheoryOfRegionsConstraintImpl constr = (TheoryOfRegionsConstraintImpl) o;
			if (constr.getPrefix().equals(getPrefix())) {
				if (constr.getLast() != null && getLast() != null) {
					return constr.getLast().equals(getLast());
				} else
					return constr.getLast() == null && getLast() == null;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 31 * getPrefix().hashCode();
		return getLast() == null ? hash + 1 : (37 * getLast().hashCode()) + hash;
	}

	@Override
	public String toString() {
		String str = "( " + getPrefix().toString() + ", ";
		str += getLast() == null ? "" : getLast().toString();
		return str + ")";
	}

}
