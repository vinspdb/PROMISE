package org.processmining.hybridilpminer.templates.lp.constraints;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.SlackBasedFilterHybridVariableMapping;
import org.processmining.hybridilpminer.utils.abstraction.LPLogAbstractionUtils;
import org.processmining.lpengines.interfaces.LPEngine;

public class TheoryOfRegionsConstraint {

	/**
	 * Constructs a variable-mapping based constraint using an abstraciton of
	 * (prefixes of) traces. i.e. given we have activities a,b,c and d and the
	 * trace <a,b,c> with activity mapping (a -> 0, b -> 1, c -> 2, d -> 3). The
	 * trace should be encoded as [1,1,0,0,2], i.e. a occurs once, b occurs
	 * once, c does not occur, d does not occur, c is the last event in the
	 * sequence.
	 * 
	 * @param varMap
	 *            variable mapping
	 * @param abs
	 *            sequence abstraction
	 * @param target
	 *            container to encode the constraint
	 * @return constraint
	 */
	public static <T extends HybridVariableMapping<Integer>> double[] construct(T varMap, int[] abs, double[] target) {
		target[varMap.getMarkingVariableLPIndex()] = 1;
		for (int i = 0; i < abs.length - 1; i++) {
			if (abs[i] > 0) {
				if (varMap.isSingleVariableObject(i))
					target[varMap.getSingleVariableIndexOf(i)] = abs[i];
				else {
					target[varMap.getXVariableIndexOf(i)] = abs[i];
					target[varMap.getYVariableIndexOf(i)] = abs[i] * -1;
				}
			}
		}
		if (abs[abs.length - 1] != -1) {
			int i = abs[abs.length - 1];
			if (varMap.isSingleVariableObject(i))
				target[varMap.getSingleVariableIndexOf(i)]++;
			else {
				target[varMap.getYVariableIndexOf(i)]--;
			}
		}
		return target;
	}

	public static <T extends SlackBasedFilterHybridVariableMapping<Integer, int[]>> double[] construct(T varMap,
			int[] abstraction, double[] target, LPEngine engine) {
		target = construct((HybridVariableMapping<Integer>) varMap, abstraction, target);
		target[varMap.getSlackVariableIndex(abstraction)] = LPLogAbstractionUtils.smallestPossibleSlackValue(
				abstraction, engine, varMap);
		return target;
	}

}
