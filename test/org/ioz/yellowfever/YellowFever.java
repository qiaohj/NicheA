package org.ioz.yellowfever;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class YellowFever {
	@Test
	public void addCitytoPassengerFlow() throws SQLException{
		Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/flights?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
		PreparedStatement stmt_search1 = con.prepareStatement("select distinct o_airport_name from passenger_estimation where o_city_longitude is null");
		PreparedStatement stmt_search = con.prepareStatement("select * from airports where name=?");
		PreparedStatement stmt_search2 = con.prepareStatement("select * from cities where UPPER(Country)=? and City=LOWER(?)");
		PreparedStatement stmt_update1_1 = con.prepareStatement("update passenger_estimation set o_city_name=? where o_airport_name=?");
		PreparedStatement stmt_update2_1 = con.prepareStatement("update passenger_estimation set d_city_name=? where d_airport_name=?");
		
		PreparedStatement stmt_update1 = con.prepareStatement("update passenger_estimation set o_city_longitude=?, o_city_latitude=? where o_airport_name=?");
		PreparedStatement stmt_update2 = con.prepareStatement("update passenger_estimation set d_city_longitude=?, d_city_latitude=? where d_airport_name=?");
		ResultSet rs = stmt_search1.executeQuery();
		int index = 1;
		while (rs.next()){
			System.out.println(index++);
			stmt_search.setString(1, rs.getString("o_airport_name"));
			ResultSet rs2 = stmt_search.executeQuery();
			while (rs2.next()){
				stmt_update1_1.setString(1, rs2.getString("municipality"));
				stmt_update2_1.setString(1, rs2.getString("municipality"));
				
				stmt_update1_1.setString(2, rs2.getString("name"));
				stmt_update2_1.setString(2, rs2.getString("name"));
				
				stmt_update1_1.execute();
				stmt_update2_1.execute();
				
				stmt_search2.setString(1, rs2.getString("iso_country"));
				stmt_search2.setString(2, rs2.getString("municipality"));
				
				ResultSet rs3 = stmt_search2.executeQuery();
				while (rs3.next()){
				
					stmt_update1.setString(1, rs3.getString("Longitude_f"));
					stmt_update2.setString(1, rs3.getString("Longitude_f"));
					stmt_update1.setString(2, rs3.getString("Latitude_f"));
					stmt_update2.setString(2, rs3.getString("Latitude_f"));
					stmt_update1.setString(3, rs2.getString("name"));
					stmt_update2.setString(3, rs2.getString("name"));
					stmt_update1.execute();
					stmt_update2.execute();
				}
				rs3.close();
			}
			rs2.close();
		}
		rs.close();
	}
}
