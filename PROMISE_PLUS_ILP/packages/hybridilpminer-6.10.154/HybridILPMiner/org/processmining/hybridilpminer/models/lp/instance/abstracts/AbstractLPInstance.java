package org.processmining.hybridilpminer.models.lp.instance.abstracts;

import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.lp.instance.interfaces.LPInstance;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.VariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

public abstract class AbstractLPInstance<T extends VariableMapping> implements LPInstance {

	protected LPEngine engine;
	protected Pair<double[], Double> result;
	protected long solveTime;
	protected T varMap;

	public AbstractLPInstance(LPEngine engine, T varMap) {
		this.engine = engine;
		this.varMap = varMap;
	}

	public long getSolveTime() {
		return this.solveTime;
	}

	protected abstract void instantiateEngine();

	protected abstract void cleanEngine();

	public Pair<double[], Double> solutionWithObjectiveValue() {
		return this.result;
	}

	public double[] solution() {
		return result.getFirst();
	}

	@Override
	public void run() {
		this.instantiateEngine();
		long start = System.currentTimeMillis();
		this.result = this.engine.solveAndValueNative();
		this.solveTime = System.currentTimeMillis() - start;
		this.cleanEngine();
	}

}
