package org.processmining.hybridilpminer.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.hybridilpminer.parameters.LPFilterType;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.widgets.wizard.AbstractDialog;
import org.processmining.widgets.wizard.Dialog;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class SummaryDialogImpl extends AbstractDialog<XLogHybridILPMinerParametersImpl> {

	private static final long serialVersionUID = -402538762507286931L;

	public SummaryDialogImpl(UIPluginContext context, XLogHybridILPMinerParametersImpl parameters,
			Dialog<XLogHybridILPMinerParametersImpl> parent) {
		super(context, "ILP Based Process Discovery Configuration Overview", parameters, parent);
	}

	protected boolean canProceedToNext() {
		return true;
	}

	protected Dialog<XLogHybridILPMinerParametersImpl> determineNextDialog() {
		return null;
	}

	protected String getUserInputProblems() {
		return "";
	}

	public boolean hasNextDialog() {
		return false;
	}

	protected void printLine(GridBagConstraints c, int row, String key, String value) {
		c.gridx = 0;
		c.gridy = row;
		add(SlickerFactory.instance().createLabel(key), c);
		c.gridx = 1;
		add(SlickerFactory.instance().createLabel(value), c);
		c.gridy = c.gridx = 0;
	}

	public void updateParameters() {
		//NOP
	}

	public JComponent visualize() {
		removeAll();
		repaint();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;
		printLine(c, row++, "LP Engine: ", getParameters().getEngine().toString());
		printLine(c, row++, "Event Classifier: ", getParameters().getEventClassifier().toString());
		printLine(c, row++, "Net Class: ", getParameters().getNetClass().toString());
		printLine(c, row++, "Constraint Set: ", getParameters().getLPConstraintTypes().toString());
		printLine(c, row++, "Variable Distribution: ", getParameters().getLPVaraibleType().toString());
		printLine(c, row++, "Objective Function: ", getParameters().getObjectiveType().toString());
		printLine(c, row++, "Filter: ", getParameters().getFilter().getFilterType().toString());
		if (getParameters().getFilter().getFilterType() != LPFilterType.NONE) {
			printLine(c, row++, "Filter Threshold: ", Double.toString(getParameters().getFilter().getThreshold()));
		}
		printLine(c, row++, "Disovery Strategy: ",
				getParameters().getDiscoveryStrategy().getDiscoveryStrategyType().toString());

		if (!getParameters().isSolve()) {
			printLine(c, row++, "ILP Output Location: ", getParameters().getILPOutputLocation().toString());
		}
		return this;
	}

	public void updateParametersOnGetNext() {
		//NOP		
	}

	public void updateParametersOnGetPrevious() {
		//NOP		
	}

}
