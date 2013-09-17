package com.app.ui.menu.FriendCenter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.adapters.AdapterForFriendList;
import com.app.addFriendPack.searchFriend;
import com.app.catherine.R;
import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.TableFriends;
import com.app.ui.NotificationCenter;
import com.app.utils.HttpSender;
import com.app.utils.Messager;
import com.app.utils.MyBroadcastReceiver;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;
import com.app.widget.LetterSidebar;
import com.app.widget.LetterSidebar.OnTouchingLetterChangedListener;


public class FriendCenter {

	private final String TAG = "FriendCenter";
	private Context context;
	private View friendCenterView;
	private View friendNotificationView;
//	private Button recommendedFriendsBtn;
//	private Button notificationBtn;
	private EditText searchEditText;
	public ListView functionsListView;
	public ListView friendListView;
	private LetterSidebar sidebar;
	
	private int userId = -1;
	private Handler uiHandler;
	private myHandler fcHandler = new myHandler();
	private boolean isFirstVisit;
	public static final int MSG_WHAT_ON_UPDATE_LIST = -1;
	public static final int MSG_WHAT_ON_UPDATE_AVATAR = -2;
	public static final int MSG_WHAT_ON_NEW_FRIEND_COME = -3;
	
	ArrayList<FriendStruct> friends;
	ArrayList<HashMap<String, Object>> functionsList = new ArrayList<HashMap<String,Object>>();
	ArrayList<HashMap<String, Object>> friendList = new ArrayList<HashMap<String,Object>>();
	ArrayList<HashMap<String, Object>> subfriendList = new ArrayList<HashMap<String,Object>>();
	AdapterForFriendList friendListAdapter, functionsAdapter;
	HashMap<String, Integer> alphaIndex = new HashMap<String, Integer>();
	HashMap<String, Integer> subalphaIndex = new HashMap<String, Integer>();
	Comparator<Object> chinese_Comparator = Collator.getInstance(Locale.CHINA);
	
	
	private PinYinComparator myPinYinComparator = new PinYinComparator();
	
	public FriendCenter(Context context, View friendsCenterView, Handler uiHandler, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.friendCenterView = friendsCenterView;
		this.userId = userId;
		this.uiHandler = uiHandler;
		this.isFirstVisit = true;
	}
	
	
	public void init() {
		// TODO Auto-generated method stub
//	    new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
                setLayout();
                askServerForFriendList();
//            }        
//        }).start();
	}
	
	public void setLayout()
	{
		friendNotificationView = LayoutInflater.from(context).inflate(R.layout.friend_center_notification, null);
//		recommendedFriendsBtn = (Button)friendCenterView.findViewById(R.id.menu_friend_center_recommendfriendBtn);
//		notificationBtn = (Button)friendCenterView.findViewById(R.id.menu_friend_center_notificationBtn);
		searchEditText = (EditText)friendCenterView.findViewById(R.id.menu_friend_center_searchmyfriend);
		
		searchEditText.setOnClickListener(editTextOnClickListener);
		searchEditText.setLongClickable(false);
		functionsListView = (ListView)friendCenterView.findViewById(R.id.menu_friend_center_functions);
		functionsListView.setDivider(null);
		functionsAdapter = new AdapterForFriendList(context, functionsList, 
	            R.layout.friend_list_item, 
	            new String[] {"avatar", "fname"}, 
	            new int[] {R.id.friend_list_item_avatar, R.id.friend_list_item_fname});
		functionsListView.setOnItemClickListener(functionsListListener);
		friendListView = (ListView)friendCenterView.findViewById(R.id.menu_friend_center_friendlist);
		friendListView.setDivider(null);
		friendListAdapter = new AdapterForFriendList(context, friendList, 
				R.layout.friend_list_item, 
				new String[] {"avatar", "fname"}, 
				new int[] {R.id.friend_list_item_avatar, R.id.friend_list_item_fname});
		functionsListView.setAdapter(functionsAdapter);
		friendListView.setAdapter(friendListAdapter);
		friendListView.setOnScrollListener(friendListScrollListener);
		sidebar=(LetterSidebar)friendCenterView.findViewById(R.id.lettersidebar);
        sidebar.setOnTouchingLetterChangedListener(letterChangedListener);
        
        initFunctionsList();
	}
	
	OnScrollListener friendListScrollListener = new OnScrollListener() {
        
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            if ( visibleItemCount > 0 )
            {
                HashMap<String, Object> map = (HashMap<String, Object>)friendListAdapter.getItem(firstVisibleItem);
                if (map != null)
                {
                    int letter = PinYinComparator.getPinYin((String)(map.get("fname"))).toUpperCase().charAt(0) - 'A';
                    sidebar.OnScrollChangedLetter(letter);
                }
            }
        }
    };
	
	OnItemClickListener functionsListListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // TODO Auto-generated method stub
            if (arg2 == 0)
            {
//              Intent intent1 = new Intent();
//              intent1.setClass(context, searchFriend.class);
//              intent1.putExtra("userId", userId);
//              context.startActivity(intent1);
            }
            else if (arg2 == 1) 
            {
                Intent intent2 = new Intent();
                intent2.setClass(context, NotificationCenter.class);
                intent2.putExtra("userId", userId);
                context.startActivity(intent2);
            }
            else {
                Log.i("Friend Center", "error position in functions list");
            }
        }
	    
	};

	OnTouchingLetterChangedListener letterChangedListener = new OnTouchingLetterChangedListener() {
	   
	    @Override
        public void onTouchingLetterChanged(String s) {
            // TODO Auto-generated method stub
            Log.i("Letter Sidebar letter is : ", s);
            //int position = s.charAt(0) - 'A';
            //Log.i("Letter Sidebar position is : ", position + "");
            Integer position = alphaIndex.get(s);
            if (position != null)
            {
                friendListView.setSelection(position);
            }
        }
	};
	
OnClickListener editTextOnClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            searchEditText.setFocusable(true);
            searchEditText.setFocusableInTouchMode(true);
        }
};
	
	
	public void initFunctionsList()
	{
	    String[] functionTitle = { "�����Ƽ�", "��Ϣ����" };
	    for (String title : functionTitle)
	    {
	        HashMap<String, Object> map = new HashMap<String, Object>();
	        map.put("fname", title);
	        map.put("fid", 0);
	        functionsList.add(map);
	    }
	}
	
	public void askServerForFriendList()
	{
		TableFriends tbFriends = new TableFriends(context);
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("friends_number", tbFriends.size(userId));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSender httpSender = new HttpSender();
		httpSender.Httppost(OperationCode.SYNCHRONIZE, params, fcHandler);
	}
	
	public void showFriendList()
	{
	    if (!isFirstVisit) {
	        return ;
	    }
	    Messager.getInstance().regsiterHandler(fcHandler, "FriendCenter");
	    imageUtil.getInstance().unregisterHandler("FriendCenter");
	    
        TableFriends tf = new TableFriends(context);
        friends = tf.getAllFriends(userId+"");
        if(friends.size() == 0)
        {
            Toast.makeText(context, "����ʱ��û�к���Ŷ", Toast.LENGTH_SHORT).show();
        }
        else
        { 
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub     
                    subfriendList.clear();
                    for (FriendStruct fs : friends)
                    {
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("uid", userId);
                            map.put("fname", fs.fname);
                            map.put("fid",fs.fid);
                            subfriendList.add(map);
                             
                    }
                    Collections.sort(subfriendList, myPinYinComparator);
                    insertLetterTag();
                    Message msg = fcHandler.obtainMessage(MSG_WHAT_ON_UPDATE_LIST);
                    msg.sendToTarget();
                }
               
            }).start();
        }
        
		imageUtil.getInstance().registerHandler(fcHandler, "FriendCenter");
		isFirstVisit = false;

	}
	
	public void insertLetterTag()
	{
	    if (subfriendList == null)
	    {
	        Log.i("insertLetterTag: ","subfriendList is null");
	        return;
	    }
	    HashMap<String, Integer> tempIndex = new HashMap<String, Integer>();
	    ArrayList<HashMap<String, Object>> tempList = new ArrayList<HashMap<String,Object>>();
	    String currentTag = "";
	    String tempTag;
	    for (HashMap<String, Object> hMap : subfriendList)
	    {
	        tempTag = PinYinComparator.getPinYin((String)hMap.get("fname")).charAt(0) + "";
	        tempTag = tempTag.toUpperCase();
	        if (!tempTag.equals(currentTag))
	        {
	            currentTag = tempTag;
	            HashMap<String, Object> tempMap = new HashMap<String, Object>();
	            tempMap.put("fid", -1);
	            tempMap.put("fname", currentTag);
	            int pos = tempList.size();
	            tempIndex.put(currentTag, pos);
	            tempList.add(tempMap); 
	        }
	        tempList.add(hMap);
	    }

	    subfriendList.clear();
	    subfriendList.addAll(tempList);
	    subalphaIndex.clear();
	    subalphaIndex.putAll(tempIndex);
	}
	
	public void sychronizeFriendsList(Message msg)
	{
		try{
			TableFriends tbFriends = new TableFriends(context);
			ArrayList<FriendStruct> friends = new ArrayList<FriendStruct>();
			JSONObject jsResponse = new JSONObject(msg.obj.toString());
			int cmd = jsResponse.getInt("cmd");
			Log.i(TAG, "ͬ�������յ���json: "+jsResponse);
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				JSONArray jsArray = jsResponse.getJSONArray("friend_list");
				int length = jsArray.length();
				if(length == 0)
				{
					Toast.makeText(context, "�����б��Ѿ����£�����Ҫͬ��", Toast.LENGTH_SHORT).show();
				}
				else
				{
					tbFriends.deleteFriendsTable(userId);
					for(int i=0;i<length;i++)
					{
						JSONObject jo = jsArray.getJSONObject(i);
						jo.put("uid", userId);
						Log.i(TAG, "һ�����ѵ���Ϣ: "+jo.toString());
						FriendStruct fs = new FriendStruct();
						fs = FriendStruct.getFromJSON(jo);
//						fs.uid = userId;
						friends.add(fs);
					}
					tbFriends.add(friends);	//add to the friends table of the local database
					Toast.makeText(context, "�����б�ͬ���ɹ�", Toast.LENGTH_SHORT).show();
				}
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(context, "����������", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(context, "���Ѿ��������˽ٳ�~", Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static class PinYinComparator implements Comparator<HashMap<String,Object>>
	{

		@Override
		public int compare(HashMap<String,Object> arg0, HashMap<String,Object> arg1) {
			// TODO Auto-generated method stub
			String pinYinName0 = getPinYin(arg0.get("fname").toString());
			String pinYinName1 = getPinYin(arg1.get("fname").toString());
			return pinYinName0.compareTo(pinYinName1);
		}
		
		
		public static String getPinYin(String inputString) {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
 
            char[] input = inputString.trim().toCharArray();// ���ַ���ת�����ַ�����
            String output = "";
 
            try {
                for (int i = 0; i < input.length; i++) {
                    // \\u4E00��unicode���룬�ж��ǲ�������
                    if (java.lang.Character.toString(input[i]).matches(
                            "[\\u4E00-\\u9FA5]+")) {
                        // ������ƴ����ȫƴ�浽temp����
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                input[i], format);
                        // ȡƴ���ĵ�һ������
                        output += temp[0];
                    }
                    // ��д��ĸת����Сд��ĸ
                    else if (input[i] > 'A' && input[i] < 'Z') {
                        output += java.lang.Character.toString(input[i]);
                        output = output.toLowerCase();
                    }
                    output += java.lang.Character.toString(input[i]);
                }
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return output;
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
			case OperationCode.SYNCHRONIZE:
				sychronizeFriendsList(msg);
				break;
            case MSG_WHAT_ON_UPDATE_LIST:
                friendList.clear();
                friendList.addAll(subfriendList);
                alphaIndex.clear();
                alphaIndex.putAll(subalphaIndex);
                friendListAdapter.notifyDataSetChanged();
                break;
            case MSG_WHAT_ON_UPDATE_AVATAR:
                friendListAdapter.notifyDataSetChanged();
                break;
            case MSG_WHAT_ON_NEW_FRIEND_COME:
                isFirstVisit = true;
                showFriendList();
			default: 
			    break;
			}
			super.handleMessage(msg);
		}
	}
	
	public View getFriendCenterView()
	{
		return friendCenterView;
	}
	
	public View getFriendNotificationView()
	{
		return friendNotificationView;
	}
	
	
}
