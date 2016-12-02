package com.cs442.group5.feedback_server.model;

import java.sql.Connection;
import java.util.ArrayList;

import com.cs442.group5.feedback_server.dao.Database;
import com.cs442.group5.feedback_server.dao.StoreDAO;
import com.cs442.group5.feedback_server.dto.Store;

public class StoreManager {
	
	
	public ArrayList<Store> getAllStores()throws Exception {
		ArrayList<Store> feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
			    StoreDAO project= new StoreDAO();
				feeds=project.getAllStores(connection);
		
		} catch (Exception e) {
			throw e;
		}
		return feeds;
	}
	
	public Store getStore(long id)throws Exception {
		Store feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
			    StoreDAO project= new StoreDAO();
				feeds=project.getStore(connection,(long) id);
				System.out.println("GETSTORE\n");
		
		} catch (Exception e) {
			throw e;
		}
		return feeds;
	}
	public String addStore(Store store)throws Exception {
		String feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
				StoreDAO project= new StoreDAO();
				feeds=project.addStore(connection,store);
		
		} catch (Exception e) {
			throw e;
		}
		return feeds;
	}
	public String updateStore(Store store)throws Exception {
		String feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
				StoreDAO project= new StoreDAO();
				feeds=project.updateStore(connection,store);
		
		} catch (Exception e) {
			throw e;
		}
		return feeds;
	}
	public ArrayList<Store> getMyStores(String ownerid)throws Exception {
		ArrayList<Store> feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
			    StoreDAO project= new StoreDAO();
				feeds=project.getMyStores(connection, ownerid);
		
		} catch (Exception e) {
			throw e;
		}
		return feeds;
	}

	public ArrayList<Store> getBookmarkedStores(long ownerid) {
		ArrayList<Store> feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
			    StoreDAO project= new StoreDAO();
				feeds=project.getBookmarkedStores(connection, ownerid);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feeds;
	}
}
