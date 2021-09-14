package org.processmining.hybridilpminer.models.lp.configuration.parameters;

@Deprecated //relocated: org.processmining.hybridilpminer.params
public enum LPFilterType {

	NONE("None", 0.0, "No filter."), SEQUENCE_ENCODING("Sequence Encoding Filter", 0.2,
			"Specifies at what level a branch should be \"cut off\"."), SLACK_VAR("Slack Variable Filter", 0.2,
					"Specifies what protion of constraints might be shut off.");

	private final double defaultThreshold;
	private final String name;
	private final String thresholdDescription;

	LPFilterType(String name, double defaultThreshold, String thresholdDescription) {
		this.name = name;
		this.defaultThreshold = defaultThreshold;
		this.thresholdDescription = thresholdDescription;
	}

	@Override
	public String toString() {
		return name;
	}

	public double getDefaultThreshold() {
		return defaultThreshold;
	}

	public String getThresholdDescription() {
		return thresholdDescription;
	}

	// use getDefaultThreshold
	@Deprecated
	public double getThreshold() {
		return defaultThreshold;
	}

	@Deprecated
	public void setThreshold(double threshold) {
		// NOP
	}

}
