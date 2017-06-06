package com.example.logisticsprivacysystem.Address;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.logisticsprivacysystem.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Axes on 2017/5/8.
 */

public class AddaddressActivity extends Activity{
	private ListView listView;
	private MyDatabaseHelper dbHelper;
	private AddressAdapter adapter;
	int a;
	private List<Address> addressesList = new ArrayList<Address>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_my_list1);
		dbHelper = new MyDatabaseHelper(AddaddressActivity.this, "Addressbook.db", null, 1);

		initAddress();
		adapter = new AddressAdapter(AddaddressActivity.this, R.layout.address_item, addressesList);
		listView=(ListView)findViewById(R.id.listview);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Address address = addressesList.get(position);
				a=1;
				Intent intent=new Intent();
				intent.putExtra("a",a+"");
				intent.putExtra("code",address.getCode());
				intent.setClass(AddaddressActivity.this,Address_addpage.class);
				AddaddressActivity.this.startActivity(intent);
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				final Address address = addressesList.get(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(AddaddressActivity.this);
				builder.setItems(new String[]{getResources().getString(R.string.delete_item)}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String delete_code=address.getCode();
						SQLiteDatabase db=dbHelper.getWritableDatabase();
						db.delete("Addressbook","code=?",new String[]{delete_code});
						addressesList.remove(position);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
				});
				builder.show();

				return true;
			}
		});

		Button newaddbutton = (Button) this.findViewById(R.id.add_address);
		newaddbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				a=0;
				Intent intent=new Intent();
				intent.putExtra("a",a+"");
				intent.setClass(AddaddressActivity.this,Address_addpage.class);
				AddaddressActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		addressesList.removeAll(addressesList);
		initAddress();
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	private void initAddress() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("Addressbook", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// 遍历Cursor对象，取出数据并打印
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String phone = cursor.getString(cursor.getColumnIndex("phone"));
				String address = cursor.getString(cursor.getColumnIndex("address"));
				String code = cursor.getString(cursor.getColumnIndex("code"));
				Address name_edit = new Address(name,phone,address,code);
				addressesList.add(name_edit);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}
}
