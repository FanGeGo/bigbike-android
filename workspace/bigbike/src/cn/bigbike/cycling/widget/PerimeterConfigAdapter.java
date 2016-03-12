package cn.bigbike.cycling.widget;

import java.util.HashMap;
import java.util.List;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.ui.PerimeterConfigActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PerimeterConfigAdapter extends BaseAdapter {
	
	private List<HashMap<String, String>> mData;
	private LayoutInflater mInflater;
	
	public PerimeterConfigAdapter(PerimeterConfigActivity activity, List<HashMap<String, String>> data) {
		super();
        mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
        if (convertView == null) {  
        	v = mInflater.inflate(R.layout.listview_item_config_perimeter, parent, false);
        } else {
        	v = convertView;
        }
        
        HashMap<String, String> map = mData.get(position);
        
        String size = map.get("size");
    	String length = map.get("length");
    	String state = map.get("state");
    	
    	TextView sizeView = (TextView)v.findViewById(R.id.listview_item_config_perimeter_size);
    	TextView lengthView = (TextView)v.findViewById(R.id.listview_item_config_perimeter_length);
    	ImageView stateButton = (ImageView)v.findViewById(R.id.listview_item_config_perimeter_checked);
    	
    	sizeView.setText(size);
    	lengthView.setText(length+" ภๅรื");
    	if(state.equals("0")){
    		stateButton.setVisibility(View.GONE);
    	}else{
    		stateButton.setVisibility(View.VISIBLE);
    	}
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

}
