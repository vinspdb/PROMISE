package org.processmining.hybridilpminer.algorithms.decorators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.parameters.HybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.utils.abstraction.LPLogAbstractionUtils;

import gnu.trove.set.hash.THashSet;

public abstract class AbstractSequenceEncodingFilterDecorator<T extends HybridVariableMapping<Integer>>
		extends HybridILPDecoratorImpl<T> {

	public AbstractSequenceEncodingFilterDecorator(T varMap, HybridILPMinerParametersImpl configuration,
			LPLogAbstraction<?> logAbstraction) {
		super(varMap, configuration, logAbstraction);
	}

	private Map<int[], Set<int[]>> addNewFrontCandidate(Map<int[], Set<int[]>> candidateMap, int[] child) {
		int[] parent = LPLogAbstractionUtils.getParent(candidateMap.keySet(), child);
		if (parent.length != 0) {
			candidateMap.get(parent).add(child);
		}
		return candidateMap;
	}

	private Set<int[]> applyFilterFromRoot(List<int[]> abstractionsOrdered) {
		final Set<int[]> result = new THashSet<>();
		Map<int[], Set<int[]>> candidateMap = new HashMap<>();
		candidateMap.put(abstractionsOrdered.get(0), new THashSet<int[]>());
		int previousFrontSequenceLength = 0;
		result.add(abstractionsOrdered.get(0));

		for (int i = 1; i < abstractionsOrdered.size(); i++) {
			boolean isPotentialChild = LPLogAbstractionUtils
					.lengthOfCorrespondingSequence(abstractionsOrdered.get(i)) == previousFrontSequenceLength + 1;
			if (isPotentialChild) {
				candidateMap = addNewFrontCandidate(candidateMap, abstractionsOrdered.get(i));
			}
			if (!isPotentialChild || i == abstractionsOrdered.size() - 1) {
				Set<int[]> newFront = evaluateFrontCandidates(candidateMap);
				result.addAll(newFront);
				candidateMap.clear();
				for (int[] newParent : newFront) {
					candidateMap.put(newParent, new THashSet<int[]>());
				}
				previousFrontSequenceLength++;
				candidateMap = addNewFrontCandidate(candidateMap, abstractionsOrdered.get(i));
			}
		}
		if (!candidateMap.isEmpty()) {
			result.addAll(evaluateFrontCandidates(candidateMap));
			candidateMap.clear();
		}
		return result;
	}

	@Override
	protected Set<int[]> calculateConstraintBody() {
		List<int[]> abstractions = LPLogAbstractionUtils
				.sortAbstractions(super.logAbstraction.prefixClosureAbstraction().keySet());
		//		assert (LPLogAbstractionUtils.lengthOfCorrespondingSequence(abstractions.get(0)) == 0);
		return applyFilterFromRoot(abstractions);
	}

	protected abstract Set<int[]> evaluateFrontCandidates(Map<int[], Set<int[]>> candidates);

}
