package cn.bigbike.cycling.ui;

import java.text.DecimalFormat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.system.BigApp;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FullscreenCyclingActivity extends Activity {
	
	private static final String TAG = "FullscreenCyclingActivity";
	private View controlsView, contentView;
	private static final int AUTO_HIDE_DELAY_MILLIS = 2000;
	private boolean viewIsHide = false;
	
	private static final int DELAY_MILLIS = 1000;  // UI刷新间隔时间	
	private TextView speedNow, todaySpeedAvg, todaySpeedMax, todayMileage, onceSpeedAvg, onceSpeedMax, onceMileage, totalSpeedAvg, totalSpeedMax, totalMileage;
	private LinearLayout onceView;
	
	private BigApp bigApp;
	
	private MyConfig mConfig;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cycling_fullscreen);

		bigApp = (BigApp)this.getApplication();
		mConfig = new MyConfig(this);
		
		controlsView = findViewById(R.id.activity_cycling_fullscreen_controls);
		contentView = findViewById(R.id.activity_cycling_fullscreen_content);
		
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(viewIsHide){
					viewIsHide = false;
					YoYo.with(Techniques.FadeInUp)
	                .duration(300)
	                .playOn(controlsView);  // 动画
					mHideHandler.removeCallbacks(mHideRunnable);
					mHideHandler.postDelayed(mHideRunnable, AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});
		
		Button exit = (Button)findViewById(R.id.activity_cycling_fullscreen_exit);
		exit.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				FullscreenCyclingActivity.this.finish();
			}
		});
		
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, AUTO_HIDE_DELAY_MILLIS);
		
		speedNow = (TextView)findViewById(R.id.activity_cycling_fullscreen_speednow);
		todaySpeedAvg = (TextView)findViewById(R.id.activity_cycling_fullscreen_today_speedavg);
		todaySpeedMax = (TextView)findViewById(R.id.activity_cycling_fullscreen_today_speedmax);
		todayMileage = (TextView)findViewById(R.id.activity_cycling_fullscreen_today_mileage);
		onceSpeedAvg = (TextView)findViewById(R.id.activity_cycling_fullscreen_once_speedavg);
		onceSpeedMax = (TextView)findViewById(R.id.activity_cycling_fullscreen_once_speedmax);
		onceMileage = (TextView)findViewById(R.id.activity_cycling_fullscreen_once_mileage);
		totalSpeedAvg = (TextView)findViewById(R.id.activity_cycling_fullscreen_total_speedavg);
		totalSpeedMax = (TextView)findViewById(R.id.activity_cycling_fullscreen_total_speedmax);
		totalMileage = (TextView)findViewById(R.id.activity_cycling_fullscreen_total_mileage);
		onceView =  (LinearLayout)findViewById(R.id.activity_cycling_fullscreen_once);
		
		Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/text.otf");
		speedNow.setTypeface(typeFace);
		todaySpeedAvg.setTypeface(typeFace);
		todaySpeedMax.setTypeface(typeFace);
		todayMileage.setTypeface(typeFace);
		onceSpeedAvg.setTypeface(typeFace);
		onceSpeedMax.setTypeface(typeFace);
		onceMileage.setTypeface(typeFace);
		totalSpeedAvg.setTypeface(typeFace);
		totalSpeedMax.setTypeface(typeFace);
		totalMileage.setTypeface(typeFace);
		
		refreshUI();
	}
	
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			YoYo.with(Techniques.FadeOutDown)
            .duration(300)
            .playOn(controlsView);  // 动画
			viewIsHide = true;
		}
	};
	
	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		handler.removeCallbacks(runnable);  // 关闭定时刷新
		handler.postDelayed(runnable, DELAY_MILLIS);  // 开启定时刷新
		// 设置长亮
        mConfig.read();
		if( mConfig.displayLongBright ){
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // 禁止屏幕休眠和锁屏
		}
	}
	
	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
		handler.removeCallbacks(runnable);  // 关闭定时刷新
		bigApp.localService.mCont.saveData();  // 保存数据
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHideRunnable = null;
		mHideHandler = null;
		runnable = null;
		handler = null;
	}
	
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			refreshUI();
			handler.postDelayed(this, DELAY_MILLIS);
		}
	};
	
	public void refreshUI(){
		//Log.i(TAG, "refreshUI");
		if( bigApp.localService==null )
			return;
		if( bigApp.localService.mCont==null )
			return;
		
		bigApp.localService.mCont.refreshTodayDate();  // 日期变化时，刷新今日数据
		
		if( bigApp.localService.mCont.onceTitle==null ){
			onceView.setVisibility(View.GONE);
		}else{
			onceView.setVisibility(View.VISIBLE);
		}
		
		DecimalFormat dfInt = new DecimalFormat("0");
		DecimalFormat dfFloat = new DecimalFormat("0.0");
		
		speedNow.setText( dfFloat.format(bigApp.localService.mCont.speedNow) );

		todaySpeedMax.setText( bigApp.localService.mCont.todaySpeedMax>100?dfInt.format(bigApp.localService.mCont.todaySpeedMax):dfFloat.format(bigApp.localService.mCont.todaySpeedMax) );
		onceSpeedMax.setText( bigApp.localService.mCont.onceSpeedMax>100?dfInt.format(bigApp.localService.mCont.onceSpeedMax):dfFloat.format(bigApp.localService.mCont.onceSpeedMax) );
		totalSpeedMax.setText( bigApp.localService.mCont.totalSpeedMax>100?dfInt.format(bigApp.localService.mCont.totalSpeedMax):dfFloat.format(bigApp.localService.mCont.totalSpeedMax) );
		
		todaySpeedAvg.setText( dfFloat.format(bigApp.localService.mCont.todaySpeedAvg) );
		onceSpeedAvg.setText( dfFloat.format(bigApp.localService.mCont.onceSpeedAvg) );
		totalSpeedAvg.setText( dfFloat.format(bigApp.localService.mCont.totalSpeedAvg) );
		
		todayMileage.setText( dfFloat.format(bigApp.localService.mCont.todayMileage) );
		onceMileage.setText( dfFloat.format(bigApp.localService.mCont.onceMileage) );
		totalMileage.setText( dfFloat.format(bigApp.localService.mCont.totalMileage) );
	}

}
