package com.example.logisticsprivacysystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

@SuppressLint("NewApi")
public class CheckActivity extends Activity{
	public static final String URL = "http://192.168.1.233:8080/TaxiServlet/login";
	private Button checkOk=null;
	private Button ewmbtn=null;
	private TextView showResult=null;
	private EditText input=null;
	private EditText inputCheckCode=null;
	private static final int REQUESTCODE = 1;   //返回的结果码

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkinfo);
		checkOk=(Button)findViewById(R.id.okcheck);
		input=(EditText)findViewById(R.id.input_no);
		inputCheckCode=(EditText)findViewById(R.id.input_checkcode);
		showResult=(TextView)findViewById(R.id.showresult);
		checkOk.setOnClickListener(new checkOnClickListener());
		ewmbtn=(Button)findViewById(R.id.ewmbtn);
		ewmbtn.setOnClickListener(new ewmOnClickListener());

	}
	class checkOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new SubmitAsyncTask().execute(URL);
		}
	}
	class ewmOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(CheckActivity.this,SaomiaoActivity.class);
			//启动意图
			startActivityForResult(intent, REQUESTCODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==2){
			if(requestCode==REQUESTCODE){
				String Num=data.getStringExtra("Num");
				String pick_num=data.getStringExtra("pick_num");
				input.setText(Num);
				inputCheckCode.setText(pick_num);
			}
		}
	}
	public class SubmitAsyncTask extends AsyncTask<String, Void, String>{
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
			if(input.getText().toString().isEmpty()){
				Toast.makeText(CheckActivity.this, "请输入单号！", Toast.LENGTH_SHORT).show();
			}else{
				if(res.isEmpty()){
					Toast.makeText(CheckActivity.this, "服务器连接错误，请检查网络设置", Toast.LENGTH_SHORT).show();
				}else{
					String []arr=res.split("#");
					String s="";
					for(int i=0;i<arr.length;i++){
						s=s+arr[i]+"\r\n";
					}
					showResult.setText(s);
				}
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
			String danhao =input.getText().toString().trim();
			String checkcode=inputCheckCode.getText().toString().trim();
			paramsList.add(new BasicNameValuePair("ORDER", "3"));             //命令2为验证查询单号
			paramsList.add(new BasicNameValuePair("DanHao", danhao));
			paramsList.add(new BasicNameValuePair("CheckCode", checkcode));

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
