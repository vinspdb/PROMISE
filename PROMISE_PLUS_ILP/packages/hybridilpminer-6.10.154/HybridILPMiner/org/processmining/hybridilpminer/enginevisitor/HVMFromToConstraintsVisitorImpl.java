package org.processmining.hybridilpminer.enginevisitor;

import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.interfaces.LPEngine.Operator;

public class HVMFromToConstraintsVisitorImpl<T extends HybridVariableMapping<E>, E>
		extends AbstractHybridLPEngineVisitor<T> {

	private final E from;

	private final E to;

	public HVMFromToConstraintsVisitorImpl(T mapping, E from, E to) {
		super(mapping);
		this.from = from;
		this.to = to;
	}

	public void visit(LPEngine engine) {
		// no marking constraint
		double[] constr = engine.emptyConstraint();
		constr[getVariableMapping().getMarkingVariableLPIndex()] = 1d;
		engine.addConstraint(constr, LPEngine.Operator.EQUAL, 0);

		constr = engine.emptyConstraint();
		int i = getVariableMapping().isSingleVariableObject(from) ? getVariableMapping().getSingleVariableIndexOf(from)
				: getVariableMapping().getXVariableIndexOf(from);
		constr[i] = 1d;
		engine.addConstraint(constr, LPEngine.Operator.EQUAL, 1);

		constr = engine.emptyConstraint();
		if (getVariableMapping().isSingleVariableObject(to)) {
			constr[getVariableMapping().getSingleVariableIndexOf(to)] = -1;
		} else {
			constr[getVariableMapping().getYVariableIndexOf(to)] = 1;
		}
		engine.addConstraint(constr, Operator.EQUAL, 1);
	}

}
