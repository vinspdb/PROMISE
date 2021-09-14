package org.processmining.hybridilpminer.algorithms.decorators;

import org.processmining.lpengines.interfaces.LPEngine;

import gnu.trove.set.TIntSet;

public interface LPDecorator extends Runnable {

	public LPEngine engine();

	/**
	 * Due to in-ilp filtering (Prefix Automaton based) a column might get of
	 * the form (0,0,....,0). In this case, it is not allowed to use this column
	 * as it has no more meaning.
	 * 
	 * note: this is, sadly, somewhat hardcoded as it assumes we use some
	 * indexing structure of the activities in the log.
	 * 
	 * @return
	 */
	public TIntSet getInvalidActivityIndices();

	public long computationTimeMs();
}
