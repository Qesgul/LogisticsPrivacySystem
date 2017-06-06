package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class SendMessageActivity extends Activity{
	public static final String URL = "http://192.168.1.233:8080/TaxiServlet/login";
	Button sendMessageOk=null;
	
    private Spinner receiverSheng;
    private ArrayAdapter receiverShengadapter;
    
    private Spinner receiverShi;
    private ArrayAdapter receiverShiadapter;
    
    private Spinner receiverXianOrQu;
    private ArrayAdapter receiverXianOrQuadapter;
    
    private Spinner yuanShengspinnear;
    private ArrayAdapter yuanShengadapter;
    
    private Spinner yuanShi;
    private ArrayAdapter yuanShiadapter;
    
    private Spinner yuanXianOrQu;
    private ArrayAdapter yuanXianOrQuadapter;
    
    private String goalSheng="";
	private String yuanSheng="";
	private String goalCity="";
	private String yuanCity="";
	private String goalXian="";
	private String yuanXian="";
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
	static int goalProvincePosition = 0;
	static int yuanProvincePosition = 0;

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private String[] province = new String[] {"江苏省","广东省"};//,"重庆","黑龙江","江苏","山东","浙江","香港","澳门"};
    //地级选项值
    private String[][] city = new String[][] 
            {
                    { "南京市", "徐州市"},
                    { "广州市", "深圳市", "韶关市" // ,"珠海","汕头","佛山","湛江","肇庆","江门","茂名","惠州","梅州",
                    // "汕尾","河源","阳江","清远","东莞","中山","潮州","揭阳","云浮"
                    }
            };
    //县级选项值
    private String[][][] county = new String[][][] 
            {
                    {   //南京
                        {"玄武区","白下区","秦淮区","建邺区","鼓楼区","下关区","浦口区","栖霞区","雨花台区","江宁区",
                        "六合区","溧水县","高淳县"},
                        {"鼓楼区","云龙区","泉山区","九里区","铜山县","邳州市","丰县", "沛县" ,"铜山县","睢宁县" ,"新沂市"}
                    },
                    {    //广东
                        {"海珠区","荔湾区","越秀区","白云区","萝岗区","天河区","黄埔区","花都区","从化市","增城市","番禺区","南沙区"}, //广州
                        {"宝安区","福田区","龙岗区","罗湖区","南山区","盐田区"}, //深圳
                        {"武江区","浈江区","曲江区","乐昌市","南雄市","始兴县","仁化县","翁源县","新丰县","乳源县"}  //韶关
                    }
            };

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendmessage);
		pref= PreferenceManager.getDefaultSharedPreferences(this);
		goalSetSpinner();
		goalStreet=(EditText)findViewById(R.id.receivernoeditText);
		goalYouBian=(EditText)findViewById(R.id.receiverzipcodeeditText);
		goalName=(EditText)findViewById(R.id.receivernameeditText);
		goalPhone=(EditText)findViewById(R.id.receivecalleditText);
		
		senderSetSpinner();

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
		nfc_rec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(SendMessageActivity.this,NfcActivity.class);
				SendMessageActivity.this.startActivity(intent);
			}
		});
		nfc_post.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(SendMessageActivity.this,NfcActivity.class);
				SendMessageActivity.this.startActivity(intent);
			}
		});
	}
	private void goalSetSpinner(){
		//收件人信息；
		receiverSheng = (Spinner) findViewById(R.id.ReceiverSheng);
		receiverShengadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,province);           //设置下拉列表的风格
		receiverSheng.setAdapter(receiverShengadapter);  //添加事件Spinner事件监听
		receiverSheng.setSelection(0,true);

		
		receiverShi=(Spinner)findViewById(R.id.ReceiverShi);
		receiverShiadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,city[0]);
		receiverShi.setAdapter(receiverShiadapter);
		receiverShi.setSelection(0,true);
		
		receiverXianOrQu=(Spinner)findViewById(R.id.ReceiverXianOrQu);
		receiverXianOrQuadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,county[0][0]);
		receiverXianOrQu.setAdapter(receiverXianOrQuadapter);
		receiverXianOrQu.setSelection(0,true);

		
		receiverSheng.setOnItemSelectedListener(new goalShengSpinnerXMLSelectedListener());                   //设置默认值
		
		
//		goalSheng=(EditText)findViewById(R.id.receiverCityeditText);
//		goalCity=(EditText)findViewById(R.id.receiverCountyeditText);
		receiverShi.setOnItemSelectedListener(new goalShiSpinnerXMLSelectedListener());
		
		
	}
	private void senderSetSpinner(){
		
		yuanShengspinnear = (Spinner) findViewById(R.id.SenderSheng);
		yuanShengadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,province);           //设置下拉列表的风格
		yuanShengspinnear.setAdapter(yuanShengadapter);                   //添加事件Spinner事件监听
		yuanShengspinnear.setSelection(0,true);
		
		yuanShi=(Spinner)findViewById(R.id.SenderShi);
		yuanShiadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,city[0]);
		yuanShi.setAdapter(yuanShiadapter);
		yuanShi.setSelection(0,true);

		yuanXianOrQu=(Spinner)findViewById(R.id.SenderXianOrQu);
		yuanXianOrQuadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,county[0][0]);
		yuanXianOrQu.setAdapter(yuanXianOrQuadapter);
		yuanXianOrQu.setSelection(0,true);

		yuanShengspinnear.setOnItemSelectedListener(new yuanShengSpinnerXMLSelectedListener());                   //设置默认值
		
		yuanShi.setOnItemSelectedListener(new yuanShiSpinnerXMLSelectedListener());
	}
	class sendMessageOklistener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new logiInfoTransmission().execute(URL);
		}
	}

    class goalShengSpinnerXMLSelectedListener  implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
        	receiverShiadapter = new ArrayAdapter<String>(
                    SendMessageActivity.this, android.R.layout.simple_spinner_item, city[arg2]);
            // 设置二级下拉列表的选项内容适配器
    		receiverShi.setAdapter(receiverShiadapter);
    		goalProvincePosition = arg2;    //记录当前省级序号，留给下面修改县级适配器时用
        }          
		public void onNothingSelected(AdapterView<?> arg0) {         
		}     
	} 
    class goalShiSpinnerXMLSelectedListener  implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
        	receiverXianOrQuadapter = new ArrayAdapter<String>(SendMessageActivity.this,
                     android.R.layout.simple_spinner_item, county[goalProvincePosition][arg2]);
        	receiverXianOrQu.setAdapter(receiverXianOrQuadapter);
        }          
		public void onNothingSelected(AdapterView<?> arg0) {         
		}     
	} 

    
    
    class yuanShengSpinnerXMLSelectedListener  implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
        	 //position为当前省级选中的值的序号

            //将地级适配器的值改变为city[position]中的值
            yuanShiadapter = new ArrayAdapter<String>(SendMessageActivity.this, android.R.layout.simple_spinner_item, city[arg2]);
            // 设置二级下拉列表的选项内容适配器
            yuanShi.setAdapter(yuanShiadapter);
            yuanProvincePosition = arg2;    //记录当前省级序号，留给下面修改县级适配器时用
        }          
		public void onNothingSelected(AdapterView<?> arg0) {         
		}     
	} 
    class yuanShiSpinnerXMLSelectedListener  implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
        	yuanXianOrQuadapter = new ArrayAdapter<String>(SendMessageActivity.this,
                    android.R.layout.simple_spinner_item, county[yuanProvincePosition][arg2]);
        	yuanXianOrQu.setAdapter(yuanXianOrQuadapter);
        }          
		public void onNothingSelected(AdapterView<?> arg0) {         
		}     
	}
	@Override
	protected void onRestart(){
		super.onRestart();

	}

    
	public class logiInfoTransmission extends AsyncTask<String, Void, String>{
/*		String goallevel1=receiverSheng.getSelectedItem().toString()+receiverShi.getSelectedItem().toString();//收件人之省市
		String goallevel2=receiverXianOrQu.getSelectedItem().toString()+goalStreet.getText().toString()+goalYouBian.getText().toString();
		String goallevel3=goalName.getText().toString()+goalPhone.getText().toString();
		String yuanlevel1=yuanShengspinnear.getSelectedItem().toString()+yuanShi.getSelectedItem().toString();
		String yuanlevel2=yuanXianOrQu.getSelectedItem().toString()+yuanStreet.getText().toString()+yuanYouBian.getText().toString();
		String yuanlevel3=yuanName.getText().toString()+yuanPhone.getText().toString();*/
		String senAndRecinfo = receiverSheng.getSelectedItem().toString()+" "+receiverShi.getSelectedItem().toString()+" "+receiverXianOrQu.getSelectedItem().toString()+" "+goalStreet.getText().toString()+" "+goalYouBian.getText().toString()+" "
				+goalName.getText().toString()+" "+goalPhone.getText().toString()
				+" "+yuanShengspinnear.getSelectedItem().toString()+" "+yuanShi.getSelectedItem().toString()+" "+yuanXianOrQu.getSelectedItem().toString()+" "+yuanStreet.getText().toString()+" "+yuanYouBian.getText().toString()+" "
				+yuanName.getText().toString()+" "+yuanPhone.getText().toString()+" "+othertext.getText().toString()+" ";
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
				Toast.makeText(SendMessageActivity.this, "寄件信息上传成功！", Toast.LENGTH_SHORT).show();
				editor=pref.edit();
				editor.putString("msg1",goalName.getText().toString());
				editor.putString("msg2",goalPhone.getText().toString());
				editor.putString("msg3",yuanName.getText().toString());
				editor.putString("msg4",yuanPhone.getText().toString());
				editor.putString("msg5",othertext.getText().toString());
				editor.apply();
				Intent intent=new Intent();
				intent.putExtra("SenAndRecInfo", res.substring(1));
				intent.setClass(SendMessageActivity.this, ShowQRActivity.class);
				SendMessageActivity.this.startActivity(intent);
			}else if(res.substring(0,1).equals("0")){
				Toast.makeText(SendMessageActivity.this, "寄件信息上传失败！", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(SendMessageActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
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
