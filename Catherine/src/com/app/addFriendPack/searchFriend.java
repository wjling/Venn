package com.app.addFriendPack;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.adapters.AdapterForFriendInfo;
import com.app.catherine.R;
import com.app.localDataBase.FriendStruct;
import com.app.ui.menu.FriendCenter.FriendCenter;
import com.app.utils.HttpSender;
import com.app.utils.Messager;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class searchFriend extends Activity{
	
	private EditText friendEmailET;
	private Button searchFriendBtn;
	private ListView searchResult;
	private ArrayList<FriendStruct> result_list;
	private TextView searchHint;
	private String searchString;
	private int userId;
	private MessageHandler handler;
	private AdapterForFriendInfo adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriend);
		
		userId = getIntent().getIntExtra("userId", -250);
		friendEmailET = (EditText)findViewById(R.id.search_friend_content);
		friendEmailET.setHint(R.string.search_friend_hint);
		searchFriendBtn = (Button)findViewById(R.id.search_friend_button);
		searchFriendBtn.setOnClickListener(searchListener);
		searchResult = (ListView)findViewById(R.id.search_friend_list);
		result_list = new ArrayList<FriendStruct>();
		adapter = new AdapterForFriendInfo(this, result_list, userId);
		searchResult.setAdapter(adapter);
		searchResult.setDivider(null);
		searchHint = (TextView)findViewById(R.id.search_friend_hint);
		searchHint.setVisibility(View.GONE);
		handler = new MessageHandler(Looper.myLooper());
		imageUtil.getInstance().registerHandler(handler, "searchFriend");
	}
	
	private OnClickListener searchListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		    /**
             * �Ȼ�ȡҪ������Email
             * ��ʾ������...
             * ���ؽ������ʾ�������
             */     
		    searchHint.setVisibility(View.VISIBLE);
		    searchHint.setText("�����У����Ժ�...");
            
		    searchString = friendEmailET.getText().toString().trim();
            if( searchString == null || searchString.trim().equals(""))
            {
                searchHint.setText("�������û�������û���...");
            }
            else
            {// ���������������    
                sendSearchRequest();    
            }          
        }
	};
	

	private void sendSearchRequest() 
	{
	    JSONObject searchParams = new JSONObject();
		try {
		    String emailFormat = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
			if (searchString.matches(emailFormat)) {
			    searchParams.put("email", searchString);
			}
			else {
			    searchParams.put("name", searchString);
			}
			searchParams.put("myId", userId);
			HttpSender http = new HttpSender();
			http.Httppost(OperationCode.SEARCH_FRIEND, searchParams, handler);
//			Log.i("search friend", "http sent");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void chgHint(String newString) {
        searchHint.setText(newString);
    }
	
	@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        imageUtil.getInstance().unregisterHandler("searchFriend");
        super.onBackPressed();
    }
	
	
	class MessageHandler extends Handler
	{
		public MessageHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
			case OperationCode.SEARCH_FRIEND:
				try
				{							
				    JSONObject jo = new JSONObject(msg.obj.toString());
				    Log.i("search_friend", jo.toString());
					int cmdSearch	= jo.getInt("cmd");
				    if( ReturnCode.USER_EXIST == cmdSearch)
				    {
				        JSONArray ja = jo.getJSONArray("friend_list");
				        int len = ja.length();
				        result_list.clear();
				        for (int i = 0; i < len; i++) {
				            JSONObject each_friend = ja.getJSONObject(i);
				            FriendStruct fs = new FriendStruct(each_friend, true);   
				            result_list.add(fs);
				        }
				        searchHint.setVisibility(View.GONE);
				        adapter.notifyDataSetChanged();
				    }
				    else if( ReturnCode.USER_NOT_FOUND==cmdSearch)	//�û�������		
				        chgHint("�û�������Ŷ...���������������û���");
				    else 
				        chgHint("�����쳣");
				}catch (JSONException e) {
					e.printStackTrace();
				}				
				break;
			case FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR:
			    adapter.notifyDataSetChanged();
			    break;
			default:
				break;
			}
		}
	}

}
