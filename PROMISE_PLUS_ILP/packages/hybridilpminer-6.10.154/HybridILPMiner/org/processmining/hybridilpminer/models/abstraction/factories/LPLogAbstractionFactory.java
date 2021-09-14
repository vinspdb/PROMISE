package org.processmining.hybridilpminer.models.abstraction.factories;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.hybridilpminer.models.abstraction.implementations.LPXEventClassBasedXLogToIntArrAbstractionImpl;
import org.processmining.hybridilpminer.models.abstraction.interfaces.LPLogAbstraction;

public class LPLogAbstractionFactory {

	public static LPLogAbstraction<XEventClass> createXEventClassBasedIntArrayAbstraction(XLog log,
			XEventClassifier classifier) {
		return new LPXEventClassBasedXLogToIntArrAbstractionImpl(log, classifier);
	}
}
