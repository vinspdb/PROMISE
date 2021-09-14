package org.processmining.hybridilpminer.algorithms.miners;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.algorithms.decorators.LPDecorator;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.instance.implementations.HybridPairBasedInstanceImpl;
import org.processmining.hybridilpminer.models.lp.instance.interfaces.LPInstance;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.utils.array.ArrayUtils;
import org.processmining.lpengines.interfaces.LPEngine;

public class HybridPairBasedLPMiner<T extends LPDecorator, S extends HybridVariableMapping<Integer>, U> extends
		AbstractHybridIntLPMiner<T, S, U> {

	protected final PluginContext context;
	private Object lock = new Object();
	protected Set<Pair<U, U>> pairs;
	protected boolean verbose = false;

	public HybridPairBasedLPMiner(S varMap, T decorator, LPLogAbstraction<U> logAbstraction, Set<Pair<U, U>> pairs,
			PluginContext context) {
		super(varMap, decorator, logAbstraction);
		this.pairs = pairs;
		this.context = context;
		this.verbose = this.context != null;
	}

	protected void execute() {
		this.decorator.run();

		// start solving
		ExecutorService executor = Executors.newFixedThreadPool(Math.max(1,
				Runtime.getRuntime().availableProcessors() - 2));
		if (verbose) {
			synchronized (lock) {
				this.context.log("Starting mining phase...");
				this.context.getProgress().setMinimum(0);
				this.context.getProgress().setMaximum(this.pairs.size());
				this.context.getProgress().setValue(this.context.getProgress().getMinimum());
			}
		}
		for (Pair<U, U> pair : this.pairs) {
			Runnable instance = this.pairBasedInstance(pair);
			executor.execute(instance);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// solving...
		}
//		findSourcePlaces();
	}

	protected Runnable pairBasedInstance(final Pair<U, U> pair) {
		return new Runnable() {
			@Override
			public void run() {
				if (verbose) {
					synchronized (lock) {
						context.log("Starting mining instance for causal relation (" + pair.getFirst().toString()
								+ " -> " + pair.getSecond().toString() + ")...");
					}
				}
				Pair<Integer, Integer> intPair = new Pair<>(logAbstraction.encode(pair.getFirst()),
						logAbstraction.encode(pair.getSecond()));
				LPInstance instance = null;
				try {
					LPEngine engine = decorator.engine().clone();
					instance = new HybridPairBasedInstanceImpl<S, Integer>(varMap, engine, intPair);
					instance.run();
					engine.destroy();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
				synchronized (lock) {
					solveTimeCummulative += instance.getSolveTime();
				}
				storeSolution(varMap.projectOnHybridVariableIndices(ArrayUtils.round(instance.solution())));
			}
		};
	}

	protected void storeSolution(double[] solution) {
		this.solutions.add(solution);
	}
}
