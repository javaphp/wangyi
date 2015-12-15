package com.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;
import android.widget.Toast;

public class HttpUtil {

	// Get方式请求
	public static String requestByGet(String path) throws Exception {
		//String path = "http://api.map.baidu.com/place/v2/search?q=%E9%A5%AD%E5%BA%97&region=%E9%AB%98%E5%B7%9E&output=json&ak=bx86TsbMMZY8re9OszjfVlDP";
		// 新建一个URL对象
		URL url = new URL(path);
		// 打开一个HttpURLConnection连接
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		// 设置连接超时时间
		urlConn.setConnectTimeout(5 * 1000);
		// 开始连接
		urlConn.connect();
		// 判断请求是否成功
		if (urlConn.getResponseCode() == 200) {
			// 获取返回的数据
			Log.i("internet", "200");
			return readStream(urlConn.getInputStream());
		} else {
			//Toast.makeText(, "网络连接失败", 3000);
		}
		// 关闭连接
		urlConn.disconnect();
		return "";
	}

	private static String readStream(InputStream inputStream) {
		String jsonString = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len;
		try {
			while((len = inputStream.read(data))!=-1) {
				outputStream.write(data,0,len);
			}
			jsonString = new String(outputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonString;
	}
	
	/**
     * Get image from newwork
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public static byte[] getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == 200){
            return readInputStream(inStream);
        }
        return null;
    }
    
    /**
     * Get data from stream
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
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
