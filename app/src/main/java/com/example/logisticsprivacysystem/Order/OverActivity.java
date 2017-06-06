package com.example.logisticsprivacysystem.Order;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.logisticsprivacysystem.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axes on 2017/5/17.
 */

public class OverActivity extends Activity{
	public static final String URL = "http://192.168.1.233:8080/TaxiServlet/PostOrder";
	private ListView overlist;
	private orderAdapter orderadapter;
	private List<Order> orderList = new ArrayList<Order>();
	String text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_over);

		new SubmitAsyncTask().execute(URL);
		initOrder();

	}
	private void initOrder() {

	}
	public class SubmitAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			String reps = "";
			reps = doPost(url);
			return reps;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//res 格式为：登陆校验标记（0,1）#等级#是否为寄件员工#私钥#公钥#等
			String res = result.trim();
				if(res.isEmpty()){
					Toast.makeText(OverActivity.this, "服务器连接错误，请检查网络设置", Toast.LENGTH_SHORT).show();
				}else{
					text=res;
					String [] arr;
					arr=text.split("#");
					String s="";
					for(int i=0;i<10;i++)
					{
						String num = arr[i];
						s = String.valueOf(i);
						Order order_item = new Order(s,num);
						orderList.add(order_item);
					}
					orderadapter = new orderAdapter(OverActivity.this, R.layout.order_item, orderList);
					overlist=(ListView)findViewById(R.id.over_list);
					overlist.setAdapter(orderadapter);
					overlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Order order = orderList.get(position);
							Toast.makeText(OverActivity.this, "点击了"+order.getNum(), Toast.LENGTH_SHORT).show();
						}
					});
			}
			super.onPostExecute(result);
		}
	}
	/**
	 * 用Post方式跟服务器传递数据
	 * @param url
	 * @return
	 */
	private String doPost(String url){
		String responseStr = "";
		try {
			HttpPost httpRequest = new HttpPost(url);
			HttpParams params = new BasicHttpParams();
			ConnManagerParams.setTimeout(params, 1000); //从连接池中获取连接的超时时间
			HttpConnectionParams.setConnectionTimeout(params, 3000);//通过网络与服务器建立连接的超时时间
			HttpConnectionParams.setSoTimeout(params, 5000);//读响应数据的超时时间
			httpRequest.setParams(params);
			//下面开始跟服务器传递数据，使用BasicNameValuePair
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("ORDER", "0"));
			UrlEncodedFormEntity mUrlEncodeFormEntity = new UrlEncodedFormEntity(paramsList, HTTP.UTF_8);
			httpRequest.setEntity(mUrlEncodeFormEntity);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			final int ret = httpResponse.getStatusLine().getStatusCode();
			if(ret == HttpStatus.SC_OK){
				responseStr = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			}else{
				responseStr = "-1";
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseStr;
	}

}

