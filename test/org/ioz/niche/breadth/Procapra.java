package org.ioz.niche.breadth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import Jama.Matrix;

public class Procapra {
	@Test
	public void jaccardIndex() throws IOException{
		int var_count = 19;
		String valueFile = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/niche_breadth_results/standardized_allresult_R.csv";
//		values[bio][species][min,max];
		double[][][] values = new double[var_count][3][2];
		String[] speciesNames = new String[]{
				"Mongolian", "Przewalski", "Tibetan"
		};
		for (int i=0;i<var_count;i++){
			for (int j=0;j<3;j++){
				values[i][j][0] = Double.MAX_VALUE;
				values[i][j][1] = -1 * Double.MAX_VALUE;
			}
		}
		//Mongolian=0
		//Przewalski=1
		//Tibetan=2
		ArrayList<String> valueStr = CommonFun.readFromFile(valueFile);
		for (String valueStritem : valueStr){
			String[] valueItems = valueStritem.split(",");
			if (valueItems.length==4){
				if (CommonFun.isDouble(valueItems[0])){
					int bio = getBIO(valueItems[2]);
					int species = getSpecies(valueItems[3]);
					int min_max = getMinMax(valueItems[1]);
					double value = Double.valueOf(valueItems[0]).doubleValue();
					if (min_max==-1){
						continue;
					}
					if (min_max==0){
						values[bio - 1][species][0] = (values[bio - 1][species][0]>value)?value:values[bio - 1][species][0];
					}
					if (min_max==1){
						values[bio - 1][species][1] = (values[bio - 1][species][1]<value)?value:values[bio - 1][species][1];
					}
				}
			}
		}
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("Species1,Species2,Variables,Intersection,Union,JaccardIndex,JaccardDistance" + Const.LineBreak);
		for (int i=0;i<2;i++){
			for (int j=i+1;j<3;j++){
				for (int k=0;k<19;k++){
					double interSection = Math.min(values[k][i][1], values[k][j][1]) - Math.max(values[k][i][0], values[k][j][0]);
					double union = Math.max(values[k][i][1], values[k][j][1]) - Math.min(values[k][i][0], values[k][j][0]);
					double jaccardIndex = interSection / union;
					double jaccardDistance = 1 - jaccardIndex;
					sb.append(String.format("%s,%s,%s,%f,%f,%f,%f%n", speciesNames[i], speciesNames[j], 
							"BIO" + CommonFun.Integer2String(k + 1, 2), interSection, union, jaccardIndex, jaccardDistance));
				}
			}
		}
		String resultFile = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/niche_breadth_results/";
		CommonFun.writeFile(sb.toString(), resultFile + "jaccard.csv");
	}
	private int getMinMax(String string) {
		if (string.equalsIgnoreCase("Min")){
			return 0;
		}
		if (string.equalsIgnoreCase("Max")){
			return 1;
		}
		return -1;
	}
	private int getSpecies(String species) {
		if (species.equalsIgnoreCase("Mongolian")){
			return 0;
		}
		if (species.equalsIgnoreCase("Przewalski")){
			return 1;
		}
		if (species.equalsIgnoreCase("Tibetan")){
			return 2;
		}
		return -1;
	}
	@Test
	public void test(){
		String a="BIO11";
		System.out.println(getBIO(a));
	}
	private int getBIO(String value){
		String item = value.substring(3, 5);
		if (item.startsWith("0")){
			item = item.substring(1, 2);
		}
		return Integer.valueOf(item).intValue();
	}
	@Test
	public void calculateMVE() throws IOException{
		gdal.AllRegister();
		int var_count = 19;
		
		String[] presents = new String[] {
				"/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Mongolian_current_thresholded/present.tiff",
				"/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Przewalski_current_threshold/present.tiff",
				"/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Tibetan_current_threshold/present.tiff"
		};
		String[] speciesNames = new String[]{
				"Mongolian", "Przewalski", "Tibetan"
		};
		String bioclimLayer = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/current/standardized/bio%d.tif";
		String resultFile = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/niche_breadth_results/standardized_";
		StringBuilder allResultFile = new StringBuilder();
		allResultFile.append("Value,Types,Label,Species" + Const.LineBreak);
//		String bioclimLayer = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/current/bio%d.tif";
//		String resultFile = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/niche_breadth_results/";
		for (int species=0;species<presents.length;species++){
			double[][] allResults = new double[var_count][var_count * 3];
			for (int i=0;i<allResults.length;i++){
				for (int j=0;j<allResults[0].length;j++){
					allResults[i][j] = -9999;
				}
			}
			for (int i1=1;i1<var_count;i1++){
				for (int i2=i1+1;i2<=var_count;i2++){
					System.out.println(speciesNames[species] + "," + i1 + "," + i2);
					StringBuilder sb_statistics = new StringBuilder();
					String[] layers = new String[2];
					layers[0] = String.format(bioclimLayer, i1);
					layers[1] = String.format(bioclimLayer, i2);
					
					GeoTiffObject[] envLayers = new GeoTiffObject[layers.length];
					for (int i=0;i<layers.length;i++){
						envLayers[i] = new GeoTiffObject(layers[i]);
					}
					
					double threshold = 0.5;
					//get occurrent point count
				
					GeoTiffObject occLayer = new GeoTiffObject(presents[species]);
					int occCount = 0;
					for (int x=0;x<occLayer.getXSize();x++){
						for (int y=0;y<occLayer.getYSize();y++){
							double value = occLayer.readByXY(x, y);
			//				System.out.print(value + "\t");
							if (value>threshold){
								occCount++;
							}
						}
			//			System.out.println();
					}
					
					double[][] v = new double[layers.length][occCount];
					occCount = 0;
					for (int x=0;x<occLayer.getXSize();x++){
						for (int y=0;y<occLayer.getYSize();y++){
							double value = occLayer.readByXY(x, y);
							if (value>threshold){
								for (int i=0;i<layers.length;i++){
									v[i][occCount] = envLayers[i].readByXY(x, y);
								}
								occCount++;
							}
						}
					}
					MinimumVolumeEllipsoidResult mveMatrix = null;
					try{
						mveMatrix = MinimumVolumeEllipsoid.getMatrix(v, 3);
						Matrix center = mveMatrix.getCenter();
						
						sb_statistics.append(String.format("A:%n%s%n", CommonFun.MatrixtoString(mveMatrix.getA())));
						sb_statistics.append(String.format("C:%n%s%n", CommonFun.MatrixtoString(center)));
						
						
						
						int u_resolution = 100;
						int v_resolution = 100;
						double[] semi_axis = new double[envLayers.length];
						for (int i=0;i<layers.length;i++){
							semi_axis[i] = mveMatrix.getSemi_axis(i); 
						}
						Matrix eigenValue = mveMatrix.getEigenValue();
						Matrix eigenVector = mveMatrix.getEigenVector();
						sb_statistics.append(String.format("eigenValue:%n%s%n", CommonFun.MatrixtoString(eigenValue)));
						sb_statistics.append(String.format("eigenVector:%n%s%n", CommonFun.MatrixtoString(eigenVector)));
						float scale = 1;
						double[][] values = new double[u_resolution * (v_resolution + 1)][2];
						for (int i=0;i<values.length;i++){
							for (int j=0;j<values[0].length;j++){
								values[i][j] = -9999;
							}
						}
						float offset_x = (float) center.get(0, 0);
						float offset_y = (float) center.get(1, 0);
						float max_x = -1 * Float.MAX_VALUE;
						float min_x = Float.MAX_VALUE;
						float max_y = -1 * Float.MAX_VALUE;
						float min_y = Float.MAX_VALUE;
						
						for (int i=0;i<u_resolution;i++){
							for (int j=0;j<v_resolution;j++){ 
								float ui = (float) ((Math.PI*2) * (float)i/(float)u_resolution);
								float vi = (float) (Math.PI * (float)j/(float)v_resolution);
								float x = (float) (semi_axis[0] * Math.cos(ui) * Math.sin(vi));
								float y = (float) (semi_axis[1] * Math.sin(ui) * Math.sin(vi));
								double[][] t = new double[envLayers.length][1];
								t[0][0] = x * scale;
								t[1][0] = y * scale;
								Matrix p = new Matrix(t);
								p = eigenVector.times(p);
								x = (float) p.get(0, 0) / scale;
								y = (float) p.get(1, 0) / scale;
								x = x + offset_x;
								y = y + offset_y;
								values[i * (v_resolution + 1) + j][0] = x;
								values[i * (v_resolution + 1) + j][1] = y;
								
								max_x = (max_x<x)?x:max_x;
								min_x = (min_x>x)?x:min_x;
								max_y = (max_y<y)?y:max_y;
								min_y = (min_y>y)?y:min_y;
							}
							
				//			lines.setColor(i, new float[]{ui * 16f, ui * 16f, ui * 16f});
							
						}
						if ((CommonFun.equal(min_x, Float.MAX_VALUE, 1000))||(CommonFun.equal(max_x, -1 * Float.MAX_VALUE, 1000))){
							allResults[(i1 - 1)][(i2 - 1) * 3 + 0] = -9999;
							allResults[(i1 - 1)][(i2 - 1) * 3 + 1] = -9999;
							allResults[(i1 - 1)][(i2 - 1) * 3 + 2] = -9999;
						}else{
							allResults[(i1 - 1)][(i2 - 1) * 3 + 0] = min_x;
							allResults[(i1 - 1)][(i2 - 1) * 3 + 1] = max_x;
							allResults[(i1 - 1)][(i2 - 1) * 3 + 2] = offset_x;
							allResultFile.append(String.format("%f,%s,%s,%s%n", min_x, "Min", "BIO" + CommonFun.Integer2String(i1, 2), speciesNames[species]));
							allResultFile.append(String.format("%f,%s,%s,%s%n", max_x, "Max", "BIO" + CommonFun.Integer2String(i1, 2), speciesNames[species]));
							allResultFile.append(String.format("%f,%s,%s,%s%n", offset_x, "Center", "BIO" + CommonFun.Integer2String(i1, 2), speciesNames[species]));
						}
						if ((CommonFun.equal(min_x, Float.MAX_VALUE, 1000))||(CommonFun.equal(max_x, -1 * Float.MAX_VALUE, 1000))){
							allResults[(i2 - 1)][(i1 - 1) * 3 + 0] = -9999;
							allResults[(i2 - 1)][(i1 - 1) * 3 + 1] = -9999;
							allResults[(i2 - 1)][(i1 - 1) * 3 + 2] = -9999;
						}else{
							allResults[(i2 - 1)][(i1 - 1) * 3 + 0] = min_y;
							allResults[(i2 - 1)][(i1 - 1) * 3 + 1] = max_y;
							allResults[(i2 - 1)][(i1 - 1) * 3 + 2] = offset_y;
							allResultFile.append(String.format("%f,%s,%s,%s%n", min_y, "Min", "BIO" + CommonFun.Integer2String(i2, 2), speciesNames[species]));
							allResultFile.append(String.format("%f,%s,%s,%s%n", max_y, "Max", "BIO" + CommonFun.Integer2String(i2, 2), speciesNames[species]));
							allResultFile.append(String.format("%f,%s,%s,%s%n", offset_y, "Center", "BIO" + CommonFun.Integer2String(i2, 2), speciesNames[species]));
						}
						CommonFun.writeArray(values, String.format(resultFile + "%s_%d_%d.csv", speciesNames[species], i1, i2), ",");
					}catch(Exception e){
						e.printStackTrace();
						CommonFun.writeArray(v, String.format(resultFile + "Error_%s_%d_%d.csv", speciesNames[species], i1, i2), ",");
					}
					
				}
			}
			CommonFun.writeArray(allResults, String.format(resultFile + "%s_all.csv", speciesNames[species]), ",");
			
			
		}
		CommonFun.writeFile(allResultFile.toString(), resultFile + "allresult_R.csv");
	}
	
	@Test
	public void calculateMVEMiltiD() throws IOException{
		gdal.AllRegister();
		
		
		String[] presents = new String[] {
				"/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Mongolian_current_thresholded/present.tiff",
				"/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Przewalski_current_threshold/present.tiff",
				"/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Tibetan_current_threshold/present.tiff"
		};
		String[] speciesNames = new String[]{
				"Mongolian", "Przewalski", "Tibetan"
		};
		int var_count = 18;
		String bioclimLayer = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/current/standardized/bio%d.tif";
		String resultFile = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/niche_breadth_results/standardized_miltid_";
		StringBuilder allResultFile = new StringBuilder();
		allResultFile.append("Value,Types,Label,Species" + Const.LineBreak);
//		String bioclimLayer = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/current/bio%d.tif";
//		String resultFile = "/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/niche_breadth_results/";
		double[][] ranges = new double[var_count][2];
		for (int species=0;species<presents.length;species++){
			System.out.println(speciesNames[species]);
			double[][] allResults = new double[var_count][var_count * 3];
			for (int i=0;i<allResults.length;i++){
				for (int j=0;j<allResults[0].length;j++){
					allResults[i][j] = -9999;
				}
			}
			String[] layers = new String[var_count];
			GeoTiffObject[] envLayers = new GeoTiffObject[layers.length];
			
			for (int i1=1;i1<=var_count+1;i1++){
				if (i1==14){
					continue;
				}
				int index = (i1>14)?i1-1:i1;
				layers[index - 1] = String.format(bioclimLayer, i1);
				envLayers[index - 1] = new GeoTiffObject(layers[index - 1]);
				ranges[index - 1] = envLayers[index - 1].getMaxMin();
			}
//			for (int i1=1;i1<=var_count;i1++){
//				int index = i1;
//				layers[index - 1] = String.format(bioclimLayer, i1);
//				envLayers[index - 1] = new GeoTiffObject(layers[index - 1]);
//			}	
			double threshold = 0.5;
					//get occurrent point count
				
			GeoTiffObject occLayer = new GeoTiffObject(presents[species]);
			int occCount = 0;
			for (int x=0;x<occLayer.getXSize();x++){
				for (int y=0;y<occLayer.getYSize();y++){
					double value = occLayer.readByXY(x, y);
					if (value>threshold){
						occCount++;
					}
				}
			}
					
			double[][] v = new double[layers.length][occCount];
			occCount = 0;
			for (int x=0;x<occLayer.getXSize();x++){
				for (int y=0;y<occLayer.getYSize();y++){
					double value = occLayer.readByXY(x, y);
					if (value>threshold){
						for (int i=0;i<layers.length;i++){
							v[i][occCount] = envLayers[i].readByXY(x, y);
						}
						occCount++;
					}
				}
			}
			
			StringBuilder sb_statistics = new StringBuilder();
			MinimumVolumeEllipsoidResult mveMatrix = null;
			try{
				mveMatrix = MinimumVolumeEllipsoid.getMatrix(v, 3);
				Matrix center = mveMatrix.getCenter();
				
				sb_statistics.append(String.format("A:%n%s%n", CommonFun.MatrixtoString(mveMatrix.getA())));
				sb_statistics.append(String.format("C:%n%s%n", CommonFun.MatrixtoString(center)));
				
				
				
				int u_resolution = 100;
				int v_resolution = 100;
				double[] semi_axis = new double[envLayers.length];
				for (int i=0;i<layers.length;i++){
					semi_axis[i] = mveMatrix.getSemi_axis(i); 
				}
				Matrix eigenValue = mveMatrix.getEigenValue();
				Matrix eigenVector = mveMatrix.getEigenVector();
				sb_statistics.append(String.format("eigenValue:%n%s%n", CommonFun.MatrixtoString(eigenValue)));
				sb_statistics.append(String.format("eigenVector:%n%s%n", CommonFun.MatrixtoString(eigenVector)));
				
				//反复1000000次寻找极值
				double[][] ellipsoidRange = new double[var_count][2];
				for (int j=0;j<var_count;j++){
					ellipsoidRange[j][0] = Double.MAX_VALUE;
					ellipsoidRange[j][1] = -1 * Double.MAX_VALUE;
				}
				for (int i=0;i<1000000;i++){
					if ((i % 100000)==0){
						System.out.println(i);
					}
					double[] value = new double[var_count];
					for (int j=0;j<var_count;j++){
						value[j] = ranges[j][0] + Math.random() * (ranges[j][1] - ranges[j][0]); 
					}
					if (CommonFun.isInEllipsoid(mveMatrix.getA(), mveMatrix.getCenter(), value)){
						for (int j=0;j<var_count;j++){
							ellipsoidRange[j][0] = (ellipsoidRange[j][0]>value[j])?value[j]:ellipsoidRange[j][0];
							ellipsoidRange[j][1] = (ellipsoidRange[j][1]<value[j])?value[j]:ellipsoidRange[j][1];
						}
					}
				}
				CommonFun.writeArray(ellipsoidRange, String.format(resultFile + "%s.csv", speciesNames[species]), ",");
				CommonFun.writeArray(ranges, String.format(resultFile + "%s_range.csv", speciesNames[species]), ",");
			}catch(Exception e){
				e.printStackTrace();
				CommonFun.writeArray(v, String.format(resultFile + "Error_%s.csv", speciesNames[species]), ",");
			}
					
				
			CommonFun.writeArray(allResults, String.format(resultFile + "%s_all.csv", speciesNames[species]), ",");
			
			
		}
		CommonFun.writeFile(allResultFile.toString(), resultFile + "allresult_R.csv");
	}
	
	@Test
	public void standardlizeVariables() throws FileNotFoundException{
		String fitem = "/Users/huijieqiao/Dropbox/GISLayers/Bioclim/bio_10m_tiff/bio%d.tiff";
		String ftarget = "/Users/huijieqiao/Dropbox/GISLayers/Bioclim/bio_10m_tiff/standardlized/bio%d.tiff";
		for (int i=1;i<20;i++){
			double[] maxmin = new double[2];
			double[] maxmin_new = new double[]{-1, 1};
			maxmin[0] = Double.MAX_VALUE;
			maxmin[1] = Double.MIN_VALUE;
			int xsize = 0;
			int ysize = 0;
			GeoTiffObject geo = new GeoTiffObject(String.format(fitem, i));
			xsize = geo.getXSize();
			ysize = geo.getYSize();
			double[] mm = geo.getMaxMin();
			maxmin[0] = (maxmin[0]>mm[0])?mm[0]:maxmin[0];
			maxmin[1] = (maxmin[1]<mm[1])?mm[1]:maxmin[1];
			
			double[] values = new double[xsize * ysize];
			geo = new GeoTiffObject(String.format(fitem, i));
			for (int x=0;x<geo.getXSize();x++){
				for (int y=0;y<geo.getYSize();y++){
					double value = geo.readByXY(x, y);
					if (CommonFun.equal(value, geo.getNoData(), 1000)){
						values[y * xsize + x] = geo.getNoData();
					}else{
						double v = standardized(maxmin, maxmin_new, value);
						values[y * xsize + x] = v;
					}
				}
			}
			GeoTiffController.createTiff(String.format(ftarget, i), xsize, ysize, 
					geo.getDataset().GetGeoTransform(), values, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
		}
		
	}
	private double standardized(double[] maxmin_orginal, double[] maxmin_new, double value){
		return ((value - maxmin_orginal[0])/(maxmin_orginal[1] - maxmin_orginal[0])) * (maxmin_new[1] - maxmin_new[0]) + maxmin_new[0];
	}
}
