package com.app.ui;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.RegUtils;
import com.app.utils.ReturnCode;

import android.app.Activity;
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
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class loginAndreg extends Activity
{
	//hehehhehe
	private HorizontalScrollView scrollView;
	private ViewGroup parent;
	private int w, h;
	private View login, reg;
	private boolean isLogin = true;
	
	private EditText regEmailET, regUNameET, regPwdET, regPwdAgainET;
	private boolean flag;
	private String regEmail, regName, regPwd;
	private int regGender;
	
	private AutoCompleteTextView loginEmailAC;
	private EditText loginPwdET;
	private String loginEmail, loginPwd, responseSalt;
	private CheckBox savePwdCheckBox;
	private SharedPreferences sp;
	
	private MsgHandler handler;
	private HttpSender sender;
	private int userId;
	private MyBroadcastReceiver broadcastReceiver;
	private Intent serviceIntent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loginregutil);
		 
		handler = new MsgHandler( Looper.myLooper() );
		sender = new HttpSender();
		init();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		if( serviceIntent!=null )
//			stopService(serviceIntent);
		
//		logout();
		
		super.onDestroy();
	}

	private void init()
	{
		scrollView = (HorizontalScrollView)findViewById(R.id.testHorizonScro);
		parent = (ViewGroup)findViewById(R.id.parent);        
		scrollView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
	}
	
	private void setLayoutLogin()
	{
		View top = login.findViewById(R.id.topBlock);
			initLayoutH(top, 0.5);
		View topLogin = login.findViewById(R.id.topLoginBlock);
			initLayoutH(topLogin, 0.47);
		View secondBlock = login.findViewById(R.id.secondBlock);
			initLayoutH(secondBlock, 0.03);
		
		View buttonBlock = login.findViewById(R.id.InfoBlock);
			initLayoutH(buttonBlock, 0.45);	
		View loginBtn = login.findViewById(R.id.loginBtn);
			initLayoutH(loginBtn, 0.08);
			loginBtn.setOnClickListener(clickListener);
			
		loginEmailAC = (AutoCompleteTextView)login.findViewById(R.id.loginEmail);
			initLayoutH(loginEmailAC, 0.1);
		loginPwdET = (EditText)login.findViewById(R.id.loginPwd);
			initLayoutH(loginPwdET, 0.1);
		savePwdCheckBox = (CheckBox)login.findViewById(R.id.loginRemember);	
			initLayoutH(savePwdCheckBox, 0.1);
			
		sp = this.getSharedPreferences("loginInfo", MODE_PRIVATE);
		savePwdCheckBox.setChecked(true);
		loginEmailAC.setThreshold(1);
		loginEmailAC.addTextChangedListener(loginEmailWatcher);
	}
	
	private void setLayoutReg()
	{
		View top = reg.findViewById(R.id.topBlock);
			initLayoutH(top, 0.3);
		View topLogin = reg.findViewById(R.id.topLoginBlock);
			initLayoutH(topLogin, 0.27);
		View secondBlock = reg.findViewById(R.id.secondBlock);
			initLayoutH(secondBlock, 0.03);
		
		View buttonBlock = reg.findViewById(R.id.InfoBlock);
			initLayoutH(buttonBlock, 0.65);	
		View loginBtn = reg.findViewById(R.id.regBtn);
			initLayoutH(loginBtn, 0.08);
			loginBtn.setOnClickListener(clickListener);
			
		regEmailET = (EditText)reg.findViewById(R.id.regEmail);
			initLayoutH(regEmailET, 0.1);
		regUNameET = (EditText)reg.findViewById(R.id.regUName);
			initLayoutH(regUNameET, 0.1);	
		regPwdET = (EditText)reg.findViewById(R.id.regPwd);
			initLayoutH(regPwdET, 0.1);	
		regPwdAgainET = (EditText)reg.findViewById(R.id.regPwdAgain);
			initLayoutH(regPwdAgainET, 0.1);
			
		RadioGroup regGender = (RadioGroup)reg.findViewById(R.id.reg_genderGroup);		
			initLayoutH(regGender, 0.1);
	}
	
	private void initLayoutH(View v, double rate)
	{
		int screenH = getWindowManager().getDefaultDisplay().getHeight();
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
		params.height = (int) (rate * screenH);
		v.setLayoutParams(params);		
	}
	
	private void initMarginTop(View v, double rate) 
	{
		int screenH = getWindowManager().getDefaultDisplay().getHeight();
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
		params.topMargin = (int) (screenH * rate);
		v.setLayoutParams(params);
	}
	
	private OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			// TODO Auto-generated method stub
			parent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			
			LayoutInflater inlfater = LayoutInflater.from(parent.getContext());
	        login = inlfater.inflate(R.layout.login, null);
	        setLayoutLogin();
	        reg = inlfater.inflate(R.layout.register, null);
	        reg.setVisibility(View.GONE);
	        setLayoutReg();

            w = scrollView.getMeasuredWidth();
            h = scrollView.getMeasuredHeight();
            parent.addView(reg, w, h); 
            parent.addView(login, w, h);    
                     		   		
            View toReg =  login.findViewById(R.id.signuphint);
            toReg.setOnClickListener( clickListener );
            View toLogin = reg.findViewById(R.id.loginhint);
            toLogin.setOnClickListener(clickListener);
            
            scrollView.post(new Runnable() {                
                @Override
                public void run() { 
                    scrollView.scrollBy( w, 0); 
                }
            });		
		}
	};
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
			case R.id.signuphint:
				displayOrHide();
				break;
			case R.id.loginhint:
				displayOrHide();
				break;
			case R.id.regBtn:
				register();
				break;
			case R.id.loginBtn:
				login();
				break;
			default:
				break;
			}
		}
	};
	
	private void displayOrHide()
	{
		if( isLogin )
		{
			scrollView.smoothScrollBy(-w, 0);
			login.setVisibility(View.GONE);
			reg.setVisibility(View.VISIBLE);
		}
		else	
		{
			scrollView.smoothScrollBy(w, 0);
			reg.setVisibility(View.GONE);
			login.setVisibility(View.VISIBLE);
		}
		
		isLogin = !isLogin;
	}
	
	//======================================================================================
	private void register()
	{
		String saltStr = null;
	
		regEmail = getRegEmail();
		if( regEmail!=null )
		{
			regName = getRegName();
			if( regName!=null )
			{
				regPwd = getRegPwd();
				if( regPwd!=null)
				{
					regGender = getRegGender();
					saltStr = RegUtils.getRandomString(6);
					
					JSONObject params = new JSONObject();
					try{
						params.put("email", regEmail);
						params.put("name", regName);
						params.put("passwd", RegUtils.Md5(  regPwd + saltStr ));
						params.put("salt", saltStr);
						params.put("gender", regGender);
						
						sender.Httppost(OperationCode.REGISTER, params, handler);
					}
					catch (JSONException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
				}
			}
		}
	}
	
	public CharSequence getColorHint(String str)
	{
		return Html.fromHtml("<font color=#ff0000>"+str+"</font>");
	}
	
	private String getRegEmail()
	{
		Editable emailText = regEmailET.getText();
		String emailStr = emailText.toString();
		String emailRegex="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		if( emailStr.matches(emailRegex))			
			return emailStr;			
		else 
		{
			flag = false;
			regEmailET.setError(getColorHint("请输入正确的邮箱地址"));
			return null;
		}
	}
	
	private String getRegName()
	{
		Editable userNameText = regUNameET.getText();
		String userNameStr = userNameText.toString();
		if( userNameStr.length()>=2 && userNameStr.length()<=10)			
			return userNameStr;
		
		else 
		{
			flag = false;			
			regUNameET.setError(getColorHint("用户名长度为2-10哦"));	
			return null;
		}
	}
	
	private String getRegPwd()
	{
		boolean pwdFlag = true;
		Editable pwdText = regPwdET.getText();
		String pwdStr = pwdText.toString();
		if( pwdStr.length()<5 || pwdStr.length()>15)
		{
			pwdFlag = false;
			flag = false;
			regPwdET.setError(getColorHint("密码长度为5-15哦"));
		}
		
		Editable confirmPwdText = regPwdAgainET.getText();
		String confirmPwdStr = confirmPwdText.toString();
		if( confirmPwdStr.equals(pwdStr))
		{							
			if(pwdFlag)				
				return pwdStr;				
			else
				return null;
		}
		else
		{
			flag = false;
			regPwdAgainET.setError(getColorHint("密码不一致哦"));	
			return null;
		}
	}
	
	private int getRegGender()
	{
		RadioButton manRadio = (RadioButton)reg.findViewById(R.id.reg_genderMan);

		if( manRadio.isChecked())
			return 1;
		else 
			return 0;
	}
	
	//================================================================================================
	private void login()
	{
		loginEmail = loginEmailAC.getText().toString();
		loginPwd = loginPwdET.getText().toString();
		
		getLoginSalt();
	}
	
	private TextWatcher loginEmailWatcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub
			String[] allUserName = new String[sp.getAll().size()];     // 已存在的键值对数目
			allUserName = sp.getAll().keySet().toArray(new String[0]);
			//getAll()返回hash map，由key-value组成
			//keySet()返回 a set of keys
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					loginAndreg.this, 
					android.R.layout.simple_list_item_1,
					allUserName);

			loginEmailAC.setAdapter(adapter);  //设置数据适配器		
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
	
	//================================================================================
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
				case OperationCode.REGISTER:
					 String response = msg.obj.toString();
					 try{
							if (response != "DEFAULT")
							{
								 JSONObject json = new JSONObject(response);
									int cmd = json.getInt("cmd");
									if (ReturnCode.NORMAL_REPLY == cmd)
									{
										//跳转到登录页面
										Log.i("Register", "注册成功");
										
										Intent intent = new Intent();
										intent.setClass(loginAndreg.this, loginAndreg.class);
										startActivity(intent);	
										Toast.makeText(loginAndreg.this, "注册成功", Toast.LENGTH_SHORT).show();	
									}
									else if (ReturnCode.USER_EXIST == cmd)								
										Toast.makeText(loginAndreg.this, "用户已存在", Toast.LENGTH_SHORT).show();	
									else 
										Toast.makeText(loginAndreg.this, "穿越了", Toast.LENGTH_SHORT).show();	
							}
							else
								Toast.makeText(loginAndreg.this, "网络异常", Toast.LENGTH_SHORT).show();
						 } 
						 catch (JSONException e)
						{
						e.printStackTrace();
						}	
					 break;
					 
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
								
								if( ReturnCode.GET_SALT==cmdGetSalt )  //返回盐值
								{									
									responseSalt = responseGetSaltJson.getString("salt");	
									Log.i("Login", "盐值: " + responseSalt);
									//如果responseSalt==null，说明用户不存在; 否则，向服务器请求登录
									if( null != responseSalt )						
										loginTry();	
								}
								else if( ReturnCode.USER_NOT_FOUND==cmdGetSalt)  //用户不存在的话，只是提示，不跳转														
									Toast.makeText(loginAndreg.this, "用户不存在,请先注册", Toast.LENGTH_SHORT).show();							
								else if(cmdGetSalt == ReturnCode.SERVER_FAIL)		    //连接失败	
									Toast.makeText(loginAndreg.this, "连接失败", Toast.LENGTH_SHORT).show();	
								else if(cmdGetSalt == ReturnCode.USER_ALREADY_ONLINE)
									Toast.makeText(loginAndreg.this, "已经在其他手机登陆了,不能重复登陆", Toast.LENGTH_SHORT).show();
								else							                                                    //ELSE
									Toast.makeText(loginAndreg.this, "你已经穿越...", Toast.LENGTH_SHORT).show();							
						}
						else		//	网络异常 return DEFAULT
							Toast.makeText(loginAndreg.this, "网络异常", Toast.LENGTH_SHORT).show();					
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
						
						//登录成功
						if( ReturnCode.LOGIN_SUC == cmdLogin)
						{
							//选择了保存密码，且登录成功时保存密码
							if( savePwdCheckBox.isChecked())									
								sp.edit().putString(loginEmail, loginPwd).commit();																			
							
							//获取用户id
							userId = respLoginJson.getInt("id");																	
							sp.edit().putString("id"+loginEmail, ""+userId);								
							
							Log.i("Login", "Login succeed");
							
							//跳转到UserInterface.java
							serviceIntent = new Intent("HeartbeatService");
							serviceIntent.putExtra("uid", userId);
							startService(serviceIntent);
							
							IntentFilter intentFilter = new IntentFilter();
							intentFilter.addAction("connected");
							broadcastReceiver = new MyBroadcastReceiver();
							loginAndreg.this.registerReceiver( broadcastReceiver, intentFilter);
							
//								Intent intent = new Intent();
//								intent.setClass(loginAndreg.this, UserInterface.class);
//								intent.putExtra("userId", ""+userId);
//								intent.putExtra("email", loginEmail);
//								startActivity(intent);		
						}
						else if (ReturnCode.PASSWD_NOT_CORRECT==cmdLogin)								
							Toast.makeText(loginAndreg.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();								
					} catch (JSONException e) {
						e.printStackTrace();
					}				
					break;
				case OperationCode.LOGOUT:
						Toast.makeText(loginAndreg.this, "logout", Toast.LENGTH_SHORT).show();	
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
			
			if ( "connected".equals(action) ) {
				Log.i("test", "跳转啦");
				Intent intent2 = new Intent();
					intent2.setClass(loginAndreg.this, UserInterface.class);
					intent2.putExtra("userId", userId);
					intent2.putExtra("email", loginEmail);				
				startActivity(intent2);	
				unregisterReceiver( broadcastReceiver);
				loginAndreg.this.finish();
			}
		}
		
	}
	
	public void logout()
	{
		try{
			JSONObject params = new JSONObject();
			params.put("uid", userId);
			
			sender.Httppost(OperationCode.LOGOUT, params, handler);
		}
		catch (JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}