package com.example.logisticsprivacysystem.Address;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.logisticsprivacysystem.R;

/**
 * Created by Axes on 2017/5/8.
 */

public class Address_addpage extends Activity {
	Button savebutton;
	EditText addresssheng;
	EditText addressqu;
	EditText addressedit;
	EditText addressname;
	EditText addressphone;
	EditText youbian;
	String address;
	String name;
	String phone;
	String code;
	String code_read;
	private MyDatabaseHelper dbHelper;
	SQLiteDatabase db;
	int a;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_my_list2);
		Intent intent = getIntent();
		a=Integer.valueOf(intent.getStringExtra("a"));
		savebutton=(Button)findViewById(R.id.saveButton);
		addresssheng=(EditText)findViewById(R.id.addresssheng);
		addressqu=(EditText)findViewById(R.id.addressqu);
		addressedit=(EditText)findViewById(R.id.addressedit);
		addressname=(EditText)findViewById(R.id.addressname);
		addressphone=(EditText)findViewById(R.id.addressphone);
		youbian=(EditText)findViewById(R.id.youbian);
		dbHelper = new MyDatabaseHelper(Address_addpage.this, "Addressbook.db", null, 1);
		db = dbHelper.getWritableDatabase();
		code_read=intent.getStringExtra("code");

		if(a==1){
			Cursor cursor = db.query("Addressbook", null, "code=?", new String[]{code_read}, null, null, null);
			if (cursor.moveToFirst()) {
				do {
					// 遍历Cursor对象，取出数据并打印
					String name = cursor.getString(cursor.getColumnIndex("name"));
					String phone = cursor.getString(cursor.getColumnIndex("phone"));
					String address = cursor.getString(cursor.getColumnIndex("address"));
					String code = cursor.getString(cursor.getColumnIndex("code"));
					addressname.setText(name);
					addressphone.setText(phone);
					youbian.setText(code);
					addresssheng.setText(address.substring(0,3));
					addressqu.setText(address.substring(3,6));
					addressedit.setText(address.substring(6));
				} while (cursor.moveToNext());
			}
			cursor.close();
		}

		savebutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				address=addresssheng.getText().toString()+addressqu.getText().toString()+addressedit.getText().toString();
				name=addressname.getText().toString();
				phone=addressphone.getText().toString();
				code=youbian.getText().toString();
				ContentValues values = new ContentValues();
				switch (a){
					case 0:
						// 开始组装第一条数据
						values.put("name", name);
						values.put("phone", phone);
						values.put("address", address);
						values.put("code", code);
						db.insert("Addressbook", null, values); // 插入第一条数据
						values.clear();
						break;
					case 1:
						values.put("name", name);
						values.put("phone", phone);
						values.put("address", address);
						values.put("code", code);
						db.update("Addressbook", values, "code = ?", new String[] { code_read });
						break;
				}
				finish();
			}
		});

	}
}
