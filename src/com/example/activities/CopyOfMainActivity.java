package com.example.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import com.example.adapters.MainActivityListViewAdapter;
import com.example.models.Article;
import com.example.utils.GetAndParseJson;
import com.example.utils.GetXmlAndParse;
import com.example.utils.HttpUtil;
import com.example.utils.MyConstants;
import com.example.wangyi.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.util.Log;
import android.util.Xml;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class CopyOfMainActivity extends Activity implements OnClickListener, OnGestureListener {

	private Gallery gallery;
	private Button mBell, mOptions;
	private ViewFlipper mFlipper;
	private LinearLayout mLinearLayout;
	private ListView mListView;
	private List<Article> articleList = new ArrayList<Article>();
	//private String url = "http://127.0.0.1/basic/web/index.php/articleapis";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		
		initView();
		
		SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		String userName1 = sp.getString("USER_NAME", "NULL");
		Toast.makeText(this, "欢迎：" + userName1, 3000).show();
		
		mBell.setOnClickListener(this);
		mOptions.setOnClickListener(this);
		
		gallery.setAdapter(new GalleryAdapter(this));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				Toast.makeText(CopyOfMainActivity.this, "当前点击的是："+position, 3000).show();
				switch (position) {
				case 1:
					break;

				default:
					break;
				}
			}
		});
		
		final Handler handler =new Handler(){  
            public void handleMessage(android.os.Message msg)  
            {  
            	articleList = new ArrayList<Article>();
            	JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(msg.obj.toString());
					for(int i = 0; i<jsonArray.length(); i++) {
						JSONObject item = jsonArray.getJSONObject(i);
						int id = item.getInt("id");
						String title = item.getString("title");
						String brief = item.getString("abstract");
						String content = item.getString("content");
						String imgUrl = item.getString("img");
						Article article = new Article(id, title, brief, content, imgUrl);
						articleList.add(article);
					}
					initData();
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	
            };  
        };
        
		new Thread() {
			public void run() {
				try {
					String url = MyConstants.BASE_PATH + "articleapis";
					String gift = HttpUtil.requestByGet(url); 
					Message msg = new Message();
					msg.obj = gift; 
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
	}

	protected void initData() {
        List<Article> items = new ArrayList<Article>();
        for (Article article: articleList) {
        	items.add(article);
        }
        MainActivityListViewAdapter adapter = new MainActivityListViewAdapter(
        		this, items);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent =new Intent(CopyOfMainActivity.this,ArticleDetailActivity.class);
			    
			    //用Bundle携带数据
			    Bundle bundle=new Bundle();
			    //传递name参数为tinyphp
			    bundle.putInt("id", articleList.get(position).getId());
			    bundle.putString("imgUrl", articleList.get(position).getImgUrl());
			    intent.putExtras(bundle);
			    
			    startActivity(intent); 
			}
		});
        
    }

	private void initView() {
		//标题栏按钮监听
		mBell =  (Button) findViewById(R.id.btnBell);
		mOptions = (Button) findViewById(R.id.btnOption);
		gallery = (Gallery) findViewById(R.id.gallery);
		mListView = (ListView) findViewById(R.id.lvnews);
		mFlipper = (ViewFlipper) findViewById(R.id.vfFliper);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnBell:
			break;

		case R.id.btnOption:
			Intent intent = new Intent(CopyOfMainActivity.this, TitleOptionActivity.class);
			startActivity(intent);
			break;
		
		default:
			Log.i("internet", "" + view.getId());
			break;
		}
	}
	
	class GalleryAdapter extends BaseAdapter{
	    private Context context;
	    private String[] titles={
	            " 头条"," 娱乐"," 热点"," 体育"," 广州"," 财经"," 科技"," 段子",
	            " 图片"," 汽车"," 时尚"," 轻松一刻"," 军事"
	    };
	    public GalleryAdapter(Context context){
	        this.context = context;
	    }
	    @Override
	    public int getCount() {
	        return titles.length;
	    }
	    @Override
	    public Object getItem(int position) {
	        return position;
	    }
	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View layout = View.inflate(context, R.layout.activity_main_gallery_item, null);
	    	TextView textView = (TextView) layout.findViewById(R.id.title);
	    	textView.setText("　"+titles[position]);
	    	return layout;
	    }
	
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		if (e1.getX() - e2.getX() > 100) {  
            //设置View进入和退出的动画效果  
            this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,  
                    R.anim.left_in));  
            this.mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,  
                    R.anim.left_out));  
            this.mFlipper.showNext();  
            return true;  
        }
        return false;
	} 
	

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
