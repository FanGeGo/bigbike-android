package cn.bigbike.cycling.system;

import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.myclass.MyController;
import cn.bigbike.cycling.myclass.MyFloat;
import cn.bigbike.cycling.myclass.MyLocation;
import cn.bigbike.cycling.myclass.MySync;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BigService extends Service {

	private static final String TAG = "BigService";
	
	private IBinder LocalBinder = new BigService.LocalBinder();
	public HeadsetPlugReceiver hpReceiver = new HeadsetPlugReceiver();
	
	private BigApp bigApp;
	
	public MyController mCont;  // ��������
	public MyLocation mLoc;  // ��λ��
	public MyFloat mFloat;  // ����������
	public MySync mSync;  // ͬ����
	
	// ����������̳�Binder
	public class LocalBinder extends Binder {
		// ���ر��ط���
		public BigService getService() {
			return BigService.this;
		}
	}
	
	// ��������������Ķ���
	public void destroyController(){
		mCont.stop();
		mCont.saveData();  // ��������
		mCont.release();
	}
	
	// ��������������Ķ���
	public void createController(){
		mCont = new MyController(this, bigApp.user);
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		
		bigApp = (BigApp)this.getApplication();
		registerReceiver(hpReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		
		createController();
		mLoc = new MyLocation(this, mCont);
		mFloat = new MyFloat(this, bigApp);
		mSync = new MySync(this, bigApp);
		
		if( mCont.runMode==MyConfig.MODE_GPS ){
			mLoc.start();
		}
		if( bigApp.user.lastRunTime>bigApp.user.lastSyncTime ){
			mSync.start();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return LocalBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(hpReceiver);
		destroyController();
		mLoc.stop();
		mFloat.removeFloatView();
		mSync.release();
		bigApp.GPSOpenTip = false;  // ��ʼ��GPS����ʾ
	}
	
    // ����������Ͱγ�
    public class HeadsetPlugReceiver extends BroadcastReceiver {
    	
    	private static final String TAG = "HeadsetPlugReceiver";
    	
    	public static final int PULLOUT = 0;
    	public static final int INSERT = 1;
    	
    	public int status = PULLOUT;
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
			if ( intent.hasExtra("state") ){
				if( intent.getIntExtra("state", 0)==PULLOUT ){
					if( status!=2 ){
						Log.i(TAG, "����û������");
						if( mCont.runMode==MyConfig.MODE_HARDWARE ){
							// ֹͣ����
							mCont.stop();
							mLoc.stop();
						}
						status = 2;
					}
				}else if( intent.getIntExtra("state", 0)==INSERT ){
					if( status!=1 ){
						Log.i(TAG, "�����Ѿ�����");
						if( mCont.runMode==MyConfig.MODE_HARDWARE ){
							// ��������
							mCont.start();
							mLoc.start();
						}
						status = 1;
					}
				}
			}
    	}

    }

}
