package org.ioz.bear;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class ABB {
	@Test
	public void getDiff() throws IOException{
		GeoTiffObject maxent = new GeoTiffObject("E:/Dropbox/Papers/bear/BC/data/oso_3mil_median/oso_3mil_median.asc");
		GeoTiffObject light = new GeoTiffObject("E:/Dropbox/Papers/bear/BC/data/luz_original/luz_original.asc");
		StringBuilder sb = new StringBuilder();
		sb.append("maxent, light" + Const.LineBreak);
		double[] differ = new double[maxent.getXSize() * maxent.getYSize()];
		for (int x=0;x<maxent.getXSize();x++){
			for (int y=0;y<maxent.getYSize();y++){
				double v = maxent.readByXY(x, y);
				double[] ll = CommonFun.PositionToLL(maxent.getDataset().GetGeoTransform(), new int[]{x, y});
				double v_light = light.readByLL(ll[0], ll[1]);
				if (CommonFun.equal(v, maxent.getNoData(), 1000) || CommonFun.equal(v_light, light.getNoData(), 1000)){
					differ[y * maxent.getXSize() + x] = -9999;
				}else{
					if (v<0.319494){
						differ[y * maxent.getXSize() + x] = -9999;
					}else{
						v_light = v_light / 255d;
						if ((v - v_light)>2){
							System.out.println("Exception");
						}
						differ[y * maxent.getXSize() + x] = v - v_light;
						sb.append(v + "," + v_light + Const.LineBreak);
					}
				}
			}
		}
		GeoTiffController.createTiff("E:/Dropbox/Papers/bear/BC/data/differ.tif",
				maxent.getXSize(), maxent.getYSize(), maxent.getDataset().GetGeoTransform(), differ, -9999, gdalconst.GDT_Float32, maxent.getDataset().GetProjection());
		CommonFun.writeFile(sb.toString(), "E:/Dropbox/Papers/bear/BC/data/differ.csv");
	}
	
	@Test
	public void getMaxentCover() throws IOException{
		GeoTiffObject maxent = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/bear/BC/data/oso_3mil_median/oso_3mil_median.asc");
		int all = 0;
		int inmaxent = 0;
		for (int x=0;x<maxent.getXSize();x++){
			for (int y=0;y<maxent.getYSize();y++){
				double v = maxent.readByXY(x, y);
				if (CommonFun.equal(v, maxent.getNoData(), 1000)){
				}else{
					all++;
					if (v<0.319494){
						
					}else{
						inmaxent++;
					}
				}
			}
		}
		System.out.println(all + ":" + inmaxent);
	}
}
