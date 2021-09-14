package org.processmining.hybridilpminer.algorithms.decorators;

import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.SlackBasedFilterHybridVariableMapping;
import org.processmining.hybridilpminer.parameters.HybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.templates.lp.constraints.EmptyAfterCaseCompletionConstraint;
import org.processmining.hybridilpminer.templates.lp.constraints.TheoryOfRegionsConstraint;
import org.processmining.hybridilpminer.templates.lp.objectives.WeightedParikhObjective;
import org.processmining.lpengines.factories.LPEngineFactory;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.interfaces.LPEngine.Operator;

public class SlackBasedFilterHybridILPDecoratorImpl<T extends SlackBasedFilterHybridVariableMapping<Integer, int[]>>
		extends HybridILPDecoratorImpl<T> {

	private final double threshold;

	public SlackBasedFilterHybridILPDecoratorImpl(T varMap, HybridILPMinerParametersImpl configuration,
			LPLogAbstraction<?> logAbstraction, double threshold) {
		super(varMap, configuration, logAbstraction);
		this.threshold = threshold;
	}

	protected void addSlackBasedFilterConstraint() {
		double[] constraint = engine.emptyConstraint();
		for (int i : varMap.slackVariableIndices()) {
			constraint[i] = 1;
		}
		double targetValue = threshold * logAbstraction.prefixClosureAbstraction().size();
		addConstraint(constraint, LPEngine.Operator.LESS_EQUAL, targetValue);
	}

	@Override
	protected void setupConstraints() {
		super.setupConstraints();
		addSlackBasedFilterConstraint();
	}

	@Override
	protected LPEngine setupEngine() {
		int numColumns = varMap.getSingleVariables().size() + 2 * varMap.getDualVariableObjects().size() + 1
				+ logAbstraction.prefixClosureAbstraction().size();
		return LPEngineFactory.createLPEngine(LPEngine.EngineType.LPSOLVE, numConstraints, numColumns);
	}

	protected void setupVariables() {
		super.setupVariables();
		for (int i : varMap.slackVariableIndices()) {
			engine.setType(i, LPEngine.VariableType.BOOLEAN);
		}
	}

	@Override
	protected double[] weightedAbsoluteParikhObjective() {
		return WeightedParikhObjective.construct(varMap, super.constraintBody, super.logAbstraction,
				super.engine.emptyConstraint(), false);
	}

	@Override
	protected double[] weightedRelativeParikhObjective() {
		return WeightedParikhObjective.construct(varMap, super.constraintBody, super.logAbstraction,
				super.engine.emptyConstraint(), true);
	}

	@Override
	protected Pair<double[], Double> theoryOfRegionsConstraint(int[] w) {
		return new Pair<>(
				TheoryOfRegionsConstraint.construct(this.varMap, w, super.engine.emptyConstraint(), super.engine), 0.0);
	}

	@Override
	protected void addEmptyAfterCompletionConstraints() {
		for (int[] traceAbstraction : this.logAbstraction.tracePrefixMapping().keySet()) {
			if (this.constraintBody.contains(traceAbstraction)) {
				for (Set<int[]> prefixes : super.logAbstraction.tracePrefixMapping().get(traceAbstraction)) {
					Pair<double[], Double> constr = this.emptyAfterCaseCompletionConstraint(traceAbstraction, prefixes);
					this.addConstraint(constr.getFirst(), Operator.LESS_EQUAL, constr.getSecond());
				}
			}
		}
	}

	protected Pair<double[], Double> emptyAfterCaseCompletionConstraint(int[] abstraction, Set<int[]> prefixes) {
		return new Pair<>(EmptyAfterCaseCompletionConstraint.construct(this.varMap, abstraction, prefixes,
				super.engine.emptyConstraint(), super.engine), 0.0);
	}

}
