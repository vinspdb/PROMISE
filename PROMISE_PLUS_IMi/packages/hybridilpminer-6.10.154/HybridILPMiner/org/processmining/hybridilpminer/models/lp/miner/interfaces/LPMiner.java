package org.processmining.hybridilpminer.models.lp.miner.interfaces;

import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

@Deprecated
public interface LPMiner extends Runnable {

	public long computationTimeMs();

	public long decorationTimeMs();

	public Set<double[]> solutions();

	public long solveTimeMs();

	public Pair<Petrinet, Marking> synthesizeNet();

}
