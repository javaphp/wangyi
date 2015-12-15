package com.example.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapters.MainActivityListViewAdapter.ListItemView;
import com.example.models.Article;
import com.example.models.Reply;
import com.example.models.User;
import com.example.utils.HttpUtil;
import com.example.utils.MyConstants;
import com.example.wangyi.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReplyListActivity extends Activity {
	private Button btnReplyBack;
	private ListView mListView;
	public TextView tvUser, tvReplyContent;
	private List<Reply> replyList;
	private int articleId;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_reply_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.detailtitlebar);
		
		initView();
		Bundle bundle = this.getIntent().getExtras();
        articleId = bundle.getInt("articleId");
		
		btnReplyBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		final Handler handler =new Handler(){  
            public void handleMessage(android.os.Message msg)  
            {  
            	replyList = new ArrayList<Reply>();
            	JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(msg.obj.toString());
					for(int i = 0; i<jsonArray.length(); i++) {
						JSONObject item = jsonArray.getJSONObject(i);
						int id = item.getInt("id");
						int userId = item.getInt("user_id");
						int articleId = item.getInt("article_id");
						User user = new User(id, "admin", "admin");
						String replyContent = item.getString("reply_content");
						Reply reply = new Reply(id, user, articleId, replyContent);
						replyList.add(reply);
					}
					initData();
	            	Log.i("internet", "length:" + replyList.size());
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	
            }
        };
       
        new Thread() {
			public void run() {
				try {
					String url = MyConstants.BASE_PATH + "replyapi/getreplies?articleId=" + articleId;
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
	
	private void initData() {
		ReplyListViewAdapter rlvaAdapter = new ReplyListViewAdapter(this, replyList);
		mListView.setAdapter(rlvaAdapter);
	}; 

	private void initView() {
		btnReplyBack = (Button) findViewById(R.id.btnReplyBack);
		mListView = (ListView) findViewById(R.id.lvReplies);
	}
	
	class ReplyListViewAdapter extends BaseAdapter {
		private Context context;
		private List<Reply> replyList;
		private LayoutInflater listContainer;
		
		public ReplyListViewAdapter(Context context, List<Reply> replyList) {
			super();
			this.context = context;
			this.replyList = replyList;
			this.listContainer = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return replyList.size();
		}

		@Override
		public Reply getItem(int position) {
			return replyList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder viewHolder;
			if(convertView == null) {
				viewHolder = new ViewHolder();
	            convertView = listContainer.inflate(R.layout.reply_list_item, null);
	            viewHolder.tvUser = (TextView) convertView.findViewById(R.id.tvUser);
	            viewHolder.tvReplyContent = (TextView) convertView.findViewById(R.id.tvReplyContent);
	            convertView.setTag(viewHolder);
	             
	            viewHolder.tvUser.setText(replyList.get(position).getUser().getName());
	            viewHolder.tvReplyContent.setText(replyList.get(position).getReplyContent());
	            
	        } else {
				viewHolder =  (ViewHolder) convertView.getTag();
			}
	        return convertView; 
		}
		class ViewHolder{ 
	        public TextView tvUser, tvReplyContent;         
		} 
	}
	
	
}
