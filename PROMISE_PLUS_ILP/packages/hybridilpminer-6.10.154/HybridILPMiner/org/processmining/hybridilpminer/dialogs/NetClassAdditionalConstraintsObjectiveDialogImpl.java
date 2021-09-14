package org.processmining.hybridilpminer.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.hybridilpminer.parameters.LPConstraintType;
import org.processmining.hybridilpminer.parameters.LPObjectiveType;
import org.processmining.hybridilpminer.parameters.NetClass;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.widgets.wizard.AbstractDialog;
import org.processmining.widgets.wizard.Dialog;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import gnu.trove.set.hash.THashSet;

public class NetClassAdditionalConstraintsObjectiveDialogImpl extends AbstractDialog<XLogHybridILPMinerParametersImpl> {

	private static final long serialVersionUID = 7548788048916610587L;

	private ProMList<NetClass> netClassList = null;
	private ProMList<LPObjectiveType> objectiveTypes = null;
	private ProMList<LPConstraintType> optionalConstraints = null;
	private JCheckBox findSink;

	public NetClassAdditionalConstraintsObjectiveDialogImpl(UIPluginContext context,
			XLogHybridILPMinerParametersImpl parameters, Dialog<XLogHybridILPMinerParametersImpl> parent) {
		super(context, "Select Net Class, Additional Constraints and Objective", parameters, parent);
		findSink = SlickerFactory.instance().createCheckBox("Add Sink Place", getParameters().isFindSink());
	}

	protected boolean canProceedToNext() {
		return getUINetClass() != null && getUIObjectiveType() != null;
	}

	protected void constructNetClassList() {
		if (netClassList == null) {
			DefaultListModel<NetClass> netClasses = new DefaultListModel<NetClass>();
			for (NetClass netClass : EnumSet.allOf(NetClass.class)) {
				netClasses.addElement(netClass);
			}
			netClassList = new ProMList<NetClass>("Select the desired resulting net class:", netClasses);
			netClassList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if (getParameters().getNetClass() != null) {
				netClassList.setSelection(getParameters().getNetClass());
			} else {
				netClassList.setSelection(NetClass.PT_NET);
			}
			netClassList.setPreferredSize(new Dimension(100, 150));

			netClassList.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (getUINetClass() != null) {
						NetClass newNetClass = getUINetClass();
						Collection<LPConstraintType> currentSelectedOptionalConstraints = new HashSet<>(
								getUIOptionalConstraints());
						setupOptionalConstraints(newNetClass, currentSelectedOptionalConstraints, true);
						visualize();
					}
				}
			});
		}
	}

	protected Dialog<XLogHybridILPMinerParametersImpl> determineNextDialog() {
		return new VariableDistributionFilterAndStrategyDialogImpl(getUIPluginContext(), getParameters(), this);
	}

	private NetClass getUINetClass() {
		NetClass result;
		List<NetClass> selectedList = this.netClassList.getSelectedValuesList();
		assert (selectedList.size() <= 1);
		if (selectedList.isEmpty())
			result = null;
		else
			result = selectedList.get(0);
		return result;
	}

	private LPObjectiveType getUIObjectiveType() {
		assert (this.objectiveTypes.getSelectedValuesList().size() <= 1);
		LPObjectiveType result = null;
		if (!(this.objectiveTypes.getSelectedValuesList().isEmpty()))
			result = this.objectiveTypes.getSelectedValuesList().get(0);
		return result;
	}

	private Collection<LPConstraintType> getUIOptionalConstraints() {
		Collection<LPConstraintType> result = new HashSet<LPConstraintType>();
		for (LPConstraintType constr : this.optionalConstraints.getSelectedValuesList()) {
			result.add(constr);
		}
		return result;
	}

	protected String getUserInputProblems() {
		String message = "<html><p> Please select: </p><ul>";
		if (getUINetClass() == null) {
			message += "<li> A Petri net class.</li>";
		}
		if (getUIObjectiveType() == null) {
			message += "<li> An objective function.</li>";
		}
		message += "</ul></html>";
		return message;
	}

	public boolean hasNextDialog() {
		return true;
	}

	protected void refreshConstraints() {
		getParameters().getLPConstraintTypes().clear();
		getParameters().getLPConstraintTypes().addAll(getParameters().getNetClass().getRequiredConstraints());
		getParameters().getLPConstraintTypes().addAll(getUIOptionalConstraints());
	}

	private void setupLPObjectiveTypes() {
		if (objectiveTypes == null) {
			DefaultListModel<LPObjectiveType> listModel = new DefaultListModel<LPObjectiveType>();
			for (LPObjectiveType objType : EnumSet.allOf(LPObjectiveType.class)) {
				listModel.addElement(objType);
			}
			this.objectiveTypes = new ProMList<LPObjectiveType>("LP Objective", listModel);
			this.objectiveTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if (getParameters().getObjectiveType() != null) {
				this.objectiveTypes.setSelection(getParameters().getObjectiveType());
			} else {
				this.objectiveTypes.setSelection(LPObjectiveType.WEIGHTED_ABSOLUTE_PARIKH);
			}
			this.objectiveTypes.setPreferredSize(new Dimension(100, 150));
		}
	}

	private void setupOptionalConstraints(NetClass netClass, Collection<LPConstraintType> tryToSelect,
			boolean refresh) {
		if (optionalConstraints == null || refresh) {
			DefaultListModel<LPConstraintType> listModel = new DefaultListModel<LPConstraintType>();
			for (LPConstraintType constraintType : netClass.getOptionalConstraints()) {
				listModel.addElement(constraintType);
			}
			optionalConstraints = new ProMList<LPConstraintType>(
					"Optional constraints for net class \"" + netClass.toString() + "\" (CTRL+click to deselect)",
					listModel);
			optionalConstraints.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

			Set<LPConstraintType> optionalConstrSet = new THashSet<>();
			if (tryToSelect != null) {
				optionalConstrSet.addAll(netClass.getOptionalConstraints());
				optionalConstrSet.retainAll(tryToSelect);
			}
			optionalConstraints.setSelection(optionalConstrSet.toArray());
			optionalConstraints.setPreferredSize(new Dimension(100, 150));

			optionalConstraints.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (!getUIOptionalConstraints().contains(LPConstraintType.EMPTY_AFTER_COMPLETION)) {
						if (findSink.isSelected()) {
							JOptionPane.showMessageDialog(optionalConstraints,
									"You've unselected \"emptiness after completion\", this implies we can not find a sink, option will be unchecked and disabled",
									"Conflicting Setting", JOptionPane.WARNING_MESSAGE);
						}
						findSink.setSelected(false);
						findSink.setEnabled(false);
					} else {
						findSink.setEnabled(true);
					}
					visualize();
				}
			});
		}
	}

	public void updateParameters() {
		NetClass clazz;
		if ((clazz = getUINetClass()) != null) {
			getParameters().setNetClass(clazz);
			Collection<LPConstraintType> optConstr;
			if ((optConstr = getUIOptionalConstraints()) != null) {
				Set<LPConstraintType> constraints = new HashSet<>(
						getParameters().getNetClass().getRequiredConstraints());
				constraints.addAll(optConstr);
				getParameters().setLPConstraintTypes(constraints);
			}
		}
		LPObjectiveType obj;
		if ((obj = getUIObjectiveType()) != null) {
			getParameters().setObjectiveType(obj);
		}
		getParameters().setFindSink(findSink.isSelected());
	}

	public JComponent visualize() {
		removeAll();
		repaint();
		constructNetClassList();
		setupOptionalConstraints(getParameters().getNetClass(), getParameters().getLPConstraintTypes(), false);
		setupLPObjectiveTypes();

		// set some layout
		this.setOpaque(false);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		// row 0, col 0
		c.gridx = c.gridy = 0;
		add(netClassList, c);

		c.gridy += 1;
		add(objectiveTypes, c);

		c.gridy += 1;
		add(optionalConstraints, c);

		c.gridy += 1;
		add(findSink, c);

		revalidate();
		repaint();
		return this;
	}

	public void updateParametersOnGetNext() {
		updateParameters();
	}

	public void updateParametersOnGetPrevious() {
		updateParameters();
	}
}
