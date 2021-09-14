package org.processmining.hybridilpminer.models.lp.miner.abstracts;

import java.util.Set;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.decorators.interfaces.LPDecorator;
import org.processmining.hybridilpminer.models.lp.miner.interfaces.LPMiner;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.VariableMapping;
import org.processmining.hybridilpminer.utils.trove.hashing.DoubleArrHashingStrategy;

import gnu.trove.set.hash.TCustomHashSet;

@Deprecated
public abstract class AbstractLPMiner<T extends LPDecorator, S extends VariableMapping, U> implements LPMiner {

	protected S varMap;
	protected T decorator;
	protected LPLogAbstraction<U> logAbstraction;
	protected long computationTime;
	protected long solveTimeCummulative = 0;

	protected final Set<double[]> solutions = new TCustomHashSet<>(new DoubleArrHashingStrategy());

	public AbstractLPMiner(S varMap, T decorator, LPLogAbstraction<U> logAbstraction) {
		this.varMap = varMap;
		this.decorator = decorator;
		this.logAbstraction = logAbstraction;
	}

	protected abstract void execute();

	public void run() {
		long start = System.currentTimeMillis();
		this.execute();
		this.computationTime = System.currentTimeMillis() - start;
	}

	@Override
	public Set<double[]> solutions() {
		return this.solutions;
	}

	@Override
	public long computationTimeMs() {
		return this.computationTime;
	}

	@Override
	public long decorationTimeMs() {
		return this.decorator.computationTimeMs();
	}

	@Override
	public long solveTimeMs() {
		return this.solveTimeCummulative;
	}
}
