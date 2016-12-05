package com.cs442.group5.feedback.model;

/**
 * Created by sauja7 on 11/7/16.
 */

public class Store {
	long id;
	String name;
	String address;
	String Location;
	String zipcode;
	String gpsLat;
	String gpsLng;
	String phone_no;
	String imgurl;
	String emailid;
	String website;
	String ownerID;
	String tags;
	float rating;
	String isBookmarked;

	public String getIsBookmarked() {
		return isBookmarked;
	}

	public void setIsBookmarked(String isBookmarked) {
		this.isBookmarked = isBookmarked;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}



	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public Store() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}


	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public String getEmailid() {
		return emailid;
	}

	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getGpsLat() {
		return gpsLat;
	}

	public void setGpsLat(String gpsLat) {
		this.gpsLat = gpsLat;
	}

	public String getGpsLng() {
		return gpsLng;
	}

	public void setGpsLng(String gpsLng) {
		this.gpsLng = gpsLng;
	}

}
