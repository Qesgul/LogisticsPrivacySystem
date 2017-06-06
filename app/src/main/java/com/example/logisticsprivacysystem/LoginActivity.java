package com.example.logisticsprivacysystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
public class LoginActivity extends Activity {
	public static final String URL = "http://192.168.1.233:8080/TaxiServlet/login";
	Button loginOkOrNot=null;
	Button VisitButton=null;
	EditText loginUserName=null;
	EditText loginPassword=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginOkOrNot=(Button)findViewById(R.id.login_OKButton);
		loginUserName=(EditText)findViewById(R.id.login_usernameeditText);
		loginPassword=(EditText)findViewById(R.id.login_passwordeditText);
		loginOkOrNot.setOnClickListener(new loginOkOrNotListener());
		VisitButton=(Button)findViewById(R.id.VisitButton);
		VisitButton.setOnClickListener(new VisitListener());
	}
	class loginOkOrNotListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new SubmitAsyncTask().execute(URL);
		}
		
	}
	class VisitListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(LoginActivity.this, MainActionActivity.class);
			LoginActivity.this.startActivity(intent);
		}

	}

	public class SubmitAsyncTask extends AsyncTask<String, Void, String>{
		String info = "";
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
				Toast.makeText(LoginActivity.this, "服务器连接错误", Toast.LENGTH_SHORT).show();
			}else{
	//			final GlobalData globalData=(GlobalData)getApplication();
	//			globalData.setUserInfo(res);
				SharedPreferences.Editor editor=getSharedPreferences("power",MODE_PRIVATE).edit();
				editor.putString("res",res);
				editor.apply();
			if(res.substring(0,1).equals("1")){
					Intent intent=new Intent();
					intent.setClass(LoginActivity.this, MainActionActivity.class);
					LoginActivity.this.startActivity(intent);
				}else{
					Toast.makeText(LoginActivity.this, "用户名与密码校验失败", Toast.LENGTH_SHORT).show();
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
			String name =loginUserName.getText().toString().trim();
			String code = loginPassword.getText().toString().trim();            
			paramsList.add(new BasicNameValuePair("ORDER", "0"));
			paramsList.add(new BasicNameValuePair("NAME", name));
			paramsList.add(new BasicNameValuePair("CODE", code));
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
