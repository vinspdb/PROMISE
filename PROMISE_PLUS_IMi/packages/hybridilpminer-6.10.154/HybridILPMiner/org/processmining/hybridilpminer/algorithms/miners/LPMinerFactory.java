package org.processmining.hybridilpminer.algorithms.miners;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.Pair;
import org.processmining.hybridilpminer.algorithms.decorators.HybridILPDecoratorImpl;
import org.processmining.hybridilpminer.algorithms.decorators.LPDecorator;
import org.processmining.hybridilpminer.algorithms.decorators.SequenceEncodingFilterMaxInclusionHybridILPDecoratorImpl;
import org.processmining.hybridilpminer.algorithms.decorators.SlackBasedFilterHybridILPDecoratorImpl;
import org.processmining.hybridilpminer.models.abstraction.factories.LPLogAbstractionFactory;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;
import org.processmining.hybridilpminer.models.lp.variablemapping.factories.VariableMappingFactory;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.HybridVariableMapping;
import org.processmining.hybridilpminer.models.lp.variablemapping.interfaces.SlackBasedFilterHybridVariableMapping;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.models.causalgraph.Relation;
import org.processmining.models.causalgraph.SimpleCausalGraph;
import org.processmining.plugins.log.logabstraction.LogRelations;
import org.processmining.plugins.log.logabstraction.factories.LogRelationsFactory;

import gnu.trove.set.hash.THashSet;

public class LPMinerFactory {

	private static XLogInfo logInfo;

	public static LPMiner createLPMiner(XLogHybridILPMinerParametersImpl configuration) {
		return createLPMiner(configuration, null);
	}

	public static LPMiner createLPMiner(XLogHybridILPMinerParametersImpl configuration, PluginContext context) {
		LPMiner miner = null;
		logInfo = XLogInfoFactory.createLogInfo(configuration.getLog(), configuration.getEventClassifier());
		Pair<Set<XEventClass>, Set<XEventClass>> variableDistribution = determineVariableDistribution(
				configuration.getLog(), configuration);

		LPLogAbstraction<XEventClass> logAbstraction = LPLogAbstractionFactory
				.createXEventClassBasedIntArrayAbstraction(configuration.getLog(), configuration.getEventClassifier());

		miner = createLPMiner(configuration, context, logAbstraction, variableDistribution.getFirst(),
				variableDistribution.getSecond());

		return miner;
	}

	private static LPMiner createLPMiner(XLogHybridILPMinerParametersImpl configuration, PluginContext context,
			LPLogAbstraction<XEventClass> logAbstraction, Set<XEventClass> singleVariables,
			Set<XEventClass> dualVariables) {
		LPMiner miner = null;
		switch (configuration.getFilter().getFilterType()) {
			case SLACK_VAR :
				SlackBasedFilterHybridVariableMapping<Integer, int[]> slackVarMap = VariableMappingFactory
						.createIntArrSlackBasedFilterHybridVariableMapping(configuration.getEngine(),
								logAbstraction.encode(singleVariables), logAbstraction.encode(dualVariables),
								logAbstraction);
				miner = createLPMiner(configuration.getLog(), configuration, context, logAbstraction, singleVariables,
						dualVariables, slackVarMap);

				break;
			case NONE :
			case SEQUENCE_ENCODING :
			default :
				HybridVariableMapping<Integer> hybridVarMap = VariableMappingFactory.createHybridVariableMapping(
						configuration.getEngine(), logAbstraction.encode(singleVariables),
						logAbstraction.encode(dualVariables));
				miner = createLPMiner(configuration.getLog(), configuration, context, logAbstraction, singleVariables,
						dualVariables, hybridVarMap);
				break;
		}
		return miner;
	}

	@SuppressWarnings("unchecked")
	private static <T extends HybridVariableMapping<Integer>> LPMiner createLPMiner(XLog log,
			XLogHybridILPMinerParametersImpl configuration, PluginContext context,
			LPLogAbstraction<XEventClass> logAbstraction, Set<XEventClass> singleVariables,
			Set<XEventClass> dualVariables, T varMap) {
		LPMiner miner = null;
		switch (configuration.getFilter().getFilterType()) {
			case SLACK_VAR :
				LPDecorator slackFilterBasedDecorator = new SlackBasedFilterHybridILPDecoratorImpl<SlackBasedFilterHybridVariableMapping<Integer, int[]>>(
						(SlackBasedFilterHybridVariableMapping<Integer, int[]>) varMap, configuration, logAbstraction,
						configuration.getFilter().getThreshold());
				miner = createLPMiner(log, configuration, context, logAbstraction, singleVariables, dualVariables,
						varMap, slackFilterBasedDecorator);
				break;
			case SEQUENCE_ENCODING :
				LPDecorator sequenceEncodingBasedDecorator = new SequenceEncodingFilterMaxInclusionHybridILPDecoratorImpl<HybridVariableMapping<Integer>>(
						varMap, configuration, logAbstraction, configuration.getFilter().getThreshold());
				miner = createLPMiner(log, configuration, context, logAbstraction, singleVariables, dualVariables,
						varMap, sequenceEncodingBasedDecorator);
				break;
			case NONE :
			default :
				LPDecorator conventionalDecorator = new HybridILPDecoratorImpl<HybridVariableMapping<Integer>>(varMap,
						configuration, logAbstraction);
				miner = createLPMiner(log, configuration, context, logAbstraction, singleVariables, dualVariables,
						varMap, conventionalDecorator);
				break;
		}
		return miner;
	}

	private static <T extends HybridVariableMapping<Integer>> LPMiner createLPMiner(XLog log,
			XLogHybridILPMinerParametersImpl configuration, PluginContext context,
			LPLogAbstraction<XEventClass> logAbstraction, Set<XEventClass> singleVariables,
			Set<XEventClass> dualVariables, T varMap, LPDecorator decorator) {
		LPMiner miner = null;
		Set<Pair<XEventClass, XEventClass>> pairs = new HashSet<>();
		switch (configuration.getDiscoveryStrategy().getDiscoveryStrategyType()) {
			case TRANSITION_PAIR :
			default :
				pairs = getAllEventClassPairs();
				break;
			case CAUSAL_E_VERBEEK :
				pairs = configuration.getDiscoveryStrategy().getCausalActivityGraph().getSetCausalities();
				break;
			case CAUSAL_FLEX_HEUR :
				pairs = convertCausalities(configuration.getDiscoveryStrategy().getSimpleCag());

				break;
		}
		if (!configuration.isSolve()) {
			miner = new HybridPairBasedDummyLPMiner<LPDecorator, HybridVariableMapping<Integer>, XEventClass>(varMap,
					decorator, logAbstraction, pairs, context, XConceptExtension.instance().extractName(log),
					configuration.getILPOutputLocation());
		} else {
			miner = new HybridPairBasedLPMiner<LPDecorator, HybridVariableMapping<Integer>, XEventClass>(varMap,
					decorator, logAbstraction, pairs, context);
		}
		return miner;
	}

	private static Set<Pair<XEventClass, XEventClass>> convertCausalities(SimpleCausalGraph cag) {
		Set<Pair<XEventClass, XEventClass>> pairs = new HashSet<>();
		for (Relation r : cag.getCausalRelations()) {
			pairs.add(new Pair<XEventClass, XEventClass>(r.getSource(), r.getTarget()));
		}
		return pairs;
	}

	private static Set<Pair<XEventClass, XEventClass>> getAllEventClassPairs() {
		Set<Pair<XEventClass, XEventClass>> pairs = new THashSet<>();
		for (XEventClass ec1 : logInfo.getEventClasses().getClasses()) {
			for (XEventClass ec2 : logInfo.getEventClasses().getClasses()) {
				pairs.add(new Pair<XEventClass, XEventClass>(ec1, ec2));
			}
		}
		return pairs;
	}

	private static Pair<Set<XEventClass>, Set<XEventClass>> determineVariableDistribution(XLog log,
			XLogHybridILPMinerParametersImpl configuration) {
		Set<XEventClass> singleVariables = new THashSet<>();
		Set<XEventClass> dualVariables = new THashSet<>();
		LogRelations relations = LogRelationsFactory.constructAlphaLogRelations(log, logInfo);
		switch (configuration.getLPVaraibleType()) {
			case SINGLE :
				singleVariables.addAll(relations.getEventClasses().getClasses());
				break;
			case DUAL :
				dualVariables.addAll(relations.getEventClasses().getClasses());
				break;
			case HYBRID :
				singleVariables.addAll(relations.getEventClasses().getClasses());
				for (Map.Entry<XEventClass, Double> e : relations.lengthOneLoops().entrySet()) {
					if (e.getValue() > 0) {
						singleVariables.remove(e.getKey());
						dualVariables.add(e.getKey());
					}
				}
				break;
		}
		return new Pair<>(singleVariables, dualVariables);
	}
}
