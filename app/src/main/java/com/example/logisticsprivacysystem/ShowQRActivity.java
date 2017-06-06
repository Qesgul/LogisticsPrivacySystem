package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Utils.DataCut;
import com.example.Utils.EncodingHandler;
import com.example.rsa_encrypt.EncAndDec;

import java.io.File;
import java.io.FileOutputStream;


public class ShowQRActivity extends Activity {
	private Dialog dialog;
	private ImageView qrImgImageView;
	private ImageView mqrImgImageView;
	private Bitmap qrCodeBitmap;
	private TextView msg1;
	private TextView msg2;
	private TextView msg3;
	private TextView msg4;
	private TextView msg5;
	private TextView danhaoshow;
	ImageButton camera;
	private String danhao="";
	private String Name = "jizhi.jpg";
	private String Filepath = "/sdcard/Qesgul/";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showqr);

		msg1 = (TextView) findViewById(R.id.textView2);
		msg2 = (TextView) findViewById(R.id.textView3);
		msg3 = (TextView) findViewById(R.id.textView10);
		msg4 = (TextView) findViewById(R.id.textView11);
		msg5 = (TextView) findViewById(R.id.textView6);
		danhaoshow=(TextView)findViewById(R.id.danhaoshow);

		camera=(ImageButton)findViewById(R.id.camera);
		camera.setOnClickListener(new cameraListener());


		qrImgImageView = (ImageView) this.findViewById(R.id.showqr_imageView);
		Intent intent = getIntent();
		String senAndRecInfo = intent.getStringExtra("SenAndRecInfo");
		String showmsg=intent.getStringExtra("showmsg");

		DataCut dataCut = new DataCut();
		String[] strlevel1 = dataCut.OridataCut(senAndRecInfo);
		String[] msglevel1 = dataCut.OridataCut(showmsg);

		msg1.setText(msglevel1[0]);
		msg2.setText(msglevel1[1]);
		msg3.setText(msglevel1[2]);
		msg4.setText(msglevel1[3]);
		msg5.setText(msglevel1[4]);

		danhao=strlevel1[0];
		danhaoshow.setText(danhao);
		EncAndDec encAndDec = new EncAndDec();
		SharedPreferences pref=getSharedPreferences("power",MODE_PRIVATE);
		String userauthri=pref.getString("res","");
		String[] arr = userauthri.split("#");

		String miwen0 = encAndDec.startEncryption(arr[6], strlevel1[0]);
		String miwen1 = encAndDec.startEncryption(arr[4], strlevel1[1]);
		String miwen2 = encAndDec.startEncryption(arr[5], strlevel1[2]);
		String miwen3 = encAndDec.startEncryption(arr[6], strlevel1[3]);

		try {
			qrCodeBitmap = EncodingHandler.createQRCode(miwen0 + "#@%" + miwen1 + "#@%" + miwen2 + "#@%" + miwen3, 500);
			qrImgImageView.setImageBitmap(qrCodeBitmap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Button back = (Button) findViewById(R.id.backButton);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShowQRActivity.this, MainActionActivity.class);
				startActivity(intent);
			}
		});
		qrImgImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				init();
				dialog.show();
			}
		});

	}
	class cameraListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(ShowQRActivity.this,CameraActivity.class);
			intent.putExtra("danhao", danhao);
			ShowQRActivity.this.startActivity(intent);
		}

	}
    private void init() {
	    dialog = new Dialog(ShowQRActivity.this, R.style.AlertDialog_AppCompat_Light_);
	    mqrImgImageView = getImageView();
	    dialog.setContentView(mqrImgImageView);

	//大图的点击事件（点击让他消失）
	mqrImgImageView.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog.dismiss();
		}
	});

	//大图的长按监听
	mqrImgImageView.setOnLongClickListener(new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			//弹出的“保存图片”的Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(ShowQRActivity.this);
			builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GetandSaveCurrentImage();
				}
			});
			builder.show();
			return true;
		}
	});
}


	private void GetandSaveCurrentImage() {
		mqrImgImageView.setDrawingCacheEnabled(true);
		final Bitmap Bmp = Bitmap.createBitmap(mqrImgImageView.getDrawingCache());  //获取到Bitmap的图片
		mqrImgImageView.setDrawingCacheEnabled(false);
		String SavePath = getSDCardPath() + "/Qesgul/order_num";
		// 3.保存Bitmap
		try {
			File path = new File(SavePath);
			// 文件
			String filepath = SavePath +"/"+danhao+ ".png";
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				Toast.makeText(this, "截屏文件已保存成功",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取SDCard的目录路径功能
	 */
	private String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}


	//动态的ImageView
	private ImageView getImageView(){
		ImageView iv = new ImageView(this);
		//宽高
		iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//设置Padding
		iv.setPadding(100,100,100,100);
		//imageView设置图片
		iv.setImageBitmap(qrCodeBitmap);
		return iv;
	}

}
