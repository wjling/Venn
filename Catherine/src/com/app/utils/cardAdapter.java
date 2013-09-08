package com.app.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.addActivityPack.CircularImage;
import com.app.catherine.R;
import com.app.comment.CommentPage;
import com.app.localDataBase.NotificationTableAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class cardAdapter extends BaseAdapter
{
	private static final String TAG = "cardAdapter";
	private ArrayList<HashMap<String, Object>> list;
	private LayoutInflater mInflater;
	
	private Context context;
	private int resource;
	private String from[];
	private int to[];	
	private int screenW;
	private imageUtil forImageUtil  = imageUtil.getInstance();
	private boolean inActivity;
	
	private int userId = -1;

	private myHandler ncHandler = new myHandler();
	
	public cardAdapter() {
		// TODO Auto-generated constructor stub
		Log.e(TAG, "cardAdapter constructor error: no params");
	}
	
	public cardAdapter(Context context, ArrayList<HashMap<String, Object>> list, int resource,
			String []from, int []to, int screenW, boolean inActivity, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.resource = resource;
		this.from = from;
		this.to = to;
		this.screenW = screenW;
		this.inActivity = inActivity;
		this.userId = userId;
		Log.e("cardAdapter",   "in cardadapter constructor");
	}
	
	private void SetContentWidth(View main, View v)
    {
		//reference
		View dateView = main.findViewById(R.id.leftDateBlock);
		RelativeLayout.LayoutParams paramsdate = (RelativeLayout.LayoutParams)dateView.getLayoutParams();
		int dateW = paramsdate.width;

		int delPix = 110 * dateW / 55;
    	int screenWidth = screenW;
    	
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
		params.width = screenWidth - delPix;
		v.setLayoutParams(params);
	}
	
	private void SetUserInfoWidth(View main, View v)
    {
		//reference
		View dateView = main.findViewById(R.id.activityDate);
		RelativeLayout.LayoutParams paramsdate = (RelativeLayout.LayoutParams)dateView.getLayoutParams();
		int dateW = paramsdate.width;

		int delPix = 40 * dateW / 55;
    	int screenWidth = screenW;
    	
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
		params.width = screenWidth - delPix;
		v.setLayoutParams(params);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) 
	{

		// TODO Auto-generated method stub
		if (view==null) {
			view = mInflater.inflate(resource, null);
		}
		
		//set text or set something else about the view
		init(view, position);
		for (int i = 0; i < to.length; i++) 
		{
			TextView Title = (TextView)view.findViewById( to[i] );
			Title.setText( (String)list.get(position).get( from[i] ) );
		}
//		view.setOnTouchListener(itemOnTouchListener);
		return view;
	}
	
	OnTouchListener itemOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int action = event.getAction();
			if(action == MotionEvent.ACTION_DOWN)
			{
				
			}
			else if(action == MotionEvent.ACTION_MOVE)
			{
				
			}
			else if(action == MotionEvent.ACTION_UP)
			{
				
			}
			return false;
		}
	};
	
	private void addAParticipant( View v, int id )
	{
		LinearLayout userInfoBlockSecondBlock = (LinearLayout)v.findViewById(R.id.userAvatar);
		RelativeLayout child = (RelativeLayout)((Activity) context).getLayoutInflater().inflate(R.layout.addcircleimage, null);
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
	
	private void clearAvatar(View v)
	{
		LinearLayout userInfoBlockSecondBlock = (LinearLayout)v.findViewById(R.id.userAvatar);
		int count = userInfoBlockSecondBlock.getChildCount();

		userInfoBlockSecondBlock.removeAllViews();
	}
	
	private void init(View view, final int pos)
	{
		HashMap<String, Object> item = list.get(pos);
		JSONArray avatarJsonArray = (JSONArray) item.get("photolistJsonArray");
		
		int length = avatarJsonArray.length();
		Log.e(TAG, "pos " + pos + "; length " + length );
		try {
			int i=0;
			clearAvatar(view);
			for ( ; i < length; i++) 
			{
				int id = avatarJsonArray.getInt(i);
				
				addAParticipant( view, id );
			}

		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		View activityInfoAllView = view.findViewById(R.id.activityInfoAll);
		SetContentWidth(view, activityInfoAllView);
//		View userInfoView = view.findViewById(R.id.userInfo);
//		SetUserInfoWidth(view, userInfoView);
		
		CircularImage join = (CircularImage)view.findViewById(R.id.joinBtn);
		
		if( inActivity==false )
		{
			join.setImageResource(R.drawable.join);	
			join.setOnClickListener(	new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						join(pos);					
					}
			});
		}
		
		View comment_btn = view.findViewById(R.id.comment_btn);
		View takephoto_btn = view.findViewById(R.id.takephoto_btn);
		
		comment_btn.setTag(pos);
		comment_btn.setOnClickListener(buttonsOnClickListener);
		takephoto_btn.setOnClickListener(buttonsOnClickListener);
//		view.findViewById(R.id.comment_btn).setOnClickListener(BtnListener);
//		view.findViewById(R.id.takephoto_btn).setOnClickListener(BtnListener);
	}
	
	private OnClickListener buttonsOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
//			case R.id.joinBtn:
//				join(pos);
//				break;
			case R.id.comment_btn:
				Toast.makeText(context, "comment", Toast.LENGTH_SHORT).show();
				int position = (Integer) v.getTag();
				HashMap<String, Object> an_activity = list.get(position);
				int event_id = Integer.parseInt(an_activity.get("event_id").toString());
				Intent intent = new Intent();
				intent.putExtra("userId", userId);
				intent.putExtra("eventId", event_id);
				intent.setClass(context, CommentPage.class);
				context.startActivity(intent);
				break;
			case R.id.takephoto_btn:
				Toast.makeText(context, "take photo", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	
	private void join( final int pos)
	{
		final int currentUId = (Integer) list.get(pos).get("id");
		final int eventId = (Integer) list.get(pos).get("event_id");
		final int item_id = (Integer) list.get(pos).get("item_id");
		
//		Toast.makeText(context, "join: pos="+pos + " uid=" + currentUId, Toast.LENGTH_SHORT).show();
		
		AlertDialog.Builder builder = new Builder(context);
		
		builder.setTitle("活动验证").setIcon(R.drawable.ic_launcher).setMessage("加入活动吗？").create();
		builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				JSONObject params = new JSONObject();
				try {
					params.put("cmd", 997);
					params.put("id", currentUId);
					params.put("event_id", eventId);
					params.put("result", 1);
					HttpSender httpSender = new HttpSender();
					httpSender.Httppost(OperationCode.PARTICIPATE_EVENT, params, ncHandler);
					Log.e("cardAdapter", "join: " + params);
					
					NotificationTableAdapter adapter = new NotificationTableAdapter(context);
					adapter.deleteData( item_id );
					
					list.remove(pos);
					cardAdapter.this.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				JSONObject params = new JSONObject();
//				try {
//					params.put("cmd", 997);
//					params.put("id", currentUId);
//					params.put("event_id", eventId);
//					params.put("result", 0);
//					HttpSender httpSender = new HttpSender();
//					httpSender.Httppost(OperationCode.PARTICIPATE_EVENT, params, ncHandler);
//					Log.e("cardAdapter", "refuse: " + params);
//					
//					NotificationTableAdapter adapter = new NotificationTableAdapter(context);
//					adapter.deleteData( item_id );
//					
//					list.remove(pos);
//					cardAdapter.this.notifyDataSetChanged();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});
		
		builder.show();
	}
	
	public class myHandler extends Handler
	{
		public myHandler() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what)
			{
			case OperationCode.PARTICIPATE_EVENT:
				
				break;
				default: break;
			}
		}
	}

}
