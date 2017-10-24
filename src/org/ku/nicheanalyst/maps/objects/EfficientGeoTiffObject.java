package org.ku.nicheanalyst.maps.objects;

import java.io.File;
import java.io.FileNotFoundException;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.exceptions.UnsupportedFormatException;

public class EfficientGeoTiffObject {
	public String getFilename() {
		return filename;
	}

	public Dataset getDataset() {
		return dataset;
	}
	public Band getBand(){
		return band;
	}
	private String filename;
	private Dataset dataset = null;
	private Driver driver = null;
	private Band band = null;
	public EfficientGeoTiffObject(String filename) throws FileNotFoundException{
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
        
        
	}
	public void release(){
		dataset.delete();
		dataset = null;
	}
	public int getXSize(){
		return band.getXSize();
	}
	public int getYSize(){
		return band.getYSize();
	}
	public byte[] readByte(int xoff, int yoff, int xsize, int ysize){
		byte[] value = new byte[xsize * ysize];
		band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Byte, value);
		return value;
	}
	public short[] readShort(int xoff, int yoff, int xsize, int ysize){
		short[] value = new short[xsize * ysize];
		band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Int16, value);
		return value;
	}
	public float[] readFlost(int xoff, int yoff, int xsize, int ysize){
		float[] value = new float[xsize * ysize];
		band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Float32, value);
		return value;
	}
	public double[] readDouble(int xoff, int yoff, int xsize, int ysize){
		double[] value = new double[xsize * ysize];
		band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Float64, value);
		return value;
	}
	public int[] readInteger(int xoff, int yoff, int xsize, int ysize){
		int[] value = new int[xsize * ysize];
		band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Int32, value);
		return value;
	}
	public double[] readToDouble(int xoff, int yoff, int xsize, int ysize) throws UnsupportedFormatException{
		double[] value = new double[xsize * ysize];
		boolean isSupport = false;
		if ((band.getDataType()==gdalconst.GDT_Float64)
				||((band.getDataType()==gdalconst.GDT_CFloat64))){
			isSupport = true;
			band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Float64, value);
		}
		if (band.getDataType()==gdalconst.GDT_Byte){
			isSupport = true;
			byte[] values = new byte[xsize * ysize];
			band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Byte, values);
			for (int i=0;i<values.length;i++){
				value[i] = values[i];
			}
		}
		if ((band.getDataType()==gdalconst.GDT_Int16) 
				|| (band.getDataType()==gdalconst.GDT_UInt16) 
				|| (band.getDataType()==gdalconst.GDT_CInt16)){
			isSupport = true;
			short[] values = new short[xsize * ysize];
			band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Int16, values);
			for (int i=0;i<values.length;i++){
				value[i] = values[i];
			}
		}
		if ((band.getDataType()==gdalconst.GDT_Int32) 
				|| (band.getDataType()==gdalconst.GDT_UInt32) 
				|| (band.getDataType()==gdalconst.GDT_CInt32)){
			isSupport = true;
			int[] values = new int[xsize * ysize];
			band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Int32, values);
			for (int i=0;i<values.length;i++){
				value[i] = values[i];
			}
		}
		if ((band.getDataType()==gdalconst.GDT_Float32)
				||((band.getDataType()==gdalconst.GDT_CFloat32))){
			isSupport = true;
			float[] values = new float[xsize * ysize];
			band.ReadRaster(xoff, yoff, xsize, ysize, gdalconst.GDT_Float32, values);
			for (int i=0;i<values.length;i++){
				value[i] = values[i];
			}
		}
		if (!isSupport){
			throw new UnsupportedFormatException(String.format(Message.getString("unsupportedFormatException"), 
					band.getDataType(), gdalconst.GDT_Byte
					));
		}
		return value;
	}

	public double getNoData() {
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
}
