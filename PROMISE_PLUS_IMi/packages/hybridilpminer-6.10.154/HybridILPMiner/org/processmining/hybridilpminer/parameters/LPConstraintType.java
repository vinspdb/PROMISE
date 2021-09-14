package org.processmining.hybridilpminer.parameters;

public enum LPConstraintType {
	EMPTY_AFTER_COMPLETION("Empty after completion"), NO_TRIVIAL_REGION("No trivial regions"), THEORY_OF_REGIONS(
			"Theory of regions");
	// WRONG_CONTINUATION("Wrong continuation"); // not working in case of
	// single event class.

	protected final String name;

	LPConstraintType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
