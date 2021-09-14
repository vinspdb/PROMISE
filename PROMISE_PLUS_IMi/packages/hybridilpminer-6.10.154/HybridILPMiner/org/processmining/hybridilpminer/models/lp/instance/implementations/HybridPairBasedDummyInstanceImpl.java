package org.processmining.hybridilpminer.models.lp.instance.implementations;

import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

public class HybridPairBasedDummyInstanceImpl<T extends HybridVariableMapping<S>, S> extends
		HybridPairBasedInstanceImpl<T, S> {

	private final String targetFile;

	public HybridPairBasedDummyInstanceImpl(T varMap, LPEngine engine, Pair<S, S> pair, final String targetFile) {
		super(varMap, engine, pair);
		this.targetFile = targetFile;
	}

	@Override
	public void run() {
		instantiateEngine();
		engine.writeToFile(targetFile);
		cleanEngine();
	}

}
