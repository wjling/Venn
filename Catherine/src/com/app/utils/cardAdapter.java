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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
	private boolean inActivity;
	private int userId = -1;
	private Handler ncHandler;
	
	public cardAdapter() {
		// TODO Auto-generated constructor stub
		Log.e(TAG, "cardAdapter constructor error: no params");
	}
	
	public cardAdapter(Context context, ArrayList<HashMap<String, Object>> list, int resource,
			String []from, int []to, int screenW, boolean inActivity, int userId, Handler handler) {
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
		this.ncHandler = handler;
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
		
		return view;
	}
	
	private void init(View view, int pos)
	{
		HashMap<String, Object> item = list.get(pos);
		Boolean open = (Boolean)item.get("open");
		
		View firstLayer = view.findViewById(R.id.firstLayerCard);
		View secondLayer = view.findViewById(R.id.secondLayerCard);
		
		if( open.equals(false))
		{
			secondLayer.setVisibility(View.GONE);
			firstLayer.setVisibility(View.VISIBLE);
			initFirstLayer( item, view, pos);
		}
		else
		{
			firstLayer.setVisibility(View.GONE);
			secondLayer.setVisibility(View.VISIBLE);
			initSecondLayer(item, view, pos);
		}
	}
	
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

		userInfoBlockSecondBlock.removeAllViews();
	}
	
	private void initFirstLayer(final HashMap<String, Object> item, final View view, final int pos)
	{
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
		View slideBtn = view.findViewById(R.id.slideBtn);
		
		comment_btn.setTag(pos);
		comment_btn.setOnClickListener(buttonsOnClickListener);
		takephoto_btn.setOnClickListener(buttonsOnClickListener);
		slideBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View secondView = view.findViewById(R.id.secondLayerCard);
				SetSecondWidth(secondView);
				secondView.setVisibility(View.VISIBLE);
				slideview(view.findViewById(R.id.firstLayerCard), 0, -(screenW));	
				
				item.put("open", true);
			}
		});
		
		for (int i = 0; i < to.length; i++) 
		{
			TextView Title = (TextView)view.findViewById( to[i] );
			Title.setText( (String)list.get(pos).get( from[i] ) );
		}
	}
	
	private void SetSecondWidth(View main)
    {
		//reference
		View dateView = main.findViewById(R.id.secondLayer_LeftBlock);
		RelativeLayout.LayoutParams paramsdate = (RelativeLayout.LayoutParams)dateView.getLayoutParams();
		int dateW = paramsdate.width;

		int delPix = 50 * dateW / 40;
    	int screenWidth = screenW;
    	
    	View imageFlowView = main.findViewById(R.id.second_imageFlow);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageFlowView.getLayoutParams();
		params.width = (screenWidth - delPix)/2;
		imageFlowView.setLayoutParams(params);
		
		View second_commentsBlock = main.findViewById(R.id.second_commentsBlock);
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)second_commentsBlock.getLayoutParams();
		params2.width = (screenWidth - delPix)/2;
		second_commentsBlock.setLayoutParams(params2);
	}
	
	private void initSecondLayer(final HashMap<String, Object> item, final View view, int pos)
	{
		SetSecondWidth(view);
		
		View comment_btn = view.findViewById(R.id.second_comment_btn);
		View takephoto_btn = view.findViewById(R.id.second_takephoto_btn);
		View slideBtn = view.findViewById(R.id.second_slideBtn);
		
		comment_btn.setTag(pos);
		comment_btn.setOnClickListener(buttonsOnClickListener);
		takephoto_btn.setOnClickListener(buttonsOnClickListener);
		slideBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				view.findViewById(R.id.firstLayerCard).setVisibility(View.VISIBLE);
				slideview(view.findViewById(R.id.secondLayerCard), 0, (screenW));	
				item.put("open", false);
			}
		});
	}
	
	public void slideview(final View view, final float p1,  final double p2) 
	{
		long durationMillis = 500;
		
	    TranslateAnimation animation = new TranslateAnimation(p1, (float) p2, 0, 0); 
		animation.setDuration(durationMillis);	    
		
	    animation.setAnimationListener(new Animation.AnimationListener() 
	    {
	        @Override
	        public void onAnimationStart(Animation animation) 
	        {
	        }
	        
	        @Override
	        public void onAnimationRepeat(Animation animation) 
	        {
	        }
	        
	        @Override
	        public void onAnimationEnd(Animation animation) 
	        {
	            int left = view.getLeft()+(int)(p2-p1);
	            int top = view.getTop();
	            int width = view.getWidth();
	            int height = view.getHeight();
	            
	            view.clearAnimation();  //must before layout(l,t,r,b)
	            view.layout(left, top, left+width, top+height);
				
				Message msg = new Message();
				msg.what = 555;
				ncHandler.sendMessage(msg);
	        }
	    });
	    
	    view.startAnimation(animation);
	}
	
	private OnClickListener buttonsOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {

				case R.id.comment_btn:
				case R.id.second_comment_btn:
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
				case R.id.second_takephoto_btn:
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
	


}
