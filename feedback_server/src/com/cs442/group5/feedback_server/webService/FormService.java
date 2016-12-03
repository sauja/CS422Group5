package com.cs442.group5.feedback_server.webService;

import java.awt.PageAttributes.MediaType;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.cs442.group5.feedback_server.dto.Form;
import com.cs442.group5.feedback_server.model.FormManager;
import com.google.gson.Gson;

@Path("/form")
public class FormService {
	
	@POST
	@Path("/addForm")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String addStore( 
			@FormParam("name") String name,
			@FormParam("structure") String structure,
			@FormParam("storeid") long storeid) {
		String feeds  = "false";
		try 
		{
			if(name!=null && name.length()>0)
			{
				FormManager projectManager= new FormManager();
				Form form=new Form();
				form.setName(name);
				form.setStructure(structure);
				form.setStoreid(storeid);
				
				feeds = projectManager.addForm(form);
				System.out.println("addForm "+feeds);
				return feeds;
			}
			
		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		System.out.println("addForm "+feeds);
		return feeds;
	}
	@POST
	@Path("/getForm")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String getForm( @FormParam("storeid") long id ) {
		String feeds  = null;
		try 
		{
			
			Form feedData = null;
			FormManager projectManager= new FormManager();
			feedData = projectManager.getForm(id);
			if(feedData==null)
				return null;
			//StringBuffer sb = new StringBuffer();
			Gson gson = new Gson();
			System.out.println(gson.toJson(feedData));
			feeds = feedData.getStructure();//gson.toJson(feedData);
			System.out.println("GET_FORM: "+feeds);
		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;
	}

}
