package com.cs442.group5.feedback.database.User;

import java.io.Serializable;

/**
 * Created by sauja7 on 10/16/16.
 */

public class User implements Serializable
{
	static User user;

	private String userid;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	private String userName;
	private String password;
	private String email = "";
	private String address = "";
	private String contactNo = "";

	private User(){

	}
	public static User getInstance(){
		if(user == null){
			user = new User();
		}
		return user;
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

	public User(String userName, String password,  String email, String address, String contactNo)
	{
		this.userName = userName;
		this.password = password;

		this.email = email;
		this.address = address;
		this.contactNo = contactNo;
	}
}
