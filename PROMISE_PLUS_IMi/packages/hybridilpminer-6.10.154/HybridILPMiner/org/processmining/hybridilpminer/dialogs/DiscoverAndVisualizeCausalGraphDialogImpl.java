package org.processmining.hybridilpminer.dialogs;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;

import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitygraph.plugins.VisualizeCausalActivityGraphPlugin;
import org.processmining.causalactivitygraphcreator.parameters.ConvertCausalActivityMatrixToCausalActivityGraphParameters;
import org.processmining.causalactivitygraphcreator.plugins.ConvertCausalActivityMatrixToCausalActivityGraphPlugin;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.widgets.wizard.AbstractDialog;
import org.processmining.widgets.wizard.Dialog;

public class DiscoverAndVisualizeCausalGraphDialogImpl extends AbstractDialog<XLogHybridILPMinerParametersImpl> {

	private static final long serialVersionUID = 5431198259441453708L;

	private CausalActivityGraph causalActivityGraph;
	ConvertCausalActivityMatrixToCausalActivityGraphParameters causalGraphParameters;
	private ConvertCausalActivityMatrixToCausalActivityGraphPlugin causalGraphPlugin;
	private JComponent internalDialog;
	CausalActivityMatrix matrix;
	
	public DiscoverAndVisualizeCausalGraphDialogImpl(UIPluginContext context,
			Dialog<XLogHybridILPMinerParametersImpl> parent, XLogHybridILPMinerParametersImpl parameters,
			CausalActivityMatrix matrix,
			ConvertCausalActivityMatrixToCausalActivityGraphParameters causalGraphParameters) {
		super(context, "Causal Strategy - Causal Graph", parameters, parent);
		this.matrix = matrix;
		this.causalGraphParameters = causalGraphParameters;
	}

	protected boolean canProceedToNext() {
		return true;
	}

	private JComponent createCausalActivityGraphVisualization() {
		VisualizeCausalActivityGraphPlugin visualizer = new VisualizeCausalActivityGraphPlugin();
		return visualizer.apply(getUIPluginContext().createChildContext(""), this.causalActivityGraph);
	}

	protected Dialog<XLogHybridILPMinerParametersImpl> determineNextDialog() {
		return getParameters().isSolve() ? new SummaryDialogImpl(getUIPluginContext(), getParameters(), this)
				: new FileBrowserDialogImpl(getUIPluginContext(), getParameters(), this);

	}

	protected String getUserInputProblems() {
		return null;
	}

	public boolean hasNextDialog() {
		return true;
	}

	public void updateParameters() {
		//NOP
	}

	public JComponent visualize() {
		causalGraphPlugin = new ConvertCausalActivityMatrixToCausalActivityGraphPlugin();
		causalActivityGraph = causalGraphPlugin.run(getUIPluginContext().createChildContext(""), matrix,
				causalGraphParameters);
		getParameters().getDiscoveryStrategy().setCausalActivityGraph(causalActivityGraph);
		removeAll();
		internalDialog = null;
		internalDialog = createCausalActivityGraphVisualization();
		internalDialog.setSize(new Dimension(1024, 768));
		internalDialog.setOpaque(false);
		setLayout(new GridLayout(0, 1));
		add(internalDialog);
		return this;
	}

	public void updateParametersOnGetNext() {
		updateParameters();
	}

	public void updateParametersOnGetPrevious() {
		updateParameters();
	}
}
