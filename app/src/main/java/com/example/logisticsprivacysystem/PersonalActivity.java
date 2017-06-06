package com.example.logisticsprivacysystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.logisticsprivacysystem.Address.AddaddressActivity;
import com.example.logisticsprivacysystem.Address.MyDatabaseHelper;

/**
 * Created by Axes on 2017/5/8.
 */

public class PersonalActivity extends Activity{
	ImageButton about;
	LinearLayout address_btn;
	LinearLayout usermsg_btn;
	private MyDatabaseHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal);
		about=(ImageButton)findViewById(R.id.about);
		address_btn=(LinearLayout)findViewById(R.id.addressbook);
		usermsg_btn=(LinearLayout)findViewById(R.id.usermsg);
		dbHelper = new MyDatabaseHelper(this, "Addressbook.db", null, 1);

		about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dbHelper.getWritableDatabase();
				Intent intent=new Intent();
				intent.setClass(PersonalActivity.this,AboutActivity.class);
				PersonalActivity.this.startActivity(intent);
			}
		});
		address_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent=new Intent();
				intent.setClass(PersonalActivity.this,AddaddressActivity.class);
				startActivity(intent);
			}
		});
		usermsg_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent=new Intent();
				intent.setClass(PersonalActivity.this,SureActivity.class);
				PersonalActivity.this.startActivity(intent);
			}
		});
	}
}
