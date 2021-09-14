package org.processmining.hybridilpminer.models.lp.variablemapping.implementations;

import gnu.trove.map.TObjectIntMap;

import java.util.Arrays;
import java.util.Set;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.SlackBasedFilterHybridVariableMapping;
import org.processmining.hybridilpminer.utils.trove.TIntArrayIntHashMap;
import org.processmining.lpengines.interfaces.LPEngine.EngineType;

public class IntArrSlackBasedFilterHybridVariableMapping<T> extends HybridVariableMappingImpl<T> implements
		SlackBasedFilterHybridVariableMapping<T, int[]> {

	protected TObjectIntMap<int[]> objectToSlackVariableMapping = new TIntArrayIntHashMap();
	protected int[] slackVariableIndices = new int[0];
	protected int minimalSlackIndex = -1;
	protected final LPLogAbstraction<?> logAbstraction;

	public IntArrSlackBasedFilterHybridVariableMapping(EngineType engineType, Set<T> singleVariables,
			Set<T> dualVariables, LPLogAbstraction<?> logAbstraction) {
		super(engineType, singleVariables, dualVariables);
		this.logAbstraction = logAbstraction;
		for (int[] abstraction : this.logAbstraction.prefixClosureAbstraction().keySet()) {
			this.addObjectAsSlackVariable(abstraction);
		}
	}

	public int addObjectAsSlackVariable(int[] abstraction) {
		maxIndex++;
		objectToSlackVariableMapping.put(abstraction, maxIndex);
		if (slackVariableIndices.length == 0) {
			minimalSlackIndex = maxIndex;
		}
		slackVariableIndices = Arrays.copyOf(slackVariableIndices, slackVariableIndices.length + 1);
		slackVariableIndices[slackVariableIndices.length - 1] = maxIndex;
		return maxIndex;
	}

	public TObjectIntMap<int[]> getSlackMap() {
		return objectToSlackVariableMapping;
	}

	public int getSlackVariableIndex(int[] s) {
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
