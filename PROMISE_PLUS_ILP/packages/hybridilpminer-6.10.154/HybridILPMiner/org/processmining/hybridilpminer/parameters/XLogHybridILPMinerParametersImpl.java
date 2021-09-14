package org.processmining.hybridilpminer.parameters;

import java.util.Collection;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.lpengines.interfaces.LPEngine.EngineType;

public class XLogHybridILPMinerParametersImpl extends HybridILPMinerParametersImpl {

	private boolean applyStructuralRedundantPlaceRemoval = false;
	private XEventClassifier eventClassifier = null;
	private XLog log = null;

	public XLogHybridILPMinerParametersImpl(PluginContext context) {
		super(context);
	}

	public XLogHybridILPMinerParametersImpl(PluginContext context, EngineType engine,
			DiscoveryStrategy discoveryStrategy, NetClass netClass, Collection<LPConstraintType> constraints,
			LPObjectiveType objectiveType, LPVariableType variableType, LPFilter filter, boolean solve, XLog log,
			XEventClassifier classifier) {
		super(context, engine, discoveryStrategy, netClass, constraints, objectiveType, variableType, filter, solve);
		this.log = log;
		this.eventClassifier = classifier;
	}

	public XLogHybridILPMinerParametersImpl(final PluginContext context, final XLog log) {
		super(context);
		this.log = log;
	}

	public XLogHybridILPMinerParametersImpl(final PluginContext context, final XLog log,
			final XEventClassifier classifier) {
		this(context, log);
		this.eventClassifier = classifier;
	}

	public XEventClassifier getEventClassifier() {
		return eventClassifier;
	}

	public XLog getLog() {
		return log;
	}

	public String htmlPrettyPrint() {
		String result = "<html><table>";
		result += "<tr><td>Event classifier</td><td>" + getEventClassifier().toString() + "</td></tr>";
		result += "<tr><td>Net class</td><td>" + getNetClass().toString() + "</td></tr>";
		result += "<tr><td>Constraints</td><td>" + getLPConstraintTypes().toString() + "</td></tr>";
		result += "<tr><td>Filter</td><td>" + getFilter().getFilterType().toString() + "</td></tr>";
		if (getFilter().getFilterType() != LPFilterType.NONE) {
			result += "<tr><td>Filter Threshold</td><td>" + Double.toString(getFilter().getThreshold()) + "</td></tr>";
			;
		}
		result += "<tr><td>Discovery Strategy</td><td>" + getDiscoveryStrategy().getDiscoveryStrategyType().toString()
				+ "</td></tr>";
		if (getDiscoveryStrategy().getDiscoveryStrategyType().equals(DiscoveryStrategyType.CAUSAL_E_VERBEEK)) {
			result += "<tr><td>Causal Graph</td><td>"
					+ getDiscoveryStrategy().getCausalActivityGraphParameters().getMiner();
			result += "</td></tr>";
			result += "<tr><td>Causal Miner Parameters</td><td>";
			result += "zero value: " + getDiscoveryStrategy().getCausalActivityGraphParameters().getZeroValue();
			result += ", include threshold: "
					+ getDiscoveryStrategy().getCausalActivityGraphParameters().getIncludeThreshold();
			result += ", concurrency ratio: "
					+ getDiscoveryStrategy().getCausalActivityGraphParameters().getConcurrencyRatio() + "</td></tr>";
		}
		result += "<tr><td>Objective</td><td>" + getObjectiveType().toString() + "</td></tr>";
		result += "<tr><td>Variables</td><td>" + getLPVaraibleType().toString() + "</td></tr>";
		result += "<tr><td>Engine</td><td>" + getEngine().toString() + "</td></tr>";
		result += "</table></html>";
		return result;
	}

	public boolean isApplyStructuralRedundantPlaceRemoval() {
		return applyStructuralRedundantPlaceRemoval;
	}

	public void setApplyStructuralRedundantPlaceRemoval(boolean applyStructuralRedundantPlaceRemoval) {
		this.applyStructuralRedundantPlaceRemoval = applyStructuralRedundantPlaceRemoval;
	}

	public void setEventClassifier(XEventClassifier eventClassifier) {
		this.eventClassifier = eventClassifier;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	@Override
	public String toString() {
		String result = "[";
		result += "ec: " + getEventClassifier().toString() + ", ";
		//		result += "net_Class: " + getNetClass().toString() + ", ";
		result += "cons: " + getLPConstraintTypes().toString() + ", ";
		result += "fil: " + getFilter().getFilterType().toString();
		if (getFilter().getFilterType() != LPFilterType.NONE) {
			result += ", threshold: " + Double.toString(getFilter().getThreshold());
		}
		result += ", ";
		result += "stra: " + getDiscoveryStrategy().getDiscoveryStrategyType().toString() + ", ";
		result += "obj: " + getObjectiveType().toString() + ", ";
		result += "var: " + getLPVaraibleType().toString() + ", ";
		result += "engine: " + getEngine().toString();
		result += "]";
		return result;
	}

}
