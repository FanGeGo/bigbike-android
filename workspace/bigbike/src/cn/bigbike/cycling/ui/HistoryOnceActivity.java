package cn.bigbike.cycling.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyController;
import cn.bigbike.cycling.myclass.MyModel;
import cn.bigbike.cycling.system.BigApp;
import cn.bigbike.cycling.util.Calorie;
import cn.bigbike.cycling.util.DateTime;
import cn.bigbike.cycling.widget.DynamicListView;
import cn.bigbike.cycling.widget.DynamicListView.DynamicListViewListener;
import cn.bigbike.cycling.widget.HistoryOnceAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class HistoryOnceActivity extends Activity implements DynamicListViewListener {
	
	private static final String TAG = "OnceHistoryActivity";
	private DynamicListView listView;
	private List<HashMap<String, String>> data;
	private HistoryOnceAdapter adapter;
	
	private MyModel myModel;
	private BigApp bigApp;
	private int row = 0;
	private static final int PAGE_ROWS = 15;
	
	// 用于刷新控件状态
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				adapter.notifyDataSetChanged();
				listView.doneMore();
			} else {
				super.handleMessage(msg);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_once_today_history);
		
		myModel = new MyModel(this);
		bigApp = (BigApp)this.getApplication();
		
		ImageButton back = (ImageButton)findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 finish();
			}
		});
		
		TextView title = (TextView)findViewById(R.id.titlebar_title);
		title.setText("单次行程记录");
		
		listView = (DynamicListView)findViewById(R.id.activity_history_today_and_once_listview);		
		data = new ArrayList<HashMap<String,String>>();
		loadData();
		adapter = new HistoryOnceAdapter(this, data, myModel, bigApp);		
		listView.setAdapter(adapter);		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
		listView.setDoMoreWhenBottom(true);	// 滚动到低端的时候是否自动加载更多
		listView.setOnMoreListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.clear();
		myModel.release();
		myModel = null;
	}

	@Override
	public boolean onRefreshOrMore(DynamicListView dynamicListView, boolean isRefresh) {
		if (!isRefresh) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 加载更多
					loadData();
					handler.sendEmptyMessage(1);
				}
			}).start();
		}
		return false;
	}
	
	private void loadData(){
		Log.i(TAG, "loadData");
		Cursor cursor = myModel.onceReadHistory( bigApp.user.uid, row, PAGE_ROWS );
		if( cursor.getCount()<PAGE_ROWS ){
			listView.setOnMoreListener(null);
		}
		List<HashMap<String, String>> temp = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = null;
		
		float speedMax, mileage, speedAvg, altitudeMax, altitudeMin;
		long totalTime, createTime;
		int hour, minute, second;
		String state, title, id, altitude, totalTimeText, totalTimeUnit;
		
		DecimalFormat df = new DecimalFormat("0.0");
		
		while( cursor.moveToNext() ){
			
			id = cursor.getString(cursor.getColumnIndex("id"));
			title = cursor.getString(cursor.getColumnIndex("title"));
			createTime = cursor.getLong(cursor.getColumnIndex("createtime"));
			state = cursor.getString(cursor.getColumnIndex("state"));
			speedMax = cursor.getFloat(cursor.getColumnIndex("speedmax"));
			mileage = cursor.getFloat(cursor.getColumnIndex("mileage"));			
			// 骑行时间
            totalTime = cursor.getLong(cursor.getColumnIndex("totaltime"));
    		hour = (int)Math.floor( totalTime/(1000*60*60) ) ;
    		minute = (int)Math.floor( (totalTime % (1000*60*60))/(1000*60) );
    		second = (int)Math.floor( ((totalTime % (1000*60*60)) % (1000*60))/1000 );    		
    		if( hour==0 ){
    			totalTimeText = (minute<10?"0":"") + String.valueOf(minute) + ":" + (second<10?"0":"") + String.valueOf(second);
    			totalTimeUnit = "时间 m";
    		}else{
    			totalTimeText = (hour<10?"0":"") + String.valueOf(hour) + ":" + (minute<10?"0":"") + String.valueOf(minute);
    			totalTimeUnit = "时间 h";
    		}
    		// 均速
    		if( totalTime!=0 ){
    			speedAvg = mileage/totalTime*1000*3600;
    		}else{
    			speedAvg = 0.0F;
    		}
    		// 高度
    		altitudeMax = cursor.getFloat(cursor.getColumnIndex("altitudemax"));
    		altitudeMin = cursor.getFloat(cursor.getColumnIndex("altitudemin"));    		
    		if( altitudeMax!=MyController.ALTITUDE_MAX_INIT & altitudeMin!=MyController.ALTITUDE_MIN_INIT ){
    			altitude = String.valueOf( (int)(altitudeMax-altitudeMin) );
    		}else{
    			altitude = "0";
    		}
    		
			map = new HashMap<String, String>();
			map.put("id", id);
			map.put("title", title);
			map.put("createTime", String.valueOf(createTime));
			map.put("createTimeText", DateTime.getDateFormat(createTime, DateTime.FORMAT_SHORT, DateTime.FORMAT_SHORT));
			map.put("state", state);
			map.put("mileage", df.format(mileage));
			map.put("speedMax", df.format(speedMax));
			map.put("speedAvg", df.format(speedAvg));
			map.put("totalTime", totalTimeText);
            map.put("totalTimeUnit", totalTimeUnit);
            map.put("calorie", Calorie.run(speedAvg, totalTime));
			map.put("altitude", altitude);
            temp.add(map);
		}
		synchronized (data) {
			data.addAll(temp);
		}
		cursor.close();
		row += PAGE_ROWS;
	}
	
}
