package org.ioz.mn.lakeriver;

import java.util.HashMap;
import java.util.HashSet;

import org.gdal.ogr.Geometry;

public class ShapeObject {
	private String attribute;
	private String wetland_type;
	private double arces;
	private double shape_leng;
	private double shape_area;
	private Geometry geometry;
	private Geometry convex;
	private int index;
	private HashMap<Integer, ShapeObject> linked;
	public ShapeObject(int index, Geometry geometry, String attribute, String wetland_type,
			double arces, double shape_leng, double shape_area){
		this.attribute = attribute;
		this.wetland_type = wetland_type;
		this.arces = arces;
		this.shape_area = shape_area;
		this.shape_leng = shape_leng;
		
		this.index = index;
		this.geometry = geometry;
		this.convex = geometry.ConvexHull();
		this.linked = new HashMap<Integer, ShapeObject>();
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public HashMap<Integer, ShapeObject> getLinked() {
		return linked;
	}
	
	public Geometry getConvex() {
		return convex;
	}
	
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getWetland_type() {
		return wetland_type;
	}
	public void setWetland_type(String wetland_type) {
		this.wetland_type = wetland_type;
	}
	public double getArces() {
		return arces;
	}
	public void setArces(double arces) {
		this.arces = arces;
	}
	public double getShape_leng() {
		return shape_leng;
	}
	public void setShape_leng(double shape_leng) {
		this.shape_leng = shape_leng;
	}
	public double getShape_area() {
		return shape_area;
	}
	public void setShape_area(double shape_area) {
		this.shape_area = shape_area;
	}
	public void setConvex(Geometry convex) {
		this.convex = convex;
	}
	public void setLinked(HashMap<Integer, ShapeObject> linked) {
		this.linked = linked;
	}
	public void addLinked(int index, ShapeObject geometry){
		this.linked.put(index, geometry);
	}
}
