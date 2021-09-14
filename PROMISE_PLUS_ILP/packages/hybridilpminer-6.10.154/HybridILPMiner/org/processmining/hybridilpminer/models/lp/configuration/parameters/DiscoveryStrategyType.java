package org.processmining.hybridilpminer.models.lp.configuration.parameters;

@Deprecated //relocated: org.processmining.hybridilpminer.params
public enum DiscoveryStrategyType {
	CAUSAL("Mine a place per causal relation"), TRANSITION_PAIR(
			"Mine a connecting place between each pair of transitions");
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
