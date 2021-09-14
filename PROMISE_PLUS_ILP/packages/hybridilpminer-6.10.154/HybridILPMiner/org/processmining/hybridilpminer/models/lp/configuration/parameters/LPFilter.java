package org.processmining.hybridilpminer.models.lp.configuration.parameters;

@Deprecated //relocated: org.processmining.hybridilpminer.params
public class LPFilter {

	private LPFilterType filter = LPFilterType.NONE;

	private double threshold = 0.0;

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
