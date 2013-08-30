package com.app.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;

import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.RegUtils;
import com.app.utils.ReturnCode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class register extends Activity
{
	private EditText regEmailET, regUNameET, regPwdET, regPwdAgainET;
	private boolean flag;
	private String regEmail, regName, regPwd;
	private int regGender;
	
	private MsgHandler handler;
	private HttpSender sender;
	private int userId;
	private Intent serviceIntent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		 
		handler = new MsgHandler( Looper.myLooper() );
		sender = new HttpSender();

		setLayoutReg();
	}
	
	private void setLayoutReg()
	{
		View top = this.findViewById(R.id.topBlock);
			initLayoutH(top, 0.3);
		View topLogin = this.findViewById(R.id.topLoginBlock);
			initLayoutH(topLogin, 0.27);
		View secondBlock = this.findViewById(R.id.secondBlock);
			initLayoutH(secondBlock, 0.03);
		
		View buttonBlock = this.findViewById(R.id.InfoBlock);
			initLayoutH(buttonBlock, 0.65);	
		View loginBtn = this.findViewById(R.id.regBtn);
			initLayoutH(loginBtn, 0.08);
			loginBtn.setOnClickListener(clickListener);
			
		regEmailET = (EditText)this.findViewById(R.id.regEmail);
			initLayoutH(regEmailET, 0.1);
		regUNameET = (EditText)this.findViewById(R.id.regUName);
			initLayoutH(regUNameET, 0.1);	
		regPwdET = (EditText)this.findViewById(R.id.regPwd);
			initLayoutH(regPwdET, 0.1);	
		regPwdAgainET = (EditText)this.findViewById(R.id.regPwdAgain);
			initLayoutH(regPwdAgainET, 0.1);
			
		RadioGroup regGender = (RadioGroup)this.findViewById(R.id.reg_genderGroup);		
			initLayoutH(regGender, 0.1);
			
			 View toLogin = this.findViewById(R.id.loginhint);
	         toLogin.setOnClickListener(clickListener);
	}
	
	private void initLayoutH(View v, double rate)
	{
		int screenH = getWindowManager().getDefaultDisplay().getHeight();
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
		params.height = (int) (rate * screenH);
		v.setLayoutParams(params);		
	}
	
private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
			case R.id.loginhint:
				Intent intent2 = new Intent();
				intent2.setClass(register.this, login.class);			
			startActivity(intent2);	
			register.this.finish();
				break;
			case R.id.regBtn:
				toregister();
				break;
			default:
				break;
			}
		}
	};
	
	private void toregister()
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
			regEmailET.setError(getColorHint("��������ȷ�������ַ"));
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
			regUNameET.setError(getColorHint("�û�������Ϊ2-10Ŷ"));	
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
			regPwdET.setError(getColorHint("���볤��Ϊ5-15Ŷ"));
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
			regPwdAgainET.setError(getColorHint("���벻һ��Ŷ"));	
			return null;
		}
	}
	
	private int getRegGender()
	{
		RadioButton manRadio = (RadioButton)this.findViewById(R.id.reg_genderMan);

		if( manRadio.isChecked())
			return 1;
		else 
			return 0;
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
				case OperationCode.REGISTER:
					 String response = msg.obj.toString();
					 try{
							if (response != "DEFAULT")
							{
								 JSONObject json = new JSONObject(response);
									int cmd = json.getInt("cmd");
									if (ReturnCode.NORMAL_REPLY == cmd)
									{
										//��ת����¼ҳ��
										Log.i("Register", "ע��ɹ�");
										
										Intent intent = new Intent();
										intent.setClass(register.this, login.class);
										startActivity(intent);	
										finish();
										Toast.makeText(register.this, "ע��ɹ�", Toast.LENGTH_SHORT).show();	
									}
									else if (ReturnCode.USER_EXIST == cmd)								
										Toast.makeText(register.this, "�û��Ѵ���", Toast.LENGTH_SHORT).show();	
									else 
										Toast.makeText(register.this, "��Խ��", Toast.LENGTH_SHORT).show();	
							}
							else
								Toast.makeText(register.this, "�����쳣", Toast.LENGTH_SHORT).show();
						 } 
						 catch (JSONException e)
						{
						e.printStackTrace();
						}	
					 break;
					 
					default:
						break;
			}
		}
	}

}
