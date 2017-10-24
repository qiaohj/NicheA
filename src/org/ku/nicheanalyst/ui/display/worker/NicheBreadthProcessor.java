/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 12, 2012 2:09:06 PM
 * Author:   Huijie Qiao
 *
 ******************************************************************************
 * Copyright (c) 2012, Huijie Qiao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************************************************/


package org.ku.nicheanalyst.ui.display.worker;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.NicheBreadthParameters;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 * This worker is used to generate the movement of a virtual species in three environmental variables.
 *
 */
public class NicheBreadthProcessor extends SwingWorker<Void, Void> {
	private String target;
	private NicheBreadthParameters parameters;
	private Exception msg;
	private Displayer theApp;
	public NicheBreadthProcessor(Displayer theApp, String target, NicheBreadthParameters parameters){
		this.target = target;
		this.parameters = parameters;
		this.theApp = theApp;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		CommonFun.mkdirs(this.target, true);
		CommonFun.mkdirs(this.target + "/Assembly", true);
		CommonFun.writeFile(this.parameters.getInformation(), String.format("%s/configuration.txt", this.target));
		StringBuilder curve_sb = new StringBuilder();
		curve_sb.append("long,lat,");
		for (int i=0;i<this.parameters.getVariableFolders().length;i++){
			File f = new File(this.parameters.getVariableFolders()[i]);
			curve_sb.append(f.getName() + ",");
		}
		curve_sb.append("time" + Const.LineBreak);
		
		StringBuilder html_sb = new StringBuilder();
		html_sb.append("<html><body>" + Const.LineBreak);
		html_sb.append("<table><tr><td>Index</td><td>Niche</td><td>Spread</td><td>Overlap</td></tr>" + Const.LineBreak);
		setProgress(0);
		int currentSeed = 0;
		GeoTiffObject sampleGeo = new GeoTiffObject(this.parameters.getVariableFolders()[0] + "/P00000.tif");
		int[][] species = new int[sampleGeo.getXSize()][sampleGeo.getYSize()];
		
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
		double[] ll = null;
		
		if (parameters.getInitialType()==0){
			//init first step
			while (currentSeed<parameters.getInitialRandomSpeciesSeeds()){
				setProgress(currentSeed * 10 / parameters.getInitialRandomSpeciesSeeds());
				int index_x = (int) Math.round(Math.random() * species.length);
				int index_y = (int) Math.round(Math.random() * species[0].length);
				if ((index_x>=species.length)||(index_y>=species[0].length)){
					continue;
				}
				if (species[index_x][index_y]==0){
					species[index_x][index_y] = 255;
					if (ll==null){
						ll = CommonFun.PositionToLL(sampleGeo.getDataset().GetGeoTransform(), new int[]{index_x, index_y});
					}
					currentSeed++;
				}
			}
		}
		
		if (parameters.getInitialType()==1){
			ArrayList<String> lls = CommonFun.readFromFile(parameters.getInitialManualSpeciesSeeds());
			for (String llstr : lls){
				String[] llstrs = llstr.replace(",", "\t").split("\t");
				if (llstrs.length>=2){
					if (CommonFun.isDouble(llstrs[0])&&(CommonFun.isDouble(llstrs[1]))){
						double[] long_lat = new double[]{
								Double.valueOf(llstrs[0]).doubleValue(), Double.valueOf(llstrs[1]).doubleValue()};
						int[] xy = CommonFun.LLToPosition(sampleGeo.getDataset().GetGeoTransform(), long_lat);
						if ((xy[0]<species.length)&&(xy[1]<species[0].length)){
							species[xy[0]][xy[1]] = 255;
							if (ll==null){
								ll = CommonFun.PositionToLL(sampleGeo.getDataset().GetGeoTransform(), xy);
							}
						}
					}
				}
			}
			GeoTiffObject[] geos = new GeoTiffObject[this.parameters.getVariableParameters().length];
			int currentYear = 0;
			
			for (int i = 0;i<this.parameters.getVariableParameters().length;i++){
				int vx = currentYear + this.parameters.getVariableParameters()[i].getStartingPoint();
				int c = 1;
				while (this.parameters.getVariableParameters()[i].getWarm_years()<vx){
					vx = (currentYear + this.parameters.getVariableParameters()[i].getStartingPoint()) - 
						c * (
								this.parameters.getVariableParameters()[i].getWarm_years()
								- this.parameters.getVariableParameters()[i].getCold_years());
					c++;
				}
				
				String tiffName = "";
				if (vx>=0){
					tiffName = String.format("%s/P%05d.tif", this.parameters.getVariableFolders()[i], vx);
				}else{
					tiffName = String.format("%s/N%05d.tif", this.parameters.getVariableFolders()[i], 
							Integer.valueOf(Math.abs(vx)).intValue());
				}
				geos[i] = new GeoTiffObject(tiffName);
				
//				System.out.println("Seed Tiff " + i + "=" + tiffName);
				
			}
			//move
			int[][] movedSpecies = new int[species.length][species[0].length];
			for (int x=0;x<sampleGeo.getXSize();x++){
				for (int y=0;y<sampleGeo.getYSize();y++){
					movedSpecies[x][y] = -255;
				}
			}
			boolean isFinished = false;
//			int co = 1;
//			int co2 = 1;
			while (!isFinished){
				
				for (int x=0;x<sampleGeo.getXSize();x++){
					for (int y=0;y<sampleGeo.getYSize();y++){
						if (movedSpecies[x][y]==-255){
							movedSpecies[x][y] = species[x][y];
						}
						
						if (species[x][y]!=255){
							continue;
						}
						for (int x2=(int) Math.round(x - this.parameters.getMigrationAbility() - 1);
								x2<=(int) Math.round(x + this.parameters.getMigrationAbility() + 1);
								x2++){
							for (int y2=(int) Math.round(y - this.parameters.getMigrationAbility() - 1);
								y2<=(int) Math.round(y + this.parameters.getMigrationAbility() + 1);
								y2++){
								if ((x2<0)||(x2>=species.length)||(y2<0)||(y2>=species[0].length)){
									continue;
								}
								if (species[x2][y2]==-255){
									continue;
								}
								double distance = CommonFun.getDistance(x, y, x2, y2);
								if (distance>this.parameters.getMigrationAbility()){
	//									movedSpecies[x2][y2] = 0;
								}else{
									double[] v = new double[this.parameters.getVariableParameters().length];
									for (int i=0;i<this.parameters.getVariableParameters().length;i++){
										v[i] = geos[i].readByXY(x2, y2);
									}
									boolean isinEllipsoid  = CommonFun.isInEllipsoid(this.parameters.getEllipsoids().getEigenMatrix(),
											this.parameters.getEllipsoids().getEigenValue(), 
											v);
									if (isinEllipsoid){
										movedSpecies[x2][y2] = 255;
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

			}
			for (GeoTiffObject geo : geos){
				geo.release();
			}
		}
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
			this.theApp.ShowAlert(Message.getString("no_enought_species_seed_error"));
			setProgress(100);
			return null;
		}
		int currentYear = 0;
		HashMap<String, GeoTiffObject> geoList = new HashMap<String, GeoTiffObject>();
		int duration = this.parameters.getDuration() / this.parameters.getVariableParameters()[0].getSamplingFrequency();
		
		while (currentYear<duration){
			
			
			GeoTiffObject[] geos = new GeoTiffObject[this.parameters.getVariableParameters().length];
			curve_sb.append(String.format("%f,%f,", ll[0], ll[1]));
			for (int i = 0;i<this.parameters.getVariableParameters().length;i++){
				int vx = currentYear + this.parameters.getVariableParameters()[i].getStartingPoint();
				int c = 1;
				while (this.parameters.getVariableParameters()[i].getWarm_years()<vx){
					vx = (currentYear + this.parameters.getVariableParameters()[i].getStartingPoint()) - 
						c * (
								this.parameters.getVariableParameters()[i].getWarm_years()
								- this.parameters.getVariableParameters()[i].getCold_years());
					c++;
				}
				
				String tiffName = "";
				if (vx>=0){
					tiffName = String.format("%s/P%05d.tif", this.parameters.getVariableFolders()[i], vx);
				}else{
					tiffName = String.format("%s/N%05d.tif", this.parameters.getVariableFolders()[i], 
							Integer.valueOf(Math.abs(vx)).intValue());
				}
//				if (currentYear==0){
//					System.out.println("Expe Tiff " + i + "=" + tiffName);
//				}
				if (geoList.containsKey(tiffName)){
					geos[i] = geoList.get(tiffName);
				}else{
					geos[i] = new GeoTiffObject(tiffName);
					geoList.put(tiffName, geos[i]);
				}
				curve_sb.append(String.format("%f,", geos[i].readByLL(ll[0], ll[1])));
			}
			curve_sb.append(String.format("%d%n", currentYear));
			int[][] movedSpecies = new int[species.length][species[0].length];
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
						for (int x2=(int) Math.round(x - this.parameters.getMigrationAbility() - 1);
								x2<=(int) Math.round(x + this.parameters.getMigrationAbility() + 1);
								x2++){
							for (int y2=(int) Math.round(y - this.parameters.getMigrationAbility() - 1);
								y2<=(int) Math.round(y + this.parameters.getMigrationAbility() + 1);
								y2++){
								if ((x2<0)||(x2>=species.length)||(y2<0)||(y2>=species[0].length)){
									continue;
								}
								if (species[x2][y2]==-255){
									continue;
								}
								double distance = CommonFun.getDistance(x, y, x2, y2);
								if (distance>this.parameters.getMigrationAbility()){
//									movedSpecies[x2][y2] = 0;
								}else{
									movedSpecies[x2][y2] = 255;
								}
							}
						}
					}
					double[] v = new double[this.parameters.getVariableParameters().length];
					for (int i=0;i<this.parameters.getVariableParameters().length;i++){
						v[i] = geos[i].readByXY(x, y);
					}
					boolean isinEllipsoid  = CommonFun.isInEllipsoid(this.parameters.getEllipsoids().getEigenMatrix(),
							this.parameters.getEllipsoids().getEigenValue(), 
							v);
					if (isinEllipsoid){
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
			String folder = String.format("%s/%05d", this.target, currentYear);
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
			
			setProgress(currentYear * 90 / duration + 10);
			currentYear ++;
		}
		for (GeoTiffObject geo : geoList.values()){
			geo.release();
		}
		html_sb.append("</table>" + Const.LineBreak);
		html_sb.append("</body></html>" + Const.LineBreak);
		CommonFun.writeFile(html_sb.toString(), String.format("%s/Assembly/assembly.html", target));
		CommonFun.writeFile(curve_sb.toString(), String.format("%s/curve.csv", target));
		sampleGeo.release();
		setProgress(100);
		return null;
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

	@Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        try {
            get();
        } catch (Exception e) {
        	e.printStackTrace();
        	msg = e;
            firePropertyChange("done-exception", null, e);
        }
    }

	public Exception getException(){
		return msg;
	}

	

	
	
	
	
}
