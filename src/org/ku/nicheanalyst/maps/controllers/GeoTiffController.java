/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 5, 2012 6:40:18 PM
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


package org.ku.nicheanalyst.maps.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.ku.nicheanalyst.common.CommandExecutor;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;

/**
 * @author Huijie Qiao
 *
 */
public class GeoTiffController {
	public static void createTiff(String filename, int xsize, int ysize, double[] geotransform, int[] array, double nodata, int datatype, String projection){
		gdal.AllRegister();
		Driver driver = gdal.GetDriverByName("GTiff");
		Dataset dataset = driver.Create(filename, xsize, ysize, 1, datatype);
		dataset.SetGeoTransform(geotransform);
		dataset.SetProjection(projection);
        Band band = dataset.GetRasterBand(1);
        
        band.SetNoDataValue(nodata);
		band.WriteRaster(0, 0, xsize, ysize, array);
		band.FlushCache();
		dataset.FlushCache();
		dataset.delete();
		dataset = null;
		driver = null;
	}

	public static void createTiff(String filename, int xsize, int ysize, double[] geotransform, double[] array, double nodata, int datatype, String projection){
		gdal.AllRegister();
		Driver driver = gdal.GetDriverByName("GTiff");
		Dataset dataset = driver.Create(filename, xsize, ysize, 1, datatype);
		dataset.SetGeoTransform(geotransform);
		dataset.SetProjection(projection);
        Band band = dataset.GetRasterBand(1);
        
        band.SetNoDataValue(nodata);
		band.WriteRaster(0, 0, xsize, ysize, array);
		band.FlushCache();
		dataset.FlushCache();
		dataset.delete();
		dataset = null;
		driver = null;
	}
	public static String cut(String srcFilename, String targetFilename, 
			int xmin, int ymin, int xmax, int ymax, int nodata){
		String[] command = new String[10];
		command[0] = ConfigInfo.getInstance().getGdalWarp();
		command[1] = "-te";
		command[2] = String.format("%d", xmin);
		command[3] = String.format("%d", ymin);
		command[4] = String.format("%d", xmax);
		command[5] = String.format("%d", ymax);
		command[6] = "-dstnodata";
		command[7] = String.format("\"%d\"", nodata);
		command[8] = srcFilename;
		command[9] = targetFilename;
		
		return execute(command, srcFilename, targetFilename);
	}
	private static String execute(String[] command, String srcFilename, String targetFilename){
		File f = new File(srcFilename);
		if (!f.exists()){
			return null;
		}
		CommonFun.rmfile(targetFilename);
		String sb = "";
		for (String s : command){
			sb += s + " ";
		}
		System.out.println(sb);
		String output = CommandExecutor.run(command);
		System.out.println(output);
		return output;
	}
	public static String scale(String srcFilename, String targetFilename, 
			int width, int height, String resampling_method, int nodata){
		String[] command = new String[10];
		command[0] = ConfigInfo.getInstance().getGdalWarp();
		command[1] = "-ts";
		command[2] = String.format("%d", width);
		command[3] = String.format("%d", height);
		command[4] = "-r";
		command[5] = resampling_method;
		command[6] = "-dstnodata";
		command[7] = String.format("\"%d\"", nodata);
		command[8] = srcFilename;
		command[9] = targetFilename;
		
		
		return execute(command, srcFilename, targetFilename);
	}
	
	public static String cutAndScaleTiffWithRectangle(String srcFilename, String targetFilename,
			int xmin, int ymin, int xmax, int ymax, int width, int height, String resampling_method, int nodata) {
		String tempFile = ConfigInfo.getInstance().getTemp() + "/temp.tiff";
		String result = cut(srcFilename, tempFile, xmin, ymin, xmax, ymax, nodata) + Const.LineBreak;
		result += scale(tempFile, targetFilename, width, height, resampling_method, nodata);
		return result;
	}
	public static String toGeoTIFF(String srcFilename, String targetFilename){
		String[] command = new String[5];
		command[0] = ConfigInfo.getInstance().getGdalTranslate();
		command[1] = "-of";
		command[2] = "GTiff";
		command[3] = srcFilename;
		command[4] = targetFilename;
		return execute(command, srcFilename, targetFilename);
//		return StringUtils.join(command, " ");
	}
	public static String toAAIGrid(String srcFilename, String targetFilename){
		String[] command = new String[5];
		command[0] = ConfigInfo.getInstance().getGdalTranslate();
		command[1] = "-of";
		command[2] = "AAIGrid";
		command[3] = srcFilename;
		command[4] = targetFilename;
		
		return  execute(command, srcFilename, targetFilename);
//		return StringUtils.join(command, " ");
	}
	
	public static String toGIF(String srcFilename, String targetFilename){
		String[] command = new String[5];
		command[0] = ConfigInfo.getInstance().getGdalTranslate();
		command[1] = "-of";
		command[2] = "GIF";
		command[3] = srcFilename;
		command[4] = targetFilename;
		
		return execute(command, srcFilename, targetFilename);
//		return StringUtils.join(command, " ");
	}

	/**
	 * @param string
	 * @param string2
	 */
	public static String toPNG(String srcFilename, String targetFilename) {
		String[] command = new String[5];
		command[0] = ConfigInfo.getInstance().getGdalTranslate();
		command[1] = "-of";
		command[2] = "PNG";
		command[3] = srcFilename;
		command[4] = targetFilename;
		
		return execute(command, srcFilename, targetFilename);
//		return StringUtils.join(command, " ");
	}

	/**
	 * @param string
	 * @param string2
	 * @param i
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static String resizePNG(String srcFilename, String targetFilename, int resolutoin) throws IOException, InterruptedException {
		String command = String.format(ConfigInfo.getInstance().getImageMagick() + " %s -resize %dx%d %s", srcFilename, resolutoin, resolutoin, targetFilename);
		String size = "1024x768";
		ProcessBuilder pb = new ProcessBuilder(ConfigInfo.getInstance().getImageMagick(), srcFilename, "-resize", resolutoin +"x" + resolutoin, targetFilename);
		pb.redirectErrorStream(true);

		Process p = pb.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while((line=br.readLine())!=null){
//		    System.out.println(line);
		}
//		System.out.println(p.waitFor());
		return command;
	}
	

}
