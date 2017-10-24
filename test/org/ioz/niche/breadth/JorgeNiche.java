package org.ioz.niche.breadth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.gdal.gdalconst.gdalconst;
import org.jdom.JDOMException;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GompertzCurveParameters;
import org.ku.nicheanalyst.dataset.NicheBreadthParameters;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class JorgeNiche {
	private ArrayList<File> getAllTiff(File folder){
		ArrayList<File> f = new ArrayList<File>();
		for (File item : folder.listFiles()){
			if (item.isDirectory()){
				if (item.getName().startsWith("bio")){
					f.add(item);
				}else{
					ArrayList<File> newf = getAllTiff(item);
					for (File item2 : newf){
						f.add(item2);
					}
				}
			}
		}
		return f;
	}
	@Test
	public void getNicheBreadth() throws IOException, JDOMException{
		File variableFolder = new File("/Users/huijieqiao/Dropbox/Experiments/NicheBreadth/resample");
		String original_target = "/Users/huijieqiao/Dropbox/Experiments/NicheBreadth/results";
		ArrayList<String> niche_definations = CommonFun.readFromFile(
				"/Users/huijieqiao/Dropbox/Experiments/NicheBreadth/faoniches_min3.csv");
		int inital_duration = 600000;
		int migrationAbility = 1;
		int migrationInterval = 100;
		ArrayList<String> initialManualSpeciesSeeds = 
				CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Experiments/NicheBreadth/virtual_species/inital_seed.csv");
		
		ArrayList<File> variableFolders = getAllTiff(variableFolder);
		ArrayList<File> variable_bio1s = new ArrayList<File>();
		ArrayList<File> variable_bio12s = new ArrayList<File>();
		for (File fitem : variableFolders){
			if (fitem.getName().equals("bio1")){
				variable_bio1s.add(fitem);
			}else{
				variable_bio12s.add(fitem);
			}
		}
		for (String niche_defination : niche_definations){
			System.out.println("Working on " + niche_defination);
			JorgeNicheDefination niche = new JorgeNicheDefination(niche_defination, initialManualSpeciesSeeds);
			if (!niche.isGood()){
				System.out.println("Niche defination is not good enough!");
				continue;
			}
			for (File variable_bio1 : variable_bio1s){
//				System.out.println("Working on " + variable_bio1.getAbsolutePath());
				for (File variable_bio12 : variable_bio12s){
//					System.out.println("Working on " + variable_bio12.getAbsolutePath());
					GompertzCurveParameters parameter_bio1 = new GompertzCurveParameters(variable_bio1.getAbsolutePath() + "/parameters.xml");
					GompertzCurveParameters parameter_bio12 = new GompertzCurveParameters(variable_bio12.getAbsolutePath() + "/parameters.xml");
					
					
					
					String target = original_target + "/" + niche.getId() + 
							"/bio1." + variable_bio1.getParentFile().getName() + 
							"-bio12." + variable_bio12.getParentFile().getName();
					File tmp = new File(target);
					if (tmp.exists()){
						continue;
					}
					CommonFun.mkdirs(target, true);
					CommonFun.mkdirs(target + "/Assembly", true);
					NicheBreadthParameters parameters = new NicheBreadthParameters(
							new GompertzCurveParameters[]{parameter_bio1, parameter_bio12},
							new String[]{variable_bio1.getAbsolutePath(), variable_bio12.getAbsolutePath()}, 
							niche, inital_duration, migrationAbility, migrationInterval);
					
					StringBuilder curve_sb = new StringBuilder();
					curve_sb.append("long,lat,");
					for (int i=0;i<parameters.getVariableFolders().length;i++){
						File f = new File(parameters.getVariableFolders()[i]);
						curve_sb.append(f.getName() + ",");
					}
					curve_sb.append("time" + Const.LineBreak);
					
					StringBuilder html_sb = new StringBuilder();
					html_sb.append("<html><body>" + Const.LineBreak);
					html_sb.append("<table><tr><td>Index</td><td>Niche</td><td>Spread</td><td>Overlap</td></tr>" + Const.LineBreak);
					
					GeoTiffObject sampleGeo = new GeoTiffObject(parameters.getVariableFolders()[0] + "/P00000.tif");
					int[][] species = new int[sampleGeo.getXSize()][sampleGeo.getYSize()];
					//初始化物种分布变量
					for (int x=0;x<sampleGeo.getXSize();x++){
						for (int y=0;y<sampleGeo.getYSize();y++){
							double value = sampleGeo.readByXY(x, y);
							if (CommonFun.equal(value, sampleGeo.getNoData(), 1000)){
								species[x][y] = -255;
							}else{
								species[x][y] = 0;
							}
						}
					}
					//按照物种初始分布变更species变量
					double[] ll = null;
					for (double[] lls : niche.getLls()){
						int[] xy = CommonFun.LLToPosition(sampleGeo.getDataset().GetGeoTransform(), lls);
						if ((xy[0]<species.length)&&(xy[1]<species[0].length)){
							species[xy[0]][xy[1]] = 255;
							if (ll==null){
								ll = CommonFun.PositionToLL(sampleGeo.getDataset().GetGeoTransform(), xy);
							}
						}
					}
					
					GeoTiffObject[] geos = new GeoTiffObject[parameters.getVariableParameters().length];
					int currentYear = 0;
					
					for (int i = 0;i<parameters.getVariableParameters().length;i++){
						int vx = currentYear + parameters.getVariableParameters()[i].getStartingPoint();
						int c = 1;
						while (parameters.getVariableParameters()[i].getWarm_years()<vx){
							vx = (currentYear + parameters.getVariableParameters()[i].getStartingPoint()) - 
								c * (
										parameters.getVariableParameters()[i].getWarm_years()
										- parameters.getVariableParameters()[i].getCold_years());
							c++;
						}
						
						String tiffName = "";
						if (vx>=0){
							tiffName = String.format("%s/P%05d.tif", parameters.getVariableFolders()[i], vx);
						}else{
							tiffName = String.format("%s/N%05d.tif", parameters.getVariableFolders()[i], 
									Integer.valueOf(Math.abs(vx)).intValue());
						}
						geos[i] = new GeoTiffObject(tiffName);
					}
					//move
					int[][] movedSpecies = new int[species.length][species[0].length];
					for (int x=0;x<sampleGeo.getXSize();x++){
						for (int y=0;y<sampleGeo.getYSize();y++){
							movedSpecies[x][y] = -255;
						}
					}
					boolean isFinished = false;
//					int co = 1;
//					int co2 = 1;
					
					//初始状态
					while (!isFinished){
						
						for (int x=0;x<sampleGeo.getXSize();x++){
							for (int y=0;y<sampleGeo.getYSize();y++){
								if (movedSpecies[x][y]==-255){
									movedSpecies[x][y] = species[x][y];
								}
								
								if (species[x][y]!=255){
									continue;
								}
								for (int x2=(int) Math.round(x - parameters.getMigrationAbility() - 1);
										x2<=(int) Math.round(x + parameters.getMigrationAbility() + 1);
										x2++){
									for (int y2=(int) Math.round(y - parameters.getMigrationAbility() - 1);
										y2<=(int) Math.round(y + parameters.getMigrationAbility() + 1);
										y2++){
										if ((x2<0)||(x2>=species.length)||(y2<0)||(y2>=species[0].length)){
											continue;
										}
										if (species[x2][y2]==-255){
											continue;
										}
										double distance = CommonFun.getDistance(x, y, x2, y2);
										if (distance>parameters.getMigrationAbility()){
			//									movedSpecies[x2][y2] = 0;
										}else{
											double[] v = new double[parameters.getVariableParameters().length];
											for (int i=0;i<parameters.getVariableParameters().length;i++){
												v[i] = geos[i].readByXY(x2, y2);
											}
											boolean isinRectangle  = isInRectangle(
													niche.getTemp_min(), niche.getTemp_max(), niche.getPrcp_min(), niche.getPrcp_max(),
													v[0], v[1]);
											if (isinRectangle){
												movedSpecies[x2][y2] = 255;
//												GeoTiffController.createTiff("/Users/huijieqiao/temp/" + co + "." + x + "-" + y + "." + x2 + "-" + y2 + ".tif", 
//														sampleGeo.getXSize(), 
//														sampleGeo.getYSize(), 
//														sampleGeo.getDataset().GetGeoTransform(), 
//														Array2value(movedSpecies), -255, gdalconst.GDT_Int32);
//												co++;
											}
										}
									}
								}
								
							}
						}
						isFinished = true;
						for (int x=0;x<sampleGeo.getXSize();x++){
							for (int y=0;y<sampleGeo.getYSize();y++){
								if (species[x][y]!=movedSpecies[x][y]){
									isFinished = false;
								}
								species[x][y] = movedSpecies[x][y];
							}
						}
//						GeoTiffController.createTiff("/Users/huijieqiao/temp/" + co2 + ".tif", 
//								sampleGeo.getXSize(), 
//								sampleGeo.getYSize(), 
//								sampleGeo.getDataset().GetGeoTransform(), 
//								Array2value(species), -255, gdalconst.GDT_Int32);
//						co2++;
						
						
						
					}//end for 初始的seeds的扩展
					
					int seedCount = 0;
					for (int x=0;x<sampleGeo.getXSize();x++){
						for (int y=0;y<sampleGeo.getYSize();y++){
							if (species[x][y]==255){
								seedCount++;
								break;
							}
						}
						if (seedCount>0){
							break;
						}
					}
					if (seedCount==0){
						System.out.println("no enough seeds");
						continue;
					}
					currentYear = 0;
					HashMap<String, GeoTiffObject> geoList = new HashMap<String, GeoTiffObject>();
					int duration = parameters.getDuration() / parameters.getVariableParameters()[0].getSamplingFrequency();
					
					while (currentYear<duration){
						
						
						geos = new GeoTiffObject[parameters.getVariableParameters().length];
						curve_sb.append(String.format("%f,%f,", ll[0], ll[1]));
						for (int i = 0;i<parameters.getVariableParameters().length;i++){
							int vx = currentYear + parameters.getVariableParameters()[i].getStartingPoint();
							int c = 1;
							while (parameters.getVariableParameters()[i].getWarm_years()<vx){
								vx = (currentYear + parameters.getVariableParameters()[i].getStartingPoint()) - 
									c * (
											parameters.getVariableParameters()[i].getWarm_years()
											- parameters.getVariableParameters()[i].getCold_years());
								c++;
							}
							
							String tiffName = "";
							if (vx>=0){
								tiffName = String.format("%s/P%05d.tif", parameters.getVariableFolders()[i], vx);
							}else{
								tiffName = String.format("%s/N%05d.tif", parameters.getVariableFolders()[i], 
										Integer.valueOf(Math.abs(vx)).intValue());
							}
//							if (currentYear==0){
//								System.out.println("Expe Tiff " + i + "=" + tiffName);
//							}
							if (geoList.containsKey(tiffName)){
								geos[i] = geoList.get(tiffName);
							}else{
								geos[i] = new GeoTiffObject(tiffName);
								geoList.put(tiffName, geos[i]);
							}
							curve_sb.append(String.format("%f,", geos[i].readByLL(ll[0], ll[1])));
						}
						curve_sb.append(String.format("%d%n", currentYear));
						movedSpecies = new int[species.length][species[0].length];
						int[][] distribution = new int[sampleGeo.getXSize()][sampleGeo.getYSize()];
						for (int x=0;x<sampleGeo.getXSize();x++){
							for (int y=0;y<sampleGeo.getYSize();y++){
								movedSpecies[x][y] = species[x][y];
							}
						}
						for (int x=0;x<sampleGeo.getXSize();x++){
							for (int y=0;y<sampleGeo.getYSize();y++){
								if (species[x][y]==-255){
									movedSpecies[x][y] = -255;
									distribution[x][y] = -255;
									continue;
								}
								if ((currentYear!=0)&&(species[x][y]==255)){
									for (int x2=(int) Math.round(x - parameters.getMigrationAbility() - 1);
											x2<=(int) Math.round(x + parameters.getMigrationAbility() + 1);
											x2++){
										for (int y2=(int) Math.round(y - parameters.getMigrationAbility() - 1);
											y2<=(int) Math.round(y + parameters.getMigrationAbility() + 1);
											y2++){
											if ((x2<0)||(x2>=species.length)||(y2<0)||(y2>=species[0].length)){
												continue;
											}
											if (species[x2][y2]==-255){
												continue;
											}
											double distance = CommonFun.getDistance(x, y, x2, y2);
											if (distance>parameters.getMigrationAbility()){
//												movedSpecies[x2][y2] = 0;
											}else{
												movedSpecies[x2][y2] = 255;
											}
										}
									}
								}
								double[] v = new double[parameters.getVariableParameters().length];
								for (int i=0;i<parameters.getVariableParameters().length;i++){
									v[i] = geos[i].readByXY(x, y);
								}
								boolean isinRectangle  = isInRectangle(
										niche.getTemp_min(), niche.getTemp_max(), niche.getPrcp_min(), niche.getPrcp_max(),
										v[0], v[1]);
								if (isinRectangle){
									distribution[x][y] = 255;
								}else{
									distribution[x][y] = 0;
								}
							}
						}
						for (int x=0;x<sampleGeo.getXSize();x++){
							for (int y=0;y<sampleGeo.getYSize();y++){
								if (species[x][y]==-255){
									continue;
								}
								if ((distribution[x][y]==255)&&(movedSpecies[x][y]==255)){
									species[x][y] = 255;
								}else{
									species[x][y] = 0;
								}
							}
						}
						String folder = String.format("%s/%05d", target, currentYear);
						CommonFun.mkdirs(folder, true);
						int[] values = Array2value(movedSpecies);
						GeoTiffController.createTiff(folder + "/spread.tiff", 
								sampleGeo.getXSize(), sampleGeo.getYSize(), 
								sampleGeo.getDataset().GetGeoTransform(), 
								values, -255, gdalconst.GDT_Int16, sampleGeo.getDataset().GetProjection());
						values = Array2value(distribution);
						GeoTiffController.createTiff(folder + "/niche.tiff", 
								sampleGeo.getXSize(), sampleGeo.getYSize(), 
								sampleGeo.getDataset().GetGeoTransform(), 
								values, -255, gdalconst.GDT_Int16, sampleGeo.getDataset().GetProjection());
						values = Array2value(species);
						GeoTiffController.createTiff(folder + "/overlap.tiff", 
								sampleGeo.getXSize(), sampleGeo.getYSize(), 
								sampleGeo.getDataset().GetGeoTransform(), 
								values, -255, gdalconst.GDT_Int16, sampleGeo.getDataset().GetProjection());
						String niche_tiff = String.format("niche_%05d.png", currentYear);
						String overlap_tiff = String.format("overlap_%05d.png", currentYear);
						String spread_tiff = String.format("spread_%05d.png", currentYear);
						GeoTiffController.toPNG(folder + "/niche.tiff", String.format("%s/Assembly/%s", target, niche_tiff));
						GeoTiffController.toPNG(folder + "/overlap.tiff", String.format("%s/Assembly/%s", target, overlap_tiff));
						GeoTiffController.toPNG(folder + "/spread.tiff", String.format("%s/Assembly/%s", target, spread_tiff));
						html_sb.append(String.format("<tr>" +
								"<td>%d</td>" +
								"<td><img src='%s'></td>" +
								"<td><img src='%s'></td>" +
								"<td><img src='%s'></td>" +
								"</tr>%n", currentYear, niche_tiff, spread_tiff, overlap_tiff));
						
						currentYear ++;
					}
					html_sb.append("</table>" + Const.LineBreak);
					html_sb.append("</body></html>" + Const.LineBreak);
					CommonFun.writeFile(html_sb.toString(), String.format("%s/Assembly/assembly.html", target));
					CommonFun.writeFile(curve_sb.toString(), String.format("%s/curve.csv", target));
				}//end for bio12 循环
			}//end for bio1 循环
		}//end for jorge species 循环
		
		
		
		
	}
	private boolean isInRectangle(int temp_min, int temp_max,
			int prcp_min, int prcp_max, double temp, double prcp) {
		if ((temp_min<=temp)&&(temp_max>=temp)&&(prcp_min<=prcp)&&(prcp_max>=prcp)){
			return true;
		}
		return false;
	}
	private int[] Array2value(int[][] species) {
		int[] values = new int[species.length * species[0].length];
		for (int x = 0;x<species.length;x++){
			for (int y = 0;y<species[0].length;y++){
				values[y * species.length + x] = species[x][y];
			}
		}
		return values;
	}
}
