package org.processmining.hybridilpminer.models.lp.configuration.parameters;

import org.processmining.causalactivitygraph.models.CausalActivityGraph;

@Deprecated //relocated: org.processmining.hybridilpminer.params
public class DiscoveryStrategy {

	private DiscoveryStrategyType strat = DiscoveryStrategyType.CAUSAL;

	private CausalActivityGraph cag = null;

	public DiscoveryStrategy() {
	}

	public DiscoveryStrategy(DiscoveryStrategyType strat) {
		this.strat = strat;
	}

	public DiscoveryStrategyType getDiscoveryStrategyType() {
		return strat;
	}

	public CausalActivityGraph getCausalActivityGraph() {
		return cag;
	}

	public void setDiscoveryStrategyType(DiscoveryStrategyType strat) {
		this.strat = strat;
	}

	public void setCausalActivityGraph(CausalActivityGraph cag) {
		this.cag = cag;
	}

}
