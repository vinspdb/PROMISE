package org.processmining.hybridilpminer.plugins;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.deckfour.uitopia.api.event.TaskListener;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.algorithms.BerthelotAlgorithm;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dataawarecnetminer.mining.classic.HeuristicsCausalGraphBuilder.HeuristicsConfig;
import org.processmining.dataawarecnetminer.mining.classic.HeuristicsCausalGraphMiner;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginQuality;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.widgets.helper.ProMUIHelper;
import org.processmining.framework.util.ui.widgets.helper.UserCancelledException;
import org.processmining.hybridilpminer.algorithms.miners.LPMiner;
import org.processmining.hybridilpminer.algorithms.miners.LPMinerFactory;
import org.processmining.hybridilpminer.connections.XLogHybridILPMinerParametersConnection;
import org.processmining.hybridilpminer.dialogs.ConnectionsClassifierEngineAndDefaultConfigurationDialogImpl;
import org.processmining.hybridilpminer.help.HybridILPMinerHelp;
import org.processmining.hybridilpminer.parameters.DiscoveryStrategyType;
import org.processmining.hybridilpminer.parameters.LPConstraintType;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.utils.XLogUtils;
import org.processmining.log.utils.XUtils;
import org.processmining.lpengines.factories.LPEngineFactory;
import org.processmining.models.causalgraph.SimpleCausalGraph;
import org.processmining.models.causalgraph.XEventClassifierAwareSimpleCausalGraph;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.parameters.BerthelotParameters;
import org.processmining.widgets.wizard.Dialog;
import org.processmining.widgets.wizard.Wizard;
import org.processmining.widgets.wizard.WizardResult;

@Plugin(name = "ILP-Based Process Discovery", level = PluginLevel.PeerReviewed, quality = PluginQuality.VeryGood, parameterLabels = {
		"Event log", "ILP Miner Configuration", "Classifier", "Causal Graph" }, returnLabels = { "Petri net",
				"Initial Marking", "Final Marking" }, returnTypes = { Petrinet.class, Marking.class,
						Marking.class }, help = HybridILPMinerHelp.TEXT, categories = { PluginCategory.Discovery })
public class HybridILPMinerPlugin {

	@UITopiaVariant(affiliation = "Eindhoven University of Technology", author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl")
	@PluginVariant(variantLabel = "ILP-Based Process Discovery", requiredParameterLabels = { 0 })
	public static Object[] applyDetailed(final UIPluginContext context, final XLog log) {
		Object[] result = null;
		Collection<XLogHybridILPMinerParametersConnection> connections = new HashSet<>();
		try {
			connections = context.getConnectionManager().getConnections(XLogHybridILPMinerParametersConnection.class,
					context, log);
		} catch (ConnectionCannotBeObtained e) {
		}
		final String startLabel = "[start>@" + System.currentTimeMillis();
		final String endLabel = "[end]@" + System.currentTimeMillis();
		XLog artifLog = XLogUtils.addArtificialStartAndEnd(log, startLabel, endLabel);
		Dialog<XLogHybridILPMinerParametersImpl> firstDialog = new ConnectionsClassifierEngineAndDefaultConfigurationDialogImpl(
				context, null, artifLog, connections);
		WizardResult<XLogHybridILPMinerParametersImpl> wizardResult = Wizard.show(context, firstDialog);
		if (wizardResult.getInteractionResult().equals(TaskListener.InteractionResult.FINISHED)) {
			XLogHybridILPMinerParametersImpl params = wizardResult.getParameters();
			result = discoverWithArtificialStartEnd(context, log, artifLog, params);
			if (!params.getDiscoveryStrategy().getDiscoveryStrategyType()
					.equals(DiscoveryStrategyType.CAUSAL_FLEX_HEUR)) {
				Connection paramsConnection = new XLogHybridILPMinerParametersConnection(log, params);
				context.getConnectionManager().addConnection(paramsConnection);
			}
		} else if (wizardResult.getInteractionResult().equals(TaskListener.InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		return result;
	}

	@PluginVariant(variantLabel = "ILP-Based Process Discovery (Express)", requiredParameterLabels = { 0, 2 })
	public static Object[] applyExpress(PluginContext context, XLog log, XEventClassifier classifier) {
		return applyExpress(context, log, classifier, null);
	}
	
	public static Object[] applyExpress(PluginContext context, XLog log, XEventClassifier classifier,
			XLogHybridILPMinerParametersImpl parameters) {
		context.getProgress().setMinimum(0);
		if (parameters == null) {
			parameters = new XLogHybridILPMinerParametersImpl(context, log);
			context.log("ILP Based Discovery Using Express Settings");
			context.getProgress().setMaximum(4);
			context.getProgress().setValue(0);
			//		context.getProgress().setIndeterminate(true);

			context.log("Set Log");
			parameters.setLog(log);
			context.getProgress().inc();

			context.log("Set Classifier");
			parameters.setEventClassifier(classifier);
			context.getProgress().inc();
		} else {
			context.getProgress().setMaximum(0);
			context.getProgress().setMaximum(2);
			context.getProgress().setValue(0);
		}
		parameters.setApplyStructuralRedundantPlaceRemoval(true);

		context.log("Setup Heuristics Graph Configuration");
		HeuristicsConfig heuristicsConfig = new HeuristicsConfig();
		heuristicsConfig.setAllTasksConnected(true);
		HeuristicsCausalGraphMiner miner = new HeuristicsCausalGraphMiner(log, parameters.getEventClassifier());
		miner.setHeuristicsConfig(heuristicsConfig);

		context.getProgress().inc();
		context.log("Discover Causal Graph");
		SimpleCausalGraph scag = miner.mineCausalGraph();
		context.getProgress().inc();
		return applyFlexHeur(context, log, XEventClassifierAwareSimpleCausalGraph.Factory.construct(
				parameters.getEventClassifier(), scag.getSetActivities(), scag.getCausalRelations()), parameters);
	}

	@UITopiaVariant(affiliation = "Eindhoven University of Technology", author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl", uiLabel = UITopiaVariant.USEVARIANT)
	@PluginVariant(variantLabel = "ILP-Based Process Discovery (Express)", requiredParameterLabels = { 0 })
	public static Object[] applyExpress(UIPluginContext context, XLog log) {
		try {
			XEventClassifier classifier = ProMUIHelper.queryForObject(context, "Select the event classifier to be used",
					XUtils.getStandardAndLogDefinedEventClassifiers(log));
			return applyExpress(context, log, classifier, null);
		} catch (UserCancelledException e) {
			context.getFutureResult(0).cancel(true);
			return null;
		}
	}

	@UITopiaVariant(affiliation = "Eindhoven University of Technology", author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl", uiLabel = UITopiaVariant.USEVARIANT)
	@PluginVariant(variantLabel = "ILP-Based Process Discovery (Express)", requiredParameterLabels = { 0, 3 })
	public static Object[] applyFlexHeur(PluginContext context, XLog log, XEventClassifierAwareSimpleCausalGraph cag) {
		XLogHybridILPMinerParametersImpl params = new XLogHybridILPMinerParametersImpl(context);
		return applyFlexHeur(context, log, cag, params);
	}

	public static Object[] applyFlexHeur(PluginContext context, XLog log, XEventClassifierAwareSimpleCausalGraph cag,
			XLogHybridILPMinerParametersImpl params) {
		params.setEventClassifier(cag.getEventClassifier());
		params.getDiscoveryStrategy().setDiscoveryStrategyType(DiscoveryStrategyType.CAUSAL_FLEX_HEUR);
		params.getDiscoveryStrategy().setSimpleCag(cag);
		final String artiStart = "ARTIFICIAL_START";
		final String artiEnd = "ARTIFICIAL_END";
		XLog artificial = XLogUtils.addArtificialStartAndEnd(log, artiStart, artiEnd);
		params.setLog(artificial);
		return discoverWithArtificialStartEnd(context, log, artificial, params);
	}

	@UITopiaVariant(affiliation = "Eindhoven University of Technology", author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl")
	@PluginVariant(variantLabel = "ILP-Based Process Discovery, Log and Configuration", requiredParameterLabels = { 0,
			1 })
	public static Object[] applyParams(final PluginContext context, final XLog log,
			final XLogHybridILPMinerParametersImpl parameters) {
		final String startLabel = "[start>@" + System.currentTimeMillis();
		final String endLabel = "[end]@" + System.currentTimeMillis();
		XLog artifLog = XLogUtils.addArtificialStartAndEnd(log, startLabel, endLabel);
		parameters.setLog(artifLog);
		return discoverWithArtificialStartEnd(context, log, artifLog, parameters);
	}

	public static Object[] discover(LPMiner miner, final XLog inputLog,
			final XLogHybridILPMinerParametersImpl parameters, final String artificStartLabel,
			final String ArtificEndLabel, final boolean removeRedundant) {
		miner.run();
		Pair<Petrinet, Marking> netAndMarking = miner.synthesizeNet();
		Object[] result;
		result = processPetriNet(netAndMarking.getFirst(), artificStartLabel, ArtificEndLabel, parameters.isFindSink(),
				removeRedundant);
		if (!parameters.getLPConstraintTypes().contains(LPConstraintType.EMPTY_AFTER_COMPLETION)) {
			result[2] = null;
		}
		return result;
	}

	public static Object[] discoverWithArtificialStartEnd(final PluginContext context, final XLog originalLog,
			final XLog artificialLog, final XLogHybridILPMinerParametersImpl parameters) {
		if (parameters.getDiscoveryStrategy().getDiscoveryStrategyType().equals(DiscoveryStrategyType.CAUSAL_FLEX_HEUR)
				&& parameters.getDiscoveryStrategy().getSimpleCag() == null) {
			return applyExpress(context, originalLog, parameters.getEventClassifier(), parameters);
		}
		String artificStartLabel = parameters.getEventClassifier().getClassIdentity(artificialLog.get(0).get(0));
		String artificEndLabel = parameters.getEventClassifier()
				.getClassIdentity(artificialLog.get(0).get(artificialLog.get(0).size() - 1));
		return discoverWithArtificialStartEnd(context, artificialLog, parameters, artificStartLabel, artificEndLabel);
	}

	public static Object[] discoverWithArtificialStartEnd(final PluginContext context, final XLog log,
			final XLogHybridILPMinerParametersImpl parameters, final String artificStartLabel,
			final String artificEndLabel) {
		context.log("Establishing connection to selected LP-Engine");
		// establish some engineType connection.
		LPEngineFactory.createLPEngine(parameters.getEngine());
		context.log("Connected to Engine");
		LPMiner miner = LPMinerFactory.createLPMiner(parameters, context);
		Object[] result = discover(miner, log, parameters, artificStartLabel, artificEndLabel,
				parameters.isApplyStructuralRedundantPlaceRemoval());
		Connection iMarking = new InitialMarkingConnection((Petrinet) result[0], (Marking) result[1]);
		context.getConnectionManager().addConnection(iMarking);
		if (parameters.getLPConstraintTypes().contains(LPConstraintType.EMPTY_AFTER_COMPLETION)) {
			Connection finalMarking = new FinalMarkingConnection((Petrinet) result[0], (Marking) result[2]);
			context.getConnectionManager().addConnection(finalMarking);
		}
		return result;
	}

	private static Object[] processPetriNet(Petrinet net, final String startName, final String endName,
			boolean findSink, final boolean removeRedundant) {
		Place ini = null, fin = null, unconnected = null;
		Transition start = null, end = null;
		Iterator<Transition> trit = net.getTransitions().iterator();
		Set<Transition> remove = new HashSet<>();
		while (trit.hasNext()) {
			Transition t = trit.next();
			if (t.getLabel().equals(startName)) {
				t.setInvisible(true);
				start = t;
				ini = net.addPlace("source");
				net.addArc(ini, t);
			} else if (t.getLabel().equals(endName)) {
				t.setInvisible(true);
				end = t;
				if (findSink) {
					fin = net.addPlace("sink");
					net.addArc(t, fin);
				}
			} else {
				if (net.getInEdges(t).isEmpty() && net.getOutEdges(t).isEmpty()) {
					if (unconnected == null) {
						unconnected = net.addPlace("p" + net.getPlaces().size());
					}
					net.addArc(unconnected, t);
					net.addArc(t, unconnected);
				}
			}
		}
		if (unconnected != null) {
			net.addArc(start, unconnected);
			net.addArc(unconnected, end);
		}
		for (Transition t : remove) {
			net.removeTransition(t);
		}

		Marking initialMarking = new Marking(Collections.singleton(ini));
		Marking finalMarking = findSink ? new Marking(Collections.singleton(fin)) : new Marking();
		if (findSink && removeRedundant) {
			BerthelotParameters berthelotParameters = new BerthelotParameters();
			berthelotParameters.setInitialMarking(initialMarking);
			berthelotParameters.setFinalMarkings(Collections.singleton(finalMarking));
			BerthelotAlgorithm berthelotAlgorithm = new BerthelotAlgorithm();
			net = berthelotAlgorithm.apply(null, net, berthelotParameters);
			initialMarking = berthelotParameters.getInitialBerthelotMarking();
			finalMarking = berthelotParameters.getFinalBerthelotMarkings().iterator().next();
		}
		return new Object[] { net, initialMarking, finalMarking };

	}

}
