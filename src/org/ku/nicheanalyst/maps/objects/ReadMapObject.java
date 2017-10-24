package org.ku.nicheanalyst.maps.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;

public class ReadMapObject {
	private ArrayList<String> layerIds;
	private ArrayList<String> points;
	private HashMap<String, String> layerNames;
	private int[][] resultValue;
	public ReadMapObject(){
		layerIds = new ArrayList<String>();
		points = new ArrayList<String>();
		layerNames = new HashMap<String, String>();
	}
	public ArrayList<String> getLayerIds() {
		return layerIds;
	}
	public void setLayerIds(ArrayList<String> layerIds) {
		this.layerIds = layerIds;
	}
	public ArrayList<String> getPoints() {
		return points;
	}
	public void setPoints(ArrayList<String> points) {
		this.points = points;
	}
	
	
	private double[] getLL(String point){
		double[] result = new double[0];
		if (point==null){
			return result;
		}
		point = point.replace("\t", ",").replace(" ", ",");
		String[] ll = point.split(",");
		if (ll.length<2){
			return result;
		}
		if (CommonFun.isDouble(ll[0])&&CommonFun.isDouble(ll[1])){
			double lon = Double.valueOf(ll[0]);
			double lat = Double.valueOf(ll[1]);
			if ((lon>=-180)&&(lon<=180)&&(lat>=-180)&&(lat<=180)){
				result = new double[2];
				result[0] = lon;
				result[1] = lat;
				return result; 
			}
		}
		return result;
	}
	
	public HashMap<String, String> getLayerNames() {
		return layerNames;
	}
	
}
