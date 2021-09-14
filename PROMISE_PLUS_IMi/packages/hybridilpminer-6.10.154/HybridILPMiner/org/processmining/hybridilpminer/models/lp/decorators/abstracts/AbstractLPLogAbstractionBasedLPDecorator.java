package org.processmining.hybridilpminer.models.lp.decorators.abstracts;

import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.configuration.interfaces.LPMinerConfiguration;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPConstraintType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPFilterType;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.utils.LPEngineUtilities;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;

@Deprecated //relocated:org.processmining.hybridilpminer.algorithms.decorators
public abstract class AbstractLPLogAbstractionBasedLPDecorator extends AbstractLPDecorator {

	protected final LPMinerConfiguration configuration;
	protected Set<int[]> constraintBody = new THashSet<>();
	protected int constraintIndex = 1;
	protected final LPLogAbstraction<?> logAbstraction;
	protected int numConstraints;

	public AbstractLPLogAbstractionBasedLPDecorator(LPMinerConfiguration configuration,
			LPLogAbstraction<?> logAbstraction) {
		this.configuration = configuration;
		this.logAbstraction = logAbstraction;
		this.constraintIndex = LPEngineUtilities.minimalConstraintIndex(this.configuration.getEngineType());
		this.numConstraints = estimateNumberOfConstraints();
	}

	protected void addConstraint(double[] constraint, LPEngine.Operator operator, double rhs) {
		if (constraintIndex <= numConstraints) {
			engine.setConstraint(constraintIndex, constraint, operator, rhs);
			constraintIndex++;
		} else {
			engine.addConstraint(constraint, operator, rhs);
		}
	}

	protected void addEmptyAfterCompletionConstraints() {
		for (int[] traceAbstraction : this.logAbstraction.eventLogAbstraction().keySet()) {
			if (this.constraintBody.contains(traceAbstraction)) {
				addConstraint(emptyAfterCompletionConstraint(traceAbstraction), LPEngine.Operator.LESS_EQUAL, 0);
			}
		}
	}

	protected void addTheoryOfRegionsConstraints() {
		for (int[] abstraction : this.constraintBody) {
			if (abstraction.length > 0) {
				Pair<double[], Double> constrRhs = theoryOfRegionsConstraint(abstraction);
				addConstraint(constrRhs.getFirst(), LPEngine.Operator.GREATER_EQUAL, constrRhs.getSecond());
			}
		}
	}

	protected Set<int[]> calculateConstraintBody() {
		return this.logAbstraction.prefixClosureAbstraction().keySet();
	}

	protected TIntSet calculateUnusedEvents() {
		TIntSet unusedEvents = new TIntHashSet();
		unusedEvents.addAll(this.logAbstraction.range());
		for (int[] member : constraintBody) {
			for (int i = 0; i < member.length - 1; i++) {
				if (member[i] != 0) {
					unusedEvents.remove(i);
				}
			}
			if (member[member.length - 1] != -1) {
				unusedEvents.remove(member[member.length - 1]);
			}
		}
		return unusedEvents;
	}

	protected void disableUnusedEvents() {
		this.addConstraint(this.setOfObjectBasedVariablesConstraint(), LPEngine.Operator.EQUAL, 0);
	}

	protected abstract double[] emptyAfterCompletionConstraint(int[] w);

	protected int estimateNumberOfConstraints() {
		int numConstr = 0;
		numConstr += numConstraintsBasedOnLPConstraintTypes(configuration.getLPConstraintTypes());
		numConstr += numConstraintsBasedOnFilter(configuration.getFilter().getFilterType());
		return numConstr;
	}

	protected abstract double[] minimizeArcsObjective();

	private int numConstraintsBasedOnFilter(LPFilterType filter) {
		int result = 0;
		switch (configuration.getFilter().getFilterType()) {
			case SLACK_VAR :
				result++;
				break;
			default :
				break;
		}
		return result;
	}

	private int numConstraintsBasedOnLPConstraintTypes(Set<LPConstraintType> s) {
		int result = 0;
		for (LPConstraintType type : s) {
			// we can only set some default value of rows for th. of regions and
			// no marking constraint
			// as the default (unchangeable) rhs. of a constraint is 0.
			switch (type) {
				case THEORY_OF_REGIONS :
					result += logAbstraction.prefixClosureAbstraction().size();
					break;
				case EMPTY_AFTER_COMPLETION :
					result += logAbstraction.eventLogAbstraction().size();
					break;
				case NO_TRIVIAL_REGION :
					result++;
					break;
			}
		}
		return result;
	}

	protected abstract double[] setOfObjectBasedVariablesConstraint();

	@Override
	protected void setupConstraints() {
		this.constraintBody = this.calculateConstraintBody();
		this.disableUnusedEvents();
		for (LPConstraintType type : configuration.getLPConstraintTypes()) {
			switch (type) {
				case THEORY_OF_REGIONS :
					addTheoryOfRegionsConstraints();
					break;
				case EMPTY_AFTER_COMPLETION :
					addEmptyAfterCompletionConstraints();
					break;
				case NO_TRIVIAL_REGION :
					// not implemented yet.
					break;
			}
		}
	}

	protected void setupObjective() {
		double[] objective;
		LPEngine.ObjectiveTargetType targetType;
		switch (configuration.getObjectiveType()) {
			case UNWEIGHTED_PARIKH :
				objective = unweightedParikhObjective();
				targetType = LPEngine.ObjectiveTargetType.MIN;
				break;
			case WEIGHTED_ABSOLUTE_PARIKH :
				objective = weightedAbsoluteParikhObjective();
				targetType = LPEngine.ObjectiveTargetType.MIN;
				break;
			case MINIMIZE_ARCS :
				objective = minimizeArcsObjective();
				targetType = LPEngine.ObjectiveTargetType.MIN;
				break;
			default :
			case WEIGHTED_RELATIVE_PARIKH :
				objective = weightedRelativeParikhObjective();
				targetType = LPEngine.ObjectiveTargetType.MIN;
				break;
		}
		super.engine.setObjective(objective, targetType);
	}

	protected abstract Pair<double[], Double> theoryOfRegionsConstraint(int[] w);

	protected abstract double[] unweightedParikhObjective();

	protected abstract double[] weightedAbsoluteParikhObjective();

	protected abstract double[] weightedRelativeParikhObjective();

}
