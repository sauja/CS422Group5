package com.cs442.group5.feedback_server.webService;

import java.awt.PageAttributes.MediaType;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.cs442.group5.feedback_server.dto.Store;
import com.cs442.group5.feedback_server.model.StoreManager;
import com.google.gson.Gson;

@Path("/store")
public class StoreService {

	@GET
	@Path("/getAllStores")
	@Produces("application/json")
	public String feed()
	{
		String feeds  = null;
		try 
		{
			ArrayList<Store> feedData = null;
			StoreManager projectManager= new StoreManager();
			feedData = projectManager.getAllStores();
			//StringBuffer sb = new StringBuffer();
			Gson gson = new Gson();
			System.out.println("getAllStores");
			feeds = gson.toJson(feedData);

		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;
	}

	@POST
	@Path("/getStore")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String getStore( @FormParam("id") long id ) {
		String feeds  = null;
		try 
		{

			Store feedData = null;
			StoreManager projectManager= new StoreManager();
			feedData = projectManager.getStore(id);
			//StringBuffer sb = new StringBuffer();
			Gson gson = new Gson();
			System.out.println(gson.toJson(feedData));
			feeds = gson.toJson(feedData);

		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;

	}

	@POST
	@Path("/addStore")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String addStore( 
			@FormParam("name") String name,
			@FormParam("address") String address,
			@FormParam("location") String location,
			@FormParam("zipcode") String zipcode,
			@FormParam("phone_no") String phone_no,
			@FormParam("emailid") String emailid,
			@FormParam("website") String website,
			@FormParam("gpsLat") String gpsLat,
			@FormParam("gpsLng") String gpsLng,
			@FormParam("ownerid") String ownerid)
	{
		String feeds  = "false";
		try 
		{
			if(name!=null && name.length()>0)
			{
				StoreManager projectManager= new StoreManager();
				Store store=new Store();
				store.setName(name);
				store.setAddress(address);
				store.setLocation(location);
				store.setZipcode(zipcode);
				store.setEmailid(emailid);
				store.setGpsLat(gpsLat);
				store.setGpsLng(gpsLng);
				store.setOwnerID(ownerid);
				store.setWebsite(website);
				store.setPhone_no(phone_no);
				
				
				feeds = projectManager.addStore(store);
				return feeds;
			}
			System.out.println("Name empty");
			return "false";


		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;

	}

}
