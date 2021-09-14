package org.processmining.hybridilpminer.parameters;

import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitygraphcreator.parameters.DiscoverCausalActivityGraphParameters;
import org.processmining.models.causalgraph.SimpleCausalGraph;

public class DiscoveryStrategy {

	private CausalActivityGraph cag = null;
	private SimpleCausalGraph simpleCag = null;

	private DiscoverCausalActivityGraphParameters cagParams = null;

	private DiscoveryStrategyType type = DiscoveryStrategyType.CAUSAL_FLEX_HEUR;

	public DiscoveryStrategy() {
	}

	public DiscoveryStrategy(DiscoveryStrategy strat) {
		this.type = strat.getDiscoveryStrategyType();
		if (type.equals(DiscoveryStrategyType.CAUSAL_E_VERBEEK)) {
			setCausalActivityGraph(strat.getCausalActivityGraph());
			if (strat.getCausalActivityGraphParameters() != null) {
				setCausalActivityGraphParameters(
						new DiscoverCausalActivityGraphParameters(strat.getCausalActivityGraphParameters()));
			}
		}
	}

	public DiscoveryStrategy(DiscoveryStrategyType strat) {
		this.type = strat;
	}

	public CausalActivityGraph getCausalActivityGraph() {
		return cag;
	}

	public DiscoverCausalActivityGraphParameters getCausalActivityGraphParameters() {
		return cagParams;
	}

	public DiscoveryStrategyType getDiscoveryStrategyType() {
		return type;
	}

	public SimpleCausalGraph getSimpleCag() {
		return simpleCag;
	}

	public void setCausalActivityGraph(CausalActivityGraph cag) {
		this.cag = cag;
	}

	public void setCausalActivityGraphParameters(DiscoverCausalActivityGraphParameters cagParams) {
		this.cagParams = cagParams;
	}

	public void setDiscoveryStrategyType(DiscoveryStrategyType strat) {
		this.type = strat;
	}

	public void setSimpleCag(SimpleCausalGraph simpleCag) {
		this.simpleCag = simpleCag;
	}
}
