package org.ioz.niche.breadth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import Jama.Matrix;

public class NicheBreadthTest {
	@Test
	public void interplate() throws IOException{
		String file1 = "N00049.tif";
		String folder = "/Users/huijieqiao/Dropbox/Experiments/NicheBreadth/resample/longest_warm/bio12/";
		for (int i=50;i<150;i++){
			CommonFun.copyFile(folder + file1, folder + "N00" + CommonFun.Integer2String(i, 3) + ".tif");
		}
//		//将p1 - p50所有的文件向后移100位
//		for (int i=1;i<=50;i++){
//			CommonFun.copyFile(String.format("%s%s%s%s", folder, "P00", CommonFun.Integer2String(i, 3), ".tif"), 
//					String.format("%s%s%s%s", folder, "P00", CommonFun.Integer2String(i+100, 3), ".tif"));
//		}
//		//将p0复制100遍，替换p1-p50
//		for (int i=1;i<=100;i++){
//			CommonFun.copyFile(folder + "P00000.tif", 
//					String.format("%s%s%s%s", folder, "P00", CommonFun.Integer2String(i, 3), ".tif"));
//		}
	}
	@Test
	public void CreateVariables() throws FileNotFoundException{
		 gdal.AllRegister();
		 GeoTiffObject geo = new GeoTiffObject("/Users/huijieqiao/data/nichebreadth/bio1.tiff");
		 double[] values_low = new double[geo.getXSize() * geo.getYSize()];
		 double[] values_high = new double[geo.getXSize() * geo.getYSize()];
		 double[] range = geo.getMaxMin();
		 for (int x=0; x<geo.getXSize();x++){
			 for (int y=0;y<geo.getYSize();y++){
				 double value = geo.readByXY(x, y);
				 if (CommonFun.equal(value, geo.getNoData(), 1000)){
					 values_low[y * geo.getXSize() + x] = geo.getNoData();
					 values_high[y * geo.getXSize() + x] = geo.getNoData();
				 }else{
					 values_low[y * geo.getXSize() + x] = value - Math.random() * .05 * (range[1] - range[0]);
					 values_high[y * geo.getXSize() + x] = value + Math.random() * .05 * (range[1] - range[0]);
				 }
			 }
		 }
		 GeoTiffController.createTiff("/Users/huijieqiao/data/nichebreadth/bio1_glacial_test.tiff", 
				 geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(),
				 values_low, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
		 GeoTiffController.createTiff("/Users/huijieqiao/data/nichebreadth/bio1_Interglacial_test.tiff", 
				 geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(),
				 values_high, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
		
	}
	
	@Test
	public void StatVariables() throws FileNotFoundException{
		gdal.AllRegister();
		GeoTiffObject geo1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheBreadth/bio1_glacial.tif");
		GeoTiffObject geo2 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheBreadth/bio1_Interglacial.tif");
		int lowcount = 0;
		int highcount = 0;
		int equal = 0;
		for (int x=0; x<geo1.getXSize();x++){
			 for (int y=0;y<geo1.getYSize();y++){
				 if (CommonFun.equal(geo1.readByXY(x, y), geo1.getNoData(), 1000)){
					 continue;
				 }
				 if (CommonFun.equal(geo2.readByXY(x, y), geo2.getNoData(), 1000)){
					 continue;
				 }
				 if (CommonFun.equal(geo1.readByXY(x, y), geo2.readByXY(x, y), 1000)){
					 equal++;
					 continue;
				 }
				 if (geo1.readByXY(x, y)>geo2.readByXY(x, y)){
					 lowcount++;
					 continue;
				 }
				 if (geo1.readByXY(x, y)<geo2.readByXY(x, y)){
					 highcount++;
				 }
			 }
		}
		System.out.println(String.format("%d,%d,%d", lowcount, highcount, equal));
	}
	
	@Test
	public void testMatrix(){
		double[][] v = new double[3][1];
		for (int j=0;j<v.length;j++){
			for (int i=0;i<v[0].length;i++){
				v[j][i] = Math.random();
			}
		}
		Matrix m = new Matrix(v);
		String str = toString(m);
		System.out.println(str);
		System.out.println();
		m = loadFromString(str);
		str = toString(m);
		System.out.println(str);
		System.out.println();
		m = loadFromString(str);
		str = toString(m);
		System.out.println(str);
		
	}
	
	private Matrix loadFromString(String str) {
		String[] strs = str.split(Const.LineBreak);
		String[] colstr = strs[0].split(",");
		double[][] v = new double[strs.length][colstr.length];
		for (int i=0;i<strs.length;i++){
			colstr = strs[i].split(",");
			for (int j=0;j<colstr.length;j++){
				v[i][j] = Double.valueOf(colstr[j]).doubleValue();
			}
		}
		return new Matrix(v);
	}

	public String toString(Matrix m){
		StringBuilder sb = new StringBuilder();
		double[][] v = m.getArray();
		for (int j=0;j<v.length;j++){
			String t = "";
			for (int i=0;i<v[0].length;i++){
				t +=v[j][i] + " ";
			}
			sb.append(String.format("%s%n", t.trim().replace(" ", ",")));
		}
		return sb.toString();
	}
	private double standardized(double[] maxmin_orginal, double[] maxmin_new, double value){
		return ((value - maxmin_orginal[0])/(maxmin_orginal[1] - maxmin_orginal[0])) * (maxmin_new[1] - maxmin_new[0]) + maxmin_new[0];
	}
	@Test
	public void standardlizeVariables() throws FileNotFoundException{
		String folder = "/Users/huijieqiao/Dropbox/Experiments/ENM_Shape/";
		String folder_target = "/Users/huijieqiao/Dropbox/Experiments/ENM_Shape/normalization/";
		File f = new File(folder);
		double[] maxmin = new double[2];
		double[] maxmin_new = new double[]{-1, 1};
		maxmin[0] = Double.MAX_VALUE;
		maxmin[1] = Double.MIN_VALUE;
		int xsize = 0;
		int ysize = 0;
		for (File fitem : f.listFiles()){
			if (fitem.getName().toLowerCase().endsWith(".tiff")){
				GeoTiffObject geo = new GeoTiffObject(fitem.getAbsolutePath());
				xsize = geo.getXSize();
				ysize = geo.getYSize();
				double[] mm = geo.getMaxMin();
				maxmin[0] = (maxmin[0]>mm[0])?mm[0]:maxmin[0];
				maxmin[1] = (maxmin[1]<mm[1])?mm[1]:maxmin[1];
			}
		}
		double[] values = new double[xsize * ysize];
		for (File fitem : f.listFiles()){
			if (fitem.getName().toLowerCase().endsWith(".tiff")){
				GeoTiffObject geo = new GeoTiffObject(fitem.getAbsolutePath());
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
				GeoTiffController.createTiff(folder_target + "/" + fitem.getName(), xsize, ysize, 
						geo.getDataset().GetGeoTransform(), values, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
			}
			
		}
		
		
	}
	
	@Test
	public void generateRandomLayers() throws FileNotFoundException{
		File f = new File("/Users/huijieqiao/Dropbox/Papers/NicheOverlap_Procapra/原羚属/current");
		String f1 = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap_Procapra/原羚属/Last_inter_glacial";
		String f2 = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap_Procapra/原羚属/2080s_cccma_cgcm2_sres_a2a_IPCC_4AR(Stanford)";
		String resultFolder = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap_Procapra/原羚属/random";
		CommonFun.mkdirs(resultFolder, true);
		for (File fitem : f.listFiles()){
			if (fitem.getName().endsWith(".tif")){
				GeoTiffObject geo = new GeoTiffObject(fitem.getAbsolutePath());
				int xsize = geo.getXSize();
				int ysize = geo.getYSize();
				int[] values = new int[xsize * ysize];
				if (fitem.getName().contains("landcov")){
					HashSet<Integer> types = new HashSet<Integer>();
					for (int x=0;x<xsize;x++){
						for (int y=0;y<ysize;y++){
							Integer value = new Integer((int) geo.readByXY(x, y));
							types.add(value);
						}
					}
					int[] typeList = new int[types.size()];
					int i=0;
					for (Integer v : types){
						typeList[i] = v.intValue();
						i++;
					}
					for (int x=0;x<xsize;x++){
						for (int y=0;y<ysize;y++){
							double v = geo.readByXY(x, y);
							if (CommonFun.equal(v, geo.getNoData(), 1000)){
								values[y * xsize + x] = (int) geo.getNoData(); 
							}else{
								int index = (int) (Math.random() * typeList.length);
								if (index==typeList.length){
									index--;
								}
								values[y * xsize + x] = typeList[index];
							}
						}
					}
					
				}else{
					
					GeoTiffObject geo1 = new GeoTiffObject(f1 + "/" + fitem.getName());
					GeoTiffObject geo2 = new GeoTiffObject(f2 + "/" + fitem.getName());
					
					double[] min_max = geo.getMaxMin();
					min_max[0] = geo1.getMaxMin()[0]<min_max[0]?geo1.getMaxMin()[0]:min_max[0];
					min_max[1] = geo1.getMaxMin()[1]>min_max[1]?geo1.getMaxMin()[1]:min_max[1];
					min_max[0] = geo2.getMaxMin()[0]<min_max[0]?geo2.getMaxMin()[0]:min_max[0];
					min_max[1] = geo2.getMaxMin()[1]>min_max[1]?geo2.getMaxMin()[1]:min_max[1];
					
					for (int x=0;x<xsize;x++){
						for (int y=0;y<ysize;y++){
							double value = geo.readByXY(x, y);
							double result = geo.getNoData();
							if (!CommonFun.equal(value, geo.getNoData(), 1000)){
								result = Math.random() * (min_max[1] - min_max[0]) + min_max[0];
							}
							values[y * xsize + x] = (int) result;
						}
					}
				}
				GeoTiffController.createTiff(resultFolder + "/random_" + fitem.getName(),
						xsize, ysize, geo.getDataset().GetGeoTransform(),
						values, geo.getNoData(), gdalconst.GDT_Int32, geo.getDataset().GetProjection());
			}
		}
	}
	@Test
	public void testRoud(){
		System.out.println(Math.floor(0.2));
		System.out.println(Math.floor(1.4));
		System.out.println(Math.floor(1.5));
		System.out.println(Math.floor(1.9));
		
	}
	@Test
	public void generateRandom() throws FileNotFoundException{
		String sample = "/Users/huijieqiao/Dropbox/Experiments/ENM_Shape/normalization/bio12.tiff";
		String target = sample.replace(".tiff", "") + "_random.tiff";
		GeoTiffObject samplegeo = new GeoTiffObject(sample);
		double[] values = samplegeo.getValueArray();
		for (int x=0;x<samplegeo.getXSize();x++){
			for (int y=0;y<samplegeo.getYSize();y++){
				if (!CommonFun.equal(values[y * samplegeo.getXSize() + x], samplegeo.getNoData(), 1000)){
					values[y * samplegeo.getXSize() + x] = Math.random() * 2f - 1;
				}
			}
		}
		GeoTiffController.createTiff(target, 
				samplegeo.getXSize(), samplegeo.getYSize(), 
				samplegeo.getDataset().GetGeoTransform(), values, 
				samplegeo.getNoData(), samplegeo.getDataType(), samplegeo.getDataset().GetProjection());
	}
}
