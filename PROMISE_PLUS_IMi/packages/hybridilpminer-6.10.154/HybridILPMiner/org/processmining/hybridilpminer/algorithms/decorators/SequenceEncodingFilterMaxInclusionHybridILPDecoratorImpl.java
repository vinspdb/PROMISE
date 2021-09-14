package org.processmining.hybridilpminer.algorithms.decorators;

import java.util.Map;
import java.util.Set;

import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.parameters.HybridILPMinerParametersImpl;

import com.google.common.primitives.Ints;

import gnu.trove.set.hash.THashSet;

public class SequenceEncodingFilterMaxInclusionHybridILPDecoratorImpl<T extends HybridVariableMapping<Integer>>
		extends AbstractSequenceEncodingFilterDecorator<T> {

	double threshold;

	public SequenceEncodingFilterMaxInclusionHybridILPDecoratorImpl(T varMap,
			HybridILPMinerParametersImpl configuration, LPLogAbstraction<?> logAbstraction, double threshold) {
		super(varMap, configuration, logAbstraction);
		this.threshold = threshold;
	}

	@Override
	protected Set<int[]> evaluateFrontCandidates(Map<int[], Set<int[]>> candidates) {
		Set<int[]> candidatesToKeep = new THashSet<>();
		for (Set<int[]> candidateSet : candidates.values()) {
			if (!candidateSet.isEmpty()) {
				int max = this.maxFrequencyValue(candidateSet);
				double includeThreshold = max - (threshold * max);
				for (int[] candidate : candidateSet) {
					if (super.logAbstraction.cardinality(candidate) >= includeThreshold) {
						candidatesToKeep.add(candidate);
					}
				}
			}
		}
		return candidatesToKeep;
	}

	private int maxFrequencyValue(Set<int[]> abstractions) {
		int[] frequencies = new int[abstractions.size()];
		int index = 0;
		for (int[] candidate : abstractions) {
			frequencies[index] = super.logAbstraction.cardinality(candidate);
			index++;
		}
		return Ints.max(frequencies);
	}

}
