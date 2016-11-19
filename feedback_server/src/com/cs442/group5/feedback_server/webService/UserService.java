package com.cs442.group5.feedback_server.webService;

import java.awt.PageAttributes.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;


import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.cs442.group5.feedback_server.dao.Database;

import com.google.gson.Gson;

@Path("/user")
public class UserService {
	
	@POST
	@Path("/addUser")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String addUser( 
			@FormParam("uid") String uid,
			@FormParam("tokenid") String tokenid)
			 {
		String feeds  = "false";
		try 
		{
			if(uid!=null && tokenid.length()>0)
			{
				Database database= new Database();
			    Connection connection = database.Get_Connection();
				PreparedStatement ps = (connection).prepareStatement("INSERT INTO `feedback_db`.`user` (`uid`,`tokenid`) VALUES (?,?)");
				ps.setString(1, uid);
				ps.setString(2, tokenid);
				ps.executeUpdate();
				return "true";
			}
			
		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;
	}
	@POST
	@Path("/updateUser")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String updateUser( 
			@FormParam("uid") String uid,
			@FormParam("tokenid") String tokenid)
			 {
		String feeds  = "false";
		try 
		{
			if(uid!=null && tokenid.length()>0)
			{
				Database database= new Database();
			    Connection connection = database.Get_Connection();
				PreparedStatement ps = (connection).prepareStatement("UPDATE `feedback_db`.`user` SET `tokenid` = ? WHERE `uid` =? COLLATE latin1_bin ");
				ps.setString(1, tokenid);
				ps.setString(2, uid);
				ps.executeUpdate();
				return "true";
			}
			
		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;
	}

}
