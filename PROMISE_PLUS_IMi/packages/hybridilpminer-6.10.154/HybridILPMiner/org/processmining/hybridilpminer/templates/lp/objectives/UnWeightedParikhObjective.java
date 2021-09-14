package org.processmining.hybridilpminer.templates.lp.objectives;

import java.util.Set;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;

public class UnWeightedParikhObjective {

	public static <T extends HybridVariableMapping<Integer>> double[] construct(T varMap, Set<int[]> abstractions,
			double[] target) {
		for (int[] abstraction : abstractions) {
			target = construct(varMap, abstraction, target);
		}
		return target;
	}

	public static <T extends HybridVariableMapping<Integer>> double[] construct(T varMap, int[] abstraction,
			double[] target) {
		target[varMap.getMarkingVariableLPIndex()] += 1;
		for (int i = 0; i < abstraction.length - 1; i++) {
			if (abstraction[i] > 0) {
				target = updateTargetByIndex(varMap, i, abstraction[i], target);
			}
		}
		if (abstraction[abstraction.length - 1] >= 0) {
			target = updateTargetByIndex(varMap, abstraction[abstraction.length - 1], 1, target);
		}
		return target;
	}

	private static <T extends HybridVariableMapping<Integer>> double[] updateTargetByIndex(T varMap, int index,
			int value, double[] target) {
		if (varMap.isSingleVariableObject(index)) {
			target[varMap.getSingleVariableIndexOf(index)] += value;
		} else if (varMap.isDualVariableObject(index)) {
			target[varMap.getXVariableIndexOf(index)] += value;
			target[varMap.getYVariableIndexOf(index)] -= value;
		}
		return target;
	}

}
