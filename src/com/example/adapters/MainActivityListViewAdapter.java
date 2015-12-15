package com.example.adapters;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.models.Article;
import com.example.utils.HttpUtil;
import com.example.utils.MyConstants;
import com.example.wangyi.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivityListViewAdapter extends BaseAdapter {
	
	private Context context;
	private List<Article> articleList;
	private LayoutInflater listContainer; 
	private ImageView imageView;
	private Bitmap bitmap;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			imageView.setImageBitmap((Bitmap) msg.obj);
		};
	};
	
	public final class ListItemView{                //自定义控件集合     
        public ImageView thumb;     
        public TextView title;     
        public TextView brief;         
	}  
	
	public MainActivityListViewAdapter() {
		super();
	}

	public MainActivityListViewAdapter(Context context,
			List<Article> articleList) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context);
		this.articleList = articleList;
	}

	@Override
	public int getCount() {
		return articleList.size();
	}

	@Override
	public Article getItem(int position) {
		return articleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ListItemView listItemView = null;
        if (convertView == null) { 
        	listItemView = new ListItemView();
            convertView = listContainer.inflate(R.layout.list_item, null);
            
            listItemView.thumb = (ImageView) convertView.findViewById(R.id.ivThumb);
            listItemView.title = (TextView) convertView.findViewById(R.id.tvTitle);
            listItemView.brief = (TextView) convertView.findViewById(R.id.tvBrief);
            
            
            convertView.setTag(listItemView);
            
        } else {
			listItemView = (ListItemView) convertView.getTag();
		}
        Article article = articleList.get(position);
        //加载新闻图片
        loadArticleImg(article.getImgUrl(), listItemView.thumb);
        //listItemView.thumb.setImageBitmap(bitmap);
        listItemView.title.setText(article.getTitle());
        listItemView.brief.setText(article.getBrief());
        
        return convertView; 
	}

	private void loadArticleImg(final String imgUrl, final ImageView thumb) {
		new Thread() {
			public void run() {
				try {
					String fullUrl = MyConstants.IMG_BASE_PATH + imgUrl;
					byte[] data = getImage(fullUrl);
                    if(data != null) {
                    	bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    } 
					
					imageView = thumb;
					Message msg = new Message();
					msg.obj = bitmap; 
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/**
     * Get image from newwork
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public byte[] getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == 200){
            return readStream(inStream);
        }
        return null;
    }
    
    /**
     * Get data from stream
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

}
