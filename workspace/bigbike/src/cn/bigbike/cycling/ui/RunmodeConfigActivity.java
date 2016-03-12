package cn.bigbike.cycling.ui;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.myclass.MyController;
import cn.bigbike.cycling.system.BigApp;
import cn.bigbike.cycling.system.BigService;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.LinearLayout;

public class RunmodeConfigActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "RunmodeConfigActivity";
	
	private MyConfig mConfig;
	private BigApp bigApp;
	private boolean appIsFirstStart = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_runmode);

		mConfig = new MyConfig(this);
		bigApp = (BigApp)getApplication();
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if( bundle!=null ){
			appIsFirstStart = bundle.getBoolean("appIsFirstStart");
		}
		
		final ImageView gpsChecked = (ImageView) findViewById(R.id.activity_config_runmode_gps_checked);
		final ImageView hardwareChecked = (ImageView) findViewById(R.id.activity_config_runmode_hardware_checked);
		
		if( mConfig.appRunMode==MyConfig.MODE_GPS ){
			gpsChecked.setVisibility(View.VISIBLE);
		}
		
		if( mConfig.appRunMode==MyConfig.MODE_HARDWARE ){
			hardwareChecked.setVisibility(View.VISIBLE);
		}

		LinearLayout gps = (LinearLayout) findViewById(R.id.activity_config_runmode_gps);
		gps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mConfig.appRunMode!=MyConfig.MODE_GPS ){
					mConfig.appRunMode = MyConfig.MODE_GPS;
					mConfig.save();					
					// 切换模式
					bigApp.localService.mCont.runMode = MyConfig.MODE_GPS;
					bigApp.localService.mCont.stop();
					bigApp.localService.mLoc.start();
				}
				gpsChecked.setVisibility(View.VISIBLE);
				hardwareChecked.setVisibility(View.GONE);
				finish();
			}
		});
		
		LinearLayout hardware = (LinearLayout) findViewById(R.id.activity_config_runmode_hardware);
		hardware.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mConfig.appRunMode!=MyConfig.MODE_HARDWARE ){
					mConfig.appRunMode = MyConfig.MODE_HARDWARE;
					mConfig.save();					
					// 切换模式
					bigApp.localService.mCont.runMode = MyConfig.MODE_HARDWARE;
					if( bigApp.localService.hpReceiver.status==BigService.HeadsetPlugReceiver.PULLOUT ){
						bigApp.localService.mLoc.stop();
						bigApp.localService.mCont.runState = MyController.NO_START;
					}else{
						bigApp.localService.mCont.start();
						bigApp.localService.mLoc.start();
					}
					// 设置车轮周长
					if( appIsFirstStart ){
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(), PerimeterConfigActivity.class);
						startActivity(intent);
					}
				}
				hardwareChecked.setVisibility(View.VISIBLE);
				gpsChecked.setVisibility(View.GONE);
				finish();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
