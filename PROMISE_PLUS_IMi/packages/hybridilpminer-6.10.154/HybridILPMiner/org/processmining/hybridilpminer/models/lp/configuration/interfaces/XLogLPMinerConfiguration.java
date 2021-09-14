package org.processmining.hybridilpminer.models.lp.configuration.interfaces;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;

@Deprecated
public interface XLogLPMinerConfiguration extends LPMinerConfiguration {

	public XEventClassifier getEventClassifier();

	public XLog getLog();

	public void setEvenClassifier(XEventClassifier classifier);

	public void setLog(XLog log);

}
