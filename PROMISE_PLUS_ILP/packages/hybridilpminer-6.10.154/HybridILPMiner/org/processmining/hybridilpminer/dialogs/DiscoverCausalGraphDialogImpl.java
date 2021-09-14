package org.processmining.hybridilpminer.dialogs;

import javax.swing.JComponent;

import org.processmining.causalactivitygraphcreator.algorithms.DiscoverCausalActivityGraphAlgorithm;
import org.processmining.causalactivitygraphcreator.dialogs.DiscoverCausalActivityGraphDialog;
import org.processmining.causalactivitygraphcreator.parameters.DiscoverCausalActivityGraphParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.widgets.wizard.AbstractDialog;
import org.processmining.widgets.wizard.Dialog;

public class DiscoverCausalGraphDialogImpl extends AbstractDialog<XLogHybridILPMinerParametersImpl> {

	private static final long serialVersionUID = -9163283333637800661L;

	public static final String TITLE = "Configure Causal Graph";

	private final DiscoverCausalActivityGraphDialog dialog;
	private final DiscoverCausalActivityGraphParameters graphParams;
	private final DiscoverCausalActivityGraphAlgorithm algorithm = new DiscoverCausalActivityGraphAlgorithm();

	public DiscoverCausalGraphDialogImpl(UIPluginContext context, XLogHybridILPMinerParametersImpl parameters,
			Dialog<XLogHybridILPMinerParametersImpl> previous) {
		super(context, TITLE, parameters, previous);
		DiscoverCausalActivityGraphParameters cagParamsInParams = getParameters().getDiscoveryStrategy()
				.getCausalActivityGraphParameters();
		graphParams = cagParamsInParams == null ? new DiscoverCausalActivityGraphParameters(parameters.getLog())
				: cagParamsInParams;
		graphParams.setClassifier(parameters.getEventClassifier());
		graphParams.setShowClassifierPanel(false);
		dialog = new DiscoverCausalActivityGraphDialog(context, parameters.getLog(), graphParams);
	}

	public boolean hasNextDialog() {
		return true;
	}

	public void updateParametersOnGetNext() {
		getParameters().getDiscoveryStrategy().setCausalActivityGraphParameters(graphParams);
		getParameters().getDiscoveryStrategy()
				.setCausalActivityGraph(algorithm.apply(getUIPluginContext(), getParameters().getLog(), graphParams));

	}

	public void updateParametersOnGetPrevious() {
		updateParametersOnGetNext();
	}

	public JComponent visualize() {
		return dialog;
	}

	protected boolean canProceedToNext() {
		return true;
	}

	protected Dialog<XLogHybridILPMinerParametersImpl> determineNextDialog() {
		return getParameters().isSolve() ? new SummaryDialogImpl(getUIPluginContext(), getParameters(), this)
				: new FileBrowserDialogImpl(getUIPluginContext(), getParameters(), this);
	}

	protected String getUserInputProblems() {
		return "";
	}

}
