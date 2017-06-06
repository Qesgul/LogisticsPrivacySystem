package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Axes on 2017/4/9.
 */

public class CameraActivity extends Activity{
	private static final int PHOTO_CAPTURE = 0x11;
	private static String photoPath;
	private static String photoName;
	private static String danhao;
	Uri imageUri = Uri.fromFile(new File(Environment
			.getExternalStorageDirectory(), "image.jpg"));
	private Button photo, sc_photo;
	private ImageView img_photo;

	private String newName;
	private String uploadFile;
	private String actionUrl = "http://192.168.1.233:8080/TaxiServlet/get";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_post);
		Intent intent = getIntent();
		danhao = intent.getStringExtra("danhao");
		photo = (Button) findViewById(R.id.photo);
		sc_photo = (Button) findViewById(R.id.sc_photo);
		sc_photo.setOnClickListener(new sc_photo());
		img_photo = (ImageView) findViewById(R.id.imt_photo);
		// android.os.NetworkOnMainThreadException
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());
		photo.setOnClickListener(new photo());
	}

	class sc_photo implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			dialog();
		}

	}

	class photo implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Date date = new Date();
			SimpleDateFormat formatter0 = new SimpleDateFormat("yyyyMMdd");
			String pathNumber=formatter0.format(date);
			photoPath="/sdcard/Qesgul/"+pathNumber+"/";
			photoName=photoPath +danhao+ ".jpg";
			newName=danhao+ ".jpg";
			uploadFile=photoPath+newName;
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			File file = new File(photoPath);
			if (!file.exists()) { // 检查图片存放的文件夹是否存在
				file.mkdir();// 不存在的话 创建文件夹
				System.out.print("创建文件夹");
			}
			File photo = new File(photoName);
			imageUri = Uri.fromFile(photo);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
			startActivityForResult(intent, PHOTO_CAPTURE);

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String sdStatus = Environment.getExternalStorageState();
		switch (requestCode) {
			case PHOTO_CAPTURE:
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Log.i("内存卡错误", "请检查您的内存卡");
				} else {
					BitmapFactory.Options op = new BitmapFactory.Options();
					// 设置图片的大小
					Bitmap bitMap = BitmapFactory.decodeFile(photoName);
					int width = bitMap.getWidth();
					int height = bitMap.getHeight();
					// 设置想要的大小
					int newWidth = 480;
					int newHeight = 640;
					// 计算缩放比例
					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;
					// 取得想要缩放的matrix参数
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					// 得到新的图片
					bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
							matrix, true);
					// canvas.drawBitmap(bitMap, 0, 0, paint)
					// 防止内存溢出
					op.inSampleSize = 4; // 这个数字越大,图片大小越小.
					Bitmap pic = null;
					pic = BitmapFactory.decodeFile(photoName, op);
					img_photo.setImageBitmap(pic); // 这个ImageView是拍照完成后显示图片
					FileOutputStream b = null;
					;
					try {
						b = new FileOutputStream(photoName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (pic != null) {
						pic.compress(Bitmap.CompressFormat.JPEG, 50, b);
					}
				}
				break;
			default:
				return;
		}
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
		builder.setMessage("确认上传图片吗？");

		builder.setTitle("提示");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				uploadFile();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();
	}

	/* 上传文件至Server的方法 */
	private void uploadFile() {
		System.out.print("正在发送请求！");
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);
			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设置每次写入1024bytes */
			System.out.print("已经找到数据正在发送！");
			int bufferSize = 1024*10;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			showDialog("上传成功");
			/* 关闭DataOutputStream */
			ds.close();
		} catch (Exception e) {
			System.out.print("网络出现异常！");
			showDialog("上传失败");
			e.printStackTrace();
		}
	}
	/* 显示Dialog的method */
	private void showDialog(String mess) {
		new AlertDialog.Builder(CameraActivity.this).setTitle("提示")
				.setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}


}