package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.logisticsprivacysystem.Address.NfcActivity;
import com.example.logisticsprivacysystem.Order.OrderActivity;
import com.zxing.activity.CaptureActivity;

public class MainActionActivity extends Activity {
	LinearLayout sendMessage=null;
	LinearLayout distribution=null;
	LinearLayout distriCheck=null;
	LinearLayout CheckInfo=null;
	ImageButton nfcbutton;
	ImageButton about;
	LinearLayout Image=null;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainaction);
		sendMessage=(LinearLayout)findViewById(R.id.mainaction_sendButton);
		sendMessage.setOnClickListener(new SendButtonlistener());
		distribution=(LinearLayout)findViewById(R.id.mainaction_distributionButton);
		distribution.setOnClickListener(new Distrilistener());
		distriCheck=(LinearLayout)findViewById(R.id.distriCheck);
		distriCheck.setOnClickListener(new distriCheckListener());
		CheckInfo=(LinearLayout) findViewById(R.id.login_queryButton);
		CheckInfo.setOnClickListener(new checkOkListener());
		about=(ImageButton)findViewById(R.id.person);
		about.setOnClickListener(new aboutListener());
		nfcbutton=(ImageButton) findViewById(R.id.nfcbutton);
		nfcbutton.setOnClickListener(new nfcokListener());
		Image=(LinearLayout)findViewById(R.id.image);
		Image.setOnClickListener(new ImageListener());
	}
	class SendButtonlistener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(MainActionActivity.this, RecandPosActivity.class);
		    MainActionActivity.this.startActivity(intent);
		}
	}
	class Distrilistener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent openCameraIntent = new Intent(MainActionActivity.this,CaptureActivity.class);
			startActivityForResult(openCameraIntent, 0);
		}
		
	}
	class distriCheckListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent openCameraIntent1 = new Intent(MainActionActivity.this,DistriCheckActivity.class);
			startActivityForResult(openCameraIntent1, 0);
		}
		
	}
	class checkOkListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(MainActionActivity.this,CheckActivity.class);
			MainActionActivity.this.startActivity(intent);
		}

	}
	class aboutListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(MainActionActivity.this,PersonalActivity.class);
			MainActionActivity.this.startActivity(intent);
		}

	}
	class nfcokListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(MainActionActivity.this,NfcActivity.class);
			MainActionActivity.this.startActivity(intent);
		}

	}
	class ImageListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(MainActionActivity.this,OrderActivity.class);
			MainActionActivity.this.startActivity(intent);
		}

	}

}
