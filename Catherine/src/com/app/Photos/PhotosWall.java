package com.app.Photos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.PhotoMainPage.PhotoMainPage;
import com.app.catherine.R;
import com.app.photoUtils.HttpHelperPlus;
import com.app.photoUtils.KeyFile;
import com.app.photoUtils.SimpleKeyValue;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;
import com.app.widget.AvatarDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotosWall extends Activity
{
	private final String TAG = "PhotosWall";
	private ImageView addPhoto;
	private LinearLayout leftView, rightView;
	private int userId = -1;
	private int event_Id = -1;
	private int sequence = 0;   //ÿ�η���photoIdList��ʱ�򣬻ḽ��һ��sequence��������ȡ��һ��list
	private AvatarDialog pictureDialog;
    public final int CASE_PHOTO = 0;
    public final int CASE_CAMERA = 1;
    private Uri uri;
    private String path;
    private Bitmap bm;
    private int setWidth;
    private MsgHandler mhandler;
	private ProgressDialog progressDialog;
	private JSONArray photoIdList = null;
	private HttpSender sender;
	private int photoIdListIndex = 0;
	public static Drawable clickedPhoto;
	private Bitmap takephotoBM=null;
	private File takephotoFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.images_page);
		
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		event_Id = intent.getIntExtra("eventId", -1);
		mhandler = new MsgHandler( Looper.myLooper());
		sender = new HttpSender();
		
		init();
	}
	
	private void init()
	{
		addPhoto = (ImageView)findViewById( R.id.addPhoto);
		leftView = (LinearLayout)findViewById(R.id.leftView);
		rightView = (LinearLayout)findViewById(R.id.rightView);
		
		setWidth();
		addPhoto.setOnClickListener( clickListener );
		
		getPhotoIdList();
	}
	
	private void getPhotoIdList()
	{
		JSONObject params = new JSONObject();
		
		if( sequence!=-1)
		{
			try {
				params.put("id", userId);
				params.put("event_id", event_Id);
				params.put("sequence", sequence);
				sender.Httppost(OperationCode.GET_PHOTO_LIST, params, mhandler);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
			
	}
	
	private void setWidth()
	{
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		setWidth = (screenWidth - 25)/2;
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)leftView.getLayoutParams();
		params.width = setWidth;
		leftView.setLayoutParams(params);
		
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)rightView.getLayoutParams();
		params2.width = setWidth;
		rightView.setLayoutParams(params2);
		
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.addPhoto:
//				addAPhoto(userId, "nice");
//				 pictureDialog = new AvatarDialog(PhotosWall.this, R.style.avatar_dialog);
//				 pictureDialog.setCanceledOnTouchOutside(true);//���õ��Dialog�ⲿ��������ر�Dialog         
//	             pictureDialog.show();
//	             pictureDialog.setAlbumButtonListener(uploadByPhotosListener);
//	             pictureDialog.setCameraButtonListener(uploadByCameraListener);
				
				Dialog dialog = new AlertDialog.Builder(PhotosWall.this)
					.setTitle("��ѡ��")
					.setPositiveButton("���", uploadByPhotosListener)
					.setNeutralButton("����", uploadByCameraListener)
					.setNegativeButton("ȡ��", null).create();
				dialog.show();
					
				break;

			default:
				break;
			}
		}
	};
	
	private android.content.DialogInterface.OnClickListener uploadByPhotosListener = 
			new android.content.DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
	        	intent.setType("image/*");
	        	intent.setAction(Intent.ACTION_GET_CONTENT);
	        startActivityForResult(intent, CASE_PHOTO); 
		}
	};
	
	private android.content.DialogInterface.OnClickListener uploadByCameraListener = 
			new android.content.DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
//			Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			startActivityForResult(intentCam, CASE_CAMERA);
			
			destoryImage();
			String state = Environment.getExternalStorageState();
			if( state.equals(Environment.MEDIA_MOUNTED))
			{
				try {
		            File sdCardDir = Environment.getExternalStorageDirectory();
		            File filePath = new File(sdCardDir.getAbsolutePath() + "/Catherine" );
		            if(!filePath.exists())
		                filePath.mkdirs();
		          
		            String saveFilePath = filePath + "/" + filename() + ".png";
		            takephotoFile = new File(saveFilePath);
		            takephotoFile.delete();
		            if( !takephotoFile.exists() )
		            {
		            	try
		            	{
		            		takephotoFile.createNewFile();
		            	}
		            	catch(IOException e)
		            	{
		            		e.printStackTrace();
		            		Toast.makeText(PhotosWall.this, "��Ƭ����ʧ��!", Toast.LENGTH_SHORT).show();
		            		return;
		            	}
		            }
		            
		            Intent intentCam = new Intent("android.media.action.IMAGE_CAPTURE");
		            intentCam.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takephotoFile));
		            startActivityForResult(intentCam, CASE_CAMERA);
		            
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
			}
			else
			{
				Toast.makeText(PhotosWall.this, "sdcardû�в���", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private void destoryImage()
	{
		if( takephotoBM!=null)
		{
			takephotoBM.recycle();
			takephotoBM = null;
		}
	}
	
//	private OnClickListener uploadByPhotosListener = new OnClickListener() {
//        
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            pictureDialog.dismiss();
//            Intent intent = new Intent();
//            	intent.setType("image/*");
//            	intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent, CASE_PHOTO); 
//        }
//    };
    
//    private OnClickListener uploadByCameraListener = new OnClickListener() {
//        
//        @Override
//        public void onClick(View arg0) {
//            // TODO Auto-generated method stub
//            pictureDialog.dismiss();
//            Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intentCam, CASE_CAMERA);
//        }
//    };
	    
    /*ͨ��uri��ȡͼƬ·��*/
    public void getPath( Uri uri)
    {
        String []proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);              
    }
    
    /* ����֮�󣬸���Ƭ����*/
    public String filename() {
        Random random = new Random(System.currentTimeMillis());
        java.util.Date dt = new java.util.Date(System.currentTimeMillis()); 
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String fileName = fmt.format(dt)+ Math.abs(random.nextInt()) % 100000;      
        return fileName;
    }
    
    /*������ȡ��ͼƬ֮�󣬻ص�����*/
    public void onpicturesetFromPhoto(Intent data)
    {
        uri = data.getData();                 
        getPath(uri);
      //for function "addAPhoto"
        bm = imageUtil.getInstance().getSmallBitmap(path);     //��ȡС�ߴ�ͼƬ
        bm = imageUtil.getInstance().compressImage(bm);      //����ѹ��
        
        //�ٽ�������ѹ��
        String saveFilePath = "";
        
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            File filePath = new File(sdCardDir.getAbsolutePath() + "/Catherine" );
            if(!filePath.exists())
                filePath.mkdirs();
          
            saveFilePath = filePath + "/" + filename() + ".png";
            FileOutputStream baos= new FileOutputStream(saveFilePath);    		
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baos.flush();
            baos.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        
		SimpleKeyValue []kvs = 
			{ 
				new SimpleKeyValue("id", userId),
				new SimpleKeyValue("event_id", event_Id),
				new SimpleKeyValue("cmd", "upload")
			};
		Log.e(TAG, "id="+userId + " event id = " + event_Id);
		
		KeyFile []kfs =
			{
				new KeyFile("photos", new File(saveFilePath) )
			};
		
//		progressDialog = new ProgressDialog(PhotosWall.this);
//			progressDialog.setMessage("ͼƬ�ϴ���...");
//			progressDialog.setTitle("���Ժ�");
//			progressDialog.show();
		//upload image
		HttpHelperPlus.getInstance().sendRequest(kvs, kfs, OperationCode.UPLOAD_PHOTO, mhandler);
    }	  
    
    /*���ջ�ȡͼƬ֮�󣬻ص�����*/
    public void onpicturesetFromCamera(Intent data)
    {
//        Bundle extras = data.getExtras();
//        Bitmap camera_bitmap = (Bitmap) extras.get("data");     
        
    	if( takephotoFile!=null && takephotoFile.exists() )
    	{
    		String saveFilePath = takephotoFile.getPath();
    		
    		//for function "addAPhoto"
    		bm = imageUtil.getInstance().getSmallBitmap(saveFilePath);     //��ȡС�ߴ�ͼƬ
            bm = imageUtil.getInstance().compressImage(bm);      //����ѹ��              
            
            try {
                FileOutputStream baos= new FileOutputStream(saveFilePath);    		
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
            } catch (Exception e) {
              e.printStackTrace();
            }

        //upload file
        SimpleKeyValue []kvs = 
			{ 
				new SimpleKeyValue("id", userId),
				new SimpleKeyValue("event_id", event_Id),
				new SimpleKeyValue("cmd", "upload")
			};
		Log.e(TAG, "id="+userId + " event id = " + event_Id);
		
		KeyFile []kfs =
			{
				new KeyFile("photos", new File(saveFilePath) )
			};
		
		progressDialog = new ProgressDialog(PhotosWall.this);
			progressDialog.setMessage("ͼƬ�ϴ���...");
			progressDialog.setTitle("���Ժ�");
			progressDialog.show();
		//upload image
		HttpHelperPlus.getInstance().sendRequest(kvs, kfs, OperationCode.UPLOAD_PHOTO, mhandler);
    
    	}
    }
	
    /* �������������������ʾͼƬ*/
	private void addAPhoto(Bitmap bm, String photoTextStr, final int photoId)
	{
		LinearLayout child = (LinearLayout)this.getLayoutInflater().inflate(R.layout.images_page_item, null);
		ImageView photo = (ImageView) child.findViewById(R.id.photo);
		TextView photoText = (TextView) child.findViewById(R.id.photoText);
		
		int width = bm.getWidth();     
		//��ͼƬ���쵽��Ӧwidth
//        if( setWidth<width )
//        {
        	 int height = bm.getHeight();
             int setHeight = setWidth*height/width;     
        	bm = imageUtil.scaleBitmap(bm, setWidth, setHeight);
//        }
		
		photo.setImageBitmap(bm);
		photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickedPhoto = ((ImageView) v).getDrawable();
				Intent intent = new Intent();
				intent.putExtra("userId", userId);
				intent.putExtra("photo_id", photoId);
				intent.setClass(PhotosWall.this, PhotoMainPage.class);
				PhotosWall.this.startActivity(intent);
			}
		});
		
		
//		photoText.setText( photoTextStr );
		
		int leftHeight = leftView.getMeasuredHeight();
		int rightHeight = rightView.getMeasuredHeight();
		
		if( rightHeight>=leftHeight )
			leftView.addView( child);
		else
			rightView.addView(child);
	}
	
	/**�������������ȡ��ͼƬ֮�󣬾ͻ�����������
	 * ��������������ö�Ӧ�Ļص���������õ���ͼƬ
	 */
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) 
	{
	      // TODO Auto-generated method stub
	      if (requestCode == CASE_PHOTO && resultCode == RESULT_OK && null != data)
	      {	          
	    	  progressDialog = new ProgressDialog(PhotosWall.this);
				progressDialog.setMessage("ͼƬ������...");
				progressDialog.setTitle("���Ժ�");
				progressDialog.show();
				
	    	  new Thread()
	    	  {
	    		  public void run()
	    		  {
	    			  onpicturesetFromPhoto(data);	      //ѡ��ص�����  
	    		  }
	    	  }.start();
                  
	      }
	      else if (requestCode == CASE_CAMERA && resultCode == RESULT_OK )
	      {
                  onpicturesetFromCamera(data);		//ѡ��ص�����
	      }
	      else if (requestCode == CASE_PHOTO || requestCode == CASE_CAMERA)
	      {
            Toast.makeText(this, "������ѡ��ͷ��", Toast.LENGTH_SHORT).show();
        }
	      
	      super.onActivityResult(requestCode, resultCode, data);
	 }
	
	class MsgHandler extends Handler
	{
		public MsgHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			String returnStr;
			JSONObject respJson;
			int cmd;
			
			returnStr = msg.obj.toString();		
			if( !"DEFAULT".equals(returnStr))
			{
				switch ( msg.what) {
				case OperationCode.UPLOAD_PHOTO:     //����HttpHelperPlus��������֮�󣬵õ��Ľ��
							progressDialog.dismiss();			
					try {											
							respJson  = new JSONObject(returnStr);
							cmd = respJson.getInt("cmd");
							Log.e(TAG, "cmd="+cmd);
							
							if(ReturnCode.NORMAL_REPLY == cmd )
							{
								//addAPhoto(bm, "�����ϴ���");
//								int photoId = (int)respJson.optLong("photo_id");
								String photoIdStr = respJson.optString("photo_id");
								
								int photoId = Integer.parseInt( photoIdStr.substring(0, photoIdStr.length()-1));
//								int photoId = respJ/son.optLong("photo_id");
								Log.i(TAG, "photoId: "+photoId);								
								addAPhoto(bm, "���ص�", photoId);
								Toast.makeText(PhotosWall.this, "ͼƬ�ϴ��ɹ�", Toast.LENGTH_SHORT).show();
							}
							else
							{
								Toast.makeText(PhotosWall.this, "�ϴ�ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
							}											
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					break;
					
				case OperationCode.GET_PHOTO_LIST:
					try {											
						respJson  = new JSONObject(returnStr);
						cmd = respJson.getInt("cmd");
						
						if(ReturnCode.NORMAL_REPLY == cmd )
						{
							sequence = respJson.getInt("sequence");
							photoIdList = respJson.getJSONArray("photo_list");
							if( photoIdList==null ) break;
							
							int length = photoIdList.length();
							if( length>0 )
							{
								//down first image 
								photoIdListIndex = 0;                             //index initialize
						        SimpleKeyValue []kvs = 
									{ 
										new SimpleKeyValue("photo_id", photoIdList.getInt(photoIdListIndex)),
										new SimpleKeyValue("cmd", "download")
									};
								HttpHelperPlus.getInstance().sendRequest(kvs, OperationCode.DOWNLOAD_PHOTO, mhandler);
							}
							else
							{
								Toast.makeText(PhotosWall.this, "û�и���ͼƬ��", Toast.LENGTH_SHORT).show();
							}
						}
						else
						{
							Toast.makeText(PhotosWall.this, "��ȡͼƬid�б���ֵ�쳣", Toast.LENGTH_SHORT).show();
						}		
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
					break;
					
				case OperationCode.DOWNLOAD_PHOTO:

					byte is[] = (byte[])msg.obj;
					int photoID;
					
					try{
					photoID = photoIdList.getInt(photoIdListIndex);
					
					if( is.length>0)
					{
						Bitmap bm = BitmapFactory.decodeByteArray(is, 0, is.length);
						addAPhoto(bm, "���ص�", photoID);
					}
										
						photoIdListIndex++;   
						
						//download the next image from the server if there are more images
						if( photoIdListIndex < photoIdList.length())
						{
							SimpleKeyValue []kvs = 
								{ 
									new SimpleKeyValue("photo_id", photoIdList.getInt(photoIdListIndex)),
									new SimpleKeyValue("cmd", "download")
								};
							HttpHelperPlus.getInstance().sendRequest(kvs, OperationCode.DOWNLOAD_PHOTO, mhandler);
						}
						//
						else
						{
							getPhotoIdList();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					break;
	
				default:
					break;
				}
			}
			else
			{
				Toast.makeText(PhotosWall.this, "�����쳣", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

}
