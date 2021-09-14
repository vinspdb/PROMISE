package org.processmining.hybridilpminer.models.lp.configuration.implementations;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitygraphcreator.parameters.ConvertCausalActivityMatrixToCausalActivityGraphParameters;
import org.processmining.causalactivitygraphcreator.plugins.ConvertCausalActivityMatrixToCausalActivityGraphPlugin;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrixminer.miners.MatrixMiner;
import org.processmining.causalactivitymatrixminer.miners.MatrixMinerParameters;
import org.processmining.causalactivitymatrixminer.miners.impl.HAFMiniMatrixMiner;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.hybridilpminer.models.lp.configuration.interfaces.LPMinerConfiguration;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.DiscoveryStrategy;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.DiscoveryStrategyType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPConstraintType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPFilter;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPObjectiveType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPVariableType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.NetClass;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.lpengines.interfaces.LPEngine.EngineType;

@Deprecated
public class LPMinerConfigurationImpl implements LPMinerConfiguration {

	private Set<LPConstraintType> constraints = new HashSet<>(NetClass.PT_NET.getRequiredConstraints());
	private DiscoveryStrategy discoveryStrategy = new DiscoveryStrategy();
	private LPEngine.EngineType engineType = EngineType.LPSOLVE;
	private XEventClassifier eventClassifier = null;
	private LPFilter filter = new LPFilter();
	private XLog log = null;
	private NetClass netClass = NetClass.PT_NET;
	private LPObjectiveType objectiveType = LPObjectiveType.WEIGHTED_ABSOLUTE_PARIKH;
	private LPVariableType variableType = LPVariableType.DUAL;
	private boolean solve = false;
	private File dummyLocation = null;

	public LPMinerConfigurationImpl() {
	}

	/**
	 * Constructor for default settings.
	 * 
	 * @param context
	 * @param log
	 * @param classifier
	 */
	public LPMinerConfigurationImpl(PluginContext context, XLog log, XEventClassifier classifier) {
		this.log = log;
		this.eventClassifier = classifier;
		constraints.add(LPConstraintType.EMPTY_AFTER_COMPLETION);
		discoveryStrategy.setDiscoveryStrategyType(DiscoveryStrategyType.CAUSAL);
		MatrixMiner miner = new HAFMiniMatrixMiner();
		MatrixMinerParameters minerParameters = new MatrixMinerParameters(log);
		minerParameters.setClassifier(eventClassifier);
		CausalActivityMatrix matrix = miner.mineMatrix(context, log, minerParameters);
		ConvertCausalActivityMatrixToCausalActivityGraphPlugin creator = new ConvertCausalActivityMatrixToCausalActivityGraphPlugin();
		ConvertCausalActivityMatrixToCausalActivityGraphParameters creatorParameters = new ConvertCausalActivityMatrixToCausalActivityGraphParameters();
		creatorParameters.setZeroValue(miner.getZeroValue());
		creatorParameters.setConcurrencyRatio(miner.getConcurrencyRatio());
		creatorParameters.setIncludeThreshold(miner.getIncludeThreshold());
		CausalActivityGraph graph = creator.run(context, matrix, creatorParameters);
		discoveryStrategy.setCausalActivityGraph(graph);
	}

	public LPMinerConfigurationImpl(XLog log, LPEngine.EngineType engineType, XEventClassifier classifier,
			DiscoveryStrategy discoveryStrategy, Collection<LPConstraintType> staticConstraintTypes,
			LPObjectiveType objectiveType, LPVariableType variableType, LPFilter filter, final boolean dummy) {
		this.log = log;
		this.engineType = engineType;
		this.eventClassifier = classifier;
		this.discoveryStrategy = discoveryStrategy;
		this.constraints.addAll(staticConstraintTypes);
		this.objectiveType = objectiveType;
		this.variableType = variableType;
		this.filter = filter;
		this.solve = dummy;
	}

	public void addLPConstraintType(LPConstraintType t) {
		constraints.add(t);
	}

	public DiscoveryStrategy getDiscoveryStrategy() {
		return discoveryStrategy;
	}

	@Override
	public LPEngine.EngineType getEngineType() {
		return engineType;
	}

	@Override
	public XEventClassifier getEventClassifier() {
		return eventClassifier;
	}

	public LPFilter getFilter() {
		return filter;
	}

	public XLog getLog() {
		return log;
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

	public void setDiscoveryStrategy(DiscoveryStrategy strategy) {
		discoveryStrategy = strategy;

	}

	public void setEngineType(EngineType type) {
		engineType = type;
	}

	public void setEvenClassifier(XEventClassifier classifier) {
		eventClassifier = classifier;
	}

	public void setFilter(LPFilter filter) {
		this.filter = filter;

	}

	public void setLog(XLog log) {
		this.log = log;

	}

	public void setLPConstraintTypes(Set<LPConstraintType> types) {
		constraints.addAll(types);
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

	public boolean isDummy() {
		return solve;
	}

	public void setIsDummy(boolean dummy) {
		this.solve = dummy;
	}

	public File getDummyLocation() {
		return dummyLocation;
	}

	public void setDummyLocation(File dummyLocation) {
		this.dummyLocation = dummyLocation;
	}

	public boolean isSolve() {
		return solve;
	}

	public void setIsSolve(boolean solve) {
		this.solve = solve;
	}

}
