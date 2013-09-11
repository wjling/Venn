package com.app.utils;

import java.io.File;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.util.Log;

public class photoSender 
{
	private HttpClient httpClient;
	private HttpPost httppost;
	private File file;
	private String httpUrl = "";
	private String URL = "http://222.200.182.183:20000/";
	private int operationCode;
	private String cmd;
	private int userId;
	private int eventId;
    private MyThread myThread;
	
	public photoSender( int operationCode, File file, int uid, int event_id )
	{
		this.operationCode = operationCode;
		this.file = file;
		this.userId = uid;
		this.eventId = event_id;
		
		myThread = new MyThread();
        myThread.start();
	}
	
	public void post() throws IOException
	{
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		httpUrl = URL;
		
		switch (operationCode) {
		case OperationCode.UPLOAD_PHOTO:
			httpUrl += "uploadphoto";
			cmd = "upload";
			Log.e("photoSender", httpUrl);
			break;

		default:
			break;
		}
		
		httppost = new HttpPost(httpUrl);
        
		MultipartEntity mpEntity = new MultipartEntity();
//			mpEntity.addPart("photos", new FileBody(file, "image/jpeg"));
			mpEntity.addPart("id", new StringBody(userId+"")); 
			mpEntity.addPart("event_id", new StringBody(eventId+""));
			mpEntity.addPart("cmd", new StringBody(cmd));
		
			httppost.setEntity(mpEntity);
			System.out.println("executing request"+httppost.getRequestLine());
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			
			System.out.println(response.getStatusLine());
			if (resEntity != null){
				System.out.println(EntityUtils.toString(resEntity));
			}
			if (resEntity != null){
				resEntity.consumeContent();
			}
			httpClient.getConnectionManager().shutdown();
	}
	
    class MyThread extends Thread {
    	public void run() {
    		try {
				post();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
	

}
