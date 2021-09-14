package org.processmining.hybridilpminer.enginevisitor;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.models.thr.TheoryOfRegionsConstraintImpl;
import org.processmining.lpengines.interfaces.LPEngine;

public class HVMTheoryOfRegionsVisitorImpl<T extends HybridVariableMapping<E>, E>
		extends AbstractHybridLPEngineVisitor<T> {

	private final Iterable<TheoryOfRegionsConstraintImpl<E>> constraints;

	public HVMTheoryOfRegionsVisitorImpl(T mapping, Iterable<TheoryOfRegionsConstraintImpl<E>> constraints) {
		super(mapping);
		this.constraints = constraints;
	}

	public void visit(LPEngine engine) {
		for (TheoryOfRegionsConstraintImpl<E> constraint : constraints) {
			double[] constr = engine.emptyConstraint();
			constr[getVariableMapping().getMarkingVariableLPIndex()] = 1;
			// process prefix
			for (E e : constraint.getPrefix().baseSet()) {
				if (getVariableMapping().isSingleVariableObject(e)) {
					constr[getVariableMapping().getSingleVariableIndexOf(e)] = constraint.getPrefix().occurrences(e);
				} else {
					constr[getVariableMapping().getXVariableIndexOf(e)] = constraint.getPrefix().occurrences(e);
					constr[getVariableMapping().getYVariableIndexOf(e)] = constraint.getPrefix().occurrences(e) * -1;
				}
			}
			// process last element
			if (constraint.getLast() != null) {
				if (getVariableMapping().isSingleVariableObject(constraint.getLast())) {
					constr[getVariableMapping().getSingleVariableIndexOf(constraint.getLast())]++;
				} else {
					constr[getVariableMapping().getYVariableIndexOf(constraint.getLast())]--;
				}
			}
			engine.addConstraint(constr, LPEngine.Operator.GREATER_EQUAL, 0.0d);
		}
	}

}
