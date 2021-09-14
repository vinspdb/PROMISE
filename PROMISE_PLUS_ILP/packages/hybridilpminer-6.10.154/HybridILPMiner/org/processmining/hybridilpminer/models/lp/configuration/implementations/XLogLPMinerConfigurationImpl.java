package org.processmining.hybridilpminer.models.lp.configuration.implementations;

import java.util.Collection;

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
import org.processmining.hybridilpminer.models.lp.configuration.interfaces.XLogLPMinerConfiguration;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.DiscoveryStrategy;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.DiscoveryStrategyType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPConstraintType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPFilter;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPObjectiveType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPVariableType;
import org.processmining.lpengines.interfaces.LPEngine;

@Deprecated
public class XLogLPMinerConfigurationImpl extends LPMinerConfigurationImpl implements XLogLPMinerConfiguration {

	private XEventClassifier eventClassifier = null;
	private XLog log = null;

	public XLogLPMinerConfigurationImpl() {
		super();
	}

	public XLogLPMinerConfigurationImpl(PluginContext context, XLog log, boolean solve) {
		this.log = log;
		setIsDummy(solve);
	}

	/**
	 * Constructor for default settings.
	 * 
	 * @param context
	 * @param log
	 * @param classifier
	 */
	public XLogLPMinerConfigurationImpl(PluginContext context, LPEngine.EngineType engineType, XLog log,
			XEventClassifier classifier, boolean dummy) {
		this.log = log;
		this.eventClassifier = classifier;
		setEngineType(engineType);
		getLPConstraintTypes().add(LPConstraintType.EMPTY_AFTER_COMPLETION);
		getDiscoveryStrategy().setDiscoveryStrategyType(DiscoveryStrategyType.CAUSAL);
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
		getDiscoveryStrategy().setCausalActivityGraph(graph);
		setIsDummy(dummy);
	}

	public XLogLPMinerConfigurationImpl(XLog log, LPEngine.EngineType engineType, XEventClassifier classifier,
			DiscoveryStrategy discoveryStrategy, Collection<LPConstraintType> staticConstraintTypes,
			LPObjectiveType objectiveType, LPVariableType variableType, LPFilter filter, final boolean dummy) {
		super(log, engineType, classifier, discoveryStrategy, staticConstraintTypes, objectiveType, variableType,
				filter, dummy);
	}

	public XEventClassifier getEventClassifier() {
		return eventClassifier;
	}

	public XLog getLog() {
		return log;
	}

	public void setEvenClassifier(XEventClassifier classifier) {
		eventClassifier = classifier;
	}

	public void setLog(XLog log) {
		this.log = log;

	}
}
