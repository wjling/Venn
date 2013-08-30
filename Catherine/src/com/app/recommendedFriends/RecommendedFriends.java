package com.app.recommendedFriends;

import com.app.catherine.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class RecommendedFriends extends Activity{
	
	private int userId = -1;
	private View contentView;
	private Button backBtn;
	private Button uploadContactsBtn;
	private ListView reommendedFriendsList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(this).inflate(R.layout.recommended_friends, null);
		setContentView(contentView);
		init();
	}
	private void init() {
		// TODO Auto-generated method stub
		backBtn = (Button) findViewById(R.id.recommended_friends_backBtn);
		uploadContactsBtn = (Button) findViewById(R.id.recommended_friends_uploadPhoneBookBtn);
		reommendedFriendsList = (ListView) findViewById(R.id.recommended_friends_recommendedFriendsList);
	}
}
