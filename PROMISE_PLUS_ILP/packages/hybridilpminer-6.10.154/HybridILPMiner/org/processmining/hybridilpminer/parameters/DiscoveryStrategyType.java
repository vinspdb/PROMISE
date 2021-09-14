package org.processmining.hybridilpminer.parameters;

public enum DiscoveryStrategyType {
	CAUSAL_E_VERBEEK("Mine a place per causal relation (configurable)"), TRANSITION_PAIR(
			"Mine a connecting place between each pair of transitions"), CAUSAL_FLEX_HEUR(
					"Mine a place per causal relation (flexible heuristics miner)"), @Deprecated
	CAUSAL("Causal (Deprecated)");
	// K_BOUND("Mine up to a certain number of \"k\" places"); 

	private String name;

	DiscoveryStrategyType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}