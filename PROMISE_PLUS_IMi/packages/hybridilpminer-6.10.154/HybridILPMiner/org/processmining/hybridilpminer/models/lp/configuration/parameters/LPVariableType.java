package org.processmining.hybridilpminer.models.lp.configuration.parameters;

@Deprecated //relocated: org.processmining.hybridilpminer.params
public enum LPVariableType {
	DUAL("Two variables per event"), HYBRID(
			"One variable per event, two for an event which is potentially in a self loop"), SINGLE(
					"One variable per event");

	private String name;

	LPVariableType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
