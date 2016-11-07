package com.cs442.group5.feedback_server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cs442.group5.feedback_server.dto.Form;


public class FormDAO {
	public String addForm(Connection connection,Form form) throws Exception
	{
		Form feedData =null;
		try
		{
			PreparedStatement ps = (connection).prepareStatement("INSERT INTO forms(name,formid,structure,storeid) VALUES(?,?,?,?)");
			ps.setString(1,form.getName());
			ps.setString(2,form.getName()+"_"+form.getStoreid());
			System.out.println("Structure "+form.getStructure());
			ps.setString(3,form.getStructure());
			ps.setLong(4,form.getStoreid());
			
			int rs = ps.executeUpdate();
			return "true";
		}
		catch(Exception e)
		{
			System.out.println(e);
			return "false";
		}
	}
	public Form getForm(Connection connection,long id) throws Exception
	{
		Form feedData =null;
		try
		{
			PreparedStatement ps = (connection).prepareStatement("SELECT `id`,`name`,`address`,`location`,`zipcode`,`phone_no`,`emailid`,`website`,`gps`,`ownerid` FROM `store` where id="+id+" ORDER BY id DESC");
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				Form formObject = new Form();
				/*storeObject.setId(rs.getLong("id"));
				storeObject.setName(rs.getString("name"));
				storeObject.setAddress(rs.getString("address"));
				storeObject.setLocation(rs.getString("location"));
				storeObject.setZipcode(rs.getString("zipcode"));
				if(storeObject.getName()!=null)
					feedData=storeObject;*/
			}
			return feedData;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
}


