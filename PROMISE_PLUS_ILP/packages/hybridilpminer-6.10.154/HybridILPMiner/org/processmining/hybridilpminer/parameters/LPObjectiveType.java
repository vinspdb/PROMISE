package org.processmining.hybridilpminer.parameters;

public enum LPObjectiveType {
	MINIMIZE_ARCS("Minimize Arcs"), // implemented for: EC/XY
	UNWEIGHTED_PARIKH("Unweighted Parikh values"), // implemented for: EC/XY
	WEIGHTED_ABSOLUTE_PARIKH("Weighted Parikh values, using absolute frequencies"), WEIGHTED_RELATIVE_PARIKH(
			"Weighted Parikh values, using relative frequencies");

	private String name;

	LPObjectiveType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}