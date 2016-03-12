package cn.bigbike.cycling.ui;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.myclass.MyLocation;
import cn.bigbike.cycling.system.BigApp;
import cn.bigbike.cycling.system.BigService;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

public class MainActivity extends Activity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";
	
    private RadioGroup tabRadioGroup;
    private InteractFragment mInteract;
    private CyclingFragment mCycling;
    private MallFragment mMall;
    private int tabLastChecked = R.id.tabbar_radiobutton_cycling;  // Ĭ��Ϊcyclingѡ��
    
    public BigService localService;
    private BigApp bigApp;
    
    private MyConfig mConfig;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_main);
        
        // ���Activity�����պ����������µ�Fragment�ظ��������ص�������
        if( savedInstanceState!=null ){
        	mInteract = (InteractFragment) getFragmentManager().findFragmentByTag( String.valueOf(R.id.tabbar_radiobutton_interact) );
        	mCycling = (CyclingFragment) getFragmentManager().findFragmentByTag( String.valueOf(R.id.tabbar_radiobutton_cycling) );
        	mMall = (MallFragment) getFragmentManager().findFragmentByTag( String.valueOf(R.id.tabbar_radiobutton_mall) );
        }
        
        bigApp = (BigApp)getApplication();
        mConfig = new MyConfig(this);

        tabRadioGroup = (RadioGroup)findViewById(R.id.tabbar_radiogroup);        
        tabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            	FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();    
            	switch (checkedId) {
                case R.id.tabbar_radiobutton_interact:
					if( mInteract==null ){
						mInteract = new InteractFragment();
					}
					if (!mInteract.isAdded())
						transaction.hide( getLastCheckedFragment(tabLastChecked) ).add(R.id.activity_main_content, mInteract, String.valueOf(R.id.tabbar_radiobutton_interact)).commit();
					else
						transaction.hide( getLastCheckedFragment(tabLastChecked) ).show(mInteract).commit();
                    break;
                case R.id.tabbar_radiobutton_cycling:
					if( mCycling==null ){
				        mCycling = new CyclingFragment();
					}
					if (!mCycling.isAdded())
						transaction.hide( getLastCheckedFragment(tabLastChecked) ).add(R.id.activity_main_content, mCycling, String.valueOf(R.id.tabbar_radiobutton_cycling)).commit();
					else
						transaction.hide( getLastCheckedFragment(tabLastChecked) ).show(mCycling).commit();
                    break;
                case R.id.tabbar_radiobutton_mall:
					if( mMall==null ){
						mMall = new MallFragment();
					}
					if (!mMall.isAdded())
						transaction.hide( getLastCheckedFragment(tabLastChecked) ).add(R.id.activity_main_content, mMall, String.valueOf(R.id.tabbar_radiobutton_mall)).commit();
					else
						transaction.hide( getLastCheckedFragment(tabLastChecked) ).show(mMall).commit();
					break;
                }
            	tabLastChecked = checkedId;
            }
        });
        
        setDefaultFragment();
        binderService();  // setDefaultFragment()Ҫ����ǰ��
        
        if( mConfig.appIsFirstStart ){
			Intent intent = new Intent();
			intent.putExtra("appIsFirstStart", mConfig.appIsFirstStart);
			intent.setClass(getApplicationContext(), RunmodeConfigActivity.class);
			startActivity(intent);
        	mConfig.appIsFirstStart = false;
        	mConfig.save();
        }
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	// GPS����ʾ
    	mConfig.read();    	
    	if( mConfig.appRunMode==MyConfig.MODE_GPS & bigApp.GPSOpenTip==false & MyLocation.isOpenGPS(getApplicationContext())==false ){
    		dialogShow();
    		bigApp.GPSOpenTip = true;
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// ���ó���
        mConfig.read();
        if( mConfig.displayLongBright )
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // ��ֹ��Ļ���ߺ�����
        else
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConn);
	}
	
	// ����Ĭ��Fragment
	private void setDefaultFragment() {
		FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mCycling = new CyclingFragment();
        transaction.add(R.id.activity_main_content, mCycling, String.valueOf(R.id.tabbar_radiobutton_cycling)).commit();
    }
	
	// �������ѡ���Fragment
	private Fragment getLastCheckedFragment( int checkedId ){
		Fragment checked = null;
		switch (checkedId) {
        case R.id.tabbar_radiobutton_interact:
        	checked = mInteract;
        	break;
        case R.id.tabbar_radiobutton_cycling:
        	checked = mCycling;
        	break;
        case R.id.tabbar_radiobutton_mall:
        	checked = mMall;
        	break;
		}
		return checked;
	}
	
	private ServiceConnection serviceConn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			localService = ((BigService.LocalBinder)service).getService();
			bigApp.localService = localService; // ��Service�����ô�ŵ�ȫ����
	        mCycling.refreshUI();  // ˢ��UI
		}
		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			localService = null;
		}
    };
    
	// ��bindService������������
    private void binderService() {
         Intent intent = new Intent(this, BigService.class);
         bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
    }
    
	private void dialogShow(){
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
		dialogBuilder
	    .withTitle("GPSδ����")
	    .withTitleColor("#FFFFFF")
	    .withDividerColor("#11000000")
	    .withMessage("���������á�����GPSλ�÷��񣬿�������ܻ�ȡ��������")
	    .withMessageColor("#FFFFFFFF")
	    .withDialogColor("#FFE74C3C")
	    .withDuration(200)
	    .withEffect(Effectstype.SlideBottom)
	    .withButton1Text("����")
	    .withButton2Text("����")
	    .isCancelableOnTouchOutside(true)
	    .setButton1Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
	        	dialogBuilder.cancel();
	        }
	    })
	    .setButton2Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialogBuilder.cancel();
	        }
	    })
	    .show();
	}
    
}
