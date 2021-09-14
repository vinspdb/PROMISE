package org.processmining.hybridilpminer.utils.abstraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

public class LPLogAbstractionUtils {

	private static class LPLogAbstractionComparator implements Comparator<int[]> {
		public int compare(int[] o1, int[] o2) {
			return lengthOfCorrespondingSequence(o1) - lengthOfCorrespondingSequence(o2);
		}
	}

	private static <T extends HybridVariableMapping<Integer>> double computeAbsBoundaryVariableValueOfEvent(int event,
			T varMap, LPEngine engine) {
		double result = 0.0;
		if (varMap.isSingleVariableObject(event)) {

			result = Math.abs(engine.getVariableLowerBound(varMap.getSingleVariableIndexOf(event)));
		} else if (varMap.isDualVariableObject(event)) {
			result = Math.abs(engine.getVariableUpperBound(varMap.getYVariableIndexOf(event)));
		}
		return result;
	}

	public static int[] getParent(Set<int[]> possibleParents, int[] child) {
		int[] result = new int[0];
		for (int[] parent : possibleParents) {
			if (isParent(parent, child)) {
				result = parent;
				break;
			}
		}
		return result;
	}

	public static boolean isParent(int[] parent, int[] child) {
		int[] parentClone = Arrays.copyOf(parent, parent.length);
		int finalElem = parentClone[parentClone.length - 1];
		if (finalElem != -1)
			parentClone[finalElem]++;
		boolean result = true;
		for (int i = 0; i < parentClone.length - 1; i++) {
			if (parentClone[i] != child[i]) {
				result = false;
				break;
			}
		}
		return result;
	}

	public static int lengthOfCorrespondingSequence(int[] abstraction) {
		int sum = 0;
		for (int i = 0; i < abstraction.length - 1; i++) {
			sum += abstraction[i];
		}
		return abstraction[abstraction.length - 1] == -1 ? sum : sum + 1;
	}

	/**
	 * Computes the smallest possible slack value to always be able to ignore
	 * all token consumptions possible for a given abstraction. Hence the value
	 * depends on the parikh values within the abstraction multiplied by the
	 * (minimal in single / maximal in dual) value of a consumption variable.
	 * 
	 * @param sequenceAbstraction
	 *            abstraction of a sequence
	 * @param engine
	 *            LP engine used, needed to query boundary values of variables
	 * @param varMap
	 *            Mapping of integers representing events to integers
	 *            representing lp variable indices
	 * @return
	 */
	public static <T extends HybridVariableMapping<Integer>> double smallestPossibleSlackValue(
			int[] sequenceAbstraction, LPEngine engine, T varMap) {
		double closestPossibleSlackValue = 0;
		for (int event = 0; event < sequenceAbstraction.length - 1; event++) {
			if (sequenceAbstraction[event] > 0) {
				closestPossibleSlackValue += (sequenceAbstraction[event] * computeAbsBoundaryVariableValueOfEvent(
						event, varMap, engine));
			}
		}
		if (sequenceAbstraction[sequenceAbstraction.length - 1] != -1) {
			closestPossibleSlackValue += computeAbsBoundaryVariableValueOfEvent(
					sequenceAbstraction[sequenceAbstraction.length - 1], varMap, engine);
		}
		return closestPossibleSlackValue;
	}

	public static List<int[]> sortAbstractions(Collection<int[]> abstractions) {
		List<int[]> result = new ArrayList<>(abstractions);
		Collections.sort(result, new LPLogAbstractionUtils.LPLogAbstractionComparator());
		return result;
	}
}
