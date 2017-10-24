package org.umn.niche.theory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class Maxent {
	@Test
	public void movies() throws IOException{
		File f = new File("/Users/huijieqiao/Experiments/ENM_Theory/Samples/output/png");
		StringBuilder sb = new StringBuilder();
		HashSet<String> labels = new HashSet<String>();
		for (File fitem : f.listFiles()){
			String[] label = fitem.getName().split("_");
			labels.add(label[0]);
		}
		for (String label : labels){
			sb.append(String.format("convert -delay 10 %s_*.png ../movies/%s.gif%n", label, label));
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Experiments/ENM_Theory/Samples/movies.sh");
	}
	@Test
	public void rename() throws IOException{
		File f = new File("/Users/huijieqiao/Experiments/ENM_Theory/Samples/output/png");
		StringBuilder sb = new StringBuilder();
		
		for (File fitem : f.listFiles()){
			for (int i=1; i<=10; i++){
				if (fitem.getName().contains(String.format("_%d_", i))){
					if (i<10){
						sb.append(String.format("mv %s %s%n", fitem.getName(), 
							fitem.getName().replace(String.format("_%d_", i), String.format("_%s_", i + ".0"))));
					}else{
						sb.append(String.format("mv %s %s%n", fitem.getName(), 
								fitem.getName().replace(String.format("_%d_", i), String.format("_A%s_", i + ".0"))));
					}
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Experiments/ENM_Theory/Samples/rm.sh");
	}
	@Test
	public void generateRaster() throws IOException{
		GeoTiffObject mask = new GeoTiffObject("/Users/huijieqiao/GIS/WorldClim/bio_10m_tif/bio19.tif");
		ArrayList<String> values = CommonFun.readFromFile("/Users/huijieqiao/Experiments/ENM_Theory/Samples/samples.csv");
		int xsize = 101;
		int ysize = 101;
		StringBuilder occs_rn_early_moon = new StringBuilder();
		occs_rn_early_moon.append("lon,lat" + Const.LineBreak);
		
		StringBuilder occs_rn_late_moon = new StringBuilder();
		occs_rn_late_moon.append("lon,lat" + Const.LineBreak);
		
		StringBuilder occs_rn_half_moon = new StringBuilder();
		occs_rn_half_moon.append("lon,lat" + Const.LineBreak);
		
		StringBuilder bg = new StringBuilder();
		bg.append("lon,lat" + Const.LineBreak);
		double[] v1 = new double[xsize * ysize];
		double[] v2 = new double[xsize * ysize];
		double[] fn = new double[xsize * ysize];
		double[] rn_early_moon = new double[xsize * ysize];
		double[] rn_late_moon = new double[xsize * ysize];
		double[] rn_half_moon = new double[xsize * ysize];
		for (int x=0;x<xsize; x++){
			for (int y=0;y<ysize; y++){
				String[] v_split = values.get(y * xsize + x + 1).split(",");
				v1[y * xsize + x] = Double.valueOf(v_split[0]).doubleValue();
				v2[y * xsize + x] = Double.valueOf(v_split[1]).doubleValue();
				fn[y * xsize + x] = v_split[3].equalsIgnoreCase("FALSE")?0:1;
				
				rn_early_moon[y * xsize + x] = v_split[7].equalsIgnoreCase("FALSE")?0:1;
				rn_late_moon[y * xsize + x] = v_split[8].equalsIgnoreCase("FALSE")?0:1;
				rn_half_moon[y * xsize + x] = v_split[9].equalsIgnoreCase("FALSE")?0:1;
				
				double[] ll = CommonFun.PositionToLL(mask.getDataset().GetGeoTransform(), new int[]{x, y});
				if (!v_split[7].equalsIgnoreCase("FALSE")){
					occs_rn_early_moon.append(String.format("%f,%f%n", ll[0], ll[1]));
				}
				
				if (!v_split[8].equalsIgnoreCase("FALSE")){
					occs_rn_late_moon.append(String.format("%f,%f%n", ll[0], ll[1]));
				}
				
				if (!v_split[9].equalsIgnoreCase("FALSE")){
					occs_rn_half_moon.append(String.format("%f,%f%n", ll[0], ll[1]));
				}
				bg.append(String.format("%f,%f%n", ll[0], ll[1]));
			}
		}
		CommonFun.writeFile(occs_rn_early_moon.toString(), "/Users/huijieqiao/Experiments/ENM_Theory/Samples/occs_rn_early_moon.csv");
		CommonFun.writeFile(occs_rn_late_moon.toString(), "/Users/huijieqiao/Experiments/ENM_Theory/Samples/occs_rn_late_moon.csv");
		CommonFun.writeFile(occs_rn_half_moon.toString(), "/Users/huijieqiao/Experiments/ENM_Theory/Samples/occs_rn_half_moon.csv");
		CommonFun.writeFile(bg.toString(), "/Users/huijieqiao/Experiments/ENM_Theory/Samples/bg.csv");
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/ENM_Theory/Samples/v1.tif", 
				xsize, ysize, mask.getDataset().GetGeoTransform(), v1, -9999, gdalconst.GDT_Float32, 
				mask.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/ENM_Theory/Samples/v2.tif", 
				xsize, ysize, mask.getDataset().GetGeoTransform(), v2, -9999, gdalconst.GDT_Float32, 
				mask.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/ENM_Theory/Samples/fn.tif", 
				xsize, ysize, mask.getDataset().GetGeoTransform(), fn, -9999, gdalconst.GDT_Float32, 
				mask.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/ENM_Theory/Samples/rn_early_moon.tif", 
				xsize, ysize, mask.getDataset().GetGeoTransform(), rn_early_moon, -9999, gdalconst.GDT_Float32, 
				mask.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/ENM_Theory/Samples/rn_late_moon.tif", 
				xsize, ysize, mask.getDataset().GetGeoTransform(), rn_late_moon, -9999, gdalconst.GDT_Float32, 
				mask.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/ENM_Theory/Samples/rn_half_moon.tif", 
				xsize, ysize, mask.getDataset().GetGeoTransform(), rn_half_moon, -9999, gdalconst.GDT_Float32, 
				mask.getDataset().GetProjection());
	}
}
