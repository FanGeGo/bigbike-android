package cn.bigbike.cycling.ui;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyLocation;
import cn.bigbike.cycling.system.BigApp;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class TestActivity extends Activity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "TestActivity";
	private static final int DELAY_MILLIS = 30;  // UIˢ�¼��ʱ��
	private BigApp bigApp;
	private float value = MyLocation.ALTITUDE_INIT;
	private TextView text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		bigApp = (BigApp)getApplication();
		text = (TextView)findViewById(R.id.activity_test_text);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		handler.removeCallbacks(runnable);  // �رն�ʱˢ��
		handler.postDelayed(runnable, DELAY_MILLIS);  // ������ʱˢ��
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(runnable);  // �رն�ʱˢ��
	}
	
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			refreshUI();
			handler.postDelayed(this, DELAY_MILLIS);
		}
	};
	
	private void refreshUI(){
		if( value!=bigApp.localService.mLoc.altitude ){
			value = bigApp.localService.mLoc.altitude;
			text.setText( String.valueOf(value) + " - " + "\n" + text.getText().toString() );
		}
	}

}
