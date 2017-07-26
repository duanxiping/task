package com.ble.adapter;

import java.util.ArrayList;

import com.ble.R;
import com.ble.model.OpenLockeBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OpenLockeAdapter extends BaseAdapter {

	private ArrayList<OpenLockeBean> arrayList ;
	private LayoutInflater mInflater;
	// 添加构造方法实例化我们LayoutInflater
		public OpenLockeAdapter(Context context ) {
			mInflater = LayoutInflater.from(context);

		}

		// 创建 添加数据方法

		public void addDataSource(ArrayList<OpenLockeBean> arrayList) {

			this.arrayList = arrayList;

		}

		// 创建 插入数据方法

		public void insertDataSource(OpenLockeBean bean) {

			arrayList.add(bean);
			// 当数据发生变化的时候刷新数据
//			notifyDataSetChanged();

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size() > 0 ? arrayList.size() : 0;
		}

		// 当前行的数据对象
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrayList.size() > 0 ? arrayList.get(position) : "";
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		// 加载布局及赋值
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				// 通过LayoutInflater.inflate()返回 我们列表项视图
				convertView = mInflater.inflate(R.layout.open_lock_item, parent,
						false);
				// 创建ViewHolder 放入控件
				holder = new ViewHolder();
				holder.openLocke = (TextView) convertView
						.findViewById(R.id.state);
				holder.openTime = (TextView) convertView
						.findViewById(R.id.openTime);
				// 通过设置tag的方式把我们控件进行缓存
				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			// 取消息对象
			OpenLockeBean bean = (OpenLockeBean) getItem(position);

			// 赋值
				holder.openLocke.setVisibility(View.VISIBLE);
				holder.openTime.setVisibility(View.VISIBLE);
				holder.openLocke.setText(bean.getOpenSuccess());
				holder.openTime.setText(bean.getOpenTime());


			return convertView;
		}

		// 创建ViewHolder 缓存我们控件

		static class ViewHolder {
			TextView openLocke;
			TextView openTime;
			

		}

}
