package org.processmining.hybridilpminer.utils;

import java.util.Collections;
import java.util.List;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class XLogUtils {

	@SuppressWarnings("unchecked")
	public static XLog addArtificialStartAndEnd(XLog log, String artifStart, String artifEnd) {
		XLog copy = (XLog) log.clone();
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		List<XEventClassifier> classifiers = (List<XEventClassifier>) (log.getClassifiers().isEmpty()
				? Collections.singletonList(
						new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier()))
				: log.getClassifiers());
		for (XTrace t : copy) {
			t.add(0, factory.createEvent(createArtificialAttributeMap(factory, classifiers, artifStart)));
			t.add(factory.createEvent(createArtificialAttributeMap(factory, classifiers, artifEnd)));
		}
		if (copy.isEmpty()) {
			XTrace t = factory.createTrace();
			t.add(0, factory.createEvent(createArtificialAttributeMap(factory, classifiers, artifStart)));
			t.add(factory.createEvent(createArtificialAttributeMap(factory, classifiers, artifEnd)));
			copy.add(t);
		}
		return copy;
	}

	private static XAttributeMap createArtificialAttributeMap(final XFactory factory,
			final List<XEventClassifier> classifiers, final String artificialLabel) {
		XAttributeMap map = factory.createAttributeMap();
		for (XEventClassifier classifier : classifiers) {
			for (String s : classifier.getDefiningAttributeKeys()) {
				map.put(s, factory.createAttributeLiteral(s, artificialLabel, null));
			}
		}
		return map;
	}

}
