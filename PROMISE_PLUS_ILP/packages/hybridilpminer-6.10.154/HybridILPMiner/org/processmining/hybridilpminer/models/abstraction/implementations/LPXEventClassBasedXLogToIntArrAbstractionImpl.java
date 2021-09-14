package org.processmining.hybridilpminer.models.abstraction.implementations;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.hybridilpminer.models.abstraction.abstracts.AbstractLPLogAbstraction;
import org.processmining.hybridilpminer.utils.trove.hashing.SetOfIntArrHashingStrategy;

public class LPXEventClassBasedXLogToIntArrAbstractionImpl extends AbstractLPLogAbstraction<XEventClass> {

	public LPXEventClassBasedXLogToIntArrAbstractionImpl(final XLog log, final XEventClassifier classifier) {
		constructAlphabet(log, classifier);
		for (XTrace t : log) {
			this.addTrace(t, classifier);
		}
	}

	protected void addTrace(XTrace t, XEventClassifier classifier) {
		Set<int[]> prefixes = new THashSet<>();
		List<XEventClass> prefix = new ArrayList<>();
		int[] prefixAbstraction = this.encode(prefix);
		prefixClosure.adjustOrPutValue(prefixAbstraction, 1, 1);
		prefixes.add(prefixAbstraction);
		for (int i = 0; i < t.size(); i++) {
			XEventClass eventClass = new XEventClass(classifier.getClassIdentity(t.get(i)), -1);
			prefix.add(eventClass);
			prefixAbstraction = this.encode(prefix);
			prefixClosure.adjustOrPutValue(prefixAbstraction, 1, 1);
			prefixes.add(prefixAbstraction);
		}
		this.log.adjustOrPutValue(prefixAbstraction, 1, 1);
		if (this.traceMap.get(prefixAbstraction) == null) {
			this.traceMap.put(prefixAbstraction, new TCustomHashSet<Set<int[]>>(new SetOfIntArrHashingStrategy()));

		}
		this.traceMap.get(prefixAbstraction).add(prefixes);
	}

	protected TObjectIntMap<XEventClass> constructAlphabet(XLog log, XEventClassifier classifier) {
		XLogInfo info = XLogInfoFactory.createLogInfo(log, classifier);
		for (XEventClass ec : info.getEventClasses(classifier).getClasses()) {
			super.alphabet.putIfAbsent(ec, super.alphabet.values().length);
		}
		return super.alphabet;
	}
}
