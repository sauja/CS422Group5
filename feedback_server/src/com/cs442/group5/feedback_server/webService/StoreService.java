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
import com.google.gson.reflect.TypeToken;

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
	@Path("/getMyStores")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String getMyStores( @FormParam("ownerid") String ownerid ) {
		String feeds  = null;
		try 
		{
			System.out.println("getMyStores");
			ArrayList<Store> feedData = null;
			StoreManager projectManager= new StoreManager();
			feedData = projectManager.getMyStores(ownerid);
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
	public String addStore( @FormParam("store") String storeString)
	{
		String feeds  = "false";
		try 
		{


			StoreManager projectManager= new StoreManager();
			Store store=new Gson().fromJson(storeString, new TypeToken<Store>() {}.getType());
			if(store.getName()!=null && store.getName().length()>0)
			{
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
	@POST
	@Path("/updateStore")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String updateStore( @FormParam("store") String storeString)
	{
		String feeds  = "false";
		try 
		{

System.out.println("updateStore: "+storeString);
			StoreManager projectManager= new StoreManager();
			Store store=new Gson().fromJson(storeString, new TypeToken<Store>() {}.getType());
			if(store.getName()!=null && store.getName().length()>0)
			{
				feeds = projectManager.updateStore(store);
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
	@POST
	@Path("/getBookmarkedStores")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String getBookmarkedStores( @FormParam("ownerid") long ownerid ) {
		String feeds  = null;
		try 
		{
			System.out.println("getBookmarkedStores");
			ArrayList<Store> feedData = null;
			StoreManager projectManager= new StoreManager();
			feedData = projectManager.getBookmarkedStores(ownerid);
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

}
