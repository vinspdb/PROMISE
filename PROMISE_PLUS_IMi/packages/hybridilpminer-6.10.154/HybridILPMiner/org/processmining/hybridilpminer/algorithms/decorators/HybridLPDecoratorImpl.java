package org.processmining.hybridilpminer.algorithms.decorators;

import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.parameters.HybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.templates.lp.constraints.EmptyAfterCaseCompletionConstraint;
import org.processmining.hybridilpminer.templates.lp.constraints.SetOfObjectBasedVariablesConstraint;
import org.processmining.hybridilpminer.templates.lp.constraints.TheoryOfRegionsConstraint;
import org.processmining.hybridilpminer.templates.lp.objectives.UnWeightedParikhObjective;
import org.processmining.hybridilpminer.templates.lp.objectives.UnaryArcsObjective;
import org.processmining.hybridilpminer.templates.lp.objectives.WeightedParikhObjective;
import org.processmining.lpengines.factories.LPEngineFactory;
import org.processmining.lpengines.interfaces.LPEngine;

public class HybridLPDecoratorImpl<T extends HybridVariableMapping<Integer>>
		extends AbstractLPLogAbstractionBasedLPDecorator {

	protected T varMap;

	public HybridLPDecoratorImpl(T varMap, HybridILPMinerParametersImpl configuration,
			LPLogAbstraction<?> logAbstraction) {
		super(configuration, logAbstraction);
		this.varMap = varMap;
	}

	@Override
	protected double[] emptyAfterCompletionConstraint(int[] w) {
		return EmptyAfterCaseCompletionConstraint.construct(varMap, w, super.engine.emptyConstraint());
	}

	protected double[] minimizeArcsObjective() {
		return UnaryArcsObjective.construct(varMap, super.engine.emptyConstraint());
	}

	@Override
	protected double[] setOfObjectBasedVariablesConstraint() {
		return SetOfObjectBasedVariablesConstraint.construct(this.varMap, super.calculateUnusedEvents(),
				super.engine.emptyConstraint(), false);
	}

	@Override
	protected LPEngine setupEngine() {
		int singleVariables = varMap.getSingleVariables().size();
		int doubleVariables = varMap.getDualVariableObjects().size();
		return LPEngineFactory.createLPEngine(LPEngine.EngineType.LPSOLVE, numConstraints,
				singleVariables + 2 * doubleVariables + 1);
	}

	protected void setupVariables() {
		super.engine.setType(varMap.getMarkingVariableLPIndex(), LPEngine.VariableType.REAL);
		super.engine.setUpperBound(varMap.getMarkingVariableLPIndex(), 1);
		super.engine.setLowerBound(varMap.getMarkingVariableLPIndex(), 0);
		for (int i : varMap.getSingleVariableIndices()) {
			super.engine.setType(i, LPEngine.VariableType.REAL);
			super.engine.setUpperBound(i, 1);
			super.engine.setLowerBound(i, -1);
		}
		for (int i : varMap.getXVariableIndices()) {
			super.engine.setType(i, LPEngine.VariableType.REAL);
			super.engine.setUpperBound(i, 1);
			super.engine.setLowerBound(i, 0);
		}
		for (int i : varMap.getYVariableIndices()) {
			super.engine.setType(i, LPEngine.VariableType.REAL);
			super.engine.setUpperBound(i, 1);
			super.engine.setLowerBound(i, 0);
		}
	}

	@Override
	protected Pair<double[], Double> theoryOfRegionsConstraint(int[] w) {
		return new Pair<>(TheoryOfRegionsConstraint.construct(this.varMap, w, super.engine.emptyConstraint()), 0.0);
	}

	@Override
	protected double[] unweightedParikhObjective() {
		return UnWeightedParikhObjective.construct(this.varMap, super.constraintBody, super.engine.emptyConstraint());
	}

	@Override
	protected double[] weightedAbsoluteParikhObjective() {
		return WeightedParikhObjective.construct(this.varMap, super.constraintBody, super.logAbstraction,
				super.engine.emptyConstraint(), false);
	}

	@Override
	protected double[] weightedRelativeParikhObjective() {
		return WeightedParikhObjective.construct(this.varMap, super.constraintBody, super.logAbstraction,
				super.engine.emptyConstraint(), true);
	}
}
