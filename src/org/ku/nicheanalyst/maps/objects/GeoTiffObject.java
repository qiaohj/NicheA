package org.ku.nicheanalyst.maps.objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.Transformer;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.osr.CoordinateTransformation;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;

import rjava.rcaller.RCaller;
import rjava.rcaller.RCode;

public class GeoTiffObject {
	public String getFilename() {
		return filename;
	}

	public Dataset getDataset() {
		return dataset;
	}

	private String filename;
	private HashMap<Double, Integer> values;
	private Dataset dataset = null;
	private Driver driver = null;
	private Band band = null;
	private double[] valueArray;
	private int[] maxMin;
	public GeoTiffObject(String filename) throws FileNotFoundException{
		File f = new File(filename);
		if (!f.exists()){
			throw new FileNotFoundException(filename);
		}
		this.filename = filename;
		gdal.AllRegister();
//		System.out.println(gdal.GetDriverCount());
//		
//		for (int i=0;i<gdal.GetDriverCount();i++){
//			System.out.println(gdal.GetDriver(i).getLongName());
//		}
        driver = gdal.GetDriverByName("GTiff");
        dataset = gdal.Open(filename, gdalconst.GA_ReadOnly);
        
        band = dataset.GetRasterBand(1);
        
        values = new HashMap<Double, Integer>();
        int x = band.getXSize();
		int y = band.getYSize();
        valueArray = new double[x * y];
        band.ReadRaster(0, 0, x, y, valueArray);
        
	}
	public void release(){
		dataset.delete();
		dataset = null;
	}
	public Band getBand(){
		return band;
	}
	public int getXSize(){
		return band.getXSize();
	}
	public int getYSize(){
		return band.getYSize();
	}
	private void getValueStat(){
		int x = band.getXSize();
		int y = band.getYSize();
		for (int j=0;j<y;j++){
			for (int i=0;i<x;i++){			
				double value = valueArray[j * x + i];
				if (values.containsKey(value)){
					values.put(value, values.get(value) + 1);
				}else{
					values.put(value, 1);
				}
			}
		}
	}
	public int getDataCount(){
		int count = 0;
		Band band = dataset.GetRasterBand(1);
		int x = band.getXSize();
		int y = band.getYSize();
		double[] valueArray = new double[x * y];
		band.ReadRaster(0, 0, x, y, valueArray);
		for (int j=0;j<y;j++){
			for (int i=0;i<x;i++){			
				double value = valueArray[j * x + i];
				if (!CommonFun.equal(value, getNoData(), 1000)){
					count++;
				}
			}
		} 
		return count;
	}
	public int[] transferFromMask(Dataset maskdataset, double[] xy){
		Transformer t = new Transformer(maskdataset, dataset, null);
		t.TransformPoint(0, xy);
		return new int[]{(int)xy[0], (int)xy[1]};
	}
	public double ReadfromMask(Dataset maskdataset, int maskx, int masky){
		Band maskband = maskdataset.GetRasterBand(1);
		int z = 0;
		int[] xy = transferFromMask(maskdataset, new double[]{maskx, masky, z});
		return valueArray[xy[1] * band.getXSize() + xy[0]];
	}
	public double readByXY(int x, int y){
		try{
			if ((x<0)||(x>=getXSize())||(y<0)||(y>=getYSize())){
				return getNoData();
			}
			double value = valueArray[y * band.getXSize() + x];
			return value;
		}catch (Exception e){
			return getNoData();
		}
	}
	public double[] readLLByPosition(int x, int y){
		double[] geoTransform = new double[6];
		dataset.GetGeoTransform(geoTransform);
		return CommonFun.PositionToLL(geoTransform, new int[]{x,y});
	}
	public int[] readPositionByLL(double longitude, double latitude){
		double[] geoTransform = new double[6];
		dataset.GetGeoTransform(geoTransform);
		return CommonFun.LLToPosition(geoTransform, new double[]{longitude, latitude});
	}
	public double readByLL(double longitude, double latitude){
		double[] geoTransform = new double[6];
		dataset.GetGeoTransform(geoTransform);
		int[] xy = CommonFun.LLToPosition(geoTransform, new double[]{longitude, latitude});
		
        int x = xy[0];
        int y = xy[1];
        return readByXY(x, y);
	}
	public double getNoData(){
		Double[] val = new Double[]{0.0};
		band.GetNoDataValue(val);
		if (val==null){
			return Const.NoData;
		}
		if (val[0]==null){
			return Const.NoData;
		}
		return (double)val[0];
	}
	public void showInfo(){		
		//dataset.SetProjection(wkt);
		System.out.println("Raster dataset parameters:");
        System.out.println("  Projection: " + dataset.GetProjectionRef());
        System.out.println("  RasterCount: " + dataset.getRasterCount());
        System.out.println("  RasterSize (" + dataset.getRasterXSize() + "," + dataset.getRasterYSize() + ")");

        System.out.format("  DataType (%s)%d\r\n", gdal.GetDataTypeName(band.getDataType()), band.getDataType());
        double[] argout = new double[]{Double.MAX_VALUE, -1 * Double.MAX_VALUE};
        band.ComputeRasterMinMax(argout);
        Double[] min = new Double[]{0.0};
        band.GetMinimum(min);
        Double[] max = new Double[]{0.0};
        band.GetMaximum(max);        
        System.out.format("Computed: Min:%f\tMax:%f\r\n", argout[0], argout[1]);
        System.out.format("Stored  : Min:%f\tMax:%f\r\n", min[0], max[0]);
        Double[] nodata = new Double[]{0.0};
        band.GetNoDataValue(nodata);
        System.out.format("No Data Value: %f\r\n", nodata[0]);
        double[] mean = new double[]{0.0};
        double[] stddev = new double[]{0.0};
        double[] min2 = new double[]{0.0};
        double[] max2 = new double[]{0.0};
        band.ComputeStatistics(true, min2, max2, mean, stddev);
        
        System.out.format("Computed: Min:%f\tMax:%f\tMean:%f\tStddev:%f\r\n", min2[0], max2[0], mean[0], stddev[0]);
        int totalCount = band.getXSize() * band.getYSize();
        System.out.format("Total count:%d\r\n", totalCount);
        getValueStat();
        for (double value: values.keySet()){
        	System.out.format("Value:%d\tCount:%d\tPercent:%f\r\n", (int)value, values.get(value), (double)((int)values.get(value)) * 100 / totalCount);
        }
	}

	public double[] getMaxMin() {
		return getMaxMin(getNoData());
	}

	/**
	 * @throws IOException 
	 * 
	 */
	public void createInfo(HashSet<Double> ignoreList) throws IOException {
		if (ignoreList==null){
			ignoreList = new HashSet<Double>();
		}
		StringBuilder sb = new StringBuilder();
		double[] maxmin = getMaxMin();
		sb.append(String.format("xsize:%d ysize:%d nodata:%d min:%f max:%f", getXSize(), getYSize(), getNoData(), maxmin[0], maxmin[1])
				+ Const.LineBreak);
		HashMap<Double, Integer> ignoreCount = new HashMap<Double, Integer>();
		
		for (Double ignore : ignoreList){
			ignoreCount.put(ignore, 0);
		}
		ignoreCount.put((double)getNoData(), 0);
		
		
		RCaller caller = new RCaller();
		caller.setRscriptExecutable(ConfigInfo.getInstance().getRScript());
		
		RCode code = new RCode();
		code.clear();
		String tempfile = ConfigInfo.getInstance().getTemp() + "/temp.txt";
		StringBuilder sb2 = new StringBuilder();
		for (double value : valueArray){
			if ((!CommonFun.equal(value, getNoData(), 1000))&&(!ignoreList.contains(value))){
				sb2.append(value + Const.LineBreak);
			}else{
				ignoreCount.put(value, ignoreCount.get(value) + 1);
			}
		}
		
		sb.append("ignore list:");
		for (Double key : ignoreCount.keySet()){
			sb.append(String.format("Key:%f/Value:%d\t", key, ignoreCount.get(key)));
		}
		sb.append(Const.LineBreak);
		CommonFun.writeFile(sb.toString(), filename + ".info.txt");
		CommonFun.writeFile(sb2.toString(), tempfile);
		code.addRCode("x<-read.table(file='"+tempfile+"')");
		code.addRCode("png('" + filename.replace("\\", "/") + ".info.png')");
		code.addRCode("hist(x$V1)");
		code.addRCode("dev.off()");
		caller.setRCode(code);
		caller.runOnly();
	}
	
	public double[] getValueArray() {
		return valueArray;
	}

	/**
	 * @param integer
	 * @param x
	 * @param y
	 */
	public void writeByXY(int value, int x, int y) {
		try{
			if ((x<0)||(x>=getXSize())||(y<0)||(y>=getYSize())){
				return;
			}
			valueArray[y * band.getXSize() + x] = value;
		}catch (Exception e){
			e.printStackTrace();
			return;
		}
		
	}

	/**
	 * @return
	 */
	public int[] binarize(int gap) {
		int[] result = new int[valueArray.length];
		for (int i=0;i<result.length;i++){
			result[i] = (valueArray[i]>gap)?255:0;
		}
		return result;
	}
	public int[][] getRectangle(){
		int[][] rectangle = new int[2][2];
		double x1, x2, y1, y2;
		String pszProjection;
		double[] adfGeoTransform = new double[6];
		CoordinateTransformation hTransform = null;

		/* -------------------------------------------------------------------- */
		/*      Transform the point into georeferenced coordinates.             */
		/* -------------------------------------------------------------------- */
		adfGeoTransform = dataset.GetGeoTransform();
		{
			pszProjection = dataset.GetProjectionRef();

			x1 = adfGeoTransform[0] + adfGeoTransform[1] * 0
					+ adfGeoTransform[2] * 0;
			y1 = adfGeoTransform[3] + adfGeoTransform[4] * 0
					+ adfGeoTransform[5] * 0;
			
			x2 = adfGeoTransform[0] + adfGeoTransform[1] * getXSize()
				+ adfGeoTransform[2] * getXSize();
			y2 = adfGeoTransform[3] + adfGeoTransform[4] * getYSize()
				+ adfGeoTransform[5] * getYSize();
			rectangle[0][0] = (int) Math.min(x1, x2);
			rectangle[0][1] = (int) Math.min(y1, y2);
			rectangle[1][0] = (int) Math.max(x1, x2);
			rectangle[1][1] = (int) Math.max(y1, y2);
			
		}
		return rectangle;
	}
	
	/**
	 * @param rectangle
	 * @return
	 */
	public int[][] convertRectangle(int[][] rectangle) {
		int[][] selfRectangle = getRectangle();
		double scaleX = (double)(selfRectangle[1][0] - selfRectangle[0][0]) / (double)getXSize();
		double scaleY = (double)(selfRectangle[1][1] - selfRectangle[0][1]) / (double)getYSize();
		int[][] newRectangle = new int[2][2];
		for (int i=0;i<2;i++){
			for (int j=0;j<2;j++){
				newRectangle[i][j] = (int)((j==0)?(selfRectangle[0][0] + rectangle[i][j] * scaleX)
						:(selfRectangle[1][1] - rectangle[i][j] * scaleY));
			}
		}
		return newRectangle;
	}

	/**
	 * @param nodata
	 * @return
	 */
	public double[] getMaxMin(double nodata) {
		double max = -1 * Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		for (int j=0;j<getYSize();j++){
			for (int i=0;i<getXSize();i++){
				double value = readByXY(i, j);
				if (CommonFun.equal(value, nodata, 1000)){
					continue;
				}
				max = (value>max)?value:max;
				min = (value<min)?value:min;
			}
		}
		return new double[]{min, max};
	}

	public int getDataType() {
		return band.getDataType();
	}

	
}
