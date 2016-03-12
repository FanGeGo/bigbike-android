package cn.bigbike.cycling.widget;

import java.util.HashMap;
import java.util.List;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyModel;
import cn.bigbike.cycling.system.BigApp;
import cn.bigbike.cycling.ui.HistoryOnceActivity;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryOnceAdapter extends BaseAdapter {
	
	private List<HashMap<String, String>> mData;
	private HistoryOnceActivity mActivity;
	private LayoutInflater mInflater;
	private MyModel myModel;
	private BigApp bigApp;
	
	public HistoryOnceAdapter(HistoryOnceActivity activity, List<HashMap<String, String>> data, MyModel myModel, BigApp bigApp) {
		super();
		mActivity = activity;
        mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myModel = myModel;
        this.bigApp = bigApp;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v;
        if (convertView == null) {  
        	v = mInflater.inflate(R.layout.listview_item_history_once, parent, false);  
        } else {
        	v = convertView;
        }
        
        HashMap<String, String> map = mData.get(position);
        
        final String id = map.get("id");
        final String title = map.get("title");    	
    	String createTimeText = map.get("createTimeText");
    	String state = map.get("state");
    	String mileage = map.get("mileage");
    	String speedMax = map.get("speedMax");
    	String speedAvg = map.get("speedAvg");
    	String totalTime = map.get("totalTime");
    	String totalTimeUnit = map.get("totalTimeUnit");
    	String calorie = map.get("calorie");
    	String altitude = map.get("altitude");
    	
    	TextView titleView = (TextView)v.findViewById(R.id.listview_item_history_once_title);
    	TextView dateView = (TextView)v.findViewById(R.id.listview_item_history_once_date);
    	ImageButton deleteButton = (ImageButton)v.findViewById(R.id.listview_item_history_once_delete);
    	ImageButton openButton = (ImageButton)v.findViewById(R.id.listview_item_history_once_open);
    	TextView mileageView = (TextView)v.findViewById(R.id.listview_item_history_once_mileage);
    	TextView speedMaxView = (TextView)v.findViewById(R.id.listview_item_history_today_speedmax);
    	TextView speedAvgView = (TextView)v.findViewById(R.id.listview_item_history_once_speedavg);
    	TextView totalTimeView = (TextView)v.findViewById(R.id.listview_item_history_once_totaltime);
    	TextView totalTimeUnitView = (TextView)v.findViewById(R.id.listview_item_history_once_totaltimeunit);
    	TextView calorieView = (TextView)v.findViewById(R.id.listview_item_history_once_calorie);
    	TextView altitudeView = (TextView)v.findViewById(R.id.listview_item_history_once_altitude);
    	
    	titleView.setText(title);
    	dateView.setText(createTimeText);
    	if( state.equals("0") ){
    		openButton.setBackgroundResource(R.drawable.cycling_open);
    		openButton.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Cursor cursor = myModel.onceGetChecked(bigApp.user.uid);
    				if( cursor.getCount()==0 ){
    					myModel.onceSetChecked(bigApp.user.uid, id);
    					bigApp.localService.mCont.refreshOnce();
    					mActivity.finish();
    				}else{
    					Toast.makeText(mActivity.getApplicationContext(), "发现有打开的单程, 请关闭后再试", Toast.LENGTH_SHORT).show();
    				}
    				cursor.close();
    			}
    		});
    	}else{
    		openButton.setBackgroundResource(R.drawable.cycling_open_checked);
    	}
    	deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				dialogShow(position, id, title);
			}
		});
    	
    	mileageView.setText(mileage);
    	speedMaxView.setText(speedMax);
    	speedAvgView.setText(speedAvg);
    	totalTimeView.setText(totalTime);
    	totalTimeUnitView.setText(totalTimeUnit);
    	calorieView.setText(calorie);
    	altitudeView.setText(altitude);
		return v;		
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private void dialogShow(final int position, final String id, String title){
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
		dialogBuilder
	    .withTitle("删除单程")
	    .withTitleColor("#FFFFFF")
	    .withDividerColor("#11000000")
	    .withMessage("确定要删除 “"+title+"” 这次行程记录吗?")
	    .withMessageColor("#FFFFFFFF")
	    .withDialogColor("#FFE74C3C")
	    .withDuration(200)
	    .withEffect(Effectstype.SlideBottom)
	    .withButton1Text("确定")
	    .withButton2Text("取消")
	    .isCancelableOnTouchOutside(true)
	    .setButton1Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	myModel.onceDelete(bigApp.user.uid, id);
	        	bigApp.localService.mCont.refreshOnce();
	        	mData.remove(position);
				notifyDataSetChanged();  // 刷新
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
