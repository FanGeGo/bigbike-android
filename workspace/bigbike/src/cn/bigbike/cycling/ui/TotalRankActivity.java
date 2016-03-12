package cn.bigbike.cycling.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import cn.bigbike.cycling.R;

import cn.bigbike.cycling.myclass.MyUser;
import cn.bigbike.cycling.system.BigApp;

import cn.bigbike.cycling.widget.LinearLayoutForAdapter;
import cn.bigbike.cycling.widget.LinearLayoutForListView;
import cn.bigbike.cycling.widget.LoadingDialog;

import android.app.Activity;
import android.app.Dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TotalRankActivity extends Activity {

	private static final String TAG = "TotalRankActivity";

	private BigApp bigApp;
	private MyUser user;

	private Dialog loading;

	private LinearLayout body;
	private LinearLayoutForListView listView;
	private List<HashMap<String, String>> data;
	private LinearLayoutForAdapter adapter;
	
	private TextView rankNumber, rankMileage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank_total);

		bigApp = (BigApp) this.getApplication();
		user = bigApp.user;
		
		ImageButton back = (ImageButton)findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		loading = LoadingDialog.createDialog(this);

		body = (LinearLayout) findViewById(R.id.activity_body);
		body.setVisibility(View.INVISIBLE);
		
		listView = (LinearLayoutForListView) findViewById(R.id.activity_rank_listview);
		data = new ArrayList<HashMap<String,String>>();

		rankNumber = (TextView) findViewById(R.id.activity_rank_number);
		rankMileage = (TextView) findViewById(R.id.activity_rank_mileage);
		loadServerData();
	}

	private void setAdapter() {
		adapter = new LinearLayoutForAdapter(this, data,
				R.layout.listview_item_rank_total, new String[] { "number", "nickname", "avatarurl", "mileage" }, 
				new int[] {
						R.id.listview_item_rank_total_number,
						R.id.listview_item_rank_total_nickname,
						R.id.listview_item_rank_total_avatar,
						R.id.listview_item_rank_total_mileage 
						});
		listView.setAdapter(adapter);
	}

	private void loadServerData() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", String.valueOf(user.uid));
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST,
				"http://bigbike.sinaapp.com/android/ranktotal", params,
				new RequestCallBack<String>() {

					@Override
					public void onStart() {
						loading.show();
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						//Log.e(TAG, "response: " + responseInfo.result);
						loading.cancel();
						try {
							JSONObject response = new JSONObject(responseInfo.result);
							int code = response.getInt("code");
							if (code == 2000) {
								JSONObject dataObj = response.getJSONObject("data");
								String rank = dataObj.getString("rank");
								String mileage = dataObj.getString("mileage");
								JSONArray top = dataObj.getJSONArray("top");
								List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> map = null;
								for (int i = 0; i < top.length(); i++) {									
									JSONObject obj = top.getJSONObject(i);
									map = new HashMap<String, String>();
									map.put("number", String.valueOf(i+1));
									map.put("nickname", obj.getString("nickname"));
									map.put("avatarurl", obj.getString("avatarurl"));
									map.put("mileage", obj.getString("mileage")+" km");
									temp.add(map);
								}
								synchronized (data) {
									data.addAll(temp);
								}
								setAdapter();
								rankNumber.setText(rank);
								rankMileage.setText(mileage);
								body.setVisibility(View.VISIBLE);
							}
							if (code == 3000) {
								String msg = response.getString("msg");
								Log.e(TAG, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Log.e(TAG, error.getExceptionCode() + ":" + msg);
						loading.cancel();
					}
				});
	}

}
