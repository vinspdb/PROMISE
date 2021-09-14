package org.processmining.hybridilpminer.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;

public class XLogHybridILPMinerParametersConnection extends AbstractConnection {

	public final static String KEY_EVENT_LOG = "Event Log";

	public final static String KEY_PARAMETERS = "Hybrid ILP Miner Parameters";

	public final static String KEY_TIMESTAMP = "Timestamp";

	// store pointers to non-referenced objects!
	@SuppressWarnings("unused")
	private final XLogHybridILPMinerParametersImpl params;
	private final Long timestamp;

	public XLogHybridILPMinerParametersConnection(XLog log, XLogHybridILPMinerParametersImpl parameters) {
		super("Connection (XLog, Hybrid ILP Miner Parameters)" + System.currentTimeMillis());
		this.timestamp = System.currentTimeMillis();
		this.params = parameters;
		put(KEY_EVENT_LOG, log);
		put(KEY_PARAMETERS, parameters);
		put(KEY_TIMESTAMP, timestamp);
	}

}
