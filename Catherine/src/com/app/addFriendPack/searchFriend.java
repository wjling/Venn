package com.app.addFriendPack;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;

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
import android.widget.TextView;
import android.widget.Toast;

public class searchFriend extends Activity{
	
	private Button searchFriendTag;
	private View searchView;
	private EditText friendEmailET;
	private String searchEmail;
	private Button searchFriendBtn;
	private Button searchResultBtn;
	private TextView searchResultHint;
	private String searchResultStr;
	private int friendUid;
	private String friendEmail;
	private String friendName;
	private int gender;
	private int userId;
	private MessageHandler handler;
	
	JSONObject searchParams = new JSONObject();
	String respSearch = null;
	JSONObject respSearchJson;
	int cmdSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriend);
		
		userId = getIntent().getIntExtra("userId", -250);
		searchFriendTag = (Button)findViewById(R.id.search_friend_tag);
		searchFriendTag.setOnClickListener(searchTagListener);
		searchView = (View)findViewById(R.id.rela_search);
		friendEmailET = (EditText)findViewById(R.id.search_email);
		searchResultBtn = (Button)findViewById(R.id.searchResult);
		searchResultHint = (TextView)findViewById(R.id.searchResultHint);
		handler = new MessageHandler(Looper.myLooper());
	}
	
	private OnClickListener searchTagListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			searchFriendTag.setVisibility(View.GONE);
			searchView.setVisibility(View.VISIBLE);
			
			searchFriendBtn = (Button)findViewById(R.id.search_friend_btn);
			searchFriendBtn.setOnClickListener(searchBtnListener);
		}
	};
	
	private OnClickListener searchBtnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/**
			 * 先获取要搜索的Email
			 * 提示搜索中...
			 * 返回结果后，跳转到搜索结果页面
			 */				
			searchResultBtn.setVisibility(View.GONE);
			searchResultHint.setText("搜索中，请稍候...");
			
			searchEmail = friendEmailET.getText().toString().trim();
			if( searchEmail == null || searchEmail.trim().equals(""))
			{
				searchResultHint.setText("请输入用户邮箱...");
			}
			else
			{// 向服务器发送请求	
//				new Thread()
//				{
//					public void run()
//					{
						sendSearchRequest();	
//					}	
//				}.start();
//				String result = sendSearchRequest();
//				searchResultHint.setText(result);
			}			
		}
	};
	
	private OnClickListener resultBtnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			Toast.makeText(searchFriend.this, "你想添加他为好友哦", Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent();
			intent.setClass(searchFriend.this, addFriend.class);
			intent.putExtra("id", userId);
			intent.putExtra("friendEmail", friendEmail);
			intent.putExtra("friendName", friendName);
			intent.putExtra("friendUid", friendUid);
			intent.putExtra("gender", gender);			
			
			startActivity(intent);	
		}
	};

	private void sendSearchRequest() 
	{
		try {
			searchParams.put("email", searchEmail);
			HttpSender http = new HttpSender();
			http.Httppost(OperationCode.SEARCH_FRIEND, searchParams, handler);
//			Log.i("search friend", "http sent");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void chgHint(String str)
	{
		searchResultHint.setText(str);
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
					respSearch = msg.obj.toString();
					if( respSearch != "DEFAULT" )
					{
						respSearchJson = new JSONObject(respSearch);												
						cmdSearch = respSearchJson.getInt("cmd");
						Log.i("searchFriend", cmdSearch+"");
						
						// 搜索到好友后，就把好友信息放在searchResultBtn,且要把searchResultHint的值置为“”				 
						if( ReturnCode.USER_EXIST==cmdSearch )
						{
							friendEmail = respSearchJson.getString("email");
							friendName = respSearchJson.getString("name");
							friendUid = respSearchJson.getInt("id");
							gender = respSearchJson.getInt("gender");
							
							searchResultBtn.setVisibility(View.VISIBLE);
							if( gender==1 )			
								searchResultBtn.setText( "邮箱: " + friendEmail + "\n名字:" + friendName + "\n性别: 男");				
							else				
								searchResultBtn.setText( "邮箱: " + friendEmail + "\n名字:" + friendName + "\n性别: 女");
							
							searchResultBtn.setOnClickListener(resultBtnListener);
							chgHint("");
						}
						else if( ReturnCode.USER_NOT_FOUND==cmdSearch)	//用户不存在		
							chgHint("用户不存在哦...再试试搜索其他用户呗");
						else 
							chgHint("你穿越了，居然返回了其他值");
						}
					else 
						chgHint("网络异常");
				}catch (JSONException e) {
					e.printStackTrace();
				}
				
				break;

			default:
				break;
			}
		}
	}

}
