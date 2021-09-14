package org.processmining.hybridilpminer.enginevisitor;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.interfaces.LPEngine.VariableType;

public class HVMSetupVariablesVisitorImpl<T extends HybridVariableMapping<?>>
		extends AbstractHybridLPEngineVisitor<T> {

	public HVMSetupVariablesVisitorImpl(T mapping) {
		super(mapping);
	}

	public void visit(LPEngine engine) {
		engine.setType(getVariableMapping().getMarkingVariableLPIndex(), VariableType.BOOLEAN);
		for (int i : getVariableMapping().getSingleVariableIndices()) {
			engine.setType(i, VariableType.INTEGER);
			engine.setLowerBound(i, -1);
			engine.setUpperBound(i, 1);
		}
		for (int i : getVariableMapping().getXVariableIndices()) {
			engine.setType(i, VariableType.BOOLEAN);
		}
		for (int i : getVariableMapping().getYVariableIndices()) {
			engine.setType(i, VariableType.BOOLEAN);
		}

	}

}
