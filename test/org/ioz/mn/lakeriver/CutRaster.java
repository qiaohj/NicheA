package org.ioz.mn.lakeriver;

import java.io.FileNotFoundException;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class CutRaster {
	@Test
	public void cut() throws FileNotFoundException{
		GeoTiffObject accum = new GeoTiffObject("/Users/huijieqiao/River_Lake/accum10001.tif");
		GeoTiffObject lake = new GeoTiffObject("/Users/huijieqiao/River_Lake/Lake_Raster.tif");
		GeoTiffObject river = new GeoTiffObject("/Users/huijieqiao/River_Lake/River_Raster.tif");
		System.out.println("nodata for accum is " + accum.getNoData());
		System.out.println("nodata for lake is " + lake.getNoData());
		System.out.println("nodata for river is " + river.getNoData());
		double[] values_lake = new double[accum.getXSize() * accum.getYSize()];
		
		double[] values_river = new double[accum.getXSize() * accum.getYSize()];
		System.out.println("Init arrays");
		for (int i=0; i< values_lake.length; i++){
			values_lake[i] = -9999;
			values_river[i] = -9999;
		}
		//double[] values_accum = accum.getValueArray();
		
		for (int x=0; x< accum.getXSize(); x++){
			System.out.println(x + "/" + accum.getXSize());
			for (int y=0; y< accum.getYSize(); y++){
				double v = accum.readByXY(x, y);
				if (!CommonFun.equal(v, accum.getNoData(), 1000)){
					double[] ll = CommonFun.PositionToLL(accum.getDataset().GetGeoTransform(), new int[] {x, y});
					values_lake[y * accum.getXSize() + x] = lake.readByLL(ll[0], ll[1]);
					values_river[y * accum.getXSize() + x] = river.readByLL(ll[0], ll[1]);
				}
			}
		}
		GeoTiffController.createTiff("/Users/huijieqiao/River_Lake/River.tif",
				accum.getXSize(), accum.getYSize(), accum.getDataset().GetGeoTransform(), 
				values_river, -9999, gdalconst.GDT_Int32, accum.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/River_Lake/Lake.tif",
				accum.getXSize(), accum.getYSize(), accum.getDataset().GetGeoTransform(), 
				values_lake, -9999, gdalconst.GDT_Int32, accum.getDataset().GetProjection());
	}
}
