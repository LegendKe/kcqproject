package com.djx.pin.improve.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djx.pin.R;
import com.djx.pin.activity.LogoActivity;

/**
 * Created by 柯传奇 on 2016/12/17 0017.
 */
public class GuideFragment04 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide04, null);
        view.findViewById(R.id.ll_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LogoActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }
}
