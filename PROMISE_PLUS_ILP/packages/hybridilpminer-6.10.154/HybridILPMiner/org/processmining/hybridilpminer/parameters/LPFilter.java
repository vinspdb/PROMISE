package org.processmining.hybridilpminer.parameters;

public class LPFilter {

	private final LPFilterType DEFAULT = LPFilterType.SEQUENCE_ENCODING;

	private LPFilterType filter = DEFAULT;

	private double threshold = DEFAULT.getDefaultThreshold();

	public LPFilter() {
	}

	public LPFilter(LPFilterType filter, final double threshold) {
		this.filter = filter;
		this.threshold = threshold;
	}

	/**
	 * @return the filter
	 */
	public LPFilterType getFilterType() {
		return filter;
	}

	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilterType(LPFilterType filter) {
		this.filter = filter;
	}

	/**
	 * @param threshold
	 *            the threshold to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
