package com.example.logisticsprivacysystem.Order;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.logisticsprivacysystem.R;

/**
 * Created by Axes on 2017/5/17.
 */

public class OrderActivity extends ActivityGroup implements View.OnClickListener {
	//定义帧布局对象
	private FrameLayout mContent;

	//定义图片按钮对象
	private Button over,unover;

	//定义标示符
	private static final String HOME_LIKE_ID = "like";
	private static final String HOME_MARK_ID = "mark";

	//点击灰度效果的颜色矩阵
	public final float[] SCROLL_DOWN = new float[] {1,0,0,0,50,0,1,0,0,50,0,0,1,0,50,0,0,0,1,0};
	//恢复原状的颜色矩阵
	public final float[] SCROLL_CANCEL =new float[] {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);

		mContent = (FrameLayout) findViewById(R.id.content);

		//得到按钮对象
		over = (Button) findViewById(R.id.home_bt_like);
		unover = (Button) findViewById(R.id.home_bt_mark);

		//按钮设置监听
		over.setOnClickListener(this);
		unover.setOnClickListener(this);

		//初始化默认显示的页面
		showMyView();
	}

	/**
	 * 添加视图
	 */
	public void addView(String id, Class<?> clazz) {
		Intent intent = new Intent(this, clazz);
		//移除这个布局中所有的组件
		mContent.removeAllViews();
		mContent.addView(getLocalActivityManager().startActivity(id, intent).getDecorView());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.home_bt_like:
				showMyView();
				break;
			case R.id.home_bt_mark:
				showMarkView();
				break;
			default:
				break;
		}
	}

	/**
	 * 显示我的贴吧页面
	 */
	private void showMyView(){
		addView(HOME_LIKE_ID, OverActivity.class);
		unover.getBackground().setColorFilter(new ColorMatrixColorFilter(SCROLL_CANCEL));
		over.getBackground().setColorFilter(new ColorMatrixColorFilter(SCROLL_DOWN));
	}

	/**
	 * 显示我的标签页面
	 */
	private void showMarkView(){
		addView(HOME_MARK_ID, UnOverActivity.class);
		over.getBackground().setColorFilter(new ColorMatrixColorFilter(SCROLL_CANCEL));
		unover.getBackground().setColorFilter(new ColorMatrixColorFilter(SCROLL_DOWN));
	}
}
