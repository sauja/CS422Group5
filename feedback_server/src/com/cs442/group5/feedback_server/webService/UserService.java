package com.cs442.group5.feedback_server.webService;

import java.awt.PageAttributes.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.cs442.group5.feedback_server.dao.Database;
import com.cs442.group5.feedback_server.dto.Store;
import com.cs442.group5.feedback_server.dto.User;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path("/user")
public class UserService {
	
	
	@POST
	@Path("/updateUser")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String updateUser( 
			@FormParam("user") String userString)
			 {
		String feeds  = "false";
		try 
		{
			if(userString!=null )
			{
				Database database= new Database();
			    Connection connection = database.Get_Connection();
			    System.out.println("UPDATE_USER"+userString);
			    User user=new Gson().fromJson(userString, new TypeToken<User>() {}.getType());
			    PreparedStatement ps = (connection).prepareStatement("INSERT INTO `feedback_db`.`user` "
			    		+ " (`uid`,`usertype`,`tokenid`,`displayname`,`fName`,`lName`,`username`,`email`,`profileImageURL`)"
			    		+ " VALUES"
			    		+ " (?,?,?,?,?,?,?,?,?);");
			    ps.setString(1, user.getUid());
			    ps.setInt(2, user.getUserType());
			    ps.setString(3, user.getTokenid());
			    ps.setString(4, user.getDisplayName());
			    ps.setString(5, user.getfName());
			    ps.setString(6, user.getlName());
			    ps.setString(7, user.getUsername());
			    ps.setString(8, user.getEmail());
			    ps.setString(9, user.getProfileImageURL());
			    
			    try
			    {
			    	ps.executeUpdate();
			    }
			    catch(SQLException e)
			    {
			    	ps = (connection).prepareStatement("UPDATE `feedback_db`.`user`"
							+" SET"
							
							+" `usertype` = ?,"
							+" `tokenid` = ?,"
							+" `displayname` = ?,"
							+" `username` = ?,"
							+" `email` = ?,"
							+" `profileImageURL` = ?"
							+" WHERE `uid` =? COLLATE latin1_bin;");
					
				    ps.setInt(1, user.getUserType());
				    ps.setString(2, user.getTokenid());
				    ps.setString(3, user.getDisplayName());
				    ps.setString(4, user.getUsername());
				    ps.setString(5, user.getEmail());
				    ps.setString(6, user.getProfileImageURL());
				    ps.setString(7, user.getUid());
					ps.executeUpdate();
					return "true";
			    }
				
			}
			
		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;
	}

}
