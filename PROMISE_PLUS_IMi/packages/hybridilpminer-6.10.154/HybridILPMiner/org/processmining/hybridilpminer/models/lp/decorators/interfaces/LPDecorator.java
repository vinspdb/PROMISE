package org.processmining.hybridilpminer.models.lp.decorators.interfaces;

import org.processmining.lpengines.interfaces.LPEngine;

@Deprecated //relocated:org.processmining.hybridilpminer.algorithms.decorators
public interface LPDecorator extends Runnable {

	public LPEngine engine();

	//TODO: Remove this uglyness.
	/**
	 * Due to in-ilp filtering (Prefix Automaton based) a column might get of
	 * the form (0,0,....,0). In this case, it is not allowed to use this column
	 * as it has no more meaning.
	 * 
	 * @return
	 */
	@Deprecated
	public int[] inivalidEventIndices();

	public long computationTimeMs();
}
