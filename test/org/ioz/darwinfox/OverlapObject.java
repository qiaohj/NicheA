package org.ioz.darwinfox;

public class OverlapObject {
	private int vs1;
	private int vs2;
	private double v_vs1;
	private double v_vs2;
	private double overlap;
	public int getVs1() {
		return vs1;
	}
	public int getVs2() {
		return vs2;
	}
	public double getV_vs1() {
		return v_vs1;
	}
	public double getV_vs2() {
		return v_vs2;
	}
	public double getOverlap() {
		return overlap;
	}
	public OverlapObject(int vs1, int vs2, double v_vs1, double v_vs2,
			double overlap) {
		super();
		this.vs1 = vs1;
		this.vs2 = vs2;
		this.v_vs1 = v_vs1;
		this.v_vs2 = v_vs2;
		this.overlap = overlap;
	}
	
}
