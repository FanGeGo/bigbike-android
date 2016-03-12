package cn.bigbike.cycling.ui;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.myclass.MyLocation;
import cn.bigbike.cycling.myclass.MyUser;
import cn.bigbike.cycling.myclass.MyVersion;
import cn.bigbike.cycling.system.BigApp;
import cn.bigbike.cycling.util.DateTime;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ConfigActivity extends Activity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "ConfigActivity";
	
	private MyConfig mConfig;
	private BigApp bigApp;
	private MyUser user;
	
	private ToggleButton altitudeState, displayState;
	private TextView perimeterValue, runModeValue, lastSycnTime, versionNew, altitudeText;
	private LinearLayout perimeterBox;
	
	private SyncReceiver sReceiver = new SyncReceiver();
	
	private MyVersion mVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		
		bigApp = (BigApp)this.getApplication();
		user = bigApp.user;
		mConfig = new MyConfig(this);		
		mVersion = new MyVersion(this, mConfig);
		
		ImageButton back = (ImageButton)findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		LinearLayout runMode = (LinearLayout)findViewById(R.id.activity_config_runmode);
		runMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), RunmodeConfigActivity.class);
				startActivity(intent);
			}
		});
		
		LinearLayout perimeter = (LinearLayout)findViewById(R.id.activity_config_perimeter);
		perimeter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), PerimeterConfigActivity.class);
				startActivity(intent);
			}
		});
		
		displayState = (ToggleButton)findViewById(R.id.activity_config_display_value);
		LinearLayout display = (LinearLayout)findViewById(R.id.activity_config_display);		
		display.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( displayState.isChecked() ){
					displayState.setChecked(false);
					mConfig.displayLongBright = false;
				}else{
					displayState.setChecked(true);
					mConfig.displayLongBright = true;
				}
				mConfig.save();
			}
		});
		
		LinearLayout altitude = (LinearLayout)findViewById(R.id.activity_config_altitude);
		altitude.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		
		LinearLayout sync = (LinearLayout)findViewById(R.id.activity_config_sync);
		sync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( user.uid==0 ){
					Toast.makeText(getApplicationContext(), "登陆后才能同步数据", Toast.LENGTH_SHORT).show();
					// 打开登陆窗口
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), LoginActivity.class);
					startActivity(intent);
					return;
				}
				bigApp.localService.mSync.start();
			}
		});
		
		LinearLayout checkVersion = (LinearLayout)findViewById(R.id.activity_config_version_check);
		checkVersion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mVersion.checkServerVersion();
			}
		});		
		
		runModeValue = (TextView)findViewById(R.id.activity_config_runmode_value);
		perimeterBox = (LinearLayout)findViewById(R.id.activity_config_perimeter_box);
		perimeterValue = (TextView)findViewById(R.id.activity_config_perimeter_value);
		altitudeText = (TextView)findViewById(R.id.activity_config_altitude_text);
		altitudeState = (ToggleButton)findViewById(R.id.activity_config_altitude_value);
		lastSycnTime = (TextView)findViewById(R.id.activity_config_lastsycntime);
		versionNew = (TextView)findViewById(R.id.activity_config_version_new);
		
		registerReceiver(sReceiver, new IntentFilter("cn.bigbike.cycling.SYNC_STATE"));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if( MyLocation.isOpenGPS(this) ){
			altitudeState.setChecked(true);
		}else{
			altitudeState.setChecked(false);
		}
		mConfig.read();
		// 运行模式
		if( mConfig.appRunMode==MyConfig.MODE_GPS ){
			runModeValue.setText("GPS模式");
			perimeterBox.setVisibility(View.GONE);
			altitudeText.setText("GPS");
		}else{
			runModeValue.setText("智能硬件模式");
			perimeterBox.setVisibility(View.VISIBLE);
			altitudeText.setText("爬坡 (GPS)");
		}
		// 车轮周长
		perimeterValue.setText(String.valueOf((int)mConfig.wheelPerimeter)+" 厘米");
		if( mConfig.appLatestVersion>MyVersion.getVersionName(this) ){
			versionNew.setVisibility(View.VISIBLE);
		}else{
			versionNew.setVisibility(View.INVISIBLE);
		}
		// 屏幕长亮
		displayState.setChecked( mConfig.displayLongBright );
		// 最后同步时间
		if( user.lastSyncTime==0 ){
			lastSycnTime.setText("");
		}else{
			String timeText = DateTime.getDateFormat(user.lastSyncTime, DateTime.FORMAT_SHORT);
			if( DateTime.getDateFormat(user.lastSyncTime, DateTime.FORMAT_LONG).equals(DateTime.getTodayDate()) )
				timeText = "今日 " + DateTime.getDateFormat(user.lastSyncTime, DateTime.FORMAT_HIDE, DateTime.FORMAT_SHORT);
			if( DateTime.getDateFormat(user.lastSyncTime, DateTime.FORMAT_LONG).equals(DateTime.getYestoryDate()) )
				timeText = "昨日 " + DateTime.getDateFormat(user.lastSyncTime, DateTime.FORMAT_HIDE, DateTime.FORMAT_SHORT);
			lastSycnTime.setText( timeText );
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(sReceiver);
	}

    // 检测同步状态
	private class SyncReceiver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		lastSycnTime.setText( "刚刚   " );
    	}
    }

}
