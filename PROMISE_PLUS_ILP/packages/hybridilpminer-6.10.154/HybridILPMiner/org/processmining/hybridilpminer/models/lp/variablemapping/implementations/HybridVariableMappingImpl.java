package org.processmining.hybridilpminer.models.lp.variablemapping.implementations;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Arrays;
import java.util.Set;

import org.processmining.hybridilpminer.models.lp.variablemapping.abstracts.AbstractVariableMapping;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.utils.LPEngineUtilities;

import com.google.common.primitives.Ints;

public class HybridVariableMappingImpl<T> extends AbstractVariableMapping implements HybridVariableMapping<T> {

	protected final Set<T> domain = new THashSet<>();

	protected final Set<T> dualVariableObjects;
	protected int markingVariable;

	protected int maxIndex;
	protected final TObjectIntMap<T> objectToVariableIndexMap = new TObjectIntHashMap<>();
	protected final Set<T> singleVariableObjects;

	public HybridVariableMappingImpl(LPEngine.EngineType engineType, Set<T> singleVariableObjects,
			Set<T> dualVariableObjects) {
		super(engineType);
		this.singleVariableObjects = singleVariableObjects;
		this.dualVariableObjects = dualVariableObjects;
		this.domain.addAll(this.singleVariableObjects);
		this.domain.addAll(this.dualVariableObjects);
		setupMapping();
	}

	public Set<T> getDomain() {
		return this.domain;
	}

	@Override
	public Set<T> getDualVariableObjects() {
		return dualVariableObjects;
	}

	@Override
	public int getMarkingVariableLPIndex() {
		return markingVariable;
	}

	@Override
	public T getObjectOfLpIndex(int lpIndex) {
		T result = null;
		int searchIndex = lpIndex;
		if (lpIndex != markingVariable) {
			if (lpIndex <= Ints.max(objectToVariableIndexMap.values()) + 1) {
				if (!Ints.contains(objectToVariableIndexMap.values(), lpIndex)) {
					// the lpIndex is within range, though is not within the map
					// this means that it is an y-variable, its corresponding
					// x-variable
					// has an lpIndex value which is one lower.
					searchIndex--;
				}
				for (T t : objectToVariableIndexMap.keySet()) {
					if (objectToVariableIndexMap.get(t) == searchIndex) {
						result = t;
						break;
					}
				}
			}
		}
		return result;
	}

	@Override
	public int getSingleVariableIndexOf(T t) {
		return objectToVariableIndexMap.get(t);
	}

	@Override
	public int[] getSingleVariableIndices() {
		int[] result = new int[singleVariableObjects.size()];
		int i = 0;
		for (T t : objectToVariableIndexMap.keySet()) {
			if (singleVariableObjects.contains(t)) {
				result[i] = objectToVariableIndexMap.get(t);
				i++;
			}
		}
		return result;
	}

	@Override
	public Set<T> getSingleVariables() {
		return singleVariableObjects;
	}

	@Override
	public int getXVariableIndexOf(T t) {
		return objectToVariableIndexMap.get(t);
	}

	@Override
	public int[] getXVariableIndices() {
		int[] result = new int[dualVariableObjects.size()];
		int i = 0;
		for (T t : objectToVariableIndexMap.keySet()) {
			if (dualVariableObjects.contains(t)) {
				result[i] = objectToVariableIndexMap.get(t);
				i++;
			}
		}
		return result;
	}

	@Override
	public int getYVariableIndexOf(T t) {
		return getXVariableIndexOf(t) + 1;
	}

	@Override
	public int[] getYVariableIndices() {
		int[] result = new int[dualVariableObjects.size()];
		int i = 0;
		for (T t : objectToVariableIndexMap.keySet()) {
			if (dualVariableObjects.contains(t)) {
				// shift the value one up to get actual y-variable index
				result[i] = objectToVariableIndexMap.get(t) + 1;
				i++;
			}
		}
		return result;
	}

	public boolean isDual() {
		return singleVariableObjects.isEmpty();
	}

	public boolean isDualVariableIndex(int lpVarIndex) {
		return isXVariableIndex(lpVarIndex) || isYVariableIndex(lpVarIndex);
	}

	@Override
	public boolean isDualVariableObject(T eventClass) {
		return dualVariableObjects.contains(eventClass);
	}

	public boolean isEventRelatedVariableIndex(int i) {
		return this.isDualVariableIndex(i) || this.isSingleVariableIndex(i);
	}

	public boolean isHybrid() {
		return (!isDual() && !isSingle());
	}

	public boolean isSingle() {
		return dualVariableObjects.isEmpty();
	}

	public boolean isSingleVariableIndex(int lpIndex) {
		T t = getObjectOfLpIndex(lpIndex);
		return (t != null && Ints.contains(objectToVariableIndexMap.values(), lpIndex) && singleVariableObjects
				.contains(t));
	}

	@Override
	public boolean isSingleVariableObject(T eventClass) {
		return singleVariableObjects.contains(eventClass);
	}

	@Override
	public boolean isXVariableIndex(int lpIndex) {
		T t = getObjectOfLpIndex(lpIndex);
		return (t != null && Ints.contains(objectToVariableIndexMap.values(), lpIndex) && dualVariableObjects
				.contains(t));
	}

	@Override
	public boolean isYVariableIndex(int lpIndex) {
		T t = getObjectOfLpIndex(lpIndex);
		return (t != null && !(Ints.contains(objectToVariableIndexMap.values(), lpIndex)) && dualVariableObjects
				.contains(t));
	}

	public double[] projectOnHybridVariableIndices(double[] solution) {
		int minIndex = 0;
		int max = Ints.max(this.objectToVariableIndexMap.values());
		if (!this.dualVariableObjects.isEmpty()) {
			max++;
		}

		return Arrays.copyOfRange(solution, minIndex, max + 1);
	}

	protected void setupMapping() {
		maxIndex = LPEngineUtilities.minimalVariableIndex(engineType);
		markingVariable = maxIndex;
		for (T t : singleVariableObjects) {
			maxIndex++;
			objectToVariableIndexMap.put(t, maxIndex);
		}
		for (T t : dualVariableObjects) {
			maxIndex++;
			objectToVariableIndexMap.put(t, maxIndex);
			// extra inc. for y-variables
			maxIndex++;
		}
	}
}
