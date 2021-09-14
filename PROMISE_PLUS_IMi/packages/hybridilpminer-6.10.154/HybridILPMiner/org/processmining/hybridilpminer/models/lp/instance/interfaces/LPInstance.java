package org.processmining.hybridilpminer.models.lp.instance.interfaces;

import org.processmining.framework.util.Pair;

/**
 * An LPInstance yields a possible result in the form of a place.
 *
 * @param <T>
 *            transition type
 */
public interface LPInstance extends Runnable {

	public Pair<double[], Double> solutionWithObjectiveValue();

	public double[] solution();

	public long getSolveTime();
}
