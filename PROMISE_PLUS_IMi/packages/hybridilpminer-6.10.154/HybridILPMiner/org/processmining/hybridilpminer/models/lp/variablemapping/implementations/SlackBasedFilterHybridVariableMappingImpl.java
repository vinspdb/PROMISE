package org.processmining.hybridilpminer.models.lp.variablemapping.implementations;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.Arrays;
import java.util.Set;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.SlackBasedFilterHybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine.EngineType;

public class SlackBasedFilterHybridVariableMappingImpl<T, S> extends HybridVariableMappingImpl<T> implements
		SlackBasedFilterHybridVariableMapping<T, S> {

	protected TObjectIntMap<S> objectToSlackVariableMapping = new TObjectIntHashMap<>();
	protected int[] slackVariableIndices = new int[0];
	protected int minimalSlackIndex = -1;

	public SlackBasedFilterHybridVariableMappingImpl(EngineType engineType, Set<T> singleVariables, Set<T> dualVariables) {
		super(engineType, singleVariables, dualVariables);
	}

	public int addObjectAsSlackVariable(S s) {
		maxIndex++;
		objectToSlackVariableMapping.put(s, maxIndex);
		if (slackVariableIndices.length == 0) {
			minimalSlackIndex = maxIndex;
		}
		slackVariableIndices = Arrays.copyOf(slackVariableIndices, slackVariableIndices.length + 1);
		slackVariableIndices[slackVariableIndices.length - 1] = maxIndex;
		return maxIndex;
	}

	public TObjectIntMap<S> getSlackMap() {
		return objectToSlackVariableMapping;
	}

	public int getSlackVariableIndex(S s) {
		return objectToSlackVariableMapping.get(s);
	}

	public boolean isSlackVariableIndex(int lpIndex) {
		return lpIndex > minimalSlackIndex && lpIndex < minimalSlackIndex + slackVariableIndices.length;
	}

	public int slackVariableIndex(int[] w) {
		return objectToSlackVariableMapping.get(w);
	}

	public int[] slackVariableIndices() {
		return slackVariableIndices;
	}

}
