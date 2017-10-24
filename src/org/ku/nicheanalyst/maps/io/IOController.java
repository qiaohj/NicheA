package org.ku.nicheanalyst.maps.io;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.jdom.Element;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class IOController {
	private ArrayList<GeoTiffObject> mapobjects;
	private ArrayList<double[]> positions;
	public IOController(ArrayList<String> filenames, ArrayList<double[]> positions) throws FileNotFoundException{
		mapobjects = new ArrayList<GeoTiffObject>();
		positions = new ArrayList<double[]>();
		for (String filename: filenames){
			mapobjects.add(new GeoTiffObject(filename));
		}
	}
	public Element toXML(){
		return null;
	}
}
