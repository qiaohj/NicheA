package org.ku.nicheanalyst.dataset;

import javax.vecmath.Color3f;

public class DrawingObject {
	private String filename;
	private boolean isbackgroupd;
	private boolean isResult;
	private double threshold;
//	private int resultType;
	private Color3f mve_color;
	private Color3f ch_color;
	private Color3f point_color;
	private boolean isGradual;
	private String[] layers;
	private double[][][] maxmin;
	
	public DrawingObject(String filename, boolean isbackgroupd,
			boolean isResult, double threshold,
			Color3f mve_color, Color3f ch_color, Color3f point_color,
			boolean isGradual, String[] layers, double[][][] maxmin) {
		super();
		this.filename = filename;
		this.isbackgroupd = isbackgroupd;
		this.isResult = isResult;
		this.threshold = threshold;
//		this.resultType = resultType;
		this.mve_color = mve_color;
		this.ch_color = ch_color;
		this.point_color = point_color;
		this.isGradual = isGradual;
		this.layers = layers;
		this.maxmin = maxmin;
	}
	public String getFilename() {
		return filename;
	}
	public boolean isIsbackgroupd() {
		return isbackgroupd;
	}
	public boolean isResult() {
		return isResult;
	}
	public double getThreshold() {
		return threshold;
	}
//	public int getResultType() {
//		return resultType;
//	}
	public Color3f getMve_color() {
		return mve_color;
	}
	public Color3f getCh_color() {
		return ch_color;
	}
	public Color3f getPoint_color() {
		return point_color;
	}
	public boolean isGradual() {
		return isGradual;
	}
	public String[] getLayers() {
		return layers;
	}
	public double[][][] getMaxmin() {
		return maxmin;
	}
	
}
