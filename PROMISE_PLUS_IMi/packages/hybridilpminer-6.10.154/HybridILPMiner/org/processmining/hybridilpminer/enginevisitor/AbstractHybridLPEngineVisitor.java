package org.processmining.hybridilpminer.enginevisitor;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;

public abstract class AbstractHybridLPEngineVisitor<T extends HybridVariableMapping<?>>
		implements HybridLPEngineVisitor<T> {

	private final T mapping;

	public AbstractHybridLPEngineVisitor(final T mapping) {
		this.mapping = mapping;
	}

	public T getVariableMapping() {
		return mapping;
	}

}
