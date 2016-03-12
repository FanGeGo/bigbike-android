package cn.bigbike.cycling.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.system.BigApp;
import cn.bigbike.cycling.widget.DynamicListView;
import cn.bigbike.cycling.widget.DynamicListView.DynamicListViewListener;
import cn.bigbike.cycling.widget.PerimeterConfigAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class PerimeterConfigActivity extends Activity implements DynamicListViewListener {
	
	private static final String TAG = "PerimeterConfigActivity";
	private DynamicListView listView;
	private List<HashMap<String, String>> data;
	private PerimeterConfigAdapter adapter;
	
	private MyConfig myConfig;
	private BigApp bigApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_perimeter);
		
		bigApp = (BigApp)getApplication();
		myConfig = new MyConfig(getApplicationContext());

		listView = (DynamicListView)findViewById(R.id.activity_config_perimeter_listview);
		data = new ArrayList<HashMap<String,String>>();		
		loadData();		
		adapter = new PerimeterConfigAdapter(PerimeterConfigActivity.this, data);
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> map = data.get(position-1);
				String size = map.get("size");
		    	String length = map.get("length");
		    	myConfig.wheelSize = size;
		    	myConfig.wheelPerimeter = Float.valueOf(length);
		    	myConfig.save();
		    	bigApp.localService.mCont.perimeter = myConfig.wheelPerimeter;
		    	finish();
			}
		});
		
		Toast.makeText(getApplicationContext(), "根据自行车轮胎侧面的型号标示，选择对应的周长", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.clear();
	}
	
	@Override
	public boolean onRefreshOrMore(DynamicListView dynamicListView, boolean isRefresh) {
		return false;
	}
	
	private void loadData(){
		Log.i(TAG, "loadData");
		String[][] list = new String[][]{
										{"20 × 1.75", "150"},
										{"24 × 1", "175"},
										{"24 × 3/4 Tubular", "178"},
										{"24 × 1-1/8 Tubular", "179"},
										{"24 × 1-1/4", "191"},
										{"24 × 1.75", "189"},
										{"24 × 2.00", "192"},
										{"24 × 2.125", "196"},
										{"26 × 1 (559mm)", "191"},
										{"26 × 1 (650c)", "195"},
										{"26 × 1.25", "195"},
										{"26 × 1-1/8 Tubular", "197"},
										{"26 × 1-3/8", "207"},
										{"26 × 1-1/2", "210"},
										{"26 × 1.40", "200"},
										{"26 × 1.50", "199"},
										{"26 × 1.75", "202"},
										{"26 × 1.95", "205"},
										{"26 × 2.00", "206"},
										{"26 × 2.1", "207"},
										{"26 × 2.125", "207"},
										{"26 × 2.35", "208"},
										{"27 × 1", "215"},
										{"27 × 1-1/8", "216"},
										{"27 × 1-1/4", "216"},
										{"27 × 1-3/8", "217"},
										{"650 × 35A", "209"},
										{"650 × 38A", "212"},
										{"650 × 38B", "211"},
										{"700 × 18C", "207"},
										{"700 × 19C", "209"},
										{"700 × 20C", "209"},
										{"700 × 23C", "210"},
										{"700 × 25C", "211"},
										{"700 × 28C", "214"},
										{"700 × 30C", "217"},
										{"700 × 32C", "216"},
										{"700C Tubular", "213"},
										{"700 × 35C", "217"},
										{"700 × 38C", "218"},
										{"700 × 44C", "222"}
										};
		List<HashMap<String, String>> temp = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = null;
		for(String v[] : list){
			map = new HashMap<String, String>();
			map.put("size", v[0]);
			map.put("length", v[1]);
			map.put("state", v[0].equals(myConfig.wheelSize)?"1":"0");
            temp.add(map);
		}
		synchronized (data) {
			data.addAll(temp);
		}
	}
	
}
