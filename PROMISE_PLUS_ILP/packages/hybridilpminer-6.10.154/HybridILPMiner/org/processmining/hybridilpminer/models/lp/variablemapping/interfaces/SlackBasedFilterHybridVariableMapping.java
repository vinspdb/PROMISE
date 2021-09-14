package org.processmining.hybridilpminer.models.lp.variablemapping.interfaces;

import gnu.trove.map.TObjectIntMap;

public interface SlackBasedFilterHybridVariableMapping<T, S> extends HybridVariableMapping<T> {

	public int addObjectAsSlackVariable(S s);

	public TObjectIntMap<S> getSlackMap();

	public boolean isSlackVariableIndex(int lpIndex);

	public int getSlackVariableIndex(S s);

	public int[] slackVariableIndices();

}
