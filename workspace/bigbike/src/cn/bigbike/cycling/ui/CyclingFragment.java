package cn.bigbike.cycling.ui;

import java.text.DecimalFormat;

import com.lidroid.xutils.BitmapUtils;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.myclass.MyController;
import cn.bigbike.cycling.myclass.MyModel;
import cn.bigbike.cycling.myclass.MyLocation;
import cn.bigbike.cycling.myclass.MyUser;
import cn.bigbike.cycling.system.BigApp;
import cn.bigbike.cycling.system.BigService;
import cn.bigbike.cycling.util.Calorie;
import de.hdodenhof.circleimageview.CircleImageView;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CyclingFragment extends Fragment {
	
	private static final String TAG = "CyclingFragment";
	
	private MainActivity mainActivity;
	
	private static final int DELAY_MILLIS = 500;  // UIˢ�¼��ʱ��
	private int arrowCount = 1;	// ��ͷ��˸����	
	private String titleRadioGroupChecked = "today";  // Ĭ��Ϊtodayѡ��
	
	private TextView runState, speedNow, speedAvg, speedMax, mileage, mileageLabel, totalTime, totalTimeUnit, onceTitleText, altitude, calorie, altitudeNow;
	private LinearLayout todayOnceView, toolToday, toolOnce, toolTotal, onceTitleView;
	private ImageButton toolOnceSave, toolFloatTrigger, toolLocation, login;
	private View blockOne, blockTwo, blockThree;
	private CircleImageView userInfo;
	private RelativeLayout toolView;
	
	private BitmapUtils bitmapUtils;
	private MyModel myModel;
	private MyUser user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity)getActivity();
		myModel = new MyModel(mainActivity.getApplicationContext());
		BigApp bigApp = (BigApp)mainActivity.getApplication();
		user = bigApp.user;
		bitmapUtils = new BitmapUtils(mainActivity.getApplicationContext());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_cycling, container, false);
		
		login = (ImageButton)view.findViewById(R.id.titlebar_cycling_login);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), LoginActivity.class);
				startActivity(intent);
			}
		});
		
		userInfo = (CircleImageView)view.findViewById(R.id.titlebar_cycling_userinfo);
		userInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), UserinfoActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton config = (ImageButton)view.findViewById(R.id.titlebar_cycling_config);
		config.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), ConfigActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton toolTodayRank = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_today_rank);
		toolTodayRank.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( user.uid==0 ){
					Toast.makeText(mainActivity.getApplicationContext(), "��½����ܲ鿴", Toast.LENGTH_SHORT).show();
					// �򿪵�½����
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
		
		ImageButton toolTodayHistory = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_today_history);
		toolTodayHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), HistoryTodayActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton toolOnceAdd = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_once_add);
		toolOnceAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), AddOnceActivity.class);
				startActivity(intent);
			}
		});
		
		toolOnceSave = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_once_save);
		toolOnceSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mainActivity.localService.mCont.onceTitle!=null ){
					myModel.onceSetChecked(user.uid, null);
					mainActivity.localService.mCont.refreshOnce();
					refreshUI();  // ˢ��UI
				}
			}
		});
		
		ImageButton toolOnceHistory = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_once_history);
		toolOnceHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), HistoryOnceActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton toolTotalRank = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_total_rank);
		toolTotalRank.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( user.uid==0 ){
					Toast.makeText(mainActivity.getApplicationContext(), "��½����ܲ鿴", Toast.LENGTH_SHORT).show();
					// �򿪵�½����
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
		
		ImageButton toolFullscreen = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_fullscreen);
		toolFullscreen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mainActivity.getApplicationContext(), FullscreenCyclingActivity.class);
				startActivity(intent);
			}
		});
		
		toolFloatTrigger = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_float);
		toolFloatTrigger.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mainActivity.localService==null)  // ��һ��onStartʱService��δ����
					return;
				if( !mainActivity.localService.mFloat.isShowFloatView() ){
					mainActivity.localService.mFloat.createFloatView();
					Toast.makeText(mainActivity.getApplicationContext(), "���ڿ���ʹ�õ�ͼ���������, �����������ڿɷ��ش������", Toast.LENGTH_LONG).show();
					mainActivity.moveTaskToBack(true);
					toolFloatTrigger.setBackgroundResource(R.drawable.cycling_float_checked);
				}else{
					mainActivity.localService.mFloat.removeFloatView();
					toolFloatTrigger.setBackgroundResource(R.drawable.cycling_float);
				}
			}
		});
		
		toolLocation = (ImageButton)view.findViewById(R.id.fragment_cycling_tool_location);
		toolLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		RadioGroup titleRadioGroup = (RadioGroup)view.findViewById(R.id.titlebar_cycling_radiogroup); 
        titleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            	// �л�������
            	switch (checkedId) {
                case R.id.titlebar_cycling_radiogroup_today:
                	titleRadioGroupChecked = "today";
                	toolToday.setVisibility(View.VISIBLE);
                	toolOnce.setVisibility(View.INVISIBLE);
                	toolTotal.setVisibility(View.INVISIBLE);
                    break;                    
                case R.id.titlebar_cycling_radiogroup_once:
                	titleRadioGroupChecked = "once";
                	toolToday.setVisibility(View.INVISIBLE);
                	toolOnce.setVisibility(View.VISIBLE);
                	toolTotal.setVisibility(View.INVISIBLE);
                    break;                    
                case R.id.titlebar_cycling_radiogroup_total:
                	titleRadioGroupChecked = "total";
                	toolToday.setVisibility(View.INVISIBLE);
                	toolOnce.setVisibility(View.INVISIBLE);
                	toolTotal.setVisibility(View.VISIBLE);
					break;
                }
            	refreshUI();  // ˢ��UI
            }
        });
        runState = (TextView)view.findViewById(R.id.fragment_cycling_runstate);
        
		speedNow = (TextView)view.findViewById(R.id.fragment_cycling_speednow);		
		speedAvg = (TextView)view.findViewById(R.id.fragment_cycling_speedavg);
		speedMax = (TextView)view.findViewById(R.id.fragment_cycling_speedmax);		
		mileage = (TextView)view.findViewById(R.id.fragment_cycling_mileage);
		mileageLabel = (TextView)view.findViewById(R.id.fragment_cycling_mileage_label);
		totalTime = (TextView)view.findViewById(R.id.fragment_cycling_totaltime);
		totalTimeUnit = (TextView)view.findViewById(R.id.fragment_cycling_totaltime_unit);
		
		todayOnceView = (LinearLayout)view.findViewById(R.id.fragment_cycling_today_once);
		onceTitleView = (LinearLayout)view.findViewById(R.id.fragment_cycling_once_title);
		onceTitleText = (TextView)view.findViewById(R.id.fragment_cycling_once_title_text);
		
		calorie = (TextView)view.findViewById(R.id.fragment_cycling_calorie);
		altitude = (TextView)view.findViewById(R.id.fragment_cycling_altitude);
		altitudeNow  = (TextView)view.findViewById(R.id.fragment_cycling_altitude_now);
		
		blockOne = (View)view.findViewById(R.id.fragment_cycling_block_one);
		blockTwo = (View)view.findViewById(R.id.fragment_cycling_block_two);
		blockThree = (View)view.findViewById(R.id.fragment_cycling_block_three);
		
		toolView = (RelativeLayout)view.findViewById(R.id.fragment_cycling_tool);
		toolToday = (LinearLayout)view.findViewById(R.id.fragment_cycling_tool_today);
		toolOnce = (LinearLayout)view.findViewById(R.id.fragment_cycling_tool_once);
		toolTotal = (LinearLayout)view.findViewById(R.id.fragment_cycling_tool_total);
		
		Typeface typeFace = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/text.otf");
		speedNow.setTypeface(typeFace);
		speedAvg.setTypeface(typeFace);
		speedMax.setTypeface(typeFace);
		mileage.setTypeface(typeFace);
		totalTime.setTypeface(typeFace);
		altitude.setTypeface(typeFace);
		altitudeNow.setTypeface(typeFace);
		calorie.setTypeface(typeFace);
		return view;
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		handler.removeCallbacks(runnable);  // �رն�ʱˢ��
		handler.postDelayed(runnable, DELAY_MILLIS);  // ������ʱˢ��
		refreshUI();  // ˢ��UI
		// �ر���������
		if( mainActivity.localService!=null ){
			if( mainActivity.localService.mFloat.isShowFloatView() )
				toolFloatTrigger.callOnClick();
		}
		// ͷ������
		if( user.uid==0 ){
			login.setVisibility(View.VISIBLE);
			userInfo.setVisibility(View.GONE);
		}else{
			login.setVisibility(View.GONE);
			if( userInfo.getVisibility()!=View.VISIBLE ){
				bitmapUtils.display(userInfo, user.avatarUrl);
				userInfo.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		handler.removeCallbacks(runnable);  // �رն�ʱˢ��
		if( mainActivity.localService!=null ){
			mainActivity.localService.mCont.saveData();  // ��������
		}
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		myModel.release();
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
		if( mainActivity.localService==null )
			return;
		if( mainActivity.localService.mCont==null )
			return;
		
		mainActivity.localService.mCont.refreshTodayDate();  // ���ڱ仯ʱ��ˢ�½�������
		
		float mMileage = 0;
		long mTotalTime = 0;
		float mSpeedMax = 0;
		float mSpeedAvg = 0;
		float mAltitudeMax = -10000;
		float mAltitudeMin = 10000;
		
		// Tab�л�
		todayOnceView.setVisibility(View.VISIBLE);		
		if( titleRadioGroupChecked.equals("today") ){
			mMileage = mainActivity.localService.mCont.todayMileage;
			mTotalTime = mainActivity.localService.mCont.todayTotalTime;
			mSpeedMax = mainActivity.localService.mCont.todaySpeedMax;
			mSpeedAvg = mainActivity.localService.mCont.todaySpeedAvg;
			mAltitudeMax = mainActivity.localService.mCont.todayAltitudeMax;
			mAltitudeMin = mainActivity.localService.mCont.todayAltitudeMin;
			mileageLabel.setText("���");
			onceTitleView.setVisibility(View.GONE);
		}
		if( titleRadioGroupChecked.equals("once") ){
			mMileage = mainActivity.localService.mCont.onceMileage;
			mTotalTime = mainActivity.localService.mCont.onceTotalTime;
			mSpeedMax = mainActivity.localService.mCont.onceSpeedMax;
			mSpeedAvg = mainActivity.localService.mCont.onceSpeedAvg;
			mAltitudeMax = mainActivity.localService.mCont.onceAltitudeMax;
			mAltitudeMin = mainActivity.localService.mCont.onceAltitudeMin;
			mileageLabel.setText("���");
        	if(mainActivity.localService.mCont.onceTitle!=null){
        		onceTitleText.setText(mainActivity.localService.mCont.onceTitle);
        		onceTitleView.setVisibility(View.VISIBLE);
        		toolOnceSave.setVisibility(View.VISIBLE);
        	}else{
        		onceTitleView.setVisibility(View.GONE);
        		toolOnceSave.setVisibility(View.GONE);
        	}
		}
		if( titleRadioGroupChecked.equals("total") ){
			mMileage = mainActivity.localService.mCont.totalMileage;
			mTotalTime = mainActivity.localService.mCont.totalTotalTime;
			mSpeedMax = mainActivity.localService.mCont.totalSpeedMax;
			mSpeedAvg = mainActivity.localService.mCont.totalSpeedAvg;
			mileageLabel.setText("�����");
			todayOnceView.setVisibility(View.INVISIBLE);
		}
		
		toolView.setVisibility(View.VISIBLE);
		
		// ״̬
		int mRunState = mainActivity.localService.mCont.runState;
		switch( mRunState ){
			case MyController.NO_START:
				if( mainActivity.localService.mCont.runMode==MyConfig.MODE_GPS ){
					runState.setText("GPSδ����");
				}else{
					if( mainActivity.localService.hpReceiver.status==BigService.HeadsetPlugReceiver.PULLOUT ){
						runState.setText("δ�����豸");
					}else{
						runState.setText("�豸������");
					}
				}
				flicker(0);  // ������ҫ����
				break;
			case MyController.INIT_FAILED:
				runState.setText("��ʼ��ʧ��");
				break;
			case MyController.MIC_OCCUPIED:
				runState.setText("��˷类����APPռ�ã���360���������¼��Ȩ��");
				break;
			case MyController.GPS_CLOSE:
				runState.setText("GPS�رգ���������ÿ���");
				break;
			case MyController.GPS_NO_SIGNAL:
				runState.setText("GPSû���źţ����ƶ�");
				break;
			case MyController.IS_RUNNING:
				if( mainActivity.localService.mCont.wheelState==MyController.WHEEL_ROTATION ){
					runState.setText("������");
					toolView.setVisibility(View.INVISIBLE);
					if( arrowCount<=3 ){
						flicker(arrowCount);
						arrowCount++;
						if(arrowCount==4)
							arrowCount = 1;
					}
				}else{
					runState.setText("����ԭ�ش���");
					flicker(0);  // ������ҫ����
				}
				break;
		}
		
		// GPS״̬��ť
		boolean isOpenGPS = MyLocation.isOpenGPS(mainActivity.getApplicationContext());
		if( isOpenGPS ){
			toolLocation.setBackgroundResource(R.drawable.cycling_location_open);
		}else{
			toolLocation.setBackgroundResource(R.drawable.cycling_location);
		}
		
		// �������
		DecimalFormat dfInt = new DecimalFormat("0");
		DecimalFormat dfFloat = new DecimalFormat("0.0");
		
		speedNow.setText( dfFloat.format(mainActivity.localService.mCont.speedNow) );
		speedAvg.setText( dfFloat.format(mSpeedAvg) );
		speedMax.setText( mSpeedMax>=100?dfInt.format(mSpeedMax):dfFloat.format(mSpeedMax) );
		mileage.setText( dfFloat.format(mMileage) );
		// ����ʱ��
		int hour = (int)Math.floor( mTotalTime/(1000*60*60) ) ;
		int minute = (int)Math.floor( (mTotalTime % (1000*60*60))/(1000*60) );
		int second = (int)Math.floor( ((mTotalTime % (1000*60*60)) % (1000*60))/1000 );
		if( hour==0 ){
			totalTimeUnit.setText("M");
			totalTime.setText( (minute<10?"0":"") + String.valueOf(minute) + ":" + (second<10?"0":"") + String.valueOf(second) );
		}else{
			totalTimeUnit.setText("H");
			totalTime.setText( (hour<10?"0":"") + String.valueOf(hour) + ":" + (minute<10?"0":"") + String.valueOf(minute) );
		}
		// ��·��
		if( mTotalTime!=0 ){
			calorie.setText( Calorie.run(mMileage/mTotalTime*1000*3600, mTotalTime) );
		}else{
			calorie.setText("0");
		}
		// �߶�
		if( mAltitudeMax!=MyController.ALTITUDE_MAX_INIT & mAltitudeMin!=MyController.ALTITUDE_MIN_INIT ){
			altitude.setText( String.valueOf( (int)(mAltitudeMax-mAltitudeMin) ) );
		}else{
			altitude.setText("0");
		}
		// ʵʱ����
		altitudeNow.setText("");
		if( titleRadioGroupChecked.equals("today") || ( titleRadioGroupChecked.equals("once") & mainActivity.localService.mCont.onceTitle!=null ) ){
			if( isOpenGPS & mainActivity.localService.mCont.altitude!=MyLocation.ALTITUDE_INIT ){
				altitudeNow.setText( "���� " + String.valueOf( (int)mainActivity.localService.mCont.altitude) );
			}
		}
	}
	
	// �ü�ͷ��˸
	private void flicker( int num ){
		blockOne.setVisibility(View.INVISIBLE);
		blockTwo.setVisibility(View.INVISIBLE);
		blockThree.setVisibility(View.INVISIBLE);
		switch( num ){
			case 1:
				blockOne.setVisibility(View.VISIBLE);
				break;
			case 2:
				blockTwo.setVisibility(View.VISIBLE);
				break;
			case 3:
				blockThree.setVisibility(View.VISIBLE);
				break;
		}
	}

}
