package org.ioz.bai;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class TestClass {
	@Test
	public void getFossilValue() throws IOException{
		double lat = 36.58;
		double lon = 112.51;
		GeoTiffObject bio1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio1.tif");
		GeoTiffObject bio10 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio10.tif");
		GeoTiffObject bio11 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio11.tif");
		GeoTiffObject bio12 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio12.tif");
		GeoTiffObject bio13 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio13.tif");
		GeoTiffObject bio14 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio14.tif");
		GeoTiffObject bio7 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio7.tif");
		GeoTiffObject pc1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/1.tiff");
		GeoTiffObject pc2 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/2.tiff");
		GeoTiffObject pc3 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/3.tiff");
		GeoTiffObject pc4 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/4.tiff");
		GeoTiffObject pc5 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/5.tiff");
		GeoTiffObject pc6 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/6.tiff");
		GeoTiffObject pc7 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/7.tiff");
		
		GeoTiffObject maxent = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/models/maxent.asc");
		GeoTiffObject garp = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/models/garp.tif");
		GeoTiffObject glm = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/models/glm.asc");
		
		StringBuilder sb = new StringBuilder();
		sb.append("layer,x,y,lon,lat,value" + Const.LineBreak);
		int[] xy = CommonFun.LLToPosition(bio1.getDataset().GetGeoTransform(), new double[]{lon, lat});
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "bio1", xy[0], xy[1], lon, lat, bio1.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "bio10", xy[0], xy[1], lon, lat, bio10.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "bio11", xy[0], xy[1], lon, lat, bio11.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "bio12", xy[0], xy[1], lon, lat, bio12.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "bio13", xy[0], xy[1], lon, lat, bio13.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "bio14", xy[0], xy[1], lon, lat, bio14.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "bio7", xy[0], xy[1], lon, lat, bio7.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "pc1", xy[0], xy[1], lon, lat, pc1.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "pc2", xy[0], xy[1], lon, lat, pc2.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "pc3", xy[0], xy[1], lon, lat, pc3.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "pc4", xy[0], xy[1], lon, lat, pc4.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "pc5", xy[0], xy[1], lon, lat, pc5.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "pc6", xy[0], xy[1], lon, lat, pc6.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "pc7", xy[0], xy[1], lon, lat, pc7.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "maxent", xy[0], xy[1], lon, lat, maxent.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "garp", xy[0], xy[1], lon, lat, garp.readByXY(xy[0], xy[1])) + Const.LineBreak);
		sb.append(String.format("%s,%d,%d,%f,%f,%f", "glm", xy[0], xy[1], lon, lat, glm.readByXY(xy[0], xy[1])) + Const.LineBreak);
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/Acergriseum/G/fossil.csv");
	}
	@Test
	public void generateLL() throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("long,lat" + Const.LineBreak);
		StringBuilder sb_xy = new StringBuilder();
		sb_xy.append("x,y" + Const.LineBreak);
		
		StringBuilder sb_info = new StringBuilder();
		sb_info.append("long,lat,x,y,value,variable" + Const.LineBreak);
		
		GeoTiffObject bio1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio1.tif");
		GeoTiffObject bio10 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio10.tif");
		GeoTiffObject bio11 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio11.tif");
		GeoTiffObject bio12 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio12.tif");
		GeoTiffObject bio13 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio13.tif");
		GeoTiffObject bio14 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio14.tif");
		GeoTiffObject bio7 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio7.tif");
		for (int x=0;x<bio1.getXSize();x++){
			for (int y=0;y<bio1.getYSize();y++){
				int index = y * bio1.getXSize() + x;
				if (index<10000){
					sb_xy.append(String.format("%d,%d", x, y)+Const.LineBreak);
					double[] ll = CommonFun.PositionToLL(bio1.getDataset().GetGeoTransform(), new int[]{x, y});
					sb.append(String.format("%f,%f", ll[0], ll[1]) + Const.LineBreak);
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/Acergriseum/G/PPT/ll.txt");
		CommonFun.writeFile(sb_xy.toString(), "/Users/huijieqiao/Dropbox/Papers/Acergriseum/G/PPT/xy.txt");
		
		ArrayList<String> llstr = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Acergriseum-jw.csv");
		for (String llitem : llstr){
			String[] lls = llitem.split(",");
			if (lls.length==3){
				if (CommonFun.isDouble(lls[1])){
					double lon = Double.valueOf(lls[1]);
					double lat = Double.valueOf(lls[2]);
					int[] xy = CommonFun.LLToPosition(bio1.getDataset().GetGeoTransform(), new double[]{lon, lat});
					sb_info.append(String.format("%f,%f,%d,%d,%f,%s", lon, lat, xy[0], xy[1], bio1.readByXY(xy[0], xy[1]), "bio1") + Const.LineBreak);
					sb_info.append(String.format("%f,%f,%d,%d,%f,%s", lon, lat, xy[0], xy[1], bio10.readByXY(xy[0], xy[1]), "bio10") + Const.LineBreak);
					sb_info.append(String.format("%f,%f,%d,%d,%f,%s", lon, lat, xy[0], xy[1], bio11.readByXY(xy[0], xy[1]), "bio11") + Const.LineBreak);
					sb_info.append(String.format("%f,%f,%d,%d,%f,%s", lon, lat, xy[0], xy[1], bio12.readByXY(xy[0], xy[1]), "bio12") + Const.LineBreak);
					sb_info.append(String.format("%f,%f,%d,%d,%f,%s", lon, lat, xy[0], xy[1], bio13.readByXY(xy[0], xy[1]), "bio13") + Const.LineBreak);
					sb_info.append(String.format("%f,%f,%d,%d,%f,%s", lon, lat, xy[0], xy[1], bio14.readByXY(xy[0], xy[1]), "bio14") + Const.LineBreak);
					sb_info.append(String.format("%f,%f,%d,%d,%f,%s", lon, lat, xy[0], xy[1], bio7.readByXY(xy[0], xy[1]), "bio7") + Const.LineBreak);
					
				}
			}
		}
		CommonFun.writeFile(sb_info.toString(), "/Users/huijieqiao/Dropbox/Papers/Acergriseum/G/values.txt");
	}
	@Test
	public void writePCAbacktoRaster() throws IOException{
		ArrayList<String> ppt_bio_v = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio.csv");
		ArrayList<String> ppt_pca_v = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/pca.csv");
		GeoTiffObject bio1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio1.tif");
		GeoTiffObject bio10 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio10.tif");
		GeoTiffObject bio11 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio11.tif");
		GeoTiffObject bio12 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio12.tif");
		GeoTiffObject bio13 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio13.tif");
		GeoTiffObject bio14 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio14.tif");
		GeoTiffObject bio7 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/bio7.tif");
		double[] bio1_v = bio1.getValueArray();
		double[] bio10_v = bio10.getValueArray();
		double[] bio11_v = bio11.getValueArray();
		double[] bio12_v = bio12.getValueArray();
		double[] bio13_v = bio13.getValueArray();
		double[] bio14_v = bio14.getValueArray();
		double[] bio7_v = bio7.getValueArray();
		int index = 0;
		for (String v : ppt_bio_v){
			String[] vv = v.split(",");
			if (vv.length==7){
				if (CommonFun.isDouble(vv[0])){
					bio1_v[index] = Double.valueOf(vv[0]);
					bio10_v[index] = Double.valueOf(vv[1]);
					bio11_v[index] = Double.valueOf(vv[2]);
					bio12_v[index] = Double.valueOf(vv[3]);
					bio13_v[index] = Double.valueOf(vv[4]);
					bio14_v[index] = Double.valueOf(vv[5]);
					bio7_v[index] = Double.valueOf(vv[6]);
					index ++;
				}
			}
		}
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio1.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), bio1_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio10.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), bio10_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio11.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), bio11_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio12.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), bio12_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio13.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), bio13_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio14.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), bio14_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio7.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), bio7_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		
		
		
		GeoTiffObject pc1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/1.tiff");
		GeoTiffObject pc2 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/2.tiff");
		GeoTiffObject pc3 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/3.tiff");
		GeoTiffObject pc4 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/4.tiff");
		GeoTiffObject pc5 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/5.tiff");
		GeoTiffObject pc6 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/6.tiff");
		GeoTiffObject pc7 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/7.tiff");
		double[] pc1_v = pc1.getValueArray();
		double[] pc2_v = pc2.getValueArray();
		double[] pc3_v = pc3.getValueArray();
		double[] pc4_v = pc4.getValueArray();
		double[] pc5_v = pc5.getValueArray();
		double[] pc6_v = pc6.getValueArray();
		double[] pc7_v = pc7.getValueArray();
		index = 0;
		for (String v : ppt_pca_v){
			String[] vv = v.split(",");
			if (vv.length==7){
				if (CommonFun.isDouble(vv[0])){
					pc1_v[index] = Double.valueOf(vv[0]);
					pc2_v[index] = Double.valueOf(vv[1]);
					pc3_v[index] = Double.valueOf(vv[2]);
					pc4_v[index] = Double.valueOf(vv[3]);
					pc5_v[index] = Double.valueOf(vv[4]);
					pc6_v[index] = Double.valueOf(vv[5]);
					pc7_v[index] = Double.valueOf(vv[6]);
					index ++;
				}
			}
		}
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC1.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), pc1_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC2.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), pc2_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC3.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), pc3_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC4.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), pc4_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC5.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), pc5_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC6.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), pc6_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC7.tif", 
				bio1.getXSize(), bio1.getYSize(), bio1.getDataset().GetGeoTransform(), pc7_v, -9999, gdalconst.GDT_Float32, bio1.getDataset().GetProjection());
	}
	@Test
	public void generatePPT() throws IOException{
		double[] bio1 = {85, 151};
		double[] bio10 = {198, 275};
		double[] bio11 = {-3, 20};
		double[] bio7 = {250, 260};
		double[] bio12 = {845.6, 1050.9};
		double[] bio13 = {183.6, 229.4};
		double[] bio14 = {19.2, 21.2};
		int random_points = 10000;
		int i = 0;
		double[][] pcs = new double[7][7];
		ArrayList<String> av = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/eigenvectors.csv");
		int pci = 0;
		for (String pcstr : av){
			String[] pcstrs = pcstr.split(",");
			if (pcstrs.length==8){
				if (CommonFun.isDouble(pcstrs[1])){
					for (int j = 1; j<8;j++){
						System.out.println(pcstrs.length + "," + pci + "," + j);
						pcs[j-1][pci] = Double.valueOf(pcstrs[j]);
					}
					pci++;
				}
				
			}
			
		}
		GeoTiffObject mask = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PCA/1.tiff");
		double[] bio1_values = mask.getValueArray();
		double[] bio10_values = new double[bio1_values.length];
		double[] bio11_values = new double[bio1_values.length];
		double[] bio12_values = new double[bio1_values.length];
		double[] bio13_values = new double[bio1_values.length];
		double[] bio14_values = new double[bio1_values.length];
		double[] bio7_values = new double[bio1_values.length];
		double[] pc1_values = new double[bio1_values.length];
		double[] pc2_values = new double[bio1_values.length];
		double[] pc3_values = new double[bio1_values.length];
		double[] pc4_values = new double[bio1_values.length];
		double[] pc5_values = new double[bio1_values.length];
		double[] pc6_values = new double[bio1_values.length];
		double[] pc7_values = new double[bio1_values.length];
		for (int j = 0; j<bio1_values.length; j++){
			bio1_values[j] = -9999;
			bio10_values[j] = -9999;
			bio11_values[j] = -9999;
			bio12_values[j] = -9999;
			bio13_values[j] = -9999;
			bio14_values[j] = -9999;
			bio7_values[j] = -9999;
			pc1_values[j] = -9999;
			pc2_values[j] = -9999;
			pc3_values[j] = -9999;
			pc4_values[j] = -9999;
			pc5_values[j] = -9999;
			pc6_values[j] = -9999;
			pc7_values[j] = -9999;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("BIO1,BIO10,BIO11,BIO12,BIO13,BIO14,BIO7" + Const.LineBreak);
		while (i<random_points){
			double bio1v = Math.random() * (bio1[1] - bio1[0]) + bio1[0];
			double bio10v = Math.random() * (bio10[1] - bio10[0]) + bio10[0];
			double bio11v = Math.random() * (bio11[1] - bio11[0]) + bio11[0];
			double bio12v = Math.random() * (bio12[1] - bio12[0]) + bio12[0];
			double bio13v = Math.random() * (bio13[1] - bio13[0]) + bio13[0];
			double bio14v = Math.random() * (bio14[1] - bio14[0]) + bio14[0];
			double bio7v = Math.random() * (bio7[1] - bio7[0]) + bio7[0];
			double pc1 = pcs[0][0] * bio1v + pcs[1][0] * bio10v + pcs[2][0] * bio11v + pcs[3][0] * bio12v + pcs[4][0] * bio13v + pcs[5][0] * bio14v + pcs[6][0] * bio7v;
			double pc2 = pcs[0][1] * bio1v + pcs[1][1] * bio10v + pcs[2][1] * bio11v + pcs[3][1] * bio12v + pcs[4][1] * bio13v + pcs[5][1] * bio14v + pcs[6][1] * bio7v;
			double pc3 = pcs[0][2] * bio1v + pcs[1][2] * bio10v + pcs[2][2] * bio11v + pcs[3][2] * bio12v + pcs[4][2] * bio13v + pcs[5][2] * bio14v + pcs[6][2] * bio7v;
			double pc4 = pcs[0][3] * bio1v + pcs[1][3] * bio10v + pcs[2][3] * bio11v + pcs[3][3] * bio12v + pcs[4][3] * bio13v + pcs[5][3] * bio14v + pcs[6][3] * bio7v;
			double pc5 = pcs[0][4] * bio1v + pcs[1][4] * bio10v + pcs[2][4] * bio11v + pcs[3][4] * bio12v + pcs[4][4] * bio13v + pcs[5][4] * bio14v + pcs[6][4] * bio7v;
			double pc6 = pcs[0][5] * bio1v + pcs[1][5] * bio10v + pcs[2][5] * bio11v + pcs[3][5] * bio12v + pcs[4][5] * bio13v + pcs[5][5] * bio14v + pcs[6][5] * bio7v;
			double pc7 = pcs[0][6] * bio1v + pcs[1][6] * bio10v + pcs[2][6] * bio11v + pcs[3][6] * bio12v + pcs[4][6] * bio13v + pcs[5][6] * bio14v + pcs[6][6] * bio7v;
			bio1_values[i] = bio1v;
			bio10_values[i] = bio10v;
			bio11_values[i] = bio11v;
			bio12_values[i] = bio12v;
			bio13_values[i] = bio13v;
			bio14_values[i] = bio14v;
			bio7_values[i] = bio7v;
			pc1_values[i] = pc1;
			pc2_values[i] = pc2;
			pc3_values[i] = pc3;
			pc4_values[i] = pc4;
			pc5_values[i] = pc5;
			pc6_values[i] = pc6;
			pc7_values[i] = pc7;
			sb.append(String.format("%f,%f,%f,%f,%f,%f,%f", bio1v, bio10v, bio11v, bio12v, bio13v, bio14v, bio7v) + Const.LineBreak);
			i++;
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio.csv");
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio1.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), bio1_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio10.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), bio10_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());

		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio11.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), bio11_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());

		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio12.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), bio12_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());

		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio13.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), bio13_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());

		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio14.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), bio14_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());

		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/bio7.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), bio7_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());

		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC1.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), pc1_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC2.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), pc2_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC3.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), pc3_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC4.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), pc4_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC5.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), pc5_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC6.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), pc6_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Papers/Acergriseum/Environments/PPT/PC7.tif", mask.getXSize(), 
				mask.getYSize(), mask.getDataset().GetGeoTransform(), pc7_values, -9999, gdalconst.GDT_Float32, mask.getDataset().GetProjection());

	}
}
