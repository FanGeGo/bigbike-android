package cn.bigbike.cycling.ui;

import cn.bigbike.cycling.R;

import android.app.Fragment;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.ImageView;

public class MallFragment extends Fragment {

	private static final String TAG = "MallFragment";
	private MainActivity mainActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity)getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_mall, container, false);
		
		ImageView x1 = (ImageView)view.findViewById(R.id.fragment_mall_banner_x1);
		x1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("url", "http://bigbike.sinaapp.com/to/url/mall_goods_x1");
				intent.setClass(mainActivity.getApplicationContext(), WebActivity.class);
				startActivity(intent);
			}
		});
		
		ImageView letdooo = (ImageView)view.findViewById(R.id.fragment_mall_banner_letdooo);
		letdooo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("url", "http://bigbike.sinaapp.com/to/url/mall_goods_letdooo");
				intent.setClass(mainActivity.getApplicationContext(), WebActivity.class);
				startActivity(intent);
			}
		});
		
		return view;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

}
