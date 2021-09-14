package org.processmining.hybridilpminer.models.lp.variablemapping.factories;

import java.util.Set;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.implementations.HybridVariableMappingImpl;
import org.processmining.hybridilpminer.models.lp.variablemapping.implementations.IntArrSlackBasedFilterHybridVariableMapping;
import org.processmining.hybridilpminer.models.lp.variablemapping.implementations.SlackBasedFilterHybridVariableMappingImpl;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.SlackBasedFilterHybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

public class VariableMappingFactory {

	public static <T> HybridVariableMapping<T> createHybridVariableMapping(LPEngine.EngineType engineType,
			Set<T> singleVariables, Set<T> xyVariables) {
		return new HybridVariableMappingImpl<T>(engineType, singleVariables, xyVariables);
	}

	public static <T> SlackBasedFilterHybridVariableMapping<T, int[]> createIntArrSlackBasedFilterHybridVariableMapping(
			LPEngine.EngineType engineType, Set<T> singleVariables, Set<T> dualVariables,
			LPLogAbstraction<?> logAbstraction) {
		return new IntArrSlackBasedFilterHybridVariableMapping<>(engineType, singleVariables, dualVariables,
				logAbstraction);

	}

	public static <T, S> SlackBasedFilterHybridVariableMapping<T, S> createSlackBasedFilterHybridVariableMapping(
			LPEngine.EngineType engineType, Set<T> singleVariables, Set<T> dualVariables) {
		return new SlackBasedFilterHybridVariableMappingImpl<>(engineType, singleVariables, dualVariables);
	}
}
