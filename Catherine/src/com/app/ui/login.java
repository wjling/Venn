package com.app.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.RegUtils;
import com.app.utils.ReturnCode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class login extends Activity
{
	private MsgHandler handler;
	private HttpSender sender;
	
	private AutoCompleteTextView loginEmailAC;
	private EditText loginPwdET;
	private String loginEmail, loginPwd, responseSalt;
	private CheckBox savePwdCheckBox;
	private SharedPreferences sp;
	
	private int userId;
	private MyBroadcastReceiver broadcastReceiver;
	private Intent serviceIntent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		 
		handler = new MsgHandler( Looper.myLooper() );
		sender = new HttpSender();

		setLayoutLogin();
	}
	
	private void setLayoutLogin()
	{
		View top = this.findViewById(R.id.topBlock);
			initLayoutH(top, 0.5);
		View topLogin = this.findViewById(R.id.topLoginBlock);
			initLayoutH(topLogin, 0.47);
		View secondBlock = this.findViewById(R.id.secondBlock);
			initLayoutH(secondBlock, 0.03);
		
		View buttonBlock = this.findViewById(R.id.InfoBlock);
			initLayoutH(buttonBlock, 0.45);	
		View loginBtn = this.findViewById(R.id.loginBtn);
			initLayoutH(loginBtn, 0.08);
			loginBtn.setOnClickListener(clickListener);
			
		loginEmailAC = (AutoCompleteTextView)this.findViewById(R.id.loginEmail);
			initLayoutH(loginEmailAC, 0.1);
		loginPwdET = (EditText)this.findViewById(R.id.loginPwd);
			initLayoutH(loginPwdET, 0.1);
		savePwdCheckBox = (CheckBox)this.findViewById(R.id.loginRemember);	
			initLayoutH(savePwdCheckBox, 0.1);
			
		sp = this.getSharedPreferences("loginInfo", MODE_PRIVATE);
		savePwdCheckBox.setChecked(true);
		loginEmailAC.setThreshold(1);
		loginEmailAC.addTextChangedListener(loginEmailWatcher);
		
		 View toReg =  this.findViewById(R.id.signuphint);
         toReg.setOnClickListener( clickListener );
        
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
			case R.id.signuphint:
				Intent intent2 = new Intent();
					intent2.setClass(login.this, register.class);			
				startActivity(intent2);	
				login.this.finish();
				break;
			case R.id.loginBtn:
				ProgressDialog progressDialog = new ProgressDialog(login.this);
				progressDialog.setMessage("��¼��...��ȷ�����Ѿ�����");
				progressDialog.setTitle("���Ժ�");
				progressDialog.show();
				new Thread()
				{
					public void run() {
						tologin();
					}
				}.start();
//				tologin();
				break;
			default:
				break;
			}
		}
	};
	
	private void tologin()
	{
		loginEmail = loginEmailAC.getText().toString();
		loginPwd = loginPwdET.getText().toString();
		
		getLoginSalt();
	}
	
	private void getLoginSalt()
	{
		try{
			JSONObject params = new JSONObject();
			params.put("email", loginEmail);
			params.put("passwd", "");
			params.put("has_salt", false);
			
			sender.Httppost(OperationCode.GET_SALT_VALUE, params, handler);
		}
		catch (JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void loginTry()
	{
		try{
			JSONObject params = new JSONObject();
			params.put("email", loginEmail);
			params.put("passwd", RegUtils.Md5(  loginPwd + responseSalt ));
			params.put("has_salt", true);
			
			sender.Httppost(OperationCode.LOGIN, params, handler);
		}
		catch (JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private TextWatcher loginEmailWatcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub
			String[] allUserName = new String[sp.getAll().size()];     // �Ѵ��ڵļ�ֵ����Ŀ
			allUserName = sp.getAll().keySet().toArray(new String[0]);
			//getAll()����hash map����key-value���
			//keySet()���� a set of keys
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					login.this, 
					android.R.layout.simple_list_item_1,
					allUserName);

			loginEmailAC.setAdapter(adapter);  //��������������		
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
			loginPwdET.setText(
					sp.getString(loginEmailAC.getText().toString(), "")
					);
		}
	};
	
	private void initLayoutH(View v, double rate)
	{
		int screenH = getWindowManager().getDefaultDisplay().getHeight();
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
		params.height = (int) (rate * screenH);
		v.setLayoutParams(params);		
	}
	
	class MsgHandler extends Handler
	{
		public MsgHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{									 
				case OperationCode.GET_SALT_VALUE:
					try {
						String responseGetSalt = null;
						JSONObject responseGetSaltJson;
						int cmdGetSalt;
						
						responseGetSalt = msg.obj.toString();
						if( responseGetSalt != "DEFAULT" )
						{
								responseGetSaltJson = new JSONObject(responseGetSalt);							
								cmdGetSalt = responseGetSaltJson.getInt("cmd");		
								
								if( ReturnCode.GET_SALT==cmdGetSalt )  //������ֵ
								{									
									responseSalt = responseGetSaltJson.getString("salt");	
									Log.i("Login", "��ֵ: " + responseSalt);
									//���responseSalt==null��˵���û�������; ����������������¼
									if( null != responseSalt )						
										loginTry();	
								}
								else if( ReturnCode.USER_NOT_FOUND==cmdGetSalt)  //�û������ڵĻ���ֻ����ʾ������ת														
									Toast.makeText(login.this, "�û�������,����ע��", Toast.LENGTH_SHORT).show();							
								else if(cmdGetSalt == ReturnCode.SERVER_FAIL)		    //����ʧ��	
									Toast.makeText(login.this, "����ʧ��", Toast.LENGTH_SHORT).show();	
								else if(cmdGetSalt == ReturnCode.USER_ALREADY_ONLINE)
									Toast.makeText(login.this, "�Ѿ��������ֻ���½��,�����ظ���½", Toast.LENGTH_SHORT).show();
								else							                                                    //ELSE
									Toast.makeText(login.this, "���Ѿ���Խ...", Toast.LENGTH_SHORT).show();							
						}
						else		//	�����쳣 return DEFAULT
							Toast.makeText(login.this, "�����쳣", Toast.LENGTH_SHORT).show();					
					} catch (JSONException e){
						e.printStackTrace();
					}
					break;
					
				case OperationCode.LOGIN:
					try {
						String responseLogin;
						JSONObject respLoginJson;
						int cmdLogin;
						
						responseLogin = msg.obj.toString();
						respLoginJson = new JSONObject(responseLogin);
						cmdLogin = respLoginJson.getInt("cmd");
						
						//��¼�ɹ�
						if( ReturnCode.LOGIN_SUC == cmdLogin)
						{
							//ѡ���˱������룬�ҵ�¼�ɹ�ʱ��������
							if( savePwdCheckBox.isChecked())									
								sp.edit().putString(loginEmail, loginPwd).commit();																			
							
							//��ȡ�û�id
							userId = respLoginJson.getInt("id");																	
							sp.edit().putString("id"+loginEmail, ""+userId);								
							
							Log.i("Login", "Login succeed");
							
							//��ת��UserInterface.java
							serviceIntent = new Intent("HeartbeatService");
							serviceIntent.putExtra("uid", userId);
							startService(serviceIntent);
							
							IntentFilter intentFilter = new IntentFilter();
							intentFilter.addAction("connected");
							broadcastReceiver = new MyBroadcastReceiver();
							login.this.registerReceiver( broadcastReceiver, intentFilter);					
						}
						else if (ReturnCode.PASSWD_NOT_CORRECT==cmdLogin)								
							Toast.makeText(login.this, "�����������������", Toast.LENGTH_SHORT).show();								
					} catch (JSONException e) {
						e.printStackTrace();
					}				
					break;
				case OperationCode.LOGOUT:
						Toast.makeText(login.this, "logout", Toast.LENGTH_SHORT).show();	
					break;
					default:
						break;
			}
		}
	}
	
	private class MyBroadcastReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			//�������ͷ������ɹ�����ת
			if ( "connected".equals(action) ) {
				Log.i("test", "��ת��");
				Intent intent2 = new Intent();
					intent2.setClass(login.this, UserInterface.class);
					intent2.putExtra("userId", userId);
					intent2.putExtra("email", loginEmail);				
				startActivity(intent2);	
				unregisterReceiver( broadcastReceiver);
				login.this.finish();
			}
		}
		
	}

}
