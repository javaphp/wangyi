package com.example.activities;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.models.Article;
import com.example.utils.GetAndParseJson;
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
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ArticleDetailActivity extends Activity implements OnClickListener {
	private int articleId;
	private Article article;
	private TextView tvTitle, tvContent;
	private EditText etContent;
	private Button btnBack, btnReply, btnLoginOption;
	private ImageView ivImg;
	private Bitmap bitmap;
	private String imgUrl;
	
	private final static int IMG_FLAG = 0;
	private final static int STRING_FLAG = 1;
	
	private IWXAPI api;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_article_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.reply_titlebar);
		
		initView();
		btnBack.setOnClickListener(this);
		btnReply.setOnClickListener(this);
		btnLoginOption.setOnClickListener(this);
		
		// 微信注册初始化  
	    api = WXAPIFactory.createWXAPI(this, "wx5ce2ffa100e3f587", true);  
	    api.registerApp("wx5ce2ffa100e3f587");  
		
		//新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        articleId = bundle.getInt("id");
        imgUrl = bundle.getString("imgUrl");
        Log.i("internet", "articleIdActivity" + articleId);
        
        final Handler handler =new Handler(){  
            public void handleMessage(android.os.Message msg)  
            {  
            	JSONArray jsonArray;
            	switch (msg.what) {
				case STRING_FLAG:
					try {
						//jsonArray = new JSONArray(msg.obj.toString());
						JSONObject item = new JSONObject(msg.obj.toString());
						//Log.i("internet", "item:" + item);
						int id = item.getInt("id");
						String title = item.getString("title");
						String brief = item.getString("abstract");
						String content = item.getString("content");
						imgUrl = item.getString("img");
						article = new Article(id, title, brief, content, imgUrl);
						tvTitle.setText(title);
						tvContent.setText(content);
						//etContent.setText(content);
						//etContent.setText(title + "\n" + content);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
					
				case IMG_FLAG:
					ivImg.setImageBitmap(bitmap);
					break;
					
				default:
					break;
				}
				
            }

        };
        
		new Thread() {
			public void run() {
				try {
					String url = MyConstants.BASE_PATH + "articleapi/getmodel?id=" + articleId;
					String jsonString  = HttpUtil.requestByGet(url);
					
					Message msg = new Message();
					msg.what = STRING_FLAG;
					msg.obj = jsonString;
					
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
        
		
		new Thread() {
			public void run() {
				try {
					String fullImgUrl = MyConstants.IMG_BASE_PATH + imgUrl;
					byte[] data = HttpUtil.getImage(fullImgUrl);
                    if(data != null) {
                    	bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    }
                    
                    Message msg = new Message();
					msg.what = IMG_FLAG;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvContent = (TextView) findViewById(R.id.tvContent);
		ivImg = (ImageView)findViewById(R.id.ivImg);
		//etContent = (EditText) findViewById(R.id.etContent);
		
		btnBack = (Button) findViewById(R.id.btnBack);
		btnReply = (Button) findViewById(R.id.btnReply);
		btnLoginOption = (Button) findViewById(R.id.btnLoginOption);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnBack:
			finish();
			break;
			
		case R.id.btnReply:
			Intent intent = new Intent(this, ReplyListActivity.class);
			//用Bundle携带数据
		    Bundle bundle=new Bundle();
		    bundle.putInt("articleId", articleId);
		    intent.putExtras(bundle);
		    startActivity(intent);   
			break;
			
		case R.id.btnLoginOption:
			Log.i("internet", "btnLoginOption");
			showLoginDialog();
			break;

		default:
			break;
		}
	}
	
	

	private void showLoginDialog() { 
		AlertDialog.Builder builder = new Builder(this);
		String[] items = {"登陆", "分享到朋友圈"};
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int which) {
				switch (which) {
				case 0:
					//Toast.makeText(this, "登陆", 3000);
					Log.i("internet", "登陆");
					Intent intent = new Intent(ArticleDetailActivity.this, LoginActivity.class);
					startActivity(intent);
					break;

				case 1:
					Log.i("internet", "分享到");
					share2weixin(SendMessageToWX.Req.WXSceneSession);
					break;
					
				default:
					break;
				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
			
	} 
	
	private void share2weixin(int flag) {  
	    // Bitmap bmp = BitmapFactory.decodeResource(getResources(),  
	    // R.drawable.weixin_share);  
	  
	    if (!api.isWXAppInstalled()) {  
	        Toast.makeText(ArticleDetailActivity.this, "您还未安装微信客户端",  
	                Toast.LENGTH_SHORT).show();  
	        return;  
	    }  
	  
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = "http://baidu.com";  
	    WXMediaMessage msg = new WXMediaMessage(webpage);  
	  
	    msg.title = "title";  
	    msg.description = "description"; 
	    Bitmap thumb = BitmapFactory.decodeResource(getResources(),  
	            R.drawable.weixin);  
	    msg.setThumbImage(thumb);  
	    SendMessageToWX.Req req = new SendMessageToWX.Req();  
	    req.transaction = String.valueOf(System.currentTimeMillis());  
	    req.message = msg;  
	    req.scene = flag;  
	    api.sendReq(req);  
	} 
	
}
