package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.logisticsprivacysystem.Address.NfcActivity;

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
 * Created by Axes on 2017/5/6.
 */

public class RecandPosActivity extends Activity{
	public static final String URL = "http://192.168.1.233:8080/TaxiServlet/login";
	Button sendMessageOk=null;


	private String goalSheng="";
	private String yuanSheng="";
	private String goalXian="";
	private String yuanXian="";
	private EditText goalCity=null;
	private EditText yuanCity=null;
	private EditText goalqu=null;
	private EditText yuanqu=null;
	private EditText goalStreet=null;
	private EditText yuanStreet=null;
	private EditText goalYouBian=null;
	private EditText yuanYouBian=null;
	private EditText goalName=null;
	private EditText yuanName=null;
	private EditText goalPhone=null;
	private EditText yuanPhone=null;
	private EditText othertext=null;
	private ImageButton nfc_rec;
	private ImageButton nfc_post;
	private int symbol=0;
	private String dizhi;
	private String ming;



	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ceshi);

		goalCity=(EditText)findViewById(R.id.ReceiverSheng);
		goalqu=(EditText)findViewById(R.id.ReceiverXianOrQu);
		goalStreet=(EditText)findViewById(R.id.receivernoeditText);
		goalYouBian=(EditText)findViewById(R.id.receiverzipcodeeditText);
		goalName=(EditText)findViewById(R.id.receivernameeditText);
		goalPhone=(EditText)findViewById(R.id.receivecalleditText);

		yuanCity=(EditText)findViewById(R.id.SenderSheng);
		yuanqu=(EditText)findViewById(R.id.SenderXianOrQu);
		yuanStreet=(EditText)findViewById(R.id.sendernoeditText);
		yuanYouBian=(EditText)findViewById(R.id.senderzipcodeeditText);
		yuanName=(EditText)findViewById(R.id.sendernameeditText);
		yuanPhone=(EditText)findViewById(R.id.sendercalleditText);
		//后台响应事件
		othertext=(EditText)findViewById(R.id.otherText);
		sendMessageOk=(Button)findViewById(R.id.SendMsgOk);
		sendMessageOk.setOnClickListener(new sendMessageOklistener());
		nfc_rec=(ImageButton)findViewById(R.id.nfc_rec);
		nfc_post=(ImageButton)findViewById(R.id.nfc_pos);
		nfc_rec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				symbol=1;
				Intent intent=new Intent();
				intent.setClass(RecandPosActivity.this,NfcActivity.class);
				startActivityForResult(intent, 1000);
			}
		});
		nfc_post.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				symbol=2;
				Intent intent=new Intent();
				intent.setClass(RecandPosActivity.this,NfcActivity.class);
				startActivityForResult(intent, 1000);
			}
		});


	}
	class sendMessageOklistener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new logiInfoTransmission().execute(URL);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1000 && resultCode == 1001)
		{
			dizhi = data.getStringExtra("dizhi");
			ming = data.getStringExtra("ming");
			switch (symbol){
				case 0:
					break;
				case 1:
					goalCity.setText(dizhi.substring(0,3));
					goalqu.setText(dizhi.substring(3,6));
					goalName.setText(ming);
					goalStreet.setText(dizhi.substring(6));
					break;
				case 2:
					yuanCity.setText(dizhi.substring(0,3));
					yuanqu.setText(dizhi.substring(3,6));
					yuanName.setText(ming);
					yuanStreet.setText(dizhi.substring(6));
					break;
			}
		}
	}

	public class logiInfoTransmission extends AsyncTask<String, Void, String> {

		String senAndRecinfo = goalCity.getText().toString()+"\n"+"   "+"\n"+goalqu.getText().toString()+"\n"+goalStreet.getText().toString()+"\n"+goalYouBian.getText().toString()+"\n"
				+goalName.getText().toString()+"\n"+goalPhone.getText().toString()
				+"\n"+yuanCity.getText().toString()+"\n"+"   "+"\n"+yuanqu.getText().toString()+"\n"+yuanStreet.getText().toString()+"\n"+yuanYouBian.getText().toString()+"\n"
				+yuanName.getText().toString()+"\n"+yuanPhone.getText().toString()+"\n"+othertext.getText().toString()+"\n";
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
				Toast.makeText(RecandPosActivity.this, "寄件信息上传成功！", Toast.LENGTH_SHORT).show();
				String showmsg=goalName.getText().toString()+"#"+goalPhone.getText().toString()+"#"+yuanName.getText().toString()+"#"+yuanPhone.getText().toString()+"#"+othertext.getText().toString();
				Intent intent=new Intent();
				intent.putExtra("showmsg",showmsg);
				intent.putExtra("SenAndRecInfo", res.substring(1));
				intent.setClass(RecandPosActivity.this, ShowQRActivity.class);
				RecandPosActivity.this.startActivity(intent);
			}else if(res.substring(0,1).equals("0")){
				Toast.makeText(RecandPosActivity.this, "寄件信息上传失败！", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(RecandPosActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
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
			paramsList.add(new BasicNameValuePair("ORDER", "1"));                //order 1为传输寄件信息数据
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
