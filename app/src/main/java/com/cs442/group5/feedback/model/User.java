package com.cs442.group5.feedback.model;

/**
 * Created by aqueelmiqdad on 11/11/16.
 */

public class User {

	private String uid;
	private String fName;
	private String lName;
	private String username;
	private String email;
	private String profileImageURL="";

	public User() {
	}

	public String getProfileImageURL() {
		return profileImageURL;
	}

	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}

	public User(String uid, String fName, String lName, String username, String email) {
		this.uid = uid;
		this.fName = fName;
		this.lName = lName;
		this.username = username;
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "User{" +
				"uid='" + uid + '\'' +
				", fName='" + fName + '\'' +
				", lName='" + lName + '\'' +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}