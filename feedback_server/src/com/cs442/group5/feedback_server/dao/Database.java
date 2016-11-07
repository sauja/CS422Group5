package com.cs442.group5.feedback_server.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
	Properties prop;
	public Database() {
		prop = new Properties();
		
		InputStream input = null;
	
		try {
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			input = classLoader.getResourceAsStream("com/cs442/group5/feedback_server/dao/database_config.prop");
			

			// load a properties file
			prop.load(input);
			

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public Connection Get_Connection() throws Exception
	{
		
		try
		{
		String connectionURL = prop.getProperty("connectionURL");
		Connection connection = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		connection = DriverManager.getConnection(connectionURL, prop.getProperty("dbuser"), prop.getProperty("password"));
		
	    return connection;
		}
		catch (SQLException e)
		{
		throw e;	
		}
		catch (Exception e)
		{
		throw e;	
		}
	}

}