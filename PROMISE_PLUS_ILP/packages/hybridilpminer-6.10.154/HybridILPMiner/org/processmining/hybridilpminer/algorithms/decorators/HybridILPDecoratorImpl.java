package org.processmining.hybridilpminer.algorithms.decorators;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.parameters.HybridILPMinerParametersImpl;
import org.processmining.lpengines.interfaces.LPEngine.VariableType;

public class HybridILPDecoratorImpl<T extends HybridVariableMapping<Integer>> extends HybridLPDecoratorImpl<T> {

	public HybridILPDecoratorImpl(T varMap, HybridILPMinerParametersImpl configuration,
			LPLogAbstraction<?> logAbstraction) {
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
