package org.ioz.virtual.enm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class VirtualScenario {
	@Test
	public void createVirtualEnvironment() throws FileNotFoundException{
		String folder = "/Users/huijieqiao/Dropbox/Experiments/VirtualScenario/VirtualEnvironmentLayers/";
		String sample = "sample.tiff";
		GeoTiffObject samplefile = new GeoTiffObject(folder + sample);
		double[] range = new double[]{0, 1};
		int number_of_layers = 2;
		
		for (int i=0;i<number_of_layers;i++){
			double[] data = samplefile.getValueArray();
			for (int j=0;j<data.length;j++){
				data[j] = Math.random() * (range[1] - range[0]) + range[0];
			}
			GeoTiffController.createTiff(
					folder + "random_layer_" + i + ".tiff",  
					samplefile.getXSize(), samplefile.getYSize(), 
					samplefile.getDataset().GetGeoTransform(), 
					data, samplefile.getNoData(), 
					gdalconst.GDT_Float32, samplefile.getDataset().GetProjection());
		}
		
	}
	@Test
	public void createVirtualSpecies() throws IOException{
		String folder = "/Users/huijieqiao/Dropbox/Experiments/VirtualScenario/VirtualSpecies/FullDataset/";
		String ll = "ll.txt";
		ArrayList<String> lls = CommonFun.readFromFile(folder + ll);
		double target_percent = 0.1 * Double.valueOf(lls.size());
		int repeat = 1;
		for (int i=0;i<repeat;i++){
			ArrayList<String> newll = new ArrayList<String>();
			ArrayList<String> newll2 = new ArrayList<String>();
			ArrayList<String> newll3 = new ArrayList<String>();
			newll3.add("#id	label	long	lat	abundance");
			int ii = 1;
			while (target_percent>=newll.size()){
				int index = (int) (lls.size() * Math.random());
				if (index<lls.size()){
					String llstr = lls.get(index);
					if (llstr.contains(",")){
						newll.add("vs" + i + "," + llstr);
						newll2.add(llstr);
						ii++;
						newll3.add(String.format("%d\tvs.%d\t%s\t1", ii, i, llstr.replace(",", "\t")));
					}
					lls.remove(index);
				}
			}
			
			CommonFun.writeFile(newll, folder + i + "/maxent_ll_" + i + ".csv");
			CommonFun.writeFile(newll2, folder + i + "/ll_" + i + ".csv");
			CommonFun.writeFile(newll3, folder + i + "/openmodeller_ll_" + i + ".txt");
		}
	}
	@Test
	public void createAHalfDataset() throws IOException{
		String folder = "/Users/huijieqiao/Dropbox/Experiments/VirtualScenario/VirtualSpecies/HalfDataset/";
		String ll = "ll.txt";
		ArrayList<String> lls = CommonFun.readFromFile(folder + ll);
		ArrayList<String> values = CommonFun.readFromFile(folder + "value.txt");
		ArrayList<String> Bottom = new ArrayList<String>();
		ArrayList<String> Bottomleft = new ArrayList<String>();
		ArrayList<String> Bottomright = new ArrayList<String>();
		ArrayList<String> Left = new ArrayList<String>();
		ArrayList<String> Right = new ArrayList<String>();
		ArrayList<String> Top = new ArrayList<String>();
		ArrayList<String> Topleft = new ArrayList<String>();
		ArrayList<String> Topright = new ArrayList<String>();
		for (int i=0;i<lls.size();i++){
			String[] valuess = values.get(i).split(",");
			if (valuess.length==3){
				if ((Double.valueOf(valuess[0])>=0.5)&&(Double.valueOf(valuess[1]))>=0.5){
					
				}
			}
		}
	}
}
