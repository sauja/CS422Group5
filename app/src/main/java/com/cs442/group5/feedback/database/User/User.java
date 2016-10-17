package com.cs442.group5.feedback.database.User;

import java.io.Serializable;

/**
 * Created by sauja7 on 10/16/16.
 */

public class User implements Serializable
{
	private String userName;
	private String password;
	private String fname="";
	private String lname="";
	private String email="";
	private String address="";
	private String contactNo="";


	public String getFname()
	{
		return fname;
	}

	public void setFname(String fname)
	{
		this.fname = fname;
	}

	public String getLname()
	{
		return lname;
	}

	public void setLname(String lname)
	{
		this.lname = lname;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getContactNo()
	{
		return contactNo;
	}

	public void setContactNo(String contactNo)
	{
		this.contactNo = contactNo;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public User(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
	}

	public User(String userName, String password, String fname, String lname, String email, String address, String contactNo)
	{
		this.userName = userName;
		this.password = password;
		this.fname = fname;
		this.lname = lname;
		this.email = email;
		this.address = address;
		this.contactNo = contactNo;
	}
}
