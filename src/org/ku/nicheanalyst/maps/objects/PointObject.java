package org.ku.nicheanalyst.maps.objects;

import org.ku.nicheanalyst.common.CommonFun;


public class PointObject {
	private int id;
	private double longtitude;
	private double latitude;
	private int x;
	private int y;
	private boolean[] type;
	private int possibility;
	public PointObject(int id, double longtitude, double latitude, double[] geoTransform){
		this.id = id;
		this.longtitude = longtitude;
		this.latitude = latitude;
		int[] xy = CommonFun.LLToPosition(geoTransform, new double[]{longtitude, latitude});
		this.x = xy[0];
		this.y = xy[1];
		type = new boolean[]{false, false, false};
		possibility = 0;
	}
	
	public PointObject(int id, int x, int y, double[] geoTransform){
		this.id = id;
		this.x = x;
		this.y = y;
		double[] long_lat = CommonFun.PositionToLL(geoTransform, new int[]{x, y});
		this.longtitude = long_lat[0];
		this.latitude = long_lat[1];
		type = new boolean[]{false, false, false};
		possibility = 0;
	}
	
	

	public int getPossibility() {
		return possibility;
	}

	public void setPossibility(int possibility) {
		this.possibility = possibility;
	}

	public boolean[] getType() {
		return type;
	}

	public void setType(boolean subtype, int position) {
		if (position>=type.length){
			return;
		}
		type[position] = subtype;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	
}
