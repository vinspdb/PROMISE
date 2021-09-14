package org.processmining.hybridilpminer.enginevisitor;

import org.processmining.framework.util.collection.MultiSet;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.models.thr.TheoryOfRegionsConstraintImpl;
import org.processmining.lpengines.interfaces.LPEngine;

public class HVMSetupObjectiveVisitorImpl<T extends HybridVariableMapping<E>, E>
		extends AbstractHybridLPEngineVisitor<T> {

	private final MultiSet<TheoryOfRegionsConstraintImpl<E>> weightedPcl;

	public HVMSetupObjectiveVisitorImpl(T mapping, MultiSet<TheoryOfRegionsConstraintImpl<E>> weightedPcl) {
		super(mapping);
		this.weightedPcl = weightedPcl;
	}

	public void visit(LPEngine engine) {
		double[] coeff = engine.emptyConstraint();
		for (TheoryOfRegionsConstraintImpl<E> constraint : weightedPcl.baseSet()) {
			int factor = weightedPcl.occurrences(constraint);
			coeff[getVariableMapping().getMarkingVariableLPIndex()] += factor;
			for (E e : constraint.getPrefix().baseSet()) {
				int val = factor * constraint.getPrefix().occurrences(e);
				if (getVariableMapping().isSingleVariableObject(e)) {
					coeff[getVariableMapping().getSingleVariableIndexOf(e)] += val;
				} else {
					coeff[getVariableMapping().getXVariableIndexOf(e)] += val;
					coeff[getVariableMapping().getYVariableIndexOf(e)] -= val;
				}
			}
			if (constraint.getLast() != null) {
				if (getVariableMapping().isSingleVariableObject(constraint.getLast())) {
					coeff[getVariableMapping().getSingleVariableIndexOf(constraint.getLast())] += factor;
				} else {
					coeff[getVariableMapping().getXVariableIndexOf(constraint.getLast())] += factor;
					coeff[getVariableMapping().getYVariableIndexOf(constraint.getLast())] -= factor;
				}
			}
		}
		engine.setObjective(coeff, LPEngine.ObjectiveTargetType.MIN);
	}

}
