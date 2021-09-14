package org.processmining.hybridilpminer.models.lp.configuration.factories;

import java.util.Collection;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.hybridilpminer.models.lp.configuration.implementations.LPMinerConfigurationImpl;
import org.processmining.hybridilpminer.models.lp.configuration.implementations.XLogLPMinerConfigurationImpl;
import org.processmining.hybridilpminer.models.lp.configuration.interfaces.LPMinerConfiguration;
import org.processmining.hybridilpminer.models.lp.configuration.interfaces.XLogLPMinerConfiguration;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.DiscoveryStrategy;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPConstraintType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPFilter;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPObjectiveType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPVariableType;
import org.processmining.lpengines.interfaces.LPEngine;

@Deprecated
public class LPMinerConfigurationFactory {

	public static LPMinerConfiguration createDefaultConfiguration() {
		return new LPMinerConfigurationImpl();
	}

	public static LPMinerConfiguration createDefaultDummyConfiguration() {
		LPMinerConfiguration result = createDefaultConfiguration();
		result.setIsSolve(true);
		return result;
	}

	public static LPMinerConfiguration customConfiguration(XLog log, LPEngine.EngineType engineType,
			XEventClassifier classifier, DiscoveryStrategy discoveryStrategy, Collection<LPConstraintType> constraints,
			LPObjectiveType objectiveType, LPVariableType variableType, LPFilter filter, boolean dummy) {
		return new LPMinerConfigurationImpl(log, engineType, classifier, discoveryStrategy, constraints, objectiveType,
				variableType, filter, dummy);
	}

	public static XLogLPMinerConfiguration createDefaultConfiguration(PluginContext context,
			LPEngine.EngineType engineType, XLog log, XEventClassifier classifier, boolean solve) {
		return new XLogLPMinerConfigurationImpl(context, engineType, log, classifier, solve);
	}

	public static XLogLPMinerConfiguration createDefaultConfiguration(PluginContext context, XLog log, boolean solve) {
		return new XLogLPMinerConfigurationImpl(context, log, solve);
	}
}
