package com.djx.pin.utils;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class DAdapter<T> extends BaseAdapter {

	private Context context;

	public DAdapter(Context context) {
		super();
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	private List<T> dataList;

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getCount() {

		if (null != dataList) {
			return dataList.size();
		}

		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

}
