package com.example.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.example.models.Article;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

/**
 * ��ȡ�����ϵ�xml,������
 * @author jph
 * Date��2014.09.26
 */
public class GetXmlAndParse {
	//private String path="http://trade.500.com/static/public/ssc/xml/newlyopenlist.xml";
	private String path = "http://192.168.1.101/basic/web/index.php/articleapi/index";
	public static final int PARSESUCCWSS=0x2001;
	//private ArrayList<Article> articleList;
	
	private Handler handler;
	public GetXmlAndParse(Handler handler) {
		this.handler=handler;
	}
	/**
	 * ��ȡ�����ϵ�XML
	 */
	public void getXml(){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpURLConnection conn=(HttpURLConnection)new 
							URL(path).openConnection();
					conn.setConnectTimeout(5000);//�������ӳ�ʱ
					conn.setRequestMethod("GET");
					
					if (conn.getResponseCode()==200) {
						InputStream inputStream=conn.getInputStream();
						
						ArrayList<Article> articleList=pullXml(inputStream);
						Log.i("internet", "length:" + articleList.size());
						if (articleList.size()>0) {//������������Ϊ���򽫽����������ݷ��͸�UI�߳�
							Message msg=new Message();
							msg.obj=articleList;
							msg.what=PARSESUCCWSS;
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//�ͷ�������������
			}
		}).start();
	}
	/**
	 * ����Xml,�������װ��
	 * @param inputStream
	 */
	protected ArrayList<Article> pullXml(InputStream inputStream) {		
		ArrayList<Article> articleList = new ArrayList();
		try {
			XmlPullParser pullParser=Xml.newPullParser();
			Article article = null;
			pullParser.setInput(inputStream, "utf-8");
			int eventCode=pullParser.getEventType();
			Log.i("internet", "eventCode:" + eventCode);
			while (eventCode!=XmlPullParser.END_DOCUMENT) { 
				String targetName=pullParser.getName();
				Log.i("internet", "targetName:" + targetName);
				switch (eventCode) {
				case XmlPullParser.START_TAG:					
					if ("row".equals(targetName)) {//����news�Ŀ�ʼ�ڵ�
						article =new Article();
						article.setId(Integer.parseInt(pullParser.getAttributeValue(0)));
						article.setTitle(pullParser.getAttributeValue(1));
						article.setBrief(pullParser.getAttributeValue(2));
						article.setContent(pullParser.getAttributeValue(3));
						articleList.add(article);
					}else if("item".equals(targetName)) {
						article =new Article();
						Log.i("internet", "else");
						//break; 
					}else if("id".equals(targetName)){
						eventCode = pullParser.next();
						article.setId(Integer.parseInt(pullParser.getText()));
						Log.i("internet", "test");
					}
					break;
				case XmlPullParser.END_TAG:
					if ("response".equals(targetName)) {//����news�Ľ����ڵ�
						articleList.add(article);
						Log.i("internet", "endTag");
					} 
					break;
					default:
						eventCode=pullParser.next();
						eventCode = pullParser.next();
						Log.i("internet", "default:" + eventCode);
						break;
				}
				eventCode=pullParser.next();//������һ���ڵ㣨��ʼ�ڵ㣬�����ڵ㣩
				Log.i("internet", "next()");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return articleList;
	}
}