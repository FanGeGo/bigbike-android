package cn.bigbike.cycling.system;

import java.util.HashMap;

import cn.bigbike.cycling.myclass.MyUser;

import android.app.Application;
import android.util.Log;

public class BigApp extends Application {

	private static final String TAG = "BigApp";
	
	private HashMap<String,Object> map = new HashMap<String,Object>();
	
	public BigService localService;
	public MyUser user;
	
	public boolean GPSOpenTip = false;  // GPS������ʾ
	
	// Activity֮�䴫������
	public void putObject( String key, Object value ) {
		map.put(key, value);
	}
	public Object getObject( String key ) {
		return map.get(key);
	}
	public void clearObject() {
		map.clear();
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		user = new MyUser(getApplicationContext());
	}

	@Override
	public void onTerminate() {
		Log.d(TAG, "onTerminate");
		super.onTerminate();
		user.save();  // �����û�����
		clearObject();  // �����������
	}
	
	@Override
	public void onLowMemory() {
		Log.d(TAG, "onLowMemory");
		super.onLowMemory();
	}

}
