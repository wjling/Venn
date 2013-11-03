package com.app.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.UserDataHandler;

import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.TableFriends;
import com.app.ui.UserInterface;
import com.app.ui.menu.FriendCenter.FriendCenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver
{

	protected static final String TAG = "NotificationUtil";
	private Context context = null;
	private int uid;
	private Handler fcHandler, ncHandler;
	
	
	public MyBroadcastReceiver( Context context, int uid)
	{
		this.context = context;
		this.uid = uid;
		this.fcHandler = null;
		this.ncHandler = null;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		if ( "postMsg".equals(action) ) {
			String postStr = intent.getStringExtra("postMsg");
			insertNotification( postStr );
			UserInterface.notifyNum++;
		}
	}
	
	public void insertNotification(String msg)
	{
		Log.i(TAG, "want to insert: " + msg);
		NotificationTableAdapter adapter = new NotificationTableAdapter(context);
		JSONObject msgJson = null;
		int msgCMD;
		
		try {
			 msgJson = new JSONObject(msg);
			 msgCMD = msgJson.getInt("cmd");
			 	switch (msgCMD) {
			 			case 999:
			 				//��������֪ͨ��д�����ݿ�
			 				adapter.insertData(1, uid, "ADD_FRIEND_REQUEST", msg);
			 				Messager.getInstance().notifyChanged(0);
			 				break;
			 			case 998:
			 				//ͬ����߾ܾ���ӣ�д����Ϣ���ݿ�
			 				adapter.insertData(1, uid,  "ADD_FRIEND_VERIFY", msg);
			 				//���ͬ�⣬д��������ݿ�
			 				Boolean result = msgJson.getBoolean("result");
							if(result.equals(true))
							{
								Log.i(TAG, "ͬ������ң����ҰѺ�����Ϣ�������ݿ�"+msg);
								msgJson.put("uid", uid);
								FriendStruct newFriend = new FriendStruct(msgJson);
								TableFriends tableFriends = new TableFriends(context);
								tableFriends.add(newFriend);
								Messager.getInstance().notifyChanged(1);
							}	
							else {
								Log.i(TAG, "�ܾ��������"+msg);
								Messager.getInstance().notifyChanged(0);
							}
			 				break;
			 			case 997:
			 				//�»����֪ͨ��д����Ϣ���ݿ�
			 				adapter.insertData(1,  uid, "ADD_ACTIVITY_INVITATION", msg);
			 				break;
			 			case 996:
			 				adapter.insertData(1,  uid, "ADD_ACTIVITY_FEEDBACK", msg);
			 				Messager.getInstance().notifyChanged(0);
			 				break;
			 			case 995:
			 				//İ�����������
			 				adapter.insertData(1,  uid, "REQUEST_INTO_ACTIVITY", msg);
			 				Messager.getInstance().notifyChanged(0);
			 				break;
			 			case 994:
			 				//İ���������������
			 				adapter.insertData(1,  uid, "RESPONSE_INTO_ACTIVITY", msg);
			 				Messager.getInstance().notifyChanged(0);
			 				break;
			 			default:
			 				break;
			}
			 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
