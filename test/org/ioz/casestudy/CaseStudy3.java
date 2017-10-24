package org.ioz.casestudy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class CaseStudy3 {
	@Test
	public void binarized_classification() throws FileNotFoundException{
		String filename="/Users/huijieqiao/NicheBreadth/PastClimate/cut.tif";
		GeoTiffObject geo = new GeoTiffObject(filename);
		double[] values = geo.getValueArray();
		for (int i=0;i<values.length;i++){
			if (!CommonFun.equal(values[i], geo.getNoData(), 1000)){
				values[i] = 255;
			}else{
				values[i] = geo.getNoData();
			}
		}
		GeoTiffController.createTiff(filename.replace(".tiff", ".tiff"), 
				geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(), 
				values, geo.getNoData(), gdalconst.GDT_Int32, geo.getDataset().GetProjection());
		
	}
	
	@Test
	public void sepOccurrenceByMask() throws IOException{
		GeoTiffObject mask = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheA_Case_Study/StudyCase3/masks/NAmerica.tif");
		ArrayList<String> occurrences = 
				CommonFun.readFromFile("/Users/huijieqiao/Dropbox/tmp/Occurrences.csv");
		StringBuilder sb = new StringBuilder();
		sb.append("long,lat" + Const.LineBreak);
		for (String occurrence : occurrences){
			String[] lls = occurrence.split(",");
			if (lls.length==3){
				if (CommonFun.isDouble(lls[2])){
					double[] ll = new double[]{Double.valueOf(lls[1]), Double.valueOf(lls[2])};
					double value = mask.readByLL(ll[0], ll[1]);
					if (CommonFun.equal(value, 255, 1000)){
						sb.append(String.format("%f,%f%n", ll[0], ll[1]));
					}
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/tmp/NAmerica.csv");
	}
}
