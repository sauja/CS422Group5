package com.cs442.group5.feedback_server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cs442.group5.feedback_server.dto.Form;
import com.cs442.group5.feedback_server.utils.Constants;


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
		
		catch(SQLException e)
		{
			if(e.getErrorCode()==Constants.MYSQL_DUPLICATE_PK)
			{
				System.out.println("Duplicate formID");
				return ""+Constants.MYSQL_DUPLICATE_PK;
			}
			System.out.println(e.getMessage());
			return ""+e.getErrorCode()+" "+e.getMessage();
			
		}
	}
	public Form getForm(Connection connection,long id) throws Exception
	{
		System.out.println("GET_FORM "+id);
		Form feedData =null;
		try
		{
			PreparedStatement ps = (connection).prepareStatement("SELECT * FROM feedback_db.forms where storeid=? order by id desc ;");
			ps.setLong(1,id);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				Form formObject = new Form();
				formObject.setId(rs.getLong("id"));
				formObject.setFormid(rs.getString("formid"));
				formObject.setName(rs.getString("name"));
				formObject.setStructure(rs.getString("structure"));
				formObject.setStoreid(rs.getLong("storeid"));
				feedData=formObject;
				break;
			}
			return feedData;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}


