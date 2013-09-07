package com.app.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.addActivityPack.AddActivity;
import com.app.addFriendPack.searchFriend;
import com.app.catherine.R;
import com.app.ui.menu.FriendCenter.FriendCenter;
import com.app.ui.menu.MyEvents.MyEvents;
import com.app.ui.menu.RelativeEvents.RelativeEvents;
import com.app.utils.MyBroadcastReceiver;

public class UserInterface extends Activity implements OnTouchListener,
GestureDetector.OnGestureListener
{
	private static final int MENU_CLICKED = -2;
	private LinearLayout contentLayout;
	private LinearLayout menuLayout;
	private LinearLayout UILayout; //UILayout分为左右两部分，左边是Menu,右边是Content
	private Button menuButton, addActivityBtn;
	private Button addFriendBtn;
	private GestureDetector UIGestureDetector;
	private int window_width;
	private static float FLIP_DISTANCE_X = 400;	//检测甩手动作时候的最低速度值
	private int speed = 50;					//用于菜单栏自动回滚过程中的速度
	private int menu_width = 0;
	private int mScrollX;		//Scroll过程中X轴方向的位移
	private boolean isScrolling = false;	//是否滚动
	public static boolean hasScrolled = false;	//是否滚动过
	private boolean isFinish = true;		//是否后台回滚完毕
	private boolean isMenuOpen = false;		//是否显示了菜单栏
	private boolean hasMeasured = false;
	
	
	private myHandler uiHandler = new myHandler();
	
	//My Events 
	private static final int MSG_WHAT_ON_LOAD_DATA = -4;
	private static final int MSG_WHAT_LOAD_DATA_DONE = -5;
	private static final int MSG_WHAT_REFRESH_DONE = -6;
	private static final int MSG_WHAT_GET_MORE_DONE = -7;
	
	private Menu UI_Menu;
	private MyEvents UI_myEvents;
	private FriendCenter UI_friendCenter;
	private Settings UI_settings;
	
	private int userId = -1;
	private String email;
	private Intent serviceIntent = null;
	
	//add by luo
	private MyBroadcastReceiver broadcastReceiver  = null;
	public RelativeEvents relativeEventsPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ui, null);
		setContentView(v);
		UI_Menu = new Menu(this,v,uiHandler);
		UI_Menu.setMenu();
		//hello
		
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		email = intent.getStringExtra("email");
		serviceIntent = new Intent("HeartbeatService");
		
		
		init();
		
	}
	
	
	//按下返回键
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		
//		Builder dialog = new AlertDialog.Builder(UserInterface.this)
//								.setTitle("提示")
//								.setMessage("确定要退出程序吗？")
//								.setPositiveButton("是", 
//										new DialogInterface.OnClickListener() {
//											
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//												// TODO Auto-generated method stub
//												//跳回到主页面userinterface
//												finish();
//											}
//										})
//								.setNegativeButton("否", 
//										new DialogInterface.OnClickListener() {
//											
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//												// TODO Auto-generated method stub
//												//啥都不做
//											}
//										});
//			dialog.show();
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if( broadcastReceiver!=null )
			unregisterReceiver(broadcastReceiver);
		if( serviceIntent!=null){
			stopService(serviceIntent);
			Log.e("test", "ondestroy");
		}
		
		super.onDestroy();
	}

	public void init()
	{
		//broadcast filter add by luo
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("postMsg");
		broadcastReceiver = new MyBroadcastReceiver(this, userId);
		this.registerReceiver( broadcastReceiver, intentFilter);
		
		contentLayout = (LinearLayout)findViewById(R.id.ui_content);
		menuLayout = (LinearLayout)findViewById(R.id.ui_menu);
		UILayout = (LinearLayout)findViewById(R.id.ui_myui);
		menuButton = (Button)findViewById(R.id.ui_content_menuBtn);
		menuButton.setOnClickListener(menuButtonOnClickListener);
		addActivityBtn = (Button)findViewById(R.id.ui_addActivityBtn);
		addActivityBtn.setOnClickListener(addActivityListener);
		addFriendBtn = (Button)findViewById(R.id.ui_content_addFriendBtn);
		addFriendBtn.setOnClickListener(ui_ButtonClickListener);
		addFriendBtn.setVisibility(View.GONE);
		
		UILayout.setOnTouchListener(this);
		UIGestureDetector = new GestureDetector(this);
		UIGestureDetector.setIsLongpressEnabled(false);
		setParams();
		initMyEvents();
		initFriendsCenter();
		initSettings();
		initRelativeActivityPage();    // add by luo	
		}
	private OnClickListener ui_ButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.ui_content_addFriendBtn:
				Intent intent1 = new Intent();
				intent1.setClass(UserInterface.this, searchFriend.class);
				intent1.putExtra("userId", userId);
				UserInterface.this.startActivity(intent1);
				break;
				default: break;
			}
		}
	};
	//add by luo
	private OnClickListener addActivityListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(UserInterface.this, AddActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("email", email);
			startActivity(intent);
//			finish();  //add 8 . 22
		}
	};
	
	private void initMyEvents()
	{
		int width = getWindowManager().getDefaultDisplay().getWidth();
		UI_myEvents = new MyEvents(this, UI_Menu.getMyEventsView(), uiHandler, width, userId);
		UI_myEvents.init();
		UI_myEvents.myEventsListView.setOnTouchListener(this);	//非常重要的一步~
		UI_myEvents.loadData();
	}
	
	private void initFriendsCenter()
	{
		UI_friendCenter = new FriendCenter(this, UI_Menu.getFriendsCenterView(), uiHandler, userId);
		UI_friendCenter.init();
		View friendCenterView = UI_Menu.getFriendsCenterView();
		EditText searchEdText = (EditText) friendCenterView.findViewById(R.id.menu_friend_center_searchmyfriend);
		TextView searchButton = (TextView) friendCenterView.findViewById(R.id.menu_friend_center_searchmyfriendBtn);
		ListView friendList = (ListView) friendCenterView.findViewById(R.id.menu_friend_center_friendlist);
//		searchEdText.setOnTouchListener(this);
//		searchButton.setOnTouchListener(this);
//		friendList.setOnTouchListener(this);
//		LinearLayout menuLayout_root = (LinearLayout)friendCenterView.findViewById(R.id.menu_friend_center_searchLayout);
//		menuLayout_root.setOnTouchListener(this);
	}
	
	private void initSettings()
    {
        UI_settings = new Settings(this, UI_Menu.getSettingsView(), userId);
    }
	
	//add by luo
	private void initRelativeActivityPage()
	{
		int width = getWindowManager().getDefaultDisplay().getWidth();
		relativeEventsPage = new RelativeEvents(this, userId, width, UI_Menu.getPrivateView());
	}
	
	OnClickListener menuButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			jump();
		}
	};
	
	/**
	 * @author WJL
	 * @description Jump between menuLayout and contentLayout
	 *
	 */
	public void jump()
	{
		
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
//		Log.i("myUI","In jump(): leftMargin = "+ layoutParams.leftMargin);
		if(layoutParams.leftMargin>= 0)
		{
			new AsynMove().execute(-speed);
		}
		else
		{
			new AsynMove().execute(speed);
		}
	}
//	
//	public Menu getUIMenu()
//	{
//		return UI_Menu;
//	}
	
	//单位从dip转化成px
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
	
	//单位从px转化成dip
	public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
	
	public void setParams()
	{
		ViewTreeObserver viewTreeObserver = UILayout.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				if(!hasMeasured)
				{
					window_width = getWindowManager().getDefaultDisplay().getWidth();
					RelativeLayout.LayoutParams layoutParams_UI = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
					LinearLayout.LayoutParams layoutParams_content = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
//					LinearLayout.LayoutParams layoutParams_menu = (LinearLayout.LayoutParams)menuLayout.getLayoutParams();
//					
//					layoutParams_menu.width = (int) (window_width*0.6);
//					layoutParams_menu.width = dip2px(UserInterface.this, 200);
//					Log.i("myUI", "dp width: "+layoutParams_menu.width);
//					menuLayout.setLayoutParams(layoutParams_menu);
//					menu_width = layoutParams_menu.width;
					menu_width = menuLayout.getWidth();
					layoutParams_UI.width = window_width+menu_width;
					layoutParams_UI.leftMargin = -menu_width;
					UILayout.setLayoutParams(layoutParams_UI);
					
					layoutParams_content.width = window_width;
					contentLayout.setLayoutParams(layoutParams_content);
					
					Log.i("myUI", "UI width: "+UILayout.getWidth());
					Log.i("myUI", "content width: "+contentLayout.getWidth());
					
					hasMeasured = true;
					
				}
				return true;
			}
		});
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
//		Log.i("myUI","UI onTouch: "+event.getAction());
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN)
		{
			hasScrolled = false;
		}
		else if(action == MotionEvent.ACTION_UP) //为了解决滑动的时候又点入活动主要信息
		{
			if(hasScrolled)
			{
				
				UIGestureDetector.onTouchEvent(event);
//				hasScrolled = false;
				return true;
			}
			else
			{
				UIGestureDetector.onTouchEvent(event);
				return false;
			}
		}
		return UIGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		mScrollX = 0;
		isScrolling = false;
		UI_myEvents.myEventsListView.onTouchEvent(arg0);		
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
//		Log.i("myUI","onFlip: arg2:"+arg2+", arg3: "+arg3);
		hasScrolled = true;
		int currentX = (int)arg1.getX();
		int lastX = (int)arg0.getX();
		int deltaX = currentX - lastX;
		int deltaY = (int) (arg1.getY() - arg0.getY());
		if(Math.abs(deltaX) >= Math.abs(deltaY))
		{
			if(isMenuOpen)
			{
				if(!isScrolling && currentX - lastX >= 0)
				{
					return false;
				}
			}
			else
			{
				if(!isScrolling && currentX - lastX <= 0)
				{
					return false;
				}
			}
			
			boolean speedEnough = false;
			if(arg2 > FLIP_DISTANCE_X || arg2 < -FLIP_DISTANCE_X)
			{
				speedEnough = true;
			}
			else
			{
				speedEnough = false;
			}
			doCloseScroll(speedEnough);
		}
		else
		{
			UI_myEvents.myEventsListView.onTouchEvent(arg1);
			doCloseScroll(false);
		}
		
		
		return true;   //为了解决滑动时候进入活动主要信息，改成了true试一下
	}

	private void doCloseScroll(boolean speedEnough) {
		// TODO Auto-generated method stub
		if(isFinish)
		{
			RelativeLayout.LayoutParams layoutParams_UI = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
			int currentSpeed = this.speed;
			if(isMenuOpen)
			{
				currentSpeed = -currentSpeed;
			}
			
//			Log.i("myUI", "In doCloseScroll: leftMargin = "+ layoutParams_UI.leftMargin);
			if(speedEnough || (!isMenuOpen && (layoutParams_UI.leftMargin > window_width/2- menu_width))
					|| (isMenuOpen && layoutParams_UI.leftMargin < window_width/2 - menu_width))
			{
				new AsynMove().execute(currentSpeed);
			}
			else
			{
				new AsynMove().execute(-currentSpeed);
			}
		}
	}
	
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {  
		// TODO Auto-generated method stub
//		Log.i("myUI","onScroll: arg2:"+arg2+", arg3: "+arg3);
		if(Math.abs(arg2) >= Math.abs(arg3))
		{
			if(isFinish)
			{
				float distanceX = arg2;
				doScrolling(distanceX);
			}
		}
		else
		{
			UI_myEvents.myEventsListView.onTouchEvent(arg1);
		}
		return true;
	}

	private void doScrolling(float distanceX) {
		// TODO Auto-generated method stub
		isScrolling = true;
		hasScrolled = true;
		mScrollX += distanceX;// distanceX: negative for right, positive for left
		RelativeLayout.LayoutParams layoutParams_UI = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
		layoutParams_UI.leftMargin -= mScrollX;
		
		if(layoutParams_UI.leftMargin <= -menu_width)//向左拉过头
		{
			isScrolling = false;
			layoutParams_UI.leftMargin = -menu_width;
		}
		else if(layoutParams_UI.leftMargin  >= 0)//向右拉过头
		{
			isScrolling = false;
			layoutParams_UI.leftMargin = 0;
		}
		UILayout.setLayoutParams(layoutParams_UI);
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	class AsynMove extends AsyncTask<Integer, Integer, Void>
	{

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			isFinish = false;
			int times;
			times = menu_width/Math.abs(params[0])+1;
			for(int i=0; i<times; i++)
			{
				publishProgress(params[0]);
				try {
					Thread.sleep(Math.abs(params[0]));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			isFinish = true;
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
			if(layoutParams.leftMargin >= 0)
			{
				isMenuOpen = true;
			}
			else
			{
				isMenuOpen = false;
			}
			super.onPostExecute(result);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
			if(values[0]>0)//右移
			{
				layoutParams.leftMargin = Math.min(layoutParams.leftMargin + values[0], 0);
			}
			else//左移
			{
				layoutParams.leftMargin = Math.max(layoutParams.leftMargin + values[0], -menu_width);
			}
			UILayout.setLayoutParams(layoutParams);
			super.onProgressUpdate(values);
			
		}
		
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
			case MENU_CLICKED:
				View menu = (View) msg.obj;
				jump(); // Jump to the corresponding page
				// Something should be done corresponding menu button
				switch(menu.getId())
				{
				case R.id.ui_menu_myevents:
					addActivityBtn.setVisibility(View.VISIBLE);
					addFriendBtn.setVisibility(View.GONE);
//					UI_myEvents.loadData();
					break;
				case R.id.ui_menu_privateevents:
					addActivityBtn.setVisibility(View.GONE);
					addFriendBtn.setVisibility(View.GONE);
					//add by luo
					relativeEventsPage.init();
					relativeEventsPage.showNotification();
					break;
				case R.id.ui_menu_recommendedevents:
					addActivityBtn.setVisibility(View.GONE);
					addFriendBtn.setVisibility(View.GONE);
					break;
				case R.id.ui_menu_friendscenter:
					addActivityBtn.setVisibility(View.GONE);
					addFriendBtn.setVisibility(View.VISIBLE);
					UI_friendCenter.showFriendList();
					break;
				case R.id.ui_menu_update:
					addActivityBtn.setVisibility(View.GONE);
					addFriendBtn.setVisibility(View.GONE);
					break;
				case R.id.ui_menu_settings:
					addActivityBtn.setVisibility(View.GONE);
					addFriendBtn.setVisibility(View.GONE);
					UI_settings.initData();
					break;
				case R.id.ui_menu_exit:
					UserInterface.this.finish();
					break;
					default: break;
				}
				break;
			case MSG_WHAT_ON_LOAD_DATA:
//				myEventsPullUpDownView.notifyOnLoadData();
				UI_myEvents.myEventsPullUpDownView.notifyOnLoadData();
				break;
			case MSG_WHAT_LOAD_DATA_DONE:
					UI_myEvents.myEventsAdapter.notifyDataSetChanged();
					
					UI_myEvents.myEventsPullUpDownView.notifyLoadDataDone();
				break;
			case MSG_WHAT_REFRESH_DONE:
//				String string1 = (String) msg.obj;
//				UI_myEvents.myEventsList.add(0,string1);
				UI_myEvents.myEventsAdapter.notifyDataSetChanged();
				UI_myEvents.myEventsPullUpDownView.notifyRefreshDone();
				break;
			case MSG_WHAT_GET_MORE_DONE:
//				String string2 = (String) msg.obj;
//				UI_myEvents.myEventsList.add(string2);
				UI_myEvents.myEventsAdapter.notifyDataSetChanged();
				UI_myEvents.myEventsPullUpDownView.notifyGetMoreDone();
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
	      // TODO Auto-generated method stub
	      if (requestCode == UI_settings.CASE_PHOTO && resultCode == RESULT_OK && null != data)
	      {
	          new Thread(new Runnable()
	          {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    UI_settings.onAvatarsetFromPhoto(data);
                }
	              
	          }).start();
	          
	      }
	      else if (requestCode == UI_settings.CASE_CAMERA && resultCode == RESULT_OK && null != data)
	      {
	          new Thread(new Runnable()
              {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    UI_settings.onAvatarsetFromCamera(data);
                }
                  
              }).start();
	      }
	      else if (requestCode == UI_settings.CASE_PHOTO || requestCode == UI_settings.CASE_CAMERA)
	      {
              Toast.makeText(this, "请重新选择头像", Toast.LENGTH_SHORT).show();
          }
	      else if (requestCode == UI_settings.CASE_CHANGE_PW && resultCode == RESULT_OK)
          {
	          Toast.makeText(this, "修改密码成功", Toast.LENGTH_SHORT).show();
          }
//	      else {
//              Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
//          }
	      
	      super.onActivityResult(requestCode, resultCode, data);
	 }
	

}
