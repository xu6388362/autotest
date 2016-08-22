package org.webdriver.patatiumwebui.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class HttpRequest {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}
	/**
	 * 实现POST的请求
	 * url:请求的url
	 * para:post的请求内容
	 * return 响应的内容
	 * */
	public static String doPost(String url, Map<String, String> para) {
		String uriAPI = url;// Post方式没有参数在这里
		String result = "";
		// 创建HttpPost请求对象
		HttpPost httpRequst = new HttpPost(uriAPI);
		//建立一个NameValuePair（简单名称值对节点类型） list，用于存储欲传送的参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//变量map 键值对
		for (Map.Entry<String, String> entry : para.entrySet()) {
			// System.out.println(entry.getKey()+"--->"+entry.getValue());
			//将map键值对转换成简单名称节点类型的post 请求参数
			NameValuePair nameValuePair=new BasicNameValuePair(entry.getKey(), entry.getValue());
			//将转换过的map存储到欲传输参数列表
			params.add(nameValuePair);
		}

		try {
			//将NameValuePair格式的请求参数转换为UrlEncodedFormEntity
			UrlEncodedFormEntity urlEncodedFormEntity=new UrlEncodedFormEntity(params, HTTP.UTF_8);
			//设置http请求实体
			httpRequst.setEntity(urlEncodedFormEntity);
			@SuppressWarnings({ "resource" })
			//创建http响应对象，执行http post请求
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
			//判断是否响应成功
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//获取http响应实体
				HttpEntity httpEntity = httpResponse.getEntity();
				// 从响应实体取出应答字符串
				result = EntityUtils.toString(httpEntity);
			}
		} catch (Exception e) {
			System.out.println("doPost 发送post请求出错！");
			e.printStackTrace();
		}
		// System.out.println(result);
		return result;
	}

	/**
	 * 实现GET的请求
	 * url:请求的url
	 * */
	public static String doGet(String url) {
		String result="";
		// 创建HttpClientBuilder
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// HttpClient
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		//创建httpget请求
		HttpGet httpGet = new HttpGet(url);
		System.out.println(httpGet.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
			// 获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			// 响应状态
			System.out.println("status:" + httpResponse.getStatusLine());
			if (httpResponse.getStatusLine().getStatusCode()==200) {
				//获取http响应实体
				HttpEntity httpEntity = httpResponse.getEntity();
				// 从响应实体取出应答字符串
				result = EntityUtils.toString(httpEntity);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { // 关闭流并释放资源
				closeableHttpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return  result;
	}

}
