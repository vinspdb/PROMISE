package org.processmining.hybridilpminer.models.lp.instance.implementations;

import java.util.Set;

import org.processmining.hybridilpminer.models.lp.instance.abstracts.AbstractLPInstance;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.lpengines.interfaces.LPEngine;

import gnu.trove.set.hash.THashSet;

// TODO: move constraint functions to template
public class HybridSourcePlaceLPInstanceImpl<T extends HybridVariableMapping<S>, S> extends AbstractLPInstance<T> {

	protected int constraintsAdded = 0;
	protected final Set<S> sourceCandidates;

	public HybridSourcePlaceLPInstanceImpl(T varMap, LPEngine engine, Set<S> sourceCandidates) {
		super(engine, varMap);
		this.sourceCandidates = sourceCandidates;
	}

	@Deprecated
	protected double[] allDisallowedYComponentsConstraint() {
		double[] constr = super.engine.emptyConstraint();
		Set<S> disAllowedObjects = new THashSet<>();
		disAllowedObjects.addAll(super.varMap.getDomain());
		disAllowedObjects.removeAll(this.sourceCandidates);
		for (S obj : disAllowedObjects) {
			if (super.varMap.isDualVariableObject(obj)) {
				constr[super.varMap.getYVariableIndexOf(obj)] = 1;
			}
		}
		return constr;
	}

	@Deprecated
	protected double[] allXComponentsConstraint() {
		double[] constr = super.engine.emptyConstraint();
		for (int i : varMap.getXVariableIndices()) {
			constr[i] = 1d;
		}
		return constr;
	}

	protected void cleanEngine() {
		engine.destroy();
	}

	protected void instantiateEngine() {
		engine.addConstraint(this.markingPresentConstraint(), LPEngine.Operator.EQUAL, 1d);
		this.constraintsAdded++;

		// switch off all x-components
		engine.addConstraint(this.allXComponentsConstraint(), LPEngine.Operator.EQUAL, 0);
		this.constraintsAdded++;

		// switch off all disallowed y components
		engine.addConstraint(this.allDisallowedYComponentsConstraint(), LPEngine.Operator.EQUAL, 0);
		this.constraintsAdded++;

		// set all single variables
		for (int i : varMap.getSingleVariableIndices()) {
			double[] constr = super.engine.emptyConstraint();
			constr[i] = 1d;
			if (sourceCandidates.contains(varMap.getObjectOfLpIndex(i))) {
				engine.addConstraint(constr, LPEngine.Operator.LESS_EQUAL, 0);
			} else {
				engine.addConstraint(constr, LPEngine.Operator.EQUAL, 0);
			}
			this.constraintsAdded++;
		}
	}

	@Deprecated
	protected double[] markingPresentConstraint() {
		double[] constr = super.engine.emptyConstraint();
		constr[varMap.getMarkingVariableLPIndex()] = 1d;
		return constr;
	}

}
