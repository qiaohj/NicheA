package org.ioz.mn.lakeriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;


public class ConnectionDetectRaster {
	@Test
	public void createMaxVertexLayer() throws IOException{
		GeoTiffObject mask = new GeoTiffObject("/Users/huijieqiao/temp/script/Lake_Raster.tif");
		ArrayList<String> vertics = CommonFun.readFromFile("/Users/huijieqiao/temp/script/max_vertex.csv");
		HashMap<Integer, StringBuilder> rivers_lakes = new HashMap<Integer, StringBuilder>();
		ArrayList<String> lakes_str = CommonFun.readFromFile("E:/River_Lake/script/df_path.csv");
		for (String lake_str : lakes_str){
			String[] infos = lake_str.split(",");
			if (infos.length==5){
				if (CommonFun.isDouble(infos[0])){
					int id = Integer.valueOf(infos[3]);
					if (rivers_lakes.containsKey(id)){
						rivers_lakes.get(id).append(lake_str + Const.LineBreak);
					}else{
						StringBuilder sb = new StringBuilder();
						sb.append(lake_str + Const.LineBreak);
						rivers_lakes.put(id, sb);
					}
				}
			}
		}
		
		
		System.out.println("rivers_lakes:" + rivers_lakes.size());
		
		HashSet<Integer> added = new HashSet<Integer>();
		//ArrayList<String> vertics = CommonFun.readFromFile("E:/River_Lake/script/max_vertex_5.csv");
		HashSet<Integer> v_list = new HashSet<Integer>();
		for (String vertex : vertics){
			if (CommonFun.isDouble(vertex.replace("\"", ""))){
				v_list.add(Integer.valueOf(vertex.replace("\"", "")));
			}
		}
		File f = new File("E:/River_Lake/script/result");
		StringBuilder finalpath = new StringBuilder(); 
		finalpath.append("FID,x,y,ID,type" + Const.LineBreak);
		for (File fitem : f.listFiles()){
			String[] items = fitem.getName().split("_");
			int id1 = Integer.valueOf(items[1]);
			int id2 = Integer.valueOf(items[3]);
			if (v_list.contains(id1)&&(v_list.contains(id2))){
				ArrayList<String> path = CommonFun.readFromFile(fitem.getAbsolutePath() + "/path.csv");
				for (int i=1; i<path.size(); i++){
					finalpath.append("2," + path.get(i) + "0,2" + Const.LineBreak);
				}
				
				if (!added.contains(id1)){
					if (rivers_lakes.containsKey(id1)){
						finalpath.append(rivers_lakes.get(id1).toString());
						added.add(id1);
					}
				}
				if (!added.contains(id2)){
					if (rivers_lakes.containsKey(id2)){
						added.add(id2);
						finalpath.append(rivers_lakes.get(id2).toString());
					}
				}
			}
			
		}
		GeoTiffController.createTiff("/Users/huijieqiao/temp/script/max_vertics_lake.tif", mask.getXSize(), mask.getYSize(), mask.getDataset().GetGeoTransform(), 
				new double[]{}, -9999, gdalconst.GDT_Int32, mask.getDataset().GetProjection());
		CommonFun.writeFile(finalpath.toString(), "E:/River_Lake/script/max_vertex_points_5.csv");
	}
	@Test
	public void detectUnfinished() throws IOException{
		System.out.println("start");
		ArrayList<String> connections = CommonFun.readFromFile("E:/River_Lake/script/Connections_1000.csv");
		int ccc = 1;
		int i = 0;
		for (String connection_Str : connections){
			//System.out.println(++ccc + "/" + connections.size());
			String[] connection = connection_Str.split(",");
			if (connection.length==6){
				if (CommonFun.isInteger(connection[0])){
					int from_id = Integer.valueOf(connection[1]);
					int to_id = Integer.valueOf(connection[2]);
					int step = 40;
					int maxStep = 50;
					double distance = Double.valueOf(connection[5]);
					if (distance<=40){
						continue;
					}
					if (from_id>to_id){
						continue;
					}
					String target = String.format("E:/River_Lake/script/result/from_%d_to_%d_step_%d_maxStep_%d/", 
							from_id, to_id, step, maxStep); 
					
					File f = new File(target);
					if (f.exists()){
						if (f.list().length==0){
							System.out.println("rmdir " + f.getAbsolutePath());
							i++;
						}
						
						
					}
					
							
				}
			}
			
			
			
		}
		System.out.println("removed " + i + " folders");
	}
	@Test
	public void readResult() throws IOException{
		ArrayList<String> connectionsStr = CommonFun.readFromFile("E:/River_Lake/script/Connections_1000.csv");
		StringBuilder sb = new StringBuilder();
		for (String connectionStr : connectionsStr){
			String[] connections = connectionStr.split(",");
			if (connections.length==6){
				if (CommonFun.isDouble(connections[1])){
					int from_id = Integer.valueOf(connections[1]);
					int to_id = Integer.valueOf(connections[2]);
					double distance = Double.valueOf(connections[5]);
					if (distance<=40){
						sb.append(connectionStr + ",0,0" + Const.LineBreak);
						continue;
					}
					if (from_id>to_id){
						continue;
					}
					File folder = new File(String.format("E:/River_Lake/script/result/from_%d_to_%d_step_40_maxStep_50/path.txt", from_id, to_id));
					if (folder.exists()){
						sb.append(connectionStr + "," + CommonFun.readFromFile(folder.getAbsolutePath()).get(0) + Const.LineBreak);
					}else{
						System.out.println(folder.getAbsolutePath());
						sb.append(connectionStr + ",NA,NA" + Const.LineBreak);
					}
				}else{
					sb.append(connectionStr.replace("\"", "") + ",distance_2,steps" + Const.LineBreak);
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "E:/River_Lake/script/Connections_1000_2.csv");
	}
	@Test
	public void detectPath() throws IOException{
		File f = new File("E:/River_Lake/script/result");
		
		for (File items : f.listFiles()){
			if (items.getName().startsWith("from_")){
				System.out.println(items.getAbsolutePath());
				HashSet<Node> nodes = new HashSet<Node>();
				Node leaf = null;
				File target = new File(items.getAbsolutePath() + "/path.txt");
				if (target.exists()){
					System.out.println("skip");
					continue;
				}
				CommonFun.writeFile("", target.getAbsolutePath());
				//from_1_to_25_step_40_maxStep_50
				String[] itstr = items.getName().split("_");
				int from_id = Integer.valueOf(itstr[1]);
				int to_id = Integer.valueOf(itstr[3]);
				for (int i=0; i<50; i++){
					ArrayList<String> paths = CommonFun.readFromFile(items.getAbsolutePath() + "/" + i + ".csv");
					HashSet<Node> allnodes = new HashSet<Node>();
					for (String path : paths){
						
						String[] pathitem = path.split(",");
						if (pathitem.length==5){
							if (CommonFun.isDouble(pathitem[0])){
								double x = Double.valueOf(pathitem[0]);
								double y = Double.valueOf(pathitem[1]);
								int id = Integer.valueOf(pathitem[2]);
								int fid = Integer.valueOf(pathitem[3]);
								int type = Integer.valueOf(pathitem[4]);
								Node node = new Node(x, y, id, fid, type, null);
								allnodes.add(node);
								if (i==0){
									if ((id==from_id)||(id==to_id)){
										if (id==to_id){
											to_id = from_id;
											from_id = id;
										}
										nodes.add(node);
									}
								}
								if (id==to_id){
									leaf = node;
								}
							}
						}
					}
					
					for (Node node : allnodes){
						if (included(nodes, node)==null){
							Node parent = getParent(nodes, node);
							parent.addChildren(node);
							node.setParent(parent);
						}
					}
					
				}
				StringBuilder sb = new StringBuilder();
				
				if (leaf!=null){
					sb.append(leaf.getLength() + "," + leaf.getSteps());
					StringBuilder sbpath = new StringBuilder();
					sbpath.append("x,y" + Const.LineBreak);
					sbpath.append(leaf.getPath());
					CommonFun.writeFile(sbpath.toString(), target.getAbsolutePath().replace(".txt", ".csv"));
					
				}else{
					sb.append("-1,-1");
				}
				
				CommonFun.writeFile(sb.toString(), target.getAbsolutePath());
			}
			
		}
		
		
	}
	private HashSet<Node> getAllnodes(HashSet<Node> nodes){
		HashSet<Node> results = new HashSet<Node>();
		for (Node subnode : nodes){
			results.add(subnode);
			HashSet<Node> subchildren = getAllnodes(subnode.getChildren());
			for (Node subchild : subchildren){
				results.add(subchild);
			}
		}
		return results;
	}
	private Node getParent(HashSet<Node> nodes, Node node) {
		double minDistance = Double.MAX_VALUE;
		Node result = null;
		for (Node subnode : getAllnodes(nodes)){
			double distance = node.getDistance(subnode);
			if (minDistance>distance){
				result = subnode;
				minDistance = distance;
			}
		}
		return result;
	}

	private Node included(HashSet<Node> nodes, Node node) {
		for (Node subnode : nodes){
			if (subnode.getLabel().equals(node.getLabel())){
				return subnode;
			}
			Node childnode = included(subnode.getChildren(), node);
			if (childnode!=null){
				return childnode;
			}
		}
		return null;
	}

	@Test
	public void detectConnections() throws IOException{

		System.out.println("Init points");
		ArrayList<String> pathstr = CommonFun.readFromFile("E:/River_Lake/script/df_path.csv");
		
		HashMap<Integer, HashSet<PointObj>> grouped_path_nodes = new HashMap<Integer, HashSet<PointObj>>();
		HashSet<PointObj> lake_nodes = new HashSet<PointObj>();
		HashSet<PointObj> river_nodes = new HashSet<PointObj>();
		HashSet<PointObj> path_nodes = new HashSet<PointObj>();
		
		
		for (String path : pathstr){
			String[] pathitem = path.split(",");
			if (pathitem.length==5){
				if (CommonFun.isDouble(pathitem[0])){
					int FID = Integer.valueOf(pathitem[0]);
					double x = Double.valueOf(pathitem[1]);
					double y = Double.valueOf(pathitem[2]);
					int ID = Integer.valueOf(pathitem[3]);
					int type = Integer.valueOf(pathitem[4]);
					if (type==0){
						ID = -1;
					}
					PointObj p = new PointObj(x, 
							y, type, FID, ID);
					
					HashSet<PointObj> points = grouped_path_nodes.get(ID);
					if (points==null){
						points = new HashSet<PointObj>();
					}
					points.add(p);
					grouped_path_nodes.put(ID, points);
					path_nodes.add(p);
					//type=1 lake, type=2 river, type=0 accu
					switch(type){
						case 1:
							lake_nodes.add(p);
							break;
						case 2:
							river_nodes.add(p);
							break;
						default:
							
							break;
					}
				}
			}
		}
		
		ArrayList<String> connections = CommonFun.readFromFile("E:/River_Lake/script/Connections_1000.csv");
		int ccc = 1;
		for (String connection_Str : connections){
			System.out.println(++ccc + "/" + connections.size());
			String[] connection = connection_Str.split(",");
			if (connection.length==6){
				if (CommonFun.isInteger(connection[0])){
					int from_id = Integer.valueOf(connection[1]);
					int to_id = Integer.valueOf(connection[2]);
					//rmdir E:\River_Lake\script\result\from_6254_to_6323_step_40_maxStep_50
				
					int step = 40;
					int maxStep = 50;
					double distance = Double.valueOf(connection[5]);
					if (distance<=40){
						continue;
					}
					if (from_id>to_id){
						continue;
					}
					String target = String.format("E:/River_Lake/script/result/from_%d_to_%d_step_%d_maxStep_%d/", 
							from_id, to_id, step, maxStep); 
					System.out.println(target);
					File f = new File(target);
					if (f.exists()){
						System.out.println("Skip");
						continue;
					}
					CommonFun.mkdirs(target, false);
					
					
					
					HashMap<PointObj, HashSet<PointObj>> point_map = new HashMap<PointObj, HashSet<PointObj>>();
					
					
					HashSet<PointObj> from_points = grouped_path_nodes.get(from_id);
					HashSet<PointObj> to_points = grouped_path_nodes.get(to_id);
					if ((from_points==null)||(to_points==null)){
						continue;
					}
					if (from_points.size()>to_points.size()){
						int t_id = from_id;
						from_id = to_id;
						to_id = t_id;
						from_points = grouped_path_nodes.get(from_id);
						to_points = grouped_path_nodes.get(to_id);
						
					}
					System.out.println("FROM Point Size: " + from_points.size());
					System.out.println("To Point Size: " + to_points.size());
					
					//Expand FROM
					int i=0;
					boolean found = false;
					while ((i<maxStep)&&(!found)){
						System.out.println("Current step " + i + "/" + maxStep);
						HashSet<PointObj> expended_from_points = new HashSet<PointObj>();
						for (PointObj p : from_points){
							
							HashSet<PointObj> expanded_points = point_map.get(p);
							if (expanded_points==null){
								expanded_points = ExpandPoint(p, path_nodes, step);
								point_map.put(p, expanded_points);
							}
							for (PointObj p2 : expanded_points){
								expended_from_points.add(p2);
							}
						}
						
						from_points = new HashSet<PointObj>();
						StringBuilder sb = new StringBuilder();
						sb.append("x,y,id,fid,type"+Const.LineBreak);
						for (PointObj p : expended_from_points){
							sb.append(String.format("%f,%f,%d,%d,%d%n", p.getX(), p.getY(), p.getId(), p.getFID(), p.getType()));
							if (p.getId()==to_id){
								found = true;
							}
							from_points.add(p);
						}
						CommonFun.writeFile(sb.toString(), target + i + ".csv");
						i++;
					}
					System.out.println("done!");
					
				}
			}
			
			
			
		}
	}

	private HashSet<PointObj> ExpandPoint(PointObj p, HashSet<PointObj> path_nodes, double step) {
		HashSet<PointObj> result = new HashSet<PointObj>();
		for (PointObj p2 : path_nodes){
			if ((CommonFun.between(p2.getX(), p.getX() - step, p.getX() + step))&&
					(CommonFun.between(p2.getY(), p.getY() - step, p.getY() + step))){
				result.add(p2);
			}
		}
		
		return result;
	}
}
