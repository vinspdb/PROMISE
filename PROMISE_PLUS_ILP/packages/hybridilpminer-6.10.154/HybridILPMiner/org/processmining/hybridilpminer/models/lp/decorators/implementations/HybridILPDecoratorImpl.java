package org.processmining.hybridilpminer.models.lp.decorators.implementations;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.configuration.interfaces.LPMinerConfiguration;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine.VariableType;

@Deprecated //relocated:org.processmining.hybridilpminer.algorithms.decorators
public class HybridILPDecoratorImpl<T extends HybridVariableMapping<Integer>> extends HybridLPDecoratorImpl<T> {

	public HybridILPDecoratorImpl(T varMap, LPMinerConfiguration configuration, LPLogAbstraction<?> logAbstraction) {
		super(varMap, configuration, logAbstraction);
	}

	protected void setupVariables() {
		engine.setType(varMap.getMarkingVariableLPIndex(), VariableType.BOOLEAN);
		for (int i : varMap.getSingleVariableIndices()) {
			engine.setType(i, VariableType.INTEGER);
			engine.setLowerBound(i, -1);
			engine.setUpperBound(i, 1);
		}
		for (int i : varMap.getXVariableIndices()) {
			engine.setType(i, VariableType.BOOLEAN);
		}
		for (int i : varMap.getYVariableIndices()) {
			engine.setType(i, VariableType.BOOLEAN);
		}
	}
}
