package com.example.logisticsprivacysystem.Address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.logisticsprivacysystem.R;

import java.util.List;


public class AddressAdapter extends ArrayAdapter<Address> {
	private int resourceId;

	public AddressAdapter(Context context, int textViewResourceId,
						List<Address> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Address address = getItem(position); // 获取当前项的Fruit实例
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.name_edit = (TextView) view.findViewById (R.id.name_edit);
			viewHolder.phone_edit = (TextView) view.findViewById(R.id.phone_edit);
			viewHolder.address_edit = (TextView) view.findViewById(R.id.address_edit);
			viewHolder.code_edit = (TextView) view.findViewById(R.id.code_edit);
			view.setTag(viewHolder); // 将ViewHolder存储在View中
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
		}
		viewHolder.name_edit.setText(address.getName());
		viewHolder.phone_edit.setText(address.getPhone());
		viewHolder.address_edit.setText(address.getAddress());
		viewHolder.code_edit.setText(address.getCode());
		return view;
	}

	class ViewHolder {
		TextView name_edit;
		TextView phone_edit;
		TextView address_edit;
		TextView code_edit;

	}

}
