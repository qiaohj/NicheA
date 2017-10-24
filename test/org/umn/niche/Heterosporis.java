package org.umn.niche;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class Heterosporis {
	@Test
	public void cutRNWithFN() throws FileNotFoundException{
		GeoTiffObject fn_all = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/NicheA_Models/HTSP_ll.tiff");
		GeoTiffObject fn_ourlier = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/NicheA_Models/HTSP_Without_LL.tiff");
		GeoTiffObject rn_kde_large_bw = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/KDE/kde_large_bw.tif");
		GeoTiffObject rn_kde_small_bw = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/KDE/kde_small_bw.tif");
		GeoTiffObject rn_ma = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/MA/MA.tif");
		double [] values_ma_fn_all = new double[rn_ma.getValueArray().length];
		double [] values_ma_fn_ourlier = new double[rn_ma.getValueArray().length];
		double [] values_kde_large_bw_fn_all = new double[rn_ma.getValueArray().length];
		double [] values_kde_large_bw_fn_ourlier = new double[rn_ma.getValueArray().length];
		double [] values_kde_small_bw_fn_all = new double[rn_ma.getValueArray().length];
		double [] values_kde_small_bw_fn_ourlier = new double[rn_ma.getValueArray().length];
		
		
		for (int x=0; x<rn_ma.getXSize(); x++){
			for (int y=0; y<rn_ma.getYSize(); y++){
				double v = rn_ma.readByXY(x, y);
				
				if (CommonFun.equal(v, rn_ma.getNoData(), 100000)){
					values_ma_fn_all[y * rn_ma.getXSize() + x] = -9999;
					values_ma_fn_ourlier[y * rn_ma.getXSize() + x] = -9999;
					values_kde_large_bw_fn_all[y * rn_ma.getXSize() + x] = -9999;
					values_kde_large_bw_fn_ourlier[y * rn_ma.getXSize() + x] = -9999;
					values_kde_small_bw_fn_all[y * rn_ma.getXSize() + x] = -9999;
					values_kde_small_bw_fn_ourlier[y * rn_ma.getXSize() + x] = -9999;
					continue;
				}
				double[] ll = CommonFun.PositionToLL(rn_ma.getDataset().GetGeoTransform(), new int[]{x, y});
				if ((fn_all.readByLL(ll[0], ll[1])>=0)&&(rn_ma.readByLL(ll[0], ll[1])>0)){
					values_ma_fn_all[y * rn_ma.getXSize() + x] = 1;
				}else{
					values_ma_fn_all[y * rn_ma.getXSize() + x] = 0;
				}
				
				if ((fn_ourlier.readByLL(ll[0], ll[1])>=0)&&(rn_ma.readByLL(ll[0], ll[1])>0)){
					values_ma_fn_ourlier[y * rn_ma.getXSize() + x] = 1;
				}else{
					values_ma_fn_ourlier[y * rn_ma.getXSize() + x] = 0;
				}
				
				if ((fn_all.readByLL(ll[0], ll[1])>=0)&&(rn_kde_large_bw.readByLL(ll[0], ll[1])>0)){
					values_kde_large_bw_fn_all[y * rn_ma.getXSize() + x] = 1;
				}else{
					values_kde_large_bw_fn_all[y * rn_ma.getXSize() + x] = 0;
				}
				
				if ((fn_ourlier.readByLL(ll[0], ll[1])>=0)&&(rn_kde_large_bw.readByLL(ll[0], ll[1])>0)){
					values_kde_large_bw_fn_ourlier[y * rn_ma.getXSize() + x] = 1;
				}else{
					values_kde_large_bw_fn_ourlier[y * rn_ma.getXSize() + x] = 0;
				}
				
				if ((fn_all.readByLL(ll[0], ll[1])>=0)&&(rn_kde_small_bw.readByLL(ll[0], ll[1])>0)){
					values_kde_small_bw_fn_all[y * rn_ma.getXSize() + x] = 1;
				}else{
					values_kde_small_bw_fn_all[y * rn_ma.getXSize() + x] = 0;
				}
				
				if ((fn_ourlier.readByLL(ll[0], ll[1])>=0)&&(rn_kde_small_bw.readByLL(ll[0], ll[1])>0)){
					values_kde_small_bw_fn_ourlier[y * rn_ma.getXSize() + x] = 1;
				}else{
					values_kde_small_bw_fn_ourlier[y * rn_ma.getXSize() + x] = 0;
				}
			}
		}
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/ma_fn_all.tif", 
				rn_ma.getXSize(), rn_ma.getYSize(), rn_ma.getDataset().GetGeoTransform(), values_ma_fn_all, -9999, 
				gdalconst.GDT_Int32, rn_ma.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/ma_fn_ourlier.tif", 
				rn_ma.getXSize(), rn_ma.getYSize(), rn_ma.getDataset().GetGeoTransform(), values_ma_fn_ourlier, -9999, 
				gdalconst.GDT_Int32, rn_ma.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_large_bw_fn_all.tif", 
				rn_ma.getXSize(), rn_ma.getYSize(), rn_ma.getDataset().GetGeoTransform(), values_kde_large_bw_fn_all, -9999, 
				gdalconst.GDT_Int32, rn_ma.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_large_bw_fn_ourlier.tif", 
				rn_ma.getXSize(), rn_ma.getYSize(), rn_ma.getDataset().GetGeoTransform(), values_kde_large_bw_fn_ourlier, -9999, 
				gdalconst.GDT_Int32, rn_ma.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_small_bw_fn_all.tif", 
				rn_ma.getXSize(), rn_ma.getYSize(), rn_ma.getDataset().GetGeoTransform(), values_kde_small_bw_fn_all, -9999, 
				gdalconst.GDT_Int32, rn_ma.getDataset().GetProjection());
		
		GeoTiffController.createTiff("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_small_bw_fn_ourlier.tif", 
				rn_ma.getXSize(), rn_ma.getYSize(), rn_ma.getDataset().GetGeoTransform(), values_kde_small_bw_fn_ourlier, -9999, 
				gdalconst.GDT_Int32, rn_ma.getDataset().GetProjection());
	}
	@Test
	public void testRead() throws FileNotFoundException{
		GeoTiffObject modis = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/RS_cut/evmmod3a.tif");
		System.out.println(modis.readByLL(-94.01147, 46.47222));
		System.out.println(modis.readByLL(-94.62086, 47.50650));
	}
	
	@Test
	public void getCurve() throws IOException{
		GeoTiffObject mask = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/PCA/PC1.tif");
		GeoTiffObject fn_all = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/NicheA_Models/HTSP_ll.tiff");
		GeoTiffObject fn_ourlier = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/NicheA_Models/HTSP_Without_LL.tiff");
		
		//is_rn_ma,is_rn_kde_large_bw,is_rn_kde_small_bw
		GeoTiffObject rn_kde_large_bw = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/KDE/kde_large_bw.tif");
		GeoTiffObject rn_kde_small_bw = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/KDE/kde_small_bw.tif");
		GeoTiffObject rn_ma = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/MA/MA.tif");
		
		GeoTiffObject rn_kde_large_bw_cut_fn_all = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_large_bw_fn_all.tif");
		GeoTiffObject rn_kde_small_bw_cut_fn_all = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_small_bw_fn_all.tif");
		GeoTiffObject rn_ma_cut_fn_all = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/ma_fn_all.tif");
		
		GeoTiffObject rn_kde_large_bw_cut_fn_ourlier = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_large_bw_fn_ourlier.tif");
		GeoTiffObject rn_kde_small_bw_cut_fn_ourlier = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/kde_small_bw_fn_ourlier.tif");
		GeoTiffObject rn_ma_cut_fn_ourlier = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/RN_Cut_by_FN/ma_fn_ourlier.tif");
				
		ArrayList<GeoTiffObject> Layers = new ArrayList<GeoTiffObject>();
		
		File f = new File("/Users/huijieqiao/Experiments/Heterosporis/current_10min_cut");
		for (File fitem : f.listFiles()){
			if (fitem.getName().endsWith(".asc")){
				Layers.add(new GeoTiffObject(fitem.getAbsolutePath()));
			}
		}
		
		f = new File("/Users/huijieqiao/Experiments/Heterosporis/RS_cut");
		for (File fitem : f.listFiles()){
			if (fitem.getName().endsWith(".tif")){
				Layers.add(new GeoTiffObject(fitem.getAbsolutePath()));
			}
		}
		
		ArrayList<String> occs = CommonFun.readFromFile("/Users/huijieqiao/Experiments/Heterosporis/HTSP_ll.csv");
		HashSet<String> occset = new HashSet<String>();
		for (String occ : occs){
			String[] occsplit = occ.split(",");
			if (occsplit.length==2){
				if (CommonFun.isDouble(occsplit[1])){
					int[] xy = CommonFun.LLToPosition(mask.getDataset().GetGeoTransform(), 
							new double[]{Double.valueOf(occsplit[0]), Double.valueOf(occsplit[1])});
					occset.add(String.format("%d,%d", xy[0], xy[1]));
				}
			}
		}
		
		for (GeoTiffObject layer : Layers){
			StringBuilder sb = new StringBuilder();
			sb.append("lon,lat,x,y,value,is_fn_all,is_fn_ourlier,is_rn_ma,is_rn_kde_large_bw,is_rn_kde_small_bw,is_occ,is_occ_outlier,"
					+ "is_rn_ma_cut_fn_all,is_rn_kde_large_bw_cut_fn_all,is_rn_kde_small_bw_cut_fn_all,"
					+ "is_rn_ma_cut_fn_ourlier,is_rn_kde_large_bw_cut_fn_ourlier,is_rn_kde_small_bw_cut_fn_ourlier"
					+ Const.LineBreak);
			String layer_name = new File(layer.getFilename()).getName().replace(".asc", "").replace(".tif", "");
			System.out.println(layer_name);
			for (int x=0; x<mask.getXSize(); x++){
				for (int y=0;y<mask.getYSize(); y++){
					double v = mask.readByXY(x, y);
					if (CommonFun.equal(v, mask.getNoData(), 10000)){
						continue;
					}
					double[] ll = CommonFun.PositionToLL(mask.getDataset().GetGeoTransform(), new int[]{x, y});
					int is_fn_all = (((int)fn_all.readByXY(x, y))>=0)?1:0;
					int is_fn_ourlier = (((int)fn_ourlier.readByXY(x, y))>=0)?1:0;
					int is_rn_ma = (((int)rn_ma.readByLL(ll[0], ll[1]))>=1)?1:0;
					int is_rn_kde_large_bw = (((int)rn_kde_large_bw.readByLL(ll[0], ll[1]))>=1)?1:0;
					int is_rn_kde_small_bw = (((int)rn_kde_small_bw.readByLL(ll[0], ll[1]))>=1)?1:0;
					int is_occ = (occset.contains(String.format("%d,%d", x, y)))?1:0;
					int is_rn_ma_cut_fn_all = (int) rn_ma_cut_fn_all.readByLL(ll[0], ll[1]);
					int is_rn_ma_cut_fn_ourlier = (int) rn_ma_cut_fn_ourlier.readByLL(ll[0], ll[1]);
					int is_rn_kde_large_bw_cut_fn_all = (int) rn_kde_large_bw_cut_fn_all.readByLL(ll[0], ll[1]);
					int is_rn_kde_large_bw_cut_fn_ourlier = (int) rn_kde_large_bw_cut_fn_ourlier.readByLL(ll[0], ll[1]);
					int is_rn_kde_small_bw_cut_fn_all = (int) rn_kde_small_bw_cut_fn_all.readByLL(ll[0], ll[1]);
					int is_rn_kde_small_bw_cut_fn_ourlier = (int) rn_kde_small_bw_cut_fn_ourlier.readByLL(ll[0], ll[1]);
					
					
					double value = layer.readByLL(ll[0], ll[1]);
					
					sb.append(String.format("%f,%f,%d,%d,%f,%d,%d,%d,%d,%d,%d,0,%d,%d,%d,%d,%d,%d%n", 
							ll[0], ll[1], x, y, value, is_fn_all,is_fn_ourlier,is_rn_ma,
							is_rn_kde_large_bw,is_rn_kde_small_bw,is_occ,
							is_rn_ma_cut_fn_all,is_rn_kde_large_bw_cut_fn_all,is_rn_kde_small_bw_cut_fn_all,
							is_rn_ma_cut_fn_ourlier,is_rn_kde_large_bw_cut_fn_ourlier,is_rn_kde_small_bw_cut_fn_ourlier));		
				}
			}
			CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Experiments/Heterosporis/CSVs/" + layer_name + ".csv");
		}
	}
	
	@Test
	public void filledFN() throws IOException{
		GeoTiffObject fn_all = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/NicheA_Models/HTSP_ll.tiff");
		GeoTiffObject fn_ourlier = new GeoTiffObject("/Users/huijieqiao/Experiments/Heterosporis/NicheA_Models/HTSP_Without_LL.tiff");
		ArrayList<String> ma = CommonFun.readFromFile("/Users/huijieqiao/Experiments/Heterosporis/MA/ma.csv");
		StringBuilder sb = new StringBuilder();
		for (String item : ma){
			String[] items = item.split(",");
			if (items.length==6){
				if (CommonFun.isDouble(items[0])){
					double lon = Double.valueOf(items[4]);
					double lat = Double.valueOf(items[5]);
					sb.append(String.format("%s,%d,%d%n", item, (int)fn_all.readByLL(lon, lat), (int)fn_all.readByLL(lon, lat)));
				}else{
					sb.append(String.format("%s,is_fn_all,is_fn_outlier%n", item));
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Experiments/Heterosporis/MA/ma_with_fn.csv");
		
	}
}
