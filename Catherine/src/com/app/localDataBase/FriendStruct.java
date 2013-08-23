package com.app.localDataBase;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FriendStruct {
	public int uid;
	public int fid;
	public String fname;
	public String gender;
	public String email;
	
	public FriendStruct() {
		// TODO Auto-generated constructor stub
		uid = -1;
		fid = -1;
		fname = null;
		gender = null;
		email = null;
	}
	
	public FriendStruct(JSONObject jo)
	{
		try {
			uid = jo.getInt("uid");
			fid = jo.getInt("id");
			fname =  jo.getString("name");
			gender = jo.getString("gender");
			email = jo.getString("email");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static FriendStruct getFromJSON(JSONObject jo)
	{
		FriendStruct fs = new FriendStruct();
		try {
			fs.fid = jo.getInt("id");
//			Log.v("friends","fs.fid "+fs.fid);
			fs.fname = jo.getString("name");
			fs.gender = jo.getString("gender");
			fs.email = jo.getString("email");
			fs.uid = jo.getInt("uid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fs;
	}
	
	
}
