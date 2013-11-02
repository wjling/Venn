package com.app.ui;

import com.app.catherine.R;
import com.app.ui.menu.RelativeEvents.RelativeEvents;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Menu {
	private Context context;
	private LinearLayout ui_content;
	
	//菜单栏按钮
	private Button myEventsBtn;
	private Button privateEventsBtn;
	private Button recommendEventsBtn;
	private Button friendsCenterBtn;
	private Button updateBtn;
	private Button settingsBtn;
	private Button exitBtn;
	
	private View view;	//基础ui布局界面
	private View myEventsView;
	private View privateEventsView;
	private View recommendedEventsView;
	private View friendCenterView;
	private View updateView;
	private View settingsView;
	private View currentUI;
	private View currentMenuView;
	private Handler handler;
	

	public Menu(Context context, View v, Handler handler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		view = v;
		this.handler = handler;
	}
	
	
	public View getCurrentUI()
	{
		return currentUI;
	}
	
	/**
	 * @author WJL
	 * @description Return View of My Events
	 * @return
	 */
	public View getMyEventsView()
	{
		return myEventsView;
	}
	
	public View getFriendsCenterView()
	{
		return friendCenterView;
	}
	
	public View getSettingsView()
	  {
	      return settingsView;
	  }
	
	
	//add by luo
	public View getPrivateView()
	{
		return privateEventsView;
	}
	
	public void setMenu()
	{
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		Resources resources = context.getResources();
		ui_content = (LinearLayout)view.findViewById(R.id.ui_content_thecontent);
		myEventsBtn = (Button)view.findViewById(R.id.ui_menu_myevents);
		privateEventsBtn = (Button)view.findViewById(R.id.ui_menu_privateevents);
		recommendEventsBtn = (Button)view.findViewById(R.id.ui_menu_recommendedevents);
		friendsCenterBtn = (Button)view.findViewById(R.id.ui_menu_friendscenter);
		updateBtn = (Button)view.findViewById(R.id.ui_menu_update);
		settingsBtn = (Button)view.findViewById(R.id.ui_menu_settings);
		exitBtn = (Button)view.findViewById(R.id.ui_menu_exit);
		
		myEventsBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.on_01));
		privateEventsBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.off_02));
		recommendEventsBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.off_03));
		friendsCenterBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.off_04));
		updateBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.off_05));
		settingsBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.off_06));
		exitBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.off_07));
		
		currentMenuView = myEventsBtn;
		currentMenuView.setTag(1);
		
		myEventsView = LayoutInflater.from(context).inflate(R.layout.menu_my_events, null);
		privateEventsView = LayoutInflater.from(context).inflate(R.layout.menu_private_events, null);
		recommendedEventsView = LayoutInflater.from(context).inflate(R.layout.menu_recommended_events, null);
		friendCenterView = LayoutInflater.from(context).inflate(R.layout.menu_friends_center, null);
		updateView = LayoutInflater.from(context).inflate(R.layout.menu_update, null);
		settingsView = LayoutInflater.from(context).inflate(R.layout.menu_settings, null);
		
		currentUI = myEventsView;
		
		myEventsBtn.setOnTouchListener(menuOnTouchListener);
		privateEventsBtn.setOnTouchListener(menuOnTouchListener);
		recommendEventsBtn.setOnTouchListener(menuOnTouchListener);
		friendsCenterBtn.setOnTouchListener(menuOnTouchListener);
		updateBtn.setOnTouchListener(menuOnTouchListener);
		settingsBtn.setOnTouchListener(menuOnTouchListener);
		exitBtn.setOnTouchListener(menuOnTouchListener);
		
		ui_content.addView(currentUI);
	}
	
	OnTouchListener menuOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.ui_menu_myevents:
				buttonBackGroundChange(v,event,1);
				currentUI = myEventsView;
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
//				Toast.makeText(context, "My Events", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ui_menu_privateevents:
				buttonBackGroundChange(v,event,2);
				currentUI = privateEventsView;
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
				
//				Toast.makeText(context, "Private Events", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ui_menu_recommendedevents:
				buttonBackGroundChange(v,event,3);
				currentUI = recommendedEventsView;
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
//				Toast.makeText(context, "Recommended Events", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ui_menu_friendscenter:
				buttonBackGroundChange(v,event,4);
				currentUI = friendCenterView;
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
				ui_content.invalidate();
//				Toast.makeText(context, "Friends Center", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ui_menu_update:
				buttonBackGroundChange(v,event,5);
				currentUI = updateView;
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
//				Toast.makeText(context, "Update", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ui_menu_settings:
				buttonBackGroundChange(v,event,6);
				currentUI = settingsView;
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
//				Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ui_menu_exit:
				buttonBackGroundChange(v,event,7);
//				Toast.makeText(context, "Exit", Toast.LENGTH_SHORT).show();
				break;
				default: break;
			}
			
			return false;
		}
	};
	
	private void buttonBackGroundChange(View v, MotionEvent event, int index)
	{
		int action = event.getAction();
		Resources resources = context.getResources();
		int lastIndex = Integer.parseInt(currentMenuView.getTag().toString());
		int R_id;
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
			R_id = resources.getIdentifier(String.format("off_%02d", lastIndex), "drawable", context.getPackageName());
			currentMenuView.setBackgroundDrawable(resources.getDrawable(R_id));
			
			v.setBackgroundDrawable(resources.getDrawable(R.drawable.click));
			v.getBackground().setAlpha(0);
			break;
		case MotionEvent.ACTION_UP:
			currentMenuView = v;
			currentMenuView.setTag(index);
//			currentMenuIndex = index;
			R_id = resources.getIdentifier(String.format("on_%02d", index), "drawable", context.getPackageName());
			v.setBackgroundDrawable(resources.getDrawable(R_id));
			Message msg = new Message(); 
			msg.what = -2;
			msg.obj = v;
			handler.sendMessage(msg);
			break;
			default: break;
		}
	}
	
	
	
}
