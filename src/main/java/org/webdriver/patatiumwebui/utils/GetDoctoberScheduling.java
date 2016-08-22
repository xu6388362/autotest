
package org.webdriver.patatiumwebui.utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class GetDoctoberScheduling{

	/**
	 * 获取医生号源
	 * tyle:1-正式环境，0-测试环境 ,
	 * hospitalId:医院ID,
	 * hospitalDepartmentId:科室ID(可选参数-不填就默认查询医院所有的医生)
	 * */
	public static String doPostQueryDoctors(int type, String hospitalId,
											String... hospitalDepartmentId) {
		String url = "";
		String result = "";
		if (type == 1) { // 正式环境
			url = "https://patientapi.hk515.com/PreTreatment/QueryDoctors";
		} else if (type == 0) { // 测试环境
			url = "http://192.168.0.31:1233/PreTreatment/QueryDoctors";
		} else {
			System.out.println("type 的值只能为0 或者 1");
		}

		Map<String, String> para = new HashMap<String, String>();
		para.put("HospitalId", hospitalId);

		if (hospitalDepartmentId.length > 0) {
			para.put("HospitalDepartmentId", hospitalDepartmentId[0]);
		} else {
			para.put("HospitalDepartmentId", "");
		}

		para.put("ProfessionDepartmentId", "");
		para.put("CityId", "");
		para.put("DistrictId", "");
		para.put("IsShowAvailableCount", "True");
		para.put("DoctorType", "2");
		para.put("StartIndex", "0");
		para.put("EndIndex", "100"); // 查询的医生数量限制为100
		para.put("OrderBy", "0");

		String resJson = doPost(url, para);
		// System.out.println(resJson);
		try {
			JSONObject json = new JSONObject(resJson);
			String returndata = json.getString("ReturnData");

			if (returndata == "null") {
				throw new Exception("查找不到医生数据，请查看医院ID是否有效或者医院是否有可挂号医生！");
			}

			JSONArray jsonArray = json.getJSONArray("ReturnData");
			// System.out.println(jsonArray);
			String hospitalName = "";
			String departmentName = "";
			String doctorName = "";

			System.out.println("医生数量为：" + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject user = (JSONObject) jsonArray.get(i);
				hospitalName = user.getString("HospitalName");
				departmentName = user.getString("DepartmentName");
				doctorName = user.getString("DoctorName");

				int count = doPostQueryDoctorSchedulings(type,
						user.get("TicketPoolName").toString(),
						user.get("DoctorId").toString(), user.get("DoctorType")
								.toString());
				if (count != 0) {
					if (type == 1) { // 访问的地址
						url = "http://www.hk515.com/Doctor/"
								+ user.get("DoctorId").toString();
						result += hospitalName + "," + departmentName + ","
								+ doctorName + "," + url + ";";
					} else {
						url = "http://192.168.0.21:8099/Doctor/"
								+ user.get("DoctorId").toString();
						result += hospitalName + "," + departmentName + ","
								+ doctorName + "," + url + ";";
					}

				}

			}

			if(result.length() >= 1){
				result = result.substring(0, result.length() - 1); // 去掉最后的分号
			}

			System.out.println(result);

		} catch (Exception e) {
			System.out.println(" doPostQueryDoctors方法中 解析json错误");
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 查询医院排班及号源情况，返回号源数不为0的医生的访问地址
	 *  type:1-正式环境、0-测试环境,
	 *  ticketPoolName：票池,
	 *  doctorId： 医生ID,
	 *   doctorType,医生类别  1-华康医生 2-挂号医生
	 * */
	public static int doPostQueryDoctorSchedulings(int type,
												   String ticketPoolName, String doctorId, String doctorType) {
		String result = "";
		String url = "";
		int count = 0; // 号源数

		if (type == 1) { // post的地址
			url = "https://patientapi.hk515.com/PreTreatment/QueryDoctorSchedulings";
		} else if (type == 0) {
			url = "http://192.168.0.31:1233/PreTreatment/QueryDoctorSchedulings";
		} else {
			System.out.println("type 的值只能为0 或者 1");
		}

		// post的内容
		Map<String, String> para = new HashMap<String, String>();
		para.put("ticketPoolName", ticketPoolName);
		para.put("doctorId", doctorId);
		para.put("doctorType", doctorType);

		String resJson = doPost(url, para);
		// System.out.println(doctorId + " 医生号源：" + resJson);

		try {

			JSONObject json = new JSONObject(resJson);
			json = json.getJSONObject("ReturnData"); // 拿到二级json内容

			JSONArray jsonArray = json.getJSONArray("Schedulings"); // 从json数组中得到相应java数组
			// System.out.println(jsonArray);

			if (jsonArray.length() == 0) { // 判断有没有排班数据
				System.out.println("医生ID为： " + doctorId + " 排班数为空");
			} else {

				String schedulingDate = "";
				for (int i = 0; i < jsonArray.length(); i++) { // 获取总的号源数据
					JSONObject user = (JSONObject) jsonArray.get(i);
					schedulingDate += user.getString("SchedulingDate") + "、";
					count += (Integer) user.get("AvailableCount");
				}

				if(count ==0 ){ // 可挂号数为0
					result = "医生ID为：" + doctorId + " 在 " + schedulingDate
							+ " 的可挂号数为 " + count;
				}else{
					if (type == 1) { // 访问的地址
						url = "http://www.hk515.com/Doctor/" + doctorId;
					} else {
						url = "http://192.168.0.21:8099/Doctor/" + doctorId;
					}

					result = "医生ID为：" + doctorId + " 在 " + schedulingDate
							+ " 的可挂号数为 " + count + " URL: " + url;
				}

				System.out.println(result);
				return count;

			}
			// System.out.print("");
		} catch (Exception e) {
			System.out.println(" doPostQueryDoctors方法中 解析json错误");
			e.printStackTrace();
		}
		return count;
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

		HttpPost httpRequst = new HttpPost(uriAPI);// 创建HttpPost对象

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (Map.Entry<String, String> entry : para.entrySet()) {
			// System.out.println(entry.getKey()+"--->"+entry.getValue());
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		try {
			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			@SuppressWarnings("resource")
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequst);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
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
	public static void doGet(String url) {

		// 创建HttpClientBuilder
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// HttpClient
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

		HttpGet httpGet = new HttpGet(url);
		System.out.println(httpGet.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
			// 获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			// 响应状态
			System.out.println("status:" + httpResponse.getStatusLine());
			// 判断响应实体是否为空
			if (entity != null) {
				System.out.println("contentEncoding:"
						+ entity.getContentEncoding());
				System.out.println("response content:"
						+ EntityUtils.toString(entity));
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
	}

	public static void main(String args[]) throws Exception {
//		doPostQueryDoctors(1, "601", "");
		doPostQueryDoctors(1,"468680523883999238");
		doPostQueryDoctors(0,"15062317271288185404756759525713187","3179","444","gggg");

	}

}