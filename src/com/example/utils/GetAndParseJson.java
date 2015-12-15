package com.example.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.models.Article;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**

* 获取并解析网络上的Json

* @author jph

* Date:2014.09.26

*/
public class GetAndParseJson {

  private String url = "http://127.0.0.1/basic/web/index.php/articleapi/index";
  
  public static final int PARSESUCCWSS=0x2001;

  private Handler handler;

  public GetAndParseJson(Handler handler) {
      this.handler=handler;
  }

  /**

   * 获取网络上的Json
   */
  public void getJsonFromInternet () {
      new Thread(new Runnable() {     
          @Override
          public void run() {
              try {
                  HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
                  conn.setConnectTimeout(5000);
                  conn.setRequestMethod("GET");
                  Log.i("internet", "getJsonFromInternet");
                  Log.i("internet", "getAndParseJson"+conn.getResponseCode());
                  //if (conn.getResponseCode()==200) {
                      InputStream inputStream=conn.getInputStream(); 
                      Log.i("internet", "getJsonFromInternet2");
                      List<Article> listArticles = parseJson(inputStream);

                      if (listArticles.size()>0) {
                    	  Log.i("internet", "articleList:" + listArticles.size());
                          Message msg=new Message();
                          msg.what=PARSESUCCWSS;//通知UI线程Json解析完成
                          msg.obj=listArticles;//将解析出的数据传递给UI线程
                          handler.sendMessage(msg);
                      }

                  //}

              } catch (Exception e) {
                  e.printStackTrace();

              }              

          }

      }).start();
  }

  /**

   * 解析json格式的输入流转换成List
   * @param inputStream
   * @return List

   */
  protected List<Article> parseJson(InputStream inputStream) {
	  Log.i("internet", "parseJson2");
      List<Article> listArticles=new ArrayList<Article>();
      String json=convertIsToByteArray(inputStream);
      //String json=new String(jsonBytes);
      try {
          JSONArray jsonArray=new JSONArray(json);
          for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jObject=jsonArray.getJSONObject(i);

              int id=jObject.getInt("id");

              String title=jObject.getString("title");  
              String brief=jObject.getString("abstract");  
              String content=jObject.getString("content"); 
              String imgUrl = jObject.getString("img");

              Article article=new Article(id, title, brief, content, imgUrl);
              listArticles.add(article);
              Log.i("internet", "i:" + i);
          }
      } catch (JSONException e) {
          e.printStackTrace();

      }

      return listArticles;

  }

  /**

   * 将输入流转化成ByteArray

   * @param inputStream

   * @return ByteArray

   */

  private String convertIsToByteArray(InputStream inputStream) {
      ByteArrayOutputStream baos=new ByteArrayOutputStream();

      byte[] buffer=new byte[1024];

      int length=0;

      try {

          while ((length=inputStream.read(buffer))!=-1) {

              baos.write(buffer, 0, length);             

          }

          //inputStream.close();

          //baos.flush();

      } catch (IOException e) {
          e.printStackTrace();
      }
      return new String(baos.toByteArray());

  }

}

