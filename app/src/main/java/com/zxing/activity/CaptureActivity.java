package com.zxing.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logisticsprivacysystem.R;
import com.example.logisticsprivacysystem.RemindPackageActivity;
import com.example.rsa_encrypt.EncAndDec;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

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
import java.util.Vector;
/**
 * Initial the camera
 * @author Ryan.Tang
 */
@SuppressLint("NewApi")
public class CaptureActivity extends Activity implements Callback {
	public static final String URL = "http://192.168.1.233:8080/TaxiServlet/login";

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button cancelScanButton;
	private Button distriButton;
	private TextView scanResult;
	String company="";                                         //所属转运中心
	String Num="";                                             //单号解码
	String street="";                                          //街道、姓名、电话
	String name="";
	String phone="";
	String level="";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
		distriButton=(Button)findViewById(R.id.distri_ok_button);
		distriButton.setOnClickListener(new distriListener());
		scanResult=(TextView)findViewById(R.id.scan_result_textView);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
	
	class distriListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new Builder(CaptureActivity.this);
			builder.setMessage("确定要配送么至：\r\n"+scanResult.getText());
			builder.setTitle("提示");
			builder.setPositiveButton("确认",new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
				new SubmitAsyncTask().execute(URL);
				arg0.dismiss();
			}});
			builder.setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			});
			builder.create().show();
		}
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
		//quit the scan view
		cancelScanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CaptureActivity.this.finish();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * Handler scan result
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		//FIXME
		if (resultString.equals("")||resultString.equals(null)) {
			Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
//			final GlobalData globalData=(GlobalData)getApplication();
//			String userauthri=globalData.getUserInfo();
			SharedPreferences pref=getSharedPreferences("power",MODE_PRIVATE);
			String userauthri=pref.getString("res","");
			String []arr=userauthri.split("#"); //arr数组存储私钥
			String []mingwen=resultString.split("#@%");//mingwen数组存储存储密文
			level=arr[1];
/*			long []time=new long[100];
			 for(int i=0;i<100;i++){
		        	long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
		        	EncAndDec encAndDec=new EncAndDec();
					Num=encAndDec.startDecrypt(arr[arr.length-2],mingwen[0]);
					String mingwen1=encAndDec.startDecrypt(arr[3], mingwen[1]);
		    		long endMili=System.currentTimeMillis();
		    		time[i]=endMili-startMili;
		        }
		        for(int i=0;i<100;i++){
		    		System.out.println("解密总耗时为："+time[i]+"毫秒");
		        }*/
			EncAndDec encAndDec=new EncAndDec();
			Num=encAndDec.startDecrypt(arr[arr.length-2],mingwen[0]);
//			Num=encAndDec.startDecrypt(arr[arr.length-2], mingwen[0]);
			company=arr[arr.length-1];
			if(arr[1].equals("1")){
				String mingwen1=encAndDec.startDecrypt(arr[3], mingwen[1]);
				scanResult.setText(mingwen1);
//				scanResult.setText(resultString);
			}else if(arr[1].equals("2")){
				String mingwen1=encAndDec.startDecrypt(arr[3], mingwen[2]);
				scanResult.setText(mingwen1);
			}else{
				String mingwen1=encAndDec.startDecrypt(arr[3], mingwen[3]);
				String str[]=mingwen1.split(";");
				street=str[0];
				name=str[1].substring(0, 1)+"先生/女士";
				phone=str[2].substring(str[2].length()-4);
				scanResult.setText("街道地址："+street+"\r\n"+getResources().getString(R.string.Name)+name+"\r\n"+"手机尾号："+phone);
			}
//			Toast.makeText(CaptureActivity.this, resultString, Toast.LENGTH_SHORT).show();
/*
//			System.out.println("Result:"+resultString);
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			resultIntent.putExtras(bundle);
			this.setResult(RESULT_OK, resultIntent);*/
		}
//		CaptureActivity.this.finish();
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
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
			if(res.isEmpty()){
				Toast.makeText(CaptureActivity.this, "服务器连接错误，请检查网络设置", Toast.LENGTH_SHORT).show();
			}else{
				if(level.equals("3")){
					Toast.makeText(CaptureActivity.this, "物流记录已更新", Toast.LENGTH_SHORT).show();
					Intent intent=new Intent();
					intent.putExtra("Street", street);
					intent.putExtra("Name", name);
					intent.putExtra("Phone", phone);
					intent.setClass(CaptureActivity.this, RemindPackageActivity.class);
					CaptureActivity.this.startActivity(intent);
				}else{
					Toast.makeText(CaptureActivity.this, "物流记录已更新", Toast.LENGTH_SHORT).show();
                    finish();
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
			paramsList.add(new BasicNameValuePair("ORDER", "2"));             //命令3为更新物流记录
			paramsList.add(new BasicNameValuePair("DanHao", Num));
			paramsList.add(new BasicNameValuePair("COMPANY", company));
			paramsList.add(new BasicNameValuePair("LEVEL", level));
			if(level.equals("3")){
				paramsList.add(new BasicNameValuePair("Phone", phone));
			}
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