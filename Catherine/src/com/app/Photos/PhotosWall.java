package com.app.Photos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Random;

import com.app.catherine.R;
import com.app.utils.imageUtil;
import com.app.widget.AvatarDialog;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotosWall extends Activity
{
	private Button addPhoto;
	private LinearLayout leftView, rightView;
	private imageUtil forImageUtil  = imageUtil.getInstance();
	private int userId = -1;
	private int event_Id = -1;
	private AvatarDialog pictureDialog;
    public final int CASE_PHOTO = 0;
    public final int CASE_CAMERA = 1;
    private Uri uri;
    private String path;
    private Bitmap bm;
    private int setWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.images_page);
		
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		event_Id = intent.getIntExtra("eventId", -1);
		
		init();
	}
	
	private void init()
	{
		addPhoto = (Button)findViewById( R.id.addPhoto);
		leftView = (LinearLayout)findViewById(R.id.leftView);
		rightView = (LinearLayout)findViewById(R.id.rightView);
		setWidth();
		
		addPhoto.setOnClickListener( clickListener );
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
				 pictureDialog = new AvatarDialog(PhotosWall.this, R.style.avatar_dialog);
				 pictureDialog.setCanceledOnTouchOutside(true);//���õ��Dialog�ⲿ��������ر�Dialog         
	             pictureDialog.show();
	             pictureDialog.setAlbumButtonListener(uploadByPhotosListener);
	             pictureDialog.setCameraButtonListener(uploadByCameraListener);
				break;

			default:
				break;
			}
		}
	};
	
	  OnClickListener uploadByPhotosListener = new OnClickListener() {
	        
	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            pictureDialog.dismiss();
	            Intent intent = new Intent();
	            	intent.setType("image/*");
	            	intent.setAction(Intent.ACTION_GET_CONTENT);
	            startActivityForResult(intent, CASE_PHOTO); 
	        }
	    };
	    
	    OnClickListener uploadByCameraListener = new OnClickListener() {
	        
	        @Override
	        public void onClick(View arg0) {
	            // TODO Auto-generated method stub
	            pictureDialog.dismiss();
	            Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            startActivityForResult(intentCam, CASE_CAMERA);
	        }
	    };
	    
	    public void getPath( Uri uri)
	    {
	        String []proj = { MediaStore.Images.Media.DATA };
	        Cursor cursor = managedQuery(uri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	            cursor.moveToFirst();
	            path = cursor.getString(column_index);              
	    }
	    
	    public void onpicturesetFromPhoto(Intent data)
	    {
	        uri = data.getData();                 
	        getPath(uri);
	        bm = BitmapFactory.decodeFile(path);
	        addAPhoto(bm, "����");
	    }	  
	    
	    public void onpicturesetFromCamera(Intent data)
	    {
	        Bundle extras = data.getExtras();
	        Bitmap camera_bitmap = (Bitmap) extras.get("data");
	        String saveFilePath = "";
	      
	        try {
	            File sdCardDir = Environment.getExternalStorageDirectory();
	            File filePath = new File(sdCardDir.getAbsolutePath() + "/Catherine" );
	            if(!filePath.exists())
	                filePath.mkdirs();
	          
	            saveFilePath = filePath + "/" + filename() + ".png";
	            FileOutputStream baos= new FileOutputStream(saveFilePath);
	            camera_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	            baos.flush();
	            baos.close();
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	        
	        bm = BitmapFactory.decodeFile(saveFilePath);
	        addAPhoto(bm, "���յ�");

	    }
	    
	    public String filename() {
	        Random random = new Random(System.currentTimeMillis());
	        java.util.Date dt = new java.util.Date(System.currentTimeMillis()); 
	        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); 
	        String fileName = fmt.format(dt)+ Math.abs(random.nextInt()) % 100000;      
	        return fileName;
	    }  
	
	private void addAPhoto(Bitmap bm, String photoTextStr)
	{
		LinearLayout child = (LinearLayout)this.getLayoutInflater().inflate(R.layout.images_page_item, null);
		ImageView photo = (ImageView) child.findViewById(R.id.photo);
		TextView photoText = (TextView) child.findViewById(R.id.photoText);
		
		int width = bm.getWidth();
        int height = bm.getHeight();
		bm = forImageUtil.scaleBitmap(bm, setWidth, setWidth*height/width);
		
		photo.setImageBitmap(bm);
		photoText.setText( photoTextStr );
		
		int leftHeight = leftView.getMeasuredHeight();
		int rightHeight = rightView.getMeasuredHeight();
		
		if( rightHeight>=leftHeight )
			leftView.addView( child);
		else
			rightView.addView(child);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
	      // TODO Auto-generated method stub
	      if (requestCode == CASE_PHOTO && resultCode == RESULT_OK && null != data)
	      {	          
                  onpicturesetFromPhoto(data);	        
	      }
	      else if (requestCode == CASE_CAMERA && resultCode == RESULT_OK && null != data)
	      {
                  onpicturesetFromCamera(data);
	      }
	      else if (requestCode == CASE_PHOTO || requestCode == CASE_CAMERA)
	      {
            Toast.makeText(this, "������ѡ��ͷ��", Toast.LENGTH_SHORT).show();
        }
	      
	      super.onActivityResult(requestCode, resultCode, data);
	 }

}
