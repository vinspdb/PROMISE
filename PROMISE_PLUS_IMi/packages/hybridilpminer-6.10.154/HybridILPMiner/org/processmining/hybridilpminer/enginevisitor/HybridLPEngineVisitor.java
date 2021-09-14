package org.processmining.hybridilpminer.enginevisitor;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngineVisitor;

public interface HybridLPEngineVisitor<T extends HybridVariableMapping<?>> extends LPEngineVisitor {

	T getVariableMapping();

}
