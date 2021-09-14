package org.processmining.hybridilpminer.models.lp.instance.implementations;

import java.util.Arrays;

import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.models.lp.instance.abstracts.AbstractLPInstance;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

// TODO: move constraint functions to template
public class HybridPairBasedInstanceImpl<T extends HybridVariableMapping<S>, S> extends AbstractLPInstance<T> {

	private boolean DEBUG = false;

	Pair<S, S> pair;

	public HybridPairBasedInstanceImpl(T varMap, LPEngine engine, Pair<S, S> pair) {
		super(engine, varMap);
		this.pair = pair;
	}

	protected void cleanEngine() {
		if (DEBUG) {
			System.out.println(Arrays.toString(this.result.getFirst()));
		}
		for (int i = 0; i < 3; i++) {
			super.engine.pop();
		}
	}

	@Deprecated
	protected double[] disableMarkingConstraint() {
		double[] constr = engine.emptyConstraint();
		constr[super.varMap.getMarkingVariableLPIndex()] = 1d;
		return constr;
	}

	protected void instantiateEngine() {

		super.engine.addConstraint(this.disableMarkingConstraint(), LPEngine.Operator.EQUAL, 0d);
		super.engine.addConstraint(this.sourcePresenceConstraint(), LPEngine.Operator.EQUAL, 1d);
		super.engine.addConstraint(this.targetPresenceConstraint(), LPEngine.Operator.EQUAL, 1d);

		if (DEBUG) {
			printPairAndEngine();
		}
	}

	protected void printPairAndEngine() {
		System.out.println("Mining: " + pair.getFirst().toString() + " -> " + pair.getSecond().toString());
		engine.print();
	}

	@Deprecated
	protected double[] sourcePresenceConstraint() {
		double[] constr = super.engine.emptyConstraint();
		if (varMap.isSingleVariableObject(this.pair.getFirst())) {
			constr[varMap.getSingleVariableIndexOf(this.pair.getFirst())] = 1d;
		} else {
			constr[varMap.getXVariableIndexOf(this.pair.getFirst())] = 1d;
		}
		return constr;
	}

	@Deprecated
	protected double[] targetPresenceConstraint() {
		double[] constr = engine.emptyConstraint();
		if (varMap.isSingleVariableObject(this.pair.getSecond())) {
			constr[varMap.getSingleVariableIndexOf(this.pair.getSecond())] = -1d;
		} else {
			constr[varMap.getYVariableIndexOf(this.pair.getSecond())] = 1d;
		}
		return constr;
	}
}
