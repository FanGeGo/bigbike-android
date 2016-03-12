package cn.bigbike.cycling.ui;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyUser;
import cn.bigbike.cycling.system.BigApp;

import android.app.Fragment;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class InteractFragment extends Fragment {

	private static final String TAG = "InteractFragment";
	private MainActivity mainActivity;
	
	private MyUser user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity) getActivity();
		BigApp bigApp = (BigApp)mainActivity.getApplication();
		user = bigApp.user;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_interact, container, false);

		LinearLayout rankTotal = (LinearLayout) view.findViewById(R.id.fragment_interact_rank_total);
		rankTotal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( user.uid==0 ){
					Toast.makeText(mainActivity.getApplicationContext(), "登陆后才能查看", Toast.LENGTH_SHORT).show();
					// 打开登陆窗口
					Intent intent = new Intent();
					intent.setClass(mainActivity.getApplicationContext(), LoginActivity.class);
					startActivity(intent);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), TotalRankActivity.class);
				startActivity(intent);
			}
		});
		
		LinearLayout rankToday = (LinearLayout) view.findViewById(R.id.fragment_interact_rank_today);
		rankToday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( user.uid==0 ){
					Toast.makeText(mainActivity.getApplicationContext(), "登陆后才能查看", Toast.LENGTH_SHORT).show();
					// 打开登陆窗口
					Intent intent = new Intent();
					intent.setClass(mainActivity.getApplicationContext(), LoginActivity.class);
					startActivity(intent);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), TodayRankActivity.class);
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
