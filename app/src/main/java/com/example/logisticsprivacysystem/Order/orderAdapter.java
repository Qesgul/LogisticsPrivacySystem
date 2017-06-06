package com.example.logisticsprivacysystem.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.logisticsprivacysystem.R;

import java.util.List;


public class orderAdapter extends ArrayAdapter<Order> {
	private int resourceId;

	public orderAdapter(Context context, int textViewResourceId,
						List<Order> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Order order = getItem(position); // 获取当前项的Fruit实例
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.order_no = (TextView) view.findViewById (R.id.order_id);
			viewHolder.order_num = (TextView) view.findViewById(R.id.order_name);
			view.setTag(viewHolder); // 将ViewHolder存储在View中
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
		}
		viewHolder.order_no.setText(order.getNo());
		viewHolder.order_num.setText(order.getNum());
		return view;
	}

	class ViewHolder {
		TextView order_no;
		TextView order_num;
	}

}
