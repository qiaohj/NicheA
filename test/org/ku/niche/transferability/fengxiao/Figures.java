package org.ku.niche.transferability.fengxiao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class Figures {
	@Test
	public void Figure1() throws IOException{
		GeoTiffObject mask = new GeoTiffObject("/Users/huijieqiao/New_Experiments/bio_2.5m_Eurasia/PCA/PC1.tif");
		ArrayList<String> nbs = new ArrayList<String>();
		nbs.add("Large");
		nbs.add("Median");
		for (int i=9;i<=9;i++){
			for (String nb : nbs){
				ArrayList<String> csv = CommonFun.readFromFile(
						String.format("/Users/huijieqiao/New_Experiments/PCA_With_Cood/%s_%d_region_3_rep_156.csv", nb, i));
				double[] values = mask.getValueArray();
				for (int j=0; j<values.length; j++){
					values[j] = -9999;
				}
				for (String item : csv){
					
					String[] items = item.split(",");
					if (items.length==14){
						if (CommonFun.isDouble(items[0])){
							double lon = Double.valueOf(items[3]);
							double lat = Double.valueOf(items[4]);
							int[] xy = CommonFun.LLToPosition(mask.getDataset().GetGeoTransform(), new double[]{lon, lat});
							int city = Integer.valueOf(items[9]);
							values[xy[1] * mask.getXSize() + xy[0]] = city;
							
						}
					}
				}
				
				csv = CommonFun.readFromFile(
						String.format("/Users/huijieqiao/New_Experiments/Split/split_%s_%d.csv", nb, i));
				for (String item : csv){
					String[] items = item.split(",");
					if (items.length==5){
						if (CommonFun.isDouble(items[0])){
							double lon = Double.valueOf(items[0]);
							double lat = Double.valueOf(items[1]);
							int[] xy = CommonFun.LLToPosition(mask.getDataset().GetGeoTransform(), new double[]{lon, lat});
							int city = Integer.valueOf(items[4]) + 10;
							values[xy[1] * mask.getXSize() + xy[0]] = city;
						}
					}
				}
				GeoTiffController.createTiff(
						String.format("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Figures_20170118/Figure1/GeoTIFF/%s_%d.tif", nb, i), 
								mask.getXSize(), mask.getYSize(), mask.getDataset().GetGeoTransform(), values, -9999, 
								gdalconst.GDT_Int32, mask.getDataset().GetProjection());
			}
			
		}
	}
}
