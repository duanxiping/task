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
	// ��ӹ��췽��ʵ��������LayoutInflater
		public OpenLockeAdapter(Context context ) {
			mInflater = LayoutInflater.from(context);

		}

		// ���� ������ݷ���

		public void addDataSource(ArrayList<OpenLockeBean> arrayList) {

			this.arrayList = arrayList;

		}

		// ���� �������ݷ���

		public void insertDataSource(OpenLockeBean bean) {

			arrayList.add(bean);
			// �����ݷ����仯��ʱ��ˢ������
//			notifyDataSetChanged();

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size() > 0 ? arrayList.size() : 0;
		}

		// ��ǰ�е����ݶ���
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

		// ���ز��ּ���ֵ
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				// ͨ��LayoutInflater.inflate()���� �����б�����ͼ
				convertView = mInflater.inflate(R.layout.open_lock_item, parent,
						false);
				// ����ViewHolder ����ؼ�
				holder = new ViewHolder();
				holder.openLocke = (TextView) convertView
						.findViewById(R.id.state);
				holder.openTime = (TextView) convertView
						.findViewById(R.id.openTime);
				// ͨ������tag�ķ�ʽ�����ǿؼ����л���
				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			// ȡ��Ϣ����
			OpenLockeBean bean = (OpenLockeBean) getItem(position);

			// ��ֵ
				holder.openLocke.setVisibility(View.VISIBLE);
				holder.openTime.setVisibility(View.VISIBLE);
				holder.openLocke.setText(bean.getOpenSuccess());
				holder.openTime.setText(bean.getOpenTime());


			return convertView;
		}

		// ����ViewHolder �������ǿؼ�

		static class ViewHolder {
			TextView openLocke;
			TextView openTime;
			

		}

}
