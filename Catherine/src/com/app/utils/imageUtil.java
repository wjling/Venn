package com.app.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.adapters.MessagaAdapter;
import com.app.catherine.R;
import com.app.ui.menu.FriendCenter.FriendCenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


public class imageUtil 
{
    private final static String APP_PATH = Environment.getExternalStorageDirectory() + "/Catherine/";
	private final static String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/Catherine/Avatar/";
//	private HashMap<Integer, Bitmap> imageMap = new HashMap<Integer, Bitmap>();
	
	private volatile static imageUtil uniqueInstance = null;
	private LruCache<Integer, Bitmap> mMemoryCache;
	private int maxMemory;
	private int cacheSize;
	private myHandler mHandler;
	private Handler fcHandler, ncHandler, commentHandler;
	
	private imageUtil()
	{
		//get max available vm memory
		maxMemory = (int )(Runtime.getRuntime().maxMemory() / 1024);
		//use 1/8th of the available memory for this memory cache
		cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				//the cache size will be measured in kilobytes 
				return bitmap.getRowBytes()*bitmap.getHeight() / 1024;
//				return bitmap.getByteCount() / 1024;
			}
		};
		mHandler = new myHandler();
		fcHandler = null;
		ncHandler = null;
		commentHandler = null;
	}
	
    
    public static imageUtil getInstance(){
        if(uniqueInstance == null){
            synchronized(imageUtil.class) {
                if(uniqueInstance == null) {
                    uniqueInstance = new imageUtil();
                }
            }
        }
        
        return uniqueInstance;       
    }
	
	 public static byte[] String2Bytes(String imgStr) 
    {
        byte[] bytes = null;
        try {
            bytes = Base64.decode(imgStr, Base64.DEFAULT);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i]<0) {
                    bytes[i] += 256;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bytes;
    }
	 
    public static Bitmap scaleBitmap(Bitmap bm,int newWidth,int newHeight)
    {
        // ԭʼͼ��Ŀ�͸�
        int width = bm.getWidth();
        int height = bm.getHeight();
        
        // ����������
        float scaleWidth = ((float)newWidth) / width;
        float scaleHeight = ((float)newHeight) / height;
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        
        return resizedBitmap;
    }
    
	public static void savePhoto(int uid, Bitmap bmp)
	{		
		//when you need to save the image inside your own folder in the sd card
	    File imageFileFolder = new File( APP_PATH );

        if( !imageFileFolder.exists() )
        {
            imageFileFolder.mkdir();
            Log.i("myevent", "************����һ��Ŀ¼" + APP_PATH);
        }
        
        imageFileFolder = new File(IMAGE_PATH);

		if( !imageFileFolder.exists() )
		{
			imageFileFolder.mkdir();
			Log.i("myevent", "************����һ��Ŀ¼" + IMAGE_PATH);
		}
		
		FileOutputStream out = null;
		File imageFileName = new File(imageFileFolder, uid+".jpg");
		try
		{
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			out = null;
			Log.i("myevent", "************������һ��ͼƬ" + uid);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * precondition:
	 * 		fileExist return true;
	 * @param uid
	 * @return
	 */
	public static Bitmap getLocalBitmapBy(int uid)
	{
		Bitmap bitmap = null;
		
		bitmap = BitmapFactory.decodeFile( IMAGE_PATH + uid + ".jpg" );
	
		return bitmap;
	}
	
	public static boolean fileExist(int uid)
	{
		File file = new File( IMAGE_PATH + uid + ".jpg" );
		return file.exists();
	}
	
//	//map
//	public boolean fileExistInMap(int uid)
//	{
//		return imageMap.containsKey(uid) || fileExist(uid);
//	}
//	
//	public Bitmap getBitmapInMap(int uid)
//	{
//		Bitmap bitmap = null;
//		
//		if( imageMap.containsKey(uid) )	
//		{
//			bitmap = imageMap.get(uid);
//			Log.e("imageUtil", "in map: " + uid);
//		}			
//		else if( fileExist(uid) )
//		{
//			bitmap = getLocalBitmapBy(uid);
//			putBitmapInMap(uid, bitmap);
//			Log.e("imageUtil", "in local: " + uid);
//		}
//		
//		return bitmap;
//	}
//	
//	public void putBitmapInMap(int uid, Bitmap bitmap)
//	{
//		imageMap.put(uid, bitmap);
//	}
	
	//LruCache
	public void addBitmapToMemoryCache(int uid, Bitmap bitmap) {
		//if image not in cache, add to cache
		if( mMemoryCache.get(uid)==null )
		{
			mMemoryCache.put(uid, bitmap);
			Log.e("imageUtil", "put an image: " + uid);
		}
	}

	/**
	 * precondition:
	 * 		imageExistInCache return true;
	 * @param uid
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(int uid) 
	{	
		Log.e("imageUtil", "get an image: " + uid);
		return mMemoryCache.get(uid);
	}
	
	//image in cache or not
	public boolean imageExistInCache( int uid )
	{
		Log.e("imageUtil", "want to get image: " + uid);
		if( mMemoryCache.get(uid)==null ) 
		{
			if( fileExist(uid)  )
			{
				Bitmap bitmap = getLocalBitmapBy(uid);
				if( bitmap!=null ) {
					mMemoryCache.put(uid, bitmap);
					return true;
				}
			}
				
			return false;
		}
		else 
			return true;
	}
	
	public Bitmap getAvatar(int uid)
	{
	    if (imageExistInCache(uid))
	    {
	        return getBitmapFromMemCache(uid);
	    }
	    else {
	        retrieve_avatar(uid);
	        return null;
	    }
	}
	
	private void retrieve_avatar(int uid)
    {
        JSONObject params = new JSONObject();
        try
        {
            params.put("id", uid);
            params.put("operation", 0);
            new HttpSender().Httppost(OperationCode.GET_AVATAR, params, mHandler);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	private void writeAvatar(String str)
    {
        JSONObject returnJson;
        try {
            returnJson = new JSONObject(str );
            if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
            {
                String returnStr = returnJson.getString("avatar");  
                byte[] temp = String2Bytes(returnStr);
                if(temp!=null)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                    savePhoto(returnJson.getInt("id"), bitmap);
                    notifyChanged();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
	
	public void registerHandler(Handler handler, String className)
	{
	    if (className.equals("FriendCenter")) {
	        this.fcHandler = handler;
	    }
	    else if (className.equals("NotificationCenter")) {
	        this.ncHandler = handler;
	    }
	    else if (className.equals("CommentPage")) {
	        this.commentHandler = handler;
	    }
	}
	
	private void notifyChanged()
	{
	    if (null != fcHandler)
	    {
	        Message msg = fcHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR);
	        msg.sendToTarget();
	    }
	    if (null != ncHandler)
	    {
	        Message msg = ncHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR);
            msg.sendToTarget();
	    }
	    if (null != commentHandler)
	    {
	        Message msg = commentHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR);
            msg.sendToTarget();
	    }
	}
	
	public void unregisterHandler(String className) {
	    if (className.equals("FriendCenter")) {
	        fcHandler = null;
	    }
	    else if (className.equals("NotificationCenter")) {
            this.ncHandler = null;
        }
	    else if (className.equals("CommentPage")) {
	        this.commentHandler = null;
	    }
	}
	
	public void changeCacheImage(int uid)
	{
	    if (null != mMemoryCache.get(uid)) {
	        mMemoryCache.put(uid, getLocalBitmapBy(uid));
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
            super.handleMessage(msg);
            switch(msg.what)
            {
            case OperationCode.GET_AVATAR:
                final String final_mes = msg.obj.toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    writeAvatar(final_mes);
                    }
                }).start();
                break;
            default: 
                break;
            }
        }
    }
}
