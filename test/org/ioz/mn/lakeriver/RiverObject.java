package org.ioz.mn.lakeriver;

import java.util.HashMap;
import java.util.HashSet;

import org.gdal.ogr.Geometry;

public class RiverObject {
	
	
	private double Shape_Leng;
	private Geometry geometry;
	private Geometry convex;
	private int index;
	private HashMap<Integer, RiverObject> linked;
	public RiverObject(int index, Geometry geometry, double Shape_Leng){
	
		this.Shape_Leng = Shape_Leng;
		
		this.index = index;
		this.geometry = geometry;
		this.convex = geometry.ConvexHull();
		this.linked = new HashMap<Integer, RiverObject>();
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
	public HashMap<Integer, RiverObject> getLinked() {
		return linked;
	}
	
	public Geometry getConvex() {
		return convex;
	}
	
	
	
	public double getShape_Leng() {
		return Shape_Leng;
	}
	public void setConvex(Geometry convex) {
		this.convex = convex;
	}
	public void setLinked(HashMap<Integer, RiverObject> linked) {
		this.linked = linked;
	}
	public void addLinked(int index, RiverObject geometry){
		this.linked.put(index, geometry);
	}
}
