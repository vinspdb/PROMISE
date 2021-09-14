package org.processmining.hybridilpminer.models.lp.decorators.abstracts;

import org.processmining.hybridilpminer.models.lp.decorators.interfaces.LPDecorator;
import org.processmining.lpengines.interfaces.LPEngine;

@Deprecated //relocated:org.processmining.hybridilpminer.algorithms.decorators
public abstract class AbstractLPDecorator implements LPDecorator {

	protected LPEngine engine;
	@Deprecated
	protected int[] invalidEventIndices = new int[0];
	protected long computationTime = 0;

	protected abstract LPEngine setupEngine();

	@Override
	public LPEngine engine() {
		return this.engine;
	}

	public void run() {
		long start = System.currentTimeMillis();
		engine = setupEngine();
		setupVariables();
		setupConstraints();
		setupObjective();
		computationTime = System.currentTimeMillis() - start;
	}

	protected abstract void setupVariables();

	protected abstract void setupConstraints();

	protected abstract void setupObjective();

	@Deprecated
	public int[] inivalidEventIndices() {
		return invalidEventIndices;
	}

	public long computationTimeMs() {
		return computationTime;
	}

}
