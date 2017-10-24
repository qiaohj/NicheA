package org.ioz.mn.lakeriver;

public class PointObj {
	private double x;
	private double y;
	private int type;
	private int FID;
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFID() {
		return FID;
	}
	public void setFID(int fID) {
		FID = fID;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public PointObj(double x, double y, int type, int fID, int Id) {
		super();
		this.x = x;
		this.y = y;
		this.type = type;
		this.FID = fID;
		this.id = Id;
	}
	
	
}
