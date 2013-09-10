package com.app.PhotoMainPage;

import com.app.catherine.R;
import com.app.utils.OperationCode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoMainPage extends Activity{

	private int userId;
	private int eventId;
	private View contentView;
	
	private Button backBtn;
	private Button photoDeleteBtn;
	private ImageView masterAvatar;
	private TextView masterName;
	private TextView masterTime;
	private ImageView photo;
	private TextView photoDescription;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(this).inflate(R.layout.photo_main_page, null);
		setContentView(contentView);
		init();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		eventId = intent.getIntExtra("eventId", -1);
		
		backBtn = (Button) contentView.findViewById(R.id.photo_main_page_backBtn);
		photoDeleteBtn = (Button)contentView.findViewById(R.id.photo_main_page_deletePhoto);
		masterAvatar = (ImageView)contentView.findViewById(R.id.photo_main_page_masterAvatar);
		masterName = (TextView)contentView.findViewById(R.id.photo_main_page_masterName);
		masterTime = (TextView)contentView.findViewById(R.id.photo_main_page_time);
		photo = (ImageView) contentView.findViewById(R.id.photo_main_page_photo);
		photoDescription = (TextView) contentView.findViewById(R.id.photo_main_page_description);
		
		
	}
	
	public class myHandler extends Handler
	{
		public myHandler() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case OperationCode.ADD_PCOMMENT:
				break;
			case OperationCode.DELETE_PCOMMENT:
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
}
