package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RemindPackageActivity extends Activity{
	private TextView remindinfo;
	String street="";
	String name="";
	String phone="";
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remind_package);
		remindinfo=(TextView)findViewById(R.id.remindinfo);
		Intent intent=getIntent();
		street=intent.getStringExtra("Street");
		name=intent.getStringExtra("Name");
		phone=intent.getStringExtra("Phone");
		remindinfo.setText("街道地址："+street+"\r\n"+getResources().getString(R.string.Name)+name+"\r\n"+"手机尾号："+phone);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent(RemindPackageActivity.this, MainActionActivity.class);
		startActivity(intent);
	}
}
