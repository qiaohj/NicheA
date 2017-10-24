package org.ku.nicheanalyst.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class ConfigInfo {
	private static ConfigInfo instance;
	private Properties p = null;
	private File propertiesFile;
	private ConfigInfo(){
		ArrayList<String> propertyList;
		
		p = new Properties();
		try {
			if (Message.getString("version")==null){
				propertiesFile = null;
			}else{
				propertiesFile = new File(CommonFun.getUserFolder(), "nichea." + 
						Message.getString("version").replace(" ", ".") + 
						".properties");
			}	
			if (propertiesFile==null){
				p.load(this.getClass().getResourceAsStream("/nichea.properties"));
				p.store(new FileOutputStream(propertiesFile), "Niche Analyst");
			}else{
				if (propertiesFile.exists()){
					p.load(new FileInputStream(propertiesFile));
					
				}else{
					p.load(this.getClass().getResourceAsStream("/nichea.properties"));
					p.store(new FileOutputStream(propertiesFile), "Niche Analyst");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static final ConfigInfo getInstance(){
		if (instance==null){
			try{
				instance=new ConfigInfo();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		return instance;
	}
	public final String getTemp(){
		File temp = new File("");

	    try {
			temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
			if (!(temp.delete())) {
				throw new IOException("Could not delete temp file: "
						+ temp.getAbsolutePath());
			}

			if (!(temp.mkdir())) {
				throw new IOException("Could not create temp directory: "
						+ temp.getAbsolutePath());
			}
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return temp.getAbsolutePath();
	}
	public final int getBackgroundPointCount(){
		return Integer.valueOf(p.getProperty("backgroupdpointcount")).intValue();
	}
	public final int getMaxPoint(){
		return Integer.valueOf(p.getProperty("maxpoint")).intValue();
	}
	public final String getGdalWarp(){
		String path = getExecutePath(p.getProperty("default_gdalwarp"), "gdalwarp");
		if (path!=null){
			return path;
		}
		if (CommonFun.isWindows()){
			File f = new File(CommonFun.getCurrentPath() + "supports/gdal/bin/gdal/apps/gdalwarp.exe");
			if (f.exists()){
				return f.getAbsolutePath();
			}
		}
		return p.getProperty("gdalwarp");
		
	}
	private String getExecutePath(String default_string, String command){
		if (default_string==null){
			return null;
		}
		String[] default_gdal_translate = default_string.split(";");
		for (String p : default_gdal_translate){
			if (p.trim().equals("")){
				continue;
			}
			File f = new File(p);
			if (f.exists()&&p.replace(".exe", "").toLowerCase().endsWith(command.toLowerCase())){
				return p;
			}
		}
		return null;
	}
	public final String getGdalTranslate(){
		String path = getExecutePath(p.getProperty("default_gdal_translate"), "gdal_translate");
		if (path!=null){
			return path;
		}
		if (CommonFun.isWindows()){
			File f = new File(CommonFun.getCurrentPath() + "supports/gdal/bin/gdal/apps/gdal_translate.exe");
			if (f.exists()){
				return f.getAbsolutePath();
			}
		}
		return p.getProperty("gdal_translate");
	}
	public final float getEllipsoidVertexSize(){
		return Float.valueOf(p.getProperty("ellisoidVertexSize")).floatValue();
	}
	public final String getRScript(){
		String path = getExecutePath(p.getProperty("default_rscript"), "rscript");
		if (path!=null){
			return path;
		}
		if (p.getProperty("rscript")==null){
			return "";
		}
		return p.getProperty("rscript");
	}
	public final String getImageMagick() {
		String path = getExecutePath(p.getProperty("default_convert"), "convert");
		if (path!=null){
			return path;
		}
		if (CommonFun.isWindows()){
			File f = new File(CommonFun.getCurrentPath() + "supports/ImageMagick/convert.exe");
			if (f.exists()){
				return f.getAbsolutePath();
			}
		}
		return p.getProperty("convert");
	}
	public final String getLang(){
		return p.getProperty("lang");
	}
	public final String getProperty(String key){
		return p.getProperty(key);
	}
	public final String getGdaljni(){
		return CommonFun.getCurrentPath() + "supports/gdal/bin/gdal/java";
	}
	public final void setProperty(String key, String value){
		p.setProperty(key, value);
		try {
			p.store(new FileOutputStream(propertiesFile), "Niche Analyst");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getLastFolder() {
		return p.getProperty("lastFolder");
	}
	public File getPropertiesFile(){
		return propertiesFile;
	}
}

