package com.cs442.group5.feedback.model;

/**
 * Created by sauja7 on 11/13/16.
 */


		import java.sql.Timestamp;

public class Review implements Comparable<Review>{
	int id;
	long storeid;
	String uid;
	String comment;
	float rating;
	Timestamp timestamp;
	String fullname;
	String imgurl;

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getStoreid() {
		return storeid;
	}
	public void setStoreid(long storeid) {
		this.storeid = storeid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "id "+id+"\n"+
				"storeid "+storeid+"\n"+
				"uid "+uid+"\n"+
				"comment "+comment+"\n"+
				"rating "+rating+"\n"+
				"date "+timestamp;
	}

	@Override
	public int compareTo(Review review) {
		return review.getId()-this.id ;
	}
}
