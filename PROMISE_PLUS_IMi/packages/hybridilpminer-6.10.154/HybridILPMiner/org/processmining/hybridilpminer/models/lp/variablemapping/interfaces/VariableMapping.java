package org.processmining.hybridilpminer.models.lp.variablemapping.interfaces;

import org.processmining.lpengines.interfaces.LPEngine;

public interface VariableMapping {

	/**
	 * What is the engineType-type that this variable mapping is based upon?
	 *
	 * @return type of engineType mapping.
	 */
	public LPEngine.EngineType engineType();

}
