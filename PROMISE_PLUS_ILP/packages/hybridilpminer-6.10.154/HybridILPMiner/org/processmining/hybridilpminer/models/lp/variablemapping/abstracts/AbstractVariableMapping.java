package org.processmining.hybridilpminer.models.lp.variablemapping.abstracts;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.VariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

public abstract class AbstractVariableMapping implements VariableMapping {

	protected final LPEngine.EngineType engineType;

	public AbstractVariableMapping(LPEngine.EngineType engineType) {
		this.engineType = engineType;
	}

	public LPEngine.EngineType engineType() {
		return this.engineType;
	}
}
