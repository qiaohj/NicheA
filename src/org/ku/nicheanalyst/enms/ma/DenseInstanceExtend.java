package org.ku.nicheanalyst.enms.ma;

import weka.core.DenseInstance;
import weka.core.Instance;



public class DenseInstanceExtend extends DenseInstance implements Comparable<DenseInstanceExtend>{

	/**
	 * 
	 */
	static final long serialVersionUID = -4880187493280990260L;
	private int[] xy;
	private int classValue;
	private double MaxDistance;
	private double MinDistance;
	private double sumDistance;
	private double AvgDistance;
	private int distanceCount;
	private double distanceVar;
	public DenseInstanceExtend(Instance instance) {
		super(instance);
		sumDistance = 0;
		xy = new int[2];
		MaxDistance = Double.MIN_VALUE;
		MinDistance = Double.MAX_VALUE;
		AvgDistance = 0;
		distanceCount = 0;
		distanceVar = 0;
	}
	
	public void addDistance(double distance){
		setMaxDistance(distance);
		setMinDistance(distance);
		sumDistance += distance;
		distanceCount++;
		AvgDistance = sumDistance/(double)distanceCount;
		//Collections.sort(distances);
	}
	public void setMaxDistance(double distance){
		if (distance>MaxDistance){
			MaxDistance = distance;
		}
	}
	public double getMaxDistance(){
		return MaxDistance;
	}
	public void setMinDistance(double distance){
		if (distance<MinDistance){
			MinDistance = distance;
		}
	}
	public double getMinDistance(){
		return MinDistance;
	}
	public double getSumDistance() {
		return sumDistance;
	}
	public int compareTo(DenseInstanceExtend o) {
		if (sumDistance < o.getSumDistance()){
			return -1;
		}else{
			return 1;
		}

	}
	public int[] getXy() {
		return xy;
	}
	public void setXy(int[] xy) {
		this.xy = xy;
	}
	public int getClassValue() {
		return classValue;
	}
	public void setClassValue(int classValue) {
		this.classValue = classValue;
	}
	
	public double getAvgDistance(){
		return AvgDistance;
	}

	public double getDistanceVar() {
		return distanceVar;
	}

	public void setDistanceVar(double distanceVar) {
		this.distanceVar = distanceVar;
	}
}
