package org.processmining.hybridilpminer.models.lp.configuration.parameters;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Deprecated //relocated: org.processmining.hybridilpminer.params
public enum NetClass {
	PT_NET("(p/t) net",
			new HashSet<LPConstraintType>(
					EnumSet.of(LPConstraintType.THEORY_OF_REGIONS, LPConstraintType.NO_TRIVIAL_REGION)),
			new HashSet<LPConstraintType>(EnumSet.of(LPConstraintType.EMPTY_AFTER_COMPLETION)));

	private final String name;
	private final Set<LPConstraintType> requiredConstraints;
	private final Set<LPConstraintType> optionalConstraints;

	NetClass(String name, Set<LPConstraintType> requiredConstraints, Set<LPConstraintType> optionalConstraints) {
		this.name = name;
		this.requiredConstraints = requiredConstraints;
		this.optionalConstraints = optionalConstraints;
	}

	public String givenName() {
		return this.name;
	}

	@Override
	public String toString() {
		return givenName();
	}

	public Set<LPConstraintType> getRequiredConstraints() {
		return this.requiredConstraints;
	}

	public Set<LPConstraintType> getOptionalConstraints() {
		return this.optionalConstraints;
	}

}
