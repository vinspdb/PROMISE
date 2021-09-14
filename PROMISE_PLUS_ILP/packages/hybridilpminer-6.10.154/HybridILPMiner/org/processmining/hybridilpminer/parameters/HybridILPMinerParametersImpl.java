package org.processmining.hybridilpminer.parameters;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.interfaces.LPEngine.EngineType;

public class HybridILPMinerParametersImpl extends PluginParametersImpl {

	private Set<LPConstraintType> constraints = new HashSet<>(NetClass.PT_NET.getRequiredConstraints());
	private PluginContext context;
	private DiscoveryStrategy discoveryStrategy = new DiscoveryStrategy();
	private LPEngine.EngineType engine = EngineType.LPSOLVE;
	private LPFilter filter = new LPFilter();
	private File ilpProblemOutputLocation = null;
	private NetClass netClass = NetClass.PT_NET;
	private LPObjectiveType objectiveType = LPObjectiveType.WEIGHTED_ABSOLUTE_PARIKH;
	private boolean solve = true;
	private LPVariableType variableType = LPVariableType.DUAL;
	private boolean findSink = true;

	public boolean isFindSink() {
		return findSink;
	}

	public void setFindSink(boolean findSource) {
		this.findSink = findSource;
		if (findSink) {
			if (!constraints.contains(LPConstraintType.EMPTY_AFTER_COMPLETION)) {
				constraints.add(LPConstraintType.EMPTY_AFTER_COMPLETION);
			}
		}
	}

	public HybridILPMinerParametersImpl(PluginContext context) {
		this.context = context;
		getLPConstraintTypes().add(LPConstraintType.EMPTY_AFTER_COMPLETION);
	}

	public HybridILPMinerParametersImpl(PluginContext context, LPEngine.EngineType engine,
			DiscoveryStrategy discoveryStrategy, NetClass netClass, Collection<LPConstraintType> constraints,
			LPObjectiveType objectiveType, LPVariableType variableType, LPFilter filter, final boolean solve) {
		this.context = context;
		this.engine = engine;
		this.discoveryStrategy = new DiscoveryStrategy(discoveryStrategy);
		this.netClass = netClass;
		this.constraints = new HashSet<>(constraints);
		this.objectiveType = objectiveType;
		this.variableType = variableType;
		this.filter = new LPFilter(filter.getFilterType(), filter.getThreshold());
		this.solve = solve;
	}

	public void addLPConstraintType(LPConstraintType t) {
		constraints.add(t);
	}

	public PluginContext getContext() {
		return context;
	}

	public DiscoveryStrategy getDiscoveryStrategy() {
		return discoveryStrategy;
	}

	public LPEngine.EngineType getEngine() {
		return engine;
	}

	public LPFilter getFilter() {
		return filter;
	}

	public File getILPOutputLocation() {
		return ilpProblemOutputLocation;
	}

	public Set<LPConstraintType> getLPConstraintTypes() {
		return constraints;
	}

	public LPVariableType getLPVaraibleType() {
		return variableType;
	}

	public DiscoveryStrategy getMiningStrategy() {
		return discoveryStrategy;
	}

	public NetClass getNetClass() {
		return netClass;
	}

	public LPObjectiveType getObjectiveType() {
		return objectiveType;
	}

	public Set<LPConstraintType> getStaticConstraintTypes() {
		return constraints;
	}

	public LPVariableType getVariableType() {
		return variableType;
	}

	public boolean isSolve() {
		return solve;
	}

	public void setContext(PluginContext context) {
		this.context = context;
	}

	public void setDiscoveryStrategy(DiscoveryStrategy strategy) {
		discoveryStrategy = strategy;

	}

	public void setEngineType(EngineType type) {
		engine = type;
	}

	public void setFilter(LPFilter filter) {
		this.filter = filter;

	}

	public void setILPOutputLocation(File dummyLocation) {
		this.ilpProblemOutputLocation = dummyLocation;
	}

	public void setIsSolve(boolean solve) {
		this.solve = solve;
	}

	public void setLPConstraintTypes(Set<LPConstraintType> types) {
		constraints.clear();
		constraints.addAll(types);
		if (!constraints.contains(findSink)) {
			findSink = false;
		}
	}

	public void setMiningStrategy(DiscoveryStrategy strategy) {
		discoveryStrategy = strategy;
	}

	public void setNetClass(NetClass netClass) {
		this.netClass = netClass;
		constraints.clear();
		constraints.addAll(netClass.getRequiredConstraints());
	}

	public void setObjectiveType(LPObjectiveType type) {
		objectiveType = type;

	}

	public void setStaticConstraintTypes(Set<LPConstraintType> types) {
		constraints.addAll(types);
	}

	public void setVariableType(LPVariableType type) {
		variableType = type;
	}
}
