package com.cs442.group5.feedback_server.webService;

import java.awt.PageAttributes.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.cs442.group5.feedback_server.dao.Database;
import com.cs442.group5.feedback_server.dto.Review;
import com.cs442.group5.feedback_server.dto.ReviewRatingCountChart;
import com.cs442.group5.feedback_server.dto.Store;
import com.cs442.group5.feedback_server.noti.Notification;
import com.google.gson.Gson;




@Path("/review")
public class ReviewService {

	@POST
	@Path("/addReview")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String addReview( 
			@FormParam("storeid") int storeid,
			@FormParam("uid") String uid,
			@FormParam("comment") String comment,
			@FormParam("rating") String rating)
	{
		System.out.println("ratings "+rating);
		String feeds  = "false";
		try 
		{
			System.out.println("storeid "+storeid);
			Database database= new Database();
			Connection connection = database.Get_Connection();
			PreparedStatement ps = (connection).prepareStatement("INSERT INTO `feedback_db`.`review` (`storeid`,`uid`,`comment`,`rating`) VALUES (?,?,?,?)");
			ps.setInt(1, storeid);
			ps.setString(2, uid);
			ps.setString(3, comment);
			ps.setFloat(4, Float.valueOf(rating));
			ps.executeUpdate();
			
			ps=(connection).prepareStatement("select u.uid, u.tokenid, u.displayname,s.id as storeid,s.name as storename from  user as u,store as s "
					+ "where s.ownerid=u.uid and s.id=? and u.uid=?");
			ps.setInt(1, storeid);
			ps.setString(2, uid);
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				String storename=rs.getString("storename");
				String tokenid=rs.getString("tokenid");
				String displayname=rs.getString("displayname");
				Notification.sendReviewNoti(tokenid, storeid,storename, displayname);
				System.out.println("Send Notification "+storeid+" \n"+storename+" "+displayname+" "+tokenid);
				break;
			}
			
			return "true";


		} catch (Exception e)
		{
			System.out.println("error "+e);
		}
		return feeds;
	}
	@POST
	@Path("/getAllReviews")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String getAllReviews( 
			@FormParam("storeid") int storeid)

	{
		ArrayList<Review> reviewList;

		String feeds  = "false";
		try 
		{

			Database database= new Database();
			Connection connection = database.Get_Connection();
			PreparedStatement ps = (connection).prepareStatement("SELECT r.idreview,r.uid,r.comment,r.rating,r.timestamp,u.displayname,u.profileImageURL FROM feedback_db.review as r,feedback_db.user as u where r.uid=u.uid and storeid=? order by timestamp asc");
			ps.setInt(1, storeid);
			ResultSet rs = ps.executeQuery();
			reviewList=new ArrayList<Review>();
			while(rs.next())
			{
				Review review = new Review();
				review.setId(rs.getInt("idreview"));
				review.setUid(rs.getString("uid"));
				review.setComment(rs.getString("comment"));
				review.setRating(rs.getFloat("rating"));
				review.setTimestamp(rs.getTimestamp("timestamp"));
				review.setFullname(rs.getString("displayname"));
				review.setImgurl(rs.getString("profileImageURL"));
				reviewList.add(review);

			}
			Gson gson = new Gson();
			System.out.println("getAllReviews "+storeid+" "+gson.toJson(reviewList));
			feeds = gson.toJson(reviewList);
			return feeds;
		} catch (Exception e)
		{
			System.out.println("error "+e);
			e.printStackTrace();
		}
		return feeds;
	}

	@POST
	@Path("/getReviewRatingCountChart")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public String getReviewChart( 
			@FormParam("storeid") int storeid)
	{
		ArrayList<ReviewRatingCountChart> reviewList;
		String feeds  = "false";
		try 
		{

			Database database= new Database();
			Connection connection = database.Get_Connection();
			PreparedStatement ps = (connection).prepareStatement("SELECT storeid,rating,count(rating)as count,timestamp FROM feedback_db.review where storeid=? group by rating;");
			ps.setInt(1, storeid);
			ResultSet rs = ps.executeQuery();
			reviewList=new ArrayList<ReviewRatingCountChart>();
			while(rs.next())
			{
				Review review = new Review();
			float rating=rs.getFloat("rating");
			int count=rs.getInt("count");
				reviewList.add(new ReviewRatingCountChart(rating, count));

			}
			Gson gson = new Gson();
			System.out.println("ReviewRatingCountChart");
			feeds = gson.toJson(reviewList);
			return feeds;
		} catch (Exception e)
		{
			System.out.println("error ");
			e.printStackTrace();
		}
		return feeds;
	}

}
