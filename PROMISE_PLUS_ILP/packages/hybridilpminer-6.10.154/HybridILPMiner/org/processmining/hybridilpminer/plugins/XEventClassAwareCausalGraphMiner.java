package org.processmining.hybridilpminer.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dataawarecnetminer.mining.classic.HeuristicsCausalGraphBuilder.HeuristicsConfig;
import org.processmining.dataawarecnetminer.mining.classic.HeuristicsCausalGraphMiner;
import org.processmining.dataawarecnetminer.visualization.CausalGraphConfigurationPanel;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.util.ui.widgets.helper.ProMUIHelper;
import org.processmining.framework.util.ui.widgets.helper.UserCancelledException;
import org.processmining.log.utils.XUtils;
import org.processmining.models.causalgraph.SimpleCausalGraph;
import org.processmining.models.causalgraph.XEventClassifierAwareSimpleCausalGraph;
import org.processmining.plugins.utils.ProvidedObjectHelper;

/**
 * Discover a {@link SimpleCausalGraph} based on the heuristics described in the
 * FHM paper:
 * 
 * <pre>
 * Weijters, A. J. M. M., and J. T. S. Ribeiro. "Flexible
 * heuristics miner (FHM)." Computational Intelligence and Data Mining (CIDM),
 * 2011 IEEE Symposium on. IEEE, 2011.
 * </pre>
 * 
 * @author F. Mannhardt
 * @author S.J. van Zelst
 *
 */
public class XEventClassAwareCausalGraphMiner {

	@Plugin(name = "Mine Causal Graph (Heuristic Miner, Event Classifier Aware)", parameterLabels = { "Event log" }, //
			level = PluginLevel.Regular, returnLabels = { "Causal Graph" }, returnTypes = {
					XEventClassifierAwareSimpleCausalGraph.class }, userAccessible = true, mostSignificantResult = 1, help = "Discover a SimpleCausalGraph based on the heuristics described in the FHM paper: \r\n"
							+ " Weijters, A. J. M. M., and J. T. S. Ribeiro. \"Flexible\r\n"
							+ " heuristics miner (FHM).\" Computational Intelligence and Data Mining (CIDM),\r\n"
							+ " 2011 IEEE Symposium on. IEEE, 2011.\r\n" + "")
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = " F. Mannhardt, S.J. van Zelst", email = "s.j.v.zelst@tue.nl")
	public XEventClassifierAwareSimpleCausalGraph mineCausalGraph(UIPluginContext context, XLog log)
			throws UserCancelledException {

		XEventClassifier classifier = ProMUIHelper.queryForObject(context, "Select the event classifier to be used",
				XUtils.getStandardAndLogDefinedEventClassifiers(log));

		HeuristicsConfig heuristicsConfig = new HeuristicsConfig();

		CausalGraphConfigurationPanel configPanel = new CausalGraphConfigurationPanel(heuristicsConfig);
		InteractionResult result = context.showConfiguration("Configure Heuristics", configPanel);
		if (result != InteractionResult.CANCEL) {
			context.getFutureResult(0)
					.setLabel("Causal Graph based on " + ProvidedObjectHelper.getProvidedObjectLabel(context, log));
			return doMineCausalGraph(log, classifier, heuristicsConfig);
		} else {
			context.getFutureResult(0).cancel(true);
			return null;
		}

	}

	public static XEventClassifierAwareSimpleCausalGraph doMineCausalGraph(XLog log, XEventClassifier classifier,
			HeuristicsConfig heuristicsConfig) {
		HeuristicsCausalGraphMiner miner = new HeuristicsCausalGraphMiner(log, classifier);
		miner.setHeuristicsConfig(heuristicsConfig);
		SimpleCausalGraph scag = miner.mineCausalGraph();
		return XEventClassifierAwareSimpleCausalGraph.Factory.construct(classifier, scag.getSetActivities(),
				scag.getCausalRelations());
	}

}
