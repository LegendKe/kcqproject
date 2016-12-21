package com.djx.pin.alipay;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;

public class ExternalFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_alipay_external, container,
				false);
		Bundle bundle = getActivity().getIntent().getExtras();
		((TextView) v.findViewById(R.id.product_subject)).setText("Test");
		((TextView) v.findViewById(R.id.product_detail)).setText("暂无");

		return v;
	}
}
