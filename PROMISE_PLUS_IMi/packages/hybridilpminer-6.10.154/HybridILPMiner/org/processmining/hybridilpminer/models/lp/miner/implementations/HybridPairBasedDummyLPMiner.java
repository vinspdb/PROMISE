package org.processmining.hybridilpminer.models.lp.miner.implementations;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.decorators.interfaces.LPDecorator;
import org.processmining.hybridilpminer.models.lp.instance.implementations.HybridPairBasedDummyInstanceImpl;
import org.processmining.hybridilpminer.models.lp.instance.interfaces.LPInstance;
import org.processmining.hybridilpminer.models.lp.miner.abstracts.AbstractHybridIntLPMiner;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;

@Deprecated
public class HybridPairBasedDummyLPMiner<T extends LPDecorator, S extends HybridVariableMapping<Integer>, U> extends
		AbstractHybridIntLPMiner<T, S, U> {

	protected final PluginContext context;
	private Object lock = new Object();
	protected Set<Pair<U, U>> pairs;
	protected boolean verbose = false;
	protected final String baseName;
	protected final File targetFolder;

	public HybridPairBasedDummyLPMiner(S varMap, T decorator, LPLogAbstraction<U> logAbstraction,
			Set<Pair<U, U>> pairs, PluginContext context, String baseName, final File target) {
		super(varMap, decorator, logAbstraction);
		this.pairs = pairs;
		this.context = context;
		this.verbose = this.context != null;
		this.baseName = baseName;
		this.targetFolder = target;
	}

	protected void execute() {
		this.decorator.run();

		// start solving
		ExecutorService executor = Executors.newFixedThreadPool(Math.max(1,
				Runtime.getRuntime().availableProcessors() - 2));
		if (verbose) {
			synchronized (lock) {
				this.context.log("Generating LP's...");
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
				String targetFile = targetFolder + "/" + baseName + "_" + pair.getFirst().toString() + "_"
						+ pair.getSecond().toString() + ".lp";
				if (verbose) {
					synchronized (lock) {
						context.log("Writing instance for causal relation (" + pair.getFirst().toString() + " -> "
								+ pair.getSecond().toString() + ") to " + targetFile);
					}
				}
				Pair<Integer, Integer> intPair = new Pair<>(logAbstraction.encode(pair.getFirst()),
						logAbstraction.encode(pair.getSecond()));
				LPInstance instance = null;
				try {
					instance = new HybridPairBasedDummyInstanceImpl<S, Integer>(varMap, decorator.engine().clone(),
							intPair, targetFile);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				instance.run();
			}
		};
	}
}
