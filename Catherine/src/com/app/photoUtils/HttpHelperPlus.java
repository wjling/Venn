package com.app.photoUtils;

import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.app.utils.OperationCode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpHelperPlus 
{
	private final String TAG = "PhotosWall";
	private static HttpHelperPlus instance = null;
	String URL = "http://222.200.182.183:20000/";
	String result = null;
	
	private HttpHelperPlus() 
	{
	}
	
	public static HttpHelperPlus getInstance() 
	{
		if(instance == null) {
			synchronized (HttpHelperPlus.class) 
			{
				if(instance == null)
					instance = new HttpHelperPlus();
			}
		}
		return instance;
	}
	
	public void sendRequest( final int operationCode, final Handler mHandler) 
	{
		sendRequest(null, operationCode, mHandler);
	}
	
	public void sendRequest( final SimpleKeyValue[] kvs, final int operationCode, final Handler mHandler) 
	{
		sendRequest(kvs, null, operationCode, mHandler);
	}

	public void sendRequest( final SimpleKeyValue[] kvs , final KeyFile[] kfs, final int operationCode, final Handler mHandler) 
	{
		new Thread()
		{
			public void run()
			{
				switch (operationCode) {
					case OperationCode.UPLOAD_PHOTO:
						URL += "uploadphoto";
						break;
					default:
						break;
				}
				
				try {
					HttpPost httpPost = new HttpPost(URL);		
					Log.e(TAG, URL);
					MultipartEntity entity = new MultipartEntity();
					
					if(kvs != null) 
					{
						for (SimpleKeyValue kv : kvs) 
						{
							if(kv.value == null) continue;
							entity.addPart(kv.key, new StringBody(kv.value.toString(), Charset.forName("UTF-8")));
						}
					}
					
					if(kfs != null) 
					{
						for(KeyFile kf : kfs)
							entity.addPart(kf.key, new FileBody(kf.file, "image/jpeg"));
					}
					
					httpPost.setEntity(entity);				
					
					HttpClient httpclient = new DefaultHttpClient();
					httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
					
					HttpResponse response = httpclient.execute(httpPost);										
					result = EntityUtils.toString(response.getEntity());					
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = Message.obtain();
                message.what = operationCode;
                message.obj = result;
                mHandler.sendMessage(message);
			}
		}.start();
	
	}
	
}
