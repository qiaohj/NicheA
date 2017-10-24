package org.ku.niche.transferability.fengxiao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class SampleHandler {
	@Test
	public void changeFileName() throws IOException{
		File folder = new File("/home/huijieqiao/New_Experiments/raw_projection");
		ArrayList<String> targets = new ArrayList<String>();
		targets.add(".MA");
		targets.add(".MA2");
		targets.add(".kde_large_bw");
		targets.add(".kde_small_bw");
		StringBuilder sb = new StringBuilder();
		for (String target : targets){
			for (File f : folder.listFiles()){
				if (f.getName().contains(target)){
					sb.append("mv " + f.getName() + " " + f.getName().replace(target, target.replace(".", "_")) + Const.LineBreak);
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "/home/huijieqiao/New_Experiments/rename.sh");
	}
	
	@Test
	public void changeFileNameMA2() throws IOException{
		File folder = new File("/home/huijieqiao/New_Experiments/MA2/output");
		ArrayList<String> targets = new ArrayList<String>();
		targets.add(".MA");
		StringBuilder sb = new StringBuilder();
		for (String target : targets){
			for (File f : folder.listFiles()){
				if (f.getName().contains(target)){
					sb.append("mv " + f.getName() + " " + f.getName().replace(target, target.replace(".MA", "_MA2")) + Const.LineBreak);
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "/home/huijieqiao/New_Experiments/rename_MA2.sh");
	}
	@Test
	public void fill_in_data() throws IOException{
		GeoTiffObject pc1 = new GeoTiffObject("/Users/huijieqiao/temp/bio_2.5m_Eurasia/PCA/PC1.tif");
		GeoTiffObject pc2 = new GeoTiffObject("/Users/huijieqiao/temp/bio_2.5m_Eurasia/PCA/PC2.tif");
		GeoTiffObject pc3 = new GeoTiffObject("/Users/huijieqiao/temp/bio_2.5m_Eurasia/PCA/PC3.tif");
		ArrayList<String> nbs = new ArrayList<String>();
		nbs.add("Large");
		nbs.add("Median");
		
		
		for (String nb : nbs){
			for (int i=1; i<=9; i++){
				StringBuilder sb = new StringBuilder();
				String llfile = "/Users/huijieqiao/temp/Split/split_"+nb+"_"+i+".csv";
				ArrayList<String> lines = CommonFun.readFromFile(llfile);
				HashSet<String> keys = new HashSet<String>();
				for (int j=0; j<lines.size(); j++){
					if (j==0){
						sb.append("lon,lat,x,y,pc1,pc2,pc3,region" + Const.LineBreak);
					}else{
						String[] strsplit = lines.get(j).split(",");
						double lon = Double.valueOf(strsplit[0]);
						double lat = Double.valueOf(strsplit[1]);
						int region = Integer.valueOf(strsplit[4]);
						int[] xy = CommonFun.LLToPosition(pc1.getDataset().GetGeoTransform(), new double[]{lon, lat});
						keys.add(String.format("%d,%d", xy[0], xy[1]));
						sb.append(String.format("%f,%f,%d,%d,%f,%f,%f,%d%n", lon, lat, xy[0], xy[1], 
								pc1.readByXY(xy[0], xy[1]), pc2.readByXY(xy[0], xy[1]), pc3.readByXY(xy[0], xy[1]), region));
					}
				}
				
				for (int x=0;x<pc1.getXSize(); x++){
					for (int y=0;y<pc1.getYSize(); y++){
						
						double v = pc1.readByXY(x, y);
						if (!CommonFun.equal(v, pc1.getNoData(), 10000)){
							String key = String.format("%d,%d", x, y);
							if (!keys.contains(key)){
								double[] ll = CommonFun.PositionToLL(pc1.getDataset().GetGeoTransform(), new int[] {x, y});
								sb.append(String.format("%f,%f,%d,%d,%f,%f,%f,%d%n", ll[0], ll[1], x, y, 
										pc1.readByXY(x, y), pc2.readByXY(x, y), pc3.readByXY(x, y), 0));
							}
						}
					}
				}
				CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/temp/Split/data/"+nb+"_"+i+".csv");
			}
		}
		
	}
}
