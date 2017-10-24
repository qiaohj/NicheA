package org.ku.nicheanalyst.dataset;

public class OverlapObject {
	private double precision;
	private String overlap_method;
	public double getPrecision() {
		return precision;
	}
	public String getOverlap_method() {
		return overlap_method;
	}
	public OverlapObject(double precision, String overlap_method) {
		super();
		this.precision = precision;
		this.overlap_method = overlap_method;
	}
	
}
