package com.app.PhotoMainPage;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.comment.CommentPage;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoMainPage extends Activity{

	private final String TAG = "PhotoMainPage";
	private int userId;
//	private int eventId;
	private int photo_id;
	private int author_id;
	private String author;
	private String time;
	private int good;
	private String description;
	private int sequence;
	private boolean isZan;
	private View contentView;
	
	private Button backBtn;
	private TextView photoDeleteBtn;
	private ImageView masterAvatar;
	private TextView masterName;
	private TextView masterTime;
	private ImageView photo;
	private TextView photoDescription;
	private LinearLayout photoZan;
	private ImageView photoZanImage;
	private TextView photoZanSum;
	private ImageView replyImage;
	private ImageView shareImage;
	private ImageView downloadImage;
	private TextView getMoreCommentsBtn;
	private LinearLayout photoCommentListRootView;
	
	private ArrayList<HashMap<String, Object>> photoComments = new ArrayList<HashMap<String,Object>>();
	private myHandler PMPHandler = new myHandler();
	private HttpSender httpSender;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(this).inflate(R.layout.photo_main_page, null);
		setContentView(contentView);
		init();
		
		getCommentsFromServer(sequence);
	}
	
	private void init() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
//		eventId = intent.getIntExtra("eventId", -1);
		photo_id = intent.getIntExtra("photo_id", -1);
		sequence = 0;
		isZan = false;
		author = "";
		time = "";
		description = "";
		good= 0;
		httpSender = new HttpSender();
		
		backBtn = (Button) contentView.findViewById(R.id.photo_main_page_backBtn);
		photoDeleteBtn = (TextView)contentView.findViewById(R.id.photo_main_page_deletePhoto);
		masterAvatar = (ImageView)contentView.findViewById(R.id.photo_main_page_masterAvatar);
		masterName = (TextView)contentView.findViewById(R.id.photo_main_page_masterName);
		masterTime = (TextView)contentView.findViewById(R.id.photo_main_page_time);
		photo = (ImageView) contentView.findViewById(R.id.photo_main_page_photo);
		photoDescription = (TextView) contentView.findViewById(R.id.photo_main_page_description);
		photoZan = (LinearLayout) contentView.findViewById(R.id.photo_main_page_photoZan);
		photoZanImage = (ImageView) contentView.findViewById(R.id.photo_main_page_zanImage);
		photoZanSum = (TextView)contentView.findViewById(R.id.photo_main_page_zanSum);
		replyImage = (ImageView) contentView.findViewById(R.id.photo_main_page_replyImage);
		shareImage = (ImageView) contentView.findViewById(R.id.photo_main_page_share);
		downloadImage = (ImageView) contentView.findViewById(R.id.photo_main_page_download);
		getMoreCommentsBtn = (TextView) contentView.findViewById(R.id.photo_main_page_getMoreComments);
		photoCommentListRootView = (LinearLayout) contentView.findViewById(R.id.photo_main_page_commentList);
		
		backBtn.setOnClickListener(backBtnOnClickListener);
		photoDeleteBtn.setOnClickListener(photoDeleteonOnClickListener);
		photoZan.setOnClickListener(photoZanOnClickListener);
		replyImage.setOnClickListener(replyImageOnClickListener);
		getMoreCommentsBtn.setOnClickListener(getMoreCommentsonOnClickListener);
		
//		imageUtil.getInstance().registerHandler(PMPHandler, "PhotoMainPage");
	}
	
	private void getCommentsFromServer(int sequence)
	{
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("photo_id", photo_id);
			params.put("sequence", sequence);
			HttpSender httpSender = new HttpSender();
			httpSender.Httppost(OperationCode.GET_PCOMMENTS, params, PMPHandler);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/********************** Listeners(Begin) ****************************/
	OnClickListener backBtnOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener photoDeleteonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new Builder(PhotoMainPage.this);
			builder.setMessage("ȷ��Ҫɾ������ͼƬ��")
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					JSONObject params = new JSONObject();
					try {
						//��ʱ��û��������ܣ��Ժ���˵���ȷ���
						params.put("id", userId);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			})
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.show();
			
		}
	};
	
	OnClickListener photoZanOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			JSONObject params = new JSONObject();
			try {
				params.put("id", userId);
				params.put("photo_id", photo_id);
				if(isZan)
				{
					isZan = false;
					good--;
					params.put("operation", 2);
					photoZanImage.setImageResource(R.drawable.like);
				}
				else
				{
					isZan = true;
					good++;
					params.put("operation", 3);
					photoZanImage.setImageResource(R.drawable.liked);
				}
				photoZanSum.setText("("+ good + ")");
				httpSender.Httppost(OperationCode.ADD_GOOD, params, PMPHandler);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	};
	
	OnClickListener replyImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final EditText edtext = new EditText(PhotoMainPage.this);
			AlertDialog.Builder builder = new Builder(PhotoMainPage.this);
			builder.setTitle("�������µ����ۣ� ")
			.setIcon(R.drawable.ic_launcher)
			.setView(edtext)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					JSONObject params = new JSONObject();
					String text = edtext.getText().toString();
					if(!"".equals(text.trim().toString()))
					{
						try {
							params.put("id", userId);
							params.put("photo_id", photo_id);
							params.put("content", text);
		//					params.put("sequence", comment_sequence);
							httpSender.Httppost(OperationCode.ADD_PCOMMENT, params, PMPHandler);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						Toast.makeText(PhotoMainPage.this, "�����۲���Ϊ��", Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.show();
		}
	};
	
	OnClickListener shareImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener downloadImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener getMoreCommentsonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			JSONObject params = new JSONObject();
			try {
				params.put("id", userId);
				params.put("photo_id", photo_id);
				params.put("sequence", sequence);
				httpSender.Httppost(OperationCode.GET_PCOMMENTS, params, PMPHandler);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	/********************** Listeners(End) ****************************/
	
	
	/********************** myHandler(Begin) ********************************/
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
			case OperationCode.GET_PCOMMENTS:
				if(sequence == 0)
				{
					handle_GET_PCOMMENTS(msg);
					displayMainInfo();
					displayReplies();
				}
				else
				{
					handle_GET_PCOMMENTS(msg);
					displayReplies();
				}
				break;
			case OperationCode.ADD_PCOMMENT:
				handle_ADD_PCOMMENT(msg);
				break;
			case OperationCode.DELETE_PCOMMENT:
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
	/********************** myHandler(End) ********************************/
	
	
	
	/**
	 * @author WJL
	 * @description 
	 * �õ���json��ʽ��
	 * cmd(int), pcomment_list[], sequence(int),
	 * isZan(boolean), photo_id(int), author(int), author_id(int),
	 * good(int), time(String)
	 *
	 *pcomment_list��ÿ��Ԫ����һ��json:
	 *pcomment_id(int), photo_id(int), author(String), author_id(int), 
	 *content(String), time(String),
	 *good(int)<�����ò�������ΪͼƬ����û�е���>
	 *
	 * @param msg ���������ص�Message
	 */
	private void handle_GET_PCOMMENTS(Message msg)
	{
		try {
			JSONObject response = new JSONObject(msg.obj.toString());
			int cmd = response.getInt("cmd");
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				if(sequence == 0)
				{
					photoComments.clear();
					isZan = response.getBoolean("isZan");
					photo_id = response.getInt("photo_id");
					author = response.getString("author");
					author_id = response.getInt("author_id");
					time = response.getString("time");
					good = response.getInt("good");
					description = response.getString("description");
				}
				sequence = response.getInt("sequence");
				if(sequence != -1)
				{
					JSONArray comments = response.getJSONArray("pcomment_list");
					int len = comments.length();
					for(int i=0;i<len;i++)
					{
						JSONObject comment = comments.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("content", comment.getString("content"));
						map.put("author", comment.getString("author"));
						map.put("author_id", comment.getInt("author_id"));
						map.put("time", comment.getString("time"));
//					 	map.put("good", comment.getInt("good"));
						map.put("pcomment_id", comment.getInt("pcomment_id"));
						map.put("photo_id", comment.getInt("photo_id"));
						photoComments.add(map);
					}
				}
				
				
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(this, "����������", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "δ֪����", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @author WJL
	 * @description 
	 * ���ص�json�Ǹղŷ���һ�������ۣ�
	 * cmd(int), photo_id(int), content(String), time(String),
	 * pcomment_id(int), author_id(int), author(String), 
	 * good(int) <�����ò���good,��ΪͼƬ������û�е��޵Ĺ���>
	 *
	 * @param msg
	 */
	private void handle_ADD_PCOMMENT(Message msg)
	{
		try {
			JSONObject response = new JSONObject(msg.obj.toString());
			int cmd = response.getInt("cmd");
			if( cmd == ReturnCode.NORMAL_REPLY)
			{
				Toast.makeText(this, "������۳ɹ�", Toast.LENGTH_SHORT).show();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("photo_id", response.getInt("photo_id"));
				map.put("author", response.getString("author"));
				map.put("author_id", response.getInt("author_id"));
				map.put("content", response.getString("content"));
				map.put("time", response.getString("time"));
				map.put("pcomment_id", response.getInt("pcomment_id"));
				photoComments.add(0,map);
				addNewComment(0, map);
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(this, "����������", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "δ֪����", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void displayMainInfo()
	{
//		masterAvatar
		Bitmap avatar = imageUtil.getInstance().getAvatar(author_id);
		masterAvatar.setImageBitmap(avatar);
		if(isZan)
		{
			photoZanImage.setImageResource(R.drawable.liked);
		}
		else
		{
			photoZanImage.setImageResource(R.drawable.like);
		}
		//testing
		Drawable test = masterAvatar.getDrawable();
		photo.setImageDrawable(test);
		
		photoZanSum.setText("("+ good + ")");
		masterName.setText(author);
		masterTime.setText("�ϴ��� " + time);
		photoDescription.setText(description);
		
	}
	
	private void displayReplies()
	{
		int size = photoComments.size();
		photoCommentListRootView.removeAllViews();
		for(int i=0;i<size;i++)
		{
			HashMap<String, Object> map = photoComments.get(i);
			addNewComment(-1,map);
		}
	}
	
	private void addNewComment(int index, HashMap<String, Object> map)
	{
		View v = LayoutInflater.from(this).inflate(R.layout.photo_reply_item, null);
		ImageView authorAvatar = (ImageView) v.findViewById(R.id.photo_reply_item_authorAvatar);
		TextView authorName = (TextView) v.findViewById(R.id.photo_reply_item_authorName);
		TextView replyTime = (TextView)v.findViewById(R.id.photo_reply_item_time);
		TextView replyContent = (TextView)v.findViewById(R.id.photo_reply_item_replyContent);
		String author = map.get("author").toString();
		int author_id = Integer.parseInt(map.get("author_id").toString());
		String time = map.get("time").toString();
		String content = map.get("content").toString();
		Bitmap avatar = imageUtil.getInstance().getAvatar(author_id);
		authorAvatar.setImageBitmap(avatar);
		authorName.setText(author);
		replyTime.setText(time);
		replyContent.setText(content);
		if( index < 0)
		{
			photoCommentListRootView.addView(v);
		}
		else
		{
			photoCommentListRootView.addView(v, index);
		}
	}
}
