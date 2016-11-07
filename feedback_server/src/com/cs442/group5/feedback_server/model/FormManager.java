package com.cs442.group5.feedback_server.model;

import java.sql.Connection;

import com.cs442.group5.feedback_server.dao.Database;
import com.cs442.group5.feedback_server.dao.FormDAO;

import com.cs442.group5.feedback_server.dto.Form;


public class FormManager {
	public String addForm(Form form)throws Exception {
		String feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
				FormDAO project= new FormDAO();
				feeds=project.addForm(connection,form);
		
		} catch (Exception e) {
			throw e;
		}
		return feeds;
	}

	public Form getForm(long id)throws Exception {
		Form feeds = null;
		try {
			    Database database= new Database();
			    Connection connection = database.Get_Connection();
			    FormDAO project= new FormDAO();
				feeds=project.getForm(connection,(long) id);
		
		} catch (Exception e) {
			throw e;
		}
		return feeds;
	}

}
