package org.processmining.hybridilpminer.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.EnumSet;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.hybridilpminer.parameters.DiscoveryStrategyType;
import org.processmining.hybridilpminer.parameters.LPFilterType;
import org.processmining.hybridilpminer.parameters.LPVariableType;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.widgets.wizard.AbstractDialog;
import org.processmining.widgets.wizard.Dialog;

public class VariableDistributionFilterAndStrategyDialogImpl extends AbstractDialog<XLogHybridILPMinerParametersImpl> {

	private class FilterSliderListner implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				LPFilterType newFilter = getUIFilter();
				if (newFilter != null) {
					switch (newFilter) {
						case NONE :
							currentThresholdPanel = noneThresholdPanel;
							break;
						case SEQUENCE_ENCODING :
							currentThresholdPanel = paThresholdPanel;
							break;
						case SLACK_VAR :
							currentThresholdPanel = slackThresholdPanel;
							break;
					}
					setupThresholdPanelVisualizer();
					validate();
					updateUI();
					repaint();
					setVisible(true);
					visualize();
				}
			}
		}
	}

	private static final long serialVersionUID = -714401031503526091L;

	private LPMinerFilterDoubleSliderPanel currentThresholdPanel = null;
	private JPanel currentThresholdPanelVisualizer = new JPanel();
	private ProMList<LPFilterType> filters = null;
	private ProMList<DiscoveryStrategyType> miningStrategy = null;

	private LPMinerFilterDoubleSliderPanel noneThresholdPanel = new LPMinerFilterDoubleSliderPanel("No Filter Selected",
			"", 0, 1, 0, false);

	private LPMinerFilterDoubleSliderPanel paThresholdPanel = new LPMinerFilterDoubleSliderPanel(
			"Sequence Encoding Filter Threshold", LPFilterType.SEQUENCE_ENCODING.getThresholdDescription(), 0, 1,
			LPFilterType.SEQUENCE_ENCODING.getDefaultThreshold(), true);

	private LPMinerFilterDoubleSliderPanel slackThresholdPanel = new LPMinerFilterDoubleSliderPanel(
			"Slack Variable Filter Threshold", LPFilterType.SLACK_VAR.getThresholdDescription(), 0, 1,
			LPFilterType.SLACK_VAR.getDefaultThreshold(), true);

	private ProMList<LPVariableType> variableTypes;

	public VariableDistributionFilterAndStrategyDialogImpl(UIPluginContext context,
			XLogHybridILPMinerParametersImpl parameters, Dialog<XLogHybridILPMinerParametersImpl> parent) {
		super(context, "Select Variable Distribution, Filtering and Discovery Strategy", parameters, parent);
		if (getParameters().getDiscoveryStrategy().getDiscoveryStrategyType()
				.equals(DiscoveryStrategyType.CAUSAL_FLEX_HEUR)) {
			getParameters().getDiscoveryStrategy().setDiscoveryStrategyType(DiscoveryStrategyType.CAUSAL_E_VERBEEK);
		}
	}

	protected boolean canProceedToNext() {
		return getDiscoveryStrategyUI() != null && getUIVariableType() != null && getUIFilter() != null;
	}

	protected Dialog<XLogHybridILPMinerParametersImpl> determineNextDialog() {
		switch (getParameters().getDiscoveryStrategy().getDiscoveryStrategyType()) {
			case CAUSAL_E_VERBEEK :
				return new DiscoverCausalGraphDialogImpl(getUIPluginContext(), getParameters(), this);
			case TRANSITION_PAIR :
			default :
				return new SummaryDialogImpl(getUIPluginContext(), getParameters(), this);
		}
	}

	private DiscoveryStrategyType getDiscoveryStrategyUI() {
		DiscoveryStrategyType result = null;
		assert (miningStrategy.getSelectedValuesList().size() <= 1);
		if (!miningStrategy.getSelectedValuesList().isEmpty())
			result = miningStrategy.getSelectedValuesList().get(0);
		return result;
	}

	private LPFilterType getUIFilter() {
		LPFilterType result;
		java.util.List<LPFilterType> selectedList = filters.getSelectedValuesList();
		assert (selectedList.size() <= 1);
		if (selectedList.isEmpty())
			result = null;
		else
			result = selectedList.get(0);
		return result;
	}

	private LPVariableType getUIVariableType() {
		assert (this.variableTypes.getSelectedValuesList().size() <= 1);
		LPVariableType result = null;
		if (!(this.variableTypes.getSelectedValuesList().isEmpty()))
			result = this.variableTypes.getSelectedValuesList().get(0);
		return result;
	}

	protected String getUserInputProblems() {
		String message = "<html><p> Please select: </p><ul>";
		message += getUIVariableType() == null ? "<li> A distribution of the ILP variables</li>" : "";
		message += getUIFilter() == null ? "<li> A filter </li>" : "";
		message += getDiscoveryStrategyUI() == null ? "<li> A Mining strategy</li>" : "";
		message += "</ul></html>";
		return message;
	}

	public boolean hasNextDialog() {
		return true;
	}

	private void setupDiscoveryStrategy() {
		if (miningStrategy == null) {
			DefaultListModel<DiscoveryStrategyType> listModel = new DefaultListModel<DiscoveryStrategyType>();
			for (DiscoveryStrategyType strat : EnumSet.of(DiscoveryStrategyType.TRANSITION_PAIR,
					DiscoveryStrategyType.CAUSAL_E_VERBEEK)) {
				listModel.addElement(strat);
			}
			miningStrategy = new ProMList<DiscoveryStrategyType>("Discovery strategy", listModel);
			miningStrategy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if (getParameters().getDiscoveryStrategy() != null) {
				miningStrategy.setSelection(getParameters().getDiscoveryStrategy().getDiscoveryStrategyType());
			} else {
				miningStrategy.setSelection(DiscoveryStrategyType.CAUSAL_E_VERBEEK);
			}
			miningStrategy.setPreferredSize(new Dimension(100, 150));
		}
	}

	private void setupFiltersUI() {
		if (filters == null) {
			DefaultListModel<LPFilterType> listModel = new DefaultListModel<LPFilterType>();
			for (LPFilterType filter : EnumSet.allOf(LPFilterType.class)) {
				listModel.addElement(filter);
			}
			filters = new ProMList<LPFilterType>("LP-Filter", listModel);
			filters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			filters.setPreferredSize(new Dimension(50, 150));

			if (getParameters().getFilter() == null) {
				filters.setSelection(LPFilterType.NONE);
			}
			switch (getParameters().getFilter().getFilterType()) {
				case NONE :
					currentThresholdPanel = noneThresholdPanel;
					break;
				case SEQUENCE_ENCODING :
					currentThresholdPanel = new LPMinerFilterDoubleSliderPanel("Sequence Encoding Filter Threshold",
							LPFilterType.SEQUENCE_ENCODING.getThresholdDescription(), 0, 1,
							getParameters().getFilter().getThreshold(), true);
					break;
				case SLACK_VAR :
					currentThresholdPanel = new LPMinerFilterDoubleSliderPanel("Slack Variable Filter Threshold",
							LPFilterType.SLACK_VAR.getThresholdDescription(), 0, 1,
							getParameters().getFilter().getThreshold(), true);
			}
			filters.setSelection(getParameters().getFilter().getFilterType());
			filters.addListSelectionListener(new FilterSliderListner());
		}
	}

	private void setupThresholdPanelVisualizer() {
		currentThresholdPanelVisualizer.removeAll();
		currentThresholdPanelVisualizer.setOpaque(false);
		currentThresholdPanelVisualizer.setLayout(new GridLayout(0, 1));
		currentThresholdPanelVisualizer.add(currentThresholdPanel);
		currentThresholdPanelVisualizer.validate();
		currentThresholdPanelVisualizer.updateUI();
		currentThresholdPanelVisualizer.repaint();
		currentThresholdPanelVisualizer.setVisible(true);
		currentThresholdPanelVisualizer.validate();
		currentThresholdPanel.updateUI();
		currentThresholdPanel.repaint();
		currentThresholdPanel.setVisible(true);
	}

	private void setupVariableTypes() {
		if (variableTypes == null) {
			DefaultListModel<LPVariableType> listModel = new DefaultListModel<LPVariableType>();
			for (LPVariableType varType : EnumSet.allOf(LPVariableType.class)) {
				listModel.addElement(varType);
			}
			variableTypes = new ProMList<LPVariableType>("LP Variable type", listModel);
			variableTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if (getParameters().getLPVaraibleType() != null) {
				variableTypes.setSelection(getParameters().getLPVaraibleType());
			} else {
				variableTypes.setSelection(LPVariableType.DUAL);
			}
			variableTypes.setPreferredSize(new Dimension(100, 150));
		}
	}

	public void updateParameters() {
		if (getDiscoveryStrategyUI() != null) {
			getParameters().setVariableType(getUIVariableType());
		}
		if (getUIVariableType() != null) {
			getParameters().getDiscoveryStrategy().setDiscoveryStrategyType(getDiscoveryStrategyUI());
		}
		if (getUIFilter() != null) {
			getParameters().getFilter().setFilterType(getUIFilter());
			switch (getParameters().getFilter().getFilterType()) {
				case SEQUENCE_ENCODING :
					getParameters().getFilter().setThreshold(currentThresholdPanel.getValue());
					break;
				case SLACK_VAR :
					getParameters().getFilter().setThreshold(currentThresholdPanel.getValue());
					break;
				default :
				case NONE :
					// NOP
					break;
			}
		}
	}

	public void updateParametersOnGetNext() {
		updateParameters();
	}

	public void updateParametersOnGetPrevious() {
		updateParameters();
	}

	public JComponent visualize() {
		removeAll();
		setupVariableTypes();
		setupFiltersUI();
		setupDiscoveryStrategy();
		setupThresholdPanelVisualizer();
		setOpaque(false);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;

		c.gridy = c.gridx = 0;
		add(filters, c);

		if (currentThresholdPanel != noneThresholdPanel) {
			c.gridx = 1;
			add(currentThresholdPanelVisualizer, c);
		}

		c.gridx = 0;
		c.gridy += 1;
		if (currentThresholdPanel != noneThresholdPanel) {
			c.gridwidth = 2;
		}
		add(variableTypes, c);

		c.gridy += 1;
		add(miningStrategy, c);
		revalidate();
		repaint();
		return this;
	}

}
