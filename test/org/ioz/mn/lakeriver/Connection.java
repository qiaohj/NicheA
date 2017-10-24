package org.ioz.mn.lakeriver;

public class Connection {
	private int id1;
	private int id2;
	private int type1;
	private int type2;
	private double distance;
	public int getId1() {
		return id1;
	}
	public void setId1(int id1) {
		this.id1 = id1;
	}
	public int getId2() {
		return id2;
	}
	public void setId2(int id2) {
		this.id2 = id2;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public int getType1() {
		return type1;
	}
	public void setType1(int type1) {
		this.type1 = type1;
	}
	public int getType2() {
		return type2;
	}
	public void setType2(int type2) {
		this.type2 = type2;
	}
	public Connection(int id1, int id2, int type1, int type2, double distance) {
		super();
		this.id1 = id1;
		this.id2 = id2;
		this.type1 = type1;
		this.type2 = type2;
		this.distance = distance;
	}
	
}
