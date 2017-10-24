package org.ioz.altay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class GPXAnalyst {
	@Test
	public void getLL() throws FileNotFoundException, JDOMException, IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("日期\t时间\t坐标点备注\t经度\t纬度" + Const.LineBreak);
		String folder = "/Users/huijieqiao/新疆航迹/中文信息";
		File f = new File(folder);
		Namespace ns = Namespace.getNamespace("http://www.topografix.com/GPX/1/1");
		for (File fitem : f.listFiles()){
			if (fitem.getName().startsWith("Waypoints")){
				Element root = CommonFun.readXML(fitem.getAbsolutePath());
				for (Object item : root.getChildren("wpt", ns)){
					Element element = (Element) item;
					
					String[] date = element.getChildText("cmt", ns).split(" ");
					sb.append(String.format("%s\t%s\t%s\t%s\t%s%n", 
							date[0], date[1], 
							element.getChildText("name", ns), 
							element.getAttributeValue("lon"),
							element.getAttributeValue("lat")));
				}
			}
		}
		CommonFun.writeFile(sb.toString(), folder + "/info.csv");
	}
	@Test
	public void readPhotoInfo() throws JpegProcessingException, IOException{
		File folder = new File("/Users/huijieqiao/新疆相机");
		ArrayList<File> files = getFiles(folder);
		StringBuilder sb = new StringBuilder();
		sb.append("Locality\tCarema Info\tPhoto ID\tDate\tTime\tF-Number\tExposure Time" + Const.LineBreak);
		for (File f : files){
			Metadata metadata = JpegMetadataReader.readMetadata(f);
			ExifSubIFDDirectory exif = metadata.getDirectory(ExifSubIFDDirectory.class);
			String[] fs = f.getAbsolutePath().split("/");
			String[] timedate = exif.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).split(" ");
			sb.append(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s%n", fs[4], fs[5], fs[6], 
					timedate[0], timedate[1], 
					exif.getString(ExifSubIFDDirectory.TAG_FNUMBER), 
					exif.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)));
		}
		CommonFun.writeFile(sb.toString(), folder + "/info.csv");
		
	}
	private ArrayList<File> getFiles(File folder){
		ArrayList<File> files = new ArrayList<File>();
		for (File f :  folder.listFiles()){
			if (f.isFile()){
				if (f.getAbsolutePath().toLowerCase().endsWith(".jpg")){
					files.add(f);
				}
			}else{
				ArrayList<File> folders = getFiles(f);
				for (File jf : folders){
					files.add(jf);
				}
			}
		}
		return files;
	}
}
