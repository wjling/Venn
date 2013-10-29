package com.app.ui.menu.MyEvents;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.customwidget.PullUpDownView;
import com.app.customwidget.PullUpDownView.onPullListener;
import com.app.ui.UserInterface;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.RegUtils;
import com.app.utils.ReturnCode;
import com.app.utils.cardAdapter;
import com.app.utils.imageUtil;


import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyEvents {

	private Context context;
	public View myEventsView;
	public PullUpDownView myEventsPullUpDownView;
	public ListView myEventsListView;
//	private Button notificationBtn;
	private onPullListener myEventsPullUpDownViewListener;
	private OnItemClickListener myEventsListViewListener;
	
	public cardAdapter myEventsAdapter;
	public ArrayList<HashMap<String, Object>> myEventsList = new ArrayList<HashMap<String,Object>>();
	
	private Handler uiHandler;
	private int screenWidth;
	private int userId;
	private HttpSender sender;
	private MsgHandler handler;
	private cardHandler msgCard = new cardHandler( Looper.myLooper());
	
	private Set<Integer> photoIdSet = new HashSet<Integer>();
	private int curRequestAvatarId;
	private JSONArray seqJsonArray = null;
	private boolean firstLoad = true, refreshing=false;
	private Vector<Integer> allEventIDList = new Vector<Integer>();
	private Vector<Integer> requestEventIDList = new Vector<Integer>();
	private int requestIndex = 0;
	private imageUtil forImageUtil;
	
	//My Events 
	private static final int MSG_WHAT_ON_LOAD_DATA = -4;
	private static final int MSG_WHAT_LOAD_DATA_DONE = -5;
	private static final int MSG_WHAT_REFRESH_DONE = -6;
	private static final int MSG_WHAT_GET_MORE_DONE = -7;
	
	public MyEvents(Context context, View myEventsView, Handler uiHandler, int screenWidth, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myEventsView = myEventsView;
		this.uiHandler = uiHandler;
		this.screenWidth = screenWidth;
		this.userId = userId;
		
		sender = new HttpSender();
		handler = new MsgHandler( Looper.myLooper() );
	}
	

	public void init() {
		// TODO Auto-generated method stub
		/**
		 * parent	: the adapterview where the click happened
		 * view 	 	: the view within the adapterview that was clicked
		 * position	: the position of the view in the adapter
		 * id 			: the row id of the item that was clicked
		 */
		myEventsListViewListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				if(UserInterface.hasScrolled) // Ϊ�˽�����������������ϸ��Ϣ��bug
				{
					return;
				}
				HashMap<String, Object> EventItem = myEventsList.get(pos);
				
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
		
		myEventsPullUpDownViewListener = new onPullListener() {
			
			@Override
			public void Refresh() {
				// TODO Auto-generated method stub
						allEventIDList.clear();
						myEventsList.clear();
						myEventsAdapter.notifyDataSetChanged();
						requestIndex = 0;
						refreshing = true;		
						
						//��������
						sendRequest(OperationCode.GET_MY_EVENTS);												
			}
			
			@Override
			public void GetMore() {
				// TODO Auto-generated method stub
						if( hasMoreEvent() )
							sendRequest(OperationCode.GET_MY_EVENTS);	
						else
						{
							Message msgLoadMore = uiHandler.obtainMessage(MSG_WHAT_GET_MORE_DONE);
							msgLoadMore.obj = "After more " + System.currentTimeMillis();
							msgLoadMore.sendToTarget();
//							Log.e("myevents", "*************no more events*********");
						}
			}
		};
		
//		notificationBtn = (Button)myEventsView.findViewById(R.id.menu_my_events_notificationBtn);
//		notificationBtn.setOnClickListener(buttonsOnClickListener);
		myEventsPullUpDownView = (PullUpDownView)myEventsView.findViewById(R.id.my_events_pull_up_down_view);
		myEventsListView = myEventsPullUpDownView.getListView();
		myEventsPullUpDownView.setOnPullListener(myEventsPullUpDownViewListener);
		
		myEventsListView.setItemsCanFocus(false);
		myEventsListView.setOnItemClickListener(myEventsListViewListener);
		
		//edit by luo
		myEventsAdapter = new cardAdapter(context, 
				myEventsList,
				R.layout.activity_item, 
				new String[]{"title", "day", "monthAndYear","time", "location", "launcher", "remark", "participantsNum"}, 
				new int[]{R.id.activityTitle, R.id.day, R.id.monthAndYear, R.id.time, R.id.location, R.id.launcher, R.id.remark, R.id.participantsNum},
				new int[]{R.id.user1, R.id.user2, R.id.user3, R.id.user4},
				screenWidth,
				true,
				userId,
				msgCard
		);
		
		myEventsListView.setAdapter(myEventsAdapter);
	}
	
	//add by luo
	private void getActivityFrom( String str)
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
			getPhotoId(photolistJsonArray);
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
			time = "0000-00-00 00:00";
				
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", subject);
		map.put("day", day+"");
		map.put("monthAndYear", month + "��" +year);
		map.put("date", time);  //date detail
		map.put("time", hour+":"+minute);
		map.put("location", location);
		map.put("launcher", "by " + launcher);
		map.put("remark", remark);
		map.put("participantsNum", member_count+"");
		map.put("photolistJsonArray", photolistJsonArray);
		map.put("id", userId);
		map.put("event_id", event_id);
		map.put("launcher_id", launcher_id);
		map.put("item_id", -1);
		map.put("open", false);
		myEventsList.add(map);		
	}
	
	//���л��ƬҪ��ʾ��ͷ��Ĳ���photoIdSet
	private void getPhotoId(JSONArray photolistJsonArray)
	{
		try {
			for (int i = 0; i < photolistJsonArray.length(); i++) 
			{
				Integer id = (Integer) photolistJsonArray.get(i);
				photoIdSet.add( id );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadData()
	{
	    myEventsList.clear();
	    
		Message msg1 = uiHandler.obtainMessage(MSG_WHAT_ON_LOAD_DATA);
		msg1.sendToTarget();
		
		//get data from server, send a request~   by luo
		sendRequest(OperationCode.GET_MY_EVENTS);
	}
	
	private void sendRequest(final int opCode)
	{
		final int curRequestAvatarIdTemp = curRequestAvatarId;
		new Thread()
		{
				public void run()
				{
					JSONObject params = new JSONObject();
					try
					{
						switch (opCode) {
						case OperationCode.GET_MY_EVENTS:
							params.put("id", userId);
							sender.Httppost(OperationCode.GET_MY_EVENTS, params, handler);
							break;
						case OperationCode.GET_EVENTS:
							params.put("sequence", new JSONArray( requestEventIDList ) );
							sender.Httppost(OperationCode.GET_EVENTS, params, handler);
							break;
						case OperationCode.GET_AVATAR:
							params.put("id", curRequestAvatarIdTemp);
							params.put("operation", 0);
							new HttpSender().Httppost(OperationCode.GET_AVATAR, params, handler);
							break;
						default:
							break;
						}						
					}
					catch (JSONException e) {
			            // TODO Auto-generated catch block
			            e.printStackTrace();
			        }
				}
		}.start();
	}
	
	//���������������ͷ��
	private void getAllavatars()
	{
		new Thread()
		{
				public void run()
				{
					for (int id : photoIdSet) {
						//��local������ͷ���ʱ�򣬲�ȥ��ȡͷ�񣬷�����ȡ
						//���ߣ���һ��ȡ�Ҫ��ȡͷ�񣬺���Ͳ�����ȡͷ����=============
						if( !imageUtil.fileExist(id) )
						{
							curRequestAvatarId = id;
							sendRequest( OperationCode.GET_AVATAR );
						}
					}
				}
		}.start();
		
	}
	
	private void initRequestEventList()
	{
		//ÿ������10��
		int i=0, length = allEventIDList.size();
		requestEventIDList.clear();
		for (; i < 10 &&  requestIndex<length; i++, requestIndex++) 
		{
			requestEventIDList.add( allEventIDList.get(requestIndex) );
		}
	}
	
	private boolean hasMoreEvent()
	{
		return requestIndex < allEventIDList.size();
	}
	
	class cardHandler extends Handler
	{
		public cardHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			switch (msg.what) {
			case cardAdapter.CARD_INFO_CHANGE:
				myEventsAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
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
			JSONObject returnJson = null;
			
			JSONArray eventJsonArray = null;
			int returnCMD;			
			
			if( returnStr!="DEFAULT")
			{
				
				try{
					returnJson = new JSONObject( returnStr );
					returnCMD = returnJson.optInt("cmd");
					
						switch ( msg.what ) {
							case OperationCode.GET_MY_EVENTS:																			
								
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{						
									seqJsonArray = returnJson.optJSONArray("sequence");
									if( seqJsonArray==null) break;
									
									int length = seqJsonArray.length();
									if( length>0 )
									{
										allEventIDList.clear();
										for( int i=0; i<length; i++)									
											allEventIDList.add( seqJsonArray.getInt(i) );

										initRequestEventList();
//										//ʹ��events sequence��������
										sendRequest(OperationCode.GET_EVENTS);										
									}
									else										
									{
										Toast.makeText(context, "��ǰû�л", Toast.LENGTH_SHORT).show();	
										//load data done; inform user interface
										Message msgLoad = uiHandler.obtainMessage(MSG_WHAT_LOAD_DATA_DONE);
										msgLoad.sendToTarget();
										
										Message msgLoadMore = uiHandler.obtainMessage(MSG_WHAT_GET_MORE_DONE);
										msgLoadMore.obj = "After more " + System.currentTimeMillis();
										msgLoadMore.sendToTarget();
										firstLoad = false;
									}
								}
								else								
									Toast.makeText(context, "get my events��������ֵ��"+returnCMD, Toast.LENGTH_SHORT).show();																	
							break;
								
							case OperationCode.GET_EVENTS:
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{
									eventJsonArray = returnJson.optJSONArray("event_list");
									int length = eventJsonArray.length();
									
									//�����event list
//									myEventsList.clear();
									for( int k=0; k<length; k++)
										getActivityFrom( eventJsonArray.getString(k) );
										
									if( firstLoad==true )
									{
										//load data done; inform user interface
										Message msg2 = uiHandler.obtainMessage(MSG_WHAT_LOAD_DATA_DONE);
										msg2.sendToTarget();
										firstLoad = false;
//										Log.i("myevent", "load done");
									}
									else if( refreshing==true )
									{
										//refresh data done
										Message msgRefresh = uiHandler.obtainMessage(MSG_WHAT_REFRESH_DONE);
										msgRefresh.obj = "After refresh " + System.currentTimeMillis();
										msgRefresh.sendToTarget();
										refreshing=false;
//										Log.i("myevent", "refresh done");
									}
									else   //load more
									{
										Message msgLoadMore = uiHandler.obtainMessage(MSG_WHAT_GET_MORE_DONE);
										msgLoadMore.obj = "After more " + System.currentTimeMillis();
										msgLoadMore.sendToTarget();
									}
								
									getAllavatars();
								}
								else
									Toast.makeText(context, "get events��������ֵ��"+returnCMD, Toast.LENGTH_SHORT).show();	
								break;
								
							case OperationCode.GET_AVATAR:
								
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{
									final int avatarForUserId = returnJson.optInt("id");
									final String imageStr = returnJson.optString("avatar");

									new Thread()
									{
										public void run(){
											
											//��ŵ�map���棬��֪�����ݸ��£��ڳ�ʼ����Ƭ��ʱ��ʹ��
											byte[] temp = imageUtil.String2Bytes(imageStr);
											 try {
										            if(temp!=null)
										            {
										                Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);												                										                
										                imageUtil.getInstance().addBitmapToMemoryCache(avatarForUserId, bitmap);

										                //notify card adapter
										                Message msg = new Message();
														msg.what = cardAdapter.CARD_INFO_CHANGE;
														msgCard.sendMessage(msg);
														
														//�ǵ�Ҫ�����߳���д�ļ�
														imageUtil.savePhoto(avatarForUserId, bitmap);
										            }
										        } catch (Exception e) {
										            // TODO Auto-generated catch block
										            e.printStackTrace();
										        } 
										}
									}.start();
																		
//									myEventsAdapter.notifyDataSetChanged();
								}
								else if ( returnCMD==ReturnCode.REQUEST_FAIL ) 
								{
//									Log.e("myevent", "����ͷ��ʧ��");
								}
								break;
								
							default:
								break;
							}
						}
						catch (JSONException e) {
				            // TODO Auto-generated catch block
				            e.printStackTrace();
				        }
				
			}
			else
			{
				Toast.makeText(context, "����������ʱ", Toast.LENGTH_SHORT).show();
			}	
		}
	}
}
