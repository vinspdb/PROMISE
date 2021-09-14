package org.processmining.hybridilpminer.templates.lp.objectives;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

public class UnaryArcsObjective {

	/**
	 * Creates a unary-used arcs objective (i.e. \vec{1} \cdot \vec{v} + \vec{1}
	 * \cdot \vec{x} + \vec{1} \cdot \vec{y}.
	 * 
	 * @param varMap
	 * @param coefficients
	 * @param minIndex
	 * @param maxIndex
	 * @return
	 */
	public static <T extends HybridVariableMapping<?>> double[] construct(T varMap, double[] coefficients) {

		for (int i : Iterables.concat(Ints.asList(varMap.getSingleVariableIndices()),
				Ints.asList(varMap.getXVariableIndices()), Ints.asList(varMap.getYVariableIndices()))) {
			coefficients[i] = 1d;
		}

		return coefficients;
	}

}
