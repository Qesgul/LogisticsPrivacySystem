package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcB;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.otg.idcard.OTGReadCardAPI;

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

import static com.example.logisticsprivacysystem.Address.NfcActivity.MESSAGE_VALID_NFCBUTTON;

/**
 * Created by Axes on 2017/5/14.
 */

public class SureActivity extends Activity {
	public static final String URL = "http://192.168.1.233:8080/TaxiServlet/getUser";

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private String order;
	private TextView Readingtext;
	private NfcAdapter mAdapter;
	private OTGReadCardAPI ReadCardAPI;
	private PendingIntent pi = null;
	//滤掉组件无法响应和处理的Intent
	private IntentFilter tagDetected = null;
	private String[][] mTechLists;
	private Intent inintent=null;
	private int readflag=1;

	private EditText sure_name;
	private EditText sure_phone;
	private EditText sure_address;
	private EditText sure_ID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_self);
		pref = PreferenceManager.getDefaultSharedPreferences(this);

		sure_name=(EditText)findViewById(R.id.sure_name);
		sure_phone=(EditText)findViewById(R.id.sure_phone);
		sure_address=(EditText)findViewById(R.id.sure_address);
		sure_ID=(EditText)findViewById(R.id.sure_ID);

		Readingtext = (TextView) findViewById(R.id.Readingtext);
		Readingtext.setVisibility(View.GONE);
		Readingtext.setText("      请放置身份证.");
		Readingtext.setTextColor(Color.RED);

		ReadCardAPI=new OTGReadCardAPI(SureActivity.this);
		mAdapter = NfcAdapter.getDefaultAdapter(SureActivity.this);
		if (mAdapter!=null) {init_NFC();}

		sure_name.setText(pref.getString("username",""));
		sure_phone.setText(pref.getString("userphone",""));
		sure_address.setText(pref.getString("useraddress",""));
		if(sure_name!=null){sure_ID.setText("已通过验证");}


		final Button surebtn=(Button)findViewById(R.id.surebtn);
		surebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String btntext=surebtn.getText().toString();
				if(btntext.equals("验证")){
					Readingtext.setVisibility(View.VISIBLE);
					readflag=0;
					surebtn.setText("保存");
					sure_name.setFocusable(true);
					sure_phone.setFocusable(true);
					sure_address.setFocusable(true);
					sure_ID.setFocusable(true);
				}else {
					new logiInfoTransmission().execute(URL);
					readflag=1;
					editor = pref.edit();
					editor.putString("username",sure_name.getText().toString());
					editor.putString("userphone",sure_phone.getText().toString());
					editor.putString("useraddress",sure_address.getText().toString());
					editor.apply();
					sure_ID.setText("已通过验证");
					Readingtext.setVisibility(View.GONE);
					surebtn.setText("验证");
					sure_name.setFocusable(false);
					sure_phone.setFocusable(false);
					sure_address.setFocusable(false);
					sure_ID.setFocusable(false);
				}
			}
		});
	}
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (readflag==1)
		{
			return;
		}
		inintent=intent;
		readflag=1;
		ReadCardAPI.writeFile("come into onNewIntent 2");
		Readingtext.setText("      正在读卡，请稍候...");
		mHandler.sendEmptyMessageDelayed(MESSAGE_VALID_NFCBUTTON, 0);
	}
	@Override
	public void onPause() {
		super.onPause();
		if (mAdapter!=null)	stopNFC_Listener();
	}
	@Override
	protected void onResume() {
		super.onResume();
		ReadCardAPI.writeFile("come into onResume 1");
		if (mAdapter != null) startNFC_Listener();
		ReadCardAPI.writeFile("come into onResume 2");
		ReadCardAPI.writeFile("pass onNewIntent 1.111111 action=" + getIntent().getAction());
	}

	private void startNFC_Listener() {
		mAdapter.enableForegroundDispatch(this, pi, new IntentFilter[] { tagDetected }, mTechLists);
	}

	private void stopNFC_Listener() {
		mAdapter.disableForegroundDispatch(this);
	}

	private void init_NFC() {
		pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);//.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		mTechLists = new String[][] { new String[] { NfcB.class.getName() } };
	}

	private void initAddress() {

	}

	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int tt;
			switch (msg.what) {
				case MESSAGE_VALID_NFCBUTTON:
					ReadCardAPI.writeFile("come into MESSAGE_CLEAR_ITEMS 1");
					tt=ReadCardAPI.NfcReadCard(inintent);
//					tt=testReadCardAPI.ReadCard();
					ReadCardAPI.writeFile("come into MESSAGE_CLEAR_ITEMS 2");
					Log.e("For Test"," ReadCard TT="+tt);
					if (tt==2)
					{
						new AlertDialog.Builder(SureActivity.this)
								.setTitle("提示" ).setMessage("接收数据超时！" )
								.setPositiveButton("确定" ,  null ).show();
//						onredo.setEnabled(true);
//						onredo.setFocusable(true);
//						onredo.setBackgroundResource(R.drawable.sfz_dq);
					}
					if (tt==41)
					{
						new AlertDialog.Builder(SureActivity.this)
								.setTitle("提示" ).setMessage("读卡失败！" )
								.setPositiveButton("确定" ,  null ).show();
					}
					if (tt==42)
					{
						new AlertDialog.Builder(SureActivity.this)
								.setTitle("提示" ).setMessage("没有找到服务器！" )
								.setPositiveButton("确定" ,  null ).show();
					}
					if (tt==43)
					{
						new AlertDialog.Builder(SureActivity.this)
								.setTitle("提示" ).setMessage("服务器忙！" )
								.setPositiveButton("确定" ,  null ).show();
					}
					if (tt==90)
					{
						Readingtext.setText("      读卡成功");
						sure_name.setText(ReadCardAPI.Name());
						sure_address.setText(ReadCardAPI.Address());
						sure_ID.setText(ReadCardAPI.CardNo());
						ReadCardAPI.release();
					}
					break;
			}
		}
	};
	public class logiInfoTransmission extends AsyncTask<String, Void, String> {
		String senAndRecinfo = "admin1"+"\n"+sure_name.getText().toString()+"\n"+sure_phone.getText().toString()+"\n"+sure_address.getText().toString()+"\n"+sure_ID.getText().toString()+"\n";
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			String reps = "";
			reps = doPost(url,senAndRecinfo);
			return reps;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			String res = result.trim();
			if(res.substring(0,1).equals("1")){
				Toast.makeText(SureActivity.this, "用户信息上传成功！", Toast.LENGTH_SHORT).show();
			}else if(res.substring(0,1).equals("0")){
				Toast.makeText(SureActivity.this, "用户信息上传失败！", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(SureActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	private String doPost(String url,String OddNumber){
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
			String Information =OddNumber.trim();
			paramsList.add(new BasicNameValuePair("INFORMATION", Information));
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

