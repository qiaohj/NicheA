package org.ioz.chikungunya;

import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class Figure4 {
	@Test
	public void genusRaster() throws IOException{
		ArrayList<String> occurrences = CommonFun.readFromFile("E:\\Dropbox\\Papers\\CHIKV\\methodology\\genus.csv");
		GeoTiffObject mask = new GeoTiffObject("E:/GISLayers/Bioclim/bio_10m_tiff/bio1.tiff");
		int[] values = new int[mask.getXSize() * mask.getYSize()];
		for (int i=0; i<values.length; i++){
			if (CommonFun.equal(mask.getNoData(), mask.getValueArray()[i], 1000)){
				values[i] = -9999;
			}else{
				values[i] = 0;
			}
		}
		for (String o : occurrences){
			String[] ll = o.split(",");
			if (ll.length==3){
				if ((CommonFun.isDouble(ll[1]))&&(CommonFun.isDouble(ll[2]))){
					int[] xy = CommonFun.LLToPosition(
							mask.getDataset().GetGeoTransform(), new double[]{Double.valueOf(ll[2]), Double.valueOf(ll[1])});
					if (values[xy[1] * mask.getXSize() + xy[0]]!=-9999){
						values[xy[1] * mask.getXSize() + xy[0]]++;
					}
				}
			}
		}
		GeoTiffController.createTiff("E:\\Dropbox\\Papers\\CHIKV\\methodology\\genus.tif", 
				mask.getXSize(), mask.getYSize(), mask.getDataset().GetGeoTransform(), values, -9999, gdalconst.GDT_Int32,
				mask.getDataset().GetProjection());
	}
}
