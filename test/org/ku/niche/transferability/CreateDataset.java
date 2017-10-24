/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Dec 2, 2012 3:56:11 PM
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


package org.ku.niche.transferability;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;

import org.ioz.mmweb.Algorithm;
import org.ioz.mmweb.AlgorithmParameter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class CreateDataset {
	@Test
	public void createSample() throws IOException{
		int percent = 5;
		int maxpoints = 1000;
		int repeat = 100;
		HashSet<File> GeoTiff = getTiffs("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS");
		for (File tiff : GeoTiff){
			System.out.println(tiff.getAbsolutePath());
			File folder = new File(tiff.getAbsolutePath().replace(".tiff", "_folder"));
			if (folder.exists()){
				CommonFun.mkdirs(folder.getAbsolutePath(), true);
				GeoTiffObject geo = new GeoTiffObject(tiff.getAbsolutePath());
				for (int i=0;i<repeat;i++){
					ArrayList<String> xy = new ArrayList<String>();
					for (int x=0;x<geo.getXSize();x++){
						for (int y=00;y<geo.getYSize();y++){
							int value = (int)geo.readByXY(x, y);
							if (value==255){
								xy.add(String.format("%d,%d", x, y));
							}
						}
					}
					int pointnumber = xy.size() * percent / 100;
					pointnumber = (pointnumber>maxpoints)?maxpoints:pointnumber;
					while (xy.size()>pointnumber){
						int index = (int) (Math.random() * xy.size());
						if (index<xy.size()){
							xy.remove(index);
						}
					}
					StringBuilder sb = new StringBuilder();
					sb.append("long,lat" + Const.LineBreak);
					for (String s : xy){
						int[] xyxy = new int[]{Integer.valueOf(s.split(",")[0]), Integer.valueOf(s.split(",")[1])};
						double[] ll = CommonFun.PositionToLL(geo.getDataset().GetGeoTransform(), xyxy);
						sb.append(String.format("%f,%f%n", ll[0], ll[1]));
					}
					CommonFun.writeFile(sb.toString(), folder.getAbsolutePath() + "/ll" + i + ".txt");
				}
			}
			
		}
	}
	private HashSet<File> getTiffs(String folder){
		HashSet<File> files = new HashSet<File>();
		File fs = new File(folder);
		for (File f : fs.listFiles()){
			if (f.isDirectory()){
				HashSet<File> subfiles = getTiffs(f.getAbsolutePath());
				for (File subf : subfiles){
					files.add(subf);
				}
			}else{
				if ((f.getAbsolutePath().endsWith("_left.tiff")||f.getAbsolutePath().endsWith("_right.tiff"))
						&&(!f.getAbsolutePath().endsWith("6_right.tiff"))){
					files.add(f);
				}
			}
		}
		return files;
	}
	@Test
	public void postTask() throws IOException, JSONException{
		HashSet<String> alglist = new HashSet<String>();
//		alglist.add("ANN");
//		alglist.add("GAM");
//		alglist.add("GBM");
//		alglist.add("GLM");
//		alglist.add("MARS");
//		alglist.add("RF");
//		alglist.add("GARP_BS");
//		alglist.add("MAXENT_JAVA");
//		alglist.add("BIOCLIM");
//		alglist.add("ENVDIST");
//		alglist.add("ENVSCORE");
		alglist.add("MA");
		int repeatFrom = 0;
		int repeatTo = 9;
		ArrayList<Algorithm> algorithms = getAlgorithms();
		HashSet<File> GeoTiff = getTiffs("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS");
		for (File tiff : GeoTiff){
			String folder = tiff.getAbsolutePath().replace(".tiff", "_folder");
			for (int i=repeatFrom;i<=repeatTo;i++){
				StringBuilder positionsb = new StringBuilder();
				String filename = folder + "/ll" + i + ".txt";
				ArrayList<String> llpart = CommonFun.readFromFile(filename);
				if (llpart.size()<=4){
					continue;
				}
				String llLabel = dealVPLabel(folder);
				for (String ll : llpart){
					if (!ll.contains("long,")){
						positionsb.append(ll + Const.LineBreak);
					}
				}
				for (Algorithm algorithm : algorithms){
					if (!alglist.contains(algorithm.getAlgorithmid())){
						continue;
					}
					String occurrencesGroup = llLabel + "." + i + "." + algorithm.getAlgorithmid();
	//				if (!occurrencesGroup.contains("vp-big-1.0.")){
	//					continue;
	//				}
					ArrayList<String> EnvironmentLayerLists = new ArrayList<String>();
					EnvironmentLayerLists.add("1f62c3cf3baa440c013baa676e660001");
					EnvironmentLayerLists.add("1f62c3cf3baa440c013baa67700c0002");
					EnvironmentLayerLists.add("1f62c3cf3baa440c013baa67713b0003");
					postTask(algorithm, EnvironmentLayerLists, "huijieqiao@gmail.com",
							"1f62c3cf3baa440c013baa676e660001", positionsb.toString(), occurrencesGroup);
//					return;
				}
			}
		}
		
	}
	private String dealVPLabel(String f){
		String a = f.replace("/", "-").replace("-Users-huijieqiao-Dropbox-Papers-NicheTransferability-VS", "")
			.replace("_", ".").replace("-", ".").replace(".folder", "");
		return a.substring(1, a.length());
	}
	@Test
	public void testDealLabel(){
		String label = "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS/VS2_BIG/present.1_left_folder";
		System.out.println(dealVPLabel(label));
	}
	private ArrayList<Algorithm> getAlgorithms() throws IOException, JSONException{
		ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();
		URL url = new URL("http://159.226.67.79:8180/MMWeb/AlgorithmServlet");
		URLConnection connection = url.openConnection();
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		JSONArray jsonarray = new JSONArray(builder.toString());
		for (int i=0;i<jsonarray.length();i++){
			Algorithm algorithm = new Algorithm();
			
			JSONObject obj = jsonarray.getJSONObject(i);
			String algorithmid = obj.getString("algorithmid");
			algorithm.setAlgorithmid(algorithmid);
			JSONArray parameters = obj.getJSONArray("parameters");
			System.out.print(algorithmid + ":");
			HashSet algparameters = new HashSet<AlgorithmParameter>();
			for (int j=0;j<parameters.length();j++){
				AlgorithmParameter algparameter = new AlgorithmParameter();
				JSONObject parameter = parameters.getJSONObject(j);
				String algorithmparameterid = parameter.getString("algorithmparameterid");
				String defaultvalue = parameter.getString("defaultvalue");
				algparameter.setAlgorithmparameterid(algorithmparameterid);
				algparameter.setDefaultvalue(defaultvalue);
				System.out.print(algorithmparameterid + "=" + defaultvalue + ";");
				algparameters.add(algparameter);
			}
			algorithm.setParameters(algparameters);
			algorithms.add(algorithm);
			System.out.println();
		}
		return algorithms;
	}
	private void postTask(Algorithm algorithm, ArrayList<String> EnvironmentLayerLists, String Email, String MaskLayerList, 
			String PositionList, String occurrencesGroup) throws IOException{
		System.out.println("Posting " + occurrencesGroup + " @ " + algorithm.getAlgorithmid());

		// Construct data
	    StringBuilder data = new StringBuilder();
	    data.append(URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(Email, "UTF-8"));
	    for (String EnvironmentLayerList : EnvironmentLayerLists){
	    	data.append("&" + URLEncoder.encode("EnvironmentLayerList", "UTF-8") + "=" + URLEncoder.encode(EnvironmentLayerList, "UTF-8"));
	    }
	    for (AlgorithmParameter parameter : algorithm.getParameters()){
	    	data.append("&" + URLEncoder.encode(algorithm.getAlgorithmid() + "_" + parameter.getAlgorithmparameterid(), "UTF-8") + "=" + URLEncoder.encode(parameter.getDefaultvalue(), "UTF-8"));
	    }
	    data.append("&" + URLEncoder.encode("MaskLayerList", "UTF-8") + "=" + URLEncoder.encode(MaskLayerList, "UTF-8"));
	    data.append("&" + URLEncoder.encode("PositionList", "UTF-8") + "=" + URLEncoder.encode(PositionList, "UTF-8"));
	    data.append("&" + URLEncoder.encode("occurrencesGroup", "UTF-8") + "=" + URLEncoder.encode(occurrencesGroup, "UTF-8"));
	    data.append("&" + URLEncoder.encode("algcheckbox_" + algorithm.getAlgorithmid(), "UTF-8") + "=" + URLEncoder.encode(algorithm.getAlgorithmid(), "UTF-8"));
	    data.append("&" + URLEncoder.encode("paracount_" + algorithm.getAlgorithmid(), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(algorithm.getParameters().size()), "UTF-8"));
	    // Send data
	    URL url = new URL("http://159.226.67.79:8180/MMWeb/TaskServlet");
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	    wr.write(data.toString());
	    wr.flush();

	    // Get the response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String line;
	    while ((line = rd.readLine()) != null) {
	        System.out.println(line);
	    }
	    wr.close();
	    rd.close();

	}
}
