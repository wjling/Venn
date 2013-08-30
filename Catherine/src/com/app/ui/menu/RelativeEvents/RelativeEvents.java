package com.app.ui.menu.RelativeEvents;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.notificationObject;
import com.app.ui.NotificationCenter.myHandler;
import com.app.ui.menu.MyEvents.EventMainPage;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.cardAdapter;
import com.app.utils.imageUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RelativeEvents
{
	private Context context;
	private int userId;
	private int screenWidth;
	private View relativeEventsView;
	private imageUtil forImageUtil;
	private cardAdapter relativeEventsAdapter;
	private ArrayList<HashMap<String, Object>> addActivityRequests = new ArrayList<HashMap<String,Object>>();
	
	public RelativeEvents(Context context, int userId, int screenWidth, View toView ) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.userId = userId;
		this.screenWidth = screenWidth;
		this.relativeEventsView = toView;
		
		toView.setBackgroundDrawable(new ColorDrawable( 0xffe0f0f0 ) );
	}
	
	public void init()
	{
		//listview����ʾ 
		ListView relativeActivityLV = (ListView)relativeEventsView.findViewById(R.id.my_relative_events_listview);
		
		relativeActivityLV.setVerticalScrollBarEnabled(false);
		
		relativeActivityLV.setDivider( new ColorDrawable( 0xffe0f0f0 ) );
		relativeActivityLV.setDividerHeight(10);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)relativeActivityLV.getLayoutParams();
		params.setMargins(8, 8, 8, 0);
		relativeActivityLV.setLayoutParams(params);
		
		relativeEventsAdapter = new cardAdapter(context, 
				addActivityRequests,
				R.layout.activity_item, 
				new String[]{"title", "day", "monthAndYear","time", "location", "launcher", "remark", "participantsNum"}, 
				new int[]{R.id.activityTitle, R.id.day, R.id.monthAndYear, R.id.time, R.id.location, R.id.launcher, R.id.remark, R.id.participantsNum},
				screenWidth,
				new int[]{R.id.user1, R.id.user2, R.id.user3, R.id.user4},
				false,
				userId
		);
		
		relativeActivityLV.setAdapter(relativeEventsAdapter);
		relativeActivityLV.setItemsCanFocus(false);
		relativeActivityLV.setOnItemClickListener(myEventsListViewListener);
	}

	OnItemClickListener myEventsListViewListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			// TODO Auto-generated method stub
			HashMap<String, Object> EventItem = addActivityRequests.get(pos);
			
			Intent intent = new Intent();
			intent.setClass(context, EventMainPage.class);
				intent.putExtra("theme", (String)EventItem.get("title"));
				intent.putExtra("location", (String)EventItem.get("location"));
				intent.putExtra("participantsNum", (String)EventItem.get("participantsNum"));
				intent.putExtra("launcher", (String)EventItem.get("launcher"));
				intent.putExtra("remark", (String)EventItem.get("remark"));
				intent.putExtra("date", (String)EventItem.get("date"));			
				intent.putExtra("photolistJsonArray", EventItem.get("photolistJsonArray").toString());
				
				intent.putExtra("id", (Integer)EventItem.get("id"));
				intent.putExtra("event_id", (Integer)EventItem.get("event_id"));
				intent.putExtra("launcher_id", (Integer)EventItem.get("launcher_id"));
			context.startActivity(intent);
		}
	};
	
	public void showNotification()
	{
		//�����ݿ��ȡ��ӻ������
		NotificationTableAdapter adapter = new NotificationTableAdapter(context);
		ArrayList<notificationObject> addActivityRequestList = 
				adapter.queryData("ADD_ACTIVITY_INVITATION", userId);
		
		//�����ݿ�����ݷŵ�һ��hashmap׼������listview����ʾ
		addActivityRequests.clear(); 
		for (notificationObject item : addActivityRequestList) 
		{
			String msg = item.msg;
			int item_id = item.item_ID;

			getActivityFrom( msg, item_id);
			Log.e("relative Event:   ", msg);
		}
		
		relativeEventsAdapter.notifyDataSetChanged();
	}
	
	private void getActivityFrom( String str, int itemID)
	{
		JSONObject eventInforJson;
		String subject="����", time="", location="δ��", launcher="˭�����?", remark="û�б�עŶoo";
		int member_count = 0;
		String year="0000", month="00", day="00", hour="00", minute="00", second="00";
		JSONArray photolistJsonArray = null;
		int event_id = 0, launcher_id = 0;
		
		try{
			eventInforJson = new JSONObject(str);
			subject = eventInforJson.optString("subject");
			time = eventInforJson.optString("time");      //���ʼʱ��
			location = eventInforJson.optString("location");
			launcher = eventInforJson.optString("launcher");
			remark = eventInforJson.optString("remark");
			member_count  = eventInforJson.optInt("member_count");
			photolistJsonArray = eventInforJson.optJSONArray("member");
			event_id = eventInforJson.optInt("event_id");
			launcher_id = eventInforJson.optInt("launcher_id");
//			getPhotoId(photolistJsonArray);
		}
		catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		//Ϊ�յ�ʱ�򣬺�̨�����ַ���None
		if( !"None".equals(time) && time.length()>0 )
		{
			year = time.substring(0, 4);
			month = time.substring(5, 7);
			day = time.substring(8, 10);
			hour = time.substring(11, 13);
			minute = time.substring(14, 16);
			second = time.substring(17,19);
		}
		
		if( "None".equals(time) ) 
			time = "0000-00-00 00:00:00";
				
		HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", subject);
			map.put("day", day+"");
			map.put("monthAndYear", month + "��" +year);
			map.put("date", time);  //date detail
//			map.put("time", hour+":"+minute+":"+second);
			map.put("time", hour+":"+minute);
			map.put("location", location);
			map.put("launcher", "by " + launcher);
			map.put("remark", remark);
			map.put("participantsNum", member_count+"");
			map.put("photolistJsonArray", photolistJsonArray);
			map.put("id", userId);
			map.put("event_id", event_id);
			map.put("launcher_id", launcher_id);
			map.put("item_id", itemID);
		addActivityRequests.add(map);		
	}
	
	
}
