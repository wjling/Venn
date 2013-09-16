package com.app.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import com.codebutler.android_websockets.WebSocketClient;
import com.codebutler.android_websockets.WebSocketClient.Listener;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WebSocketUtil
{
	protected static final String TAG = "WebSocketUtil";
	private WebSocketClient client;
	private int uid;
	private Handler handler;
	private boolean connectOrNot = false;
	
	public WebSocketUtil() {
		Log.e(TAG, "*****construct of websocketutil: parameter not enough*****");
	}
	
	public WebSocketUtil(int uid, Handler handler)
	{
		this.uid = uid;
		this.handler = handler;

//		client = new WebSocketClient(URI.create("ws://172.18.186.175:10088/"), socketListener, null);
		client = new WebSocketClient(URI.create("ws://222.200.182.183:10088/"), socketListener, null);
	}
	
	public void connectToServer()
	{
//		Log.i(TAG, "***** begin to connect post server...  *****");
		client.connect();
	}
	
	public void disconToServer()
	{
		Log.i(TAG, "***** disconnect from post server...  *****");
		client.disconnect();
	}
	
	private Listener socketListener = new Listener() {
		
		@Override
		public void onMessage(byte[] data) {
			// TODO Auto-generated method stub
			Log.i(TAG, String.format("*****Got binary message!*****"));
		}
		
		@Override
		public void onMessage(String message) {
			// TODO Auto-generated method stub
//			Log.i(TAG, String.format("*****Got string message! %s*****", message));
			sendMsg(message);
//			sendMsg(decodeUnicode(message));
		}
		
		@Override
		public void onError(Exception error) {
			// TODO Auto-generated method stub
			Log.e(TAG, "*****Error!*****", error);
		}
		
		@Override
		public void onDisconnect(int code, String reason) {
			// TODO Auto-generated method stub
			Log.i(TAG, String.format("*****Disconnected! Code: %d Reason: %s*****", code, reason));

			connectOrNot = false;
			
		}
		
		@Override
		public void onConnect() {
			// TODO Auto-generated method stub
//			client.send(uid);
			
			try {
				JSONObject params = new JSONObject();
				params.put("uid", uid);
				client.send(params.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 Log.i(TAG, "*****  Connected!    *****");	
			 connectOrNot = true;
		}
	};
	
	public boolean isconnect()
	{
		return connectOrNot;
	}
	
	public void setIsConnect(boolean connect)
	{
		connectOrNot = connect;
	}
	
	public void sendMsg(String str)
	{
		Message msg = handler.obtainMessage();
		msg.obj = str;
		handler.sendMessage(msg);
//		Log.i(TAG, "*****send received message*****");
	}
	
	public void send(String string)
	{
		client.send(string);
	}
	
	private static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
