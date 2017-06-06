package com.example.logisticsprivacysystem;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rsa_encrypt.EncAndDec;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.DistriCheckActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;



public class DistriCheckActivity extends Activity implements Callback{
	private DistriCheckActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button Ok;
	private TextView scan_result;
	private EditText input;
	String value1="1";
	String value2="2";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.distri_check);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view1);
		Ok=(Button)findViewById(R.id.distri_checkok);
		Ok.setOnClickListener(new distriCheckOkLIstener());
		scan_result=(TextView)findViewById(R.id.scan_result);
		input=(EditText)findViewById(R.id.inputcode);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
	class distriCheckOkLIstener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			value2=input.getText().toString();
			if(value2.equals("2")||value2.equals(""))
				Toast.makeText(DistriCheckActivity.this, "请输入提货校验码 ！", Toast.LENGTH_SHORT).show();
			else if(value1.equals("1")||value2.equals(""))
				Toast.makeText(DistriCheckActivity.this, "请扫描二维码获取提货校验码 ！", Toast.LENGTH_SHORT).show();
			else{
				if(value1.equals(value2)){
					// TODO Auto-generated method stub
					AlertDialog.Builder builder = new Builder(DistriCheckActivity.this);
					builder.setMessage("提货校验码校验成功！");
					builder.setTitle("提示");
					builder.setPositiveButton("确认",new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
						finish();
						arg0.dismiss();
					}});

					builder.create().show();
				}
				else
					Toast.makeText(DistriCheckActivity.this, "提货校验码校验失败 ！", Toast.LENGTH_SHORT).show();
			}

		}
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view1);
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
		
/*		//quit the scan view
		cancelScanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CaptureActivity.this.finish();
			}
		});*/
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
			Toast.makeText(DistriCheckActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
			SharedPreferences pref=getSharedPreferences("power",MODE_PRIVATE);
			String userauthri=pref.getString("res","");
			String []arr=userauthri.split("#"); //arr数组存储私钥
			String []mingwen=resultString.split("#@%");//mingwen数组存储存储密文
			EncAndDec encAndDec=new EncAndDec();
			String Num=encAndDec.startDecrypt(arr[arr.length-2],mingwen[0]);
			if(arr[1].equals("1")||arr[1].equals("2")){
				Toast.makeText(DistriCheckActivity.this, "您员工权限无法配件!", Toast.LENGTH_SHORT).show();

			}else{
				String mingwen1=encAndDec.startDecrypt(arr[3], mingwen[3]);
				value1=Num.substring(Num.length()-3)+mingwen1.substring(mingwen1.length()-3);
				scan_result.setText("已获得提货校验码");
			}
			
/*			final GlobalData globalData=(GlobalData)getApplication();
			String userauthri=globalData.getUserInfo();
			String []arr=userauthri.split("#"); //arr数组存储私钥
			String []mingwen=resultString.split("#@%");//mingwen数组存储存储密文
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
				scanResult.setText(mingwen1);
			}*/
			/*Toast.makeText(DistriCheckActivity.this, resultString, Toast.LENGTH_SHORT).show();

			System.out.println("Result:"+resultString);
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			resultIntent.putExtras(bundle);
			this.setResult(RESULT_OK, resultIntent);*/
		}
//		DistriCheckActivity.this.finish();
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
			handler = new DistriCheckActivityHandler(this, decodeFormats,characterSet);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

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
	/*public class SubmitAsyncTask extends AsyncTask<String, Void, String>{
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
				Toast.makeText(CaptureActivity.this, "物流记录已更新", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	*//**
	 * 用Post方式跟服务器传递数据
	 * @param url
	 * @return
	 *//*
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
	}*/
	
}

