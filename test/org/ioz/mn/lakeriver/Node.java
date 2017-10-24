package org.ioz.mn.lakeriver;

import java.util.HashSet;

public class Node {
	private double x;
	private double y;
	private int id;
	private int fid;
	private int type;
	private HashSet<Node> children;
	private Node parent;
	private String label;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public HashSet<Node> getChildren() {
		return children;
	}
	public void addChildren(Node child) {
		this.children.add(child);
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public String getLabel() {
		return label;
	}
	public Node(double x, double y, int id, int fid, int type, Node parent) {
		super();
		this.label = String.format("%f,%f", x, y);
		this.x = x;
		this.y = y;
		this.id = id;
		this.fid = fid;
		this.type = type;
		this.parent = parent;
		this.children = new HashSet<Node>();
	}
	public double getDistance(Node subnode) {
		return Math.sqrt(Math.pow((this.x-subnode.getX()), 2) + Math.pow((this.y - subnode.getY()), 2));
	}
	public double getLength() {
		double length = 0;
		if (parent!=null){
			length +=getDistance(parent);
			length += parent.getLength();
		}
		return length;
	}
	public int getSteps() {
		int step = 0;
		if (parent!=null){
			step++;
			step += parent.getSteps();
		}
		return step;
	}
	public String getPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%f,%f%n", x, y));
		if (parent!=null){
			sb.append(parent.getPath());
		}
		return sb.toString();
	}
	
	
}
