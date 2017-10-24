package org.ioz.nb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class Configuration {
	@Test
	public void getEurAsia() throws FileNotFoundException{
		GeoTiffObject geo = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheBreadth/data_for_erin/maps/extinction/NB-ALL.CS-ALL.DA-ALL.BM-Med.by_event.tif");
		double[] ll = CommonFun.PositionToLL(geo.getDataset().GetGeoTransform(), new int[]{0, 0});
		System.out.println(ll[0] + "," + ll[1]);
		ll = CommonFun.PositionToLL(geo.getDataset().GetGeoTransform(), new int[]{geo.getXSize()-1, geo.getYSize()-1});
		System.out.println(ll[0] + "," + ll[1]);
	}
	@Test
	public void generateCommands() throws IOException{
		HashSet<Integer> dispersal_abilities =new HashSet<Integer>();
		dispersal_abilities.add(1);
		dispersal_abilities.add(5);
		HashSet<String> env_curves = new HashSet<String>();
		env_curves.add("f");
		env_curves.add("m");
		env_curves.add("s");
		String pattern = "./workspace/NicheBreadth/Default/NicheBreadth /home/huijieqiao/NBProject/NB_Configurations/EurAsia scenarios.%s.sp_%d.da_%d /home/huijieqiao/NB_Results 8000 1000";
		StringBuilder sb_f_1_p1 = new StringBuilder();
		StringBuilder sb_f_1_p2 = new StringBuilder();
		StringBuilder sb_f_5_p1 = new StringBuilder();
		StringBuilder sb_f_5_p2 = new StringBuilder();
		StringBuilder sb_m_1_p1 = new StringBuilder();
		StringBuilder sb_m_1_p2 = new StringBuilder();
		StringBuilder sb_m_5_p1 = new StringBuilder();
		StringBuilder sb_m_5_p2 = new StringBuilder();
		StringBuilder sb_s_1_p1 = new StringBuilder();
		StringBuilder sb_s_1_p2 = new StringBuilder();
		StringBuilder sb_s_5_p1 = new StringBuilder();
		StringBuilder sb_s_5_p2 = new StringBuilder();
		
		for (int i=1;i<=1500;i++){
			if (i<=750){
				sb_f_1_p1.append(String.format(pattern, "f", i, 1) + Const.LineBreak);
				sb_f_5_p1.append(String.format(pattern, "f", i, 5) + Const.LineBreak);
				sb_m_1_p1.append(String.format(pattern, "m", i, 1) + Const.LineBreak);
				sb_m_5_p1.append(String.format(pattern, "m", i, 5) + Const.LineBreak);
				sb_s_1_p1.append(String.format(pattern, "s", i, 1) + Const.LineBreak);
				sb_s_5_p1.append(String.format(pattern, "s", i, 5) + Const.LineBreak);
			}else{
				sb_f_1_p2.append(String.format(pattern, "f", i, 1) + Const.LineBreak);
				sb_f_5_p2.append(String.format(pattern, "f", i, 5) + Const.LineBreak);
				sb_m_1_p2.append(String.format(pattern, "m", i, 1) + Const.LineBreak);
				sb_m_5_p2.append(String.format(pattern, "m", i, 5) + Const.LineBreak);
				sb_s_1_p2.append(String.format(pattern, "s", i, 1) + Const.LineBreak);
				sb_s_5_p2.append(String.format(pattern, "s", i, 5) + Const.LineBreak);
			}
		}
		
		CommonFun.writeFile(sb_f_1_p1.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_f_1_p1.sh");
		CommonFun.writeFile(sb_f_1_p2.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_f_1_p2.sh");
		CommonFun.writeFile(sb_f_5_p1.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_f_5_p1.sh");
		CommonFun.writeFile(sb_f_5_p2.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_f_5_p2.sh");
		CommonFun.writeFile(sb_m_1_p1.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_m_1_p1.sh");
		CommonFun.writeFile(sb_m_1_p2.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_m_1_p2.sh");
		CommonFun.writeFile(sb_m_5_p1.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_m_5_p1.sh");
		CommonFun.writeFile(sb_m_5_p2.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_m_5_p2.sh");
		CommonFun.writeFile(sb_s_1_p1.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_s_1_p1.sh");
		CommonFun.writeFile(sb_s_1_p2.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_s_1_p2.sh");
		CommonFun.writeFile(sb_s_5_p1.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_s_5_p1.sh");
		CommonFun.writeFile(sb_s_5_p2.toString(), "/Users/huijieqiao/NB_Configurations/EurAsia/commands/sb_s_5_p2.sh");
	}
	@Test
	public void generateConfiguration() throws IOException{
		ArrayList<String> seeds_file = CommonFun.readFromFile("/Users/huijieqiao/NB_Configurations/EurAsia/seeds.csv");
		HashMap<Integer, Double[]> seeds = new HashMap<Integer, Double[]>();
		for (String s : seeds_file){
			String[] ss = s.split(",");
			if (ss.length==3){
				if (CommonFun.isDouble(ss[1])){
					Double[] seed = {Double.valueOf(ss[1]), Double.valueOf(ss[2])};
					seeds.put(Integer.valueOf(ss[0]), seed);
				}
			}
		}
		HashSet<Integer> dispersal_abilities =new HashSet<Integer>();
		dispersal_abilities.add(1);
		dispersal_abilities.add(5);
		ArrayList<String> niche_defination_file = CommonFun.readFromFile("/Users/huijieqiao/NB_Configurations/EurAsia/niche_definition.csv");
		HashMap<Integer, Double[]> niche_definations = new HashMap<Integer, Double[]>();
		for (String n : niche_defination_file){
			String[] nn = n.split("\t");
			if (nn.length==20){
				if (CommonFun.isDouble(nn[1])){
					Double[] niche_breadth = {Double.valueOf(nn[1]), Double.valueOf(nn[2]), Double.valueOf(nn[3]), Double.valueOf(nn[4])};
					niche_definations.put(Integer.valueOf(nn[18]), niche_breadth);
				}
			}
		}
		HashSet<String[]> env_curves = new HashSet<String[]>();
		env_curves.add(new String[]{"curve_f_1", "curve_f_12"});
		env_curves.add(new String[]{"curve_m_1", "curve_m_12"});
		env_curves.add(new String[]{"curve_s_1", "curve_s_12"});
		int species_id = 0;
		for (Integer key : seeds.keySet()){
			Double[] nb = niche_definations.get(key);
			for (int i=0;i<4;i++){
				nb[i] *= 1000;
			}
			for (Integer dispersal_abiligy : dispersal_abilities){
				species_id++;
				StringBuilder sb = new StringBuilder();
				sb.append("{" + Const.LineBreak);
				sb.append(String.format("    \"id\" : %d,", species_id) + Const.LineBreak);
				
				sb.append(String.format("    \"niche_breadth\" : [[%f, %f], [%f, %f]],", nb[0], nb[1], nb[2], nb[3]) + Const.LineBreak);
				sb.append(String.format("    \"dispersal_ability\" : %d,", dispersal_abiligy) + Const.LineBreak);
				sb.append("    \"dispersal_speed\" : 500," + Const.LineBreak);
				sb.append("    \"dispersal_method\" : 2," + Const.LineBreak);
				sb.append("    \"number_of_path\" : -1," + Const.LineBreak);
				sb.append("    \"speciation_years\" : 10000," + Const.LineBreak);
				Double[] seed = seeds.get(key);
				sb.append(String.format("    \"initial_seeds\" : [[%f, %f]]", seed[0], seed[1]) + Const.LineBreak);
				sb.append("}" + Const.LineBreak);
				String filename = String.format("sp_%d.da_%d", key, dispersal_abiligy);
				CommonFun.writeFile(sb.toString(), String.format("/Users/huijieqiao/NB_Configurations/EurAsia/niche_definations/%s.json", filename));
				for (String[] curves : env_curves){
					StringBuilder sb2 = new StringBuilder();
					sb2.append("{" + Const.LineBreak);
					sb2.append(String.format("    \"environments\" : [\"%s\", \"%s\"],", curves[0], curves[1]) + Const.LineBreak);
					sb2.append("    \"total_years\" : 500000," + Const.LineBreak);
					sb2.append("    \"mask\" : \"/home/huijieqiao/NBProject/NB_Configurations/EurAsia/environment_layers/mask.tif\"," + Const.LineBreak);
					sb2.append(String.format("    \"species\" : [\"%s\"]", filename) + Const.LineBreak);
					sb2.append("}" + Const.LineBreak);
					CommonFun.writeFile(sb2.toString(), 
							String.format("/Users/huijieqiao/NB_Configurations/EurAsia/scenarios/scenarios.%s.%s.json", 
									curves[0].replace("curve_", "").replace("_1", ""), filename));
				}
			}
		}
	}
}

