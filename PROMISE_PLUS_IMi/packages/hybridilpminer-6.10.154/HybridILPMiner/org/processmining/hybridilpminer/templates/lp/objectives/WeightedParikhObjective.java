package org.processmining.hybridilpminer.templates.lp.objectives;

import java.util.Set;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.SlackBasedFilterHybridVariableMapping;
import org.processmining.hybridilpminer.utils.trove.TObjectIntMapUtils;

public class WeightedParikhObjective {

	private static <T extends HybridVariableMapping<Integer>> double[] construct(T varMap, int[] abstraction,
			int cardinality, double[] target) {
		target[varMap.getMarkingVariableLPIndex()] += cardinality;
		for (int i = 0; i < abstraction.length - 1; i++) {
			if (abstraction[i] > 0) {
				target = updateTargetByIndex(varMap, i, cardinality * abstraction[i], target);
			}
		}
		if (abstraction[abstraction.length - 1] >= 0) {
			target = updateTargetByIndex(varMap, abstraction[abstraction.length - 1], cardinality, target);
		}
		return target;
	}

	public static <T extends HybridVariableMapping<Integer>> double[] construct(T varMap, Set<int[]> abstractions,
			LPLogAbstraction<?> logAbstraction, double[] target, boolean scale) {
		for (int[] abstraction : abstractions) {
			target = construct(varMap, abstraction, logAbstraction.cardinality(abstraction), target);
		}
		if (scale) {
			double sum = TObjectIntMapUtils.sum(logAbstraction.prefixClosureAbstraction(), logAbstraction
					.prefixClosureAbstraction().keySet());
			if (sum > 0) {
				target = scale(target, sum);
			}
		}
		return target;
	}

	public static <T extends SlackBasedFilterHybridVariableMapping<Integer, int[]>> double[] construct(T varMap,
			Set<int[]> abstractions, LPLogAbstraction<?> logAbstraction, double[] coefficients, boolean scale) {

		double[] objective = WeightedParikhObjective.construct((HybridVariableMapping<Integer>) varMap, abstractions,
				logAbstraction, coefficients, scale);

		for (int[] w : abstractions) {
			int frequency = logAbstraction.cardinality(w);
			double weight = Math.pow(frequency + 1.0d, 2);
			objective[varMap.getSlackVariableIndex(w)] = weight;
		}

		return objective;
	}

	private static double[] scale(double[] coefficients, double scaleFactor) {
		for (int i = 0; i < coefficients.length; i++) {
			coefficients[i] = coefficients[i] / scaleFactor;
		}
		return coefficients;
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
