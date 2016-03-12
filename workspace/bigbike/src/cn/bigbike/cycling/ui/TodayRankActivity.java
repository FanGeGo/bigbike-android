package cn.bigbike.cycling.ui;

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
import cn.bigbike.cycling.util.DateTime;

import cn.bigbike.cycling.widget.LoadingDialog;

import android.app.Activity;
import android.app.Dialog;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import android.widget.ProgressBar;
import android.widget.TextView;

public class TodayRankActivity extends Activity {

	private static final String TAG = "TodayRankActivity";

	private BigApp bigApp;
	private MyUser user;

	private Dialog loading;

	private TextView mileage, speedAvg, mileagePercent, mileageDesc, mileageNote, speedAvgPercent, speedAvgDesc, speedAvgNote;
	private ProgressBar mileageProgressBar, speedAvgProgressBar;
	private int mp, sp, mr, sr;

	private static final int DELAY_MILLIS = 10; // 间隔时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank_today);

		bigApp = (BigApp) this.getApplication();
		user = bigApp.user;

		ImageButton back = (ImageButton) findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		loading = LoadingDialog.createDialog(this);

		mileage = (TextView) findViewById(R.id.activity_rank_mileage);
		speedAvg = (TextView) findViewById(R.id.activity_rank_speedavg);
		mileagePercent = (TextView) findViewById(R.id.activity_rank_mileage_percent);
		mileageDesc = (TextView) findViewById(R.id.activity_rank_mileage_desc);
		mileageNote = (TextView) findViewById(R.id.activity_rank_mileage_note);
		speedAvgPercent = (TextView) findViewById(R.id.activity_rank_speedavg_percent);
		speedAvgDesc = (TextView) findViewById(R.id.activity_rank_speedavg_desc);
		speedAvgNote = (TextView) findViewById(R.id.activity_rank_speedavg_note);
		mileageProgressBar = (ProgressBar) findViewById(R.id.activity_rank_mileage_progressbar);
		speedAvgProgressBar = (ProgressBar) findViewById(R.id.activity_rank_speedavg_progressbar);

		loadServerData();
	}

	private void loadServerData() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", String.valueOf(user.uid));
		params.addBodyParameter("date", DateTime.getTodayTimestamp());
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST,
				"http://bigbike.sinaapp.com/android/ranktoday", params,
				new RequestCallBack<String>() {

					@Override
					public void onStart() {
						loading.show();
					}

					@Override
					public void onLoading(long total, long current, boolean isUploading) {
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
								String m = dataObj.getString("mileage");
								String s = dataObj.getString("speedavg");
								mileage.setText(m);
								speedAvg.setText(s);
								mp = (int)(dataObj.getDouble("mileage_percent")*100);
								sp = (int)(dataObj.getDouble("speedavg_percent")*100);
								startTurnMileage();								
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

	private void startTurnMileage() {
		if (mp < 50) {
			mileagePercent.setTextColor(getResources().getColor(R.color.wathet));
			mileageDesc.setTextColor(getResources().getColor(R.color.wathet));
			mileageNote.setTextColor(getResources().getColor(R.color.wathet));
			mileageDesc.setText("骑的近");
			mileageNote.setText("同志仍须努力");
			mp = 100 - mp;
		} else {
			mileagePercent.setTextColor(getResources().getColor(R.color.red));
			mileageDesc.setTextColor(getResources().getColor(R.color.red));
			mileageNote.setTextColor(getResources().getColor(R.color.red));
			mileageDesc.setText("骑的远");
			if( mp < 70 )
				mileageNote.setText("人送外号骑行小钢炮");
			else if( mp < 90 )
				mileageNote.setText("加油, 下一站就是冠军");
			else
				mileageNote.setText("大腿引擎已入化境");
		}
		mileageNote.setVisibility(View.VISIBLE);
		mHandler.postDelayed(mRunnable, DELAY_MILLIS);
	}

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mileagePercent.setText(String.valueOf(mr) + "%");
			mileageProgressBar.setProgress(mr);
			if (mr < mp) {
				mHandler.postDelayed(this, DELAY_MILLIS);
				mr++;
			}else{
				startTurnSpeedAvg();
			}
		}
	};

	private void startTurnSpeedAvg() {
		if (sp < 50) {
			speedAvgPercent.setTextColor(getResources().getColor(R.color.wathet));
			speedAvgDesc.setTextColor(getResources().getColor(R.color.wathet));
			speedAvgNote.setTextColor(getResources().getColor(R.color.wathet));
			speedAvgDesc.setText("骑的慢");
			speedAvgNote.setText("骑的慢也是骑");
			sp = 100 - sp;
		} else {
			speedAvgPercent.setTextColor(getResources().getColor(R.color.red));
			speedAvgDesc.setTextColor(getResources().getColor(R.color.red));
			speedAvgNote.setTextColor(getResources().getColor(R.color.red));
			speedAvgDesc.setText("骑的快");
			if( sp < 70 )
				speedAvgNote.setText("一直在超车，偶尔被超越");
			else if( sp < 90 )
				speedAvgNote.setText("你的速度就像一阵风");
			else
				speedAvgNote.setText("别人只能看看你的背影");
		}
		speedAvgNote.setVisibility(View.VISIBLE);
		sHandler.postDelayed(sRunnable, DELAY_MILLIS);
	}

	private Handler sHandler = new Handler();
	private Runnable sRunnable = new Runnable() {
		@Override
		public void run() {
			speedAvgPercent.setText(String.valueOf(sr) + "%");
			speedAvgProgressBar.setProgress(sr);
			if (sr < sp) {
				sHandler.postDelayed(this, DELAY_MILLIS);
				sr++;
			}
		}
	};

}
