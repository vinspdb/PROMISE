package org.processmining.hybridilpminer.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.hybridilpminer.connections.XLogHybridILPMinerParametersConnection;
import org.processmining.hybridilpminer.parameters.DiscoveryStrategyType;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.widgets.wizard.AbstractDialog;
import org.processmining.widgets.wizard.Dialog;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class ConnectionsClassifierEngineAndDefaultConfigurationDialogImpl
		extends AbstractDialog<XLogHybridILPMinerParametersImpl> {

	private static final long serialVersionUID = -6344082412432764697L;
	private ProMList<XEventClassifier> classifiersProMList = null;
	//	private JCheckBox configureMiner = null;

	private ButtonGroup configLevel = new ButtonGroup();
	private JRadioButton configExpress = SlickerFactory.instance().createRadioButton("Express");
	private JRadioButton configBasic = SlickerFactory.instance().createRadioButton("Basic");
	private JRadioButton configAdvanced = SlickerFactory.instance().createRadioButton("Advanced");

	private ProMList<LPEngine.EngineType> engineList = null;
	private final Collection<XLogHybridILPMinerParametersConnection> paramConnections;
	private final XLog log;

	private ProMList<XLogHybridILPMinerParametersImpl> predefinedParametersList = null;

	private class PredefinedConfigurationsMouseListener implements MouseListener {

		private JComponent component;

		public PredefinedConfigurationsMouseListener(JComponent component) {
			this.component = component;
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				XLogHybridILPMinerParametersImpl params;
				if ((params = getUIPredefinedParameter()) != null) {
					JOptionPane.showMessageDialog(component, params.htmlPrettyPrint(), "Parameter Details",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

	public ConnectionsClassifierEngineAndDefaultConfigurationDialogImpl(UIPluginContext context,
			Dialog<XLogHybridILPMinerParametersImpl> parent, XLog log,
			Collection<XLogHybridILPMinerParametersConnection> paramConnections) {
		super(context, "Select Previous Configurations and Classifier",
				new XLogHybridILPMinerParametersImpl(context, log), parent);
		this.paramConnections = paramConnections;
		this.log = log;
		configLevel.add(configExpress);
		configLevel.add(configBasic);
		configLevel.add(configAdvanced);
		configBasic.setSelected(true);
	}

	protected boolean canProceedToNext() {
		//		return getUIEngineType() != null && getUIClassifier() != null;
		return getUIClassifier() != null;
	}

	private ProMList<XEventClassifier> constructClassifierList(XLog log) {
		if (classifiersProMList == null) {
			DefaultListModel<XEventClassifier> classifiersList = new DefaultListModel<XEventClassifier>();
			for (XEventClassifier ec : log.getClassifiers()) {
				classifiersList.addElement(ec);
			}
			final XEventClassifier defaultClassifier = new XEventAndClassifier(new XEventNameClassifier(),
					new XEventLifeTransClassifier());
			if (classifiersList.isEmpty()) {
				classifiersList.addElement(defaultClassifier);
				getParameters().setEventClassifier(defaultClassifier);
			}
			classifiersProMList = new ProMList<XEventClassifier>("Select the desired event classifier:",
					classifiersList);
			classifiersProMList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			classifiersProMList.setPreferredSize(new Dimension(100, 150));
			if (getParameters().getEventClassifier() != null) {
				classifiersProMList.setSelection(getParameters().getEventClassifier());
			} else {
				classifiersProMList.setSelectedIndex(0);
			}
		}
		return classifiersProMList;
	}

	//	private void constructDefaultConfiguration() {
	//		if (configureMiner == null) {
	//			configureMiner = SlickerFactory.instance().createCheckBox("Detailed configuration", false);
	//		}
	//	}

	@SuppressWarnings("unused")
	private void constructEngineList() {
		if (engineList == null) {
			DefaultListModel<LPEngine.EngineType> engines = new DefaultListModel<LPEngine.EngineType>();
			for (LPEngine.EngineType engineType : EnumSet.of(LPEngine.EngineType.LPSOLVE)) {
				engines.addElement(engineType);
			}
			engineList = new ProMList<LPEngine.EngineType>("Select the desired LP-Engine:", engines);
			engineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if (getParameters().getEngine() != null) {
				engineList.setSelection(getParameters().getEngine());
			} else {
				engineList.setSelection(LPEngine.EngineType.LPSOLVE);
			}
			engineList.setPreferredSize(new Dimension(100, 150));
		}
	}

	private ProMList<XLogHybridILPMinerParametersImpl> constructPreviousParametersList(
			Collection<XLogHybridILPMinerParametersConnection> connections) {
		if (predefinedParametersList == null) {
			List<XLogHybridILPMinerParametersConnection> ordered = new ArrayList<>(connections);
			Collections.sort(ordered, new Comparator<XLogHybridILPMinerParametersConnection>() {
				public int compare(XLogHybridILPMinerParametersConnection o1,
						XLogHybridILPMinerParametersConnection o2) {
					Date d1 = new Date(
							(long) o1.getObjectWithRole(XLogHybridILPMinerParametersConnection.KEY_TIMESTAMP));
					Date d2 = new Date(
							(long) o2.getObjectWithRole(XLogHybridILPMinerParametersConnection.KEY_TIMESTAMP));
					return -1 * d1.compareTo(d2);
				}
			});
			DefaultListModel<XLogHybridILPMinerParametersImpl> list = new DefaultListModel<>();
			for (XLogHybridILPMinerParametersConnection conn : ordered) {
				XLogHybridILPMinerParametersImpl params = conn
						.getObjectWithRole(XLogHybridILPMinerParametersConnection.KEY_PARAMETERS);
				list.addElement(params);
			}
			predefinedParametersList = new ProMList<>(
					"Previously used configurations (orderd by time, descending, right click for details):", list);
			predefinedParametersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			predefinedParametersList.setPreferredSize(new Dimension(100, 150));
			predefinedParametersList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					//					constructDefaultConfiguration();
					XLogHybridILPMinerParametersImpl prevParams;
					if ((prevParams = getUIPredefinedParameter()) != null) {
						classifiersProMList.setSelection(prevParams.getEventClassifier());
						configAdvanced.setSelected(true);
						configExpress.setEnabled(false);
						configBasic.setEnabled(false);
					} else {
						configExpress.setEnabled(true);
						configBasic.setEnabled(true);
					}
					visualize();
				}
			});
			predefinedParametersList.addMouseListener(new PredefinedConfigurationsMouseListener(this));
		}
		return predefinedParametersList;
	}

	private XLogHybridILPMinerParametersImpl getUIPredefinedParameter() {
		XLogHybridILPMinerParametersImpl param = null;
		if (predefinedParametersList != null) {
			List<XLogHybridILPMinerParametersImpl> params = predefinedParametersList.getSelectedValuesList();
			if (params.size() == 1) {
				param = params.get(0);
			}
		}
		return param;
	}

	protected Dialog<XLogHybridILPMinerParametersImpl> determineNextDialog() {
		getParameters().setEventClassifier(getUIClassifier());
		if (configExpress.isSelected()) {
			setParameters(new XLogHybridILPMinerParametersImpl(getParameters().getContext(), getParameters().getLog(),
					getParameters().getEventClassifier()));
			return new SummaryDialogImpl(getUIPluginContext(), getParameters(), this);
		} else if (configBasic.isSelected()) {
			setParameters(new XLogHybridILPMinerParametersImpl(getParameters().getContext(), getParameters().getLog(),
					getParameters().getEventClassifier()));
			getParameters().getDiscoveryStrategy().setDiscoveryStrategyType(DiscoveryStrategyType.CAUSAL_E_VERBEEK);
			return new DiscoverCausalGraphDialogImpl(getUIPluginContext(), getParameters(), this);
		} else {
			return new NetClassAdditionalConstraintsObjectiveDialogImpl(getUIPluginContext(), getParameters(), this);
		}

	}

	private XEventClassifier getUIClassifier() {
		XEventClassifier result = null;
		List<XEventClassifier> selectedList = classifiersProMList.getSelectedValuesList();
		assert (selectedList.size() <= 1);
		if (!(selectedList.isEmpty())) {
			result = selectedList.get(0);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private LPEngine.EngineType getUIEngineType() {
		LPEngine.EngineType result;
		List<LPEngine.EngineType> selectedList = this.engineList.getSelectedValuesList();
		assert (selectedList.size() <= 1);
		if (selectedList.isEmpty())
			result = null;
		else
			result = selectedList.get(0);
		return result;
	}

	protected String getUserInputProblems() {
		String message = "<html><p> Please select: </p><ul>";
		//		if (getUIEngineType() == null) {
		//			message += "<li> An LP-Engine</li>";
		//		}
		if (classifiersProMList.getSelectedValuesList().isEmpty()) {
			message += "<li> An Event Classifier</li>";
		}
		message += "</ul></html>";
		return message;
	}

	public boolean hasNextDialog() {
		return true;
	}

	public void updateParameters() {
		XLogHybridILPMinerParametersImpl prevParams;
		if ((prevParams = getUIPredefinedParameter()) != null) {
			setParameters(new XLogHybridILPMinerParametersImpl(getUIPluginContext(), prevParams.getEngine(),
					prevParams.getDiscoveryStrategy(), prevParams.getNetClass(), prevParams.getLPConstraintTypes(),
					prevParams.getObjectiveType(), prevParams.getLPVaraibleType(), prevParams.getFilter(),
					prevParams.isSolve(), log, prevParams.getEventClassifier()));
		} else if (predefinedParametersList != null) {
			setParameters(new XLogHybridILPMinerParametersImpl(getUIPluginContext(), getParameters().getLog()));
		}
		if (getUIClassifier() != null) {
			getParameters().setEventClassifier(getUIClassifier());
		}
	}

	public JComponent visualize() {
		removeAll();
		if (!paramConnections.isEmpty()) {
			constructPreviousParametersList(paramConnections);
		}
		// we only support one engine...
		//		constructEngineList();
		constructClassifierList(getParameters().getLog());
		//		constructDefaultConfiguration();

		// set some layout
		this.setOpaque(false);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		// row 0, col 0
		c.gridx = c.gridy = 0;
		if (!paramConnections.isEmpty()) {
			add(predefinedParametersList, c);
			c.gridy += 1;
		}

		add(classifiersProMList, c);

		//		c.gridy += 1;
		//		add(engineList, c);

		c.gridy += 2;
		add(SlickerFactory.instance().createLabel("Select Configuration Level:"), c);
		c.gridy += 1;
		add(configExpress, c);
		c.gridy += 1;
		add(configBasic, c);
		c.gridy += 1;
		add(configAdvanced, c);

		//		add(configureMiner, c);
		revalidate();
		repaint();
		return this;
	}

	public void updateParametersOnGetNext() {
		updateParameters();

	}

	public void updateParametersOnGetPrevious() {
		if (getUIClassifier() != null)
			updateParameters();
	}

}
