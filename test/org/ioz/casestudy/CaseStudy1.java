package org.ioz.casestudy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class CaseStudy1 {
	@Test
	public void getRange() throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("clade,variable,min,max" + Const.LineBreak);
		File occurrencesFolder = new File("/Users/huijieqiao/Dropbox/Papers/NicheA_Case_Study/StudyCase1/points");
		File tiffFolder = new File("/Users/huijieqiao/Dropbox/Papers/NicheA_Case_Study/StudyCase1/PCA");
		for (File occurrenceFile : occurrencesFolder.listFiles()){
			if (occurrenceFile.getName().equalsIgnoreCase("allxy.csv")){
				continue;
			}
			if (occurrenceFile.isFile()&&occurrenceFile.getAbsolutePath().endsWith(".csv")){
				ArrayList<String> llstrs = CommonFun.readFromFile(occurrenceFile.getAbsolutePath());
				for (File tiffFile : tiffFolder.listFiles()){
					if (tiffFile.getName().endsWith(".tiff")){
						GeoTiffObject geo = new GeoTiffObject(tiffFile.getAbsolutePath());
						double min = Double.MAX_VALUE;
						double max = Double.MIN_VALUE;
						for (String llstr : llstrs){
							String[] lls = llstr.split(",");
							if (lls.length==3){
								if (CommonFun.isDouble(lls[1])){
									double value = geo.readByLL(Double.valueOf(lls[1]), Double.valueOf(lls[2]));
									if (CommonFun.equal(value, geo.getNoData(), 1000)){
										continue;
									}
									min = (min>value)?value:min;
									max = (max>value)?max:value;
								}
							}
						}
						sb.append(String.format("%s,%s,%f,%f%n", 
								occurrenceFile.getName().replace(".csv", ""), tiffFile.getName().replace(".tiff", ""), min, max));
					}
				}
				
				
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheA_Case_Study/StudyCase1/results/range/pca.csv");
		
	}
}
