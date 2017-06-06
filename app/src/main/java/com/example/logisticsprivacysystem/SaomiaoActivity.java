package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rsa_encrypt.EncAndDec;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.InactivityTimer;
import com.zxing.decoding.SaomiaoActivityHandler;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

import static com.example.logisticsprivacysystem.R.id.checkbtn;

/**
 * Created by Axes on 2017/4/23.
 */

public class SaomiaoActivity extends Activity implements Callback {
	private SaomiaoActivityHandler handler;
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
	private Button CheckButton;
	private TextView scanResult;
	String company="";                                         //所属转运中心
	String Num="";                                             //单号解码
	String street="";                                          //街道、姓名、电话
	String name="";
	String phone="";
	String level="";
	String pick_num="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saomiao);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
		scanResult=(TextView)findViewById(R.id.scan_result_textView);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		CheckButton=(Button)findViewById(checkbtn);
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
		cancelScanButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SaomiaoActivity.this.finish();
			}
		});
		CheckButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("Num",Num);
				intent.putExtra("pick_num",pick_num);
				setResult(2,intent);
				finish();//结束当前Acitvity
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
			Toast.makeText(SaomiaoActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
	//		final GlobalData globalData=(GlobalData)getApplication();
	//		String userauthri=globalData.getUserInfo();
			SharedPreferences pref=getSharedPreferences("power",MODE_PRIVATE);
			String userauthri=pref.getString("res","");
			String []arr=userauthri.split("#"); //arr数组存储私钥
			String []mingwen=resultString.split("#@%");//mingwen数组存储存储密文
			level=arr[1];
			EncAndDec encAndDec=new EncAndDec();
			Num=encAndDec.startDecrypt(arr[arr.length-2],mingwen[0]);
			company=arr[arr.length-1];
				String mingwen1=encAndDec.startDecrypt(arr[4], mingwen[3]);
				String str[]=mingwen1.split(";");
				street=str[0];
				name=str[1].substring(0, 1)+"先生/女士";
				phone=str[2].substring(str[2].length()-4);
				pick_num=str[3];
			scanResult.setText(getResources().getString(R.string.Name)+name+"\r\n"+"单号:"+Num+"\r\n"+"校验码："+pick_num);
		}
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
			handler = new SaomiaoActivityHandler(this, decodeFormats,
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
	private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}
