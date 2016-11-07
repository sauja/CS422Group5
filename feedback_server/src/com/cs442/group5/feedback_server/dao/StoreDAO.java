package com.cs442.group5.feedback_server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.cs442.group5.feedback_server.dto.Store;


public class StoreDAO 
{
	public ArrayList<Store> getAllStores(Connection connection) throws Exception
	{
		ArrayList<Store> feedData = new ArrayList<Store>();
		try
		{
			PreparedStatement ps = (connection).prepareStatement("SELECT `id`,`name`,`address`,`location`,`zipcode`,`phone_no`,`emailid`,`website`,`gpsLat`,`gpsLng`,`ownerid` FROM `store` ORDER BY id DESC");
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				Store storeObject = new Store();
				storeObject.setId(rs.getLong("id"));
				storeObject.setName(rs.getString("name"));
				storeObject.setAddress(rs.getString("address"));
				storeObject.setLocation(rs.getString("location"));
				storeObject.setZipcode(rs.getString("zipcode"));
				storeObject.setPhone_no(rs.getString("phone_no"));
				storeObject.setEmailid(rs.getString("emailid"));
				storeObject.setWebsite(rs.getString("website"));
				storeObject.setGpsLat(rs.getString("gpsLat"));
				storeObject.setGpsLng(rs.getString("gpsLng"));
				storeObject.setOwnerID(rs.getString("ownerid"));

				feedData.add(storeObject);
			}
			return feedData;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	public Store getStore(Connection connection,long id) throws Exception
	{
		Store feedData =null;
		try
		{
			PreparedStatement ps = (connection).prepareStatement("SELECT `id`,`name`,`address`,`location`,`zipcode`,`phone_no`,`emailid`,`website`,`gpsLat`,`gpsLng`,`ownerid` FROM `store` where id="+id+" ORDER BY id DESC");
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				Store storeObject = new Store();
				storeObject.setId(rs.getLong("id"));
				storeObject.setName(rs.getString("name"));
				storeObject.setAddress(rs.getString("address"));
				storeObject.setLocation(rs.getString("location"));
				storeObject.setZipcode(rs.getString("zipcode"));
				storeObject.setPhone_no(rs.getString("phone_no"));
				storeObject.setEmailid(rs.getString("emailid"));
				storeObject.setWebsite(rs.getString("website"));
				storeObject.setGpsLat(rs.getString("gpsLat"));
				storeObject.setGpsLng(rs.getString("gpsLng"));
				storeObject.setOwnerID(rs.getString("ownerid"));
				if(storeObject.getName()!=null)
					feedData=storeObject;
			}
			return feedData;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	public String addStore(Connection connection,Store store) throws Exception
	{
		Store feedData =null;
		try
		{
			PreparedStatement ps = (connection).prepareStatement("INSERT INTO store(name,address,location,zipcode,phone_no,emailid,website,gpsLat,gpsLng,ownerid) VALUES(?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1,store.getName());
			ps.setString(2,store.getAddress());
			ps.setString(3,store.getLocation());
			ps.setString(4,store.getZipcode());
			ps.setString(5,store.getPhone_no());
			ps.setString(6,store.getEmailid());
			ps.setString(7,store.getWebsite());
			ps.setString(8,store.getGpsLat());
			ps.setString(9,store.getGpsLng());
			ps.setString(10,store.getOwnerID());
			int rs = ps.executeUpdate();
			return "true";
		}
		catch(Exception e)
		{
			System.out.println(e);
			return "false";
		}
	}


}


