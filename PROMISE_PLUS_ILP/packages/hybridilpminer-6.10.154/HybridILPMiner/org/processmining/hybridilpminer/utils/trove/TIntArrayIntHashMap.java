package org.processmining.hybridilpminer.utils.trove;

import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;

import java.util.Arrays;

public class TIntArrayIntHashMap extends TObjectIntHashMap<int[]> {

	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TIntArrayIntHashMap() {
		super();
	}

	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with a prime
	 * capacity equal to or greater than <tt>initialCapacity</tt> and with the
	 * default load factor.
	 *
	 * @param initialCapacity
	 *            an <code>int</code> value
	 */
	public TIntArrayIntHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with a prime
	 * capacity equal to or greater than <tt>initialCapacity</tt> and with the
	 * specified load factor.
	 *
	 * @param initialCapacity
	 *            an <code>int</code> value
	 * @param loadFactor
	 *            a <code>float</code> value
	 */
	public TIntArrayIntHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with a prime value
	 * at or near the specified capacity and load factor.
	 *
	 * @param initialCapacity
	 *            used to find a prime capacity for the table.
	 * @param loadFactor
	 *            used to calculate the threshold over which rehashing takes
	 *            place.
	 * @param noEntryValue
	 *            the value used to represent null.
	 */
	public TIntArrayIntHashMap(int initialCapacity, float loadFactor, int noEntryValue) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new <code>TObjectIntHashMap</code> that contains the entries in
	 * the map passed to it.
	 *
	 * @param map
	 *            the <tt>TObjectIntMap</tt> to be copied.
	 */
	public TIntArrayIntHashMap(TIntArrayIntHashMap map) {
		super(map);
	}

	@Override
	protected boolean equals(Object notnull, Object two) {
		if (two == null || two == REMOVED)
			return false;

		return Arrays.equals((int[]) notnull, (int[]) two);
	}

	@Override
	protected int hash(Object notnull) {
		return Arrays.hashCode((int[]) notnull);
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder("{");
		forEachEntry(new TObjectIntProcedure<int[]>() {
			private boolean first = true;

			public boolean execute(int[] key, int value) {
				if (first)
					first = false;
				else
					buf.append(",");

				buf.append(Arrays.toString(key)).append("=").append(value);
				return true;
			}
		});
		buf.append("}");
		return buf.toString();
	}

}
