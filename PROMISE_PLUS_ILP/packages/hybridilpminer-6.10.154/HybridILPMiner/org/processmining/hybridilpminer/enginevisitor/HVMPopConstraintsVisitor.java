package org.processmining.hybridilpminer.enginevisitor;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

public class HVMPopConstraintsVisitor<VM extends HybridVariableMapping<?>> extends AbstractHybridLPEngineVisitor<VM> {

	private final int total;

	public HVMPopConstraintsVisitor(final VM mapping, final int total) {
		super(mapping);
		this.total = total;
	}

	public void visit(LPEngine engine) {
		for (int i = 0; i < total; i++) {
			if (engine.numConstraints() == 0) {
				break;
			}
			engine.pop();
		}
	}

}
