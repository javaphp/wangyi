package com.example.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.models.Article;
import com.example.models.User;
import com.example.utils.HttpUtil;
import com.example.utils.MyConstants;
import com.example.wangyi.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText mEtUserName, mEtPassword;
	private TextView mTvUserName;
	private Button mBtnLogin;
	private SharedPreferences sp;
	
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.login_titlebar);
		
		initView();
		
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		final Handler handler =new Handler(){  
            public void handleMessage(android.os.Message msg)  
            {  
            	JSONArray jsonArray;
				try {
					//jsonArray = new JSONArray(msg.obj.toString());
					if(msg.what == MyConstants.INTERNET_RESPONSE_NULL) {
						//Toast.makeText(this, "用户名不存在", 3000).show();
						mEtPassword.setError("用户名或密码不正确");
						Log.i("internet", "userNull:");
					} else {
						JSONObject userObject = new JSONObject(msg.obj.toString());
						int id = userObject.getInt("id");
						String userName = userObject.getString("name");
						String password = userObject.getString("password");
						
						Editor editor = sp.edit();
						editor.putString("USER_NAME", userName);
						editor.putString("PASSWORD", password);
						editor.commit();
						User user = new User(id, userName, password);
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	
            };  
        };
		
		mBtnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				new Thread() {
					public void run() {
						try {
							String userName = mEtUserName.getText().toString();
							String password = mEtPassword.getText().toString();
							String url = MyConstants.BASE_PATH + "userapi/getuser?userName=" + 
										userName + "&password=" + password;
							String jsonString  = HttpUtil.requestByGet(url); 
							Message msg = new Message();
							if(jsonString.length() == 0) {
								msg.what = MyConstants.INTERNET_RESPONSE_NULL;
								
							} else {
								msg.what = MyConstants.INTERNET_RESPONSE_NOT_NULL;
							}
							msg.obj = jsonString; 
							handler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				
			}
		});
		
	}

	private void initView() {
		mEtUserName = (EditText) findViewById(R.id.etUserName);
		mEtPassword = (EditText) findViewById(R.id.etPassword);
		mTvUserName = (TextView) findViewById(R.id.tvUserName);
		mBtnLogin = (Button) findViewById(R.id.btnLogin);
	}
	
	
	
}
