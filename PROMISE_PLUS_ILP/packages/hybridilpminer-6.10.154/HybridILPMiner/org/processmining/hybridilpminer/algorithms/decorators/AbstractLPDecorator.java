package org.processmining.hybridilpminer.algorithms.decorators;

import org.processmining.lpengines.interfaces.LPEngine;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public abstract class AbstractLPDecorator implements LPDecorator {

	protected LPEngine engine;
	private TIntSet invalidActivityIndices = new TIntHashSet();
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

	public TIntSet getInvalidActivityIndices() {
		return invalidActivityIndices;
	}

	public long computationTimeMs() {
		return computationTime;
	}

}
