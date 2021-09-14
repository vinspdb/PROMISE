package org.processmining.hybridilpminer.models.lp.miner.abstracts;

import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.decorators.interfaces.LPDecorator;
import org.processmining.hybridilpminer.models.lp.instance.implementations.HybridSourcePlaceLPInstanceImpl;
import org.processmining.hybridilpminer.models.lp.instance.interfaces.LPInstance;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.utils.lp.solution.LPSolutionUtilities;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

import gnu.trove.set.hash.THashSet;

@Deprecated
public abstract class AbstractHybridIntLPMiner<T extends LPDecorator, S extends HybridVariableMapping<Integer>, U>
		extends AbstractLPMiner<T, S, U> {

	public AbstractHybridIntLPMiner(S varMap, T decorator, LPLogAbstraction<U> logAbstraction) {
		super(varMap, decorator, logAbstraction);
	}

	protected void findSourcePlaces() {
		Set<Integer> potentialSources = potentialSourceConnections();

		while (!(potentialSources.isEmpty())) {
			LPInstance instance = new HybridSourcePlaceLPInstanceImpl<HybridVariableMapping<Integer>, Integer>(varMap,
					decorator.engine(), potentialSources);
			instance.run();
			solveTimeCummulative += instance.getSolveTime();
			if (instance.solution().length == 0 || LPSolutionUtilities.isTrivial(instance.solution())
					|| LPSolutionUtilities.isOnlyMarking(varMap, instance.solution())) {
				break;
			} else {
				solutions.add(instance.solution());
				potentialSources = removePotentialSourceConnection(potentialSources, instance.solution());
			}
		}
	}

	protected Set<Integer> potentialSourceConnections() {
		Set<Integer> result = new THashSet<>();
		result.addAll(varMap.getDomain());
		for (double[] solution : solutions) {
			removePotentialSourceConnection(result, solution);
		}
		return result;
	}

	protected Set<Integer> removePotentialSourceConnection(Set<Integer> set, double[] solution) {
		for (int i = 0; i < solution.length; i++) {
			if (solution[i] != 0) {
				if ((varMap.isSingleVariableIndex(i) && solution[i] < 0) || varMap.isYVariableIndex(i)) {
					set.remove(varMap.getObjectOfLpIndex(i));
				}
			}
		}
		return set;
	}

	public Pair<Petrinet, Marking> synthesizeNet() {
		Petrinet net = PetrinetFactory.newPetrinet("Petri net");
		Marking marking = new Marking();
		Pair<Petrinet, Marking> result = new Pair<Petrinet, Marking>(net, marking);
		for (U event : logAbstraction.domain()) {
			net.addTransition(event.toString());
		}

		return LPSolutionUtilities.translateSolutionsToMarkedNet(solutions, result, varMap, logAbstraction);

	}

}
