package com.example.logisticsprivacysystem.Address;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.logisticsprivacysystem.R;
import com.otg.idcard.OTGReadCardAPI;

import java.util.ArrayList;
import java.util.List;

public class NfcActivity extends Activity{
	private String dizhi;
	private String ming;
	private TextView title;

	private TextView Readingtext;
	private MyDatabaseHelper dbHelper;

	SQLiteDatabase db;
	private ListView nfclist;
	private AddressAdapter adapter;
	private List<Address> addressesList = new ArrayList<Address>();

	private NfcAdapter mAdapter;
	private OTGReadCardAPI ReadCardAPI;
	private PendingIntent pi = null;
	//滤掉组件无法响应和处理的Intent
	private IntentFilter tagDetected = null;
	private String[][] mTechLists;
	private Intent inintent=null;

	private int readflag=0;
	private int nameflag;

	public static final int MESSAGE_VALID_NFCBUTTON=16;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		dbHelper = new MyDatabaseHelper(NfcActivity.this, "Addressbook.db", null, 1);

		title = (TextView) findViewById(R.id.title);
		Readingtext = (TextView) findViewById(R.id.Readingtext);
		title.setText("身份证识别NFC模式");
		Readingtext.setVisibility(View.GONE);
		Readingtext.setText("      正在读卡，请稍候...");
		Readingtext.setTextColor(Color.RED);

		ReadCardAPI=new OTGReadCardAPI(NfcActivity.this);
		mAdapter = NfcAdapter.getDefaultAdapter(NfcActivity.this);
		if (mAdapter!=null) {init_NFC();}

	}
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		mHandler.sendEmptyMessageDelayed(MESSAGE_CLEAR_ITEMS, 0);
		if (readflag==1)
		{
			return;
		}
		inintent=intent;
		readflag=1;
		ReadCardAPI.writeFile("come into onNewIntent 2");
		Readingtext.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(MESSAGE_VALID_NFCBUTTON, 0);
	}
	@Override
	public void onPause() {
		super.onPause();
//		mAdapter.disableForegroundDispatch(this);
		if (mAdapter!=null)	stopNFC_Listener();
	}
	@Override
	protected void onResume() {
		super.onResume();
		ReadCardAPI.writeFile("come into onResume 1");
		if (mAdapter != null) startNFC_Listener();
		ReadCardAPI.writeFile("come into onResume 2");
		ReadCardAPI.writeFile("pass onNewIntent 1.111111 action=" + getIntent().getAction());
	}

	private void startNFC_Listener() {
		mAdapter.enableForegroundDispatch(this, pi, new IntentFilter[] { tagDetected }, mTechLists);
	}

	private void stopNFC_Listener() {
		mAdapter.disableForegroundDispatch(this);
	}

	private void init_NFC() {
		pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);//.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		mTechLists = new String[][] { new String[] { NfcB.class.getName() } };
	}

	private void initAddress() {

	}

	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int tt;
			switch (msg.what) {
				case MESSAGE_VALID_NFCBUTTON:
					ReadCardAPI.writeFile("come into MESSAGE_CLEAR_ITEMS 1");
					tt=ReadCardAPI.NfcReadCard(inintent);
//					tt=testReadCardAPI.ReadCard();
					ReadCardAPI.writeFile("come into MESSAGE_CLEAR_ITEMS 2");
					Log.e("For Test"," ReadCard TT="+tt);
					if (tt==2)
					{
						new AlertDialog.Builder(NfcActivity.this)
								.setTitle("提示" ).setMessage("接收数据超时！" )
								.setPositiveButton("确定" ,  null ).show();
//						onredo.setEnabled(true);
//						onredo.setFocusable(true);
//						onredo.setBackgroundResource(R.drawable.sfz_dq);
					}
					if (tt==41)
					{
						new AlertDialog.Builder(NfcActivity.this)
								.setTitle("提示" ).setMessage("读卡失败！" )
								.setPositiveButton("确定" ,  null ).show();
					}
					if (tt==42)
					{
						new AlertDialog.Builder(NfcActivity.this)
								.setTitle("提示" ).setMessage("没有找到服务器！" )
								.setPositiveButton("确定" ,  null ).show();
					}
					if (tt==43)
					{
						new AlertDialog.Builder(NfcActivity.this)
								.setTitle("提示" ).setMessage("服务器忙！" )
								.setPositiveButton("确定" ,  null ).show();
					}
					if (tt==90)
					{
						dizhi=ReadCardAPI.Address();
						ming=ReadCardAPI.Name();
						Readingtext.setVisibility(View.VISIBLE);
						Readingtext.setText("      读卡成功");
						ReadCardAPI.release();
						nameflag=0;
						db = dbHelper.getWritableDatabase();
						Cursor cursor = db.query("Addressbook", null, "name=?", new String[]{ming.trim()}, null, null, null);
						if (cursor.moveToFirst()) {
							do {
								nameflag=1;
								String name = cursor.getString(cursor.getColumnIndex("name"));
								String phone = cursor.getString(cursor.getColumnIndex("phone"));
								String address = cursor.getString(cursor.getColumnIndex("address"));
								String code = cursor.getString(cursor.getColumnIndex("code"));
								Address name_edit = new Address(name,phone,address,code);
								addressesList.add(name_edit);
							} while (cursor.moveToNext());
						}
						cursor.close();
						Log.e("For Test"," nameflag="+nameflag);
						switch (nameflag){
							case 1:
								adapter = new AddressAdapter(NfcActivity.this, R.layout.address_item, addressesList);
								nfclist=(ListView)findViewById(R.id.nfclist);
								nfclist.setAdapter(adapter);
								nfclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
										Address address = addressesList.get(position);
										Intent intent=new Intent();
										intent.putExtra("dizhi",address.getAddress());
										intent.putExtra("ming",address.getName());
										setResult(1001, intent);
										finish();
									}
								});
								break;
							case 0:
								AlertDialog.Builder builder = new AlertDialog.Builder(NfcActivity.this);
								builder.setMessage("是否添加联系人？");
								builder.setTitle("提示");
								builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent();
										intent.putExtra("dizhi",dizhi);
										intent.putExtra("ming",ming);
										setResult(1001, intent);
										finish();
									}
								});
								builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});
								builder.create().show();
								break;
						}
					}
					readflag=0;
					break;
			}
		}
	};


}
