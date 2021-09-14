package org.processmining.hybridilpminer.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.widgets.wizard.AbstractDialog;
import org.processmining.widgets.wizard.Dialog;

public class FileBrowserDialogImpl extends AbstractDialog<XLogHybridILPMinerParametersImpl> {

	private static final long serialVersionUID = -8998122913887843605L;
	private JFileChooser fileChooser = new JFileChooser();

	public FileBrowserDialogImpl(UIPluginContext context, XLogHybridILPMinerParametersImpl parameters,
			Dialog<XLogHybridILPMinerParametersImpl> parent) {
		super(context, "Target Folder for LP Export", parameters, parent);
	}

	protected boolean canProceedToNext() {
		return true;
	}

	protected Dialog<XLogHybridILPMinerParametersImpl> determineNextDialog() {
		return new SummaryDialogImpl(getUIPluginContext(), getParameters(), this);
	}

	protected String getUserInputProblems() {
		return "";
	}

	public boolean hasNextDialog() {
		return true;
	}

	public void updateParameters() {
		getParameters().setILPOutputLocation(fileChooser.getCurrentDirectory());
	}

	public JComponent visualize() {
		removeAll();
		repaint();

		// set some layout
		this.setOpaque(true);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		// row 0, col 0
		c.gridx = c.gridy = 0;
		this.add(fileChooser, c);
		return this;
	}

	public void updateParametersOnGetNext() {
		updateParameters();
	}

	public void updateParametersOnGetPrevious() {
		updateParameters();
	}
}
