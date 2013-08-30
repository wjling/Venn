package com.app.ui.menu.MyEvents;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.addActivityPack.CircularImage;
import com.app.catherine.R;
import com.app.ui.menu.MyEvents.MyEvents.MsgHandler;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EventMainPage extends Activity {
	
	private int userNum = 99;
	private String Theme = "������뵽�Ļ��ֻ��д����";
	private String Time = "2013-08-16 21:54:00";
	private String Location = "�����뵽��ֻ�����ᣬҪ��ȥʵ���ң�";
	private String launcherName = "L J";
	private String remark = "Ҫ����һ���������ޣ���";
	private JSONArray photolistJsonArray = null;
	private int id, event_id, launcher_id;
	private HttpSender sender;
	private MsgHandler handler;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
			Theme = intent.getStringExtra("theme");
			Location = intent.getStringExtra("location");
			launcherName = intent.getStringExtra("launcher");
			remark = intent.getStringExtra("remark");
			Time = intent.getStringExtra("date");
			userNum = Integer.parseInt( intent.getStringExtra("participantsNum") );
			try {
				photolistJsonArray = new JSONArray( intent.getStringExtra("photolistJsonArray") );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			id = intent.getIntExtra("id", 0);
			event_id = intent.getIntExtra("event_id", 0);
			launcher_id = intent.getIntExtra("launcher_id", 0);
			
		initLayout();
		initThemeBlock();
		initAllUserInfoBlock();
		initRemarBlock();
//		setCommentBlock();
		
		sender = new HttpSender();
		handler = new MsgHandler( Looper.myLooper() );
		sendRequest();
	}
	
	private void sendRequest()
	{
		JSONObject params = new JSONObject();
		try {
			params.put("id", id);
			params.put("event_id", event_id);
			sender.Httppost(OperationCode.GET_IMPORTANT_INFO, params, handler);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initLayout()
	{
		CircularImage joinBtn = (CircularImage)this.findViewById(R.id.joinBtn);
		joinBtn.setImageResource(R.drawable.join);
		View main = this.findViewById(R.id.main);
		SetMainWidth(main, 0.96);
		View leftSide = this.findViewById(R.id.leftSide);
		SetSideWidth(leftSide, 0.47);
		View rightSide = this.findViewById(R.id.rightSide);
		SetSideWidth(rightSide, 0.47);
	}
	
	private void initThemeBlock()
	{
		//set theme
		int themeLength = Theme.length();
		if( themeLength==0 )
		{
			Theme = "û����������Ŷ";
			themeLength = Theme.length();
		}
		TextView themeFirstLetter = (TextView) this.findViewById(R.id.themeFirstLetter);
		themeFirstLetter.setText( Theme.substring(0, 1) );
		if( themeLength>1 )
		{
			TextView themeOtherLetters = (TextView) this.findViewById(R.id.themeOtherLetters);
			themeOtherLetters.setText( Theme.substring(1, themeLength));
		}
		
		//set time
		if( Time.length() == 0)
			Time = "0000-00-00";
		
		TextView dateTV = (TextView) this.findViewById(R.id.date);
		dateTV.setText(Time);
		
		//set location
		if( Location.length()==0 )
			Location = "�ص�û������Ŷ";
		
		TextView locTv = (TextView)this.findViewById(R.id.loc);
		locTv.setText( Location);
		
	}
	
	private void initAllUserInfoBlock()
	{
		int length = photolistJsonArray.length();
		try {
				for (int i = 0; i < length; i++) 
					addAParticipant( photolistJsonArray.getInt(i) );					
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView userNumTV = (TextView)this.findViewById(R.id.userNum);
		userNumTV.setText( userNum+"" );
		
		View joinBtnView = this.findViewById(R.id.joinBtn);
		joinBtnView.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(EventMainPage.this, "���������", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
					intent.putExtra("userId", id);
					intent.putExtra("event_id", event_id);
					intent.setClass(EventMainPage.this, AddMoreParticipants.class);
				startActivity(intent);	
			}
		});
	}
	
	private void addAParticipant( int id )
	{
		LinearLayout userInfoBlockSecondBlock = (LinearLayout)this.findViewById(R.id.userInfoBlockSecondBlock);
		RelativeLayout child = (RelativeLayout)this.getLayoutInflater().inflate(R.layout.addcircleimage, null);
		CircularImage user = (CircularImage)child.findViewById(R.id.user);
		if( imageUtil.fileExist(id) ) 
		{
			Bitmap bitmap = imageUtil.getLocalBitmapBy(id);
			user.setImageBitmap(bitmap);
		}
		else		
			user.setImageResource(R.drawable.defaultavatar);
		
		userInfoBlockSecondBlock.addView( child);
	}
	
	private void initRemarBlock()
	{
		CircularImage launcher = (CircularImage)this.findViewById(R.id.launcher);
		if( imageUtil.fileExist(launcher_id) )
		{
			Bitmap bitmap = imageUtil.getLocalBitmapBy(launcher_id);
			launcher.setImageBitmap(bitmap);
		}
		else
			launcher.setImageResource(R.drawable.defaultavatar);
		
		TextView launcherNameTV = (TextView)this.findViewById(R.id.launcherName);
		launcherNameTV.setText( launcherName );
		
		if( remark.length()==0 )
		{
			remark = "û�б�עŶ";
		}
		
		TextView remarkTV =(TextView)this.findViewById(R.id.remark);
		remarkTV.setText(remark);
	}
	
	private void setCommentBlock()
	{
//		LinearLayout leftSideLinearLayout = (LinearLayout)this.findViewById(R.id.leftSide);
//		addCommentBlockTo(leftSideLinearLayout, "�����ڿ������õģ����Ͻ�����д���� �����ڿ������õģ����Ͻ�����д���� �����ڿ������õģ����Ͻ�����д����", 10 );
//		addCommentBlockTo(leftSideLinearLayout, "�����꣬�Ͻ�д������ �����ڿ������õģ����Ͻ�����д���� �����ڿ������õģ����Ͻ�����д����", 20 );
//		addCommentBlockTo(leftSideLinearLayout, "���汾�����Ͻ��� �����ڿ������õģ����Ͻ�����д���� �����ڿ������õģ����Ͻ�����д����", 30 );
//		LinearLayout rightSideLinearLayout = (LinearLayout)this.findViewById(R.id.rightSide);
//		addCommentBlockTo(rightSideLinearLayout, "���ֽ���Ⱦ��ˣ��Ҳ��ǹ���˵������ �����ڿ������õģ����Ͻ�����д���� �����ڿ������õģ����Ͻ�����д����", 101 );
//		addCommentBlockTo(rightSideLinearLayout, "�����ֺȾ��ˣ������ڿ������õģ����Ͻ�����д���� �����ڿ������õģ����Ͻ�����д����", 102 );
//		addCommentBlockTo(rightSideLinearLayout, "�������춼�ȾƵ�����Ȱ�������������ڿ������õģ����Ͻ�����д���� �����ڿ������õģ����Ͻ�����д��������ڿ������õģ����Ͻ�����д����", 103 );
	}
	
	private void addCommentBlockTo( String content, final int loveNum, int comment_id)
	{
		LinearLayout toLayout = null; 
		if( flag==true )
			toLayout = (LinearLayout)this.findViewById(R.id.leftSide);
		else
			toLayout = (LinearLayout)this.findViewById(R.id.rightSide);
		flag = !flag;
		
		LinearLayout child = (LinearLayout)this.getLayoutInflater().inflate(R.layout.comment_item_block, null);
		TextView contentTV = (TextView)child.findViewById(R.id.content);
		contentTV.setText(content);
		TextView loveNumTV = (TextView)child.findViewById(R.id.loveNum);
		loveNumTV.setText(loveNum+"");
		 
		toLayout.addView(child);
		
		//���һ��Ҫ����addView֮�󣬷��򱨴�
		LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams)child.getLayoutParams();
		params.setMargins(0, 0, 0, 10);
		child.setLayoutParams(params);
		
		child.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(EventMainPage.this, loveNum+"", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void SetMainWidth(View v, double rate)
    {
		//reference
    	int screenWidth =getWindowManager().getDefaultDisplay().getWidth();
    	
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)v.getLayoutParams();
		params.width = (int) (screenWidth * rate);
		v.setLayoutParams(params);
	}
	
	private void SetSideWidth(View v, double rate)
    {
		//reference
    	int screenWidth =getWindowManager().getDefaultDisplay().getWidth();
    	
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
		params.width = (int) (screenWidth * rate);
		v.setLayoutParams(params);
	}
	
	class MsgHandler extends Handler
	{
		public MsgHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			String returnStr = msg.obj.toString();
			if( !"DEFAULT".equals( returnStr ) )
			{
				try {
					switch ( msg.what ) {
					case OperationCode.GET_IMPORTANT_INFO:
						Log.e("eventMainpage", msg.obj.toString());
						JSONObject returnJson = new JSONObject( msg.obj.toString());
						int cmd = returnJson.getInt("cmd");
						
						if( cmd == ReturnCode.NORMAL_REPLY )
						{
							JSONArray commentListJsonArray = returnJson.getJSONArray("comment_list");
							int length = commentListJsonArray.length();
							Log.e("eventMainpage", length+" length");
							for (int i = 0; i < length; i++) 
							{
								JSONObject commentInfo = commentListJsonArray.getJSONObject(i);
								int good = commentInfo.getInt("good");
								int comment_id = commentInfo.getInt("comment_id");
								String content = commentInfo.getString("content");
								addCommentBlockTo( content, good, comment_id);
							}
						}
						
						break;
		
					default:
						break;
					}
				} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			else		
			{
				Toast.makeText(EventMainPage.this, "����������ʱ", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

}
