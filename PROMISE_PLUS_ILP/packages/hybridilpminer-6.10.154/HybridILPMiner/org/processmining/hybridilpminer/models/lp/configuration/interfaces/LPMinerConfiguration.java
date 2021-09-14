package org.processmining.hybridilpminer.models.lp.configuration.interfaces;

import java.io.File;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.DiscoveryStrategy;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPConstraintType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPFilter;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPObjectiveType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.LPVariableType;
import org.processmining.hybridilpminer.models.lp.configuration.parameters.NetClass;
import org.processmining.lpengines.interfaces.LPEngine.EngineType;

@Deprecated //use org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParameters or HybridILPMinerParameters
public abstract interface LPMinerConfiguration {

	public void addLPConstraintType(LPConstraintType t);

	public DiscoveryStrategy getDiscoveryStrategy();

	public EngineType getEngineType();

	@Deprecated
	public XEventClassifier getEventClassifier();

	public LPFilter getFilter();

	@Deprecated
	public XLog getLog();

	public Set<LPConstraintType> getLPConstraintTypes();

	public LPVariableType getLPVaraibleType();

	public NetClass getNetClass();

	public LPObjectiveType getObjectiveType();

	public void setDiscoveryStrategy(DiscoveryStrategy strategy);

	public void setEngineType(EngineType type);

	@Deprecated
	public void setEvenClassifier(XEventClassifier classifier);

	public void setFilter(LPFilter filter);

	@Deprecated
	public void setLog(XLog log);

	public void setLPConstraintTypes(Set<LPConstraintType> types);

	public void setNetClass(NetClass netClass);

	public void setObjectiveType(LPObjectiveType type);

	public void setVariableType(LPVariableType type);

	/**
	 * A Dummy Configuration means that ProM will not solve the constructed LP's
	 * It will be used to export the lp's to disk. use: isSolve and setIsSolve
	 */
	@Deprecated
	public boolean isDummy();

	@Deprecated
	public void setIsDummy(boolean dummy);

	public boolean isSolve();

	public void setIsSolve(boolean solve);

	public File getDummyLocation();

	public void setDummyLocation(File location);
}
