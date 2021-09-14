package org.processmining.hybridilpminer.templates.lp.constraints;

import gnu.trove.set.TIntSet;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;

public class SetOfObjectBasedVariablesConstraint {

	//TODO: MAKE GENERIC, I.E. <T EXTENDS....<S>, S>
	public static <T extends HybridVariableMapping<Integer>> double[] construct(T varMap, TIntSet vars,
			double[] target, boolean includeMarking) {
		if (includeMarking) {
			target[varMap.getMarkingVariableLPIndex()] = 1;
		}
		for (int i : vars.toArray()) {
			if (varMap.isSingleVariableObject(i))
				target[varMap.getSingleVariableIndexOf(i)] = 1;
			else {
				target[varMap.getXVariableIndexOf(i)] = 1;
				target[varMap.getYVariableIndexOf(i)] = 1;
			}
		}
		return target;
	}

}
